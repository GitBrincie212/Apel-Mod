package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import org.joml.Vector3f;

/** The particle object class that represents a 2D line. It is one of the
 * most simple objects to use as it needs only a start & an ending position
 * to draw that line. The line cannot be curved and is only linear.
 *
 * <p><b>Note:</b> Rotation is not applied to the calculations, as such it doesn't make
 * any difference.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleLine extends ParticleObject<ParticleLine> {
    protected Vector3f start;
    protected Vector3f end;

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
        this.start = new Vector3f(line.start);
        this.end = new Vector3f(line.end);
    }

    /** Gets the starting endpoint
     *
     * @return The starting endpoint
     */
    public Vector3f getStart() {
        return this.start;
    }

    /**
     * Sets the starting point of the line.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param start The new starting point of the line
     * @return The previous starting point
    */
    public final Vector3f setStart(Vector3f start) {
        if (start.equals(this.end)) {
            throw new IllegalArgumentException("Endpoints must not be equal");
        }
        Vector3f prevStart = this.start;
        this.start = start;
        return prevStart;
    }

    /** Gets the ending endpoint
     *
     * @return The ending endpoint
     */
    public Vector3f getEnd() {
        return this.end;
    }

    /**
     * Sets the ending point of the line.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param end The new ending point of the line
     * @return The previous ending point
    */
    public final Vector3f setEnd(Vector3f end) {
        if (end.equals(this.start)) {
            throw new IllegalArgumentException("Endpoints must not be equal");
        }
        Vector3f prevEnd = this.end;
        this.end = end;
        return prevEnd;
    }

    @Override
    @Deprecated
    public Vector3f getRotation() {
        // Do not throw UnsupportedOperationException in case this is called in a series of ParticleObjects
        return null;
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {

        Vector3f v1 = new Vector3f(this.start).add(drawContext.getPosition()).add(this.offset);
        Vector3f v2 = new Vector3f(this.end).add(drawContext.getPosition()).add(this.offset);

        renderer.drawLine(this.particleEffect, drawContext.getCurrentStep(), v1, v2, this.amount);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleLine> {
        protected Vector3f start = new Vector3f();
        protected Vector3f end = new Vector3f();

        private Builder() {}

        /**
         * Set the start point on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B start(Vector3f start) {
            this.start = start;
            return self();
        }

        /**
         * Set the end point on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B end(Vector3f end) {
            this.end = end;
            return self();
        }

        @Override
        public ParticleLine build() {
            return new ParticleLine(this);
        }
    }
}
