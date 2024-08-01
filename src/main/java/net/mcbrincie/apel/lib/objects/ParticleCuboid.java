package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3i;

/** The particle object class that represents a cuboid which is a rectangle
 * living in 3D. It is a cube if all the values of the size vector
 * are supplied with the same value.
 * <p>
 * <strong>Note: </strong>ParticleCuboid does not respect the {@link #getAmount()} method inherited from ParticleObject.
 * Instead, it contains amounts for the edges parallel to each axis using {@link #setAmounts(Vector3i)}.  If all edges
 * should have the same number of of particles, then {@link #setAmount(int)} may be used.  The builder allows both
 * methods as well.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCuboid extends ParticleObject<ParticleCuboid> {
    protected Vector3f size = new Vector3f();
    protected Vector3i amounts = new Vector3i();

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
    public static final DrawContext.Key<Vector3f[]> VERTICES = DrawContext.vector3fArrayKey("vertices");

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleCuboid(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, 1, builder.beforeDraw, builder.afterDraw);
        // Defensive copies are made in setters to protect against in-place modification of vectors
        this.setSize(builder.size);
        this.setAmounts(builder.amounts);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.  Size and amount are copied to new vectors.
     *
     * @param cuboid The particle circle object to copy from
    */
    public ParticleCuboid(ParticleCuboid cuboid) {
        super(cuboid);
        this.size = new Vector3f(cuboid.size);
        this.amounts = new Vector3i(cuboid.amounts);
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
     * Sets the amount of particles used per axis.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param amount The amount per area
     * @return The previous amount to use
     */
    public final Vector3i setAmounts(Vector3i amount) {
        if (amount.x <= 0 || amount.y <= 0 || amount.z <= 0) {
            throw new IllegalArgumentException("One of the amount of particles axis is below or equal to 0");
        }
        Vector3i prevAmount = this.amounts;
        // Defensive copy to prevent unintended modification
        this.amounts = new Vector3i(amount);
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
    public Vector3i getAmounts() {
        return new Vector3i(this.amounts);
    }

    @Override
    protected void prepareContext(DrawContext drawContext) {
        // Scale
        float width = size.x / 2f;
        float height = size.y / 2f;
        float depth = size.z / 2f;
        // Compute the cuboid vertices
        Vector3f vertex0 = new Vector3f(width, -height, -depth);
        Vector3f vertex1 = new Vector3f(width, -height, depth);
        Vector3f vertex2 = new Vector3f(-width, -height, depth);
        Vector3f vertex3 = new Vector3f(-width, -height, -depth);
        Vector3f vertex4 = new Vector3f(width, height, -depth);
        Vector3f vertex5 = new Vector3f(width, height, depth);
        Vector3f vertex6 = new Vector3f(-width, height, depth);
        Vector3f vertex7 = new Vector3f(-width, height, -depth);

        Vector3f[] vertices = {vertex0, vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7};
        drawContext.addMetadata(VERTICES, vertices);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        // Rotation
        Quaternionfc quaternion =
                new Quaternionf().rotateZ(this.rotation.z).rotateY(this.rotation.y).rotateX(this.rotation.x);
        // Translation
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);
        // Scaled and re-positioned vertices
        Vector3f[] vertices = drawContext.getMetadata(VERTICES);

        Vector3f vertex0 = this.rigidTransformation(vertices[0], quaternion, objectDrawPos);
        Vector3f vertex1 = this.rigidTransformation(vertices[1], quaternion, objectDrawPos);
        Vector3f vertex2 = this.rigidTransformation(vertices[2], quaternion, objectDrawPos);
        Vector3f vertex3 = this.rigidTransformation(vertices[3], quaternion, objectDrawPos);
        Vector3f vertex4 = this.rigidTransformation(vertices[4], quaternion, objectDrawPos);
        Vector3f vertex5 = this.rigidTransformation(vertices[5], quaternion, objectDrawPos);
        Vector3f vertex6 = this.rigidTransformation(vertices[6], quaternion, objectDrawPos);
        Vector3f vertex7 = this.rigidTransformation(vertices[7], quaternion, objectDrawPos);

        int step = drawContext.getCurrentStep();
        int xAmount = this.amounts.x;
        int yAmount = this.amounts.y;
        int zAmount = this.amounts.z;

        // Bottom Face
        renderer.drawLine(this.particleEffect, step, vertex0, vertex1, zAmount);
        renderer.drawLine(this.particleEffect, step, vertex1, vertex2, xAmount);
        renderer.drawLine(this.particleEffect, step, vertex2, vertex3, zAmount);
        renderer.drawLine(this.particleEffect, step, vertex3, vertex0, xAmount);

        // Top Face
        renderer.drawLine(this.particleEffect, step, vertex4, vertex5, zAmount);
        renderer.drawLine(this.particleEffect, step, vertex5, vertex6, xAmount);
        renderer.drawLine(this.particleEffect, step, vertex6, vertex7, zAmount);
        renderer.drawLine(this.particleEffect, step, vertex7, vertex4, xAmount);

        // Vertical
        renderer.drawLine(this.particleEffect, step, vertex0, vertex4, yAmount);
        renderer.drawLine(this.particleEffect, step, vertex1, vertex5, yAmount);
        renderer.drawLine(this.particleEffect, step, vertex2, vertex6, yAmount);
        renderer.drawLine(this.particleEffect, step, vertex3, vertex7, yAmount);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleCuboid> {
        protected Vector3f size = new Vector3f();
        protected Vector3i amounts = new Vector3i();

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
         * Set the amount of particles to use on the builder.  This method is not cumulative; repeated calls will
         * overwrite previous values from this method and from {@link #amounts(int)}.
         *
         * @see #amounts(int)
         * @see ParticleCuboid#setAmounts(Vector3i)
         */
        public B amounts(Vector3i amount) {
            this.amounts = amount;
            return self();
        }

        /**
         * Set the amount of particles to use on all edges.  This method is not cumulative, repeated calls will
         * overwrite previous values from this method and from {@link #amounts(Vector3i)}.
         * <p>
         * Note that {@link #amount(int)} acts as s synonym, but is only considered if neither {@code amounts} method is
         * used.
         *
         * @see #amounts(Vector3i)
         * @see ParticleCuboid#setAmounts(Vector3i)
         */
        public B amounts(int amount) {
            this.amounts = new Vector3i(amount, amount, amount);
            return self();
        }

        @Override
        public ParticleCuboid build() {
            // Handle the amount being set via integer instead of Vector3i
            if (this.amounts.equals(new Vector3i()) && super.amount > 0) {
                this.amounts(new Vector3i(super.amount));
            }
            return new ParticleCuboid(this);
        }
    }
}
