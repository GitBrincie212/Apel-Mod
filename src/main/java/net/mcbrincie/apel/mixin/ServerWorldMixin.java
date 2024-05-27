package net.mcbrincie.apel.mixin;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.util.DelayedTask;
import net.mcbrincie.apel.lib.util.scheduler.ScheduledSequence;
import net.mcbrincie.apel.lib.util.scheduler.ScheduledStep;
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
		List<DelayedTask<Runnable>> tasksToRemove = new ArrayList<>();
		for (DelayedTask<Runnable> task : Apel.delayedTasks) {
			task.delay--;
			if (task.delay == 0) {
				task.func.run();
				tasksToRemove.add(task);
			}
		}
		Apel.delayedTasks.removeAll(tasksToRemove);
		List<ScheduledSequence> tasksToRemove2 = new ArrayList<>();
		for (ScheduledSequence section : Apel.apelScheduler) {
			ScheduledStep firstElement = section.first();
			if (section.isEmpty() || firstElement == null) continue;
			firstElement.delay--;
			if (firstElement.delay == 0) {
				for (Runnable func : firstElement.func) {
					func.run();
				}
				section.deallocateStep();
				if (section.isEmpty()) {
					tasksToRemove2.add(section);
				}
			}
		}
		for (ScheduledSequence sequence : tasksToRemove2) {
			Apel.apelScheduler.deallocateSequence(sequence);
		}
	}
}