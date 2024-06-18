package net.mcbrincie.apel.lib.renderers;

import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

/**
 * ApelRenderer allows Apel animations to be rendered to multiple "canvases."
 * <p>
 * ApelRenderer offers a factory method to render to the {@link ServerWorld} powering the current instance of Minecraft.
 * This can be used as follows:
 * <pre>
 * void animate(ServerWorld world, PlayerEntity user) {
 *     // Setup a line
 *     Vector3f start = new Vector3f(-3, 1, 1);
 *     Vector3f end = new Vector3f(3, 1, 1);
 *     ParticleLine line1 = new ParticleLine(ParticleTypes.COMPOSTER, start, end, 5);
 *
 *     // Setup an Animator
 *     Vector3f origin = user.getPos().toVector3f().add(0, 1, 4);
 *     PointAnimator animator = new PointAnimator(1, line1, origin, 100);
 *
 *     // Perform the animation
 *     animator.beginAnimation(ApelRenderer.create(world));
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public interface ApelRenderer {
    static ApelRenderer create(ServerWorld world) {
        return new DefaultApelRenderer(world);
    }

    /**
     * Instructs the renderer to draw the given particle effect at the given position.
     *
     * @param particleEffect The ParticleEffect to draw
     * @param drawPos The position at which to draw
     */
    void drawParticle(ParticleEffect particleEffect, int step, Vector3f drawPos);

    /**
     * Instructs the renderer to draw a line of the given particle effect from {@code start} to {@code end} using
     * {@code count} particles.  Particles
     * will be spaced evenly along the defined line.
     *
     * @param particleEffect The ParticleEffect to use
     * @param start The 3D point at which to start
     * @param end The 3D point at which to end
     * @param count The number of particles to draw along the line
     */
    default void drawLine(ParticleEffect particleEffect, int step, Vector3f start, Vector3f end, int count) {
        int amountSubOne = (count - 1);
        // Do not use 'sub', it modifies in-place
        float stepX = (end.x - start.x) / amountSubOne;
        float stepY = (end.y - start.y) / amountSubOne;
        float stepZ = (end.z - start.z) / amountSubOne;
        Vector3f curr = new Vector3f(start);
        for (int i = 0; i < count; i++) {
            drawParticle(particleEffect, step, curr);
            curr.add(stepX, stepY, stepZ);
        }
    }

    /**
     * Instructs the renderer to draw a sphere of the given particle effect at {@code drawPos} with the given
     * {@code radius}, {@code rotation}, and {@code amount} of particles.
     * <p>
     * The default implementation is inefficient due to repeated trigonometry calculations, so it is strongly
     * recommended that implementations needing the list of specific particles override this to provide some amount of
     * caching.
     * </p>
     * Reference: <a href="https://stackoverflow.com/a/44164075">"golden spiral" algorithm</a>
     *
     * @param particleEffect The ParticleEffect to use
     * @param step The current animation step
     * @param drawPos The position of the center of the sphere
     * @param radius The radius of the sphere
     * @param rotation The rotation of the sphere; this is not terribly useful, but it can be used for
     *         interesting effect, if the number of particles in the sphere changes over time.
     * @param amount The number of particles in the sphere
     */
    default void drawSphere(
            ParticleEffect particleEffect, int step, Vector3f drawPos, float radius, Vector3f rotation, int amount
    ) {
        final double sqrt5Plus1 = 3.23606;
        Quaternionfc quaternion = new Quaternionf().rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);
        for (int i = 0; i < amount; i++) {
            // Offset into the real-number distribution
            float k = i + .5f;
            // Project point on unit sphere
            double phi = Math.acos(1f - ((2f * k) / amount));
            double theta = Math.PI * k * sqrt5Plus1;
            double sinPhi = Math.sin(phi);
            float x = (float) (Math.cos(theta) * sinPhi);
            float y = (float) (Math.sin(theta) * sinPhi);
            float z = (float) Math.cos(phi);
            // Scale, rotate, translate
            Vector3f pos = new Vector3f(x, y, z).mul(radius).rotate(quaternion).add(drawPos);
            drawParticle(particleEffect, step, pos);
        }

    }

    default Vector3f drawEllipsePoint(ParticleEffect particleEffect, float r, float h, float angle, Vector3f rotation, Vector3f center, int step) {
        float x = (float) (r * Math.cos(angle));
        float y = (float) (h * Math.sin(angle));
        Vector3f finalPosVec = new Vector3f(x, y, 0)
                .rotateZ(rotation.z)
                .rotateY(rotation.y)
                .rotateX(rotation.x)
                .add(center);
        this.drawParticle(particleEffect, step, finalPosVec);
        return finalPosVec;
    }

    default void beforeFrame(ParticleObject particleObject, int step, Vector3f frameOrigin) {
    }

    default void afterFrame(ParticleObject particleObject, int step, Vector3f frameOrigin) {
    }

    ServerWorld getWorld();
}
