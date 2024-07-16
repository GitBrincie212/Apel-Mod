package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.scheduler.ScheduledStep;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


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

    protected DrawInterceptor<ParallelAnimator, OnRenderPathAnimator> onAnimatorRendering = DrawInterceptor.identity();

    public enum OnRenderPathAnimator {PATH_ANIMATOR, SHOULD_RENDER_ANIMATOR, DELAY}

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
    public int setRenderingSteps(int steps) {
        throw new UnsupportedOperationException("Parallel Animators cannot set rendering steps");
    }

    /** This method is DEPRECATED and SHOULD NOT BE USED */
    @Deprecated
    @Override
    public ParticleObject<? extends ParticleObject<?>> setParticleObject(@NotNull ParticleObject<? extends ParticleObject<?>> object) {
        throw new UnsupportedOperationException("Parallel Animators cannot set an individual particle object");
    }

    /** This method is DEPRECATED and SHOULD NOT BE USED */
    @Override
    @Deprecated
    public float setRenderingInterval(float interval) {
        throw new UnsupportedOperationException("Parallel Animators cannot set rendering interval");
    }

    @Override
    public int convertIntervalToSteps() {
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

    private int getDelayForAnimator(int step) {
        return (this.delay == -1) ? this.delays.get(step - 1) : this.delay;
    }

    @Override
    public void beginAnimation(ApelServerRenderer renderer) throws SeqDuplicateException, SeqMissingException {
        this.allocateToScheduler();
        int step = 0;
        for (PathAnimatorBase animator : this.animators) {
            step++;
            InterceptData<OnRenderPathAnimator> interceptData = this.doBeforeStep(
                    renderer.getServerWorld(), animator, getDelayForAnimator(step), step
            );
            if (!((boolean) interceptData.getMetadata(OnRenderPathAnimator.SHOULD_RENDER_ANIMATOR))) continue;
            animator = (PathAnimatorBase) interceptData.getMetadata(OnRenderPathAnimator.PATH_ANIMATOR);
            int delayForAnimator = (int) interceptData.getMetadata(OnRenderPathAnimator.DELAY);
            int delayForAnimatorInUse = this.getDelayForAnimator(step);
            if (delayForAnimator != delayForAnimatorInUse) {
                this.delays.set(step - 1, delayForAnimator);
                if (delayForAnimator != this.delay) this.delay = -1;
                for (int delayPerAnimator : this.delays) {
                    if (delayPerAnimator != delayForAnimator) continue;
                    this.delay = -1;
                    break;
                }
            }
            this.allocateNewAnimator(renderer, step, animator);
        }
    }

    protected void allocateNewAnimator(ApelServerRenderer renderer, int step, PathAnimatorBase animator) {
        Runnable func = () -> animator.beginAnimation(renderer);
        int delayUsed = getDelayForAnimator(step);
        if (delayUsed == 0) {
            Apel.DRAW_EXECUTOR.submit(func);
            return;
        }
        if (this.processingSpeed <= 1) {
            Apel.SCHEDULER.allocateNewStep(
                    this, new ScheduledStep(delayUsed, new Runnable[]{func})
            );
            return;
        } else if (step % this.processingSpeed != 0) {
            this.storedFuncsBuffer.add(func);
            return;
        }
        Apel.SCHEDULER.allocateNewStep(
                this, new ScheduledStep(delayUsed, this.storedFuncsBuffer.toArray(Runnable[]::new))
        );
        this.storedFuncsBuffer.clear();
    }

    /** Set the interceptor to run before the drawing of each individual rendering step. The interceptor will be provided
     * with references to the {@link ServerWorld}, the current step number. As far as it goes for the metadata, you
     * have access to the path animator that will be drawn, the delay of the path animator before rendering and a
     * boolean value dictating if the path animator should render at all
     *
     * @param duringRenderingSteps the new interceptor to execute before drawing the individual steps
     */
    public void setOnAnimatorRendering(DrawInterceptor<ParallelAnimator, OnRenderPathAnimator> duringRenderingSteps) {
        this.onAnimatorRendering = Optional.ofNullable(duringRenderingSteps).orElse(DrawInterceptor.identity());
    }

    protected InterceptData<OnRenderPathAnimator> doBeforeStep(
            ServerWorld world, PathAnimatorBase pathAnimatorBase, int delay, int currStep
    ) {
        InterceptData<OnRenderPathAnimator> interceptData = new InterceptData<>(
                world, null, currStep, OnRenderPathAnimator.class
        );
        interceptData.addMetadata(OnRenderPathAnimator.PATH_ANIMATOR, pathAnimatorBase);
        interceptData.addMetadata(OnRenderPathAnimator.DELAY, delay);
        interceptData.addMetadata(OnRenderPathAnimator.SHOULD_RENDER_ANIMATOR, true);
        this.onAnimatorRendering.apply(interceptData, this);
        return interceptData;
    }
}
