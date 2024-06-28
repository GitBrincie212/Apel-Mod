package net.mcbrincie.apel.client;

import net.mcbrincie.apel.lib.renderers.ApelRenderer;
import net.mcbrincie.apel.lib.renderers.BaseApelRenderer;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.joml.Vector3f;

final class ParticleManagerRenderer extends BaseApelRenderer implements ApelRenderer {

    private ParticleManager particleManager;

    ParticleManagerRenderer() {}

    // Set the ParticleManager via this setter because it isn't available when ApelClient initializes
    /* package-private */ void setParticleManager(ParticleManager particleManager) {
        this.particleManager = particleManager;
    }

    @Override
    public void drawParticle(ParticleEffect particleEffect, int step, Vector3f drawPos) {
        particleManager.addParticle(particleEffect, drawPos.x, drawPos.y, drawPos.z, 0.0f, 0.0f, 0.0f);
    }
}
