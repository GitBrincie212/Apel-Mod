package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Arrays;

/** The linear animator. Which is used for linear paths(a.k.a. paths that are drawn as a line). It
 * accepts a start and an end which is where the 2 endpoints are. One of the least versatile animators
 * but still capable of doing basic animations and is friendlier compared to other animators. It accepts
 * the 2 endpoints of the line(start and end)
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class LinearAnimator extends PathAnimatorBase {
    protected Vector3f[] endpoints;
    protected int[] renderingSteps;
    protected float[] renderingInterval;

    private final IllegalArgumentException EQUAL_POSITIONS = new IllegalArgumentException("Starting & Ending Position cannot be equal");
    private final IllegalArgumentException BELOW_2_ENDPOINTS = new IllegalArgumentException("Endpoints should be above 2");


    /**
     * Constructor for the linear animation. This constructor is
     * meant to be used in the case that you want a constant amount
     * of particles. It doesn't look pretty on large distances tho
     *
     * @param delay The delay between each particle object render
     * @param start The starting position
     * @param end The ending position
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public LinearAnimator(
            int delay, @NotNull Vector3f start, @NotNull Vector3f end,
            @NotNull ParticleObject particle, int renderingSteps
    ) {
        super(delay, particle, renderingSteps);
        if (start.equals(end)) throw EQUAL_POSITIONS;
        this.endpoints = new Vector3f[]{start, end};
        this.renderingSteps = new int[]{renderingSteps};
        this.renderingInterval = new float[]{0.0f};
    }

    /**
     * Constructor for the linear animation. This constructor is
     * meant to be used in the case that you want a good consistent
     * looking particle line & also want to create multiple endpoints.
     * Because of the interval, the amount is dynamic which can cause
     * performance issues for larger distances(The higher the interval
     * the fewer particles are rendered, and it is also applied vice versa)
     *
     * @param delay The delay between each particle object render
     * @param endpoints The endpoint positions
     * @param particle The particle to use
     * @param renderingInterval The amount of blocks before placing a new render step
     */
    public LinearAnimator(
            int delay, @NotNull Vector3f[] endpoints,
            ParticleObject particle, float renderingInterval
    ) {
        super(delay, particle, renderingInterval);
        if (endpoints.length <= 2) throw BELOW_2_ENDPOINTS;
        int index = -1;
        Vector3f curr = endpoints[0];
        for (Vector3f endpoint : endpoints) {
            index++;
            if (index == 0) continue;
            if (curr.equals(endpoint)) throw EQUAL_POSITIONS;
        }
        this.endpoints = endpoints;
        this.renderingInterval = new float[]{renderingInterval};
        this.renderingSteps = new int[]{0};
    }

    /**
     * Constructor for the linear animation. This constructor is
     * meant to be used in the case that you want a constant amount &
     * of particles also multiple endpoints. It doesn't look pretty on
     * large distances tho
     *
     * @param delay The delay between each particle object render
     * @param endpoints The endpoint positions
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public LinearAnimator(
            int delay, @NotNull Vector3f[] endpoints,
            @NotNull ParticleObject particle, int renderingSteps
    ) {
        super(delay, particle, renderingSteps);
        if (endpoints.length <= 2) throw BELOW_2_ENDPOINTS;
        int index = -1;
        Vector3f curr = endpoints[0];
        for (Vector3f endpoint : endpoints) {
            index++;
            if (index == 0) continue;
            if (curr.equals(endpoint)) throw EQUAL_POSITIONS;
        }
        this.endpoints = endpoints;
        this.renderingSteps = new int[]{renderingSteps};
        this.renderingInterval = new float[]{0.0f};
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
     * @param renderingInterval The amount of blocks before placing a new render step
     */
    public LinearAnimator(
            int delay, @NotNull Vector3f[] endpoints,
            ParticleObject particle, float[] renderingInterval
    ) {
        super(delay, particle, renderingInterval[0]);
        if (endpoints.length <= 2) throw BELOW_2_ENDPOINTS;
        if ((renderingInterval.length - 1) == endpoints.length) {
            throw new IllegalArgumentException("Intervals do not match the endpoints");
        }
        int index = -1;
        Vector3f curr = endpoints[0];
        for (Vector3f endpoint : endpoints) {
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
     * Constructor for the linear animation. This constructor is
     * meant to be used in the case that you want a constant amount &
     * of particles also multiple endpoints. It doesn't look pretty on
     * large distances tho
     *
     * @param delay The delay between each particle object render
     * @param endpoints The endpoint positions
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public LinearAnimator(
            int delay, @NotNull Vector3f[] endpoints,
            @NotNull ParticleObject particle, int[] renderingSteps
    ) {
        super(delay, particle, renderingSteps[0]);
        if (endpoints.length <= 2) throw BELOW_2_ENDPOINTS;
        if ((renderingSteps.length - 1) == endpoints.length) {
            throw new IllegalArgumentException("Steps do not match the endpoints");
        }
        int index = -1;
        Vector3f curr = endpoints[0];
        for (Vector3f endpoint : endpoints) {
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
     * Constructor for the linear animation. This constructor is
     * meant to be used in the case that you want a good consistent
     * looking particle line. The amount is dynamic which can cause
     * performance issues for larger distances(The higher the interval
     * the fewer particles are rendered, and it is also applied vice versa)
     *
     * @param delay The delay between each particle object render
     * @param start The starting position
     * @param end The ending position
     * @param particle The particle to use
     * @param renderingInterval The amount of blocks before placing a new render step
     */
    public LinearAnimator(
            int delay, Vector3f start, Vector3f end,
            ParticleObject particle, float renderingInterval
    ) {
        super(delay, particle, renderingInterval);
        if (start.equals(end)) throw EQUAL_POSITIONS;
        this.endpoints = new Vector3f[]{start, end};
        this.renderingInterval = new float[]{renderingInterval};
        this.renderingSteps = new int[]{0};
    }

    /**
     * Constructor for the linear animation. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * linear animator instance with all of its parameters regardless
     * of their visibility(this means protected & private params are copied)
     *
     * @param animator The animator to copy from
    */
    public LinearAnimator(LinearAnimator animator) {
        super(animator);
        this.endpoints = animator.endpoints;
        this.renderingInterval = animator.renderingInterval;
        this.renderingSteps = animator.renderingSteps;
    }

    /** Gets the distance between the start & end position
     *
     * @return The distance between the start and end
     */
    public float getDistance() {
        float sumDistance = 0;
        int index = -1;
        Vector3f currVec = this.endpoints[0];
        for (Vector3f endpoint : this.endpoints) {
            index++;
            if (index == 0) continue;
            sumDistance += endpoint.distance(currVec);
        }
        return sumDistance;
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
    public void beginAnimation(ServerWorld world, int startStep, int endStep) throws SeqDuplicateException, SeqMissingException {
        if (startStep < 0) throw new IllegalArgumentException("Start step is not positive");
        if (endStep < 0 && endStep != -1) throw new IllegalArgumentException("End step is invalid");
        if (endStep <= startStep && endStep != -1) throw new IllegalArgumentException("Start & End step range is invalid");
        int particleAmount;
        float particleInterval;
        Vector3f curr = new Vector3f(this.endpoints[0].x, this.endpoints[0].y, this.endpoints[0].z);
        if (this.onStart != null) {
            this.onStart.apply(startStep, endStep, curr, this.renderingSteps[0], this.renderingInterval[0]);
        }
        this.allocateToScheduler();
        int lastStep = 0;
        int endpointIndex = -1;
        for (Vector3f endPos : this.endpoints) {
            endpointIndex++;
            if(endpointIndex == 0) continue;
            particleAmount = this.renderingSteps[endpointIndex - 1];
            particleInterval = this.renderingInterval[endpointIndex - 1];
            if (particleInterval == 0.0f) {
                particleInterval = (this.getDistance() / particleAmount) * (this.endpoints.length - 1);
            } else {
                particleAmount = this.convertToSteps();
            }
            Vector3f startPos = this.endpoints[endpointIndex - 1];
            float dist = this.getDistance();
            for (int i = 0; i < particleAmount; i++) {
                double currDist = curr.distance(endPos);
                float dirX = (endPos.x - startPos.x) / dist;
                float dirY = (endPos.y - startPos.y) / dist;
                float dirZ = (endPos.z - startPos.z) / dist;
                int currDirX = (int) Math.round((endPos.x - curr.x) / currDist);
                int currDirY = (int) Math.round((endPos.y - curr.y) / currDist);
                int currDirZ = (int) Math.round((endPos.z - curr.z) / currDist);
                double dotProduct = (currDirX * dirX) + (currDirY * dirY) + (currDirZ * dirZ);
                boolean isGoingSameDir = dotProduct > 0;
                if (curr.equals(endPos) || !isGoingSameDir || (i >= endStep && endStep != -1)) {
                    lastStep = i;
                    if (i == 0) break;
                }
                float newX = curr.x + (dirX * particleInterval);
                float newY = curr.y + (dirY * particleInterval);
                float newZ = curr.z + (dirZ * particleInterval);
                curr = new Vector3f(newX, newY, newZ);
                if (i <= startStep) continue;
                this.handleDrawingStep(world, i, curr);
                if (this.onProcess != null) {
                    this.onProcess.apply(i, endStep, curr, endPos, particleAmount, particleInterval);
                }
                lastStep++;
            }
        }
        if (this.onEnd != null) {
            this.onEnd.apply(
                    startStep, lastStep, curr,
                    this.endpoints[this.endpoints.length - 1],
                    this.renderingSteps[this.renderingSteps.length - 1],
                    this.renderingInterval[this.renderingInterval.length - 1]
            );
        }
        this.finishRendering();
    }
}
