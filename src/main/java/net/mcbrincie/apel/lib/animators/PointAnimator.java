package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class PointAnimator extends PathAnimatorBase {
    protected Vec3d origin;

    public PointAnimator(int delay, @NotNull ParticleObject particle, Vec3d origin, int renderingSteps) {
        super(delay, particle, renderingSteps);
        this.origin = origin;
    }

    public PointAnimator(int delay, @NotNull ParticleObject particle, Vec3d origin, float renderingSteps) {
        super(delay, particle, Math.round(renderingSteps));
        this.origin = origin;
    }

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
    }
}
