package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import org.joml.Vector3f;

/** The particle object class that represents a cylinder.
 * It has a radius which dictates how large or small the cylinder is depending on the
 * radius value supplied and a height value for how tall it is.  The cylinder is drawn
 * with particles evenly dispersed around its sides, but has no particles filling in
 * the bases.  One base is in the xz-plane by default, and the other base is in the positive-y
 * direction.  If the cylinder should be in the negative-y direction, a rotation about the
 * x-axis will achieve that.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCylinder extends ParticleObject<ParticleCylinder> {
    protected float radius;
    protected float height;

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleCylinder(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setRadius(builder.radius);
        this.setHeight(builder.height);
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
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);
        renderer.drawCylinder(
                this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, this.radius, this.height,
                this.rotation, this.amount
        );
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleCylinder> {
        protected float radius;
        protected float height;

        private Builder() {}

        /**
         * Set the radius on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B radius(float radius) {
            this.radius = radius;
            return self();
        }

        /**
         * Set the height on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B height(float height) {
            this.height = height;
            return self();
        }

        @Override
        public ParticleCylinder build() {
            return new ParticleCylinder(this);
        }
    }
}
