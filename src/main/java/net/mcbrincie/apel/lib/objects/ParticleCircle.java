package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import org.joml.Vector3f;

/** The particle object class that represents a circle (2D shape) and not a 3D sphere.
 * It has a radius which dictates how large or small the circle is depending on the
 * radius value supplied.
 * The circle is drawn on the XY-plane (east/west and up/down) by default, but can
 * be drawn on any plane by using {@link #setRotation(Vector3f)} to provide Euler
 * angles for rotation.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCircle extends ParticleObject<ParticleCircle> {
    protected float radius;

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleCircle(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setRadius(builder.radius);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param circle The particle circle object to copy from
    */
    public ParticleCircle(ParticleCircle circle) {
        super(circle);
        this.radius = circle.radius;
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
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);
        renderer.drawEllipse(
                this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, this.radius, this.radius,
                this.rotation, this.amount
        );
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleCircle> {
        protected float radius;

        private Builder() {}

        /**
         * Set the radius on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B radius(float radius) {
            this.radius = radius;
            return self();
        }

        @Override
        public ParticleCircle build() {
            return new ParticleCircle(this);
        }
    }
}
