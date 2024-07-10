package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.Optional;

/** The particle object class that represents a 3D shape(a cone).
 * It requires a height value which dictates how tall the cone is as well as
 * the maximum radius, it also accepts rotation for the cone
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCone extends ParticleObject {
    protected float height;
    protected float radius;

    private DrawInterceptor<ParticleCone, AfterDrawData> afterDraw;
    private DrawInterceptor<ParticleCone, BeforeDrawData> beforeDraw;

    public enum BeforeDrawData {}
    public enum AfterDrawData {}

    /**
     * Provide a builder instance.
     * @return A builder instance
     */
    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleCone(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount);
        this.setHeight(builder.height);
        this.setRadius(builder.radius);
        this.setAfterDraw(builder.afterDraw);
        this.setBeforeDraw(builder.beforeDraw);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param cone The particle object to copy from
    */
    public ParticleCone(ParticleCone cone) {
        super(cone);
        this.height = cone.height;
        this.radius = cone.radius;
        this.beforeDraw = cone.beforeDraw;
        this.afterDraw = cone.afterDraw;
    }

    /** Gets the height of the cone
     *
     * @return The height of the cone
     */
    public float getHeight() {
        return this.height;
    }

    /** Sets the height of the cone
     *
     * @param height The new height
     * @return The previous height
    */
    public float setHeight(float height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be above 0");
        }
        float prevHeight = this.height;
        this.height = height;
        return prevHeight;
    }

    /** Gets the maximum radius of the cone and returns it.
     *
     * @return the maximum radius of the cone
     */
    public float getRadius() {
        return radius;
    }

    /** Set the maximum radius of this cone and returns the previous maximum radius that was used.
     *
     * @param radius the new maximum radius
     * @return the previously used maximum radius
     */
    public float setRadius(float radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius cannot be negative");
        }
        float prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    @Override
    public void draw(ApelServerRenderer renderer, int step, Vector3f drawPos) {
        this.doBeforeDraw(renderer.getServerWorld(), step);
        Vector3f objectDrawPos = new Vector3f(drawPos).add(this.offset);
        renderer.drawCone(this.particleEffect, step, objectDrawPos, this.height, this.radius, this.rotation, this.amount);
        this.doAfterDraw(renderer.getServerWorld(), step);
        this.endDraw(renderer, step, drawPos);
    }

    /** Sets the interceptor to run after drawing the cone. The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticleCone
     * instance.
     *
     * @param afterDraw the new interceptor to execute after drawing the cone
     */
    public void setAfterDraw(DrawInterceptor<ParticleCone, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, null, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /** Set the interceptor to run before drawing the cone. The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticleCone
     * instance.
     *
     * @param beforeDraw the new interceptor to execute before drawing the cone
     */
    public void setBeforeDraw(DrawInterceptor<ParticleCone, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void doBeforeDraw(ServerWorld world, int step) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, null, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B> {
        protected float height;
        protected float radius;
        protected DrawInterceptor<ParticleCone, AfterDrawData> afterDraw;
        protected DrawInterceptor<ParticleCone, BeforeDrawData> beforeDraw;

        private Builder() {}

        /**
         * Set the height on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B height(float height) {
            this.height = height;
            return self();
        }

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
         * @see ParticleCone#setAfterDraw(DrawInterceptor)
         */
        public B afterDraw(DrawInterceptor<ParticleCone, AfterDrawData> afterDraw) {
            this.afterDraw = afterDraw;
            return self();
        }

        /**
         * Sets the interceptor to run before drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleCone#setBeforeDraw(DrawInterceptor)
         */
        public B beforeDraw(DrawInterceptor<ParticleCone, BeforeDrawData> beforeDraw) {
            this.beforeDraw = beforeDraw;
            return self();
        }

        @Override
        public ParticleCone build() {
            return new ParticleCone(this);
        }
    }
}
