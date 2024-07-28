package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.AnimationTrimming;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Optional;

/** The linear animator. Which is used for linear paths(a.k.a. paths that are drawn as a line). It
 * accepts 2 or multiple points which draw the line and are called endpoints, they draw lines from the
 * previous endpoint to the next (the first to the second then second to third...). One semi-versatile
 * path animator but still capable of doing basic animations and is friendlier compared to other animators.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class LinearAnimator extends PathAnimatorBase {
    protected Vector3f[] endpoints;
    protected int[] renderingSteps;
    protected float[] renderingInterval;
    protected AnimationTrimming<Integer> trimming = new AnimationTrimming<>(0, -1);

    protected DrawInterceptor<LinearAnimator, OnRenderStep> duringRenderingSteps = DrawInterceptor.identity();

    public enum OnRenderStep {SHOULD_DRAW_STEP, CURRENT_ENDPOINT, RENDERING_POSITION}

    /** Constructor for the linear animation. This constructor is
     * meant to be used in the case that you want a constant number
     * of particles. It doesn't look pretty at large distances tho
     *
     * @param delay The delay between each particle object render
     * @param start The starting position
     * @param end The ending position
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public LinearAnimator(
            int delay, @NotNull Vector3f start, @NotNull Vector3f end,
            @NotNull ParticleObject<? extends ParticleObject<?>> particle, int renderingSteps
    ) {
        this(delay, new Vector3f[]{start, end}, particle, new int[]{renderingSteps});
    }

    /**
     * Constructor for the linear animation. This constructor is
     * meant to be used in the case that you want a good consistent
     * looking particle line. The amount is dynamic that can cause
     * performance issues for larger distances (The higher the interval,
     * the fewer particles are rendered, and it is also applied vice versa)
     *
     * @param delay The delay between each particle object render
     * @param start The starting position
     * @param end The ending position
     * @param particle The particle to use
     * @param renderingInterval The number of blocks before placing a new render step
     */
    public LinearAnimator(
            int delay, @NotNull Vector3f start, @NotNull Vector3f end,
            @NotNull ParticleObject<? extends ParticleObject<?>> particle, float renderingInterval
    ) {
        this(delay, new Vector3f[]{start, end}, particle, new float[]{renderingInterval});
    }

    /**
     * Constructor for the linear animation. This constructor is
     * meant to be used in the case that you want a good consistent
     * looking particle line & also want to create multiple endpoints.
     * Because of the interval, the amount is dynamic that can cause
     * performance issues for larger distances (The higher the interval
     * the fewer particles are rendered, and it is also applied vice versa)
     *
     * @param delay The delay between each particle object render
     * @param endpoints The endpoint positions
     * @param particle The particle to use
     * @param renderingInterval The distance, in blocks, between rendering steps
     */
    public LinearAnimator(
            int delay, @NotNull Vector3f[] endpoints, @NotNull ParticleObject<? extends ParticleObject<?>> particle,
            float renderingInterval
    ) {
        // There should be one fewer interval entries than endpoints, since each pair needs an interval
        this(delay, endpoints, particle, defaultedArray(new float[endpoints.length - 1], renderingInterval));
    }

    /**
     * Constructor for the linear animation. This constructor is
     * meant to be used in the case that you want a constant amount &
     * of particles also multiple endpoints. It doesn't look pretty at
     * large distances tho
     *
     * @param delay The delay between each particle object render
     * @param endpoints The endpoint positions
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps between each pair of endpoints
     */
    public LinearAnimator(
            int delay, @NotNull Vector3f[] endpoints, @NotNull ParticleObject<? extends ParticleObject<?>> particle,
            int renderingSteps
    ) {
        // There should be one fewer step entries than endpoints since each segment needs steps
        this(delay, endpoints, particle, defaultedArray(new int[endpoints.length - 1], renderingSteps));
    }

    /**
     * Constructor for the linear animation. This constructor is
     * meant to be used in the case that you want a good consistent
     * looking particle line as well as better control on their interval
     * Because of the interval, the amount is dynamic which can cause
     * performance issues for larger distances(The higher the interval
     * the fewer particles are rendered, and it is also applied vice versa)
     *
     * @param delay The delay between each particle object render
     * @param endpoints The endpoint positions
     * @param particle The particle to use
     * @param renderingInterval The number of blocks before placing a new render step
     */
    public LinearAnimator(
            int delay, @NotNull Vector3f[] endpoints, @NotNull ParticleObject<? extends ParticleObject<?>> particle,
            float[] renderingInterval
    ) {
        super(delay, particle, renderingInterval[0]);
        if ((renderingInterval.length - 1) == endpoints.length) {
            throw new IllegalArgumentException("Intervals do not match the endpoints");
        }
        this.endpoints = endpoints;
        this.renderingInterval = renderingInterval;
        this.renderingSteps = new int[this.renderingInterval.length];
    }

    /**
     * Constructor for the linear animation. This constructor is
     * meant to be used in the case that you want a constant amount &
     * of particles also multiple endpoints. It doesn't look pretty at
     * large distances tho
     *
     * @param delay The delay between each particle object render
     * @param endpoints The endpoint positions
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public LinearAnimator(
            int delay, @NotNull Vector3f[] endpoints, @NotNull ParticleObject<? extends ParticleObject<?>> particle, int[] renderingSteps
    ) {
        super(delay, particle, renderingSteps[0]);
        if ((renderingSteps.length - 1) == endpoints.length) {
            throw new IllegalArgumentException("Steps do not match the endpoints");
        }
        this.endpoints = endpoints;
        this.renderingSteps = renderingSteps;
        this.renderingInterval = new float[this.renderingSteps.length];
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
        this.renderingInterval = animator.renderingInterval;
        this.renderingSteps = animator.renderingSteps;
        this.trimming = animator.trimming;
        this.duringRenderingSteps = animator.duringRenderingSteps;
    }

    /** Gets the distance between the start & end position
     *
     * @return The distance between the start and end
     */
    public float getDistance() {
        float sumDistance = 0;
        for (int i = 0; i < this.endpoints.length - 1; i++) {
            sumDistance += this.endpoints[i].distance(this.endpoints[i + 1]);
        }
        return sumDistance;
    }

    /** Sets the animation trimming which accepts a start trim or
     * an ending trim. The trim parts have to be integer values
     *
     * @return The animation trimming that is used
     */
    public AnimationTrimming<Integer> setTrimming(AnimationTrimming<Integer> trimming) {
        int startStep = trimming.getStart();
        int endStep = trimming.getEnd();
        if (startStep <= 0 || endStep >= this.getRenderingSteps() || startStep >= endStep) {
            throw new IllegalArgumentException("Invalid animation trimming range");
        }
        AnimationTrimming<Integer> prevTrimming = this.trimming;
        this.trimming = trimming;
        return prevTrimming;
    }

    /** Gets the animation trimming that is used
     *
     * @return The animation trimming that is used
     */
    public AnimationTrimming<Integer> getTrimming() {
        return this.trimming;
    }

    /** Sets the endpoint to a new value and returns the previous used value
     *
     * @param endpointIndex The endpoint index
     * @param newEndpoint The new position of the endpoint
     * @return The previous endpoint
     */
    public Vector3f setEndpoint(int endpointIndex, Vector3f newEndpoint) {
        Vector3f prevEndpoint = this.endpoints[endpointIndex];
        this.endpoints[endpointIndex] = newEndpoint;
        return prevEndpoint;
    }


    @Override
    public int convertIntervalToSteps() {
        int steps = 0;
        for (int i = 0; i < this.endpoints.length - 1; i++) {
            int segmentSteps = getSegmentSteps(i, this.renderingInterval[i]);
            steps += segmentSteps;
        }
        return steps;
    }

    private int getSegmentSteps(int segmentIndex, float segmentInterval) {
        float distance = this.endpoints[segmentIndex].distance(this.endpoints[segmentIndex + 1]);
        return (int) Math.ceil(distance / segmentInterval);
    }

    @Override
    public void beginAnimation(ApelServerRenderer renderer) throws SeqDuplicateException, SeqMissingException {
        int startStep = this.trimming.getStart();
        int endStep = this.trimming.getEnd();
        this.allocateToScheduler();
        int step = -1;
        for (int segmentIndex = 0; segmentIndex < this.endpoints.length - 1; segmentIndex++) {
            Vector3f segmentStart = this.endpoints[segmentIndex];
            Vector3f segmentEnd = this.endpoints[segmentIndex + 1];
            int segmentSteps = this.renderingSteps[segmentIndex];
            float segmentInterval = this.renderingInterval[segmentIndex];
            if (segmentInterval != 0.0f) {
                // Compute steps based on length of segment
                segmentSteps = this.getSegmentSteps(segmentIndex, segmentInterval);
            }
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
    public void setDuringRenderingSteps(DrawInterceptor<LinearAnimator, OnRenderStep> duringRenderingSteps) {
        this.duringRenderingSteps = Optional.ofNullable(duringRenderingSteps).orElse(DrawInterceptor.identity());
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
}
