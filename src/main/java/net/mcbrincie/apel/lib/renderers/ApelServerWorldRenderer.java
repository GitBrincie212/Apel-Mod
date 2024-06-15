package net.mcbrincie.apel.lib.renderers;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

public class ApelServerWorldRenderer implements ApelRenderer {
    private final ServerWorld world;

    public ApelServerWorldRenderer(ServerWorld world) {
        this.world = world;
    }

    @Override
    public void drawParticle(ParticleEffect particleEffect, Vector3f drawPos) {
        world.spawnParticles(particleEffect, drawPos.x, drawPos.y, drawPos.z, 0, 0.0f, 0.0f, 0.0f, 1);
    }

    @Override
    public ServerWorld getWorld() {
        return world;
    }
}
