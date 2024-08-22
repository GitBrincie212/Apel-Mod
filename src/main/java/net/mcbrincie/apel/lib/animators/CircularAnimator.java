package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.AnimationTrimming;
import net.mcbrincie.apel.lib.util.interceptor.OldInterceptors;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * The {@code CircularAnimator} moves the given {@link ParticleObject} along a circular path.  This path is defined
 * by a center point and a radius that describe a circle in the XY-plane.  The path starts at 2&pi; radians and proceeds
 * clockwise by default.  It makes one revolution, by default, though {@link #setRevolutions(int)} may be used to do
 * more revolutions if desired.  Potential transformations include rotation, direction, and trimming.
 * <p>
 * Rotation is done by providing a {@link Vector3f} indicating rotations about each axis.  For example, putting the
 * circle in the XZ-plane can be done by passing {@code new Vector3f((float)Math.PI / 2, 0, 0)} to the constructor's
 * {@code rotation} parameter.
 * <p>
 * Directionality is controlled by {@link #setClockwise()}.  This toggles the direction, so each call reverses
 * direction, with the initial value of {@code true} indicating clockwise.
 * <p>
 * Trimming is done by providing a single arc on the circle, in radians, where the object will be rendered.  The start
 * and end points will be normalized within the interval [0, 2&pi;) such that the arc length between them is 2&pi; or
 * less.  By default, the entire circle is rendered because the trimming parameters are equal, and this class
 * interprets that as drawing all particles from the start point to the end point.
 * <p>
 * For example, calling {@link #setTrimming(AnimationTrimming)} with {@code new AnimationTrimming<>(0.0f, (float)Math.PI)}
 * would render on the second half of the circle if the direction is counter-clockwise because 0 == 2&pi; on the circle,
 * and the first half if the direction is clockwise.  The code for the first is:
 * <pre>
 * CircularAnimator.builder().particleObject(particleObject).center(new Vector3f()).radius(2.0f)
 *                 .counterclockwise().trimming(new AnimationTrimming<>(0.0f, (float)Math.PI))
 *                 .build();
 * </pre>
 * and the second is:
 * <pre>
 * CircularAnimator.builder().particleObject(particleObject).center(new Vector3f()).radius(2.0f)
 *                 .clockwise().trimming(new AnimationTrimming<>(0.0f, (float)Math.PI))
 *                 .build();
 * </pre>
 * <p>
 * As another example, the following code will begin at 2&pi;, go counter-clockwise to 3&pi;/2, then
 * skip to &pi;/2 and continue rendering until it reaches {@code 0}.
 * <pre>
 * CircularAnimator.builder().particleObject(particleObject).center(new Vector3f()).radius(2.0f)
 *                 .counterclockwise().trimming(new AnimationTrimming<>((float)Math.PI/2, -(float)Math.PI/2))
 *                 .build();
 * </pre>
 * If the previous code set `clockwise()` on the builder, it would render continuously from &pi;/2 to -&pi;/2;
 * <p>
 * To have it start at &pi;/2 and render continuously in a counter-clockwise direction, rotate the circular path &pi;/2
 * clockwise around the z-axis, and change the trim interval to [0, &pi;]:
 * <pre>
 * CircularAnimator.builder().particleObject(particleObject).center(new Vector3f()).radius(2.0f)
 *                 .rotation(new Vector3f(0.0f, 0.0f, (float)Math.PI/2))
 *                 .counterclockwise().trimming(new AnimationTrimming<>(0, (float)Math.PI))
 *                 .build();
 * </pre>
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class CircularAnimator extends PathAnimatorBase {
    protected float radius;
    protected Vector3f center;
    protected Vector3f rotation;
    protected int revolutions;
    protected AnimationTrimming<Float> trimming;
    protected boolean clockwise;

    protected OldInterceptors<CircularAnimator, OnRenderStep> duringRenderingSteps = OldInterceptors.identity();

    public enum OnRenderStep {SHOULD_DRAW_STEP, RENDERING_POSITION}

    public static <B extends Builder<B>> Builder<B> builder() {
        return new Builder<>();
    }

    private <B extends Builder<B>> CircularAnimator(Builder<B> builder) {
        super(builder);
        this.center = builder.center;
        this.radius = builder.radius;
        this.rotation = builder.rotation;
        this.revolutions = builder.revolutions;
        this.clockwise = builder.clockwise;
        this.trimming = builder.trimming;
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
        this.duringRenderingSteps = animator.duringRenderingSteps;
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

    /** Toggles the direction of rotation between clockwise and counter-clockwise.
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

    @Override
    public int convertIntervalToSteps() {
        return (int) (Math.ceil(Math.TAU / this.renderingInterval) + 1) * this.revolutions;
    }

    /**
     * This method is used to compute the animation logic.  It runs, in its entirety, as soon as it's called.
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
                Vector3f pos = calculatePoint(currAngle);
                InterceptData<OnRenderStep> interceptData = this.doBeforeStep(renderer.getServerWorld(), pos, i);
                if (!interceptData.getMetadata(OnRenderStep.SHOULD_DRAW_STEP, true)) {
                    continue;
                }
                pos = interceptData.getMetadata(OnRenderStep.RENDERING_POSITION, pos);
                this.handleDrawingStep(renderer, step, pos);
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
                this.radius * trigTable.getSine(currAngle),
                0
        );
        pos = pos.rotateZ(this.rotation.z).rotateY(this.rotation.y).rotateX(this.rotation.x);
        return pos.add(this.center);
    }

    /** Set the interceptor to run before the drawing of each individual rendering step. The interceptor will be provided
     * with references to the {@link ServerWorld}, the current step number. As far as it goes for metadata,
     * there will be a boolean value that dictates if it should draw on this step and the rendering position of the
     * point that lives in the circle
     *
     * @param duringRenderingSteps the new interceptor to execute before drawing the individual steps
     */
    public void setDuringRenderingSteps(OldInterceptors<CircularAnimator, OnRenderStep> duringRenderingSteps) {
        this.duringRenderingSteps = Optional.ofNullable(duringRenderingSteps).orElse(OldInterceptors.identity());
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

    /** This is the Circular path-animator builder used for setting up a new Circular path-animator instance.
     * It is designed to be more friendly of how you arrange the parameters. Call {@code .builder()} to initiate
     * the builder, once you supplied the parameters then you can call {@code .build()} to create the instance
     *
     * @param <B> The builder type itself
    */
    public static class Builder<B extends Builder<B>> extends PathAnimatorBase.Builder<B, CircularAnimator> {
        protected Vector3f center;
        protected float radius;
        protected Vector3f rotation = new Vector3f();
        protected int revolutions = 1;
        protected boolean clockwise = true;
        protected AnimationTrimming<Float> trimming = new AnimationTrimming<>(0.0f, 0.0f);

        private Builder() {}

        /** The center position of the circle
         *
         * @param center The center of the circle
         * @return The builder instance
        */
        public B center(Vector3f center) {
            this.center = center;
            return self();
        }

        /** The radius of the circle
         *
         * @param radius The radius of the circle
         * @return The builder instance
        */
        public B radius(float radius) {
            this.radius = radius;
            return self();
        }

        /** The rotation of the circle
         *
         * @param rotation The rotation of the circle
         * @return The builder instance
        */
        public B rotation(Vector3f rotation) {
            this.rotation = rotation;
            return self();
        }

        /** The revolutions of the circle. How many times to loop through the circle
         *
         * @param revolutions The revolutions of the circle
         * @return The builder instance
        */
        public B revolutions(int revolutions) {
            this.revolutions = revolutions;
            return self();
        }

        /** Sets the particle object to rotate clockwise of the circle
         *
         * @return The builder instance
        */
        public B clockwise() {
            this.clockwise = true;
            return self();
        }

        /** Sets the particle object to rotate counter-clockwise of the circle
         *
         * @return The builder instance
        */
        public B counterclockwise() {
            this.clockwise = false;
            return self();
        }

        /** Sets the trimming of the circle path-animator
         *
         * @param trimming The trimming for the circle path-animator
         * @return The builder instance
        */
        public B trimming(AnimationTrimming<Float> trimming) {
            this.trimming = new AnimationTrimming<>(trimming);
            return self();
        }

        @Override
        public CircularAnimator build() {
            if (this.center == null) {
                throw new IllegalStateException("Center must be provided");
            }
            if (this.radius <= 0.0f) {
                throw new IllegalStateException("Radius must be positive");
            }
            if (this.revolutions <= 0) {
                throw new IllegalStateException("Revolutions must be positive");
            }
            if (this.renderCalculationMethod == RenderCalculationMethod.UNSET) {
                throw new IllegalStateException("Either rendering steps or rendering interval must be set");
            }
            return new CircularAnimator(this);
        }
    }
}
