package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import org.joml.Vector3f;

/** The particle object class that represents a tetrahedron which may be irregular.
 *  It has four vertices that make it up which must not be coplanar with each other.
 *  The vertices can be set individually or by supplying a list of four vertices.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleTetrahedron extends ParticleObject<ParticleTetrahedron> {
    protected Vector3f vertex1;
    protected Vector3f vertex2;
    protected Vector3f vertex3;
    protected Vector3f vertex4;

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleTetrahedron(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setVertices(builder.vertex1, builder.vertex2, builder.vertex3, builder.vertex4);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.  Vertices are copied to new vectors.
     *
     * @param tetrahedron The particle tetrahedron object to copy from
    */
    public ParticleTetrahedron(ParticleTetrahedron tetrahedron) {
        super(tetrahedron);
        this.vertex1 = new Vector3f(tetrahedron.vertex1);
        this.vertex2 = new Vector3f(tetrahedron.vertex2);
        this.vertex3 = new Vector3f(tetrahedron.vertex3);
        this.vertex4 = new Vector3f(tetrahedron.vertex4);
    }

    private void checkValidTetrahedron(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f vertex4) {
        // Defensive copies prior to subtraction and cross-product, which are done in-place.
        Vector3f v1 = new Vector3f(vertex2).sub(vertex1);
        Vector3f v2 = new Vector3f(vertex3).sub(vertex1);
        Vector3f v3 = new Vector3f(vertex4).sub(vertex1);
        // This performs the scalar triple product, which calculates the volume of the parallelepiped formed by
        // three vectors sharing an origin.  If any are co-planar, the volume is zero, and there is no tetrahedron.
        float result = v1.cross(v2).dot(v3);
        if (Math.abs(result) < 0.0001f) {
            throw new IllegalArgumentException("Provided vertices do not produce a tetrahedron");
        }
    }

    private void checkValidTetrahedron(Vector3f[] vertices) {
        Vector3f vertex1 = vertices[0];
        Vector3f vertex2 = vertices[1];
        Vector3f vertex3 = vertices[2];
        Vector3f vertex4 = vertices[3];
        this.checkValidTetrahedron(vertex1, vertex2, vertex3, vertex4);
    }

    /**
     * Sets the first individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}.
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTetrahedron#setVertices(Vector3f...)
     */
    public Vector3f setVertex1(Vector3f newVertex) {
        this.checkValidTetrahedron(newVertex, this.vertex2, this.vertex3, this.vertex4);
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
     * @see ParticleTetrahedron#setVertices(Vector3f...)
     */
    public Vector3f setVertex2(Vector3f newVertex) {
        this.checkValidTetrahedron(this.vertex1, newVertex, this.vertex3, this.vertex4);
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
     * @see ParticleTetrahedron#setVertices(Vector3f...)
     */
    public Vector3f setVertex3(Vector3f newVertex) {
        this.checkValidTetrahedron(this.vertex1, this.vertex2, newVertex, this.vertex4);
        Vector3f prevVertex3 = this.vertex3;
        this.vertex3 = newVertex;
        return prevVertex3;
    }

    /**
     * Sets the third individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}.
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTetrahedron#setVertices(Vector3f...)
    */
    public Vector3f setVertex4(Vector3f newVertex) {
        this.checkValidTetrahedron(this.vertex1, this.vertex2, this.vertex3, newVertex);
        Vector3f prevVertex4 = this.vertex4;
        this.vertex4 = newVertex;
        return prevVertex4;
    }

    /**
     * Sets all vertices at once.  If you want to set one vertex at a time, use individual setters such as
     * {@link #setVertex1(Vector3f)}.  Returns nothing.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param vertices The vertices to modify
     *
     * @throws IllegalArgumentException if the number of vertices supplied isn't equal to 4
    */
    public final void setVertices(Vector3f... vertices) {
        if (vertices.length != 4) {
            throw new IllegalArgumentException("Unbalanced vertices, there must be only 4 vertices");
        }
        this.checkValidTetrahedron(vertices);
        this.vertex1 = vertices[0];
        this.vertex2 = vertices[1];
        this.vertex3 = vertices[2];
        this.vertex4 = vertices[3];
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

    /** Gets the fourth individual vertex.
     *
     * @return The fourth individual vertex
    */
    public Vector3f getVertex4() {
        return this.vertex4;
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        // Defensive copy of `drawPos`
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);

        int step = drawContext.getCurrentStep();
        renderer.drawLine(this.particleEffect, step, objectDrawPos, this.vertex1, this.vertex2, this.rotation, this.amount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, this.vertex1, this.vertex3, this.rotation, this.amount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, this.vertex1, this.vertex4, this.rotation, this.amount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, this.vertex2, this.vertex3, this.rotation, this.amount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, this.vertex2, this.vertex4, this.rotation, this.amount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, this.vertex3, this.vertex4, this.rotation, this.amount);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleTetrahedron> {
        protected Vector3f vertex1;
        protected Vector3f vertex2;
        protected Vector3f vertex3;
        protected Vector3f vertex4;

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

        /**
         * Set vertex4 on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B vertex4(Vector3f vertex4) {
            this.vertex4 = vertex4;
            return self();
        }

        @Override
        public ParticleTetrahedron build() {
            return new ParticleTetrahedron(this);
        }
    }
}
