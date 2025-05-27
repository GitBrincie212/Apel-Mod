package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.AnimationTrimming;
import net.mcbrincie.apel.lib.util.interceptor.context.AnimationContext;
import net.mcbrincie.apel.lib.util.interceptor.context.Key;
import net.mcbrincie.apel.lib.util.math.bezier.BezierCurve;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/** The Bézier curve animator which is used for curved paths, it accepts two or multiple different bézier curves,
 * the curves are called endpoints unlike the linear animator, the Bézier curve path animator the curves one by one.
 * This means that you can have a curve on one ending position and another that starts in a completely different one.
 * This animator is more advanced than the linear animator, and some math knowledge is required on the internal workings
 * of a bézier curve nonetheless, it is very capable of creating beautiful complex curved paths
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class BezierCurveAnimator extends PathAnimatorBase<BezierCurveAnimator> {
    protected List<BezierCurve> bezierCurves;
    protected List<Integer> stepsForCurves;
    protected AnimationTrimming<Float> trimming;

    public static final Key<Integer> CURRENT_CURVE_INDEX = Key.integerKey("currentCurveIndex");

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
     * Copy constructor for the bézier animation. This constructor is
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
                Vector3f renderPosition = bezierCurve.compute(t);
                AnimationContext animationContext = new AnimationContext(renderer.getServerWorld(), renderPosition, step);
                this.beforeRender.apply(animationContext, this);
                Vector3f actualPosition = animationContext.getPosition();
                this.handleDrawingStep(renderer, step, actualPosition);
            }
        }
    }

    /** This is the Bézier Curve path-animator builder used for setting up a new Bézier Curve path-animator instance.
     * It is designed to be more friendly of how you arrange the parameters. Call {@code .builder()} to initiate
     * the builder, once you supplied the parameters then you can call {@code .build()} to create the instance
     * <p>
     * The BezierCurveAnimator supports multiple, linked curves.  The travel speed may be configured by providing the
     * number of steps into which to divide the curve, or an interval that also divides the curve into steps.  These
     * interval and step values may be provided uniquely for every curve, set in groups of curves, or uniform across all
     * curves.  The priority is as follows:
     * <ol>
     *     <li>{@link #intervalForAllCurves(float)}</li>
     *     <li>{@link #stepsForAllCurves(int)}</li>
     *     <li>{@link #intervalForCurve(float)} and {@link #intervalsForCurves(List)}</li>
     *     <li>{@link #stepsForCurve(int)} and {@link #stepsForCurves(List)}</li>
     * </ol>
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

        /** Adds a new Bézier curve to the path this animator takes. This method is cumulative; repeated calls will
         * append to the list of curves.  There must be at least one curve.
         *
         * @param bezierCurve The Bézier curve to add
         * @return The builder instance
         */
        public B bezierCurve(BezierCurve bezierCurve) {
            this.bezierCurves.add(bezierCurve);
            return self();
        }

        /** Adds all Bézier curve from the list provided to the path this animator takes. This method is cumulative;
         * repeated calls will append to the list of curves.  There must be at least one curve.
         *
         * @param bezierCurves The Bézier curves to add
         * @return The builder instance
        */
        public B bezierCurves(List<BezierCurve> bezierCurves) {
            this.bezierCurves.addAll(bezierCurves);
            return self();
        }

        /**
         * Set the number of steps to use when rendering the object along the curve.  This method is cumulative, each
         * successive call configures a curve.  Any curves not configured will default to 0.  Curves with steps
         * set to 0 will use the curve's interval value.
         *
         * @param stepsForCurve The steps for the next curve
         * @return The builder instance
         */
        public B stepsForCurve(int stepsForCurve) {
            this.stepsForCurves.add(stepsForCurve);
            return self();
        }

        /**
         * Set the number of steps to use when rendering the object along multiple curves.  This method is cumulative,
         * each successive call configures one or more curves.  Any curves not configured will default to 0.  Curves
         * with steps set to 0 will use the curve's interval value.
         *
         * @param stepsForCurves The steps for the next N curves
         * @return The builder instance
         */
        public B stepsForCurves(List<Integer> stepsForCurves) {
            this.stepsForCurves.addAll(stepsForCurves);
            return self();
        }

        /**
         * Set the number of steps to use on every curve.  This method is not cumulative; repeated calls will overwrite
         * the value.
         * <p>
         * Note: This will take priority over individual curves set in {@link #stepsForCurve(int)} or
         * {@link #stepsForCurves(List)}.  If all curves but one need the same value, you must use those two methods to
         * configure values in the proper order.
         *
         * @param steps The steps for every curve
         * @return The builder instance
         */
        public B stepsForAllCurves(int steps) {
            this.stepsForAllCurves = steps;
            return self();
        }

        /**
         * Set the interval to use when rendering the object along the curve.  This method is cumulative, each
         * successive call configures a curve.  Any curves not configured will default to 0.0.  Curves with interval
         * set to 0.0 will use the curve's step value.
         *
         * @param intervalForCurve The interval for the next curve
         * @return The builder instance
         */
        public B intervalForCurve(float intervalForCurve) {
            this.intervalsForCurves.add(intervalForCurve);
            return self();
        }

        /**
         * Set the interval to use when rendering the object along multiple curves.  This method is cumulative, each
         * successive call configures one or more curves.  Any curves not configured will default to 0.0.  Curves with
         * interval set to 0.0 will use the curve's step value.
         *
         * @param intervalsForCurves The intervals for the next N curves
         * @return The builder instance
         */
        public B intervalsForCurves(List<Float> intervalsForCurves) {
            this.intervalsForCurves.addAll(intervalsForCurves);
            return self();
        }

        /**
         * Set the interval to use on every curve.  This method is not cumulative; repeated calls will overwrite the
         * value.
         * <p>
         * Note: This will take priority over individual curves set in {@link #intervalForCurve(float)} or
         * {@link #intervalsForCurves(List)}.  If all curves but one need the same value, you must use those two
         * methods to configure values in the proper order.
         *
         * @param interval The interval for every curve
         * @return The builder instance
         */
        public B intervalForAllCurves(float interval) {
            this.intervalForAllCurves = interval;
            return self();
        }

        /**
         * Set the animation trimming for the Bézier curve path-animator.  The acceptable range is from 0 to 1,
         * inclusive, which represents the t-value used when computing each point on the curves.  All curves will trim
         * using the same settings.  The default is `[0.0, 1.0]`, which will cause every frame to be rendered.
         *
         * @param trimming The animation trimming
         * @return The builder instance
         */
        public B trimming(AnimationTrimming<Float> trimming) {
            if (trimming.getStart() < 0.0f || trimming.getStart() > 1.0f) {
                throw new IllegalArgumentException("The start trim value must be between 0 and 1, inclusive");
            }
            if (trimming.getEnd() < 0.0f || trimming.getEnd() > 1.0f) {
                throw new IllegalArgumentException("The end trim value must be between 0 and 1, inclusive");
            }
            if (trimming.getStart() > trimming.getEnd()) {
                throw new IllegalArgumentException("The start trim value must be less than the end trim value");
            }
            this.trimming = new AnimationTrimming<>(trimming);
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
                for (int i = 0; i < this.bezierCurves.size(); i++) {
                    this.intervalsForCurves.add(this.intervalForAllCurves);
                }
            } else if (this.stepsForAllCurves != 0) {
                this.stepsForCurves.clear();
                for (int i = 0; i < this.bezierCurves.size(); i++) {
                    this.stepsForCurves.add(this.stepsForAllCurves);
                }
            }
            // At this point, either an "all curves" value is provided or individual values for either steps or
            // intervals must be provided.  At this point, mixing intervals and steps is not allowed.
            if ((this.stepsForCurves.size() != this.bezierCurves.size()) && (this.intervalsForCurves.size() != this.bezierCurves.size())) {
                throw new IllegalStateException("Must provide steps or intervals for every curve");
            }
            for (int i = 0; i < this.bezierCurves.size(); i++) {
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
            return new BezierCurveAnimator(this);
        }

        private int getCurveSteps(int index) {
            // TODO: choose this value better, perhaps based on distance between start/end or start/controls/end?
            float distance = this.bezierCurves.get(index).length(100);
            return (int) Math.ceil(distance / this.intervalsForCurves.get(index));
        }
    }
}
