package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import org.joml.Vector3f;

/**
 * The particle object class that represents an ellipsoid (3D shape) and not a 2D circle.
 * It has three semi-axis values that determine the ellipsoid's shape. It projects the <em>golden spiral</em>
 * on to the ellipsoid to distribute particles evenly across the surface.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleEllipsoid extends ParticleObject<ParticleEllipsoid> {
    public static final double SQRT_5_PLUS_1 = 3.23606;
    protected float xSemiAxis;
    protected float ySemiAxis;
    protected float zSemiAxis;

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
    public float getXSemiAxis() {
        return this.xSemiAxis;
    }

    /**
     * Sets length of the X semi-axis.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param xSemiAxis The length of the X semi-axis
     * @return The previous length of the X semi-axis
     */
    public final float setXSemiAxis(float xSemiAxis) {
        if (xSemiAxis <= 0) {
            throw new IllegalArgumentException("Length of X semi-axis cannot be below or equal to 0");
        }
        float prevXSemiAxis = this.xSemiAxis;
        this.xSemiAxis = xSemiAxis;
        return prevXSemiAxis;
    }

    /**
     * Gets length of the Y semi-axis.
     *
     * @return the length of the Y semi-axis
     */
    public float getYSemiAxis() {
        return this.ySemiAxis;
    }

    /**
     * Sets length of the Y semi-axis.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param ySemiAxis The length of the Y semi-axis
     * @return The previous length of the Y semi-axis
     */
    public final float setYSemiAxis(float ySemiAxis) {
        if (ySemiAxis <= 0) {
            throw new IllegalArgumentException("Length of Y semi-axis cannot be below or equal to 0");
        }
        float prevYSemiAxis = this.ySemiAxis;
        this.ySemiAxis = ySemiAxis;
        return prevYSemiAxis;
    }

    /**
     * Gets length of the Z semi-axis.
     *
     * @return the length of the Z semi-axis
     */
    public float getZSemiAxis() {
        return this.zSemiAxis;
    }

    /**
     * Sets length of the Z semi-axis.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param zSemiAxis The length of the Z semi-axis
     * @return The previous length of the Z semi-axis
     */
    public final float setZSemiAxis(float zSemiAxis) {
        if (zSemiAxis <= 0) {
            throw new IllegalArgumentException("Length of Z semi-axis cannot be below or equal to 0");
        }
        float prevZSemiAxis = this.zSemiAxis;
        this.zSemiAxis = zSemiAxis;
        return prevZSemiAxis;
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);
        renderer.drawEllipsoid(this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, this.xSemiAxis,
                               this.ySemiAxis, this.zSemiAxis, this.rotation, this.amount
        );
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleEllipsoid> {
        protected float xSemiAxis;
        protected float ySemiAxis;
        protected float zSemiAxis;

        private Builder() {}

        /**
         * Set the X semi-axis on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B xSemiAxis(float xSemiAxis) {
            this.xSemiAxis = xSemiAxis;
            return self();
        }

        /**
         * Set the Y semi-axis on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B ySemiAxis(float ySemiAxis) {
            this.ySemiAxis = ySemiAxis;
            return self();
        }

        /**
         * Set the Z semi-axis on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B zSemiAxis(float zSemiAxis) {
            this.zSemiAxis = zSemiAxis;
            return self();
        }

        @Override
        public ParticleEllipsoid build() {
            return new ParticleEllipsoid(this);
        }
    }
}
