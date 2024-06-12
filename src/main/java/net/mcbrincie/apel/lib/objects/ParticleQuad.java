package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.CommonUtils;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

import java.util.Optional;


/** The particle object class that represents a 2D quad. It is a little
 * more complex than a line, since the user has to care about 4 endpoints(if
 * they don't want a square, otherwise they can supply a width & height).
 * <br>
 * The quadrilateral is drawn in order of the vertices provided (presume the line
 * from vertex2 to vertex3 is straight, ignoring the limitations of ASCII art):
 * <pre>
 *     vertex1 ------------------ vertex2
 *        \                          |
 *         \                         |
 *          \                        |
 *        vertex4 --------------- vertex3
 * </pre><br>
*  Of course a quad can also be a rectangle, a trapezoid and anything else in between
 */
@SuppressWarnings("unused")
public class ParticleQuad extends ParticleObject {
    private static final Logger LOGGER = LogManager.getLogger();

    protected Vector3f vertex1;
    protected Vector3f vertex2;
    protected Vector3f vertex3;
    protected Vector3f vertex4;

    private DrawInterceptor<ParticleQuad, AfterDrawData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleQuad, BeforeDrawData> beforeDraw = DrawInterceptor.identity();

    /** There is no data being transmitted */
    public enum BeforeDrawData {}

    /** This data is used after calculations(it contains the modified 4 vertices) */
    public enum AfterDrawData {
        VERTEX_1, VERTEX_2, VERTEX_3, VERTEX_4
    }

    private final CommonUtils commonUtils = new CommonUtils();


    /** Constructor for the particle quad which is a 2D Quadrilateral. It accepts as parameters
     * the particle to use, the vertices coordinate, the amount of particles & the rotation to apply. There is also
     * a simplified version for no rotation.
     *
     * @param particleEffect The particle to use
     * @param amount The amount of particles for the object
     * @param vertices The vertices coordinate
     * @param rotation The rotation to apply
     *
     * @see ParticleQuad#ParticleQuad(ParticleEffect, Vector3f[], int)
    */
    public ParticleQuad(ParticleEffect particleEffect, Vector3f[] vertices, int amount, Vector3f rotation) {
        super(particleEffect, rotation);
        this.setVertices(vertices);
        this.setAmount(amount);
    }

    /** Constructor for the particle quad which is a 2D Quadrilateral. It accepts as parameters
     * the particle to use, the vertices coordinate & the amount of particles. There is also
     * a constructor that allows supplying rotation
     *
     * @param particleEffect The particle to use
     * @param amount The amount of particles for the object
     * @param vertices The vertices coordinate
     *
     * @see ParticleQuad#ParticleQuad(ParticleEffect, Vector3f[], int, Vector3f)
    */
    public ParticleQuad(ParticleEffect particleEffect, Vector3f[] vertices, int amount) {
        this(particleEffect, vertices, amount, new Vector3f(0));
    }

    /** Constructor for the particle quad which is a 2D Quadrilateral. And more specifically
     * a rectangle(or square if width = height). It accepts as parameters the particle to use,
     * the width of the rectangle, the height of the rectangle & the amount of particles.
     * There is also a constructor that allows supplying rotation
     *
     * @param particleEffect The particle to use
     * @param amount The amount of particles for the object
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     *
     * @see ParticleQuad#ParticleQuad(ParticleEffect, float, float, int, Vector3f)
    */
    public ParticleQuad(ParticleEffect particleEffect, float width, float height, int amount) {
        this(particleEffect, rectangle(width, height), amount, new Vector3f(0));
    }

    /** Constructor for the particle quad which is a 2D Quadrilateral. And more specifically
     * a rectangle(or square if width = height). It accepts as parameters the particle to use,
     * the width of the rectangle, the height of the rectangle, the amount of particles.
     * & the rotation to apply. There is also a constructor for no rotation
     *
     * @param particleEffect The particle to use
     * @param amount The amount of particles for the object
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param rotation The rotation to apply
     *
     * @see ParticleQuad#ParticleQuad(ParticleEffect, float, float, int, Vector3f)
    */
    public ParticleQuad(ParticleEffect particleEffect, float width, float height, int amount, Vector3f rotation) {
        this(particleEffect, rectangle(width, height), amount, rotation);
    }

