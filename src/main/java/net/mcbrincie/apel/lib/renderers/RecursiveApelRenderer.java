package net.mcbrincie.apel.lib.renderers;

import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.NotImplementedException;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.Stack;

/** The recursive renderer is used for defining complex fractal-alike shapes pretty simply. An example
 * of this can be stacking cubes together to form a cuboid particle object, the renderer has a stack.
 * Which it unwraps and executes the draw method of each particle object until it has fully unwrapped
 * when the stack is empty, it is refilled back with the same content and draws the particle point
 */
@SuppressWarnings("unused")
public class RecursiveApelRenderer implements ApelServerRenderer {
    protected final ServerWorld world;

    public RecursiveApelRenderer(ServerWorld world, ParticleObject... particleObjects) {
        this.world = world;
    }

    @Override
    public void drawParticle(ParticleEffect particleEffect, int step, Vector3f drawPos) {
        // Work In Progress(WIP)
        throw new NotImplementedException();
    }

    @Override
    public ServerWorld getServerWorld() {
        return this.world;
    }
}
