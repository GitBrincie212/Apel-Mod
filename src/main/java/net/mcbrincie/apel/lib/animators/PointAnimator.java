package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.context.AnimationContext;
import org.joml.Vector3f;


/** The most simple path animator out of all. It is used to animate the object in one certain place
 * called the origin, which is in a 3D position (hence the point animator). It has nothing special
 * and is commonly used for simple animations or to make certain objects stay in place
*/
@SuppressWarnings("unused")
public class PointAnimator extends PathAnimatorBase<PointAnimator> {
    protected Vector3f point;

    public static <B extends Builder<B>> Builder<B> builder() { return new Builder<>(); }

    private <B extends Builder<B>> PointAnimator(Builder<B> builder) {
        super(builder);
        this.point = builder.point;
    }

    /**
     * Copy constructor for the point animator. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * point base animator instance with all of its parameters regardless
     * of their visibility (this means protected & private params are copied)
     *
     * @param animator The animator to copy from
     */
    public PointAnimator(PointAnimator animator) {
        super(animator);
        this.point = animator.point;
    }

    /** Gets the origin point. Which is where the particle animation plays at
     *
     * @return The origin point(that is stationary)
    */
    public Vector3f getPoint() {
        return this.point;
    }

    /** Sets the origin point. Which is where the particle animation plays at. Returns
     * the previous origin point that was used
     *
     * @return The previous origin point used
    */
    public Vector3f setPoint(Vector3f point) {
        Vector3f prevPoint = this.point;
        this.point = point;
        return prevPoint;
    }

    @Override
    public int convertIntervalToSteps() {
        return this.renderingSteps;
    }

    @Override
    public void beginAnimation(ApelServerRenderer renderer) throws SeqDuplicateException, SeqMissingException {
        this.allocateToScheduler();
        for (int step = 0; step < this.renderingSteps; step++) {
            Vector3f renderPoint = new Vector3f(this.point);
            AnimationContext animationContext = new AnimationContext(renderer.getServerWorld(), renderPoint, step);
            this.beforeRender.apply(animationContext, this);
            Vector3f actualPoint = animationContext.getPosition();
            this.handleDrawingStep(renderer, step, actualPoint);
        }
    }

    /** This is the point path animator builder used for setting up a new point path animator instance.
     * It is designed to be more friendly of how you arrange the parameters. Call {@code .builder()} to initiate
     * the builder, once you supplied the parameters then you can call {@code .build()} to create the instance
     *
     * @param <B> The builder type itself
    */
    public static class Builder<B extends Builder<B>> extends PathAnimatorBase.Builder<B, PointAnimator> {
        protected Vector3f point = new Vector3f();

        private Builder() {}

        /** The point that the particle object stays anchored to
         *
         * @param point The point's location
         * @return The builder instance
         */
        public B point(Vector3f point) {
            this.point = point;
            return self();
        }

        @Override
        public PointAnimator build() {
            if (this.renderCalculationMethod != RenderCalculationMethod.RENDERING_STEPS) {
                throw new IllegalStateException("Rendering steps must be set");
            }
            return new PointAnimator(this);
        }
    }
}
