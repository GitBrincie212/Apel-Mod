package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.ComputedEasingRPO;
import net.mcbrincie.apel.lib.util.ComputedEasings;
import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import org.joml.Vector3f;

/**
 * This particle object subclass represents a 3D line. Constructing the line requires two points, both relative to the
 * provided origin in the {@code DrawContext}.  The line may be rotated and offset relative to the draw origin by using
 * the builder properties, setters after construction, or during interceptor calls.
 * <p>
 * If more complicated, line-based shapes are desired, see the following:
 * @see ParticleCuboid
 * @see ParticlePolygon
 * @see ParticleQuad
 * @see ParticleTetrahedron
 * @see ParticleTriangle
 * </p>
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleLine extends RenderableParticleObject<ParticleLine> {
    protected EasingCurve<Vector3f> start;
    protected EasingCurve<Vector3f> end;

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleLine(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setStart(builder.start);
        this.setEnd(builder.end);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.  The start and end points are copied to new
     * vectors.
     *
     * @param line The particle object to copy from
    */
    public ParticleLine(ParticleLine line) {
        super(line);
        this.start = line.start;
        this.end = line.end;
    }

    /** Gets the starting endpoint
     *
     * @return The starting endpoint
     */
    public EasingCurve<Vector3f> getStart() {
        return this.start;
    }

    /**
     * Sets the starting point of the line.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This is an overload for specifying a constant value for the start
     *
     * @param start The new starting point of the line
     * @return The previous starting point
    */
    public final EasingCurve<Vector3f> setStart(Vector3f start) {
        EasingCurve<Vector3f> prevStart = this.start;
        this.start = new ConstantEasingCurve<>(start);
        return prevStart;
    }

    /**
     * Sets the starting point of the line.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This is an overload for specifying an easing curve for the start
     *
     * @param start The new starting point of the line
     * @return The previous starting point
     */
    public final EasingCurve<Vector3f> setStart(EasingCurve<Vector3f> start) {
        EasingCurve<Vector3f> prevStart = this.start;
        this.start = start;
        return prevStart;
    }

    /** Gets the ending endpoint
     *
     * @return The ending endpoint
     */
    public EasingCurve<Vector3f> getEnd() {
        return this.end;
    }

    /**
     * Sets the ending point of the line.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This is an overload for specifying an easing curve for the end
     *
     * @param end The new ending point of the line
     * @return The previous ending point
     */
    public final EasingCurve<Vector3f> setEnd(EasingCurve<Vector3f> end) {
        EasingCurve<Vector3f> prevEnd = this.end;
        this.end = end;
        return prevEnd;
    }

    /**
     * Sets the ending point of the line.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This is an overload for specifying a constant value for the end
     *
     * @param end The new ending point of the line
     * @return The previous ending point
    */
    public final EasingCurve<Vector3f> setEnd(Vector3f end) {
        EasingCurve<Vector3f> prevEnd = this.end;
        this.end = new ConstantEasingCurve<>(end);
        return prevEnd;
    }

    @Override
    protected ComputedEasingRPO computeAdditionalEasings(ComputedEasingRPO container) {
        return container
                .addComputedField("start", this.start)
                .addComputedField("end", this.end);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        ComputedEasingRPO computedEasings = (ComputedEasingRPO) drawContext.getComputedEasings();
        int currAmount = computedEasings.computedAmount;
        Vector3f currRotation = drawContext.getComputedEasings().computedRotation;
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(computedEasings.computedOffset);
        Vector3f currStart = (Vector3f) drawContext.getComputedEasings().getComputedField("start");
        Vector3f currEnd = (Vector3f) drawContext.getComputedEasings().getComputedField("end");
        if (currStart.equals(currEnd)) {
            throw new IllegalArgumentException("Endpoints must not be equal");
        }
        renderer.drawLine(this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, currStart, currEnd, currRotation, currAmount);
    }

    public static class Builder<B extends Builder<B>> extends RenderableParticleObject.Builder<B, ParticleLine> {
        protected EasingCurve<Vector3f> start = new ConstantEasingCurve<>(new Vector3f());
        protected EasingCurve<Vector3f> end = new ConstantEasingCurve<>(new Vector3f());

        private Builder() {}

        /**
         * Set the start point on the builder. This method is not cumulative; repeated calls will overwrite the value.
         * This overload sets a constant value of the start point
         */
        public B start(Vector3f start) {
            this.start = new ConstantEasingCurve<>(start);
            return self();
        }

        /**
         * Set the end point on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         * This overload sets a constant value of the end point
         */
        public B end(Vector3f end) {
            this.end = new ConstantEasingCurve<>(end);
            return self();
        }

        /**
         * Set the start point on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         * This overload sets an easing curve value of the start point
         */
        public B start(EasingCurve<Vector3f> start) {
            this.start = start;
            return self();
        }

        /**
         * Set the end point on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         * This overload sets an easing curve value of the end point
         */
        public B end(EasingCurve<Vector3f> end) {
            this.end = end;
            return self();
        }

        @Override
        public ParticleLine build() {
            return new ParticleLine(this);
        }
    }
}
