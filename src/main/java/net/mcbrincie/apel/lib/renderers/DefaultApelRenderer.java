package net.mcbrincie.apel.lib.renderers;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

/** This is the default renderer used for apel; it draws a particle effect at xyz coordinates,
 * for most animations it is best to use this compared to the other apel renderers (since the
 * others are used for specific niche cases)
 */
public class DefaultApelRenderer extends BaseApelRenderer implements ApelServerRenderer {
    protected final ServerWorld world;

    public DefaultApelRenderer(ServerWorld world) {
        this.world = world;
    }

    @Override
    public void drawParticle(ParticleEffect particleEffect, int step, Vector3f drawPos) {
        world.spawnParticles(particleEffect, drawPos.x, drawPos.y, drawPos.z, 0, 0.0f, 0.0f, 0.0f, 1);
    }

    @Override
    public ServerWorld getServerWorld() {
        return world;
    }
}
