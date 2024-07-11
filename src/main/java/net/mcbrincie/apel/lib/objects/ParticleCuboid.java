package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.server.world.ServerWorld;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.Optional;

/** The particle object class that represents a cuboid which is a rectangle
 * living in 3D. It is a cube if all the values of the size vector
 * are supplied with the same value.
 * <p>
 * <strong>Note: </strong>ParticleCuboid does not respect the {@link #getAmount()} or {@link #setAmount(int)} methods
 * inherited from ParticleObject.  Instead, it stores amounts as described in {@link #getAmount(AreaLabel)} and
 * {@link #setAmount(Vector3i)}.  This also means that the builder method {@link ParticleObject.Builder#amount(int)} is
 * not ideal to use, though it will propagate an integer {@code i} into the vector {@code (i, i, i)}.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCuboid extends ParticleObject {
    protected Vector3f size;
    protected Vector3i amount;

    private DrawInterceptor<ParticleCuboid, AfterDrawData> afterDraw;
    private DrawInterceptor<ParticleCuboid, BeforeDrawData> beforeDraw;

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

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleCuboid(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, 1);
        // Defensive copies are made in setters to protect against in-place modification of vectors
        this.setSize(builder.size);
        this.setAmount(builder.amount);
        this.setAfterDraw(builder.afterDraw);
        this.setBeforeDraw(builder.beforeDraw);
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

    /**
     * Sets the size of the cuboid object. The X axis corresponds to width, Y axis corresponds to height, and Z axis
     * corresponds to depth.  All dimensions must be positive.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param size The size, a vector of positive numbers
     * @return The previous size
     *
     * @see ParticleCuboid#setSize(float)
     */
    public final Vector3f setSize(Vector3f size) {
        if (size.x <= 0 || size.y <= 0 || size.z <= 0) {
            throw new IllegalArgumentException("One of the size axis is below or equal to zero");
        }
        Vector3f prevSize = this.size;
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

    /**
     * Sets the amount per area.  The X coordinate dictates the particles per line on the bottom face, the Y
     * coordinate dictates the particles per line on the top face, and the Z coordinate dictates the particles per
     * vertical bar of the cuboid.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param amount The amount per area
     * @return The previous amount to use
     */
    public final Vector3i setAmount(Vector3i amount) {
        if (amount.x <= 0 || amount.y <= 0 || amount.z <= 0) {
            throw new IllegalArgumentException("One of the amount of particles axis is below or equal to 0");
        }
        Vector3i prevAmount = this.amount;
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

    /**
     * Set the interceptor to run after drawing the cuboid.  The interceptor will be provided
     * with references to the {@link ServerWorld} and the step number of the animation.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public final void setAfterDraw(DrawInterceptor<ParticleCuboid, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, null, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /**
     * Set the interceptor to run prior to drawing the cuboid.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * vertices of the cuboid.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing each particle
     */
    public final void setBeforeDraw(DrawInterceptor<ParticleCuboid, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private InterceptData<BeforeDrawData> doBeforeDraw(ServerWorld world, int step, Vector3f[] vertices) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, null, step, BeforeDrawData.class);
        interceptData.addMetadata(BeforeDrawData.VERTICES, vertices);
        this.beforeDraw.apply(interceptData, this);
        return interceptData;
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B> {
        protected Vector3f size = new Vector3f();
        protected Vector3i amount = new Vector3i();
        protected DrawInterceptor<ParticleCuboid, AfterDrawData> afterDraw;
        protected DrawInterceptor<ParticleCuboid, BeforeDrawData> beforeDraw;

        private Builder() {}

        /**
         * Set the size on the builder to construct a cube (all edges will have equal length).  This method is not
         * cumulative; repeated calls to either {@code size} method will overwrite the value.
         */
        public B size(float size) {
            this.size = new Vector3f(size);
            return self();
        }

        /**
         * Set the size on the builder.  This method is not cumulative; repeated calls to either {@code size} method
         * will overwrite the value.
         */
        public B size(Vector3f size) {
            this.size = size;
            return self();
        }

        /**
         * Set the amount of particles to use on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         *
         * @see ParticleCuboid#setAmount(Vector3i)
         */
        public B amount(Vector3i amount) {
            this.amount = amount;
            return self();
        }

        @Override
        public B amount(int amount) {
            this.amount = new Vector3i(amount, amount, amount);
            return self();
        }

        /**
         * Sets the interceptor to run after drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleCuboid#setAfterDraw(DrawInterceptor)
         */
        public B afterDraw(DrawInterceptor<ParticleCuboid, AfterDrawData> afterDraw) {
            this.afterDraw = afterDraw;
            return self();
        }

        /**
         * Sets the interceptor to run before drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleCuboid#setBeforeDraw(DrawInterceptor)
         */
        public B beforeDraw(DrawInterceptor<ParticleCuboid, BeforeDrawData> beforeDraw) {
            this.beforeDraw = beforeDraw;
            return self();
        }

        @Override
        public ParticleCuboid build() {
            // Handle the amount being set via integer instead of Vector3i
            if (this.amount.equals(new Vector3i()) && super.amount > 0) {
                this.amount(new Vector3i(super.amount));
            }
            return new ParticleCuboid(this);
        }
    }
}
