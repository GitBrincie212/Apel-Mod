package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.scheduler.ScheduledStep;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/** The parallel path animator. Which provides an interface for controlling multiple
 * concurrent path animators (they can also nest themselves) and can have an unlimited
 * number of path animators attached. They also can have delays for each path animator.
 * It is quite advanced but allows for easier management on multiple animators & is
 * versatile compared to the other easier ones
 */
@SuppressWarnings("unused")
public class ParallelAnimator extends PathAnimatorBase implements TreePathAnimator<PathAnimatorBase> {
    protected List<PathAnimatorBase> animators = new ArrayList<>();
    protected List<Integer> delays = new ArrayList<>();

    /** Constructor for the parallel animation. This constructor is
     * meant to be used in the case that you want to supply a specific
     * number of path animators in the form of varargs
     *
     * @param delay The delay between each particle object render
     * @param pathAnimators The path animators to append
     */
    public ParallelAnimator(int delay, PathAnimatorBase... pathAnimators) {
        super();
        this.renderingSteps = pathAnimators.length;
        this.setDelay(delay);
        if (pathAnimators.length == 0) {
            throw new IllegalArgumentException("There must be at least one path animator");
        }
        this.animators.addAll(List.of(pathAnimators));
    }

    /** Constructor for the parallel animation. This constructor is
     * meant to be used in the case that you have a list of path
     * animators which you want to supply all of them
     *
     * @param delay The delay between each particle object render
     * @param pathAnimators The path animators to append
    */
    public ParallelAnimator(int delay, List<PathAnimatorBase> pathAnimators) {
        super();
        this.renderingSteps = pathAnimators.size();
        this.setDelay(delay);
        if (pathAnimators.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one path animator");
        }
        this.animators.addAll(pathAnimators);
    }

    /** Constructor for the parallel animation. This constructor is
     * meant to be used in the case that you have a list of path
     * animators and a list of the delays
     *
     * @param delay The delays between each particle object render for each particle animator
     * @param pathAnimators The path animators to append
     */
    public ParallelAnimator(List<Integer> delay, List<PathAnimatorBase> pathAnimators) {
        super();
        this.delay = -1;
        this.renderingSteps = pathAnimators.size();
        if (pathAnimators.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one path animator");
        }
        if (pathAnimators.size() != delay.size()) {
            throw new IllegalArgumentException("Delays must match the number of path animators");
        }
        this.animators.addAll(pathAnimators);
        this.delays.addAll(delay);
    }

    /** Constructor for the parallel animation. This constructor is
     * meant to be used in the case that you want to supply the path
     * animators in the form of varargs, and in addition you want a
     * separate delays
     *
     * @param delay The delay between each particle object render
     * @param pathAnimators The path animators to append
     */
    public ParallelAnimator(List<Integer> delay, PathAnimatorBase... pathAnimators) {
        super();
        this.delay = -1;
        this.renderingSteps = pathAnimators.length;
        if (pathAnimators.length == 0) {
            throw new IllegalArgumentException("There must be at least one path animator");
        }
        if (pathAnimators.length != delay.size()) {
            throw new IllegalArgumentException("Delays must match the number of path animators");
        }
        this.animators.addAll(List.of(pathAnimators));
        this.delays.addAll(delay);
    }

    /** Appends a new child path animator to the collection of the child path animators
     * from the particle combiner. The method returns nothing
     *
     * @param animator The path animator to append
    */
    @Override
    public void addAnimatorPath(PathAnimatorBase animator) {
        this.animators.add(animator);
    }

    /** Removes a child path animator from the collection of the child path animators
     * from the particle combiner. The method returns nothing
     *
     * @param animator The path animator to remove
    */
    @Override
    public void removeAnimatorPath(PathAnimatorBase animator) {
        this.animators.remove(animator);
    }

    @Override
    public List<PathAnimatorBase> getPathAnimators() {
        return this.animators;
    }

    @Override
    public PathAnimatorBase getPathAnimator(int index) {
        return this.animators.get(index);
    }

    /** This method is DEPRECATED and SHOULD NOT BE USED */
    @Override
    @Deprecated
    public int setRenderSteps(int steps) {
        throw new UnsupportedOperationException("Parallel Animators cannot set rendering steps");
    }

    /** This method is DEPRECATED and SHOULD NOT BE USED */
    @Deprecated
    @Override
    public ParticleObject setParticleObject(@NotNull ParticleObject object) {
        throw new UnsupportedOperationException("Parallel Animators cannot set an individual particle object");
    }

    /** This method is DEPRECATED and SHOULD NOT BE USED */
    @Override
    @Deprecated
    public float setRenderInterval(float interval) {
        throw new UnsupportedOperationException("Parallel Animators cannot set rendering interval");
    }

    @Override
    public int convertToSteps() {
        return this.animators.size();
    }

    @Override
    protected int calculateDuration() {
        int delay = (this.delay != -1) ? this.delay : this.delays.parallelStream().reduce(0, Integer::sum);
        return delay + this.animators.parallelStream()
                .map(PathAnimatorBase::calculateDuration)
                .max(Comparator.naturalOrder())
                .orElse(0);
    }

    @Override
    public void beginAnimation(ApelServerRenderer renderer) throws SeqDuplicateException, SeqMissingException {
        this.allocateToScheduler();
        int step = 0;
        for (PathAnimatorBase animator : this.animators) {
            step++;
            this.allocateNewAnimator(renderer, step, animator);
        }
    }

    protected void allocateNewAnimator(ApelServerRenderer renderer, int step, PathAnimatorBase animator) {
        Runnable func = () -> animator.beginAnimation(renderer);
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
