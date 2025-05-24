package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.ComputedEasingRPO;
import net.mcbrincie.apel.lib.util.ComputedEasings;
import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import org.joml.Vector3f;

/** The particle object class that represents an ellipse.
 * It has a radius which dictates how large or small the ellipse is depending on the
 * radius value supplied and a stretch value for how stretched is the ellipse.  The radius
 * value is used as the X semi-axis, and the stretch value is used as the Y semi-axis.
 * Setting radius and stretch equal to one another means it is a circle.  The ellipse is
 * drawn on the xy-plane by default, though rotations can move it around.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleEllipse extends RenderableParticleObject<ParticleEllipse> {
    protected EasingCurve<Float> radius;
    protected EasingCurve<Float> stretch;

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
    public EasingCurve<Float> getRadius() {
        return radius;
    }

    /**
     * Set the radius of this ParticleEllipse and returns the previous radius that was used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This overload will set a constant value to the radius
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
     * Set the radius of this ParticleEllipse and returns the previous radius that was used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This overload will set an ease curve value to the radius
     *
     * @param radius the new radius
     * @return the previously used radius
     */
    public final EasingCurve<Float> setRadius(EasingCurve<Float> radius) {
        EasingCurve<Float> prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    /** Gets the stretch of the ParticleEllipse and returns it.
     *
     * @return the stretch of the ParticleEllipse
     */
    public EasingCurve<Float> getStretch() {
        return stretch;
    }

    /**
     * Sets the stretch of the ParticleEllipse and returns the previous stretch that was used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This overload will set a constant value to the stretch
     *
     * @param stretch The new stretch
     * @return The previous used stretch
     */
    public final EasingCurve<Float> setStretch(float stretch) {
        EasingCurve<Float> prevStretch = this.stretch;
        this.stretch = new ConstantEasingCurve<>(stretch);
        return prevStretch;
    }

    /**
     * Sets the stretch of the ParticleEllipse and returns the previous stretch that was used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This overload will set an ease curve value to the stretch
     *
     * @param stretch The new stretch
     * @return The previous used stretch
     */
    public final EasingCurve<Float> setStretch(EasingCurve<Float> stretch) {
        EasingCurve<Float> prevStretch = this.stretch;
        this.stretch = stretch;
        return prevStretch;
    }

    @Override
    protected ComputedEasings computeAdditionalEasings(ComputedEasingPO container) {
        return container
                .addComputedField("radius", this.radius)
                .addComputedField("stretch", this.stretch);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        ComputedEasingRPO computedEasingPO = (ComputedEasingRPO) drawContext.getComputedEasings();
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(computedEasingPO.computedOffset);
        float currRadius = (float) computedEasingPO.getComputedField("radius");
        float currStretch = (float) computedEasingPO.getComputedField("stretch");
        if (currRadius <= 0) {
            throw new RuntimeException("The radius must be positive and non-zero");
        } else if (currStretch <= 0) {
            throw new RuntimeException("The stretch must be positive and non-zero");
        }
        renderer.drawEllipse(
                this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, currRadius, currStretch,
                computedEasingPO.computedRotation, computedEasingPO.computedAmount
        );
    }

    public static class Builder<B extends Builder<B>> extends RenderableParticleObject.Builder<B, ParticleEllipse> {
        protected EasingCurve<Float> radius;
        protected EasingCurve<Float> stretch;

        private Builder() {}

        /**
         * Set the radius on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B radius(EasingCurve<Float> radius) {
            this.radius = radius;
            return self();
        }

        /**
         * Set the stretch on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B stretch(EasingCurve<Float> stretch) {
            this.stretch = stretch;
            return self();
        }

        /**
         * Set the radius on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B radius(float radius) {
            this.radius = new ConstantEasingCurve<>(radius);
            return self();
        }

        /**
         * Set the stretch on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B stretch(float stretch) {
            this.stretch = new ConstantEasingCurve<>(stretch);
            return self();
        }

        @Override
        public ParticleEllipse build() {
            return new ParticleEllipse(this);
        }
    }
}
