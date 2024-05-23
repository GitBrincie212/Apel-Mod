package net.mcbrincie.apel.lib.animators;

import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.util.scheduler.ScheduledStep;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public abstract class PathAnimatorBase {
    protected float renderingInterval = 0.0f;
    protected int renderingSteps = 0;
    protected int delay;
    protected int processSpeed = 1;
    @NotNull  protected ParticleObject particle;

    protected List<Runnable> storedFuncsBuffer = new ArrayList<>();

    protected Function6<Integer, Integer, Vec3d, Vec3d, Integer, Float, Void> onEnd;
    protected Function5<Integer, Integer, Vec3d, Integer, Float, Void> onStart;
    protected Function6<Integer, Integer, Vec3d, Vec3d, Integer, Float, Void> onProcess;

    public PathAnimatorBase(int delay, @NotNull  ParticleObject particle, int renderingSteps) {
        if (renderingSteps < 0) {
            throw new IllegalArgumentException("Rendering Steps is not positive or equals to 0");
        }
        this.delay = delay;
        this.particle = particle;
        this.renderingSteps = renderingSteps;
    }

    public PathAnimatorBase(int delay, @NotNull  ParticleObject particle, float renderingInterval) {
        if (delay < 0) {
            throw new IllegalArgumentException("Delay is not positive");
        } else if (renderingInterval < 0) {
            throw new IllegalArgumentException("Rendering Interval is not positive or equals to 0");
        }
        this.delay = delay;
        this.particle = particle;
        this.renderingInterval = renderingInterval;
    }

    public PathAnimatorBase(PathAnimatorBase animator) {
        this.delay = animator.delay;
        this.particle = animator.particle;
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
        if (this.delay != 0 && !Apel.apelScheduler.hasAllocated(this)) {
            int steps = this.renderingSteps != 0 ? this.renderingSteps : this.convertToSteps();
            Apel.apelScheduler.allocateNewSequence(this, steps);
        }
    }

    /** Gets the amount of particles. Which can be 0 indicating
     * that there wasn't any amount of particles specified
     *
     * @return The amount of particles
     */
    public int getAmount() {
        return this.renderingSteps;
    }

    /** Gets the interval of blocks per particle object render.
     * Which can be 0 indicating that there wasn't any
     * interval specified
     *
     * @return The interval of blocks per particle object render
     */
    public float getInterval() {
        return this.renderingInterval;
    }


    /** Binds a function to listen to end which happens when the animation
     *  Ends. The function cannot modify anything but exposes some variables
     *  to be read from
     *
     * @param onEnd The function that accepts 6 arguments and returns nothing.
     *                  <strong>start step</strong>, <strong>ending step</strong>,
     *                  <strong>starting position</strong>, <strong>current position</strong>,
     *                  <strong>particle amount</strong>, <strong>interval of blocks</strong>
     */
    public void bindEndAnimListener(
            Function6<Integer, Integer, Vec3d, Vec3d, Integer, Float, Void> onEnd
    ) {
        this.onEnd = onEnd;
    }


    /** Binds a function to listen to start which happens when the animation
     *  Begins. The function cannot modify anything but exposes some variables
     *  to be read from
     *
     * @param onStart The function that accepts 5 arguments and returns nothing.
     *                  <strong>start step</strong>, <strong>ending step</strong>,
     *                  <strong>ending position</strong>, <strong>particle amount</strong>,
     *                  <strong>interval of blocks</strong>
     */
    public void bindStartAnimListener(
            Function5<Integer, Integer, Vec3d, Integer, Float, Void> onStart
    ) {
        this.onStart = onStart;
    }


    /** Binds a function to listen to each step which happens when the animation
     *  is processing the steps. The function cannot modify anything
     *  but exposes some variables to be read from
     *
     * @param onProcess The function that accepts 6 arguments and returns nothing.
     *                  <strong>current step</strong>, <strong>ending step</strong>,
     *                  <strong>current position</strong>, <strong>ending position</strong>,
     *                  <strong>particle amount</strong>, <strong>interval of blocks</strong>
     */
    public void bindProcessAnimListener(
            Function6<Integer, Integer, Vec3d, Vec3d, Integer, Float, Void> onProcess
    ) {
        this.onProcess = onProcess;
    }

    /** Sets the processing speed to allow for even faster animations on larger rendering steps.
     *  This tells the system how many functions for that animator to execute per tick. Which means
     *  that the amount of steps per tick. It has to be above 1 (speed 1 counts normal).
     *  <br><br>
     *  Use this function when you want detailed animations go faster(if delay 1 doesn't satisfy)
     *  <br><br>
     *  <strong>note:</strong> when it is delay 0. Processing speed is completely ignored
     *
     * @param speed The amount of steps to execute per tick
     */
    public void setProcessingSpeed(int speed) {
        if (speed < 1) {
            throw new IllegalArgumentException("Process speed cannot be below 1 rs/st");
        }
        this.processSpeed = speed;
    }


    /** This method is used for beginning the animation logic.
     * Unlike its counterparts, it begins only on the starting
     * position and not at any other specified position
     *
     * @param world The server world instance
     */
    public void beginAnimation(ServerWorld world) throws SeqDuplicateException, SeqMissingException {
        this.beginAnimation(world, 0, -1);
    }


    /** This method is used for beginning the animation logic.
     * The method can accept a trim from the start. Which is
     * measured as steps(not seconds, so delay doesn't affect it)
     *
     * @param world The server world instance
     */
    public void beginAnimation(ServerWorld world, int startStep) throws SeqDuplicateException, SeqMissingException {
        this.beginAnimation(world, startStep, -1);
    }

    /** Does the calculations to convert from an interval to rendering steps
     *
     * @return The amount of steps
     */
    public abstract int convertToSteps();


    /** This method is used for beginning the animation logic.
     * It accepts the server world as well as a predefined current
     * position(from where to start)
     *
     * @param world The server world instance
     * @param startStep The time to begin the animation at. Measured as a step
     * @param endStep The time to end the animation at. Measured as a step
     */
    public abstract void beginAnimation(
            ServerWorld world, int startStep, int endStep
    ) throws SeqDuplicateException, SeqMissingException;

    public void handleDrawingStep(ServerWorld world, int i, Vec3d curr) throws SeqMissingException {
        if (this.delay == 0) {
            this.particle.draw(world, i, curr);
            return;
        }
        Runnable func = () -> this.particle.draw(world, i, curr);
        if (this.processSpeed <= 1) {
            Apel.apelScheduler.allocateNewStep(
                    this, new ScheduledStep(this.delay, new Runnable[]{func})
            );
            return;
        } else if (i % this.processSpeed != 0) {
            this.storedFuncsBuffer.add(func);
            return;
        }
        Apel.apelScheduler.allocateNewStep(
                this, new ScheduledStep(this.delay, this.storedFuncsBuffer.toArray(Runnable[]::new))
        );
        this.storedFuncsBuffer.clear();
    }
}
