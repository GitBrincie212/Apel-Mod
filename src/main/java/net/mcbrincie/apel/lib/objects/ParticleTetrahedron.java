package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.Optional;

/** The particle object class that represents a tetrahedron which may be irregular.
 *  It has four vertices that make it up which must not be coplanar with each other.
 *  The vertices can be set individually or by supplying a list of four vertices.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleTetrahedron extends ParticleObject {
    protected Vector3f vertex1;
    protected Vector3f vertex2;
    protected Vector3f vertex3;
    protected Vector3f vertex4;

    private DrawInterceptor<ParticleTetrahedron, AfterDrawData> afterDraw;
    private DrawInterceptor<ParticleTetrahedron, BeforeDrawData> beforeDraw;

    /** There is no data being transmitted */
    public enum BeforeDrawData {}
    public enum AfterDrawData {}

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleTetrahedron(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount);
        this.setVertices(builder.vertex1, builder.vertex2, builder.vertex3, builder.vertex4);
        this.setAfterDraw(builder.afterDraw);
        this.setBeforeDraw(builder.beforeDraw);
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
        this.beforeDraw = tetrahedron.beforeDraw;
        this.afterDraw = tetrahedron.afterDraw;
    }

    private void checkValidTetrahedron(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f vertex4) {
        // Defensive copies prior to subtraction and cross-product, which are done in-place.
        Vector3f v1 = new Vector3f(vertex1);
        Vector3f v2 = new Vector3f(vertex2);
        Vector3f v3 = new Vector3f(vertex3);
        Vector3f v4 = new Vector3f(vertex4);
        float result = ((v2.sub(v1).cross(v3.sub(v1))).dot(v4.sub(v1)));
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
        Vector3f prevVertex1 = this.vertex1;
        this.checkValidTetrahedron(vertex1, this.vertex2, this.vertex3, this.vertex4);
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
        Vector3f prevVertex2 = this.vertex2;
        this.checkValidTetrahedron(this.vertex1, vertex2, this.vertex3, this.vertex4);
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
        Vector3f prevVertex3 = this.vertex3;
        this.checkValidTetrahedron(this.vertex1, this.vertex2, vertex3, this.vertex4);
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
        Vector3f prevVertex4 = this.vertex4;
        this.checkValidTetrahedron(this.vertex1, this.vertex2, this.vertex3, vertex4);
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
    public void draw(ApelServerRenderer renderer, int step, Vector3f drawPos) {
        this.doBeforeDraw(renderer.getServerWorld(), step, drawPos);

        // Rotation
        Quaternionfc quaternion =
                new Quaternionf().rotateZ(this.rotation.z).rotateY(this.rotation.y).rotateX(this.rotation.x);
        // Defensive copy of `drawPos`
        Vector3f totalOffset = new Vector3f(drawPos).add(this.offset);

        // Defensive copies of internal vertices
        Vector3f v1 = this.rigidTransformation(this.vertex1, quaternion, totalOffset);
        Vector3f v2 = this.rigidTransformation(this.vertex2, quaternion, totalOffset);
        Vector3f v3 = this.rigidTransformation(this.vertex3, quaternion, totalOffset);
        Vector3f v4 = this.rigidTransformation(this.vertex4, quaternion, totalOffset);

        renderer.drawLine(this.particleEffect, step, v1, v2, this.amount);
        renderer.drawLine(this.particleEffect, step, v1, v3, this.amount);
        renderer.drawLine(this.particleEffect, step, v1, v4, this.amount);
        renderer.drawLine(this.particleEffect, step, v2, v3, this.amount);
        renderer.drawLine(this.particleEffect, step, v2, v4, this.amount);
        renderer.drawLine(this.particleEffect, step, v3, v4, this.amount);

        this.doAfterDraw(renderer.getServerWorld(), step, drawPos);
        this.endDraw(renderer, step, drawPos);
    }

    /**
     * Set the interceptor to run after drawing the tetrahedron.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the position where the tetrahedron is rendered, the
     * step number of the animation, and the ParticleTetrahedron instance.  There is no other data attached.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param afterDraw the new interceptor to execute prior to drawing the tetrahedron
     */
    public final void setAfterDraw(DrawInterceptor<ParticleTetrahedron, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step, Vector3f pos) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, pos, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /**
     * Set the interceptor to run prior to drawing the tetrahedron.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the position where the tetrahedron is rendered, and the
     * step number of the animation, and the ParticleTetrahedron instance.  There is no other data attached.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing the tetrahedron
     */
    public final void setBeforeDraw(DrawInterceptor<ParticleTetrahedron, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void doBeforeDraw(ServerWorld world, int step, Vector3f pos) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, pos, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B> {
        protected Vector3f vertex1;
        protected Vector3f vertex2;
        protected Vector3f vertex3;
        protected Vector3f vertex4;
        protected DrawInterceptor<ParticleTetrahedron, AfterDrawData> afterDraw;
        protected DrawInterceptor<ParticleTetrahedron, BeforeDrawData> beforeDraw;

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

        /**
         * Sets the interceptor to run after drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleTetrahedron#setAfterDraw(DrawInterceptor)
         */
        public B afterDraw(DrawInterceptor<ParticleTetrahedron, AfterDrawData> afterDraw) {
            this.afterDraw = afterDraw;
            return self();
        }

        /**
         * Sets the interceptor to run before drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleTetrahedron#setBeforeDraw(DrawInterceptor)
         */
        public B beforeDraw(DrawInterceptor<ParticleTetrahedron, BeforeDrawData> beforeDraw) {
            this.beforeDraw = beforeDraw;
            return self();
        }

        @Override
        public ParticleTetrahedron build() {
            return new ParticleTetrahedron(this);
        }
    }
}
