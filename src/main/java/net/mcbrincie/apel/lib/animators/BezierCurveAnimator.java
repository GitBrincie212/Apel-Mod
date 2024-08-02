package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.AnimationTrimming;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.math.bezier.BezierCurve;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** The Bézier curve animator which is used for curved paths, it accepts two or multiple different bézier curves,
 * the curves are called endpoints unlike the linear animator, the Bézier curve path animator the curves one by one.
 * This means that you can have a curve on one ending position and another that starts in a completely different one.
 * This animator is more advanced than the linear animator, and some math knowledge is required on the internal workings
 * of a bézier curve nonetheless, it is very capable of creating beautiful complex curved paths
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class BezierCurveAnimator extends PathAnimatorBase {
    protected List<BezierCurve> bezierCurves;
    protected List<Integer> stepsForCurves;
    protected AnimationTrimming<Float> trimming;

    protected DrawInterceptor<BezierCurveAnimator, OnRenderStep> duringRenderingSteps = DrawInterceptor.identity();

    public enum OnRenderStep {SHOULD_DRAW_STEP, RENDERING_POSITION}

    public static <B extends Builder<B>> Builder<B> builder() {
        return new Builder<>();
    }

    private <B extends Builder<B>> BezierCurveAnimator(Builder<B> builder) {
        super(builder);
        this.bezierCurves = builder.bezierCurves;
        this.stepsForCurves = builder.stepsForCurves;
        this.trimming = builder.trimming;
    }

    /**
     * Constructor for the bézier animation. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * bézier animator instance with all of its parameters regardless
     * of their visibility (this means protected & private params are copied)
     *
     * @param animator The animator to copy from
    */
    public BezierCurveAnimator(BezierCurveAnimator animator) {
        super(animator);
        this.bezierCurves = animator.bezierCurves;
        this.stepsForCurves = animator.stepsForCurves;
        this.trimming = animator.trimming;
        this.duringRenderingSteps = animator.duringRenderingSteps;
    }

    /** Sets the animation trimming which accepts a start trim or
     * an ending trim. The trim parts have to be integer values
     *
     * @return The animation trimming that is used
     */
    public AnimationTrimming<Float> setTrimming(AnimationTrimming<Float> trimming) {
        float start = trimming.getStart();
        float end = trimming.getEnd();
        if (start < 0.0f || end > 1.0f || start >= end) {
            throw new IllegalArgumentException("Animation trimming must be within [0.0, 1.0]");
        }
        AnimationTrimming<Float> prevTrimming = this.trimming;
        this.trimming = trimming;
        return prevTrimming;
    }

    /** Gets the animation trimming that is used
     *
     * @return The animation trimming that is used
     */
    public AnimationTrimming<Float> getTrimming() {
        return this.trimming;
    }

    @Override
    public int convertIntervalToSteps() {
        return this.stepsForCurves.stream().mapToInt(i -> i).sum();
    }

    @Override
    public void beginAnimation(ApelServerRenderer renderer) throws SeqDuplicateException, SeqMissingException {
        float tStart = this.trimming.getStart();
        float tEnd = this.trimming.getEnd();
        this.allocateToScheduler();

        int step = -1;
        for (int index = 0; index < this.bezierCurves.size(); index++) {
            BezierCurve bezierCurve = this.bezierCurves.get(index);
            int curveSteps = this.stepsForCurves.get(index);

            // Interval MUST be the reciprocal of steps so t is in [0, 1].
            float tDelta = 1.0f / curveSteps;
            for (float t = 0; t < 1.0f; t += tDelta) {
                step++;
                if (t < tStart) {
                    continue;
                }
                // Handle trimming, but only if the end was set to a non-default value
                if (t >= tEnd && tEnd != -1) {
                    break;
                }
                Vector3f pos = bezierCurve.compute(t);
                InterceptData<OnRenderStep> interceptData = this.doBeforeStep(renderer.getServerWorld(), pos, step);
                if (!interceptData.getMetadata(OnRenderStep.SHOULD_DRAW_STEP, true)) {
                    continue;
                }
                pos = interceptData.getMetadata(OnRenderStep.RENDERING_POSITION, pos);
                this.handleDrawingStep(renderer, step, pos);
            }
        }
    }

    /** Set the interceptor to run before the drawing of each individual rendering step. The interceptor will be provided
     * with references to the {@link ServerWorld}, the current step number. As far as it goes for metadata,
     * there will be a boolean value that dictates if it should draw on this step and the rendering position of the
     * point that lives in the Bézier curve
     *
     * @param duringRenderingSteps the new interceptor to execute before drawing the individual steps
     */
    public void setDuringRenderingSteps(DrawInterceptor<BezierCurveAnimator, OnRenderStep> duringRenderingSteps) {
        this.duringRenderingSteps = Optional.ofNullable(duringRenderingSteps).orElse(DrawInterceptor.identity());
    }

    protected InterceptData<OnRenderStep> doBeforeStep(
            ServerWorld world, Vector3f position, int currStep
    ) {
        InterceptData<OnRenderStep> interceptData = new InterceptData<>(
                world, null, currStep, OnRenderStep.class
        );
        interceptData.addMetadata(OnRenderStep.RENDERING_POSITION, position);
        interceptData.addMetadata(OnRenderStep.SHOULD_DRAW_STEP, true);
        this.duringRenderingSteps.apply(interceptData, this);
        return interceptData;
    }

    /** This is the Bézier Curve path-animator builder used for setting up a new Bézier Curve path-animator instance.
     * It is designed to be more friendly of how you arrange the parameters. Call {@code .builder()} to initiate
     * the builder, once you supplied the parameters then you can call {@code .build()} to create the instance
     *
     * @param <B> The builder type itself
    */
    public static class Builder<B extends Builder<B>> extends PathAnimatorBase.Builder<B, BezierCurveAnimator> {
        protected List<BezierCurve> bezierCurves = new ArrayList<>();
        protected List<Integer> stepsForCurves = new ArrayList<>();
        protected int stepsForAllCurves = 0;
        protected List<Float> intervalsForCurves = new ArrayList<>();
        protected float intervalForAllCurves = 0.0f;
        protected AnimationTrimming<Float> trimming = new AnimationTrimming<>(0.0f, 1.0f);

        private Builder() {}

        /** Adds a new Bézier curve to the Bézier curve collection
         *
         * @param bezierCurve The Bézier curve to add
         * @return The builder instance
         */
        public B bezierCurve(BezierCurve bezierCurve) {
            this.bezierCurves.add(bezierCurve);
            return self();
        }

        /** Adds all Bézier curve from the list provided to the Bézier curve collection
         *
         * @param bezierCurves The Bézier curves to add
         * @return The builder instance
        */
        public B bezierCurves(List<BezierCurve> bezierCurves) {
            this.bezierCurves.addAll(bezierCurves);
            return self();
        }

        /** Sets the rendering step for the specific Bézier curve
         *
         * @param stepsForCurve The rendering steps for the Bézier curve
         * @return The builder instance
        */
        public B stepsForCurve(int stepsForCurve) {
            this.stepsForCurves.add(stepsForCurve);
            return self();
        }

        /** Sets the rendering step for all the Bézier curves
         *
         * @param steps The rendering steps
         * @return The builder instance
        */
        public B stepsForAllCurves(int steps) {
            this.stepsForAllCurves = steps;
            return self();
        }

        /** Sets the rendering step for specific Bézier curves
         *
         * @param stepsForCurves The rendering steps for the specific Bézier curves
         * @return The builder instance
        */
        public B stepsForCurves(List<Integer> stepsForCurves) {
            this.stepsForCurves.addAll(stepsForCurves);
            return self();
        }

        /** Sets the rendering interval for the specific Bézier curve
         *
         * @param intervalForCurve The rendering interval for the Bézier curve
         * @return The builder instance
         */
        public B intervalForCurve(float intervalForCurve) {
            this.intervalsForCurves.add(intervalForCurve);
            return self();
        }

        /** Sets the rendering interval for all the Bézier curves
         *
         * @param interval The rendering interval
         * @return The builder instance
         */
        public B intervalForAllCurves(int interval) {
            this.intervalForAllCurves = interval;
            return self();
        }

        /** Sets the rendering intervals for specific Bézier curves
         *
         * @param intervalsForCurves The rendering intervals for the specific Bézier curves
         * @return The builder instance
        */
        public B intervalsForCurves(List<Float> intervalsForCurves) {
            this.intervalsForCurves.addAll(intervalsForCurves);
            return self();
        }

        /** The animation trimming for the Bézier curve path-animator
         *
         * @param trimming The animation trimming
         * @return The builder instance
         */
        public B trimming(AnimationTrimming<Float> trimming) {
            this.trimming = trimming;
            return self();
        }

        @Override
        public BezierCurveAnimator build() {
            if (this.bezierCurves.isEmpty()) {
                throw new IllegalStateException("Must provide at least one curve");
            }
            // If an "all curve" value is provided, it takes priority: interval first, then steps
            if (this.intervalForAllCurves != 0.0f) {
                this.intervalsForCurves.clear();
                for (int i = 0; i < this.bezierCurves.size() - 1; i++) {
                    this.intervalsForCurves.add(this.intervalForAllCurves);
                }
            } else if (this.stepsForAllCurves != 0) {
                this.stepsForCurves.clear();
                for (int i = 0; i < this.bezierCurves.size() - 1; i++) {
                    this.stepsForCurves.add(this.stepsForAllCurves);
                }
            }
            // At this point, either an "all curves" value is provided or individual values for either steps or
            // intervals must be provided.  At this point, mixing intervals and steps is not allowed.
            if ((this.stepsForCurves.size() + 1 != this.bezierCurves.size()) && (this.intervalsForCurves.size() + 1 != this.bezierCurves.size())) {
                throw new IllegalStateException("Must provide steps or intervals for every curve");
            }
            for (int i = 0; i < this.bezierCurves.size() - 1; i++) {
                // Pad the lists so the conversion from interval to steps is straightforward
                if (this.stepsForCurves.size() == i) {
                    this.stepsForCurves.add(0);
                }
                if (this.intervalsForCurves.size() == i) {
                    this.intervalsForCurves.add(0.0f);
                }
                // Verify that at least one of steps/interval is provided
                if (this.stepsForCurves.get(i) == 0 && this.intervalsForCurves.get(i) == 0.0f) {
                    throw new IllegalStateException("Either steps or interval must be positive for curve " + i);
                }
                // Interval takes priority, if set.  If not set, then steps must already be set.
                if (this.intervalsForCurves.get(i) != 0.0f) {
                    this.stepsForCurves.set(i, this.getCurveSteps(i));
                }
            }
            // TODO: Convert intervals to steps
            return new BezierCurveAnimator(this);
        }

        private int getCurveSteps(int index) {
            // TODO: choose this value better, perhaps based on distance between start/end or start/controls/end?
            float distance = this.bezierCurves.get(index).length(100);
            return (int) Math.ceil(distance / this.intervalsForCurves.get(index));
        }
    }
}
