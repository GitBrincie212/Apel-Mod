package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.AnimationTrimming;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/** A slightly more complex animator than ellipse animator or linear animator because it deals with an ellipse.
 * The animator basically creates an ellipse, and when animating on it, you specify which angle (IN RADIANS) should
 * be the start & end, to trim some parts. If you wanna fully revolve around the ellipse and end up back at the
 * same point, then you can specify the revolutions it should do by using {@code setRevolutions}. By default, it's
 * set to one revolution, which means it loops the ellipse once
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class EllipseAnimator extends CircularAnimator {
    protected float stretch;
    protected AnimationTrimming<Float> trimming = new AnimationTrimming<>(0.0f, (float) (Math.TAU - 0.0001f));

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
            float stretch, @NotNull ParticleObject particle, int renderingSteps
    ) {
        super(delay, radius, center, rotation, particle, renderingSteps);
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
            float stretch, @NotNull ParticleObject particle, float renderingInterval
    ) {
        super(delay, radius, center, rotation, particle, renderingInterval);
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

    /**
     * This method is used for beginning the animation logic.
     * It accepts the server world as a parameter. Unlike most
     * path animators, this one uses angles for trimming
     */
    @Override
    public void beginAnimation(ApelServerRenderer renderer) throws SeqMissingException, SeqDuplicateException {
        float startAngle = this.trimming.getStart();
        float differenceAngle = this.trimming.getEnd() - startAngle;

        int particleAmount = this.renderingSteps == 0 ? this.convertToSteps() : this.renderingSteps * this.revolutions;
        float angleInterval = this.renderingInterval == 0 ? (
                ((differenceAngle) / (this.renderingSteps - 1)) * this.revolutions
        ): this.renderingInterval * this.revolutions;

        float currAngle = startAngle;
        Vector3f pos = calculatePoint(currAngle);
        this.allocateToScheduler();
        for (int i = 0; i < particleAmount ; i++) {
            InterceptData<onRenderingStep> interceptData = this.doBeforeStep(renderer.getServerWorld(), pos, i);
            if (!((boolean) interceptData.getMetadata(onRenderingStep.SHOULD_DRAW_STEP))) continue;
            pos = (Vector3f) interceptData.getMetadata(onRenderingStep.RENDERING_POSITION);
            this.handleDrawingStep(renderer, i, pos);
            currAngle += this.clockwise ? angleInterval : -angleInterval;
            currAngle = (float) ((currAngle + Math.TAU) % Math.TAU);
            pos = this.calculatePoint(currAngle);
        }
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
}
