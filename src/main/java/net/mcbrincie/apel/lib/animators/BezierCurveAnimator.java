package net.mcbrincie.apel.lib.animators;

import com.mojang.datafixers.util.Function3;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.util.AnimationTrimming;
import net.mcbrincie.apel.lib.util.math.bezier.BezierCurve;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.function.BiFunction;

/** The Bézier curve animator. Which is used for curved paths. It accepts 2 or multiple different bézier curves,
 * the curves are called endpoints unlike the linear animator. This one computes the curves one by one which means
 * you can have a curve on one ending position and another that starts in a completely different one. This animator
 * is more advanced than the linear animator and some math knowledge is required on the internal workings of a bézier
 * curve, nonetheless it is very capable of creating beautiful complex curved paths
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class BezierCurveAnimator extends PathAnimatorBase {
    protected BezierCurve[] endpoints;
    protected int[] renderingSteps;
    protected float[] renderingInterval;
    protected AnimationTrimming<Integer> trimming = new AnimationTrimming<>(0, -1);

    protected BiFunction<AnimationTrimming<Integer>, BezierCurve[], Void> onEnd;
    protected BiFunction<AnimationTrimming<Integer>, BezierCurve[], Void> onStart;
    protected Function3<AnimationTrimming<Integer>, BezierCurve, BezierCurve[], Void> onProcess;

    private final IllegalArgumentException EQUAL_POSITIONS = new IllegalArgumentException("Starting & Ending Position cannot be equal");
    private final IllegalArgumentException EMPTY_ENDPOINTS = new IllegalArgumentException("Endpoints should not be empty");
    private final IllegalArgumentException INVALID_TRIM_RANGE = new IllegalArgumentException("Invalid animation trimming range");

    /** Constructor for the bézier curve animation. This constructor is
     * meant to be used in the case that you want a constant amount
     * of particles. It doesn't look pretty on large distances tho
     *
     * @param delay The delay between each particle object render
     * @param curve The bézier curve
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public BezierCurveAnimator(
            int delay, @NotNull BezierCurve curve, @NotNull ParticleObject particle, int renderingSteps
    ) {
        super(delay, particle, renderingSteps);
        this.endpoints = new BezierCurve[]{curve};
        this.renderingSteps = new int[]{renderingSteps};
        this.renderingInterval = new float[]{0.0f};
    }

    /**
     * Constructor for the bézier animation. This constructor is
     * meant to be used in the case that you want a good consistent
     * looking particle line & also want to create multiple bézier curves.
     * Because of the interval, the amount is dynamic which can cause
     * performance issues for larger distances(The higher the interval
     * the fewer particles are rendered, and it is also applied vice versa)
     *
     * @param delay The delay between each particle object render
     * @param endpoints The bézier curves
     * @param particle The particle to use
     * @param renderingInterval The amount of blocks before placing a new render step
     */
    public BezierCurveAnimator(
            int delay, @NotNull BezierCurve[] endpoints,
            ParticleObject particle, float renderingInterval
    ) {
        super(delay, particle, renderingInterval);
        if (endpoints.length == 0) throw EMPTY_ENDPOINTS;
        int index = -1;
        BezierCurve curr = endpoints[0];
        for (BezierCurve endpoint : endpoints) {
            index++;
            if (index == 0) continue;
            if (curr.equals(endpoint)) throw EQUAL_POSITIONS;
        }
        this.endpoints = endpoints;
        this.renderingInterval = new float[]{renderingInterval};
        this.renderingSteps = new int[]{0};
    }

    /**
     * Constructor for the Bézier curve animation. This constructor is
     * meant to be used in the case that you want a constant amount
     * of particles & also multiple bézier curves. It doesn't look pretty on
     * large distances tho
     *
     * @param delay The delay between each particle object render
     * @param endpoints The bézier curves
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public BezierCurveAnimator(
            int delay, @NotNull BezierCurve[] endpoints,
            @NotNull ParticleObject particle, int renderingSteps
    ) {
        super(delay, particle, renderingSteps);
        if (endpoints.length == 0) throw EMPTY_ENDPOINTS;
        int index = -1;
        BezierCurve curr = endpoints[0];
        for (BezierCurve endpoint : endpoints) {
            index++;
            if (index == 0) continue;
            if (curr.equals(endpoint)) throw EQUAL_POSITIONS;
        }
        this.endpoints = endpoints;
        this.renderingSteps = new int[]{renderingSteps};
        this.renderingInterval = new float[]{0.0f};
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
     * @param endpoints The bézier curves
     * @param particle The particle to use
     * @param renderingInterval The amount of blocks before placing a new render step
     */
    public BezierCurveAnimator(
            int delay, @NotNull BezierCurve[] endpoints,
            ParticleObject particle, float[] renderingInterval
    ) {
        super(delay, particle, renderingInterval[0]);
        if (endpoints.length == 0) throw EMPTY_ENDPOINTS;
        if ((renderingInterval.length - 1) == endpoints.length) {
            throw new IllegalArgumentException("Intervals do not match the endpoints");
        }
        int index = -1;
        BezierCurve curr = endpoints[0];
        for (BezierCurve endpoint : endpoints) {
            index++;
            if (index == 0) continue;
            if (curr.equals(endpoint)) throw EQUAL_POSITIONS;
        }
        this.endpoints = endpoints;
        this.renderingInterval = renderingInterval;
        int[] renderingSteps = new int[this.renderingInterval.length];
        Arrays.fill(renderingSteps, 0);
        this.renderingSteps = renderingSteps;
    }

    /**
     * Constructor for the bézier animation. This constructor is
     * meant to be used in the case that you want a constant amount &
     * of particles also multiple bézier curves. It doesn't look pretty on
     * large distances tho
     *
     * @param delay The delay between each particle object render
     * @param endpoints The bézier curves
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public BezierCurveAnimator(
            int delay, @NotNull BezierCurve[] endpoints,
            @NotNull ParticleObject particle, int[] renderingSteps
    ) {
        super(delay, particle, renderingSteps[0]);
        if (endpoints.length == 0) throw EMPTY_ENDPOINTS;
        if ((renderingSteps.length - 1) == endpoints.length) {
            throw new IllegalArgumentException("Steps do not match the endpoints");
        }
        int index = -1;
        BezierCurve curr = endpoints[0];
        for (BezierCurve endpoint : endpoints) {
            index++;
            if (index == 0) continue;
            if (curr.equals(endpoint)) throw EQUAL_POSITIONS;
        }
        this.endpoints = endpoints;
        this.renderingSteps = renderingSteps;
        float[] renderInterval = new float[this.renderingSteps.length];
        Arrays.fill(renderInterval, 0.0f);
        this.renderingInterval = renderInterval;
    }

    /**
     * Constructor for the bézier animation. This constructor is
     * meant to be used in the case that you want a good consistent
     * looking particle curve. The amount is dynamic which can cause
     * performance issues for larger distances(The higher the interval
     * the fewer particles are rendered, and it is also applied vice versa)
     *
     * @param delay The delay between each particle object render
     * @param start The starting bézier curve
     * @param end The ending bézier curve
     * @param particle The particle to use
     * @param renderingInterval The amount of blocks before placing a new render step
     */
    public BezierCurveAnimator(
            int delay, BezierCurve start, BezierCurve end,
            ParticleObject particle, float renderingInterval
    ) {
        super(delay, particle, renderingInterval);
        if (start.equals(end)) throw EQUAL_POSITIONS;
        this.endpoints = new BezierCurve[]{start, end};
        this.renderingInterval = new float[]{renderingInterval};
        this.renderingSteps = new int[]{0};
    }

    /**
     * Constructor for the bézier animation. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * bézier animator instance with all of its parameters regardless
     * of their visibility(this means protected & private params are copied)
     *
     * @param animator The animator to copy from
    */
    public BezierCurveAnimator(BezierCurveAnimator animator) {
        super(animator);
        this.endpoints = animator.endpoints;
        this.renderingInterval = animator.renderingInterval;
        this.renderingSteps = animator.renderingSteps;
        this.trimming = animator.trimming;
        this.onEnd = animator.onEnd;
        this.onStart = animator.onStart;
        this.onProcess = animator.onProcess;
    }

    /** Gets the distance needed to travel from the starting bézier curve to the ending bézier curve
     *
     * @return The distance between the starting bézier curve & ending bézier curve
     */
    public float getDistance() {
        float sumDistance = 0;
        for (BezierCurve endpoint : this.endpoints) {
            sumDistance += endpoint.length(this.convertToSteps());
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
        if (startStep <= 0) {throw INVALID_TRIM_RANGE;}
        if (endStep >= this.getRenderSteps()) {throw INVALID_TRIM_RANGE;}
        if (startStep >= endStep) {throw INVALID_TRIM_RANGE;}
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
    public int convertToSteps() {
        float sumInterval = 0;
        for (float i : this.renderingInterval) {
            sumInterval += i;
        }
        return (int) Math.ceil(this.getDistance() / sumInterval);
    }

    @Override
    protected int scheduleGetAmount() {
        int sumSteps = 0;
        for (int i : this.renderingSteps) {
            sumSteps += i;
        }
        return sumSteps * (this.endpoints.length - 1);
    }

    @Override
    public void beginAnimation(ServerWorld world) throws SeqDuplicateException, SeqMissingException {
        int index = -1;
        int step = -1;
        int particleAmount;
        float particleInterval;
        if (this.onStart != null) {
            this.onStart.apply(this.trimming, this.endpoints);
        }
        this.allocateToScheduler();
        for (BezierCurve endpoint : this.endpoints) {
            index++;
            particleAmount = this.renderingSteps[index];
            particleInterval = this.renderingInterval[index];
            if (particleInterval == 0.0f) {
                particleInterval = 1.0f / particleAmount;
            } else {
                particleAmount = this.convertToSteps();
            }
            for (int i = 0; i < particleAmount; i++) {
                step++;
                Vector3f pos = endpoint.compute(particleInterval * i);
                this.handleDrawingStep(world, step, pos);
                if (this.onProcess != null) {
                    this.onProcess.apply(this.trimming, endpoint, this.endpoints);
                }
            }
            if (this.onEnd != null) {
                this.onEnd.apply(this.trimming, this.endpoints);
            }
        }
    }
}
