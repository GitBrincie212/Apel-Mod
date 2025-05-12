package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.ComputedEasings;
import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
import net.mcbrincie.apel.lib.util.ComputedEasings;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import org.joml.Vector3f;

/** The particle object class that represents a 2D triangle.
 * It has three vertices that make it up, all of them must be coplanar with each other.
 * The vertices can be set individually or by supplying a list of three vertices.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleTriangle extends ParticleObject<ParticleTriangle> {
    protected EasingCurve<Vector3f> vertex1;
    protected EasingCurve<Vector3f> vertex2;
    protected EasingCurve<Vector3f> vertex3;

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
        this.vertex1 = triangle.vertex1;
        this.vertex2 = triangle.vertex2;
        this.vertex3 = triangle.vertex3;
    }

    private void checkValidTriangle(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3) {
        // Defensive copies prior to subtraction and cross-product, which are done in-place.
        Vector3f v1 = new Vector3f(vertex2).sub(vertex1);
        Vector3f v2 = new Vector3f(vertex3).sub(vertex1);
        // As long as the vectors from vertex1->vertex2 and vertex1->vertex3 are not collinear, the triangle is valid.
        // If they are collinear, the magnitude of the cross product vector will be zero (as will its square).
        float crossProductMagnitudeSquared = v1.cross(v2).lengthSquared();
        if (crossProductMagnitudeSquared == 0) {
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
     * vertices at once then use {@code setVertices}. This method overload will set the first vertex as a constant value
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTriangle#setVertices(Vector3f...)
     */
    public EasingCurve<Vector3f> setVertex1(Vector3f newVertex) {
        return this.setVertex1(new ConstantEasingCurve<>(newVertex));
    }

    /**
     * Sets the first individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}. This method overload will set the first vertex as an ease curve value
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTriangle#setVertices(Vector3f...)
     */
    public EasingCurve<Vector3f> setVertex1(EasingCurve<Vector3f> newVertex) {
        EasingCurve<Vector3f> prevVertex1 = this.vertex1;
        this.vertex1 = newVertex;
        return prevVertex1;
    }

    /**
     * Sets the second individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}. This method overload will set the first vertex as a constant value
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTriangle#setVertices(Vector3f...)
     */
    public EasingCurve<Vector3f> setVertex2(Vector3f newVertex) {
        return this.setVertex2(new ConstantEasingCurve<>(newVertex));
    }

    /**
     * Sets the second individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}. This method overload will set the first vertex as an ease curve value
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTriangle#setVertices(Vector3f...)
     */
    public EasingCurve<Vector3f> setVertex2(EasingCurve<Vector3f> newVertex) {
        EasingCurve<Vector3f> prevVertex2 = this.vertex2;
        this.vertex2 = newVertex;
        return prevVertex2;
    }

    /**
     * Sets the third individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}. This method overload will set the first vertex as a constant value
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTriangle#setVertices(Vector3f...)
     */
    public EasingCurve<Vector3f> setVertex3(Vector3f newVertex) {
        return this.setVertex3(new ConstantEasingCurve<>(newVertex));
    }

    /**
     * Sets the third individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}. This method overload will set the first vertex as an ease curve value
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTriangle#setVertices(Vector3f...)
     */
    public EasingCurve<Vector3f> setVertex3(EasingCurve<Vector3f> newVertex) {
        EasingCurve<Vector3f> prevVertex3 = this.vertex3;
        this.vertex3 = newVertex;
        return prevVertex3;
    }

    /**
     * Sets all vertices at once.  If you want to set one vertex at a time, use individual setters such as
     * {@link #setVertex1(Vector3f)}. Returns nothing.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method. This
     * method overload will set the vertices as constant values
     *
     * @param vertices The vertices to modify
     *
     * @throws IllegalArgumentException if the number of vertices supplied isn't equal to 3
    */
    public final void setVertices(Vector3f... vertices) {
        if (vertices.length != 3) {
            throw UNBALANCED_VERTICES;
        }
        this.vertex1 = new ConstantEasingCurve<>(vertices[0]);
        this.vertex2 = new ConstantEasingCurve<>(vertices[1]);
        this.vertex3 = new ConstantEasingCurve<>(vertices[2]);
    }

    /**
     * Sets all vertices at once.  If you want to set one vertex at a time, use individual setters such as
     * {@link #setVertex1(Vector3f)}. Returns nothing.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method. This
     * method overload will set the vertices as ease curve values
     *
     * @param vertices The vertices to modify
     *
     * @throws IllegalArgumentException if the number of vertices supplied isn't equal to 3
     */
    @SafeVarargs
    public final void setVertices(EasingCurve<Vector3f>... vertices) {
        if (vertices.length != 3) {
            throw UNBALANCED_VERTICES;
        }
        this.vertex1 = vertices[0];
        this.vertex2 = vertices[1];
        this.vertex3 = vertices[2];
    }

    /** Gets the first individual vertex.
     *
     * @return The first individual vertex
    */
    public EasingCurve<Vector3f> getVertex1() {
        return this.vertex1;
    }

    /** Gets the second individual vertex.
     *
     * @return The second individual vertex
    */
    public EasingCurve<Vector3f> getVertex2() {
        return this.vertex2;
    }

    /** Gets the third individual vertex.
     *
     * @return The third individual vertex
    */
    public EasingCurve<Vector3f> getVertex3() {
        return this.vertex3;
    }

    @Override
    protected ComputedEasings computeAdditionalEasings(ComputedEasingPO container) {
        return container
                .addComputedField("vertex1", this.vertex1)
                .addComputedField("vertex2", this.vertex2)
                .addComputedField("vertex3", this.vertex3);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        // Defensive copy of `drawPos`
        ComputedEasingPO computedEasingPO = drawContext.getComputedEasings();
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(computedEasingPO.computedOffset);
        Vector3f currVertex1 = (Vector3f) computedEasingPO.getComputedField("vertex1");
        Vector3f currVertex2 = (Vector3f) computedEasingPO.getComputedField("vertex2");
        Vector3f currVertex3 = (Vector3f) computedEasingPO.getComputedField("vertex3");
        checkValidTriangle(currVertex1, currVertex2, currVertex3);

        Vector3f computedRotation = computedEasingPO.computedRotation;
        int computedAmount = computedEasingPO.computedAmount;

        int step = drawContext.getCurrentStep();
        renderer.drawLine(this.particleEffect, step, objectDrawPos, currVertex1, currVertex2, computedRotation, computedAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, currVertex2, currVertex3, computedRotation, computedAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, currVertex3, currVertex1, computedRotation, computedAmount);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleTriangle> {
        protected EasingCurve<Vector3f> vertex1;
        protected EasingCurve<Vector3f> vertex2;
        protected EasingCurve<Vector3f> vertex3;

        private Builder() {}

        /**
         * Set vertex1 on the builder. This method is not cumulative; repeated calls will overwrite the value.
         * This method overload will set a constant value for the vertex
         */
        public B vertex1(Vector3f vertex1) {
            this.vertex1 = new ConstantEasingCurve<>(vertex1);
            return self();
        }

        /**
         * Set vertex2 on the builder. This method is not cumulative; repeated calls will overwrite the value.
         * This method overload will set a constant value for the vertex
         */
        public B vertex2(Vector3f vertex2) {
            this.vertex2 = new ConstantEasingCurve<>(vertex2);
            return self();
        }

        /**
         * Set vertex3 on the builder. This method is not cumulative; repeated calls will overwrite the value.
         * This method overload will set a constant value for the vertex
         */
        public B vertex3(Vector3f vertex3) {
            this.vertex3 = new ConstantEasingCurve<>(vertex3);
            return self();
        }

        /**
         * Set vertex1 on the builder. This method is not cumulative; repeated calls will overwrite the value.
         * This method overload will set an ease curve value for the vertex
         */
        public B vertex1(EasingCurve<Vector3f> vertex1) {
            this.vertex1 = vertex1;
            return self();
        }

        /**
         * Set vertex2 on the builder. This method is not cumulative; repeated calls will overwrite the value.
         * This method overload will set an ease curve value for the vertex
         */
        public B vertex2(EasingCurve<Vector3f> vertex2) {
            this.vertex2 = vertex2;
            return self();
        }

        /**
         * Set vertex3 on the builder. This method is not cumulative; repeated calls will overwrite the value.
         * This method overload will set an ease curve value for the vertex
         */
        public B vertex3(EasingCurve<Vector3f> vertex3) {
            this.vertex3 = vertex3;
            return self();
        }

        @Override
        public ParticleTriangle build() {
            return new ParticleTriangle(this);
        }
    }
}
