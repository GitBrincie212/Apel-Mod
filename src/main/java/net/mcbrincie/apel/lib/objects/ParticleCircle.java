package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.Optional;

/** The particle object class that represents a circle (2D shape) and not a 3D sphere.
 * It has a radius which dictates how large or small the circle is depending on the
 * radius value supplied.
 * The circle is drawn on the XY-plane (east/west and up/down) by default, but can
 * be drawn on any plane by using {@link #setRotation(Vector3f)} to provide Euler
 * angles for rotation.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCircle extends ParticleObject {
    protected float radius;

    private DrawInterceptor<ParticleCircle, AfterDrawData> afterDraw;
    private DrawInterceptor<ParticleCircle, BeforeDrawData> beforeDraw;

    /** This data is used before calculations (it contains the iterated rotation) */
    public enum BeforeDrawData {}

    /** This data is used after calculations (it contains the drawing position) */
    public enum AfterDrawData {}

    /**
     * Provide a builder instance.
     * @return A builder instance
     */
    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleCircle(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount);
        this.setRadius(builder.radius);
        this.setBeforeDraw(builder.beforeDraw);
        this.setAfterDraw(builder.afterDraw);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param circle The particle circle object to copy from
    */
    public ParticleCircle(ParticleCircle circle) {
        super(circle);
        this.radius = circle.radius;
        this.afterDraw = circle.afterDraw;
        this.beforeDraw = circle.beforeDraw;
    }

    /** Gets the radius of the ParticleCircle and returns it.
     *
     * @return the radius of the ParticleCircle
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Set the radius of this ParticleCircle and returns the previous radius that was used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param radius the new radius
     * @return the previously used radius
     */
    public final float setRadius(float radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius cannot be negative");
        }
        float prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    @Override
    public void draw(ApelServerRenderer renderer, int step, Vector3f drawPos) {
        this.doBeforeDraw(renderer.getServerWorld(), step, drawPos);
        Vector3f objectDrawPos = new Vector3f(drawPos).add(this.offset);
        renderer.drawEllipse(
                this.particleEffect, step, objectDrawPos, this.radius, this.radius, this.rotation, this.amount);
        this.doAfterDraw(renderer.getServerWorld(), step, drawPos);
        this.endDraw(renderer, step, drawPos);
    }

    /**
     * Set the interceptor to run after drawing the circle.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position of the center of the circle.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public void setAfterDraw(DrawInterceptor<ParticleCircle, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step, Vector3f centerPos) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, centerPos, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /**
     * Set the interceptor to run prior to drawing the circle.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position of the center of the circle.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing each particle
     */
    public void setBeforeDraw(DrawInterceptor<ParticleCircle, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void doBeforeDraw(ServerWorld world, int step, Vector3f pos) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, pos, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B> {
        protected float radius;
        protected DrawInterceptor<ParticleCircle, AfterDrawData> afterDraw;
        protected DrawInterceptor<ParticleCircle, BeforeDrawData> beforeDraw;

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
         * @see ParticleCircle#setAfterDraw(DrawInterceptor)
         */
        public B afterDraw(DrawInterceptor<ParticleCircle, AfterDrawData> afterDraw) {
            this.afterDraw = afterDraw;
            return self();
        }

        /**
         * Sets the interceptor to run before drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleCircle#setBeforeDraw(DrawInterceptor)
         */
        public B beforeDraw(DrawInterceptor<ParticleCircle, BeforeDrawData> beforeDraw) {
            this.beforeDraw = beforeDraw;
            return self();
        }

        @Override
        public ParticleCircle build() {
            return new ParticleCircle(this);
        }
    }
}
