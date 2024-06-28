package net.mcbrincie.apel.mixin;

import net.mcbrincie.apel.Apel;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

	@Inject(at = @At("HEAD"), method = "tick")
	private void tickInject(CallbackInfo info) {
		Apel.SCHEDULER.runTick();
	}
}