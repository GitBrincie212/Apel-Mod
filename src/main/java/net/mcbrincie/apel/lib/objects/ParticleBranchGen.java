package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

/**
 * This particle object subclass represents a set of 3D branches. Constructing the branch generator requires two points
 * which both are relative to the provided origin in the {@code DrawContext}, the minimum and maximum dimension threshold of
 * the entire branch set as well as the number of subdivisions for the fractal. The entire branch set may be rotated and offset
 * relative to the draw origin by using the builder properties, setters after construction, or during interceptor calls.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleBranchGen extends ParticleObject<ParticleBranchGen> {
    protected Vector3f minAngle;
    protected Vector3f maxAngle;
    protected float minLength;
    protected float maxLength;
    protected int maxBranchCount;
    protected int minBranchCount;
    private static final Random rand = Random.create();

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleBranchGen(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setMaxBranches(builder.maxBranches);
        this.setMinBranches(builder.minBranches);
        this.setMinAngle(builder.minAngle);
        this.setMaxAngle(builder.maxAngle);
        this.setMinLength(builder.minLengthThreshold);
        this.setMaxLength(builder.maxLengthThreshold);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.  The start and end points are copied to new
     * vectors.
     *
     * @param branchGen The particle object to copy from
    */
    public ParticleBranchGen(ParticleBranchGen branchGen) {
        super(branchGen);
        this.minAngle = new Vector3f(branchGen.minAngle);
        this.maxAngle = new Vector3f(branchGen.maxAngle);
        this.minLength = branchGen.minLength;
        this.maxLength = branchGen.maxLength;
        this.maxBranchCount = branchGen.maxBranchCount;
        this.minBranchCount = branchGen.minBranchCount;
    }

    /** Gets the minimum branch length threshold
     *
     * @return The minimum branch length threshold
     */
    public float getMinLength() {
        return this.minLength;
    }

    /**
     * Sets the minimum branch length threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param minLength The new minimum length threshold
     * @return The minimum length threshold
     */
    public final float setMinLength(float minLength) {
        if (minLength <= 0) {
            throw new IllegalArgumentException("The minimum length value is lower than or equal to zero");
        }
        float prevLength = this.minLength;
        this.minLength = minLength;
        return prevLength;
    }

    /** Gets the maximum branch length threshold
     *
     * @return The maximum branch length threshold
     */
    public float getMaxLength() {
        return this.maxLength;
    }

    /**
     * Sets the maximum branch length threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param maxLength The new maximum length threshold
     * @return The previous minimum length threshold
     */
    public final float setMaxLength(float maxLength) {
        if (maxLength <= 0) {
            throw new IllegalArgumentException("The maximum length's value is lower than or equal to zero");
        }
        float prevLength = this.maxLength;
        this.maxLength = maxLength;
        return prevLength;
    }

    /** Gets the minimum angle threshold
     *
     * @return The minimum angle threshold
     */
    public Vector3f getMinAngle() {
        return this.minAngle;
    }

    /**
     * Sets the minimum angle threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param minAngle The new minimum angle threshold
     * @return The minimum angle threshold
     */
    public final Vector3f setMinAngle(Vector3f minAngle) {
        Vector3f prevAngle = this.minAngle;
        this.minAngle = minAngle;
        return prevAngle;
    }

    /** Gets the maximum angle threshold
     *
     * @return The maximum angle threshold
     */
    public Vector3f getMaxDeviation() {
        return this.maxAngle;
    }

    /**
     * Sets the maximum angle threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param maxAngle The new maximum angle threshold
     * @return The previous maximum angle threshold
     */
    public final Vector3f setMaxAngle(Vector3f maxAngle) {
        Vector3f prevAngle = this.maxAngle;
        this.maxAngle = maxAngle;
        return prevAngle;
    }

    /** Gets the maximum branch count
     *
     * @return The maximum branch count
     */
    public int getMaxBranches() {
        return this.maxBranchCount;
    }

    /**
     * Sets the maximum branches for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param branchCount The new maximum branch count
     * @return The previous maximum branch count
     */
    public final int setMaxBranches(int branchCount) {
        if (branchCount <= 0) {
            throw new IllegalArgumentException("Maximum Branch Count has to be positive and non-zero");
        }
        int prevBranchCount = this.maxBranchCount;
        this.maxBranchCount = branchCount;
        return prevBranchCount;
    }

    /** Gets the minimum branch count
     *
     * @return The minimum branch count
     */
    public int getMinimumBranches() {
        return this.minBranchCount;
    }

    /**
     * Sets the minimum branches for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param branchCount The new minimum branch count
     * @return The previous minimum branch count
     */
    public final int setMinBranches(int branchCount) {
        if (branchCount <= 0) {
            throw new IllegalArgumentException("Minimum Branch Count has to be positive and non-zero");
        }
        int prevBranchCount = this.minBranchCount;
        this.minBranchCount = branchCount;
        return prevBranchCount;
    }

    private void generateFractal(ApelServerRenderer renderer, DrawContext drawContext, Vector3f start, int subdivs) {
        if (subdivs == this.maxBranchCount || (subdivs >= this.minBranchCount && rand.nextBoolean())) return;
        float currLength = MathHelper.nextFloat(rand, this.minLength, this.maxLength);
        float currAngleX = MathHelper.nextFloat(rand, this.minAngle.x, this.maxAngle.x);
        float currAngleY = MathHelper.nextFloat(rand, this.minAngle.y, this.maxAngle.y);
        float currAngleZ = MathHelper.nextFloat(rand, this.minAngle.z, this.maxAngle.z);
        Vector3f end = new Vector3f(start).add(currLength, currLength, currLength);
        renderer.drawLine(
                this.particleEffect,
                drawContext.getCurrentStep(),
                drawContext.getPosition(),
                new Vector3f(start).add(drawContext.getPosition()),
                end.add(drawContext.getPosition()),
                this.rotation,
                this.amount
        );
        subdivs += 1;
        generateFractal(renderer, drawContext, end.rotateZ(currAngleZ).rotateY(currAngleY).rotateX(currAngleX), subdivs);
    }


    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        generateFractal(renderer, drawContext, new Vector3f(0), 0);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleBranchGen> {
        protected float minLengthThreshold;
        protected float maxLengthThreshold;
        protected int maxBranches = 1;
        protected int minBranches = 1;
        protected Vector3f minAngle;
        protected Vector3f maxAngle;

        private Builder() {}

        /**
         * Set the number of maximum branches on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maximumBranches(int maxBranches) {
            if (maxBranches <= 0) {
                throw new IllegalArgumentException("The Argument maxBranches is negative or equal to zero");
            }
            this.maxBranches = maxBranches;
            return self();
        }

        /**
         * Set the number of minimum branches on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minimumBranches(int minBranches) {
            if (minBranches <= 0) {
                throw new IllegalArgumentException("The Argument minBranches is negative or equal to zero");
            }
            this.maxBranches = minBranches;
            return self();
        }

        /**
         * Set the minimum branch angle on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minimumAngle(Vector3f minAngle) {
            this.minAngle = minAngle;
            return self();
        }

        /**
         * Set the maximum branch angle on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maximumAngle(Vector3f maxAngle) {
            this.maxAngle = maxAngle;
            return self();
        }

        /**
         * Set the minimum branch length the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minLengthThreshold(float minLength) {
            this.minLengthThreshold = minLength;
            return self();
        }

        /**
         * Set the maximum branch length on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maxLengthThreshold(float maxLength) {
            this.maxLengthThreshold = maxLength;
            return self();
        }

        @Override
        public ParticleBranchGen build() {
            return new ParticleBranchGen(this);
        }
    }
}
