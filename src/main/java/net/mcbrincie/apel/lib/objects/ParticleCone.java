package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import org.joml.Vector3f;

/** The particle object class that represents a 3D shape(a cone).
 * It requires a height value which dictates how tall the cone is as well as
 * the maximum radius, it also accepts rotation for the cone
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCone extends ParticleObject<ParticleCone> {
    protected float height;
    protected float radius;

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleCone(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setHeight(builder.height);
        this.setRadius(builder.radius);
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
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);
        renderer.drawCone(
                this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, this.height, this.radius,
                this.rotation, this.amount
        );
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleCone> {
        protected float height;
        protected float radius;

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

        @Override
        public ParticleCone build() {
            return new ParticleCone(this);
        }
    }
}
