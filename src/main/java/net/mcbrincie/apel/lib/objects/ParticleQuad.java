package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.ComputedEasingRPO;
import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.context.DrawContext;
import org.joml.Vector3f;


/** The particle object class that represents a quadrilateral.  This can be a rectangle, a square, a trapezoid,
 * or any irregular shape with four sides.  There is no restriction on the vertices being co-planar.
 *
 * <p>The quadrilateral is drawn in order of the vertices provided (presume the line
 * from vertex2 to vertex3 is straight, ignoring the limitations of ASCII art):
 * <pre>
 *     vertex1 ------------------ vertex2
 *        \                          |
 *         \                         |
 *          \                        |
 *        vertex4 --------------- vertex3
 * </pre>
 */
@SuppressWarnings("unused")
public class ParticleQuad extends RenderableParticleObject<ParticleQuad> {
    protected EasingCurve<Vector3f> vertex1;
    protected EasingCurve<Vector3f> vertex2;
    protected EasingCurve<Vector3f> vertex3;
    protected EasingCurve<Vector3f> vertex4;

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleQuad(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setVertex1(builder.vertex1);
        this.setVertex2(builder.vertex2);
        this.setVertex3(builder.vertex3);
        this.setVertex4(builder.vertex4);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param quad The particle quad object to copy from
    */
    public ParticleQuad(ParticleQuad quad) {
        super(quad);
        this.vertex1 = quad.vertex1;
        this.vertex2 = quad.vertex2;
        this.vertex3 = quad.vertex3;
        this.vertex4 = quad.vertex4;
    }

    /** Sets all vertices at once instead of one at a time.
     * If you want to set one vertex at a time, then its recommend to use
     * the methods {@link #setVertex1(Vector3f)}, etc. This method overload
     * sets a constant value for each vertex
     *
     * @param vertices The vertices to modify
     *
     * @throws IllegalArgumentException if the number of vertices supplied isn't equal to 4
     */
    public void setVertices(Vector3f[] vertices) throws IllegalArgumentException {
        if (vertices.length != 4) {
            throw new IllegalArgumentException("The amount of vertices must be 4");
        }
        this.vertex1 = new ConstantEasingCurve<>(vertices[0]);
        this.vertex2 = new ConstantEasingCurve<>(vertices[1]);
        this.vertex3 = new ConstantEasingCurve<>(vertices[2]);
        this.vertex4 = new ConstantEasingCurve<>(vertices[3]);
    }

    /** Sets all vertices at once instead of one at a time.
     * If you want to set one vertex at a time, then its recommend to use
     * the methods {@link #setVertex1(Vector3f)}, etc. This method overload
     * sets an easing curve value for each vertex
     *
     * @param vertices The vertices to modify
     *
     * @throws IllegalArgumentException if the number of vertices supplied isn't equal to 4
     */
    public void setVertices(EasingCurve<Vector3f>[] vertices) throws IllegalArgumentException {
        if (vertices.length != 4) {
            throw new IllegalArgumentException("The amount of vertices must be 4");
        }
        this.vertex1 = vertices[0];
        this.vertex2 = vertices[1];
        this.vertex3 = vertices[2];
        this.vertex4 = vertices[3];
    }

    /**
     * Sets the first individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}. This method overload will set a constant value
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleQuad#setVertices(Vector3f[])
     */
    public final EasingCurve<Vector3f> setVertex1(Vector3f newVertex) {
        return this.setVertex1(new ConstantEasingCurve<>(newVertex));
    }

    /**
     * Sets the first individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}. This method overload will set an ease curve value
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleQuad#setVertices(Vector3f[])
     */
    public final EasingCurve<Vector3f> setVertex1(EasingCurve<Vector3f> newVertex) {
        EasingCurve<Vector3f> prevVertex = this.vertex1;
        this.vertex1 = newVertex;
        return prevVertex;
    }

    /**
     * Sets the second individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}. This method overload will set a constant value
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleQuad#setVertices(Vector3f[])
    */
    public final EasingCurve<Vector3f> setVertex2(Vector3f newVertex) {
        return this.setVertex2(new ConstantEasingCurve<>(newVertex));
    }

    /**
     * Sets the second individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}. This method overload will set an ease curve value
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleQuad#setVertices(Vector3f[])
     */
    public final EasingCurve<Vector3f> setVertex2(EasingCurve<Vector3f> newVertex) {
        EasingCurve<Vector3f> prevVertex = this.vertex2;
        this.vertex2 = newVertex;
        return prevVertex;
    }

    /**
     * Sets the third individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}. This method overload will set a constant value
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleQuad#setVertices(Vector3f[])
    */
    public final EasingCurve<Vector3f> setVertex3(Vector3f newVertex) {
        return this.setVertex3(new ConstantEasingCurve<>(newVertex));
    }

    /**
     * Sets the third individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}. This method overload will set a constant value
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleQuad#setVertices(Vector3f[])
     */
    public final EasingCurve<Vector3f> setVertex3(EasingCurve<Vector3f> newVertex) {
        EasingCurve<Vector3f> prevVertex = this.vertex3;
        this.vertex3 = newVertex;
        return prevVertex;
    }

    /**
     * Sets the fourth individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}. This method overload will set a constant value
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleQuad#setVertices(Vector3f[])
    */
    public final EasingCurve<Vector3f> setVertex4(Vector3f newVertex) {
        return this.setVertex4(new ConstantEasingCurve<>(newVertex));
    }

    /**
     * Sets the fourth individual vertex, it returns the previous vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}. This method overload will set an ease curve value
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleQuad#setVertices(Vector3f[])
     */
    public final EasingCurve<Vector3f> setVertex4(EasingCurve<Vector3f> newVertex) {
        EasingCurve<Vector3f> prevVertex = this.vertex4;
        this.vertex4 = newVertex;
        return prevVertex;
    }

    /** Gets the first individual vertex.
     *
     * @return The first individual vertex
    */
    protected EasingCurve<Vector3f> getVertex1() {
        return this.vertex1;
    }

    /** Gets the second individual vertex.
     *
     * @return The second individual vertex
    */
    protected EasingCurve<Vector3f> getVertex2() {
        return this.vertex2;
    }

    /** Gets the third individual vertex.
     *
     * @return The third individual vertex
    */
    protected EasingCurve<Vector3f> getVertex3() {
        return this.vertex3;
    }

    /** Gets the fourth individual vertex.
     *
     * @return The fourth individual vertex
    */
    protected EasingCurve<Vector3f> getVertex4() {
        return this.vertex4;
    }

    @Override
    protected ComputedEasingRPO computeAdditionalEasings(ComputedEasingRPO container) {
        return container.addComputedField("vertex1", this.vertex1)
                .addComputedField("vertex2", this.vertex2)
                .addComputedField("vertex3", this.vertex3)
                .addComputedField("vertex4", this.vertex4);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext<ComputedEasingRPO> drawContext, Vector3f actualSize) {
        ComputedEasingRPO computedEasing = drawContext.getComputedEasings();

        // Defensive copy of `drawPos`
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(computedEasing.computedOffset);
        Vector3f currVertex1 = (Vector3f) computedEasing.getComputedField("vertex1");
        Vector3f currVertex2 = (Vector3f) computedEasing.getComputedField("vertex2");
        Vector3f currVertex3 = (Vector3f) computedEasing.getComputedField("vertex3");
        Vector3f currVertex4 = (Vector3f) computedEasing.getComputedField("vertex4");
        currVertex1 = currVertex1.mul(actualSize);
        currVertex2 = currVertex2.mul(actualSize);
        currVertex3 = currVertex3.mul(actualSize);
        currVertex4 = currVertex4.mul(actualSize);

        Vector3f computedRotation = computedEasing.computedRotation;
        int computedAmount = computedEasing.computedAmount;

        int step = drawContext.getCurrentStep();
        renderer.drawLine(this.particleEffect, step, objectDrawPos, currVertex1, currVertex2, computedRotation, computedAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, currVertex2, currVertex3, computedRotation, computedAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, currVertex3, currVertex4, computedRotation, computedAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, currVertex4, currVertex1, computedRotation, computedAmount);
    }

    public static class Builder<B extends Builder<B>> extends RenderableParticleObject.Builder<B, ParticleQuad> {
        protected Vector3f vertex1;
        protected Vector3f vertex2;
        protected Vector3f vertex3;
        protected Vector3f vertex4;

        private Builder() {}

        /**
         * Sets the vertices to produce a rectangle in the XY-plane.  This method is not cumulative; repeated calls to
         * this method or the four vertex methods will overwrite values.
         */
        // Constructor helper to transform width/height into vertices
        public B rectangle(float width, float height) {
            this.vertex1 = new Vector3f(width / 2, height / 2, 0);
            this.vertex2 = new Vector3f(width / 2, -height / 2, 0);
            this.vertex3 = new Vector3f(-width / 2, -height / 2, 0);
            this.vertex4 = new Vector3f(-width / 2, height / 2, 0);
            return self();
        }

        /**
         * Set vertex1 on the builder.  This method is not cumulative; repeated calls to {@code rectangle} or this
         * method will overwrite the value.
         */
        public B vertex1(Vector3f vertex1) {
            this.vertex1 = vertex1;
            return self();
        }

        /**
         * Set vertex2 on the builder.  This method is not cumulative; repeated calls to {@code rectangle} or this
         * method will overwrite the value.
         */
        public B vertex2(Vector3f vertex2) {
            this.vertex2 = vertex2;
            return self();
        }

        /**
         * Set vertex3 on the builder.  This method is not cumulative; repeated calls to {@code rectangle} or this
         * method will overwrite the value.
         */
        public B vertex3(Vector3f vertex3) {
            this.vertex3 = vertex3;
            return self();
        }

        /**
         * Set vertex4 on the builder.  This method is not cumulative; repeated calls to {@code rectangle} or this
         * method will overwrite the value.
         */
        public B vertex4(Vector3f vertex4) {
            this.vertex4 = vertex4;
            return self();
        }

        @Override
        public ParticleQuad build() {
            return new ParticleQuad(this);
        }
    }
}
