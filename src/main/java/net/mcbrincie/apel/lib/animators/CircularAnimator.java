package net.mcbrincie.apel.lib.animators;

import com.mojang.datafixers.util.Function6;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.renderers.ApelRenderer;
import net.mcbrincie.apel.lib.util.AnimationTrimming;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/** A slightly more complex animator than linear animator or point animator because it deals with a circle.
 * The animator basically creates a circle, and when animating on it, you specify which angle (IN RADIANS) should
 * be the start & end, to trim some parts. If you wanna fully revolve around the circle and end up back at the
 * same point, then you can specify the revolutions it should do by using {@code setRevolutions}. By default, it's
 * set to one revolution, which means it loops the circle once
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class CircularAnimator extends PathAnimatorBase {
    protected float radius;
    protected Vector3f center;
    protected Vector3f rotation;
    protected int revolutions = 1;
    protected AnimationTrimming<Float> trimming = new AnimationTrimming<>(0.0f, (float) (Math.TAU - 0.0001f));
    protected boolean clockwise;

    private float tempDiffStore;

    protected Function6<AnimationTrimming<Float>, Vector3f, Float, Vector3f, Integer, Float, Void> onEnd;
    protected Function6<AnimationTrimming<Float>, Vector3f, Float, Vector3f, Integer, Float, Void> onStart;
    protected Function6<Integer, AnimationTrimming<Float>, Float, Vector3f, Integer, Float, Void> onProcess;

    /**
     * Constructor for the circular animation. This constructor is
     * meant to be used in the case that you want a constant number
     * of particles. It doesn't look pretty at large distances tho
     *
     * @param delay The delay between each particle object render
     * @param radius The radius of the 2D circle
     * @param center The center point of the 2D circle
     * @param rotation the rotation in XYZ of the 2D circle<strong>(IN RADIANS)</strong>
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public CircularAnimator(
            int delay, float radius, @NotNull  Vector3f center, @NotNull Vector3f rotation,
            @NotNull ParticleObject particle, int renderingSteps
    ) {
        super(delay, particle, renderingSteps);
        this.setRadius(radius);
        this.setCenter(center);
        this.rotate(rotation.x, rotation.y, rotation.z);
    }

    /**
     * Constructor for the circular animation. This constructor is
     * meant to be used in the case that you want a good consistent
     * looking particle circle. The amount is dynamic that can cause
     * performance issues for larger distances (The higher the interval,
     * the fewer particles are rendered, and it is also applied vice versa)
     *
     * @param delay The delay between each particle object render
     * @param radius The radius of the 2D circle
     * @param center The center point of the 2D circle
     * @param rotation the rotation in XYZ of the 2D circle<strong>(IN RADIANS)</strong>
     * @param particle The particle to use
     * @param renderingInterval The number of blocks before placing a new render step
     */
    public CircularAnimator(
            int delay, float radius, @NotNull  Vector3f center, @NotNull Vector3f rotation,
            @NotNull ParticleObject particle, float renderingInterval
    ) {
        super(delay, particle, renderingInterval);
        this.setRadius(radius);
        this.setCenter(center);
        this.rotate(rotation.x, rotation.y, rotation.z);
    }

    /**
     * Constructor for the circular animator. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * circular animator instance with all of its parameters regardless
     * of their visibility (this means protected & private params are copied)
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
        this.clockwise = animator.clockwise;
        this.trimming = animator.trimming;
    }


    /** Rotates the animator in 3D space. The params are measured as radians
     * and NOT in degrees. It uses euler's 3D rotation
     *
     * @param x The x coordinates
     * @param y The y coordinates
     * @param z The z coordinates
     */
    public void rotate(float x, float y, float z) {
        this.rotation = new Vector3f(x, y, z);
    }

    /** Sets the revolutions (looping around the circle)
     * the animator can do around the shape when animating
     *
     * @param revolutions The number of loops to do on a shape
    */
    public void setRevolutions(int revolutions) {
        if (revolutions < 1) {
            throw new IllegalArgumentException("Revolutions cannot be below 1");
        }
        this.revolutions = revolutions;
    }

    /** Sets the center position
     *
     * @param center The new center position
     */
    public void setCenter(Vector3f center) {
        this.center = center;
    }

    public Vector3f getCenter() {
        return this.center;
    }

    /** Sets the radius
     *
     * @param radius The new radius
     */
    public void setRadius(float radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius cannot be below or equal to 0");
        }
        this.radius = radius;
    }

    /** Gets the radius
     *
     * @return The radius
     */
    public float getRadius() {
        return this.radius;
    }

    /** Sets the animation trimming which accepts a start trim or
     * an ending trim. The trim parts have to be float values
     *
     * @return The animation trimming that is used
     */
    public AnimationTrimming<Float> setTrimming(AnimationTrimming<Float> trimming) {
        trimming.setStart((float) (trimming.getStart() % Math.TAU));
        trimming.setEnd((float) (trimming.getEnd() % Math.TAU));
        AnimationTrimming<Float> prevTrimming = this.trimming;
        this.trimming = trimming;
        return prevTrimming;
    }

    /** Gets the animation trimming that is used
     *
     * @return The animation trimming that is used
     */
    public AnimationTrimming<Float> getTrimming() {
        return this.trimming;
    }

    /** Sets the clockwise value that is used
     *
     * @return The previous clockwise value
     */
    public boolean setClockwise() {
        this.clockwise = !this.clockwise;
        return !this.clockwise;
    }

    /** Gets the clockwise value
     *
     * @return The clockwise value
     */
    public boolean getClockwise() {
        return this.clockwise;
    }

    /** Converts any render-interval-based animation into render steps
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

    /**
     * This method is used for beginning the animation logic.
     * It accepts the server world as a parameter. Unlike most
     * path animators, this one uses angles for trimming
     */
    @Override
    public void beginAnimation(ApelRenderer renderer) throws SeqMissingException, SeqDuplicateException {
        float startAngle = this.trimming.getStart();
        float differenceAngle = this.trimming.getEnd() - startAngle;
        this.tempDiffStore = differenceAngle;

        int particleAmount = this.renderingSteps == 0 ? this.convertToSteps() : this.renderingSteps * this.revolutions;
        float angleInterval = this.renderingInterval == 0 ? (
                ((differenceAngle) / (this.renderingSteps - 1)) * this.revolutions
        ): this.renderingInterval * this.revolutions;

        float currAngle = startAngle;
        Vector3f pos = calculatePoint(currAngle);
        if (this.onStart != null) {
            this.onStart.apply(
                    this.trimming, pos, this.radius,
                    this.center, this.renderingSteps, this.renderingInterval
            );
        }
        this.allocateToScheduler();
        for (int i = 0; i < particleAmount ; i++) {
            this.handleDrawingStep(renderer, i, pos);
            if (this.onProcess != null) {
                this.onProcess.apply(
                        i, this.trimming, this.radius, this.center, this.renderingSteps, this.renderingInterval
                );
            }
            currAngle += this.clockwise ? angleInterval : -angleInterval;
            currAngle = (float) ((currAngle + Math.TAU) % Math.TAU);
            pos = this.calculatePoint(currAngle);
        }

        if (this.onEnd != null) {
            this.onEnd.apply(
                    this.trimming, pos, this.radius, this.center, this.renderingSteps, this.renderingInterval
            );
        }
    }

    private Vector3f calculatePoint(float currAngle) {
        Vector3f pos = new Vector3f(
                this.radius * trigTable.getCosine(currAngle),
                this.radius * trigTable.getSine(currAngle),
                0
        );
        pos = pos
                .rotateZ(this.rotation.z)
                .rotateY(this.rotation.y)
                .rotateX(this.rotation.x);
        return pos.add(this.center);
    }
}
