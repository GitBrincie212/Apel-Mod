package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.context.AnimationContext;
import net.mcbrincie.apel.lib.util.scheduler.ScheduledStep;

import java.util.ArrayList;
import java.util.List;


/** The parallel path animator. Which provides an interface for controlling multiple
 * concurrent path animators (they can also nest themselves) and can have an unlimited
 * number of path animators attached. They also can have delays for each path animator.
 * It is quite advanced but allows for easier management on multiple animators and is
 * versatile compared to the other easier ones
 */
@SuppressWarnings("unused")
public class ParallelAnimator extends PathAnimatorBase<ParallelAnimator> implements TreePathAnimator {
    protected List<PathAnimatorBase<? extends PathAnimatorBase<?>>> animators;
    protected List<Integer> animatorDelays;

    public static <B extends Builder<B>> Builder<B> builder() {
        return new Builder<>();
    }

    private <B extends Builder<B>> ParallelAnimator(Builder<B> builder) {
        super();
        this.setDelay(builder.delay);
        this.setProcessingSpeed(builder.processingSpeed);
        this.animators = builder.childAnimators;
        this.animatorDelays = builder.childAnimatorDelays;
    }

    /** Appends a new child path animator to the collection of the child path animators
     * from the particle combiner. The method returns nothing
     *
     * @param animator The path animator to append
    */
    @Override
    public void addAnimatorPath(PathAnimatorBase<? extends PathAnimatorBase<?>> animator) {
        this.animators.add(animator);
    }

    /** Removes a child path animator from the collection of the child path animators
     * from the particle combiner. The method returns nothing
     *
     * @param animator The path animator to remove
    */
    @Override
    public void removeAnimatorPath(PathAnimatorBase<? extends PathAnimatorBase<?>> animator) {
        this.animators.remove(animator);
    }

    @Override
    public List<PathAnimatorBase<? extends PathAnimatorBase<?>>> getPathAnimators() {
        return this.animators;
    }

    @Override
    public PathAnimatorBase<? extends PathAnimatorBase<?>> getPathAnimator(int index) {
        return this.animators.get(index);
    }

    @Override
    public int convertIntervalToSteps() {
        return 0;
    }

    @Override
    protected int calculateDuration() {
        int maxDuration = 0;
        for (int index = 0; index < this.animators.size(); index++) {
            int childAnimatorDelay = this.animatorDelays.get(index);
            PathAnimatorBase<?> childAnimator = this.animators.get(index);
            int duration = childAnimatorDelay + childAnimator.calculateDuration();
            if (duration > maxDuration) {
                maxDuration = duration;
            }
        }
        return maxDuration;
    }

    @Override
    public void beginAnimation(ApelServerRenderer renderer) throws SeqDuplicateException, SeqMissingException {
        for (int index = 0; index < this.animators.size(); index++) {
            PathAnimatorBase<?> animator = this.animators.get(index);
            int totalDelay = this.delay + this.animatorDelays.get(index);

            AnimationContext animationContext = new AnimationContext(renderer.getServerWorld());
            animationContext.addMetadata(PATH_ANIMATOR, animator);
            animationContext.addMetadata(DELAY, totalDelay);
            this.beforeRender.compute(this, animationContext);

            if (!animationContext.shouldRender()) {
                continue;
            }

            // Effectively final variables for the lambda
            PathAnimatorBase<?> scheduledAnimator = animationContext.getMetadata(PATH_ANIMATOR, animator);
            int delayForAnimator = animationContext.getMetadata(DELAY, totalDelay);
            Runnable func = () -> scheduledAnimator.beginAnimation(renderer);

            if (delayForAnimator == 0) {
                func.run();
            } else {
                scheduledAnimator.allocateToScheduler();
                Apel.SCHEDULER.allocateNewStep(
                        scheduledAnimator, new ScheduledStep(delayForAnimator, new Runnable[]{func})
                );
            }

            this.afterRender.compute(this, animationContext);
        }
    }

    /** This is the parallel path-animator builder used for setting up a new parallel path-animator instance.
     * It is designed to be more friendly of how you arrange the parameters. Call {@code .builder()} to initiate
     * the builder, once you supplied the parameters then you can call {@code .build()} to create the instance
     *
     * @param <B> The builder type itself
    */
    public static class Builder<B extends Builder<B>> extends PathAnimatorBase.Builder<B, ParallelAnimator> {
        protected List<PathAnimatorBase<? extends PathAnimatorBase<?>>> childAnimators = new ArrayList<>();
        protected List<Integer> childAnimatorDelays = new ArrayList<>();

        private Builder () {}

        /** Add an animator to the list of path-animators
         *
         * @param animator The path-animator instance
         * @return The builder instance
        */
        public B animator(PathAnimatorBase<? extends PathAnimatorBase<?>> animator) {
            this.childAnimators.add(animator);
            return self();
        }

        /** Add the animator to the list of path-animators along with a set-delay
         *
         * @param animator The path-animator instance
         * @param delay The delay for the path-animator
         * @return The builder instance
        */
        public B animator(PathAnimatorBase<? extends PathAnimatorBase<?>> animator, int delay) {
            this.childAnimators.add(animator);
            this.childAnimatorDelays.add(delay);
            return self();
        }

        /** Add all the path-animators to the list of path-animators
         *
         * @param animators The path-animator instance
         * @return The builder instance
        */
        public B animators(List<PathAnimatorBase<? extends PathAnimatorBase<?>>> animators) {
            this.childAnimators.addAll(animators);
            return self();
        }

        /** Add all the path-animators to the list of path-animators along with
         * an individual delay for each path-animator matching 1:1
         *
         * @param animators The path-animator instance
         * @param delays The delays of each path-animator
         * @return The builder instance
        */
        public B animators(List<PathAnimatorBase<? extends PathAnimatorBase<?>>> animators, List<Integer> delays) {
            this.childAnimators.addAll(animators);
            this.childAnimatorDelays.addAll(delays);
            return self();
        }

        @Override
        public ParallelAnimator build() {
            if (this.delay < 0) {
                throw new IllegalStateException("Initial delay must be non-negative");
            }
            for (int i = 0; i < this.childAnimators.size(); i++) {
                if (this.childAnimators.get(i) == null) {
                    throw new NullPointerException("Child Animator cannot be null");
                }
                // Pad the list of delays, so it's equal in length
                if (this.childAnimatorDelays.size() == i) {
                    this.childAnimatorDelays.add(0);
                }
                if (this.childAnimatorDelays.get(i) < 0) {
                    throw new IllegalStateException("Child animator delays must be non-negative");
                }
            }
            return new ParallelAnimator(this);
        }
    }
}
