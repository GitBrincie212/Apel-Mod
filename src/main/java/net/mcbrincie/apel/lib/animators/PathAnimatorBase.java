package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ServerWorldAccess;
import net.mcbrincie.apel.lib.util.interceptor.AnimationInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.context.Key;
import net.mcbrincie.apel.lib.util.math.TrigTable;
import net.mcbrincie.apel.lib.util.scheduler.ScheduledStep;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/** The abstract base class that all path animators inherit from. It
 * does some stuff under the hood and provides useful methods for working
 * with the path animators. The scheduling is abstracted away from the user,
 * so the only thing they need to care is setting the logic
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class PathAnimatorBase<T extends PathAnimatorBase<T>> {
    protected ParticleObject<? extends ParticleObject<?>> particleObject;
    protected int delay = 1;
    protected int processingSpeed = 1;
    protected int renderingSteps = 0;
    protected float renderingInterval = 0.0f;
    protected AnimationInterceptor<T> beforeRender = AnimationInterceptor.identity();

    protected List<Runnable> storedFuncsBuffer = new ArrayList<>();

    protected static final TrigTable trigTable = Apel.TRIG_TABLE;

    protected <B extends Builder<B, T>> PathAnimatorBase(Builder<B, T> builder) {
        this.setParticleObject(builder.particleObject);
        this.setDelay(builder.delay);
        this.setProcessingSpeed(builder.processingSpeed);
        switch (builder.renderCalculationMethod) {
            case UNSET -> {
                // Take no action, since not all animators need these (e.g., Linear, BezierCurve)
            }
            case RENDERING_STEPS -> this.setRenderingSteps(builder.renderingSteps);
            case RENDERING_INTERVAL -> this.setRenderingInterval(builder.renderingInterval);
        }
        this.setBeforeRender(builder.beforeRender);
    }

    /** This is an empty constructor meant as a placeholder */
    public PathAnimatorBase() {
    }

    /**
     * Constructor for the path base animator. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * path base animator instance with all of its parameters regardless
     * of their visibility (this means protected and private params are copied)
     *
     * @param animator The animator to copy from
    */
    public PathAnimatorBase(PathAnimatorBase<T> animator) {
        this.particleObject = animator.particleObject;
        this.delay = animator.delay;
        this.processingSpeed = animator.processingSpeed;
        this.renderingInterval = animator.renderingInterval;
        this.renderingSteps = animator.renderingSteps;
        this.beforeRender = animator.beforeRender;
        this.storedFuncsBuffer = new ArrayList<>();
    }

    /** Simplifies the process of scheduling a new sequence in the scheduler.
     *  Instead of checking if the delay isn't 0 and that there is no already allocated
     *  sequence. The method does that for your convenience.
     */
    public void allocateToScheduler() {
        if (this.delay == 0) {
            return;
        }
        Apel.SCHEDULER.allocateNewSequence(this);
    }

    /** Gets the amount of rendering steps, which can be zero indicating
     * that there weren't any rendering steps specified
     *
     * @return The number of particles
     */
    public int getRenderingSteps() {
        return this.renderingSteps;
    }

    /** Sets the rendering steps to a new value. It resets the rendering interval
     *  to 0.0f. And returns the previous rendering steps used
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param steps The new rendering steps to set
     * @return The previous rendering interval
     */
    public final int setRenderingSteps(int steps) {
        if (steps < 0) {
            throw new IllegalArgumentException("Rendering Steps must be non-negative");
        }
        int prevRenderStep = this.renderingSteps;
        this.renderingSteps = steps;
        this.renderingInterval = 0.0f;
        return prevRenderStep;
    }

    /** Gets the interval of blocks per particle object render.
     * Which can be zero indicating that there wasn't any
     * interval specified
     *
     * @return The interval of blocks per particle object render
     */
    public float getRenderingInterval() {
        return this.renderingInterval;
    }

    /** Sets the rendering interval to a new value. It resets the rendering steps
     *  to 0. And returns the previous rendering interval used
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param interval The new rendering interval to set
     * @return The previous rendering interval
     */
    public final float setRenderingInterval(float interval) {
        if (interval < 0) {
            throw new IllegalArgumentException("Rendering Interval must be non-negative");
        }
        float prevInterval = this.renderingInterval;
        this.renderingInterval = interval;
        this.renderingSteps = 0;
        return prevInterval;
    }

    /** Gets the delay per rendering step, this can be zero indicating
     * that the animation plays in an instant; the amount of delay is
     * also controlled via processing speed
     *
     * @see PathAnimatorBase#setProcessingSpeed(int)
     * @return The amount of rendering steps
     */
    public int getDelay() {
        return this.delay;
    }

    /** Sets the delay per rendering step to a new value. And
     * returns the previous delay per rendering step used
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @see PathAnimatorBase#setProcessingSpeed(int)
     *
     * @param delay The new delay value to set
     * @return The previous amount of rendering steps
     */
    public final int setDelay(int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Delay is not positive");
        }
        int prevDelay = this.delay;
        this.delay = delay;
        return prevDelay;
    }

    /** Gets the particle object that is used. Particle objects in short are the
     * particles that the user sees per rendering step; they have a draw method
     *
     *
     * @return The particle object
     */
    public ParticleObject<? extends ParticleObject<?>> getParticleObject() {
        return this.particleObject;
    }

    /** Sets the particle object to a new value. And returns the
     * previous particle object used in the animator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param object The new particle object to set
     * @return The previous amount of rendering steps
     */
    public final ParticleObject<? extends ParticleObject<?>> setParticleObject(@NotNull ParticleObject<? extends ParticleObject<?>> object) {
        ParticleObject<?> particleObject = this.particleObject;
        this.particleObject = object;
        return particleObject;
    }

    /** Gets the processing speed. Which is measured in rs/st and dictates how many functions
     * to execute per rendering step. By default, it is set to 1 rs/st
     *
     * @see PathAnimatorBase#getDelay()
     * @return The processing speed used
     */
    public int getProcessingSpeed() {
        return this.processingSpeed;
    }

    /** Sets the processing speed to allow for even faster animations on larger rendering steps.
     *  This tells the system how many functions for that animator to execute per tick. Which means
     *  that the number of steps per tick. It has to be above 1 (speed 1 counts normal). The speed is
     *  measured in render steps / server ticks or rs/st. It returns the previous processing speed used
     *  <br><br>
     *  Use this function when you want detailed animations to want to go faster (if delay 1 doesn't satisfy)
     *  <br><br>
     *  <strong>Note:</strong> When delay is 0, processing speed is completely ignored.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @see PathAnimatorBase#setDelay(int)
     * @param speed The number of steps to execute per tick
     * @return The previous processing speed used
     */
    public final int setProcessingSpeed(int speed) {
        if (speed < 1) {
            throw new IllegalArgumentException("Process speed cannot be below 1 rs/st");
        }
        int prevProcessSpeed = this.processingSpeed;
        this.processingSpeed = speed;
        return prevProcessSpeed;
    }

    /**
     * Set the interceptor to run before rendering the step.  The interceptor will be provided with references to the
     * {@link ServerWorld}, the "origin" point from which the step will be rendered, whether to render during this step,
     * and any metadata available via {@link Key}s defined in specific Animator subclasses.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param beforeRender The new interceptor to execute prior to rendering the step
     */
    public final void setBeforeRender(AnimationInterceptor<T> beforeRender) {
        this.beforeRender = Optional.ofNullable(beforeRender).orElse(AnimationInterceptor.identity());
    }

    /** Does the calculations to convert from an interval to rendering steps
     *
     * @return The number of steps
     */
    public abstract int convertIntervalToSteps();

    /**
     * This method is used for beginning the animation logic.
     * This method must be used when creating a particle animator.
     * Ideally, the animators should implement their own trimming
     *
     * @throws SeqDuplicateException When it allocates a new sequence but there is already an allocated sequence
     * @throws SeqMissingException   When it finds, there is no sequence yet allocated
     *
     * @param renderer The renderer to use when beggining the animation
     */
    public abstract void beginAnimation(ApelServerRenderer renderer) throws SeqDuplicateException, SeqMissingException;

    /** Calculates the total duration, in ticks, for the path animator
     *
     * @return Animation duration, as an integer
     */
    protected int calculateDuration() {
        int steps = this.getRenderingSteps();
        int speed = this.getProcessingSpeed();
        return this.delay * ((steps == 0 ? this.convertIntervalToSteps() : steps) / speed);
    }

    /**
     * This method is used for drawing the object. It does more than just drawing, primarily scheduling
     *
     * @param renderer     The renderer used for drawing
     * @param step         The current step in
     * @param drawPosition The planned drawing position
     * @throws SeqMissingException When it finds that there is no sequence yet allocated
     */
    public void handleDrawingStep(ApelServerRenderer renderer, int step, Vector3f drawPosition) throws SeqMissingException {
        int steps = this.renderingSteps == 0 ? convertIntervalToSteps() : this.renderingSteps;
        Runnable func = () -> {
            renderer.beforeFrame(step, drawPosition);
            float deltaTickTime = ((ServerWorldAccess) renderer.getServerWorld()).APEL$getDeltaTickTime();
            this.particleObject.doDraw(renderer, step, drawPosition, steps, deltaTickTime, new Vector3f(1));
            renderer.afterFrame(step, drawPosition);
        };
        if (this.delay == 0) {
            func.run();
            return;
        }
        if (this.processingSpeed == 1) {
            Apel.SCHEDULER.allocateNewStep(this, new ScheduledStep(this.delay, new Runnable[]{func}));
            return;
        }
        this.storedFuncsBuffer.add(func);
        if (this.storedFuncsBuffer.size() == this.processingSpeed) {
            Apel.SCHEDULER.allocateNewStep(
                    this, new ScheduledStep(this.delay, this.storedFuncsBuffer.toArray(Runnable[]::new))
            );
            this.storedFuncsBuffer.clear();
        }
    }

    /**
     * Provides a way to construct an int array with a default value in all elements in a single line.
     * @param array an array to receive the default values
     * @param defaultValue the default value to put in every element
     * @return the provided array with all elements defaulted as specified
     */
    protected static int[] defaultedArray(int[] array, int defaultValue) {
        Arrays.fill(array, defaultValue);
        return array;
    }

    /**
     * Provides a way to construct a float array with a default value in all elements in a single line.
     * @param array an array to receive the default values
     * @param defaultValue the default value to put in every element
     * @return the provided array with all elements defaulted as specified
     */
    protected static float[] defaultedArray(float[] array, float defaultValue) {
        Arrays.fill(array, defaultValue);
        return array;
    }

    /** This is the path-animator builder used for setting up a new path-animator instance.
     * It is designed to be more friendly of how you arrange the parameters.
     * Call {@code .builder()} to initiate the builder, once you supplied the parameters
     * then you can call {@code .build()} to create the instance
     *
     * @param <B> The builder type itself
     * @param <T> The path-animator instance that is building
     */
    public static abstract class Builder<B extends Builder<B, T>, T extends PathAnimatorBase<T>> {
        protected ParticleObject<? extends ParticleObject<?>> particleObject;
        protected int delay = 1;
        protected int processingSpeed = 1;
        protected int renderingSteps = 0;
        protected float renderingInterval = 0.0f;
        protected AnimationInterceptor<T> beforeRender = AnimationInterceptor.identity();

        protected RenderCalculationMethod renderCalculationMethod = RenderCalculationMethod.UNSET;

        protected enum RenderCalculationMethod {
            UNSET, RENDERING_STEPS, RENDERING_INTERVAL
        }

        @SuppressWarnings({"unchecked"})
        public final B self() {
            return (B) this;
        }

        /** The particle object in use for the path animator
         *
         * @param particleObject The particle object
         * @return The builder instance
        */
        public final B particleObject(ParticleObject<? extends ParticleObject<?>> particleObject) {
            this.particleObject = particleObject;
            return self();
        }

        /** The delay in use for the path animator
         *
         * @param delay The delay in use
         * @return The builder instance
        */
        public final B delay(int delay) {
            this.delay = delay;
            return self();
        }

        /** The processingSpeed in use for the path animator
         *
         * @param processingSpeed The processingSpeed in use
         * @return The builder instance
        */
        public final B processingSpeed(int processingSpeed) {
            this.processingSpeed = processingSpeed;
            return self();
        }

        /** The renderingSteps in use for the path animator
         *
         * @param renderingSteps The processingSpeed in use
         * @return The builder instance
        */
        public final B renderingSteps(int renderingSteps) {
            if (this.renderCalculationMethod == RenderCalculationMethod.RENDERING_INTERVAL) {
                throw new IllegalStateException("Cannot specify both rendering steps and rendering intervals in the builder");
            }
            this.renderCalculationMethod = RenderCalculationMethod.RENDERING_STEPS;
            this.renderingSteps = renderingSteps;
            return self();
        }

        /** The renderingInterval in use for the path animator
         *
         * @param renderingInterval The processingSpeed in use
         * @return The builder instance
        */
        public final B renderingInterval(float renderingInterval) {
            if (this.renderCalculationMethod == RenderCalculationMethod.RENDERING_STEPS) {
                throw new IllegalStateException("Cannot specify both rendering steps and rendering intervals in the builder");
            }
            this.renderCalculationMethod = RenderCalculationMethod.RENDERING_INTERVAL;
            this.renderingInterval = renderingInterval;
            return self();
        }

        /**
         * Sets the interceptor to run before rendering.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see PathAnimatorBase#setBeforeRender(AnimationInterceptor)
        */
        public final B beforeRender(AnimationInterceptor<T> beforeRender) {
            this.beforeRender = beforeRender;
            return self();
        }

        /** Builds the path animator instance from all the supplied parameters passed to the builder
         *
         * @return The newly created path animator instance
        */
        public abstract T build();
    }
}
