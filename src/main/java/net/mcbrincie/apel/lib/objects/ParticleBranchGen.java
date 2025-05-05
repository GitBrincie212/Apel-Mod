package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import net.mcbrincie.apel.lib.util.interceptor.Key;
import net.mcbrincie.apel.lib.util.interceptor.ObjectInterceptor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

import java.util.Optional;

/**
 * This particle object subclass represents a set of 3D branches. Constructing the branch generator requires specifying
 * a minimum and maximum length, a minimum and maximum angle a branch can take. As well as a minimum and a maximum number of
 * <b>TOTAL BRANCHES</b> and finally a minimum and a maximum number of the number of <b>BRANCHES PER DIVISION</b>.
 * The entire branch set may be rotated and offset relative to the draw origin by using the builder properties,
 * setters after construction, or during interceptor calls.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleBranchGen extends ParticleObject<ParticleBranchGen> {
    protected Vector3f minAngle;
    protected Vector3f maxAngle;
    protected float minLength;
    protected float maxLength;
    protected int maxTotalBranches;
    protected int minTotalBranches;
    protected int maxBranchesPerDivision;
    protected int minBranchesPerDivision;

    private static final Random rand = Random.create();

    private ObjectInterceptor<ParticleBranchGen> beforeBranchDraw = ObjectInterceptor.identity();
    private ObjectInterceptor<ParticleBranchGen> afterBranchDraw = ObjectInterceptor.identity();

    public static final Key<Boolean> SHOULD_RENDER = Key.booleanKey("shouldRender");
    public static final Key<Vector3f> START_ENDPOINT = Key.vector3fKey("startPoint");
    public static final Key<Vector3f> END_ENDPOINT = Key.vector3fKey("startPoint");

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleBranchGen(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setMaxBranchesPerDivision(builder.maxBranchesPerDivision);
        this.setMinBranchesPerDivsion(builder.minBranchesPerDivision);
        this.setMaxTotalBranches(builder.maxTotalBranches);
        this.setMinTotalBranches(builder.minTotalBranches);
        this.setMinAngle(builder.minAngle);
        this.setMaxAngle(builder.maxAngle);
        this.setMinLength(builder.minLengthThreshold);
        this.setMaxLength(builder.maxLengthThreshold);
        this.setAfterBranchDraw(builder.afterBranchDraw);
        this.setBeforeBranchDraw(builder.beforeBranchDraw);
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
        this.maxBranchesPerDivision = branchGen.maxBranchesPerDivision;
        this.minBranchesPerDivision = branchGen.minBranchesPerDivision;
        this.minTotalBranches = branchGen.minTotalBranches;
        this.maxTotalBranches = branchGen.maxTotalBranches;
        this.beforeBranchDraw = branchGen.beforeBranchDraw;
        this.afterBranchDraw = branchGen.afterBranchDraw;
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

    /** Gets the maximum branch count per division
     *
     * @return The maximum branch count per division
     */
    public int getMaxBranchesPerDivision() {
        return this.maxBranchesPerDivision;
    }

    /**
     * Sets the maximum branches for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param branchCount The new maximum branch count
     * @return The previous maximum branch count
     */
    public final int setMaxBranchesPerDivision(int branchCount) {
        if (branchCount <= 0) {
            throw new IllegalArgumentException("Maximum Branch Count Per Division has to be positive and non-zero");
        }
        int prevBranchCount = this.maxBranchesPerDivision;
        this.maxBranchesPerDivision = branchCount;
        return prevBranchCount;
    }

    /** Gets the minimum branch count per division
     *
     * @return The minimum branch count per division
     */
    public int getMinimumBranchesPerDivision() {
        return this.minBranchesPerDivision;
    }

    /**
     * Sets the minimum branches per division for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param branchCount The new minimum branch count per division
     * @return The previous minimum branch count per division
     */
    public final int setMinBranchesPerDivsion(int branchCount) {
        if (branchCount <= 0) {
            throw new IllegalArgumentException("Minimum Branch Count Per Division has to be positive and non-zero");
        }
        int prevBranchCount = this.minBranchesPerDivision;
        this.minBranchesPerDivision = branchCount;
        return prevBranchCount;
    }

    /** Gets the maximum branch count
     *
     * @return The maximum branch count
     */
    public int getMaxTotalBranches() {
        return this.maxTotalBranches;
    }

    /**
     * Sets the maximum branches for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param branchCount The new maximum branch count
     * @return The previous maximum branch count
     */
    public final int setMaxTotalBranches(int branchCount) {
        if (branchCount <= 0) {
            throw new IllegalArgumentException("Maximum Total Branch Count has to be positive and non-zero");
        }
        int prevBranchCount = this.maxTotalBranches;
        this.maxTotalBranches = branchCount;
        return prevBranchCount;
    }

    /** Gets the minimum branch count
     *
     * @return The minimum branch count
     */
    public int getMinTotalBranches() {
        return this.minTotalBranches;
    }

    /**
     * Sets the minimum branches for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param branchCount The new minimum branch count
     * @return The previous minimum branch count
     */
    public final int setMinTotalBranches(int branchCount) {
        if (branchCount <= 0) {
            throw new IllegalArgumentException("Minimum Total Branch Count has to be positive and non-zero");
        }
        int prevBranchCount = this.minTotalBranches;
        this.minTotalBranches = branchCount;
        return prevBranchCount;
    }

    /**
     * Set the interceptor to run before drawing each branch render.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, a boolean describing whether
     * it should be rendered and the endpoints of the branch.
     *
     * @param beforeBranchDrawIntercept The interceptor to execute before drawing each branch
     */
    public void setBeforeBranchDraw(ObjectInterceptor<ParticleBranchGen> beforeBranchDrawIntercept) {
        this.beforeBranchDraw = Optional.ofNullable(beforeBranchDrawIntercept).orElse(ObjectInterceptor.identity());
    }

    /**
     * Set the interceptor to run after drawing each branch render. The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation and
     * the last endpoint of the branch.
     *
     * @param afterBranchDrawIntercept The interceptor to execute before drawing each branch
     */
    public void setAfterBranchDraw(ObjectInterceptor<ParticleBranchGen> afterBranchDrawIntercept) {
        this.afterBranchDraw = Optional.ofNullable(afterBranchDrawIntercept).orElse(ObjectInterceptor.identity());
    }

    private void generateFractal(
            ApelServerRenderer renderer,
            DrawContext drawContext,
            Vector3f start,
            int subdivs
    ) {
        boolean surpassedMinimumWithOddsNotFavour = (subdivs >= this.minBranchesPerDivision && rand.nextBoolean());
        if (subdivs == this.maxBranchesPerDivision || surpassedMinimumWithOddsNotFavour) return;
        float currLength = MathHelper.nextFloat(rand, this.minLength, this.maxLength);
        float currAngleX = MathHelper.nextFloat(rand, this.minAngle.x, this.maxAngle.x);
        float currAngleY = MathHelper.nextFloat(rand, this.minAngle.y, this.maxAngle.y);
        float currAngleZ = MathHelper.nextFloat(rand, this.minAngle.z, this.maxAngle.z);
        Vector3f end = new Vector3f(start).add(currLength, currLength, currLength);
        /*
        drawContext.addMetadata(SHOULD_RENDER, true);
        drawContext.addMetadata(START_ENDPOINT, start);
        drawContext.addMetadata(END_ENDPOINT, end);
        this.beforeBranchDraw.apply(drawContext, this);
        boolean shouldRender = drawContext.getMetadata(SHOULD_RENDER, true);
        if (!shouldRender) return;
        start = drawContext.getMetadata(START_ENDPOINT, start);
        end = drawContext.getMetadata(END_ENDPOINT, end);
         */
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
        /*
        drawContext.addMetadata(END_ENDPOINT, end);
         */
        this.afterBranchDraw.apply(drawContext, this);
        generateFractal(
                renderer,
                drawContext,
                end.rotateZ(currAngleZ).rotateY(currAngleY).rotateX(currAngleX),
                subdivs
        );
    }


    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        int totalCount = MathHelper.nextInt(rand, this.minTotalBranches, this.maxTotalBranches);
        this.beforeDraw.apply(drawContext, this);
        for (int i = 0; i < totalCount; i++) {
            generateFractal(renderer, drawContext, new Vector3f(0), 0);
        }
        this.afterDraw.apply(drawContext, this);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleBranchGen> {
        protected float minLengthThreshold;
        protected float maxLengthThreshold;
        protected int maxTotalBranches = 1;
        protected int minTotalBranches = 1;
        protected int maxBranchesPerDivision = 1;
        protected int minBranchesPerDivision = 1;
        protected Vector3f minAngle;
        protected Vector3f maxAngle;
        protected ObjectInterceptor<ParticleBranchGen> afterBranchDraw;
        protected ObjectInterceptor<ParticleBranchGen> beforeBranchDraw;

        private Builder() {}

        /**
         * Set the number of maximum branches per division on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maximumBranchesPerDivision(int maxBranches) {
            if (maxBranches <= 0) {
                throw new IllegalArgumentException("The Argument maxBranches is negative or equal to zero");
            }
            this.maxBranchesPerDivision = maxBranches;
            return self();
        }

        /**
         * Set the number of minimum branches per division on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minimumBranchesPerDivision(int minBranches) {
            if (minBranches <= 0) {
                throw new IllegalArgumentException("The Argument minBranches is negative or equal to zero");
            }
            this.minBranchesPerDivision = minBranches;
            return self();
        }

        /**
         * Set the number of maximum total branches on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maximumTotalBranches(int maxTotalBranches) {
            if (maxTotalBranches <= 0) {
                throw new IllegalArgumentException("The Argument maxTotalBranches is negative or equal to zero");
            }
            this.maxTotalBranches = maxTotalBranches;
            return self();
        }

        /**
         * Set the number of minimum total branches on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minimumTotalBranches(int minTotalBranches) {
            if (minTotalBranches <= 0) {
                throw new IllegalArgumentException("The Argument minTotalBranches is negative or equal to zero");
            }
            this.minTotalBranches = minTotalBranches;
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

        /**
         * Set a constant branch length on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B constantLengthThreshold(float length) {
            this.maxLengthThreshold = length;
            this.minLengthThreshold = length;
            return self();
        }

        /**
         * Set a constant branch angle on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B constantAngle(Vector3f angle) {
            this.maxAngle = angle;
            this.minAngle = angle;
            return self();
        }

        /**
         * Set a constant number of branches per division on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B constantBranchesPerDivision(int branchCount) {
            this.maxBranchesPerDivision = branchCount;
            this.minBranchesPerDivision = branchCount;
            return self();
        }

        /**
         * Set a constant number of total branches on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B constantTotalBranches(int branchCount) {
            this.maxTotalBranches = branchCount;
            this.minTotalBranches = branchCount;
            return self();
        }

        /**
         * Sets the interceptor to run after a branch is drawn. This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleBranchGen#setAfterBranchDraw(ObjectInterceptor)
         */
        public B afterBranchDraw(ObjectInterceptor<ParticleBranchGen> afterBranchDraw) {
            this.afterBranchDraw = afterBranchDraw;
            return self();
        }

        /**
         * Sets the interceptor to run before a branch is drawn. This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleBranchGen#setBeforeBranchDraw(ObjectInterceptor)
         */
        public B beforeBranchDraw(ObjectInterceptor<ParticleBranchGen> beforeBranchDraw) {
            this.beforeBranchDraw = beforeBranchDraw;
            return self();
        }


        @Override
        public ParticleBranchGen build() {
            return new ParticleBranchGen(this);
        }
    }
}
