package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.ComputedEasings;
import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
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
    protected EasingCurve<Vector3f> minAngle;
    protected EasingCurve<Vector3f> maxAngle;
    protected EasingCurve<Float> minLength;
    protected EasingCurve<Float> maxLength;
    protected EasingCurve<Integer> maxTotalBranches;
    protected EasingCurve<Integer> minTotalBranches;
    protected EasingCurve<Integer> maxBranchesPerDivision;
    protected EasingCurve<Integer> minBranchesPerDivision;

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
        this.minAngle = branchGen.minAngle;
        this.maxAngle = branchGen.maxAngle;
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
    public EasingCurve<Float> getMinLength() {
        return this.minLength;
    }

    /**
     * Sets the minimum branch length threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the minLength
     *
     * @param minLength The new minimum length threshold
     * @return The minimum length threshold
     */
    public final EasingCurve<Float> setMinLength(float minLength) {
        return this.setMinLength(new ConstantEasingCurve<>(minLength));
    }

    /**
     * Sets the minimum branch length threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the minLength
     *
     * @param minLength The new minimum length threshold
     * @return The minimum length threshold
     */
    public final EasingCurve<Float> setMinLength(EasingCurve<Float> minLength) {
        EasingCurve<Float> prevLength = this.minLength;
        this.minLength = minLength;
        return prevLength;
    }

    /** Gets the maximum branch length threshold
     *
     * @return The maximum branch length threshold
     */
    public EasingCurve<Float> getMaxLength() {
        return this.maxLength;
    }

    /**
     * Sets the maximum branch length threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the maxLength
     *
     * @param maxLength The new maximum length threshold
     * @return The previous minimum length threshold
     */
    public final EasingCurve<Float> setMaxLength(float maxLength) {
        return this.setMaxLength(new ConstantEasingCurve<>(maxLength));
    }

    /**
     * Sets the maximum branch length threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the maxLength
     *
     * @param maxLength The new maximum length threshold
     * @return The previous minimum length threshold
     */
    public final EasingCurve<Float> setMaxLength(EasingCurve<Float> maxLength) {
        EasingCurve<Float> prevLength = this.maxLength;
        this.maxLength = maxLength;
        return prevLength;
    }

    /** Gets the minimum angle threshold
     *
     * @return The minimum angle threshold
     */
    public EasingCurve<Vector3f> getMinAngle() {
        return this.minAngle;
    }

    /**
     * Sets the minimum angle threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the minAngle
     *
     * @param minAngle The new minimum angle threshold
     * @return The minimum angle threshold
     */
    public final EasingCurve<Vector3f> setMinAngle(Vector3f minAngle) {
        return this.setMinAngle(new ConstantEasingCurve<>(minAngle));
    }

    /**
     * Sets the minimum angle threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the minAngle
     *
     * @param minAngle The new minimum angle threshold
     * @return The minimum angle threshold
     */
    public final EasingCurve<Vector3f> setMinAngle(EasingCurve<Vector3f> minAngle) {
        EasingCurve<Vector3f> prevAngle = this.minAngle;
        this.minAngle = minAngle;
        return prevAngle;
    }

    /** Gets the maximum angle threshold
     *
     * @return The maximum angle threshold
     */
    public EasingCurve<Vector3f> getMaxDeviation() {
        return this.maxAngle;
    }

    /**
     * Sets the maximum angle threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the maxAngle
     *
     * @param maxAngle The new maximum angle threshold
     * @return The previous maximum angle threshold
     */
    public final EasingCurve<Vector3f> setMaxAngle(Vector3f maxAngle) {
        return this.setMaxAngle(new ConstantEasingCurve<>(maxAngle));
    }

    /**
     * Sets the maximum angle threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the maxAngle
     *
     * @param maxAngle The new maximum angle threshold
     * @return The previous maximum angle threshold
     */
    public final EasingCurve<Vector3f> setMaxAngle(EasingCurve<Vector3f> maxAngle) {
        EasingCurve<Vector3f> prevAngle = this.maxAngle;
        this.maxAngle = maxAngle;
        return prevAngle;
    }

    /** Gets the maximum branch count per division
     *
     * @return The maximum branch count per division
     */
    public EasingCurve<Integer> getMaxBranchesPerDivision() {
        return this.maxBranchesPerDivision;
    }

    /**
     * Sets the maximum branches for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the branch per division count
     *
     * @param branchCount The new maximum branch count
     * @return The previous maximum branch count
     */
    public final EasingCurve<Integer> setMaxBranchesPerDivision(int branchCount) {
        return this.setMaxBranchesPerDivision(new ConstantEasingCurve<>(branchCount));
    }

    /**
     * Sets the maximum branches for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the maximum branch per division count
     *
     * @param branchCount The new maximum branch count
     * @return The previous maximum branch count
     */
    public final EasingCurve<Integer> setMaxBranchesPerDivision(EasingCurve<Integer> branchCount) {
        EasingCurve<Integer> prevBranchCount = this.maxBranchesPerDivision;
        this.maxBranchesPerDivision = branchCount;
        return prevBranchCount;
    }

    /** Gets the minimum branch count per division
     *
     * @return The minimum branch count per division
     */
    public EasingCurve<Integer> getMinimumBranchesPerDivision() {
        return this.minBranchesPerDivision;
    }

    /**
     * Sets the minimum branches per division for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the minimum branch per division count
     *
     * @param branchCount The new minimum branch count per division
     * @return The previous minimum branch count per division
     */
    public final EasingCurve<Integer> setMinBranchesPerDivsion(int branchCount) {
        return setMinBranchesPerDivsion(new ConstantEasingCurve<>(branchCount));
    }

    /**
     * Sets the minimum branches per division for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the minimum branch per division count
     *
     * @param branchCount The new minimum branch count per division
     * @return The previous minimum branch count per division
     */
    public final EasingCurve<Integer> setMinBranchesPerDivsion(EasingCurve<Integer> branchCount) {
        EasingCurve<Integer> prevBranchCount = this.minBranchesPerDivision;
        this.minBranchesPerDivision = branchCount;
        return prevBranchCount;
    }

    /** Gets the maximum branch count
     *
     * @return The maximum branch count
     */
    public EasingCurve<Integer> getMaxTotalBranches() {
        return this.maxTotalBranches;
    }

    /**
     * Sets the maximum branches for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant for the maximum total branches count
     *
     * @param branchCount The new maximum branch count
     * @return The previous maximum branch count
     */
    public final EasingCurve<Integer> setMaxTotalBranches(int branchCount) {
        return this.setMaxTotalBranches(new ConstantEasingCurve<>(branchCount));
    }

    /**
     * Sets the maximum branches for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the maximum total branches count
     *
     * @param branchCount The new maximum branch count
     * @return The previous maximum branch count
     */
    public final EasingCurve<Integer> setMaxTotalBranches(EasingCurve<Integer> branchCount) {
        EasingCurve<Integer> prevBranchCount = this.maxTotalBranches;
        this.maxTotalBranches = branchCount;
        return prevBranchCount;
    }

    /** Gets the minimum branch count
     *
     * @return The minimum branch count
     */
    public EasingCurve<Integer> getMinTotalBranches() {
        return this.minTotalBranches;
    }

    /**
     * Sets the minimum branches for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the minimum total branches count
     *
     * @param branchCount The new minimum branch count
     * @return The previous minimum branch count
     */
    public final EasingCurve<Integer> setMinTotalBranches(int branchCount) {
        return this.setMinTotalBranches(new ConstantEasingCurve<>(branchCount));
    }

    /**
     * Sets the minimum branches for the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an easing curve value for the minimum total branches count
     *
     * @param branchCount The new minimum branch count
     * @return The previous minimum branch count
     */
    public final EasingCurve<Integer> setMinTotalBranches(EasingCurve<Integer> branchCount) {
        EasingCurve<Integer> prevBranchCount = this.minTotalBranches;
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

    @Override
    protected ComputedEasings computeAdditionalEasings(ComputedEasingPO container) {
        return container.addComputedField("minBranchesPerDivision", this.minBranchesPerDivision)
                .addComputedField("maxBranchesPerDivision", this.maxBranchesPerDivision)
                .addComputedField("minLength", this.minLength)
                .addComputedField("maxLength", this.maxLength)
                .addComputedField("minAngle", this.minAngle)
                .addComputedField("maxAngle", this.maxAngle)
                .addComputedField("minTotalBranches", this.minTotalBranches)
                .addComputedField("maxTotalBranches", this.maxTotalBranches);
    }

    private void generateFractal(
            ApelServerRenderer renderer,
            DrawContext drawContext,
            Vector3f start,
            int subdivs
    ) {
        ComputedEasingPO computedEasings = drawContext.getComputedEasings();
        int currMinBranchesPerDivision = (int) computedEasings.getComputedField("minBranchesPerDivision");
        int currMaxBranchesPerDivision = (int) computedEasings.getComputedField("maxBranchesPerDivision");
        if (currMinBranchesPerDivision <= 0) {
            throw new IllegalArgumentException("Minimum Branch Count Per Division has to be positive and non-zero");
        } else if (currMaxBranchesPerDivision <= 0) {
            throw new IllegalArgumentException("Maximum Branch Count Per Division has to be positive and non-zero");
        }
        float currMinLength = (float) computedEasings.getComputedField("minLength");
        float currMaxLength = (float) computedEasings.getComputedField("maxLength");
        if (currMinLength <= 0) {
            throw new IllegalArgumentException("The minimum length's value is lower than or equal to zero");
        } else if (currMaxLength <= 0) {
            throw new IllegalArgumentException("The maximum length's value is lower than or equal to zero");
        }
        Vector3f currMinAngle = (Vector3f) computedEasings.getComputedField("minAngle");
        Vector3f currMaxAngle = (Vector3f) computedEasings.getComputedField("maxAngle");
        boolean surpassedMinimumWithOddsNotFavour = (subdivs >= currMinBranchesPerDivision && rand.nextBoolean());
        if (subdivs == currMaxBranchesPerDivision || surpassedMinimumWithOddsNotFavour) return;
        float currLength = MathHelper.nextFloat(rand, currMinLength, currMaxLength);
        float currAngleX = MathHelper.nextFloat(rand, currMinAngle.x, currMaxAngle.x);
        float currAngleY = MathHelper.nextFloat(rand, currMinAngle.y, currMaxAngle.y);
        float currAngleZ = MathHelper.nextFloat(rand, currMinAngle.z, currMaxAngle.z);
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
                computedEasings.computedRotation,
                computedEasings.computedAmount
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
        ComputedEasingPO computedEasings = drawContext.getComputedEasings();
        int currMinTotalBranches = (int) computedEasings.getComputedField("minTotalBranches");
        int currMaxTotalBranches = (int) computedEasings.getComputedField("maxTotalBranches");
        if (currMinTotalBranches <= 0) {
            throw new IllegalArgumentException("Minimum Total Branch Count has to be positive and non-zero");
        } else if (currMaxTotalBranches <= 0) {
            throw new IllegalArgumentException("Maximum Total Branch Count has to be positive and non-zero");
        }
        int totalCount = MathHelper.nextInt(rand, currMinTotalBranches, currMaxTotalBranches);
        this.beforeDraw.apply(drawContext, this);
        for (int i = 0; i < totalCount; i++) {
            generateFractal(renderer, drawContext, new Vector3f(0), 0);
        }
        this.afterDraw.apply(drawContext, this);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleBranchGen> {
        protected EasingCurve<Float> minLengthThreshold;
        protected EasingCurve<Float> maxLengthThreshold;
        protected EasingCurve<Integer>  maxTotalBranches = new ConstantEasingCurve<>(1);
        protected EasingCurve<Integer> minTotalBranches = new ConstantEasingCurve<>(1);
        protected EasingCurve<Integer> maxBranchesPerDivision = new ConstantEasingCurve<>(1);
        protected EasingCurve<Integer> minBranchesPerDivision = new ConstantEasingCurve<>(1);
        protected EasingCurve<Vector3f> minAngle;
        protected EasingCurve<Vector3f> maxAngle;
        protected ObjectInterceptor<ParticleBranchGen> afterBranchDraw;
        protected ObjectInterceptor<ParticleBranchGen> beforeBranchDraw;

        private Builder() {}

        /**
         * Set the number of maximum branches per division on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maximumBranchesPerDivision(EasingCurve<Integer> maxBranches) {
            this.maxBranchesPerDivision = maxBranches;
            return self();
        }

        /**
         * Set the number of minimum branches per division on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minimumBranchesPerDivision(EasingCurve<Integer> minBranches) {
            this.minBranchesPerDivision = minBranches;
            return self();
        }

        /**
         * Set the number of maximum total branches on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maximumTotalBranches(EasingCurve<Integer> maxTotalBranches) {
            this.maxTotalBranches = maxTotalBranches;
            return self();
        }

        /**
         * Set the number of minimum total branches on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minimumTotalBranches(EasingCurve<Integer> minTotalBranches) {
            this.minTotalBranches = minTotalBranches;
            return self();
        }

        /**
         * Set the minimum branch angle on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minimumAngle(EasingCurve<Vector3f> minAngle) {
            this.minAngle = minAngle;
            return self();
        }

        /**
         * Set the maximum branch angle on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maximumAngle(EasingCurve<Vector3f> maxAngle) {
            this.maxAngle = maxAngle;
            return self();
        }

        /**
         * Set the minimum branch length the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minLengthThreshold(EasingCurve<Float> minLength) {
            this.minLengthThreshold = minLength;
            return self();
        }

        /**
         * Set the maximum branch length on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maxLengthThreshold(EasingCurve<Float> maxLength) {
            this.maxLengthThreshold = maxLength;
            return self();
        }

        /**
         * Set a constant branch length on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B constantLengthThreshold(EasingCurve<Float> length) {
            this.maxLengthThreshold = length;
            this.minLengthThreshold = length;
            return self();
        }

        /**
         * Set a constant branch angle on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B constantAngle(EasingCurve<Vector3f> angle) {
            this.maxAngle = angle;
            this.minAngle = angle;
            return self();
        }

        /**
         * Set a constant number of branches per division on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B constantBranchesPerDivision(EasingCurve<Integer> branchCount) {
            this.maxBranchesPerDivision = branchCount;
            this.minBranchesPerDivision = branchCount;
            return self();
        }

        /**
         * Set a constant number of total branches on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B constantTotalBranches(EasingCurve<Integer> branchCount) {
            this.maxTotalBranches = branchCount;
            this.minTotalBranches = branchCount;
            return self();
        }

        /**
         * Set the number of maximum branches per division on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maximumBranchesPerDivision(int maxBranches) {
            this.maxBranchesPerDivision = new ConstantEasingCurve<>(maxBranches);
            return self();
        }

        /**
         * Set the number of minimum branches per division on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minimumBranchesPerDivision(int minBranches) {
            this.minBranchesPerDivision = new ConstantEasingCurve<>(minBranches);
            return self();
        }

        /**
         * Set the number of maximum total branches on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maximumTotalBranches(int maxTotalBranches) {
            this.maxTotalBranches = new ConstantEasingCurve<>(maxTotalBranches);
            return self();
        }

        /**
         * Set the number of minimum total branches on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minimumTotalBranches(int minTotalBranches) {
            this.minTotalBranches = new ConstantEasingCurve<>(minTotalBranches);
            return self();
        }

        /**
         * Set the minimum branch angle on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minimumAngle(Vector3f minAngle) {
            this.minAngle = new ConstantEasingCurve<>(minAngle);
            return self();
        }

        /**
         * Set the maximum branch angle on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maximumAngle(Vector3f maxAngle) {
            this.maxAngle = new ConstantEasingCurve<>(maxAngle);
            return self();
        }

        /**
         * Set the minimum branch length the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minLengthThreshold(float minLength) {
            this.minLengthThreshold = new ConstantEasingCurve<>(minLength);
            return self();
        }

        /**
         * Set the maximum branch length on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maxLengthThreshold(float maxLength) {
            this.maxLengthThreshold = new ConstantEasingCurve<>(maxLength);
            return self();
        }

        /**
         * Set a constant branch length on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B constantLengthThreshold(float length) {
            this.maxLengthThreshold = new ConstantEasingCurve<>(length);
            this.minLengthThreshold = new ConstantEasingCurve<>(length);
            return self();
        }

        /**
         * Set a constant branch angle on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B constantAngle(Vector3f angle) {
            this.maxAngle = new ConstantEasingCurve<>(angle);
            this.minAngle = new ConstantEasingCurve<>(angle);
            return self();
        }

        /**
         * Set a constant number of branches per division on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B constantBranchesPerDivision(int branchCount) {
            this.maxBranchesPerDivision = new ConstantEasingCurve<>(branchCount);
            this.minBranchesPerDivision = new ConstantEasingCurve<>(branchCount);
            return self();
        }

        /**
         * Set a constant number of total branches on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B constantTotalBranches(int branchCount) {
            this.maxTotalBranches = new ConstantEasingCurve<>(branchCount);
            this.minTotalBranches = new ConstantEasingCurve<>(branchCount);
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
