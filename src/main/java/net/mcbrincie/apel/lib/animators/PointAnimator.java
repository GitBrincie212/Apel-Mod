package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class PointAnimator extends PathAnimatorBase {
    protected Vector3f origin;

    public PointAnimator(int delay, @NotNull ParticleObject particle, Vector3f origin, int renderingSteps) {
        super(delay, particle, renderingSteps);
        this.origin = origin;
    }

    public PointAnimator(int delay, @NotNull ParticleObject particle, Vector3f origin, float renderingSteps) {
        super(delay, particle, Math.round(renderingSteps));
        this.origin = origin;
    }

    /** Gets the origin point. Which is where the particle animation plays at
     *
     * @return The origin point(that is stationary)
    */
    public Vector3f getOrigin() {
        return this.origin;
    }

    /** Sets the origin point. Which is where the particle animation plays at. Returns
     * the previous origin point that was used
     *
     * @return The previous origin point used
    */
    public Vector3f setOrigin(Vector3f origin) {
        Vector3f prevOrigin = this.origin;
        this.origin = origin;
        return prevOrigin;
    }

    /**
     * Constructor for the point animator. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * point base animator instance with all of its parameters regardless
     * of their visibility(this means protected & private params are copied)
     *
     * @param animator The animator to copy from
    */
    public PointAnimator(PointAnimator animator) {
        super(animator);
        this.origin = animator.origin;
    }

    @Override
    public int convertToSteps() {
        return this.renderingSteps;
    }

    @Override
    public void beginAnimation(
            ServerWorld world, int startStep, int endStep
    ) throws SeqDuplicateException, SeqMissingException {
        this.allocateToScheduler();
        for (int i = 0; i < renderingSteps; i++) {
            this.handleDrawingStep(world, i, this.origin);
        }
        this.finishRendering();
    }
}
