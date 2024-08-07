package net.mcbrincie.apel.lib.animators;

import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.math.TrigTable;
import net.mcbrincie.apel.lib.util.scheduler.ScheduledStep;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/** The abstract base class that all path animators inherit from. It
 * does some stuff under the hood and provides useful methods for working
 * with the path animators. The scheduling is abstracted away from the user,
 * so the only thing they need to care is setting the logic
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class PathAnimatorBase {
    protected float renderingInterval = 0.0f;
    protected int renderingSteps = 0;
    protected int delay;
    protected int processSpeed = 1;
    protected ParticleObject particleObject;

    protected List<Runnable> storedFuncsBuffer = new ArrayList<>();

    protected static TrigTable trigTable = Apel.TRIG_TABLE;

    protected Function6<Integer, Integer, Vector3f, Vector3f, Integer, Float, Void> onEnd;
    protected Function5<Integer, Integer, Vector3f, Integer, Float, Void> onStart;
    protected Function6<Integer, Integer, Vector3f, Vector3f, Integer, Float, Void> onProcess;

    /**
     * Constructor for the path animator. This constructor is meant to be used when you want a
     * constant amount of rendering steps. It is worth pointing out that this won't look
     * pleasant in the eyes at larger distances. For that, it is recommended to look into:
     *
     * @param delay          The delay per rendering step
     * @param particleObject The particle object to use
     * @param renderingSteps The rendering steps to use
     * @see PathAnimatorBase#PathAnimatorBase(int, ParticleObject, float)
     */
    public PathAnimatorBase(int delay, ParticleObject particleObject, int renderingSteps) {
        this.setDelay(delay);
        this.setParticleObject(particleObject);
        this.setRenderSteps(renderingSteps);
    }

    /** This is an empty constructor meant as a placeholder */
    public PathAnimatorBase() {
    }

    /**
     * Constructor for the path animator. This constructor is meant to be used when you want a
     * consistent amount of rendering steps no matter the distance. It is worth pointing out that
     * there will be performance overhead for large distances. To minimize, it is recommended to look into:
     *
     * @param delay             The delay per rendering step
     * @param particle          The particle object to use
     * @param renderingInterval The rendering interval to use, which is how many blocks per new rendering step
     * @see PathAnimatorBase#PathAnimatorBase(int, ParticleObject, int)
     */
    public PathAnimatorBase(int delay, @NotNull ParticleObject particle, float renderingInterval) {
        this.setDelay(delay);
        this.setParticleObject(particle);
        this.setRenderInterval(renderingInterval);
    }

    /**
     * Constructor for the path base animator. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * path base animator instance with all of its parameters regardless
     * of their visibility (this means protected & private params are copied)
     *
     * @param animator The animator to copy from
    */
    public PathAnimatorBase(PathAnimatorBase animator) {
        this.delay = animator.delay;
        this.particleObject = animator.particleObject;
        this.renderingInterval = animator.renderingInterval;
        this.renderingSteps = animator.renderingSteps;
        this.processSpeed = animator.processSpeed;
        this.onEnd = animator.onEnd;
        this.onStart = animator.onStart;
        this.onProcess = animator.onProcess;
        this.storedFuncsBuffer = new ArrayList<>();
    }

    /** Simplifies the process of scheduling a new sequence in the scheduler.
     *  Instead of checking if the delay isn't 0 and that there is no already allocated
     *  sequence. The method does that for your convenience
     */
    public void allocateToScheduler() {
        if (this.delay == 0) return;
        int steps = this.renderingSteps != 0 ? this.scheduleGetAmount() : this.convertToSteps();
        Apel.SCHEDULER.allocateNewSequence(this, steps);
    }

    protected int scheduleGetAmount() {
        return this.renderingSteps;
    }

    /** Gets the amount of rendering steps, which can be zero indicating
     * that there weren't any rendering steps specified
     *
     * @return The number of particles
     */
    public int getRenderSteps() {
        return this.renderingSteps;
    }

    /** Gets the interval of blocks per particle object render.
     * Which can be zero indicating that there wasn't any
     * interval specified
     *
     * @return The interval of blocks per particle object render
     */
    public float getRenderInterval() {
        return this.renderingInterval;
    }

    /** Gets the delay per rendering step, this can be zero indicating
     * that the animation plays in an instant; the amount of delay is
     * also controlled via processing speed
     *
     * @see PathAnimatorBase#setProcessingSpeed(int)
     * @return The amount of rendering steps
     */
    public int getDelay() {
        return this.renderingSteps;
    }

    /** Sets the delay per rendering step to a new value. And
     * returns the previous delay per rendering step used
     *
     * @see PathAnimatorBase#setProcessingSpeed(int)
     * @return The previous amount of rendering steps
     */
    public int setDelay(int delay) {
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
    public ParticleObject getParticleObject() {
        return this.particleObject;
    }

    /** Sets the particle object to a new value. And returns the
     * previous particle object used in the animator
     *
     * @return The previous amount of rendering steps
     */
    public ParticleObject setParticleObject(@NotNull ParticleObject object) {
        ParticleObject particleObject = this.particleObject;
        this.particleObject = object;
        return particleObject;
    }

    /** Sets the rendering interval to a new value. It resets the rendering steps
     *  to 0. And returns the previous rendering interval used
     *
     * @param interval The new rendering interval to set
     * @return The previous rendering interval
    */
    public float setRenderInterval(float interval) {
        if (interval < 0) {
            throw new IllegalArgumentException("Rendering Interval is not positive or equals to 0");
        }
        float prevInterval = this.renderingInterval;
        this.renderingInterval = interval;
        this.renderingSteps = 0;
        return prevInterval;
    }

    /** Sets the rendering steps to a new value. It resets the rendering interval
     *  to 0.0f. And returns the previous rendering steps used
     *
     * @param steps The new rendering steps to set
     * @return The previous rendering interval
    */
    public int setRenderSteps(int steps) {
        if (steps < 0) {
            throw new IllegalArgumentException("Rendering Steps is not positive or equals to 0");
        }
        int prevRenderStep = this.renderingSteps;
        this.renderingSteps = steps;
        this.renderingInterval = 0.0f;
        return prevRenderStep;
    }


    /** Binds a function to listen to end which happens when the animation
     *  Ends. The function cannot modify anything but exposes some variables
     *  to be read from
     *
     * @param onEnd The function that accepts six arguments and returns nothing.
     *                  <strong>start step</strong>, <strong>ending step</strong>,
     *                  <strong>starting position</strong>, <strong>current position</strong>,
     *                  <strong>particle amount</strong>, <strong>interval of blocks</strong>
     */
    public void bindEndAnimListener(
            Function6<Integer, Integer, Vector3f, Vector3f, Integer, Float, Void> onEnd
    ) {
        this.onEnd = onEnd;
    }


    /** Binds a function to listen to start which happens when the animation
     *  Begins. The function cannot modify anything but exposes some variables
     *  to be read from
     *
     * @param onStart The function that accepts five arguments and returns nothing.
     *                  <strong>start step</strong>, <strong>ending step</strong>,
     *                  <strong>ending position</strong>, <strong>particle amount</strong>,
     *                  <strong>interval of blocks</strong>
     */
    public void bindStartAnimListener(
            Function5<Integer, Integer, Vector3f, Integer, Float, Void> onStart
    ) {
        this.onStart = onStart;
    }


    /** Binds a function to listen to each step that happens when the animation
     *  is processing the steps. The function cannot modify anything
     *  but exposes some variables to be read from
     *
     * @param onProcess The function that accepts six arguments and returns nothing.
     *                  <strong>current step</strong>, <strong>ending step</strong>,
     *                  <strong>current position</strong>, <strong>ending position</strong>,
     *                  <strong>particle amount</strong>, <strong>interval of blocks</strong>
     */
    public void bindProcessAnimListener(
            Function6<Integer, Integer, Vector3f, Vector3f, Integer, Float, Void> onProcess
    ) {
        this.onProcess = onProcess;
    }

    /** Sets the processing speed to allow for even faster animations on larger rendering steps.
     *  This tells the system how many functions for that animator to execute per tick. Which means
     *  that the number of steps per tick. It has to be above 1 (speed 1 counts normal). The speed is
     *  measured in render steps / server ticks or rs/st. It returns the previous processing speed used
     *  <br><br>
     *  Use this function when you want detailed animations to want to go faster (if delay 1 doesn't satisfy)
     *  <br><br>
     *  <strong>note:</strong> when it is delay 0. Processing speed is completely ignored
     *
     * @see PathAnimatorBase#setDelay(int)
     * @param speed The number of steps to execute per tick
     * @return The previous processing speed used
     */
    public int setProcessingSpeed(int speed) {
        if (speed < 1) {
            throw new IllegalArgumentException("Process speed cannot be below 1 rs/st");
        }
        int prevProcessSpeed = this.processSpeed;
        this.processSpeed = speed;
        return prevProcessSpeed;
    }

    /** Gets the processing speed. Which is measured in rs/st and dictates how many functions
     * to execute per rendering step. By default, it is set to 1 rs/st
     *
     * @see PathAnimatorBase#getDelay()
     * @return The processing speed used
     */
    public int getProcessingSpeed() {
        return this.processSpeed;
    }

    /** Does the calculations to convert from an interval to rendering steps
     *
     * @return The number of steps
     */
    public abstract int convertToSteps();

    /**
     * This method is used for beginning the animation logic.
     * This method must be used when creating a particle animator.
     * Ideally, the animators should implement their own trimming
     *
     * @throws SeqDuplicateException When it allocates a new sequence but there is already an allocated sequence
     * @throws SeqMissingException   When it finds, there is no sequence yet allocated
     */
    public abstract void beginAnimation(ApelServerRenderer renderer) throws SeqDuplicateException, SeqMissingException;

    /** Calculates the total delay for the path animator
     *
     * @return The delay as an integer
     */
    protected int calculateDuration() {
        int steps = this.getRenderSteps();
        int speed = this.getProcessingSpeed();
        return this.delay * ((steps == 0 ? this.convertToSteps() : steps) / speed);
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
        Runnable func = () -> {
            renderer.beforeFrame(step, drawPosition);
            this.particleObject.draw(renderer, step, drawPosition);
            renderer.afterFrame(step, drawPosition);
        };
        if (this.delay == 0) {
            Apel.DRAW_EXECUTOR.submit(func);
            return;
        }
        if (this.processSpeed <= 1) {
            Apel.SCHEDULER.allocateNewStep(
                    this, new ScheduledStep(this.delay, new Runnable[]{func})
            );
            return;
        } else if (step % this.processSpeed != 0) {
            this.storedFuncsBuffer.add(func);
            return;
        }
        Apel.SCHEDULER.allocateNewStep(
                this, new ScheduledStep(this.delay, this.storedFuncsBuffer.toArray(Runnable[]::new))
        );
        this.storedFuncsBuffer.clear();
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
}
