package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import org.joml.Vector3f;

/** The particle object class that represents a 2D triangle.
 * It has three vertices that make it up, all of them must be coplanar with each other.
 * The vertices can be set individually or by supplying a list of three vertices.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleTriangle extends ParticleObject<ParticleTriangle> {
    protected Vector3f vertex1;
    protected Vector3f vertex2;
    protected Vector3f vertex3;

    private final IllegalArgumentException UNBALANCED_VERTICES = new IllegalArgumentException(
            "Unbalanced vertices, there must be only 3 vertices"
    );

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleTriangle(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setVertices(builder.vertex1, builder.vertex2, builder.vertex3);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.  Vertices are copied to new vectors.
     *
     * @param triangle The particle triangle object to copy from
    */
    public ParticleTriangle(ParticleTriangle triangle) {
        super(triangle);
        this.vertex1 = new Vector3f(triangle.vertex1);
        this.vertex2 = new Vector3f(triangle.vertex2);
        this.vertex3 = new Vector3f(triangle.vertex3);
    }

    private void checkValidTriangle(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3) {
        // Defensive copy before cross-product, which is done in-place.
        float dotProduct1 = vertex3.dot(new Vector3f(vertex1).cross(vertex2));
        if (dotProduct1 == 0) {
            throw new IllegalArgumentException("Provided vertices do not produce a triangle");
        }
    }

    private void checkValidTriangle(Vector3f[] vertices) {
        Vector3f vertex1 = vertices[0];
        Vector3f vertex2 = vertices[1];
        Vector3f vertex3 = vertices[2];
        this.checkValidTriangle(vertex1, vertex2, vertex3);
    }

    /**
     * Sets the first individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}.
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTriangle#setVertices(Vector3f...)
     */
    public Vector3f setVertex1(Vector3f newVertex) {
        this.checkValidTriangle(newVertex, this.vertex2, this.vertex3);
        Vector3f prevVertex1 = this.vertex1;
        this.vertex1 = newVertex;
        return prevVertex1;
    }

    /**
     * Sets the second individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}.
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTriangle#setVertices(Vector3f...)
     */
    public Vector3f setVertex2(Vector3f newVertex) {
        this.checkValidTriangle(this.vertex1, newVertex, this.vertex3);
        Vector3f prevVertex2 = this.vertex2;
        this.vertex2 = newVertex;
        return prevVertex2;
    }

    /**
     * Sets the third individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}.
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTriangle#setVertices(Vector3f...)
     */
    public Vector3f setVertex3(Vector3f newVertex) {
        this.checkValidTriangle(this.vertex1, this.vertex2, newVertex);
        Vector3f prevVertex3 = this.vertex3;
        this.vertex3 = newVertex;
        return prevVertex3;
    }

    /**
     * Sets all vertices at once.  If you want to set one vertex at a time, use individual setters such as
     * {@link #setVertex1(Vector3f)}.  Returns nothing.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param vertices The vertices to modify
     *
     * @throws IllegalArgumentException if the number of vertices supplied isn't equal to 3
    */
    public final void setVertices(Vector3f... vertices) {
        if (vertices.length != 3) {
            throw UNBALANCED_VERTICES;
        }
        this.checkValidTriangle(vertices);
        this.vertex1 = vertices[0];
        this.vertex2 = vertices[1];
        this.vertex3 = vertices[2];
    }

    /** Gets the first individual vertex.
     *
     * @return The first individual vertex
    */
    public Vector3f getVertex1() {
        return this.vertex1;
    }

    /** Gets the second individual vertex.
     *
     * @return The second individual vertex
    */
    public Vector3f getVertex2() {
        return this.vertex2;
    }

    /** Gets the third individual vertex.
     *
     * @return The third individual vertex
    */
    public Vector3f getVertex3() {
        return this.vertex3;
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        // Defensive copy of `drawPos`
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);

        int step = drawContext.getCurrentStep();
        renderer.drawLine(this.particleEffect, step, objectDrawPos, this.vertex1, this.vertex2, this.rotation, this.amount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, this.vertex2, this.vertex3, this.rotation, this.amount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, this.vertex3, this.vertex1, this.rotation, this.amount);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleTriangle> {
        protected Vector3f vertex1;
        protected Vector3f vertex2;
        protected Vector3f vertex3;

        private Builder() {}

        /**
         * Set vertex1 on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B vertex1(Vector3f vertex1) {
            this.vertex1 = vertex1;
            return self();
        }

        /**
         * Set vertex2 on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B vertex2(Vector3f vertex2) {
            this.vertex2 = vertex2;
            return self();
        }

        /**
         * Set vertex3 on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B vertex3(Vector3f vertex3) {
            this.vertex3 = vertex3;
            return self();
        }

        @Override
        public ParticleTriangle build() {
            return new ParticleTriangle(this);
        }
    }
}
