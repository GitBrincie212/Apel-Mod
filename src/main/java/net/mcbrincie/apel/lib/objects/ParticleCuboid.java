package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.Optional;

/** The particle object class that represents a cuboid which is a rectangle
 * living in 3D. It is a cube if all the values of the size vector
 * are supplied with the same value.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCuboid extends ParticleObject {
    protected Vector3f size = new Vector3f();
    protected Vector3i amount = new Vector3i();

    private DrawInterceptor<ParticleCuboid, AfterDrawData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleCuboid, BeforeDrawData> beforeDraw = DrawInterceptor.identity();

    /** There is no data being transmitted */
    public enum AfterDrawData {}

    /** This data is used before calculations (it contains the vertices)*/
    public enum BeforeDrawData {
        /**
         * The vertices provided follow this indexing scheme (using Minecraft's axes where +x is east, +y is up, and
         * +z is south), but will be rotated and translated prior to the interceptor:
         *
         * <pre>
         *               y         z
         *               ^        ↗
         *               |       /
         *          5----|--------6
         *         /|    |     / /|
         *        / |    |    / / |
         *       /  |    |   / /  |
         *      4-------------7   +
         *      |   |    | /  |   |
         *      |   |    |/   |   |
         * x <--|--------+    |   |
         *      |   |         |   |
         *      |   |         |   |
         *      |   1---------|---2
         *      |  /          |  /
         *      | /           | /
         *      |/            |/
         *      0-------------3
         * </pre>
         */
        VERTICES,
    }

    /** This enum is used for the getter method of setAmount */
    public enum AreaLabel {
        BOTTOM_FACE, TOP_FACE, VERTICAL_BARS, ALL_FACES
    }

    /** Constructor for the particle cuboid which is a 3D rectangle. It accepts as parameters
     * the particle effect to use, the number of particles per line in each face section (bottom is X, top is Y and
     * the bars are Z), the size of the cuboid (width, height, depth), and the rotation to apply.
     *
     * <p>This implementation calls setters for rotation, size, and amount so checks are performed to
     * ensure valid values are accepted for each property.  Subclasses should take care not to violate these lest
     * they risk undefined behavior.
     *
     * @param particleEffect The particle to use
     * @param amount The number of particles for the object
     * @param size The size in regard to width, height, depth
     * @param rotation The rotation to apply
     *
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vector3i, Vector3f)
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, int, Vector3f, Vector3f)
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, int, Vector3f)
    */
    public ParticleCuboid(ParticleEffect particleEffect, Vector3i amount, @NotNull Vector3f size, Vector3f rotation) {
        super(particleEffect, rotation);
        // Defensive copies are made in setters to protect against in-place modification of vectors
        this.setSize(size);
        this.setAmount(amount);
    }

    /** Constructor for the particle cuboid which is a 3D rectangle. It accepts as parameters
     * the particle effect to use, the number of particles per line in each face section (bottom is X, top is Y and
     * the bars are Z), and the size of the cuboid (width, height, depth).
     *
     * <p>This implementation calls setters for rotation, size, and amount so checks are performed to
     * ensure valid values are accepted for each property.  Subclasses should take care not to violate these lest
     * they risk undefined behavior.
     *
     * @param particleEffect The particle to use
     * @param amount The number of particles for the object
     * @param size The size in regard to width, height, depth
     *
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vector3i, Vector3f, Vector3f)
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, int, Vector3f, Vector3f)
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, int, Vector3f)
     */
    public ParticleCuboid(ParticleEffect particleEffect, Vector3i amount, @NotNull Vector3f size) {
        this(particleEffect, amount, size, new Vector3f(0));
    }

    /** Constructor for the particle cuboid which is a 3D rectangle. It accepts as parameters
     * the particle effect to use, the number of particles per line in all face sections, the size of the cuboid
     * (width, height, depth), and the rotation to apply.
     *
     * <p>This implementation calls setters for rotation, size, and amount so checks are performed to
     * ensure valid values are accepted for each property.  Subclasses should take care not to violate these lest
     * they risk undefined behavior.
     *
     * @param particleEffect The particle to use
     * @param amount The number of particles for the object
     * @param size The size in regard to width, height, depth
     *
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vector3i, Vector3f, Vector3f)
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vector3i, Vector3f)
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, int, Vector3f)
     */
    public ParticleCuboid(ParticleEffect particleEffect, int amount, @NotNull Vector3f size, Vector3f rotation) {
        this(particleEffect, new Vector3i(amount), size, rotation);
    }

    /** Constructor for the particle cuboid which is a 3D rectangle. It accepts as parameters
     * the particle effect to use, the number of particles per line in all face sections, and the size of the cuboid
     * (width, height, depth).
     *
     * <p>This implementation calls setters for rotation, size, and amount so checks are performed to
     * ensure valid values are accepted for each property.  Subclasses should take care not to violate these lest
     * they risk undefined behavior.
     *
     * @param particleEffect The particle to use
     * @param amount The number of particles for the object
     * @param size The size in regard to width, height, depth
     *
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vector3i, Vector3f, Vector3f)
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vector3i, Vector3f)
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, int, Vector3f, Vector3f)
     */
    public ParticleCuboid(ParticleEffect particleEffect, int amount, @NotNull Vector3f size) {
        this(particleEffect, new Vector3i(amount), size, new Vector3f(0));
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.  Size and amount are copied to new vectors.
     *
     * @param cuboid The particle circle object to copy from
    */
    public ParticleCuboid(ParticleCuboid cuboid) {
        super(cuboid);
        this.size = new Vector3f(cuboid.size);
        this.amount = new Vector3i(cuboid.amount);
        this.beforeDraw = cuboid.beforeDraw;
        this.afterDraw = cuboid.afterDraw;
    }

    public Vector3f getSize() {
        // Defensive copy to prevent a caller from messing with this class' data.
        return new Vector3f(this.size);
    }

    /** Sets the size of the cuboid object. The X axis corresponds to width, Y axis corresponds to height, and Z axis
     * corresponds to depth.  All dimensions must be positive.
     *
     * @param size The size, a vector of positive numbers
     * @return The previous size
     *
     * @see ParticleCuboid#setSize(float)
     */
    public Vector3f setSize(Vector3f size) {
        if (size.x <= 0 || size.y <= 0 || size.z <= 0) {
            throw new IllegalArgumentException("One of the size axis is below or equal to zero");
        }
        Vector3f prevSize = new Vector3f(this.size);
        // Defensive copy to prevent unintended modification
        this.size = new Vector3f(size);
        return prevSize;
    }

    /** Sets the size of the cuboid object. The width, height, and depth will be equal, making a cube.
     *
     * <p>This implementation delegates to {@link #setSize(Vector3f)} so subclasses should take care not to violate
     * its contract.
     *
     * @param size The size of the cube
     * @return The previous size
     *
     * @see ParticleCuboid#setSize(Vector3f)
     */
    public Vector3f setSize(float size) {
        return this.setSize(new Vector3f(size, size, size));
    }

    /** THIS METHOD SHOULD NOT BE USED */
    @Override
    @Deprecated
    public int setAmount(int amount) {
        throw new UnsupportedOperationException("The method used is deprecated. It is not meant to be used");
    }

    /** Sets the amount per area.  The X coordinate dictates the particles per line on the bottom face, the Y
     * coordinate dictates the particles per line on the top face, and the Z coordinate dictates the particles per
     * vertical bar of the cuboid.
     *
     * @param amount The amount per area
     * @return The previous amount to use
     */
    public Vector3i setAmount(Vector3i amount) {
        if (amount.x <= 0 || amount.y <= 0 || amount.z <= 0) {
            throw new IllegalArgumentException("One of the amount of particles axis is below or equal to 0");
        }
        Vector3i prevAmount = new Vector3i(this.amount);
        // Defensive copy to prevent unintended modification
        this.amount = new Vector3i(amount);
        return prevAmount;
    }

    /** THIS METHOD SHOULD NOT BE USED */
    @Override
    @Deprecated
    public int getAmount() {
        throw new UnsupportedOperationException("The method used is deprecated. It is not meant to be used");
    }

    /** Gets the number of particles where each coordinate corresponds to an area.  If the {@code ALL_FACES} enum
     * value is provided, the vector will have all three amounts in it.  If not, the vector will have only the value
     * requested in its corresponding vector entry, and the other values will be -1.
     *
     * @return The amount of particles per line on the face requested
    */
    public Vector3i getAmount(AreaLabel faceIndex) {
        return switch(faceIndex) {
            // Defensive copies are return to prevent unintended modification
            case ALL_FACES -> new Vector3i(this.amount);
            case BOTTOM_FACE -> new Vector3i(this.amount.x, -1, -1);
            case TOP_FACE -> new Vector3i(-1, this.amount.y, -1);
            case VERTICAL_BARS -> new Vector3i(-1, -1, this.amount.z);
        };
    }

    @Override
    public void draw(ApelServerRenderer renderer, int step, Vector3f drawPos) {
        // Scale
        float width = size.x / 2f;
        float height = size.y / 2f;
        float depth = size.z / 2f;
        // Rotation
        Quaternionfc quaternion =
                new Quaternionf().rotateZ(this.rotation.z).rotateY(this.rotation.y).rotateX(this.rotation.x);
        // Translation
        Vector3f objectDrawPos = new Vector3f(drawPos).add(this.offset);
        // Compute the cuboid vertices
        Vector3f vertex0 = this.rigidTransformation(width, -height, -depth, quaternion, objectDrawPos);
        Vector3f vertex1 = this.rigidTransformation(width, -height, depth, quaternion, objectDrawPos);
        Vector3f vertex2 = this.rigidTransformation(-width, -height, depth, quaternion, objectDrawPos);
        Vector3f vertex3 = this.rigidTransformation(-width, -height, -depth, quaternion, objectDrawPos);
        Vector3f vertex4 = this.rigidTransformation(width, height, -depth, quaternion, objectDrawPos);
        Vector3f vertex5 = this.rigidTransformation(width, height, depth, quaternion, objectDrawPos);
        Vector3f vertex6 = this.rigidTransformation(-width, height, depth, quaternion, objectDrawPos);
        Vector3f vertex7 = this.rigidTransformation(-width, height, -depth, quaternion, objectDrawPos);

        Vector3f[] vertices = {vertex0, vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7};
        InterceptData<BeforeDrawData> interceptData = this.doBeforeDraw(renderer.getServerWorld(), step, vertices);
        vertices = interceptData.getMetadata(BeforeDrawData.VERTICES, vertices);

        vertex0 = vertices[0];
        vertex1 = vertices[1];
        vertex2 = vertices[2];
        vertex3 = vertices[3];
        vertex4 = vertices[4];
        vertex5 = vertices[5];
        vertex6 = vertices[6];
        vertex7 = vertices[7];

        int bottomFaceAmount = this.amount.x;
        int topFaceAmount = this.amount.y;
        int verticalBarsAmount = this.amount.z;

        // Bottom Face
        renderer.drawLine(this.particleEffect, step, vertex0, vertex1, bottomFaceAmount);
        renderer.drawLine(this.particleEffect, step, vertex1, vertex2, bottomFaceAmount);
        renderer.drawLine(this.particleEffect, step, vertex2, vertex3, bottomFaceAmount);
        renderer.drawLine(this.particleEffect, step, vertex3, vertex0, bottomFaceAmount);

        // Top Face
        renderer.drawLine(this.particleEffect, step, vertex4, vertex5, topFaceAmount);
        renderer.drawLine(this.particleEffect, step, vertex5, vertex6, topFaceAmount);
        renderer.drawLine(this.particleEffect, step, vertex6, vertex7, topFaceAmount);
        renderer.drawLine(this.particleEffect, step, vertex7, vertex4, topFaceAmount);

        // Vertical
        renderer.drawLine(this.particleEffect, step, vertex0, vertex4, verticalBarsAmount);
        renderer.drawLine(this.particleEffect, step, vertex1, vertex5, verticalBarsAmount);
        renderer.drawLine(this.particleEffect, step, vertex2, vertex6, verticalBarsAmount);
        renderer.drawLine(this.particleEffect, step, vertex3, vertex7, verticalBarsAmount);

        this.doAfterDraw(renderer.getServerWorld(), step);
        this.endDraw(renderer, step, drawPos);
    }

    /** Set the interceptor to run after drawing the cuboid.  The interceptor will be provided
     * with references to the {@link ServerWorld} and the step number of the animation.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public void setAfterDraw(DrawInterceptor<ParticleCuboid, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, null, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /** Set the interceptor to run prior to drawing the cuboid.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * vertices of the cuboid.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing each particle
     */
    public void setBeforeDraw(DrawInterceptor<ParticleCuboid, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private InterceptData<BeforeDrawData> doBeforeDraw(ServerWorld world, int step, Vector3f[] vertices) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, null, step, BeforeDrawData.class);
        interceptData.addMetadata(BeforeDrawData.VERTICES, vertices);
        this.beforeDraw.apply(interceptData, this);
        return interceptData;
    }
}
