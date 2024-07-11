package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;


/** The particle object class that represents a 2D regular polygon. Regular polygons contain the
 * same angles as well as the same edges; Examples include but are not limited to a square, a pentagon,
 * an equilateral triangle, etc. The polygon takes a variable "sides" that dictates how many sides the polygon
 * will have.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticlePolygon extends ParticleObject<ParticlePolygon> {
    protected int sides;
    protected float size;

    protected HashMap<Integer, Vector3f[]> cachedShapes = new HashMap<>();

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticlePolygon(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setSides(builder.sides);
        this.setSize(builder.size);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.
     *
     * @param polygon The particle polygon object to copy from
    */
    public ParticlePolygon(ParticlePolygon polygon) {
        super(polygon);
        this.sides = polygon.sides;
        this.size = polygon.size;
        this.cachedShapes = polygon.cachedShapes;
    }

    /**
     * Sets the sides to a new value and returns the previous sides used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param sides The new sides
     * @throws IllegalArgumentException If the sides are less than 3
     * @return The previous sides used
     */
    public final int setSides(int sides) throws IllegalArgumentException {
        if (sides < 3) {
            throw new IllegalArgumentException("Polygon must have at least 3 sides");
        }
        int prevSides = this.sides;
        this.sides = sides;
        return prevSides;
    }

    /** Gets the sides of the regular polygon.
     *
     * @return The sides of the regular polygon
     */
    public int getSides() {
        return this.sides;
    }

    /**
     * Sets the size of the polygon to a new value and returns the previous size used.  The size is the distance from
     * the centroid to any vertex.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param size The new size
     * @throws IllegalArgumentException If the size is negative or 0
     * @return The previous size used
     */
    public final float setSize(float size) throws IllegalArgumentException {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        float prevSize = this.size;
        this.size = size;
        return prevSize;
    }

    /** Gets the size of the regular polygon
     *
     * @return The size of the regular polygon
     */
    public float getSize() {
        return this.size;
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        Vector3f[] vertices = getRawVertices();
        // Defensive copy
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);
        this.computeVertices(vertices, objectDrawPos);

        // Divide the particles evenly among sides
        int particlesPerLine = this.amount / this.sides;
        for (int i = 0; i < vertices.length - 1; i++) {
            renderer.drawLine(
                    this.particleEffect, drawContext.getCurrentStep(), vertices[i], vertices[i + 1], particlesPerLine);
        }
    }

    private @NotNull Vector3f[] getRawVertices() {
        // Cache the vertices, does not rotate or offset
        // (since these change a lot and the goal is to use the cache as much as possible)
        Vector3f[] cachedVertices = this.cachedShapes.computeIfAbsent(this.sides, sides -> {
            float angleInterval = (float) (Math.TAU / this.sides);
            // Apply this angle offset to make it point up to 0º
            float offset = (float) (2.5f * Math.PI);
            // Sides + 1 to repeat the starting point to prevent modulo math later
            Vector3f[] newVertices = new Vector3f[this.sides + 1];
            for (int i = 0; i < this.sides; i++) {
                float currAngle = (angleInterval * i) + offset;
                float x = this.size * Apel.TRIG_TABLE.getCosine(currAngle);
                float y = this.size * Apel.TRIG_TABLE.getSine(currAngle);
                newVertices[i] = new Vector3f(x, y, 0);
            }
            // Ensure the last particle is exactly the same as the first
            newVertices[this.sides] = new Vector3f(newVertices[0]);
            return newVertices;
        });
        // Defensive copy of vertices so the cache isn't corrupted
        Vector3f[] verticesCopy = new Vector3f[cachedVertices.length];
        for (int i = 0; i < cachedVertices.length; i++) {
            verticesCopy[i] = new Vector3f(cachedVertices[i]);
        }
        return verticesCopy;
    }

    private void computeVertices(Vector3f[] vertices, Vector3f center) {
        Quaternionf quaternion = new Quaternionf().rotateZ(this.rotation.z)
                                                  .rotateY(this.rotation.y)
                                                  .rotateX(this.rotation.x);
        for (Vector3f vertex : vertices) {
            vertex.rotate(quaternion).add(center);
        }
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticlePolygon> {
        protected int sides;
        protected float size;

        private Builder() {}

        /**
         * Set the number of sides on the builder.  This method is not cumulative; repeated calls will overwrite the
         * value.
         */
        public B sides(int sides) {
            this.sides = sides;
            return self();
        }

        /**
         * Set the size on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         *
         * @see ParticlePolygon#setSize(float)
         */
        public B size(float size) {
            this.size = size;
            return self();
        }

        @Override
        public ParticlePolygon build() {
            return new ParticlePolygon(this);
        }
    }
}
