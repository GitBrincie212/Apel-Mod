package net.mcbrincie.apel.client;

import net.mcbrincie.apel.lib.renderers.ApelRenderer;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

public class ParticleManagerRenderer implements ApelRenderer {
    private ParticleManager particleManager;

    @Override
    public void drawParticle(ParticleEffect particleEffect, int step, Vector3f drawPos) {
        particleManager.addParticle(particleEffect, drawPos.x, drawPos.y, drawPos.z, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public ServerWorld getWorld() {
        throw new UnsupportedOperationException("Client rendering cannot access the ServerWorld");
    }

    /* package-private */ void setParticleManager(ParticleManager particleManager) {
        this.particleManager = particleManager;
    }
}
