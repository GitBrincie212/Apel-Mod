package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Optional;

/** The particle object class that represents a sphere(3D shape) and not a 2D circle.
 * It has a radius which dictates how large or small the sphere is depending on the
 * radius value supplied. And it uses the Fibonacci point distribution algorithm
 * to place the particles around the sphere (which makes it convincing)
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleSphere extends ParticleObject {
    public static final double SQRT_5_PLUS_1 = 3.23606;
    protected float radius;

    private DrawInterceptor<ParticleSphere, AfterDrawData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleSphere, BeforeDrawData> beforeDraw = DrawInterceptor.identity();

    /** This data is used before calculations (it contains the number of particles) */
    public enum BeforeDrawData {}

    /** This data is used after calculations (it contains the drawing position & the number of particles) */
    public enum AfterDrawData {}

    /**
     * Constructor for the particle sphere which is a 3D shape. It accepts as parameters
     * the particle effect to use, the radius of the sphere, the number of particles.
     * And the rotation to apply. There is also a simplified version for no rotation.
     *
     * @param particleEffect The particle to use
     * @param radius   The radius of the sphere
     * @param amount   The number of particles for the object
     * @param rotation The rotation to apply
     * @see ParticleSphere#ParticleSphere(ParticleEffect, float, int)
     */
    public ParticleSphere(@NotNull ParticleEffect particleEffect, float radius, int amount, Vector3f rotation) {
        super(particleEffect, rotation);
        this.setRadius(radius);
        this.setAmount(amount);
    }

    /** Constructor for the particle cuboid which is a 3D shape. It accepts as parameters
     * the particle effect to use, the radius of the sphere & the number of particles.
     * It is a simplified version for the case when no rotation is meant to be applied.
     * For rotation offset, you can use another constructor
     *
     * @param particleEffect The particle to use
     * @param amount The number of particles for the object
     * @param radius The radius of the sphere
     *
     * @see ParticleSphere#ParticleSphere(ParticleEffect, float, int, Vector3f)
    */
    public ParticleSphere(@NotNull ParticleEffect particleEffect, float radius, int amount) {
        this(particleEffect, radius, amount, new Vector3f(0));
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param sphere The particle sphere object to copy from
    */
    public ParticleSphere(ParticleSphere sphere) {
        super(sphere);
        this.radius = sphere.radius;
        this.amount = sphere.amount;
        this.afterDraw = sphere.afterDraw;
        this.beforeDraw = sphere.beforeDraw;
    }

    /** Sets the radius of the sphere
     *
     * @param radius The radius of the sphere
     * @return The previous radius used
    */
    public float setRadius(float radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius cannot be below or equal to 0");
        }
        float prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    /** Gets the radius of the sphere
     *
     * @return the radius of the sphere
     */
    public float getRadius() {
        return this.radius;
    }

    @Override
    public void draw(ApelServerRenderer renderer, int step, Vector3f drawPos) {
        this.doBeforeDraw(renderer.getServerWorld(), step, drawPos);
        Vector3f objectDrawPos = new Vector3f(drawPos).add(this.offset);
        renderer.drawEllipsoid(
                this.particleEffect, step, objectDrawPos, this.radius, this.radius, this.radius, this.rotation,
                this.amount
        );
        this.doAfterDraw(renderer.getServerWorld(), step, drawPos);
        this.endDraw(renderer, step, drawPos);
    }

    /** Set the interceptor to run after drawing the sphere.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the position
     * where the sphere is rendered.  It will also have the surface point and
     * number of the individual particle that was just drawn.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public void setAfterDraw(DrawInterceptor<ParticleSphere, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step, Vector3f drawPos) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, drawPos, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /** Set the interceptor to run prior to drawing the sphere.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the position
     * where the sphere is rendered.  It will also have the number of the individual particle effect
     * about to be drawn.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing the sphere
     */
    public void setBeforeDraw(DrawInterceptor<ParticleSphere, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void doBeforeDraw(ServerWorld world, int step, Vector3f drawPos) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, drawPos, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }
}
