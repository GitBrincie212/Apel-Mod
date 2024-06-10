package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticlePoint;
import net.mcbrincie.apel.lib.util.scheduler.ScheduledStep;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
public class ParallelAnimator extends PathAnimatorBase {
    protected List<PathAnimatorBase> animators = new ArrayList<>();
    protected List<Integer> delays = new ArrayList<>();

    public ParallelAnimator(int delay, PathAnimatorBase... pathAnimators) {
        super(delay, null, pathAnimators.length);
        if (pathAnimators.length == 0) {
            throw new IllegalArgumentException("There must be at least one path animator");
        }
        this.animators.addAll(List.of(pathAnimators));
    }

    public ParallelAnimator(int delay, List<PathAnimatorBase> pathAnimators) {
        super(delay, null, pathAnimators.size());
        if (pathAnimators.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one path animator");
        }
        this.animators.addAll(pathAnimators);
    }

    public ParallelAnimator(List<Integer> delay, List<PathAnimatorBase> pathAnimators) {
        super(0, null, pathAnimators.size());
        this.delay = -1;
        if (pathAnimators.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one path animator");
        }
        if (pathAnimators.size() != delay.size()) {
            throw new IllegalArgumentException("Delays must match the number of path animators");
        }
        this.animators.addAll(pathAnimators);
        this.delays.addAll(delay);
    }

    public ParallelAnimator(List<Integer> delay, PathAnimatorBase... pathAnimators) {
        super(0, null, pathAnimators.length);
        this.delay = -1;
        if (pathAnimators.length == 0) {
            throw new IllegalArgumentException("There must be at least one path animator");
        }
        if (pathAnimators.length != delay.size()) {
            throw new IllegalArgumentException("Delays must match the number of path animators");
        }
        this.animators.addAll(List.of(pathAnimators));
        this.delays.addAll(delay);
    }

    public void addAnimatorPath(PathAnimatorBase animator) {
        this.animators.add(animator);
    }

    public void removeAnimatorPath(PathAnimatorBase animator) {
        this.animators.remove(animator);
    }

    @Override
    @Deprecated
    public int setRenderSteps(int steps) {
        return -1;
    }

    @Deprecated
    @Override
    public ParticlePoint setParticleObject(@NotNull ParticlePoint object) {
        return null;
    }

    @Override
    @Deprecated
    public float setRenderInterval(float interval) {
        return -1.0f;
    }

    @Override
    public int convertToSteps() {
        return this.animators.size();
    }

    @Override
    public void beginAnimation(ServerWorld world) throws SeqDuplicateException, SeqMissingException {
        this.allocateToScheduler();
        int step = 0;
        for (PathAnimatorBase animator : this.animators) {
            step++;
            this.allocateNewAnimator(step, world, animator);
        }
    }

    public void allocateNewAnimator(int step, ServerWorld world, PathAnimatorBase animator) {
        Runnable func = () -> animator.beginAnimation(world);
        int delayUsed = (this.delay == -1) ? this.delays.get(step - 1) : this.delay;
        if (delayUsed == 0) {
            Apel.drawThread.submit(func);
            return;
        }
        if (this.processSpeed <= 1) {
            Apel.apelScheduler.allocateNewStep(
                    this, new ScheduledStep(delayUsed, new Runnable[]{func})
            );
            return;
        } else if (step % this.processSpeed != 0) {
            this.storedFuncsBuffer.add(func);
            return;
        }
        Apel.apelScheduler.allocateNewStep(
                this, new ScheduledStep(delayUsed, this.storedFuncsBuffer.toArray(Runnable[]::new))
        );
        this.storedFuncsBuffer.clear();
    }
}
