package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.Optional;

/** The particle object class that represents a cylinder.
 * It has a radius which dictates how large or small the cylinder is depending on the
 * radius value supplied and a height value for how tall it is.  The cylinder is drawn
 * with particles evenly dispersed around its sides, but has no particles filling in
 * the bases.  One base is in the xz-plane by default, and the other base is in the positive-y
 * direction.  If the cylinder should be in the negative-y direction, a rotation about the
 * x-axis will achieve that.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCylinder extends ParticleObject {
    protected float radius;
    protected float height;

    private DrawInterceptor<ParticleCylinder, AfterDrawData> afterDraw;
    private DrawInterceptor<ParticleCylinder, BeforeDrawData> beforeDraw;

    /** This data is used before calculations (it contains the iterated rotation) */
    public enum BeforeDrawData {}

    /** This data is used after calculations (it contains the drawing position) */
    public enum AfterDrawData {}

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleCylinder(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount);
        this.setRadius(builder.radius);
        this.setHeight(builder.height);
        this.setBeforeDraw(builder.beforeDraw);
        this.setAfterDraw(builder.afterDraw);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param cylinder The particle cylinder object to copy from
    */
    public ParticleCylinder(ParticleCylinder cylinder) {
        super(cylinder);
        this.radius = cylinder.radius;
        this.height = cylinder.height;
        this.afterDraw = cylinder.afterDraw;
        this.beforeDraw = cylinder.beforeDraw;
    }

    /** Gets the radius of the ParticleCylinder and returns it.
     *
     * @return the radius of the ParticleCylinder
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Set the radius of this ParticleCylinder and returns the previous radius that was used.  Radius must be positive.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param radius the new radius
     * @return the previously used radius
     */
    public final float setRadius(float radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        float prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    /** Gets the height of the ParticleCylinder and returns it.
     *
     * @return the height of the ParticleCylinder
    */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the height of the ParticleCylinder and returns the previous height that was used.  Height must be positive.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param height The new height
     * @return The previous used height
     */
    public final float setHeight(float height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        float prevHeight = this.height;
        this.height = height;
        return prevHeight;
    }

    @Override
    public void draw(ApelServerRenderer renderer, int step, Vector3f drawPos) {
        this.doBeforeDraw(renderer.getServerWorld(), step, drawPos);
        Vector3f objectDrawPos = new Vector3f(drawPos).add(this.offset);
        renderer.drawCylinder(this.particleEffect, step, objectDrawPos, this.radius, this.height, this.rotation, this.amount);
        this.doAfterDraw(renderer.getServerWorld(), step, drawPos);
        this.endDraw(renderer, step, drawPos);
    }

    /**
     * Set the interceptor to run after drawing the cylinder. The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position where the cylinder is rendered.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public final void setAfterDraw(DrawInterceptor<ParticleCylinder, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step, Vector3f centerPos) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, centerPos, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /**
     * Set the interceptor to run prior to drawing the cylinder. The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position where the cylinder is rendered.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing each particle
     */
    public final void setBeforeDraw(DrawInterceptor<ParticleCylinder, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void doBeforeDraw(ServerWorld world, int step, Vector3f pos) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, pos, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B> {
        protected float radius;
        protected float height;
        protected DrawInterceptor<ParticleCylinder, AfterDrawData> afterDraw;
        protected DrawInterceptor<ParticleCylinder, BeforeDrawData> beforeDraw;

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
         * @see ParticleCylinder#setAfterDraw(DrawInterceptor)
         */
        public B afterDraw(DrawInterceptor<ParticleCylinder, AfterDrawData> afterDraw) {
            this.afterDraw = afterDraw;
            return self();
        }

        /**
         * Sets the interceptor to run before drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleCylinder#setBeforeDraw(DrawInterceptor)
         */
        public B beforeDraw(DrawInterceptor<ParticleCylinder, BeforeDrawData> beforeDraw) {
            this.beforeDraw = beforeDraw;
            return self();
        }

        @Override
        public ParticleCylinder build() {
            return null;
        }
    }
}
