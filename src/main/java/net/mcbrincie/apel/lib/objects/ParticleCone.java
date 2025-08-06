package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingRPO;
import net.mcbrincie.apel.lib.util.interceptor.context.DrawContext;
import org.joml.Vector3f;

/** The particle object class that represents a 3D shape (a cone).
 * It requires a height value which dictates how tall the cone is as well as
 * the maximum radius, it also accepts rotation for the cone
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCone extends RenderableParticleObject<ParticleCone> {
    protected EasingCurve<Float> height;
    protected EasingCurve<Float> radius;

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

    /** Gets the radius of the cone and returns it.
     *
     * @return the radius of the cone
     */
    public EasingCurve<Float> getRadius() {
        return radius;
    }

    /**
     * Set the radius of this cone and returns the previous radius that was used. Radius must be positive.
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
     * Set the radius of this cone and returns the previous radius that was used. Radius must be positive.
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

    /** Gets the height of the cone and returns it.
     *
     * @return the height of the cone
     */
    public EasingCurve<Float> getHeight() {
        return height;
    }

    /**
     * Sets the height of the cone and returns the previous height that was used. Height must be positive.
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
     * Sets the height of the cone and returns the previous height that was used. Height must be positive.
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
    public void draw(ApelServerRenderer renderer, DrawContext<ComputedEasingRPO> drawContext, Vector3f actualSize) {
        ComputedEasingRPO computedEasingRPO = drawContext.getComputedEasings();
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(computedEasingRPO.computedOffset);
        float currRadius = (float) computedEasingRPO.getComputedField("radius");
        float currHeight = (float) computedEasingRPO.getComputedField("height");
        /*
        if (currRadius <= 0) {
            throw new RuntimeException("The cone's radius is below or equal to zero");
        } else if (currHeight <= 0) {
            throw new RuntimeException("The cone's height is below or equal to zero");
        }
         */
        currRadius *= actualSize.x;
        currHeight *= actualSize.y;

        renderer.drawCone(
                this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, currHeight, currRadius,
                computedEasingRPO.computedRotation, computedEasingRPO.computedAmount
        );
    }

    public static class Builder<B extends Builder<B>> extends RenderableParticleObject.Builder<B, ParticleCone> {
        protected EasingCurve<Float> height;
        protected EasingCurve<Float> radius;

        private Builder() {}

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
        public B radius(EasingCurve<Float> radius) {
            this.radius = radius;
            return self();
        }

        /**
         * Set the height on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B height(float height) {
            this.height = new ConstantEasingCurve<>(height);
            return self();
        }

        /**
         * Set the radius on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B radius(float radius) {
            this.radius = new ConstantEasingCurve<>(radius);
            return self();
        }

        @Override
        public ParticleCone build() {
            return new ParticleCone(this);
        }
    }
}
