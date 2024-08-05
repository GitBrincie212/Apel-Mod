package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.AnimationTrimming;
import net.mcbrincie.apel.lib.util.interceptor.OldInterceptors;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** The linear animator. Which is used for linear paths(a.k.a. paths that are drawn as a line). It
 * accepts 2 or multiple points which draw the line and are called endpoints, they draw lines from the
 * previous endpoint to the next (the first to the second then second to third...). One semi-versatile
 * path animator but still capable of doing basic animations and is friendlier compared to other animators.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class LinearAnimator extends PathAnimatorBase {
    protected List<Vector3f> endpoints;
    protected List<Integer> stepsForSegments;
    protected AnimationTrimming<Integer> trimming;

    protected OldInterceptors<LinearAnimator, OnRenderStep> duringRenderingSteps = OldInterceptors.identity();

    public enum OnRenderStep {SHOULD_DRAW_STEP, CURRENT_ENDPOINT, RENDERING_POSITION}

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
     * Constructor for the linear animation. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * linear animator instance with all of its parameters regardless
     * of their visibility (this means protected & private params are copied)
     *
     * @param animator The animator to copy from
    */
    public LinearAnimator(LinearAnimator animator) {
        super(animator);
        this.endpoints = animator.endpoints;
        this.stepsForSegments = animator.stepsForSegments;
        this.trimming = animator.trimming;
        this.duringRenderingSteps = animator.duringRenderingSteps;
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
                Vector3f pos = new Vector3f(segmentDelta).mul(i).add(segmentStart);
                InterceptData<OnRenderStep> interceptData =
                        this.doBeforeStep(renderer.getServerWorld(), segmentIndex, pos, step);
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
     * there will be a boolean value that dictates if it should draw on this step, the rendering position of the
     * point that lives in the line and the endpoint index (UNMODIFIABLE)
     *
     * @param duringRenderingSteps the new interceptor to execute before drawing the individual steps
     */
    public void setDuringRenderingSteps(OldInterceptors<LinearAnimator, OnRenderStep> duringRenderingSteps) {
        this.duringRenderingSteps = Optional.ofNullable(duringRenderingSteps).orElse(OldInterceptors.identity());
    }

    protected InterceptData<OnRenderStep> doBeforeStep(
            ServerWorld world, int currEndpointIndex, Vector3f position, int currStep
    ) {
        InterceptData<OnRenderStep> interceptData = new InterceptData<>(
                world, null, currStep, OnRenderStep.class
        );
        interceptData.addMetadata(OnRenderStep.RENDERING_POSITION, position);
        interceptData.addMetadata(OnRenderStep.CURRENT_ENDPOINT, currEndpointIndex);
        interceptData.addMetadata(OnRenderStep.SHOULD_DRAW_STEP, true);
        this.duringRenderingSteps.apply(interceptData, this);
        return interceptData;
    }

    /** This is the linear path animator builder used for setting up a new linear path animator instance.
     * It is designed to be more friendly of how you arrange the parameters. Call {@code .builder()} to initiate
     * the builder, once you supplied the parameters then you can call {@code .build()} to create the instance
     *
     * @param <B> The builder type itself
    */
    public static class Builder<B extends Builder<B>> extends PathAnimatorBase.Builder<B, LinearAnimator> {
        protected List<Vector3f> endpoints = new ArrayList<>();
        protected List<Integer> stepsForSegments = new ArrayList<>();
        protected int stepsForAllSegments;
        protected List<Float> intervalsForSegments = new ArrayList<>();
        protected float intervalForAllSegments;
        protected AnimationTrimming<Integer> trimming = new AnimationTrimming<>(0, -1);

        private Builder() {}

        /** The endpoint of the linear path animator.
         * Do note that it adds it to the list of the endpoints as last
         *
         * @param endpoint The endpoint to add to the list of endpoints
         * @return The builder instance
         */
        public B endpoint(Vector3f endpoint) {
            this.endpoints.add(endpoint);
            return self();
        }

        /** The endpoints of the linear path animator.
         * Do note that it adds all to the list of the endpoints as last
         *
         * @param endpoints The endpoints to add to the list of endpoints
         * @return The builder instance
        */
        public B endpoints(List<Vector3f> endpoints) {
            this.endpoints.addAll(endpoints);
            return self();
        }


        public B stepsForSegment(int steps) {
            if (steps < 0) {
                throw new IllegalArgumentException("Steps must be non-negative");
            }
            this.stepsForSegments.add(steps);
            return self();
        }

        // Takes priority, if set
        public B stepsForAllSegments(int steps) {
            if (steps <= 0) {
                throw new IllegalArgumentException("Steps for all segments must be positive");
            }
            this.stepsForAllSegments = steps;
            return self();
        }

        public B stepsForSegments(List<Integer> stepsForSegments) {
            for (Integer steps : stepsForSegments) {
                this.stepsForSegment(steps);
            }
            return self();
        }

        public B intervalForSegment(float interval) {
            if (interval < 0.0f) {
                throw new IllegalStateException("Interval must be non-negative");
            }
            this.intervalsForSegments.add(interval);
            return self();
        }

        // Takes priority, if set
        public B intervalForAllSegments(float interval) {
            if (interval <= 0.0f) {
                throw new IllegalStateException("Interval for all segments must be positive");
            }
            this.intervalForAllSegments = interval;
            return self();
        }

        public B intervalsForSegments(List<Float> intervalsForSegments) {
            for (Float interval : intervalsForSegments) {
                this.intervalForSegment(interval);
            }
            return self();
        }

        /** The trimming of the linear path animator
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
            this.trimming = trimming;
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
