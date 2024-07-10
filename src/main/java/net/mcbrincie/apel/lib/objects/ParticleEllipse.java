package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Optional;

/** The particle object class that represents an ellipse.
 * It has a radius which dictates how large or small the ellipse is depending on the
 * radius value supplied and a stretch value for how stretched is the ellipse.  The radius
 * value is used as the X semi-axis, and the stretch value is used as the Y semi-axis.
 * Setting radius and stretch equal to one another means it is a circle.  The ellipse is
 * drawn in the xy-plane by default, though rotations can move it around.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleEllipse extends ParticleObject {
    protected float radius;
    protected float stretch;

    private DrawInterceptor<ParticleEllipse, AfterDrawData> afterDraw;
    private DrawInterceptor<ParticleEllipse, BeforeDrawData> beforeDraw;

    /** This data is used before calculations */
    public enum BeforeDrawData {}

    /** This data is used after calculations */
    public enum AfterDrawData {}

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleEllipse(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount);
        this.setRadius(builder.radius);
        this.setStretch(builder.stretch);
        this.setAfterDraw(builder.afterDraw);
        this.setBeforeDraw(builder.beforeDraw);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param ellipse The particle ellipse object to copy from
     */
    public ParticleEllipse(ParticleEllipse ellipse) {
        super(ellipse);
        this.radius = ellipse.radius;
        this.stretch = ellipse.stretch;
        this.beforeDraw = ellipse.beforeDraw;
        this.afterDraw = ellipse.afterDraw;
    }

    /** Gets the radius of the ParticleEllipse and returns it.
     *
     * @return the radius of the ParticleEllipse
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Set the radius of this ParticleEllipse and returns the previous radius that was used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param radius the new radius
     * @return the previously used radius
     */
    public final float setRadius(float radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("stretch cannot be negative");
        }
        float prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    /** Gets the stretch of the ParticleEllipse and returns it.
     *
     * @return the stretch of the ParticleEllipse
     */
    public float getStretch() {
        return stretch;
    }

    /**
     * Sets the stretch of the ParticleEllipse and returns the previous stretch that was used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param stretch The new stretch
     * @return The previous used stretch
     */
    public final float setStretch(float stretch) {
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

    /**
     * Set the interceptor to run after drawing the ellipse. The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position of the center of the ellipse.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public final void setAfterDraw(DrawInterceptor<ParticleEllipse, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step, Vector3f drawPos) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, drawPos, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /**
     * Set the interceptor to run prior to drawing the ellipse. The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position of the center of the ellipse.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing each particle
     */
    public final void setBeforeDraw(DrawInterceptor<ParticleEllipse, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void doBeforeDraw(ServerWorld world, int step, Vector3f pos) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, pos, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B> {
        protected float radius;
        protected float stretch;
        protected DrawInterceptor<ParticleEllipse, AfterDrawData> afterDraw;
        protected DrawInterceptor<ParticleEllipse, BeforeDrawData> beforeDraw;

        private Builder() {}

        /**
         * Set the radius on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B radius(float radius) {
            this.radius = radius;
            return self();
        }

        /**
         * Set the stretch on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B stretch(float stretch) {
            this.stretch = stretch;
            return self();
        }

        /**
         * Sets the interceptor to run after drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleEllipse#setAfterDraw(DrawInterceptor)
         */
        public B afterDraw(DrawInterceptor<ParticleEllipse, AfterDrawData> afterDraw) {
            this.afterDraw = afterDraw;
            return self();
        }

        /**
         * Sets the interceptor to run before drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleEllipse#setBeforeDraw(DrawInterceptor)
         */
        public B beforeDraw(DrawInterceptor<ParticleEllipse, BeforeDrawData> beforeDraw) {
            this.beforeDraw = beforeDraw;
            return self();
        }

        @Override
        public ParticleEllipse build() {
            return new ParticleEllipse(this);
        }
    }
}
