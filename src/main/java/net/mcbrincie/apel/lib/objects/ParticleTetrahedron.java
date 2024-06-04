package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.CommonUtils;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

/** The particle object class that represents a 3D triangle(Tetrahedron). It has 4
 * vertices that make it up which must not be coplanar with each other. The vertices
 * can be set individually or by supplying a list of 4 vertices
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleTetrahedron extends ParticleObject{
    public DrawInterceptor<ParticleTetrahedron, emptyData> afterCalcsIntercept;
    public DrawInterceptor<ParticleTetrahedron, emptyData> beforeCalcsIntercept;

    private final CommonUtils commonUtils = new CommonUtils();

    /** There is no data being transmitted */
    public enum emptyData {}

    protected Vector3f vertex1;
    protected Vector3f vertex2;
    protected Vector3f vertex3;
    protected Vector3f vertex4;

    private final IllegalArgumentException UNBALANCED_VERTICES = new IllegalArgumentException(
            "Unbalanced vertices, there must be only 4 vertices"
    );

    /** Constructor for the particle tetrahedron which is a 3D triangle. It accepts as parameters
     * the particle to use, the vertices that connect the tetrahedron, the amount of particles & the
     * rotation to apply There is also a simplified version for no rotation.
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param vertices The vertices that make up the tetrahedron
     * @param rotation The rotation to apply
     *
     * @see ParticleTetrahedron#ParticleTetrahedron(ParticleEffect, Vector3f[], int)
    */
    public ParticleTetrahedron(ParticleEffect particle, Vector3f[] vertices, int amount, Vector3f rotation) {
        super(particle, rotation);
        if (vertices.length != 4) {
            throw UNBALANCED_VERTICES;
        }
        this.checkValidTetrahedron(vertices);
        this.vertex1 = vertices[0];
        this.vertex2 = vertices[1];
        this.vertex3 = vertices[2];
        this.vertex4 = vertices[3];
        this.amount = amount;
    }

    /** Constructor for the particle tetrahedron which is a 3D triangle. It accepts as parameters
     * the particle to use, the vertices that connect the tetrahedron & the amount of particles.
     * It is a simplified version for the case when no rotation is meant to be applied.
     * For rotation offset you can use another constructor
     *
     * @param particle The particle to use
     * @param vertices The vertices that make up the tetrahedron
     * @param amount The amount of particles for the object
     *
     * @see ParticleTetrahedron#ParticleTetrahedron(ParticleEffect, Vector3f[], int, Vector3f)
    */
    public ParticleTetrahedron(ParticleEffect particle, Vector3f[] vertices, int amount) {
        super(particle);
        if (vertices.length != 4) {
            throw UNBALANCED_VERTICES;
        }
        this.checkValidTetrahedron(vertices);
        this.vertex1 = vertices[0];
        this.vertex2 = vertices[1];
        this.vertex3 = vertices[2];
        this.vertex4 = vertices[3];
        this.amount = amount;
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param tetrahedron The particle tetrahedron object to copy from
    */
    public ParticleTetrahedron(ParticleTetrahedron tetrahedron) {
        super(tetrahedron);
        this.vertex1 = tetrahedron.vertex1;
        this.vertex2 = tetrahedron.vertex2;
        this.vertex3 = tetrahedron.vertex3;
        this.vertex4 = tetrahedron.vertex4;
        this.beforeCalcsIntercept = tetrahedron.beforeCalcsIntercept;
        this.afterCalcsIntercept = tetrahedron.afterCalcsIntercept;
    }

    private void checkValidTetrahedron(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f vertex4) {
        float result = ((vertex2.sub(vertex1).cross(vertex3.sub(vertex1))).dot(vertex4.sub(vertex1)));
        if (Math.abs(result) > 0.0001f) {
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

    /** Sets the first individual vertex, it returns the previous
     * vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}
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

    /** Sets the second individual vertex, it returns the previous
     * vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTetrahedron#setVertices(Vector3f...)
     */
    public Vector3f setVertex2(Vector3f newVertex) {
        Vector3f prevVertex2 = this.vertex2;
        this.checkValidTetrahedron(this.vertex2, vertex2, this.vertex3, this.vertex4);
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
     * @see ParticleTetrahedron#setVertices(Vector3f...)
     */
    public Vector3f setVertex3(Vector3f newVertex) {
        Vector3f prevVertex3 = this.vertex3;
        this.checkValidTetrahedron(this.vertex1, this.vertex2, vertex3, this.vertex4);
        this.vertex3 = newVertex;
        return prevVertex3;
    }

    /** Sets the third individual vertex, it returns the previous
     * vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleTetrahedron#setVertices(Vector3f...)
    */
    public Vector3f setVertex4(Vector3f newVertex) {
        Vector3f prevVertex4 = this.vertex4;
        this.checkValidTetrahedron(this.vertex2, this.vertex2, this.vertex3, vertex4);
        this.vertex4 = newVertex;
        return prevVertex4;
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
        if (vertices.length != 4) {
            throw UNBALANCED_VERTICES;
        }
        this.checkValidTetrahedron(vertices);
        this.vertex1 = vertices[0];
        this.vertex2 = vertices[1];
        this.vertex3 = vertices[2];
        this.vertex4 = vertices[3];
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

    /** Gets the fourth individual vertex
     *
     * @return The fourth individual vertex
    */
    public Vector3f getVertex4() {return this.vertex4;}

    @Override
    public void draw(ServerWorld world, int step, Vector3f pos) {
        InterceptedResult<ParticleTetrahedron, emptyData> modifiedBefore =
                this.interceptDrawCalcBefore(world, step, pos, this);
        ParticleTetrahedron objectToUse = modifiedBefore.object;
        Vector3f vertex0 = this.vertex1.add(pos);
        Vector3f vertex1 = this.vertex2.add(pos);
        Vector3f vertex2 = this.vertex3.add(pos);
        Vector3f vertex3 = this.vertex4.add(pos);
        commonUtils.drawLine(this, world, vertex0, vertex1, this.amount);
        commonUtils.drawLine(this, world, vertex0, vertex2, this.amount);
        commonUtils.drawLine(this, world, vertex0, vertex3, this.amount);
        commonUtils.drawLine(this, world, vertex1, vertex2, this.amount);
        commonUtils.drawLine(this, world, vertex1, vertex3, this.amount);
        commonUtils.drawLine(this, world, vertex2, vertex3, this.amount);
        this.interceptDrawCalcAfter(world, step, pos, this);
        this.endDraw(world, step, pos);
    }

    private InterceptedResult<ParticleTetrahedron, emptyData> interceptDrawCalcAfter(
            ServerWorld world, int step, Vector3f pos, ParticleTetrahedron obj
    ) {
        InterceptData<emptyData> interceptData = new InterceptData<>(world, pos, step, emptyData.class);
        if (this.afterCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.afterCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleTetrahedron, emptyData> interceptDrawCalcBefore(
            ServerWorld world, int step, Vector3f pos, ParticleTetrahedron obj
    ) {
        InterceptData<emptyData> interceptData = new InterceptData<>(world, pos, step, emptyData.class);
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeCalcsIntercept.apply(interceptData, obj);
    }
}
