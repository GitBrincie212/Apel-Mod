package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.ComputedEasings;
import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import org.joml.Vector3f;

/**
 * The particle object class that represents an ellipsoid (3D shape) and not a 2D circle.
 * It has three semi-axis values that determine the ellipsoid's shape. It projects the <em>golden spiral</em>
 * on to the ellipsoid to distribute particles evenly across the surface.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleEllipsoid extends ParticleObject<ParticleEllipsoid> {
    public static final double SQRT_5_PLUS_1 = 3.23606;
    protected EasingCurve<Float> xSemiAxis;
    protected EasingCurve<Float> ySemiAxis;
    protected EasingCurve<Float> zSemiAxis;

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleEllipsoid(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setXSemiAxis(builder.xSemiAxis);
        this.setYSemiAxis(builder.ySemiAxis);
        this.setZSemiAxis(builder.zSemiAxis);
    }

    /**
     * The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.
     *
     * @param particleEllipsoid The particle ellipsoid object to copy from
     */
    public ParticleEllipsoid(ParticleEllipsoid particleEllipsoid) {
        super(particleEllipsoid);
        this.xSemiAxis = particleEllipsoid.xSemiAxis;
        this.ySemiAxis = particleEllipsoid.ySemiAxis;
        this.zSemiAxis = particleEllipsoid.zSemiAxis;
    }

    /**
     * Gets length of the X semi-axis.
     *
     * @return the length of the X semi-axis
     */
    public EasingCurve<Float> getXSemiAxis() {
        return this.xSemiAxis;
    }

    /**
     * Sets length of the X semi-axis.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the xSemiAxis
     *
     * @param xSemiAxis The length of the X semi-axis
     * @return The previous length of the X semi-axis
     */
    public final EasingCurve<Float> setXSemiAxis(float xSemiAxis) {
        return this.setXSemiAxis(new ConstantEasingCurve<>(xSemiAxis));
    }

    /**
     * Sets length of the X semi-axis.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the xSemiAxis
     *
     * @param xSemiAxis The length of the X semi-axis
     * @return The previous length of the X semi-axis
     */
    public final EasingCurve<Float> setXSemiAxis(EasingCurve<Float> xSemiAxis) {
        EasingCurve<Float> prevXSemiAxis = this.xSemiAxis;
        this.xSemiAxis = xSemiAxis;
        return prevXSemiAxis;
    }

    /**
     * Gets length of the Y semi-axis.
     *
     * @return the length of the Y semi-axis
     */
    public EasingCurve<Float> getYSemiAxis() {
        return this.ySemiAxis;
    }

    /**
     * Sets length of the Y semi-axis.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the ySemiAxis
     *
     * @param ySemiAxis The length of the Y semi-axis
     * @return The previous length of the Y semi-axis
     */
    public final EasingCurve<Float> setYSemiAxis(float ySemiAxis) {
        return this.setYSemiAxis(new ConstantEasingCurve<>(ySemiAxis));
    }

    /**
     * Sets length of the Y semi-axis.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the ySemiAxis
     *
     * @param ySemiAxis The length of the Y semi-axis
     * @return The previous length of the Y semi-axis
     */
    public final EasingCurve<Float> setYSemiAxis(EasingCurve<Float> ySemiAxis) {
        EasingCurve<Float> prevYSemiAxis = this.ySemiAxis;
        this.ySemiAxis = ySemiAxis;
        return prevYSemiAxis;
    }

    /**
     * Gets length of the Z semi-axis.
     *
     * @return the length of the Z semi-axis
     */
    public EasingCurve<Float> getZSemiAxis() {
        return this.zSemiAxis;
    }

    /**
     * Sets length of the Z semi-axis.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the zSemiAxis
     *
     * @param zSemiAxis The length of the Z semi-axis
     * @return The previous length of the Z semi-axis
     */
    public final EasingCurve<Float> setZSemiAxis(float zSemiAxis) {
        return this.setZSemiAxis(new ConstantEasingCurve<>(zSemiAxis));
    }

    /**
     * Sets length of the Z semi-axis.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the zSemiAxis
     *
     * @param zSemiAxis The length of the Z semi-axis
     * @return The previous length of the Z semi-axis
     */
    public final EasingCurve<Float> setZSemiAxis(EasingCurve<Float> zSemiAxis) {
        EasingCurve<Float> prevZSemiAxis = this.zSemiAxis;
        this.zSemiAxis = zSemiAxis;
        return prevZSemiAxis;
    }

    @Override
    protected ComputedEasings computeAdditionalEasings(ComputedEasingPO container) {
        return container.addComputedField("xSemiAxis", this.xSemiAxis)
                .addComputedField("ySemiAxis", this.ySemiAxis)
                .addComputedField("zSemiAxis", this.zSemiAxis);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        ComputedEasingPO computedEasings = drawContext.getComputedEasings();
        float xSemiAxis = (float) computedEasings.getComputedField("xSemiAxis");
        float ySemiAxis = (float) computedEasings.getComputedField("ySemiAxis");
        float zSemiAxis = (float) computedEasings.getComputedField("zSemiAxis");
        if (xSemiAxis <= 0 || ySemiAxis <= 0 || zSemiAxis <= 0) {
            throw new RuntimeException("One of the semi axis values is negative or zero");
        }
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(computedEasings.computedOffset);
        renderer.drawEllipsoid(this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, xSemiAxis,
                ySemiAxis, zSemiAxis, computedEasings.computedRotation, computedEasings.computedAmount
        );
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleEllipsoid> {
        protected EasingCurve<Float> xSemiAxis;
        protected EasingCurve<Float> ySemiAxis;
        protected EasingCurve<Float> zSemiAxis;

        private Builder() {}

        /**
         * Set the X semi-axis on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B xSemiAxis(float xSemiAxis) {
            this.xSemiAxis = new ConstantEasingCurve<>(xSemiAxis);
            return self();
        }

        /**
         * Set the Y semi-axis on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B ySemiAxis(float ySemiAxis) {
            this.ySemiAxis = new ConstantEasingCurve<>(ySemiAxis);
            return self();
        }

        /**
         * Set the Z semi-axis on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B zSemiAxis(float zSemiAxis) {
            this.zSemiAxis = new ConstantEasingCurve<>(zSemiAxis);
            return self();
        }

        /**
         * Set the X semi-axis on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B xSemiAxis(EasingCurve<Float> xSemiAxis) {
            this.xSemiAxis = xSemiAxis;
            return self();
        }

        /**
         * Set the Y semi-axis on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B ySemiAxis(EasingCurve<Float> ySemiAxis) {
            this.ySemiAxis = ySemiAxis;
            return self();
        }

        /**
         * Set the Z semi-axis on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B zSemiAxis(EasingCurve<Float> zSemiAxis) {
            this.zSemiAxis = zSemiAxis;
            return self();
        }

        @Override
        public ParticleEllipsoid build() {
            return new ParticleEllipsoid(this);
        }
    }
}
