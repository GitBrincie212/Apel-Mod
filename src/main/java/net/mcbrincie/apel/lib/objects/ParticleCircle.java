package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
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
    protected EasingCurve<Float> radius;

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
    public EasingCurve<Float> getRadius() {
        return radius;
    }

    /**
     * Set the radius of this ParticleCircle and returns the previous radius that was used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This overload will set a constant value for the radius
     *
     * @param radius the new radius
     * @return the previously used radius
     */
    public final EasingCurve<Float> setRadius(float radius) {
        EasingCurve<Float> prevRadius = this.radius;
        this.radius = new ConstantEasingCurve<>(radius);
        return prevRadius;
    }

    /**
     * Set the radius of this ParticleCircle and returns the previous radius that was used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This overload will set a constant value for the radius
     *
     * @param radius the new radius
     * @return the previously used radius
     */
    public final EasingCurve<Float> setRadius(EasingCurve<Float> radius) {
        EasingCurve<Float> prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    @Override
    protected ComputedEasingPO computeAdditionalEasings(ComputedEasingPO container) {
        return container
                .addComputedField("radius", this.radius);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        ComputedEasingPO computedEasingPO = drawContext.getComputedEasings();
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(computedEasingPO.computedOffset);
        float currRadius = (float) computedEasingPO.getComputedField("radius");
        if (currRadius <= 0) {
            throw new RuntimeException("The radius must be positive and non-zero");
        }
        renderer.drawEllipse(
                this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, currRadius, currRadius,
                computedEasingPO.computedRotation, computedEasingPO.computedAmount
        );
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleCircle> {
        protected EasingCurve<Float> radius;

        private Builder() {}

        /**
         * Set the radius on the builder. This method is not cumulative; repeated calls will overwrite the value.
         * This method overload will set a constant value for the radius
         */
        public B radius(float radius) {
            this.radius = new ConstantEasingCurve<>(radius);
            return self();
        }

        /**
         * Set the radius on the builder. This method is not cumulative; repeated calls will overwrite the value.
         * This method overload will set a constant value for the radius
         */
        public B radius(EasingCurve<Float> radius) {
            this.radius = radius;
            return self();
        }

        @Override
        public ParticleCircle build() {
            return new ParticleCircle(this);
        }
    }
}
