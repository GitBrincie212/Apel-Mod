package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.Optional;

/** The particle object class that represents a sphere.
 * It has a radius which dictates how large or small the sphere is.  It projects the <em>golden spiral</em>
 * on to the sphere to distribute particles evenly across the surface.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleSphere extends ParticleObject {
    public static final double SQRT_5_PLUS_1 = 3.23606;
    protected float radius;

    private DrawInterceptor<ParticleSphere, AfterDrawData> afterDraw;
    private DrawInterceptor<ParticleSphere, BeforeDrawData> beforeDraw;

    /** This data is used before calculations */
    public enum BeforeDrawData {}

    /** This data is used after calculations */
    public enum AfterDrawData {}

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleSphere(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount);
        this.setRadius(builder.radius);
        this.setAfterDraw(builder.afterDraw);
        this.setBeforeDraw(builder.beforeDraw);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.
     *
     * @param sphere The particle sphere object to copy from
    */
    public ParticleSphere(ParticleSphere sphere) {
        super(sphere);
        this.radius = sphere.radius;
        this.afterDraw = sphere.afterDraw;
        this.beforeDraw = sphere.beforeDraw;
    }

    /**
     * Sets the radius of the sphere.  The radius must be positive.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param radius The radius of the sphere
     * @return The previous radius used
    */
    public final float setRadius(float radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        float prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    /** Gets the radius of the sphere.
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

    /**
     * Set the interceptor to run after drawing the sphere.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, the center
     * of the sphere, and the ParticleSphere instance.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public final void setAfterDraw(DrawInterceptor<ParticleSphere, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step, Vector3f drawPos) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, drawPos, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /**
     * Set the interceptor to run before drawing the sphere.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, the center
     * of the sphere, and the ParticleSphere instance.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing the sphere
     */
    public final void setBeforeDraw(DrawInterceptor<ParticleSphere, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void doBeforeDraw(ServerWorld world, int step, Vector3f drawPos) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, drawPos, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B> {
        protected float radius;
        protected DrawInterceptor<ParticleSphere, AfterDrawData> afterDraw;
        protected DrawInterceptor<ParticleSphere, BeforeDrawData> beforeDraw;

        private Builder() {}

        /**
         * Set the radius on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B radius(float radius) {
            this.radius = radius;
            return self();
        }

        /**
         * Sets the interceptor to run after drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleSphere#setAfterDraw(DrawInterceptor)
         */
        public B afterDraw(DrawInterceptor<ParticleSphere, AfterDrawData> afterDraw) {
            this.afterDraw = afterDraw;
            return self();
        }

        /**
         * Sets the interceptor to run before drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleSphere#setBeforeDraw(DrawInterceptor)
         */
        public B beforeDraw(DrawInterceptor<ParticleSphere, BeforeDrawData> beforeDraw) {
            this.beforeDraw = beforeDraw;
            return self();
        }

        @Override
        public ParticleSphere build() {
            return new ParticleSphere(this);
        }
    }
}
