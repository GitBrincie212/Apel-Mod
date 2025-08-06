package net.mcbrincie.apel.mixin;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.util.ServerWorldAccess;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements ServerWorldAccess {
	@Unique
	private long lastTime;

	@Unique
	private float deltaTickTime = 0;

	@Override
	public float APEL$getDeltaTickTime() {
		return this.deltaTickTime;
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void tickInject(CallbackInfo info) {
		this.deltaTickTime = (System.currentTimeMillis() - this.lastTime) / 1000f;
		Apel.SCHEDULER.runTick();
	}

	@Inject(at = @At("TAIL"), method = "tick")
	private void tickInjectEnd(CallbackInfo info) {
		this.lastTime = System.currentTimeMillis();
	}
}