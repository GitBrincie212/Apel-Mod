package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;


public class LinearAnimator extends PathAnimatorBase {
    protected Vec3d start;
    protected Vec3d end;

    private final IllegalArgumentException EQUAL_POSITIONS = new IllegalArgumentException("Starting & Ending Position cannot be equal");


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
            int delay, @NotNull Vec3d start, @NotNull Vec3d end,
            @NotNull ParticleObject particle, int renderingSteps
    ) {
        super(delay, particle, renderingSteps);
        if (start.equals(end)) throw EQUAL_POSITIONS;
        this.start = start;
        this.end = end;
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
            int delay, Vec3d start, Vec3d end,
            ParticleObject particle, float renderingInterval
    ) {
        super(delay, particle, renderingInterval);
        if (start.equals(end)) throw EQUAL_POSITIONS;
        this.start = start;
        this.end = end;
    }

    public LinearAnimator(LinearAnimator animator) {
        super(animator);
        this.start = animator.start;
        this.end = animator.end;
    }

    /** Gets the distance between the start & end position
     *
     * @return The distance between the start and end
     */
    public double getDistance() {
        return this.start.distanceTo(this.end);
    }


    @Override
    public int convertToSteps() {
        return (int) Math.ceil(this.getDistance() / this.renderingInterval);
    }

    @Override
    public void beginAnimation(ServerWorld world, int startStep, int endStep) throws SeqDuplicateException, SeqMissingException {
        if (startStep < 0) throw new IllegalArgumentException("Start step is not positive");
        if (endStep < 0 && endStep != -1) throw new IllegalArgumentException("End step is invalid");
        if (endStep <= startStep && endStep != -1) throw new IllegalArgumentException("Start & End step range is invalid");
        int particleAmount = this.renderingSteps;
        double particleInterval = this.getDistance() / this.renderingSteps;
        if (this.renderingInterval != 0.0f) {
            particleAmount = this.convertToSteps();
            particleInterval = this.renderingInterval;
        }
        Vec3d curr = new Vec3d(this.start.x, this.start.y, this.start.z);
        if (this.onStart != null) {
            this.onStart.apply(startStep, endStep, curr, this.renderingSteps, this.renderingInterval);
        }
        this.allocateToScheduler();
        int lastStep = 0;
        for (int i = 0; i < particleAmount; i++) {
            Vec3d endPos = this.end;
            Vec3d startPos = this.start;
            double dist = this.getDistance();
            double currDist = curr.distanceTo(endPos);
            double dirX = (endPos.x - startPos.x) / dist;
            double dirY = (endPos.y - startPos.y) / dist;
            double dirZ = (endPos.z - startPos.z) / dist;
            int currDirX = (int) Math.round((endPos.x - curr.x) / currDist);
            int currDirY = (int) Math.round((endPos.y - curr.y) / currDist);
            int currDirZ = (int) Math.round((endPos.z - curr.z) / currDist);
            double dotProduct = (currDirX * dirX) + (currDirY * dirY) + (currDirZ * dirZ);
            boolean isGoingSameDir = dotProduct > 0;
            if (curr.equals(endPos) || !isGoingSameDir || (i >= endStep && endStep != -1)) {
                lastStep = i;
                if (i == 0) break;
            }
            double newX = curr.x + (dirX * particleInterval);
            double newY = curr.y + (dirY * particleInterval);
            double newZ = curr.z + (dirZ * particleInterval);
            curr = new Vec3d(newX, newY, newZ);
            if (i <= startStep) continue;
            this.handleDrawingStep(world, i, curr);
            if (this.onProcess != null) {
                this.onProcess.apply(i, endStep, curr, this.end, this.renderingSteps, this.renderingInterval);
            }
            lastStep++;
        }
        if (this.onEnd != null) {
            this.onEnd.apply(startStep, lastStep, curr, this.end, this.renderingSteps, this.renderingInterval);
        }
    }
}
