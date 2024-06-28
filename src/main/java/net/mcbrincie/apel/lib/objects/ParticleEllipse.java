package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Optional;

/** The particle object class that represents a 2D ellipse.
 * It has a radius which dictates how large or small the ellipse is depending on the
 * radius value supplied and a stretch value for how stretched is the ellipse. A value of
 * stretch that equals the radius means it is a circle
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleEllipse extends ParticleObject {
    protected float radius;
    protected float stretch;
    private DrawInterceptor<ParticleEllipse, AfterDrawData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleEllipse, BeforeDrawData> beforeDraw = DrawInterceptor.identity();

    /** This data is used before calculations (it contains the iterated rotation) */
    public enum BeforeDrawData {
        ITERATED_ROTATION
    }

    /** This data is used after calculations (it contains the drawing position) */
    public enum AfterDrawData {
        DRAW_POSITION
    }

    /** Constructor for the particle ellipse which is a 3D shape. It accepts as parameters
     * the particle effect to use, the radius of the ellipse, the stretch of the ellipse,
     * the rotation to apply & the number of particles. There is also a simplified version
     * for no rotation.
     *
     * @param particleEffect The particle to use
     * @param amount The number of particles for the object
     * @param radius The radius of the ellipse (how big it is)
     * @param stretch The stretch of the ellipse (how stretched it is)
     * @param rotation The rotation to apply
     *
     * @see ParticleEllipse#ParticleEllipse(ParticleEffect, float, float, int)
     */
    public ParticleEllipse(
            @NotNull ParticleEffect particleEffect, float radius,
            float stretch, Vector3f rotation, int amount
    ) {
        super(particleEffect, rotation);
        this.setRadius(radius);
        this.setAmount(amount);
        this.setStretch(stretch);
    }

    /** Constructor for the particle ellipse which is a 3D shape. It accepts as parameters
     * the particle effect to use, the radius of the ellipse, the stretch of the ellipse
     * & the number of particles. There is also a version that allows for rotation.
     *
     * @param particleEffect The particle to use
     * @param amount The number of particles for the object
     * @param stretch The stretch of the ellipse (how stretched it is)
     * @param radius The radius of the ellipse (how big it is)
     *
     * @see ParticleEllipse#ParticleEllipse(ParticleEffect, float, float, Vector3f, int)
     */
    public ParticleEllipse(
            @NotNull ParticleEffect particleEffect,
            float radius, float stretch, int amount
    ) {
        this(particleEffect, radius, stretch, new Vector3f(0,0,0), amount);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param ellipse The particle ellipse object to copy from
     */
    public ParticleEllipse(ParticleEllipse ellipse) {
        super(ellipse);
        this.radius = ellipse.radius;
        this.amount = ellipse.amount;
        this.afterDraw = ellipse.afterDraw;
        this.beforeDraw = ellipse.beforeDraw;
        this.stretch = ellipse.stretch;
    }

    /** Gets the radius of the ParticleEllipse and returns it.
     *
     * @return the radius of the ParticleEllipse
     */
    public float getRadius() {
        return radius;
    }

    /** Set the radius of this ParticleEllipse and returns the previous radius that was used.
     *
     * @param radius the new radius
     * @return the previously used radius
     */
    public float setRadius(float radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("stretch cannot be negative");
        }
        float prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    /** Gets the stretch of the ParticleEllipse and returns it
     *
     * @return the stretch of the ParticleEllipse
     */
    public float getStretch() {return stretch;}

    /** Sets the stretch of the ParticleEllipse and returns the previous stretch that was used
     *
     * @param stretch The new stretch
     * @return The previous used stretch
     */
    public float setStretch(float stretch) {
        if (stretch < 0) {
            throw new IllegalArgumentException("stretch cannot be negative");
        }
        float prevHeight = this.stretch;
        this.stretch = stretch;
        return prevHeight;
    }

    @Override
    public void draw(ApelServerRenderer renderer, int step, Vector3f drawPos) {
        this.doBeforeDraw(renderer.getServerWorld(), step, drawPos);
        Vector3f objectDrawPos = new Vector3f(drawPos).add(this.offset);
        renderer.drawEllipse(
                this.particleEffect, step, objectDrawPos, this.radius, this.stretch, this.rotation, this.amount);
        this.doAfterDraw(renderer.getServerWorld(), step, drawPos);
        this.endDraw(renderer, step, drawPos);
    }

    /** Set the interceptor to run after drawing the ellipse. The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position where the ellipse is rendered.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public void setAfterDraw(DrawInterceptor<ParticleEllipse, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step, Vector3f drawPos) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, drawPos, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /** Set the interceptor to run prior to drawing the ellipse. The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position where the ellipse is rendered.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing each particle
     */
    public void setBeforeDraw(DrawInterceptor<ParticleEllipse, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void doBeforeDraw(ServerWorld world, int step, Vector3f pos) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, pos, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }
}
