package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.CommonUtils;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

/** The particle object class that represents a 2D triangle. It has 3
 * vertices that make it up, all of them must be coplanar with each other.
 * The vertices can be set individually or by supplying a list of 4 vertices
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleTriangle extends ParticleObject {
    public DrawInterceptor<ParticleTriangle, emptyData> afterCalcsIntercept;
    public DrawInterceptor<ParticleTriangle, emptyData> beforeCalcsIntercept;

    private final CommonUtils commonUtils = new CommonUtils();

    /** There is no data being transmitted */
    public enum emptyData {}

    protected Vector3f vertex1;
    protected Vector3f vertex2;
    protected Vector3f vertex3;

    private final IllegalArgumentException UNBALANCED_VERTICES = new IllegalArgumentException(
            "Unbalanced vertices, there must be only 3 vertices"
    );

    /** Constructor for the particle triangle which is a 2D shape. It accepts as parameters
     * the particle to use, the vertices that connect the triangle, the amount of particles & the
     * rotation to apply There is also a simplified version for no rotation.
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param vertices The vertices that make up the triangle
     * @param rotation The rotation to apply
     *
     * @see ParticleTriangle#ParticleTriangle(ParticleEffect, Vector3f[], int)
    */
    public ParticleTriangle(ParticleEffect particle, Vector3f[] vertices, int amount, Vector3f rotation) {
        super(particle, rotation);
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
     * the particle to use, the vertices that connect the triangle & the amount of particles.
     * It is a simplified version for the case when no rotation is meant to be applied.
     * For rotation offset you can use another constructor
     *
     * @param particle The particle to use
     * @param vertices The vertices that make up the triangle
     * @param amount The amount of particles for the object
     *
     * @see ParticleTriangle#ParticleTriangle(ParticleEffect, Vector3f[], int, Vector3f)
    */
    public ParticleTriangle(ParticleEffect particle, Vector3f[] vertices, int amount) {
        super(particle);
        if (vertices.length != 3) {
            throw UNBALANCED_VERTICES;
        }
        this.checkValidTriangle(vertices);
        this.vertex1 = vertices[0];
        this.vertex2 = vertices[1];
        this.vertex3 = vertices[2];
        this.amount = amount;
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
        this.beforeCalcsIntercept = triangle.beforeCalcsIntercept;
        this.afterCalcsIntercept = triangle.afterCalcsIntercept;
    }

    private void checkValidTriangle(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3) {
        float dotProduct1 = vertex3.dot(vertex1.cross(vertex2));
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
        this.checkValidTriangle(this.vertex2, vertex2, this.vertex3);
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

    /** Sets the individual vertices all at once. If you want set one vertex at a time
     * then its recommend to use the methods ``setVertex1``, ``setVertex2``.... etc.
     * The vertices have to be 4 in order to modify the values. It returns nothing back
     *
     * @param vertices The vertices to modify
     *
     * @throws IllegalArgumentException if the amount of vertices supplied isn't equal to 4
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
    public void draw(ServerWorld world, int step, Vector3f drawPos) {
        InterceptedResult<ParticleTriangle, emptyData> modifiedBefore =
                this.interceptDrawCalcBefore(world, step, drawPos, this);
        ParticleTriangle objectToUse = modifiedBefore.object;
        drawPos = drawPos.add(this.offset);
        Vector3f vertex1 = this.vertex1.add(drawPos);
        Vector3f vertex2 = this.vertex2.add(drawPos);
        Vector3f vertex3 = this.vertex3.add(drawPos);
        commonUtils.drawLine(objectToUse, world, vertex1, vertex2, objectToUse.amount);
        commonUtils.drawLine(objectToUse, world, vertex2, vertex3, objectToUse.amount);
        commonUtils.drawLine(objectToUse, world, vertex3, vertex1, objectToUse.amount);
        this.interceptDrawCalcAfter(world, step, drawPos, this);
        this.endDraw(world, step, drawPos);
    }

    private InterceptedResult<ParticleTriangle, emptyData> interceptDrawCalcAfter(
            ServerWorld world, int step, Vector3f pos, ParticleTriangle obj
    ) {
        InterceptData<emptyData> interceptData = new InterceptData<>(world, pos, step, emptyData.class);
        if (this.afterCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.afterCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleTriangle, emptyData> interceptDrawCalcBefore(
            ServerWorld world, int step, Vector3f pos, ParticleTriangle obj
    ) {
        InterceptData<emptyData> interceptData = new InterceptData<>(world, pos, step, emptyData.class);
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeCalcsIntercept.apply(interceptData, obj);
    }
}
