package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.AnimationTrimming;
import net.mcbrincie.apel.lib.util.interceptor.context.AnimationContext;
import net.mcbrincie.apel.lib.util.interceptor.context.Key;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/** The linear animator. Which is used for linear paths(a.k.a. paths that are drawn as a line). It
 * accepts 2 or multiple points which draw the line and are called endpoints, they draw lines from the
 * previous endpoint to the next (the first to the second then second to third...). One semi-versatile
 * path animator but still capable of doing basic animations and is friendlier compared to other animators.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class LinearAnimator extends PathAnimatorBase<LinearAnimator> {
    protected List<Vector3f> endpoints;
    protected List<Integer> stepsForSegments;
    protected AnimationTrimming<Integer> trimming;

    public static final Key<Integer> CURRENT_ENDPOINT_INDEX = Key.integerKey("currentEndpointIndex");

    public static <B extends Builder<B>> Builder<B> builder() {
        return new Builder<>();
    }

    private <B extends Builder<B>> LinearAnimator(Builder<B> builder) {
        super(builder);
        this.endpoints = builder.endpoints;
        this.stepsForSegments = builder.stepsForSegments;
        this.trimming = builder.trimming;
    }

    /**
     * Copy constructor for the linear animation. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * linear animator instance with all of its parameters regardless
     * of their visibility (this means protected and private params are copied)
     *
     * @param animator The animator to copy from
    */
    public LinearAnimator(LinearAnimator animator) {
        super(animator);
        this.endpoints = animator.endpoints;
        this.stepsForSegments = animator.stepsForSegments;
        this.trimming = animator.trimming;
    }

    @Override
    public int convertIntervalToSteps() {
        return this.stepsForSegments.stream().mapToInt(i -> i).sum();
    }

    @Override
    public void beginAnimation(ApelServerRenderer renderer) throws SeqDuplicateException, SeqMissingException {
        int startStep = this.trimming.getStart();
        int endStep = this.trimming.getEnd();
        this.allocateToScheduler();

        int step = -1;
        for (int segmentIndex = 0; segmentIndex < this.endpoints.size() - 1; segmentIndex++) {
            Vector3f segmentStart = this.endpoints.get(segmentIndex);
            Vector3f segmentEnd = this.endpoints.get(segmentIndex + 1);
            int segmentSteps = this.stepsForSegments.get(segmentIndex);

            Vector3f segmentDelta = new Vector3f(segmentEnd).sub(segmentStart).div(segmentSteps);
            for (int i = 0; i < segmentSteps; i++) {
                step++;
                if (i < startStep) {
                    continue;
                }
                // Handle trimming, but only if the end was set to a non-default value
                if (i >= endStep && endStep != -1) {
                    break;
                }
                Vector3f renderPosition = new Vector3f(segmentDelta).mul(i).add(segmentStart);
                AnimationContext animationContext = new AnimationContext(renderer.getServerWorld(), renderPosition, step);
                animationContext.addMetadata(CURRENT_ENDPOINT_INDEX, segmentIndex);
                this.beforeRender.compute(this, animationContext);
                Vector3f actualPosition = animationContext.getPosition();
                this.handleDrawingStep(renderer, step, actualPosition);
                this.afterRender.compute(this, animationContext);
            }
        }
    }

    /** This is the linear path animator builder used for setting up a new linear path animator instance.
     * It is designed to be more friendly of how you arrange the parameters. Call {@code .builder()} to initiate
     * the builder, once you supplied the parameters then you can call {@code .build()} to create the instance.
     * <p>
     * The LinearAnimator supports multiple, linked segments.  Every endpoint added beyond the first will create a
     * segment along which the particle object travels.  The travel speed may be configured by providing the number of
     * steps into which to divide the segment, or an interval that also divides the segment into steps.  These interval
     * and step values may be provided uniquely for every segment, set in groups of segments, or uniform across all
     * segments.  The priority is as follows:
     * <ol>
     *     <li>{@link #intervalForAllSegments(float)}</li>
     *     <li>{@link #stepsForAllSegments(int)}</li>
     *     <li>{@link #intervalForSegment(float)} and {@link #intervalsForSegments(List)}</li>
     *     <li>{@link #stepsForSegment(int)} and {@link #stepsForSegments(List)}</li>
     * </ol>
     *
     * @param <B> The builder type itself
    */
    public static class Builder<B extends Builder<B>> extends PathAnimatorBase.Builder<B, LinearAnimator> {
        protected List<Vector3f> endpoints = new ArrayList<>();
        protected List<Integer> stepsForSegments = new ArrayList<>();
        protected int stepsForAllSegments = 0;
        protected List<Float> intervalsForSegments = new ArrayList<>();
        protected float intervalForAllSegments = 0.0f;
        protected AnimationTrimming<Integer> trimming = new AnimationTrimming<>(0, -1);

        private Builder() {}

        /**
         * Adds a waypoint to the path this animator takes.  This method is cumulative; repeated calls will append to
         * the list of waypoints.  There must be at least two waypoints.
         *
         * @param endpoint The endpoint to add to the list of endpoints
         * @return The builder instance
         */
        public B endpoint(Vector3f endpoint) {
            this.endpoints.add(endpoint);
            return self();
        }

        /**
         * Adds multiple waypoints to the path this animator takes.  This method is cumulative; repeated calls will
         * append to the list of waypoints.  There must be at least two waypoints.
         *
         * @param endpoints The endpoints to add to the list of endpoints
         * @return The builder instance
        */
        public B endpoints(List<Vector3f> endpoints) {
            this.endpoints.addAll(endpoints);
            return self();
        }

        /**
         * Set the number of steps to use when rendering the object along a segment between two waypoints.  This method
         * is cumulative, each successive call configures a segment.  Any segments not configured will default to 0.
         * Segments with steps set to 0 will use the segment's interval value.
         *
         * @param steps The steps for the next segment
         * @return The builder instance
         */
        public B stepsForSegment(int steps) {
            if (steps < 0) {
                throw new IllegalArgumentException("Steps must be non-negative");
            }
            this.stepsForSegments.add(steps);
            return self();
        }

        /**
         * Set the number of steps to use when rendering the object along multiple segments.  This method is cumulative,
         * each successive call configures one or more segments.  Any segments not configured will default to 0.
         * Segments with steps set to 0 will use the segment's interval value.
         *
         * @param stepsForSegments The steps for the next N segments
         * @return The builder instance
         */
        public B stepsForSegments(List<Integer> stepsForSegments) {
            for (Integer steps : stepsForSegments) {
                this.stepsForSegment(steps);
            }
            return self();
        }

        /**
         * Set the number of steps to use on every segment.  This method is not cumulative; repeated calls will
         * overwrite the value.
         * <p>
         * Note: This will take priority over individual segments set in {@link #stepsForSegment(int)} or
         * {@link #stepsForSegments(List)}.  If all segments but one need the same value, you must use those two
         * methods to configure values in the proper order.
         *
         * @param steps The steps for every segment
         * @return The builder instance
         */
        public B stepsForAllSegments(int steps) {
            if (steps <= 0) {
                throw new IllegalArgumentException("Steps for all segments must be positive");
            }
            this.stepsForAllSegments = steps;
            return self();
        }

        /**
         * Set the interval to use when rendering the object along a segment between two waypoints.  This method
         * is cumulative, each successive call configures a segment.  Any segments not configured will default to 0.0.
         * Segments with an interval of 0.0 will use the segment's value for steps.
         *
         * @param interval The interval for the next segment
         * @return The builder instance
         */
        public B intervalForSegment(float interval) {
            if (interval < 0.0f) {
                throw new IllegalStateException("Interval must be non-negative");
            }
            this.intervalsForSegments.add(interval);
            return self();
        }

        /**
         * Set the interval to use when rendering the object along multiple segments.  This method is cumulative, each
         * successive call configures a segment.  Any segments not configured will default to 0.0.  Segments with an
         * interval of 0.0 will use the segment's value for steps.
         *
         * @param intervalsForSegments The intervals for the next N segments
         * @return The builder instance
         */
        public B intervalsForSegments(List<Float> intervalsForSegments) {
            for (Float interval : intervalsForSegments) {
                this.intervalForSegment(interval);
            }
            return self();
        }

        /**
         * Set the interval to use on every segment.  This method is not cumulative; repeated calls will overwrite the
         * value.
         * <p>
         * Note: This will take priority over individual segments set in {@link #intervalForSegment(float)} or
         * {@link #intervalsForSegments(List)}.  If all segments but one need the same value, you must use those two
         * methods to configure values in the proper order.
         *
         * @param interval The interval for every segment
         * @return The builder instance
         */
        public B intervalForAllSegments(float interval) {
            if (interval <= 0.0f) {
                throw new IllegalStateException("Interval for all segments must be positive");
            }
            this.intervalForAllSegments = interval;
            return self();
        }

        /**
         * Set the trimming of each segment of the path.  Trimming refers to step numbers, and all segments will use
         * the same trimming settings.  Valid start values range from 0 to the value used for the end.  Valid end values
         * are 0 and greater.  If the end is set to -1, no steps will be trimmed off the end.
         *
         * @param trimming The trimming of the linear path animator
         * @return The builder instance
         */
        public B trimming(AnimationTrimming<Integer> trimming) {
            if (trimming.getStart() < 0) {
                throw new IllegalArgumentException("Trim start must be non-negative");
            }
            if (trimming.getEnd() < -1) {
                throw new IllegalArgumentException("Trim end must be -1 (no trim) or non-negative");
            }
            if (trimming.getEnd() != -1 && trimming.getStart() >= trimming.getEnd()) {
                throw new IllegalArgumentException("Trim start must be less than trim end");
            }
            this.trimming = new AnimationTrimming<>(trimming);
            return self();
        }

        @Override
        public LinearAnimator build() {
            if (this.endpoints.size() < 2) {
                throw new IllegalStateException("Must provide at least two endpoints");
            }
            // If an "all segment" value is provided, it takes priority: interval first, then steps
            if (this.intervalForAllSegments != 0.0f) {
                this.intervalsForSegments.clear();
                for (int i = 0; i < this.endpoints.size() - 1; i++) {
                    this.intervalsForSegments.add(this.intervalForAllSegments);
                }
            } else if (this.stepsForAllSegments != 0) {
                this.stepsForSegments.clear();
                for (int i = 0; i < this.endpoints.size() - 1; i++) {
                    this.stepsForSegments.add(this.stepsForAllSegments);
                }
            }
            // At this point, either an "all segments" value is provided or individual values for either steps or
            // intervals must be provided.  At this point, mixing intervals and steps is not allowed.
            if ((this.stepsForSegments.size() + 1 != this.endpoints.size()) && (this.intervalsForSegments.size() + 1 != this.endpoints.size())) {
                throw new IllegalStateException("Must provide steps or intervals for every segment");
            }
            for (int i = 0; i < this.endpoints.size() - 1; i++) {
                // Pad the lists so the conversion from interval to steps is straightforward
                if (this.stepsForSegments.size() == i) {
                    this.stepsForSegments.add(0);
                }
                if (this.intervalsForSegments.size() == i) {
                    this.intervalsForSegments.add(0.0f);
                }
                // Verify that at least one of steps/interval is provided
                if (this.stepsForSegments.get(i) == 0 && this.intervalsForSegments.get(i) == 0.0f) {
                    throw new IllegalStateException("Either steps or interval must be positive for segment " + i);
                }
                // Interval takes priority, if set.  If not set, then steps must already be set.
                if (this.intervalsForSegments.get(i) != 0.0f) {
                    this.stepsForSegments.set(i, this.getSegmentSteps(i));
                }
            }
            return new LinearAnimator(this);
        }

        private int getSegmentSteps(int segmentIndex) {
            float distance = this.endpoints.get(segmentIndex).distance(this.endpoints.get(segmentIndex + 1));
            return (int) Math.ceil(distance / this.intervalsForSegments.get(segmentIndex));
        }

    }
}