    // Constructor helper to transform width/height into vertices
    private static Vector3f[] rectangle(float width, float height) {
        Vector3f vertex1 = new Vector3f(width / 2, height / 2, 0);
        Vector3f vertex2 = new Vector3f(width / 2, -height / 2, 0);
        Vector3f vertex3 = new Vector3f(-width / 2, -height / 2, 0);
        Vector3f vertex4 = new Vector3f(-width / 2, height / 2, 0);
        return new Vector3f[]{vertex1, vertex2, vertex3, vertex4};
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
        this.beforeDraw = quad.beforeDraw;
        this.afterDraw = quad.afterDraw;
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
        Vector3f prevVertex = this.vertex3;
        this.vertex3 = newVertex;
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
    protected Vector3f getVertex1() {
        return this.vertex1;
    }

    /** Gets the second individual vertex
     *
     * @return The second individual vertex
    */
    protected Vector3f getVertex2() {
        return this.vertex2;
    }

    /** Gets the third individual vertex
     *
     * @return The third individual vertex
    */
    protected Vector3f getVertex3() {
        return this.vertex3;
    }

    /** Gets the fourth individual vertex
     *
     * @return The fourth individual vertex
    */
    protected Vector3f getVertex4() {
        return this.vertex4;
    }

    @Override
    public void draw(ServerWorld world, int step, Vector3f drawPos) {
        float rotX = this.rotation.x;
        float rotY = this.rotation.y;
        float rotZ = this.rotation.z;
        this.beforeDraw(world, step);
        // Note the defensive copies prior to rotating and adding
        Vector3f alteredVertex1 = new Vector3f(this.vertex1).rotateZ(rotZ).rotateY(rotY).rotateX(rotX).add(drawPos).add(this.offset);
        Vector3f alteredVertex2 = new Vector3f(this.vertex2).rotateZ(rotZ).rotateY(rotY).rotateX(rotX).add(drawPos).add(this.offset);
        Vector3f alteredVertex3 = new Vector3f(this.vertex3).rotateZ(rotZ).rotateY(rotY).rotateX(rotX).add(drawPos).add(this.offset);
        Vector3f alteredVertex4 = new Vector3f(this.vertex4).rotateZ(rotZ).rotateY(rotY).rotateX(rotX).add(drawPos).add(this.offset);
        commonUtils.drawLine(this, world, alteredVertex1, alteredVertex2, this.amount);
        commonUtils.drawLine(this, world, alteredVertex2, alteredVertex3, this.amount);
        commonUtils.drawLine(this, world, alteredVertex3, alteredVertex4, this.amount);
        commonUtils.drawLine(this, world, alteredVertex4, alteredVertex1, this.amount);
        this.doAfterDraw(world, step, alteredVertex1, alteredVertex2, alteredVertex3, alteredVertex4);
        this.endDraw(world, step, drawPos);
    }

    /** Sets the after draw interceptor, the method executes right after the particle quad
     * is drawn onto the screen. It has the four modified vertices to attach.
     *
     * @param afterDraw The new interceptor to use
     */
    public void setAfterDraw(DrawInterceptor<ParticleQuad, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(
            ServerWorld world, int step, Vector3f alteredVertex1, Vector3f alteredVertex2, Vector3f alteredVertex3, Vector3f alteredVertex4
    ) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, null, step, AfterDrawData.class);
        interceptData.addMetadata(AfterDrawData.VERTEX_1, alteredVertex1);
        interceptData.addMetadata(AfterDrawData.VERTEX_2, alteredVertex2);
        interceptData.addMetadata(AfterDrawData.VERTEX_3, alteredVertex3);
        interceptData.addMetadata(AfterDrawData.VERTEX_4, alteredVertex4);
        this.afterDraw.apply(interceptData, this);
    }

    /** Sets the before draw interceptor, the method executes right before the particle quad
     * is drawn onto the screen. It has no data attached.
     *
     * @param beforeDraw The new interceptor to use
     */
    public void setBeforeDraw(DrawInterceptor<ParticleQuad, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void beforeDraw(ServerWorld world, int step) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, null, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }
}
