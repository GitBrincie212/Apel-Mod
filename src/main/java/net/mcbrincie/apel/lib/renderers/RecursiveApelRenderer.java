package net.mcbrincie.apel.lib.renderers;

import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.Stack;

/** The recursive renderer is used for defining complex fractal-alike shapes pretty simply. An example
 * of this can be stacking cubes together to form a cuboid particle object, the renderer has a stack.
 * Which it unwraps and executes the draw method of each particle object until it has fully unwrapped
 * when the stack is empty, it is refilled back with the same content and draws the particle point
 */
@SuppressWarnings("unused")
public class RecursiveApelRenderer implements ApelRenderer {
    protected final ServerWorld world;
    protected ParticleObject[] particleObjects;
    protected Stack<ParticleObject> stack;

    public RecursiveApelRenderer(ServerWorld world, ParticleObject... particleObjects) {
        this.particleObjects = particleObjects;
        this.stack = new Stack<>();
        this.world = world;
        Collections.addAll(this.stack, particleObjects);
    }

    @Override
    public void drawParticle(ParticleEffect particleEffect, int step, Vector3f drawPos) {
        if (this.stack.empty()) {
            Collections.addAll(this.stack, this.particleObjects);
            this.world.spawnParticles(
                    particleEffect, drawPos.x, drawPos.y, drawPos.z,
                    0, 0.0f, 0.0f, 0.0f, 0
            );
            return;
        }
        this.stack.pop().draw(this, step, drawPos);
    }

    @Override
    public ServerWorld getWorld() {
        return this.world;
    }
}
