package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.CommonUtils;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3i;

/** The particle object class that represents a cuboid. Which is a rectangle
 * living in 3D, it can also be a cube if all the values of the size vector
 * are supplied with the same value.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCuboid extends ParticleObject {
    public Vector3f size = new Vector3f();
    protected Vector3i amount;
    public DrawInterceptor<ParticleCuboid, emptyData> afterCalcsIntercept;
    public DrawInterceptor<ParticleCuboid, beforeCalcData> beforeCalcsIntercept;

    private final CommonUtils commonUtils = new CommonUtils();

    /** There is no data being transmitted */
    public enum emptyData {}

    /** This data is used before calculations(it contains the vertices)*/
    public enum beforeCalcData {
        VERTICES,
    }

    /** This enum is used for the getter method of setAmount */
    public enum areaEnum {
        BOTTOM_FACE, TOP_FACE, VERTICAL_BARS, ALL_FACES
    }

    /** Constructor for the particle cuboid which is a 3D rectangle. It accepts as parameters
     * the particle to use, the amount of particles per face section(bottom is X, top is Y and the bars are Z)
     * the size of the cuboid(width, height, depth) & the rotation to apply There is also a simplified version
     * for no rotation.
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param size The size in regard to width, height, depth
     * @param rotation The rotation to apply
     *
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vector3i, Vector3f)
    */
    public ParticleCuboid(ParticleEffect particle, Vector3i amount, @NotNull Vector3f size, Vector3f rotation) {
        super(particle, rotation);
        this.setSize(size);
        this.amount = amount;
    }

    /** Constructor for the particle cuboid which is a 3D rectangle. It accepts as parameters
     * the particle to use, the amount of particles per face section(bottom is X, top is Y and the bars are Z)
     * the size of the cuboid(width, height, depth). It is a simplified version for the case when
     * no rotation is meant to be applied. For rotation offset you can use another constructor
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param size The size in regard to width, height, depth
     *
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vector3i, Vector3f, Vector3f)
     */
    public ParticleCuboid(ParticleEffect particle, Vector3i amount, @NotNull Vector3f size) {
        super(particle);
        this.setSize(size);
        this.amount = amount;
    }

    /** Constructor for the particle cuboid which is a 3D rectangle. It accepts as parameters
     * the particle to use, the amount of particles per face section(bottom is X, top is Y and the bars are Z)
     * the size of the cuboid(width, height, depth). It is a simplified version for the case when
     * no rotation is meant to be applied. This constructor is meant when you want a constant amount of
     * particles per face section. There is a constructor that allows to handle different amounts per face
     * and another that is meant to be used when no rotation is meant to be applied
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param size The size in regard to width, height, depth
     *
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vector3i, Vector3f, Vector3f)
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, int, Vector3f)
     */
    public ParticleCuboid(ParticleEffect particle, int amount, @NotNull Vector3f size, Vector3f rotation) {
        super(particle, rotation);
        this.setSize(size);
        this.amount = new Vector3i(amount, amount, amount);
    }

    /** Constructor for the particle cuboid which is a 3D rectangle. It accepts as parameters
     * the particle to use, the amount of particles per face section(bottom is X, top is Y and the bars are Z)
     * the size of the cuboid(width, height, depth) It is a simplified version for the case when
     * no rotation is meant to be applied. This constructor is meant when you want a constant amount of
     * particles per face section. There is a constructor that allows to handle different amounts per face
     * and another that is meant to be used when no rotation is meant to be applied
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param size The size in regard to width, height, depth
     *
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vector3i, Vector3f)
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, int, Vector3f, Vector3f)
     */
    public ParticleCuboid(ParticleEffect particle, int amount, @NotNull Vector3f size) {
        super(particle);
        this.setSize(size);
        this.amount = new Vector3i(amount, amount, amount);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param cuboid The particle circle object to copy from
    */
    public ParticleCuboid(ParticleCuboid cuboid) {
        super(cuboid);
        this.size = cuboid.size;
        this.beforeCalcsIntercept = cuboid.beforeCalcsIntercept;
        this.afterCalcsIntercept = cuboid.afterCalcsIntercept;
        this.amount = cuboid.amount;
    }

    /** Sets the size of the cuboid object. The X axis corresponds to width,
     * Y axis corresponds to height and Z axis corresponds to depth. There is
     * an additional way to set size via providing a float(which makes a cube)
     *
     *
     * @param size The size(which can produce also a cuboid & a cube)
     * @return The previous size
     * @see ParticleCuboid#setSize(float)
     */
    public Vector3f setSize(Vector3f size) {
        if (size.x <= 0 || size.y <= 0 || size.z <= 0) {
            throw new IndexOutOfBoundsException("One of the size axis is below or equal to zero");
        }
        Vector3f prevSize = new Vector3f(this.size).mul(2);
        this.size = size.mul(0.5f);
        return prevSize;
    }

    /** Sets the size of the cuboid object. The width, height & depth is a constant amount
     *  which makes a cube(and not a cuboid). There is an additional ay to set size via
     *  providing a Vector3f(which can also make a cuboid and not just a cube)
     *
     * @param size The size(which will produce a cube)
     * @return The previous size used
     * @see ParticleCuboid#setSize(Vector3f)
     */
    public Vector3f setSize(float size) {
        if (size <= 0) {
            throw new IndexOutOfBoundsException("Size cannot be below or equal to zero");
        }
        Vector3f prevSize = new Vector3f(this.size).mul(2);
        size /= 2;
        this.size = new Vector3f(size, size, size);
        return prevSize;
    }

    /** THIS METHOD SHOULD NOT BE USED */
    @Override
    @Deprecated
    public int setAmount(int amount) {
        throw new UnsupportedOperationException("The method used is deprecated. It is not meant to be used");
    }

    /** Sets the amount per area. The amount differs from the normal int amount.
     *  The X coordinate dictates the bottom face, the Y coordinate dictates the
     *  top face & the Z coordinate dictates the vertical bars of the cuboid
     *  <br><br>
     *  <b>Note:</b> It is not recommended to use {@link ParticleCuboid#setAmount(int)} as its deprecated
     *
     * @param amount The amount per area
     * @return The previous amount to use
     */
    public Vector3i setAmount(Vector3i amount) {
        if (amount.x <= 0 || amount.y <= 0 || amount.z <= 0) {
            throw new IllegalArgumentException("One of the amount of particles axis is below or equal to 0");
        }
       Vector3i prevAmount = new Vector3i(this.amount);
       this.amount = amount;
       return prevAmount;
    }

    /** THIS METHOD SHOULD NOT BE USED */
    @Override
    @Deprecated
    public int getAmount() {
        throw new UnsupportedOperationException("The method used is deprecated. It is not meant to be used");
    }

    /** Gets the amount of particles. Where each coordinate corresponds to an area,
     *  you can get it via using the area enum
     *
     * @return The amount of that face
    */
    public Vector3i getAmount(areaEnum faceIndex) {
        /*
        Quite junk code but I don't care. I know that I need to favour composition over inheritance.
        But this is just one exception which doesn't make sense to develop a whole composition system
        */
        return (faceIndex == areaEnum.ALL_FACES) ? this.amount :
                (faceIndex == areaEnum.BOTTOM_FACE) ? new Vector3i(this.amount.x, -1, -1) :
                        (faceIndex == areaEnum.TOP_FACE) ? new Vector3i(-1, this.amount.y, -1) :
                                new Vector3i(-1, -1, this.amount.z);
    }

    /** Gets the vertex given the size and the center position
     *
     * @param width The width
     * @param height The height
     * @param depth The depth
     * @param pos The center position
     * @return The vertex's coordinates
     */
    public Vector3f getVertex(float width, float height, float depth, Vector3f pos) {
        Vector3f vertex = new Vector3f(width, height, depth);
        vertex = vertex
                .rotateZ(this.rotation.z)
                .rotateY(this.rotation.y)
                .rotateX(this.rotation.x);
        return vertex.add(pos);
    }

    @Override
    public void draw(ServerWorld world, int step, Vector3f drawPos) {
        float width = size.x;
        float height = size.y;
        float depth = size.z;
        Vector3f vertex1 = this.getVertex(width, height, depth, drawPos);
        Vector3f vertex2 = this.getVertex(width, -height, -depth, drawPos);
        Vector3f vertex3 = this.getVertex(-width, height, -depth, drawPos);
        Vector3f vertex4 = this.getVertex(width, height, -depth, drawPos);
        Vector3f vertex5 = this.getVertex(-width, -height, depth, drawPos);
        Vector3f vertex6 = this.getVertex(width, -height, depth, drawPos);
        Vector3f vertex7 = this.getVertex(-width, height, depth, drawPos);
        Vector3f vertex8 = this.getVertex(-width, -height, -depth, drawPos);

        int bottomFaceAmount = this.amount.x;
        int topFaceAmount = this.amount.y;
        int verticalBarsAmount = this.amount.z;

        Vector3f[] vertices = {vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7, vertex8};
        InterceptedResult<ParticleCuboid, beforeCalcData> modifiedPairBefore =
                this.interceptDrawCalcBefore(world, vertices, step, this);
        vertices = (Vector3f[]) modifiedPairBefore.interceptData.getMetadata(beforeCalcData.VERTICES);
        ParticleCuboid objectInUse = modifiedPairBefore.object;

        vertex1 = vertices[0];
        vertex2 = vertices[1];
        vertex3 = vertices[2];
        vertex4 = vertices[3];
        vertex5 = vertices[4];
        vertex6 = vertices[5];
        vertex7 = vertices[6];
        vertex8 = vertices[7];

        // Bottom Face
        if (bottomFaceAmount != 0) {
            commonUtils.drawLine(objectInUse, world, vertex2, vertex4, bottomFaceAmount);
            commonUtils.drawLine(objectInUse, world, vertex4, vertex3, bottomFaceAmount);
            commonUtils.drawLine(objectInUse, world, vertex3, vertex8, bottomFaceAmount);
            commonUtils.drawLine(objectInUse, world, vertex8, vertex2, bottomFaceAmount);
        }

        // Top Face
        if (topFaceAmount != 0) {
            commonUtils.drawLine(objectInUse, world, vertex1, vertex7, topFaceAmount);
            commonUtils.drawLine(objectInUse, world, vertex7, vertex5, topFaceAmount);
            commonUtils.drawLine(objectInUse, world, vertex5, vertex6, topFaceAmount);
            commonUtils.drawLine(objectInUse, world, vertex6, vertex1, topFaceAmount);
        }
        // Vertical
        if (verticalBarsAmount != 0) {
            commonUtils.drawLine(objectInUse, world, vertex5, vertex8, verticalBarsAmount);
            commonUtils.drawLine(objectInUse, world, vertex2, vertex6, verticalBarsAmount);
            commonUtils.drawLine(objectInUse, world, vertex3, vertex7, verticalBarsAmount);
            commonUtils.drawLine(objectInUse, world, vertex1, vertex4, verticalBarsAmount);
        }
        this.interceptDrawCalcAfter(world, step, objectInUse);
        this.endDraw(world, step, drawPos);
    }

    private void interceptDrawCalcAfter(
            ServerWorld world, int step, ParticleCuboid obj
    ) {
        InterceptData<emptyData> interceptData = new InterceptData<>(world, null, step, emptyData.class);
        if (this.afterCalcsIntercept == null) return;
        this.afterCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleCuboid, beforeCalcData> interceptDrawCalcBefore(
            ServerWorld world, Vector3f[] vertices, int step, ParticleCuboid obj
    ) {
        InterceptData<beforeCalcData> interceptData = new InterceptData<>(world, null, step, beforeCalcData.class);
        interceptData.addMetadata(beforeCalcData.VERTICES, vertices);
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeCalcsIntercept.apply(interceptData, obj);
    }
}
