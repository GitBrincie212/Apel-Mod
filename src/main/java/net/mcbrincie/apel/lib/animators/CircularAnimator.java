package net.mcbrincie.apel.lib.animators;

import com.mojang.datafixers.util.Function7;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

/** A slightly more complex animator than linear animator or point animator because it deals with a circle.
 * The animator basically creates a circle and when animating on it, you specify which angle(IN RADIANS) should
 * be the start & end, to trim some parts. If you wanna fully revolve around the circle and end up back at the
 * same point then you can specify the revolutions it should do by using {@code setRevolutions}. By default, it's
 * set to 1 revolution which means it loops the circle once
 *
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class CircularAnimator extends PathAnimatorBase {
    protected double radius;
    protected Vec3d center;
    protected Vec3d rotation;
    protected int revolutions = 1;
    private float tempDiffStore;

    protected Function7<Float, Float, Vec3d, Double, Vec3d, Integer, Float, Void> onEnd;
    protected Function7<Float, Float, Vec3d, Double, Vec3d, Integer, Float, Void> onStart;
    protected Function7<Integer, Float, Float, Double, Vec3d, Integer, Float, Void> onProcess;

    /**
     * Constructor for the circular animation. This constructor is
     * meant to be used in the case that you want a constant amount
     * of particles. It doesn't look pretty on large distances tho
     *
     * @param delay The delay between each particle object render
     * @param radius The radius of the 2D circle
     * @param center The center point of the 2D circle
     * @param rotation the rotation in XYZ of the 2D circle<strong>(IN RADIANS)</strong>
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public CircularAnimator(
            int delay, double radius, @NotNull  Vec3d center, @NotNull Vec3d rotation,
            @NotNull ParticleObject particle, int renderingSteps
    ) {
        super(delay, particle, renderingSteps);
        this.radius = radius;
        this.center = center;
        this.rotation = rotation;
    }

    /**
     * Constructor for the circular animation. This constructor is
     * meant to be used in the case that you want a good consistent
     * looking particle circle. The amount is dynamic which can cause
     * performance issues for larger distances(The higher the interval
     * the fewer particles are rendered, and it is also applied vice versa)
     *
     * @param delay The delay between each particle object render
     * @param radius The radius of the 2D circle
     * @param center The center point of the 2D circle
     * @param rotation the rotation in XYZ of the 2D circle<strong>(IN RADIANS)</strong>
     * @param particle The particle to use
     * @param renderingInterval The amount of blocks before placing a new render step
     */
    public CircularAnimator(
            int delay, double radius, @NotNull  Vec3d center, @NotNull Vec3d rotation,
            @NotNull ParticleObject particle, float renderingInterval
    ) {
        super(delay, particle, renderingInterval);
        this.radius = radius;
        this.center = center;
        this.rotation = rotation;
    }

    /**
     * Constructor for the circular animator. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * circular animator instance with all of its parameters regardless
     * of their visibility(this means protected & private params are copied)
     *
     * @param animator The animator to copy from
    */
    public CircularAnimator(CircularAnimator animator) {
        super(animator);
        this.rotation = animator.rotation;
        this.center = animator.center;
        this.radius = animator.radius;
        this.revolutions = animator.revolutions;
        this.tempDiffStore = -1.0f;
        this.onStart = animator.onStart;
        this.onEnd = animator.onEnd;
        this.onProcess = animator.onProcess;
    }


    /** Rotates the animator in 3D space. The params are measured as radians
     * and NOT in degrees. It uses euler's 3D rotation
     *
     * @param x The x coordinates
     * @param y The y coordinates
     * @param z The z coordinates
     */
    public void rotate(double x, double y, double z) {
        this.rotation = new Vec3d(x, y, z);
    }

    /** Sets the revolutions(looping around the circle)
     * the animator can do around the circle when animating
     *
     * @param revolutions The amount of loops to do on a circle
    */
    public void setRevolutions(int revolutions) {
        if (revolutions < 1) {
            throw new IllegalArgumentException("Revolutions cannot be below 1");
        }
        this.revolutions = revolutions;
    }


    /** This method is used for beginning the animation logic.
     * Unlike its counterparts, it begins only on the starting
     * position and not at any other specified position
     *
     * @param world The server world instance
     */
    @Override
    public void beginAnimation(ServerWorld world) throws SeqMissingException, SeqDuplicateException {
        this.beginAnimation(world, 0.0f, (float) (Math.TAU - 0.000001f), true);
    }


    /** This method is used for beginning the animation logic.
     * The method can accept a trim from the start. Which is
     * measured as steps(not seconds, so delay doesn't affect it)
     *
     * @param world The server world instance
     */
    public void beginAnimation(
            ServerWorld world, int startAngle
    ) throws SeqMissingException, SeqDuplicateException {
        this.beginAnimation(world, (float) startAngle, (float) (Math.TAU- 0.000001f), true);
    }

    /** Converts any render interval based animation into render steps
     *
     * @return The converted step
     */
    @Override
    public int convertToSteps() {
        return (int) (Math.ceil(this.tempDiffStore / this.renderingInterval) + 1) * this.revolutions;
    }

    @Override
    protected int scheduleGetAmount() {
        return this.renderingSteps * this.revolutions;
    }

    /** This method is used for beginning the animation logic.
     * It accepts the server world as well as a predefined current
     * position(from where to start)
     *
     * @param world The server world instance
     * @param startAngle The time to begin the animation at. Measured as a step
     * @param endAngle The time to end the animation at. Measured as a step
     */
    @Override
    public void beginAnimation(
            ServerWorld world, int startAngle, int endAngle
    ) throws SeqMissingException, SeqDuplicateException {
        this.beginAnimation(world, (float) startAngle, (float) endAngle, true);
    }


    public void beginAnimation(
            ServerWorld world, float startAngle, float endAngle, boolean clockwise
    ) throws SeqMissingException, SeqDuplicateException {
        startAngle = (float) (startAngle % Math.TAU);
        endAngle = (float) (endAngle % Math.TAU);
        float differenceAngle = endAngle - startAngle;
        this.tempDiffStore = differenceAngle;

        int particleAmount = this.renderingSteps == 0 ? this.convertToSteps() : this.renderingSteps * this.revolutions;
        float angleInterval = this.renderingInterval == 0 ? (
                ((differenceAngle) / (this.renderingSteps - 1)) * this.revolutions
        ): this.renderingInterval * this.revolutions;

        float currAngle = startAngle;
        Vec3d pos = calculatePoint(currAngle);
        if (this.onStart != null) {
            this.onStart.apply(
                    startAngle, endAngle, pos, this.radius,
                    this.center, this.renderingSteps, this.renderingInterval
            );
        }
        this.allocateToScheduler();
        for (int i = 0; i < particleAmount ; i++) {
            this.handleDrawingStep(world, i, pos);
            if (this.onProcess != null) {
                this.onProcess.apply(
                        i, currAngle, endAngle, this.radius, this.center, this.renderingSteps, this.renderingInterval
                );
            }
            currAngle += clockwise ? angleInterval : -angleInterval;
            currAngle = (float) (currAngle % Math.TAU);
            pos = this.calculatePoint(currAngle);
        }

        if (this.onEnd != null) {
            this.onEnd.apply(
                    startAngle, endAngle, pos, this.radius, this.center, this.renderingSteps, this.renderingInterval
            );
        }
    }

    private Vec3d calculatePoint(float currAngle) {
        if (this.radius <= 0) throw new IllegalArgumentException("Radius cannot be negative or 0");

        Vec3d pos = new Vec3d(
                this.radius * Math.cos(currAngle),
                this.radius * Math.sin(currAngle),
                0
        );
        pos = pos
                .rotateZ((float) this.rotation.z)
                .rotateY((float) this.rotation.y)
                .rotateX((float) this.rotation.x);
        return pos.add(this.center);
    }
}
