package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.AnimationTrimming;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Optional;

/** A slightly more complex animator than ellipse animator or linear animator because it deals with an ellipse.
 * The animator basically creates an ellipse, and when animating on it, you specify which angle (IN RADIANS) should
 * be the start & end, to trim some parts. If you wanna fully revolve around the ellipse and end up back at the
 * same point, then you can specify the revolutions it should do by using {@code setRevolutions}. By default, it's
 * set to one revolution, which means it loops the ellipse once
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class EllipseAnimator extends PathAnimatorBase {
    protected float radius;
    protected Vector3f center;
    protected Vector3f rotation;
    protected int revolutions = 1;
    protected AnimationTrimming<Float> trimming = new AnimationTrimming<>(0.0f, (float) (Math.TAU - 0.0001f));
    protected boolean clockwise;
    protected float stretch;

    private float tempDiffStore;
    private DrawInterceptor<EllipseAnimator, OnRenderStep> duringRenderingSteps = DrawInterceptor.identity();

    public enum OnRenderStep {SHOULD_DRAW_STEP, RENDERING_POSITION}

    /**
     * Constructor for the ellipse animation. This constructor is
     * meant to be used in the case that you want a constant number
     * of particles. It doesn't look pretty at large distances tho
     *
     * @param delay The delay between each particle object render
     * @param radius The radius of the 2D ellipse
     * @param center The center point of the 2D ellipse
     * @param rotation the rotation in XYZ of the 2D ellipse<strong>(IN RADIANS)</strong>
     * @param stretch the stretch factor of the ellipse
     * @param particle The particle to use
     * @param renderingSteps The amount of rendering steps for the animation
     */
    public EllipseAnimator(
            int delay, float radius, @NotNull  Vector3f center, @NotNull Vector3f rotation,
            float stretch, @NotNull ParticleObject<? extends ParticleObject<?>> particle, int renderingSteps
    ) {
        super(delay, particle, renderingSteps);
        this.setRadius(radius);
        this.setCenter(center);
        this.rotate(rotation.x, rotation.y, rotation.z);
        this.setStretch(stretch);
    }

    /**
     * Constructor for the ellipse animation. This constructor is
     * meant to be used in the case that you want a good consistent
     * looking particle ellipse. The amount is dynamic that can cause
     * performance issues for larger distances (The higher the interval,
     * the fewer particles are rendered, and it is also applied vice versa)
     *
     * @param delay The delay between each particle object render
     * @param radius The radius of the 2D ellipse
     * @param center The center point of the 2D ellipse
     * @param rotation the rotation in XYZ of the 2D ellipse<strong>(IN RADIANS)</strong>
     * @param stretch the stretch factor of the ellipse
     * @param particle The particle to use
     * @param renderingInterval The number of blocks before placing a new render step
     */
    public EllipseAnimator(
            int delay, float radius, @NotNull  Vector3f center, @NotNull Vector3f rotation,
            float stretch, @NotNull ParticleObject<? extends ParticleObject<?>> particle, float renderingInterval
    ) {
        super(delay, particle, renderingInterval);
        this.setRadius(radius);
        this.setCenter(center);
        this.rotate(rotation.x, rotation.y, rotation.z);
        this.setStretch(stretch);
    }

    /**
     * Constructor for the ellipse animator. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * ellipse animator instance with all of its parameters regardless
     * of their visibility (this means protected & private params are copied)
     *
     * @param animator The animator to copy from
    */
    public EllipseAnimator(EllipseAnimator animator) {
        super(animator);
        this.rotation = animator.rotation;
        this.center = animator.center;
        this.radius = animator.radius;
        this.stretch = animator.stretch;
        this.tempDiffStore = animator.tempDiffStore;
        this.revolutions = animator.revolutions;
        this.duringRenderingSteps = animator.duringRenderingSteps;
        this.clockwise = animator.clockwise;
        this.trimming = animator.trimming;
    }

    /** Gets the stretch of the ellipse
     *
     * @return The stretch
     */
    public float getStretch() {
        return this.stretch;
    }

    /** Sets the stretch of the ellipse
     *
     * @param stretch the new stretch value
    */
    public void setStretch(float stretch) {
        if (stretch <= 0) {
            throw new IllegalArgumentException("Stretch must be greater than 0");
        }
        this.stretch = stretch;
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

    /** Sets the revolutions (looping around the ellipse)
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
    public void beginAnimation(ApelServerRenderer renderer) throws SeqMissingException, SeqDuplicateException {
        float startAngle = this.trimming.getStart();
        float differenceAngle = this.trimming.getEnd() - startAngle;
        this.tempDiffStore = differenceAngle;

        int particleAmount = this.renderingSteps == 0 ? this.convertToSteps() : this.renderingSteps * this.revolutions;
        float angleInterval = this.renderingInterval == 0 ? (
                ((differenceAngle) / (this.renderingSteps - 1)) * this.revolutions
        ): this.renderingInterval * this.revolutions;

        float currAngle = startAngle;
        Vector3f pos = calculatePoint(currAngle);
        this.allocateToScheduler();
        for (int i = 0; i < particleAmount; i++) {
            InterceptData<OnRenderStep> interceptData = this.doBeforeStep(renderer.getServerWorld(), pos, i);
            if (!((boolean) interceptData.getMetadata(OnRenderStep.SHOULD_DRAW_STEP))) continue;
            pos = (Vector3f) interceptData.getMetadata(OnRenderStep.RENDERING_POSITION);
            this.handleDrawingStep(renderer, i, pos);
            currAngle += this.clockwise ? angleInterval : -angleInterval;
            currAngle = (float) ((currAngle + Math.TAU) % Math.TAU);
            pos = this.calculatePoint(currAngle);
        }
    }

    /** Set the interceptor to run before the drawing of each individual rendering step. The interceptor will be provided
     * with references to the {@link ServerWorld}, the current step number. As far as it goes for metadata,
     * there will be a boolean value that dictates if it should draw on this step and the rendering position of the
     * point that lives in the ellipse
     *
     * @param duringRenderingSteps the new interceptor to execute before drawing the individual steps
     */
    public void setDuringRenderingSteps(DrawInterceptor<EllipseAnimator, OnRenderStep> duringRenderingSteps) {
        this.duringRenderingSteps = Optional.ofNullable(duringRenderingSteps).orElse(DrawInterceptor.identity());
    }

    private Vector3f calculatePoint(float currAngle) {
        Vector3f pos = new Vector3f(
                this.stretch * trigTable.getCosine(currAngle),
                this.radius * trigTable.getSine(currAngle),
                0
        );
        pos = pos
                .rotateZ(this.rotation.z)
                .rotateY(this.rotation.y)
                .rotateX(this.rotation.x);
        return pos.add(this.center);
    }

    protected InterceptData<OnRenderStep> doBeforeStep(
            ServerWorld world, Vector3f position, int currStep
    ) {
        InterceptData<OnRenderStep> interceptData = new InterceptData<>(
                world, null, currStep, OnRenderStep.class
        );
        interceptData.addMetadata(OnRenderStep.RENDERING_POSITION, position);
        interceptData.addMetadata(OnRenderStep.SHOULD_DRAW_STEP, true);
        this.duringRenderingSteps.apply(interceptData, this);
        return interceptData;
    }
}
