package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.AnimationTrimming;
import net.mcbrincie.apel.lib.util.interceptor.context.AnimationContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Predicate;

/** A slightly more complex animator than ellipse animator or linear animator because it deals with an ellipse.
 * The animator basically creates an ellipse, and when animating on it, you specify which angle (IN RADIANS) should
 * be the start and end, to trim some parts. If you wanna fully revolve around the ellipse and end up back at the
 * same point, then you can specify the revolutions it should do by using {@code setRevolutions}. By default, it's
 * set to one revolution, which means it loops the ellipse once
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class EllipseAnimator extends PathAnimatorBase<EllipseAnimator> {
    protected float radius;
    protected float stretch;
    protected Vector3f center;
    protected Vector3f rotation;
    protected int revolutions;
    protected AnimationTrimming<Float> trimming;
    protected boolean clockwise;

    public static <B extends Builder<B>> Builder<B> builder() {
        return new Builder<>();
    }

    private <B extends Builder<B>> EllipseAnimator(Builder<B> builder) {
        super(builder);
        this.center = builder.center;
        this.radius = builder.radius;
        this.stretch = builder.stretch;
        this.rotation = builder.rotation;
        this.revolutions = builder.revolutions;
        this.clockwise = builder.clockwise;
        this.trimming = builder.trimming;
    }

    /**
     * Constructor for the ellipse animator. This constructor is
     * meant to be used in the case that you want to fully copy a new
     * ellipse animator instance with all of its parameters regardless
     * of their visibility (this means protected and private params are copied)
     *
     * @param animator The animator to copy from
    */
    public EllipseAnimator(EllipseAnimator animator) {
        super(animator);
        this.center = animator.center;
        this.radius = animator.radius;
        this.stretch = animator.stretch;
        this.rotation = animator.rotation;
        this.revolutions = animator.revolutions;
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

    /**
     * Sets the animation trimming.  This animator trims based on angle (in radians) such that any steps that occur
     * between the start and end of the trim interval will render.
     * <p>
     * The maximum interval of the AnimationTrimming instance is 2&pi;<sup>-</sup> radians, so it can approach
     * 2&pi;, but will never actually be 2&pi;.  If the trimming interval is greater than 2&pi;, it will be normalized
     * such that its length is &lt; 2&pi;.  If the entire circle should render an object, the default trimming is
     * {@code (0.0, 0.0)} which means "start at 0.0 and traverse the circle until reaching 0.0"; this results in
     * rendering the object at all points on the circle.
     *
     * @return The previous animation trimming
     */
    public AnimationTrimming<Float> setTrimming(AnimationTrimming<Float> trimming) {
        float start = trimming.getStart();
        float end = trimming.getEnd();
        // Ensure start is within [0, TAU)
        while (start < 0) {
            start += (float) Math.TAU;
        }
        while (start >= Math.TAU) {
            start -= (float) Math.TAU;
        }
        // Ensure end is within [0, TAU)
        while (end < 0) {
            end += (float) Math.TAU;
        }
        while (end >= Math.TAU) {
            end -= (float) Math.TAU;
        }
        AnimationTrimming<Float> prevTrimming = this.trimming;
        this.trimming = new AnimationTrimming<>(start, end);
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
    public int convertIntervalToSteps() {
        return (int) (Math.ceil(Math.TAU / this.renderingInterval) + 1) * this.revolutions;
    }

    /**
     * This method is used for beginning the animation logic.
     * It accepts the server world as a parameter. Unlike most
     * path animators, this one uses angles for trimming
     */
    @Override
    public void beginAnimation(ApelServerRenderer renderer) throws SeqMissingException, SeqDuplicateException {
        Predicate<Float> isTrimmed = this.computeTrimmingPredicate();

        int stepsPerRevolution = this.renderingSteps;
        if (this.renderingInterval != 0.0f) {
            stepsPerRevolution = (int) (Math.ceil(Math.TAU / this.renderingInterval) + 1);
        }

        float angleInterval;
        float referenceAngle;
        if (this.clockwise) {
            angleInterval = (float) (Math.TAU / stepsPerRevolution);
            referenceAngle = 0.0f;
        } else {
            angleInterval = -(float) (Math.TAU / stepsPerRevolution);
            referenceAngle = (float) Math.TAU;
        }

        this.allocateToScheduler();
        int step = -1;
        for (int revolutionCount = 0; revolutionCount < this.revolutions; revolutionCount++) {
            for (int i = 0; i < stepsPerRevolution; i++) {
                step++;
                // Compute this way to avoid the awkward i == 0 case
                float currAngle = referenceAngle + i * angleInterval;
                if (isTrimmed.test(currAngle)) {
                    continue;
                }
                Vector3f renderPosition = calculatePoint(currAngle);
                AnimationContext animationContext = new AnimationContext(renderer.getServerWorld(), renderPosition, step);
                this.beforeRender.compute(this, animationContext);
                Vector3f actualPosition = animationContext.getPosition();
                this.handleDrawingStep(renderer, step, actualPosition);
                this.afterRender.compute(this, animationContext);
            }
        }
    }

    private @NotNull Predicate<Float> computeTrimmingPredicate() {
        float startAngle = this.trimming.getStart();
        float endAngle = this.trimming.getEnd();
        if (this.clockwise) {
            if (startAngle < endAngle) {
                return (Float angle) -> angle < startAngle || angle > endAngle;
            }
            return (Float angle) -> angle < startAngle && angle > endAngle;
        }
        if (startAngle > endAngle) {
            return (Float angle) -> angle > startAngle || angle < endAngle;
        }
        return (Float angle) -> angle > startAngle && angle < endAngle;
    }

    private Vector3f calculatePoint(float currAngle) {
        Vector3f pos = new Vector3f(
                this.radius * trigTable.getCosine(currAngle),
                this.stretch * trigTable.getSine(currAngle),
                0
        );
        pos = pos.rotateZ(this.rotation.z).rotateY(this.rotation.y).rotateX(this.rotation.x);
        return pos.add(this.center);
    }

    /** This is the ellipse path-animator builder used for setting up a new ellipse path-animator instance.
     * It is designed to be more friendly of how you arrange the parameters. Call {@code .builder()} to initiate
     * the builder, once you supplied the parameters then you can call {@code .build()} to create the instance
     *
     * @param <B> The builder type itself
    */
    public static class Builder<B extends EllipseAnimator.Builder<B>> extends PathAnimatorBase.Builder<B, EllipseAnimator> {
        protected Vector3f center;
        protected float radius;
        protected float stretch;
        protected Vector3f rotation = new Vector3f();
        protected int revolutions = 1;
        protected boolean clockwise = true;
        protected AnimationTrimming<Float> trimming = new AnimationTrimming<>(0.0f, (float) (Math.TAU - 0.0001f));

        private Builder() {}

        /** The center position of the ellipse
         *
         * @param center The center of the ellipse
         * @return The builder instance
         */
        public B center(Vector3f center) {
            this.center = center;
            return self();
        }

        /** The radius of the ellipse
         *
         * @param radius The radius of the ellipse
         * @return The builder instance
        */
        public B radius(float radius) {
            this.radius = radius;
            return self();
        }

        /** The stretch of the ellipse. If this tends to equal radius, then it becomes a circle
         *
         * @param stretch The stretch of the ellipse
         * @return The builder instance
        */
        public B stretch(float stretch) {
            this.stretch = stretch;
            return self();
        }

        /** The rotation of the ellipse
         *
         * @param rotation The rotation of the ellipse
         * @return The builder instance
        */
        public B rotation(Vector3f rotation) {
            this.rotation = rotation;
            return self();
        }

        /** The revolutions of the ellipse. How many times to loop through the ellipse
         *
         * @param revolutions The revolutions of the ellipse
         * @return The builder instance
        */
        public B revolutions(int revolutions) {
            this.revolutions = revolutions;
            return self();
        }

        /** Sets the particle object to rotate clockwise of the ellipse
         *
         * @return The builder instance
        */
        public B clockwise() {
            this.clockwise = true;
            return self();
        }

        /** Sets the particle object to rotate counter-clockwise of the ellipse
         *
         * @return The builder instance
        */
        public B counterclockwise() {
            this.clockwise = false;
            return self();
        }

        /** Sets the trimming of the ellipse path-animator
         *
         * @param trimming The trimming for the ellipse path-animator
         * @return The builder instance
        */
        public B trimming(AnimationTrimming<Float> trimming) {
            this.trimming = new AnimationTrimming<>(trimming);
            return self();
        }

        @Override
        public EllipseAnimator build() {
            if (this.center == null) {
                throw new IllegalStateException("Center must be provided");
            }
            if (this.radius <= 0.0f) {
                throw new IllegalStateException("Radius must be positive");
            }
            if (this.revolutions <= 0) {
                throw new IllegalStateException("Revolutions must be positive");
            }
            if (this.stretch <= 0.0f) {
                throw new IllegalStateException("Stretch must be positive");
            }
            if (this.renderCalculationMethod == RenderCalculationMethod.UNSET) {
                throw new IllegalStateException("Either rendering steps or rendering interval must be set");
            }
            return new EllipseAnimator(this);
        }
    }
}
