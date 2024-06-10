package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.CommonUtils;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;


/** The particle object class that represents a 2D quad. It is a little
 * more complex than a line, since the user has to care about 4 endpoints(if
 * they don't want a square, otherwise they can supply a width & height).
 */
@SuppressWarnings("unused")
public class ParticleQuad extends ParticleObject {
    protected Vector3f vertex1;
    protected Vector3f vertex2;
    protected Vector3f vertex3;
    protected Vector3f vertex4;

    public DrawInterceptor<ParticleQuad, afterCalcData> afterCalcsIntercept;
    public DrawInterceptor<ParticleQuad, emptyData> beforeCalcsIntercept;

    /** There is no data being transmitted */
    public enum emptyData {}

    /** This data is used after calculations(it contains the modified 4 vertices) */
    public enum afterCalcData {
        VERTEX_1, VERTEX_2, VERTEX_3, VERTEX_4
    }

    private final CommonUtils commonUtils = new CommonUtils();


    /** Constructor for the particle quad which is a 2D Quadrilateral. It accepts as parameters
     * the particle to use, the vertices coordinate, the amount of particles & the rotation to apply. There is also
     * a simplified version for no rotation.
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param vertices The vertices coordinate
     * @param rotation The rotation to apply
     *
     * @see ParticleQuad#ParticleQuad(ParticleEffect, Vector3f[], int)
    */
    public ParticleQuad(ParticleEffect particle, Vector3f[] vertices, int amount, Vector3f rotation) {
        super(particle, rotation);
        this.setVertices(vertices);
        this.setAmount(amount);
    }

    /** Constructor for the particle quad which is a 2D Quadrilateral. It accepts as parameters
     * the particle to use, the vertices coordinate & the amount of particles. There is also
     * a constructor that allows supplying rotation
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param vertices The vertices coordinate
     *
     * @see ParticleQuad#ParticleQuad(ParticleEffect, Vector3f[], int, Vector3f)
    */
    public ParticleQuad(ParticleEffect particle, Vector3f[] vertices, int amount) {
        super(particle);
        this.setVertices(vertices);
        this.setAmount(amount);
    }

    /** Constructor for the particle quad which is a 2D Quadrilateral. And more specifically
     * a rectangle(or square if width = height). It accepts as parameters the particle to use,
     * the width of the rectangle, the height of the rectangle & the amount of particles.
     * There is also a constructor that allows supplying rotation
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     *
     * @see ParticleQuad#ParticleQuad(ParticleEffect, float, float, int, Vector3f)
    */
    public ParticleQuad(ParticleEffect particle, float width, float height, int amount) {
        super(particle);
        this.vertex1 = new Vector3f(width / 2, height / 2, 0);
        this.vertex2 = new Vector3f(width / 2, -height / 2, 0);
        this.vertex3 = new Vector3f(-width / 2, height / 2, 0);
        this.vertex4 = new Vector3f(-width / 2, -height / 2, 0);
        this.setAmount(amount);
    }

    /** Constructor for the particle quad which is a 2D Quadrilateral. And more specifically
     * a rectangle(or square if width = height). It accepts as parameters the particle to use,
     * the width of the rectangle, the height of the rectangle, the amount of particles.
     * & the rotation to apply. There is also a constructor for no rotation
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param rotation The rotation to apply
     *
     * @see ParticleQuad#ParticleQuad(ParticleEffect, float, float, int, Vector3f)
    */
    public ParticleQuad(ParticleEffect particle, float width, float height, int amount, Vector3f rotation) {
        super(particle, rotation);
        this.vertex1 = new Vector3f(width / 2, height / 2, 0);
        this.vertex2 = new Vector3f(width / 2, -height / 2, 0);
        this.vertex3 = new Vector3f(-width / 2, height / 2, 0);
        this.vertex4 = new Vector3f(-width / 2, -height / 2, 0);
        this.setAmount(amount);
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
        this.beforeCalcsIntercept = quad.beforeCalcsIntercept;
        this.afterCalcsIntercept = quad.afterCalcsIntercept;
    }

    /** Sets the individual vertices all at once. If you want set one vertex at a time
     * then its recommend to use the methods ``setVertex1``, ``setVertex2``.... etc.
     * The vertices have to be 4 in order to modify the values. It returns nothing back
     *
     * @param vertices The vertices to modify
     *
     * @throws IllegalArgumentException if the amount of vertices supplied isn't equal to 4
     */
    public void setVertices(Vector3f[] vertices) throws IllegalArgumentException {
        if (vertices.length != 4) {
            throw new IllegalArgumentException("The amount of vertices must be 4");
        }
        this.vertex1 = vertices[0];
        this.vertex2 = vertices[1];
        this.vertex3 = vertices[2];
        this.vertex4 = vertices[3];
    }

    /** Sets the first individual vertex, it returns the previous
     * vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleQuad#setVertices(Vector3f[])
     */
    public Vector3f setVertex1(Vector3f newVertex) {
        Vector3f prevVertex = this.vertex1;
        this.vertex1 = newVertex;
        return prevVertex;
    }

    /** Sets the second individual vertex, it returns the previous
     * vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleQuad#setVertices(Vector3f[])
    */
    public Vector3f setVertex2(Vector3f newVertex) {
        Vector3f prevVertex = this.vertex2;
        this.vertex2 = newVertex;
        return prevVertex;
    }

