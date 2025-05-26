package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.ComputedEasingRPO;
import net.mcbrincie.apel.lib.util.ComputedEasings;
import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
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
public class ParticleCylinder extends RenderableParticleObject<ParticleCylinder> {
    protected EasingCurve<Float> radius;
    protected EasingCurve<Float> height;

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
    public EasingCurve<Float> getRadius() {
        return radius;
    }

    /**
     * Set the radius of this ParticleCylinder and returns the previous radius that was used.  Radius must be positive.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the radius
     *
     * @param radius the new radius
     * @return the previously used radius
     */
    public final EasingCurve<Float> setRadius(float radius) {
        return this.setRadius(new ConstantEasingCurve<>(radius));
    }

    /**
     * Set the radius of this ParticleCylinder and returns the previous radius that was used.  Radius must be positive.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an easing curve value for the radius
     *
     * @param radius the new radius
     * @return the previously used radius
     */
    public final EasingCurve<Float> setRadius(EasingCurve<Float> radius) {
        EasingCurve<Float> prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    /** Gets the height of the ParticleCylinder and returns it.
     *
     * @return the height of the ParticleCylinder
    */
    public EasingCurve<Float> getHeight() {
        return height;
    }

    /**
     * Sets the height of the ParticleCylinder and returns the previous height that was used.  Height must be positive.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the height
     *
     * @param height The new height
     * @return The previous used height
     */
    public final EasingCurve<Float> setHeight(float height) {
        return this.setHeight(new ConstantEasingCurve<>(height));
    }

    /**
     * Sets the height of the ParticleCylinder and returns the previous height that was used.  Height must be positive.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the height
     *
     * @param height The new height
     * @return The previous used height
     */
    public final EasingCurve<Float> setHeight(EasingCurve<Float> height) {
        EasingCurve<Float> prevHeight = this.height;
        this.height = height;
        return prevHeight;
    }

    @Override
    protected ComputedEasingRPO computeAdditionalEasings(ComputedEasingRPO container) {
        return container.addComputedField("radius", this.radius)
                .addComputedField("height", this.height);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        ComputedEasingRPO computedEasings = (ComputedEasingRPO) drawContext.getComputedEasings();
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(computedEasings.computedOffset);
        float currRadius = (float) computedEasings.getComputedField("radius");
        float currHeight = (float) computedEasings.getComputedField("height");
        if (currRadius <= 0) {
            throw new RuntimeException("The cylinder's radius is below or equal to zero");
        } else if (currHeight <= 0) {
            throw new RuntimeException("The cylinder's height is below or equal to zero");
        }
        renderer.drawCylinder(
                this.particleEffect, drawContext.getCurrentStep(), objectDrawPos,
                currRadius, currHeight, computedEasings.computedRotation, computedEasings.computedAmount
        );
    }

    public static class Builder<B extends Builder<B>> extends RenderableParticleObject.Builder<B, ParticleCylinder> {
        protected EasingCurve<Float> radius;
        protected EasingCurve<Float> height;

        private Builder() {}

        /**
         * Set the radius on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B radius(EasingCurve<Float> radius) {
            this.radius = radius;
            return self();
        }

        /**
         * Set the height on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B height(EasingCurve<Float> height) {
            this.height = height;
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
         * Set the height on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B height(float height) {
            this.height = new ConstantEasingCurve<>(height);
            return self();
        }

        @Override
        public ParticleCylinder build() {
            return new ParticleCylinder(this);
        }
    }
}
