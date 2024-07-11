package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import org.joml.Vector3f;

/** The particle object class that represents an ellipse.
 * It has a radius which dictates how large or small the ellipse is depending on the
 * radius value supplied and a stretch value for how stretched is the ellipse.  The radius
 * value is used as the X semi-axis, and the stretch value is used as the Y semi-axis.
 * Setting radius and stretch equal to one another means it is a circle.  The ellipse is
 * drawn in the xy-plane by default, though rotations can move it around.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleEllipse extends ParticleObject<ParticleEllipse> {
    protected float radius;
    protected float stretch;

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleEllipse(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setRadius(builder.radius);
        this.setStretch(builder.stretch);
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
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);
        renderer.drawEllipse(
                this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, this.radius, this.stretch,
                this.rotation, this.amount
        );
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleEllipse> {
        protected float radius;
        protected float stretch;

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

        @Override
        public ParticleEllipse build() {
            return new ParticleEllipse(this);
        }
    }
}
