package net.mcbrincie.apel.mixin;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.util.DelayedTask;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

	@Inject(at = @At("HEAD"), method = "tick")
	private void tickInject(CallbackInfo info) {
		if (!Apel.delayedTasks.isEmpty()) {
			this.runDelayScheduleTick();
		}
		Apel.apelScheduler.runTick();
	}

	private void runDelayScheduleTick() {
		List<DelayedTask<Runnable>> tasksToRemove = new ArrayList<>();
		for (DelayedTask<Runnable> task : Apel.delayedTasks) {
			task.delay--;
			if (task.delay == 0) {
				task.func.run();
				tasksToRemove.add(task);
			}
		}
		Apel.delayedTasks.removeAll(tasksToRemove);
	}
}