    /** Sets the third individual vertex, it returns the previous
     * vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleQuad#setVertices(Vector3f[])
    */
    public Vector3f setVertex3(Vector3f newVertex) {
        Vector3f prevVertex = this.vertex2;
        this.vertex2 = newVertex;
        return prevVertex;
    }

    /** Sets the fourth individual vertex, it returns the previous
     * vertex that was used. If you want to modify multiple
     * vertices at once then use {@code setVertices}
     *
     * @param newVertex The new vertex
     * @return The previous vertex
     *
     * @see ParticleQuad#setVertices(Vector3f[])
    */
    public Vector3f setVertex4(Vector3f newVertex) {
        Vector3f prevVertex = this.vertex4;
        this.vertex4 = newVertex;
        return prevVertex;
    }

    /** Gets the first individual vertex
     *
     * @return The first individual vertex
    */
    protected Vector3f getVertex1() {return this.vertex1;}

    /** Gets the second individual vertex
     *
     * @return The second individual vertex
    */
    protected Vector3f getVertex2() {return this.vertex2;}

    /** Gets the third individual vertex
     *
     * @return The third individual vertex
    */
    protected Vector3f getVertex3() {return this.vertex3;}

    /** Gets the fourth individual vertex
     *
     * @return The fourth individual vertex
    */
    protected Vector3f getVertex4() {return this.vertex4;}

    @Override
    public void draw(ServerWorld world, int step, Vector3f drawPos) {
        float rotX = this.rotation.x;
        float rotY = this.rotation.y;
        float rotZ = this.rotation.z;
        InterceptedResult<ParticleQuad, ParticleQuad.emptyData> modifiedResultBefore =
                this.interceptDrawCalcBefore(world, step, this);
        ParticleQuad objectInUse = modifiedResultBefore.object;
        Vector3f alteredVertex1 = objectInUse.vertex1.rotateZ(rotZ).rotateY(rotX).rotateX(rotX).add(drawPos);
        Vector3f alteredVertex2 = objectInUse.vertex2.rotateZ(rotZ).rotateY(rotX).rotateX(rotX).add(drawPos);
        Vector3f alteredVertex3 = objectInUse.vertex3.rotateZ(rotZ).rotateY(rotX).rotateX(rotX).add(drawPos);
        Vector3f alteredVertex4 = objectInUse.vertex4.rotateZ(rotZ).rotateY(rotX).rotateX(rotX).add(drawPos);
        InterceptedResult<ParticleQuad, ParticleQuad.afterCalcData> modifiedResultAfter = objectInUse.interceptDrawCalcAfter(
                world, step, this, alteredVertex1, alteredVertex2, alteredVertex3, alteredVertex4
        );
        objectInUse = modifiedResultAfter.object;
        alteredVertex1 = (Vector3f) modifiedResultAfter.interceptData.getMetadata(afterCalcData.VERTEX_1);
        alteredVertex2 = (Vector3f) modifiedResultAfter.interceptData.getMetadata(afterCalcData.VERTEX_2);
        alteredVertex3 = (Vector3f) modifiedResultAfter.interceptData.getMetadata(afterCalcData.VERTEX_3);
        alteredVertex4 = (Vector3f) modifiedResultAfter.interceptData.getMetadata(afterCalcData.VERTEX_4);
        alteredVertex1 = alteredVertex1.add(this.offset);
        alteredVertex2 = alteredVertex2.add(this.offset);
        alteredVertex3 = alteredVertex3.add(this.offset);
        alteredVertex4 = alteredVertex4.add(this.offset);
        commonUtils.drawLine(objectInUse, world, alteredVertex1, alteredVertex2, objectInUse.amount);
        commonUtils.drawLine(objectInUse, world, alteredVertex3, alteredVertex4, objectInUse.amount);
        commonUtils.drawLine(objectInUse, world, alteredVertex2, alteredVertex4, objectInUse.amount);
        commonUtils.drawLine(objectInUse, world, alteredVertex1, alteredVertex3, objectInUse.amount);
    }

    private InterceptedResult<ParticleQuad, ParticleQuad.afterCalcData> interceptDrawCalcAfter(
            ServerWorld world, int step, ParticleQuad obj, Vector3f... vertices
    ) {
        InterceptData<ParticleQuad.afterCalcData> interceptData = new InterceptData<>(world, null, step, ParticleQuad.afterCalcData.class);
        interceptData.addMetadata(afterCalcData.VERTEX_1, vertices[0]);
        interceptData.addMetadata(afterCalcData.VERTEX_2, vertices[1]);
        interceptData.addMetadata(afterCalcData.VERTEX_3, vertices[2]);
        interceptData.addMetadata(afterCalcData.VERTEX_4, vertices[3]);
        if (this.afterCalcsIntercept == null) return new InterceptedResult<>(interceptData, obj);
        return this.afterCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleQuad, ParticleQuad.emptyData> interceptDrawCalcBefore(
            ServerWorld world, int step, ParticleQuad obj
    ) {
        InterceptData<ParticleQuad.emptyData> interceptData = new InterceptData<>(world, null, step, ParticleQuad.emptyData.class);
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeCalcsIntercept.apply(interceptData, obj);
    }
}
