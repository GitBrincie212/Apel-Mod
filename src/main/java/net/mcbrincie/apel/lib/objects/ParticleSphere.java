package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import org.joml.Vector3f;

/** The particle object class that represents a sphere.
 * It has a radius which dictates how large or small the sphere is.  It projects the <em>golden spiral</em>
 * on to the sphere to distribute particles evenly across the surface.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleSphere extends ParticleObject<ParticleSphere> {
    public static final double SQRT_5_PLUS_1 = 3.23606;
    protected float radius;

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleSphere(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setRadius(builder.radius);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.
     *
     * @param sphere The particle sphere object to copy from
    */
    public ParticleSphere(ParticleSphere sphere) {
        super(sphere);
        this.radius = sphere.radius;
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
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        System.out.println(drawContext.getDeltaTickTime());
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);
        renderer.drawEllipsoid(this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, this.radius,
                               this.radius, this.radius, this.rotation, this.amount
        );
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleSphere> {
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
        public ParticleSphere build() {
            return new ParticleSphere(this);
        }
    }
}
