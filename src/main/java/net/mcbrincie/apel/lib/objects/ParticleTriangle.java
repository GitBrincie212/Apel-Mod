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

/** The particle object class that represents a 2D triangle.
 * It has three vertices that make it up, all of them must be coplanar with each other.
 * The vertices can be set individually or by supplying a list of four vertices
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleTriangle extends ParticleObject {
    private DrawInterceptor<ParticleTriangle, AfterDrawData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleTriangle, BeforeDrawData> beforeDraw = DrawInterceptor.identity();

    /** There is no data being transmitted */
    public enum BeforeDrawData {}
    public enum AfterDrawData {}

    protected Vector3f vertex1;
    protected Vector3f vertex2;
    protected Vector3f vertex3;

    private final IllegalArgumentException UNBALANCED_VERTICES = new IllegalArgumentException(
            "Unbalanced vertices, there must be only 3 vertices"
    );

    /** Constructor for the particle triangle which is a 2D shape. It accepts as parameters
     * the particle effect to use, the vertices that connect the triangle, the number of particles & the
     * rotation to apply There is also a simplified version for no rotation.
     *
     * @param particleEffect The particle to use
     * @param amount The number of particles for the object
     * @param vertices The vertices that make up the triangle
     * @param rotation The rotation to apply
     *
     * @see ParticleTriangle#ParticleTriangle(ParticleEffect, Vector3f[], int)
    */
    public ParticleTriangle(ParticleEffect particleEffect, Vector3f[] vertices, int amount, Vector3f rotation) {
        super(particleEffect, rotation);
        if (vertices.length != 3) {
            throw UNBALANCED_VERTICES;
        }
        this.checkValidTriangle(vertices);
        this.vertex1 = vertices[0];
        this.vertex2 = vertices[1];
        this.vertex3 = vertices[2];
        this.amount = amount;
    }

    /** Constructor for the particle triangle which is a 2D shape. It accepts as parameters
     * the particle to use, the vertices that connect the triangle & the number of particles.
     * It is a simplified version for the case when no rotation is meant to be applied.
     * For rotation offset, you can use another constructor
     *
     * @param particleEffect The particle to use
     * @param vertices The vertices that make up the triangle
     * @param amount The number of particles for the object
     *
     * @see ParticleTriangle#ParticleTriangle(ParticleEffect, Vector3f[], int, Vector3f)
    */
    public ParticleTriangle(ParticleEffect particleEffect, Vector3f[] vertices, int amount) {
        this(particleEffect, vertices, amount, new Vector3f(0));
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param triangle The particle triangle object to copy from
    */
    public ParticleTriangle(ParticleTriangle triangle) {
        super(triangle);
        this.vertex1 = triangle.vertex1;
        this.vertex2 = triangle.vertex2;
        this.vertex3 = triangle.vertex3;
        this.beforeDraw = triangle.beforeDraw;
        this.afterDraw = triangle.afterDraw;
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

    /** Sets the first individual vertex, it returns the previous
     * vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTriangle#setVertices(Vector3f...)
     */
    public Vector3f setVertex1(Vector3f newVertex) {
        Vector3f prevVertex1 = this.vertex1;
        this.checkValidTriangle(vertex1, this.vertex2, this.vertex3);
        this.vertex1 = newVertex;
        return prevVertex1;
    }

    /** Sets the second individual vertex, it returns the previous
     * vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTriangle#setVertices(Vector3f...)
     */
    public Vector3f setVertex2(Vector3f newVertex) {
        Vector3f prevVertex2 = this.vertex2;
        this.checkValidTriangle(this.vertex1, vertex2, this.vertex3);
        this.vertex2 = newVertex;
        return prevVertex2;
    }

    /** Sets the third individual vertex, it returns the previous
     * vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTriangle#setVertices(Vector3f...)
     */
    public Vector3f setVertex3(Vector3f newVertex) {
        Vector3f prevVertex3 = this.vertex3;
        this.checkValidTriangle(this.vertex1, this.vertex2, vertex3);
        this.vertex3 = newVertex;
        return prevVertex3;
    }

    /** Sets the individual vertices all at once given a list of the vertices.
     * If you want to set one vertex at a time, then its recommend to use the methods ``setVertex1``,
     * ``setVertex2``... etc. The vertices have to be 4 to modify the values.
     * It returns nothing
     *
     * @param vertices The vertices to modify
     *
     * @throws IllegalArgumentException if the number of vertices supplied isn't equal to 4
    */
    public void setVertices(Vector3f... vertices) {
        if (vertices.length != 3) {
            throw UNBALANCED_VERTICES;
        }
        this.checkValidTriangle(vertices);
        this.vertex1 = vertices[0];
        this.vertex2 = vertices[1];
        this.vertex3 = vertices[2];
    }

    /** Gets the first individual vertex
     *
     * @return The first individual vertex
    */
    public Vector3f getVertex1() {return this.vertex1;}

    /** Gets the second individual vertex
     *
     * @return The second individual vertex
    */
    public Vector3f getVertex2() {return this.vertex2;}

    /** Gets the third individual vertex
     *
     * @return The third individual vertex
    */
    public Vector3f getVertex3() {return this.vertex3;}

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

        this.drawLine(renderer, v1, v2, step, this.amount);
        this.drawLine(renderer, v2, v3, step, this.amount);
        this.drawLine(renderer, v3, v1, step, this.amount);

        this.doAfterDraw(renderer.getServerWorld(), step, drawPos);
        this.endDraw(renderer, step, drawPos);
    }

    /** Set the interceptor to run after drawing the triangle.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the position where the triangle is rendered, and the
     * step number of the animation.  There is no other data attached.
     *
     * @param afterDraw the new interceptor to execute prior to drawing the triangle
     */
    public void setAfterDraw(DrawInterceptor<ParticleTriangle, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step, Vector3f pos) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, pos, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /** Set the interceptor to run prior to drawing the triangle.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the position where the triangle is rendered, and the
     * step number of the animation.  There is no other data attached.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing the triangle
     */
    public void setBeforeDraw(DrawInterceptor<ParticleTriangle, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void doBeforeDraw(ServerWorld world, int step, Vector3f pos) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, pos, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }
}
