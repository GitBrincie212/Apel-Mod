package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.AnimationTrimming;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.math.bezier.BezierCurve;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Optional;

/** The Bézier curve animator which is used for curved paths, it accepts two or multiple different bézier curves,
 * the curves are called endpoints unlike the linear animator, the Bézier curve path animator the curves one by one.
 * This means that you can have a curve on one ending position and another that starts in a completely different one.
 * This animator is more advanced than the linear animator, and some math knowledge is required on the internal workings
 * of a bézier curve nonetheless, it is very capable of creating beautiful complex curved paths
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class BezierCurveAnimator extends PathAnimatorBase {
    protected BezierCurve[] bezierCurves;
    protected int[] renderingSteps;
    protected float[] renderingInterval;
    protected AnimationTrimming<Integer> trimming = new AnimationTrimming<>(0, -1);

    protected DrawInterceptor<BezierCurveAnimator, OnRenderStep> duringRenderingSteps = DrawInterceptor.identity();

    public enum OnRenderStep {SHOULD_DRAW_STEP, RENDERING_POSITION}

    /**
     * Constructor for the bézier animation. This constructor is
     * meant to be used in the case that you want a good consistent
     * looking particle curve. The amount is dynamic that can cause
     * performance issues for larger distances (The higher the interval,
     * the fewer particles are rendered, and it is also applied vice versa)
     *
     * @param delay The delay between each particle object render
     * @param curve The bézier curve
     * @param particle The particle to use
     * @param renderingInterval The number of blocks before placing a new render step
     */
    public BezierCurveAnimator(
            int delay, @NotNull BezierCurve curve, @NotNull ParticleObject<? extends ParticleObject<?>> particle,
            float renderingInterval
    ) {
        this(delay, new BezierCurve[]{curve}, particle, new float[]{renderingInterval});
    }

    /** Constructor for the bézier curve animation. This constructor is
     * meant to be used in the case that you want a constant number
     * of particles. It doesn't look pretty at large distances tho
     *
     * @param delay The delay between each particle object render
     * @param curve The bézier curve
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public BezierCurveAnimator(
            int delay, @NotNull BezierCurve curve, @NotNull ParticleObject<? extends ParticleObject<?>> particle,
            int renderingSteps
    ) {
        this(delay, new BezierCurve[]{curve}, particle, new int[]{renderingSteps});
    }

    /**
     * Constructor for the bézier animation. This constructor is
     * meant to be used in the case that you want a good consistent
     * looking particle line & also want to create multiple bézier curves.
     * Because of the interval, the amount is dynamic that can cause
     * performance issues for larger distances (The higher the interval
     * the fewer particles are rendered, and it is also applied vice versa)
     *
     * @param delay The delay between each particle object render
     * @param bezierCurves The bézier curves
     * @param particle The particle to use
     * @param renderingInterval The number of blocks before placing a new render step
     */
    public BezierCurveAnimator(
            int delay, @NotNull BezierCurve[] bezierCurves,
            @NotNull ParticleObject<? extends ParticleObject<?>> particle, float renderingInterval
    ) {
        this(delay, bezierCurves, particle, defaultedArray(new float[bezierCurves.length], renderingInterval));
    }

    /**
     * Constructor for the Bézier curve animation. This constructor is
     * meant to be used in the case that you want a constant number
     * of particles & also multiple bézier curves. It doesn't look pretty at
     * large distances tho
     *
     * @param delay The delay between each particle object render
     * @param bezierCurves The bézier curves
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public BezierCurveAnimator(
            int delay, @NotNull BezierCurve[] bezierCurves,
            @NotNull ParticleObject<? extends ParticleObject<?>> particle, int renderingSteps
    ) {
        this(delay, bezierCurves, particle, defaultedArray(new int[bezierCurves.length], renderingSteps));
    }

    /**
     * Constructor for the Bézier animation. This constructor is
     * meant to be used in the case that you want a good consistent
     * looking particle curve as well as better control on their interval
     * Because of the interval, the amount is dynamic which can cause
     * performance issues for larger distances(The higher the interval
     * the fewer particles are rendered, and it is also applied vice versa)
     *
     * @param delay The delay between each particle object render
     * @param bezierCurves The bézier curves
     * @param particle The particle to use
     * @param renderingInterval The number of blocks before placing a new render step
     */
    public BezierCurveAnimator(
            int delay, @NotNull BezierCurve[] bezierCurves,
            @NotNull ParticleObject<? extends ParticleObject<?>> particle, float[] renderingInterval
    ) {
        super(delay, particle, renderingInterval[0]);
        if (bezierCurves.length == 0) {
            throw new IllegalArgumentException("Must provide at least one Bézier curve");
        }
        if (bezierCurves.length != renderingInterval.length) {
            throw new IllegalArgumentException("Length of curve and interval arrays do not match");
        }
        this.bezierCurves = bezierCurves;
        this.renderingInterval = renderingInterval;
        this.renderingSteps = new int[this.bezierCurves.length];
    }

    /**
     * Constructor for the bézier animation. This constructor is
     * meant to be used in the case that you want a constant amount &
     * of particles also multiple bézier curves. It doesn't look pretty at
     * large distances tho
     *
     * @param delay The delay between each particle object render
     * @param bezierCurves The bézier curves
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public BezierCurveAnimator(
            int delay, @NotNull BezierCurve[] bezierCurves,
            @NotNull ParticleObject<? extends ParticleObject<?>> particle, int[] renderingSteps
    ) {
        super(delay, particle, renderingSteps[0]);
        if (bezierCurves.length == 0) {
            throw new IllegalArgumentException("Must provide at least one Bézier curve");
        }
        if (bezierCurves.length != renderingSteps.length) {
            throw new IllegalArgumentException("Length of curve and step arrays do not match");
        }
        this.bezierCurves = bezierCurves;
        this.renderingSteps = renderingSteps;
        this.renderingInterval = new float[this.renderingSteps.length];
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
        this.renderingInterval = animator.renderingInterval;
        this.renderingSteps = animator.renderingSteps;
        this.trimming = animator.trimming;
        this.duringRenderingSteps = animator.duringRenderingSteps;
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

    @Override
    public int convertIntervalToSteps() {
        int steps = 0;
        for (int i = 0; i < this.bezierCurves.length; i++) {
            int curveSteps = getCurveSteps(this.bezierCurves[i], this.renderingInterval[i]);
            steps += curveSteps;
        }
        return steps;
    }

    private int getCurveSteps(BezierCurve bezierCurve, float interval) {
        // TODO: choose this value better, perhaps based on distance between start/end or start/controls/end?
        float distance = bezierCurve.length(100);
        return (int) Math.ceil(distance / interval);
    }

    @Override
    public void beginAnimation(ApelServerRenderer renderer) throws SeqDuplicateException, SeqMissingException {
        int index = -1;
        int step = -1;
        this.allocateToScheduler();
        for (BezierCurve bezierCurve : this.bezierCurves) {
            index++;
            int curveSteps = this.renderingSteps[index];
            float renderInterval = this.renderingInterval[index];
            if (renderInterval != 0.0f) {
                // Compute steps base on length of curve
                curveSteps = this.getCurveSteps(bezierCurve, renderInterval);
            }
            // Interval MUST be the reciprocal of steps so t is in [0, 1].
            float tStep = 1.0f / curveSteps;
            for (float t = 0; t < 1.0f; t += tStep) {
                step++;
                Vector3f pos = bezierCurve.compute(t);
                InterceptData<OnRenderStep> interceptData =
                        this.doBeforeStep(renderer.getServerWorld(), pos, step);
                if (!((boolean) interceptData.getMetadata(OnRenderStep.SHOULD_DRAW_STEP))) continue;
                pos = (Vector3f) interceptData.getMetadata(OnRenderStep.RENDERING_POSITION);
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
}
