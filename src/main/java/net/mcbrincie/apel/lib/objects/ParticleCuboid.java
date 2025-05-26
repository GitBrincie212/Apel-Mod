package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.ComputedEasingRPO;
import net.mcbrincie.apel.lib.util.ComputedEasings;
import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import net.mcbrincie.apel.lib.util.interceptor.Key;
import org.joml.Vector3f;
import org.joml.Vector3i;

/** The particle object class that represents a cuboid which is a rectangle
 * living in 3D. It is a cube if all the values of the size vector
 * are supplied with the same value.
 * <p>
 * <strong>Note: </strong>ParticleCuboid does not respect the {@link #getAmount()} method inherited from ParticleObject.
 * Instead, it contains amounts for the edges parallel to each axis using {@link #setAmounts(Vector3i)}.  If all edges
 * should have the same number of particles, then {@link #setAmount(int)} may be used.  The builder allows both methods
 * as well.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCuboid extends RenderableParticleObject<ParticleCuboid> {
    protected EasingCurve<Vector3f> size = new ConstantEasingCurve<>(new Vector3f());
    protected EasingCurve<Vector3i> amounts = new ConstantEasingCurve<>(new Vector3i());

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
    public static final Key<Vector3f[]> VERTICES = Key.vector3fArrayKey("vertices");

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleCuboid(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, new ConstantEasingCurve<>(1), builder.beforeDraw, builder.afterDraw);
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
        this.size = cuboid.size;
        this.amounts = cuboid.amounts;
    }

    public EasingCurve<Vector3f> getSize() {
        // Defensive copy to prevent a caller from messing with this class' data.
        return this.size;
    }

    /**
     * Sets the size of the cuboid object. The X axis corresponds to width, Y axis corresponds to height, and Z axis
     * corresponds to depth.  All dimensions must be positive.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload sets a constant value for the size
     *
     * @param size The size, a vector of positive numbers
     * @return The previous size
     *
     * @see ParticleCuboid#setSize(float)
     */
    public final EasingCurve<Vector3f> setSize(Vector3f size) {
        return this.setSize(new ConstantEasingCurve<>(size));
    }

    /**
     * Sets the size of the cuboid object. The X axis corresponds to width, Y axis corresponds to height, and Z axis
     * corresponds to depth.  All dimensions must be positive.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload sets a constant value for the size
     *
     * @param size The size, a vector of positive numbers
     * @return The previous size
     *
     * @see ParticleCuboid#setSize(float)
     */
    public final EasingCurve<Vector3f> setSize(EasingCurve<Vector3f> size) {
        EasingCurve<Vector3f> prevSize = this.size;
        this.size = size;
        return prevSize;
    }

    /** Sets the size of the cuboid object. The width, height, and depth will be equal, making a cube.
     *
     * <p>This implementation delegates to {@link #setSize(Vector3f)} so subclasses should take care not to violate
     * its contract. This method overload sets a constant float value for the size
     *
     * @param size The size of the cube
     * @return The previous size
     *
     * @see ParticleCuboid#setSize(Vector3f)
     */
    public EasingCurve<Vector3f> setSize(float size) {
        return this.setSize(new ConstantEasingCurve<>(new Vector3f(size, size, size)));
    }

    /**
     * Sets the amount of particles used per axis.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the particle amounts
     *
     * @param amount The amount per area
     * @return The previous amount to use
     */
    public final EasingCurve<Vector3i> setAmounts(Vector3i amount) {
        return this.setAmounts(new ConstantEasingCurve<>(amount));
    }

    /**
     * Sets the amount of particles used per axis.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the particle amounts
     *
     * @param amount The amount per area
     * @return The previous amount to use
     */
    public final EasingCurve<Vector3i> setAmounts(EasingCurve<Vector3i> amount) {
        EasingCurve<Vector3i> prevAmount = this.amounts;
        this.amounts = amount;
        return prevAmount;
    }

    /** THIS METHOD SHOULD NOT BE USED */
    @Override
    @Deprecated
    public EasingCurve<Integer> getAmount() {
        throw new UnsupportedOperationException("The method used is deprecated. It is not meant to be used");
    }

    /** Gets the number of particles where each coordinate corresponds to an area.  If the {@code ALL_FACES} enum
     * value is provided, the vector will have all three amounts in it.  If not, the vector will have only the value
     * requested in its corresponding vector entry, and the other values will be -1.
     *
     * @return The amount of particles per line on the face requested
    */
    public EasingCurve<Vector3i> getAmounts() {
        return this.amounts;
    }

    @Override
    protected ComputedEasingRPO computeAdditionalEasings(ComputedEasingRPO container) {
        return container.addComputedField("size", this.size)
                .addComputedField("amounts", this.amounts);
    }

    @Override
    protected void prepareContext(DrawContext drawContext) {
        // Scale
        Vector3f currSize = (Vector3f) drawContext.getComputedEasings().getComputedField("size");
        float width = currSize.x / 2f;
        float height = currSize.y / 2f;
        float depth = currSize.z / 2f;
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
        ComputedEasingPO computedEasings = drawContext.getComputedEasings();
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(computedEasings.computedOffset);
        // Scaled and re-positioned vertices
        Vector3f[] vertices = drawContext.getMetadata(VERTICES);

        Vector3f vertex0 = vertices[0];
        Vector3f vertex1 = vertices[1];
        Vector3f vertex2 = vertices[2];
        Vector3f vertex3 = vertices[3];
        Vector3f vertex4 = vertices[4];
        Vector3f vertex5 = vertices[5];
        Vector3f vertex6 = vertices[6];
        Vector3f vertex7 = vertices[7];

        int step = drawContext.getCurrentStep();
        Vector3i currAmounts = (Vector3i) computedEasings.getComputedField("amounts");
        int xAmount = currAmounts.x;
        int yAmount = currAmounts.y;
        int zAmount = currAmounts.z;

        // Bottom Face
        renderer.drawLine(this.particleEffect, step, objectDrawPos, vertex0, vertex1, computedEasings.computedRotation, zAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, vertex1, vertex2, computedEasings.computedRotation, xAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, vertex2, vertex3, computedEasings.computedRotation, zAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, vertex3, vertex0, computedEasings.computedRotation, xAmount);

        // Top Face
        renderer.drawLine(this.particleEffect, step, objectDrawPos, vertex4, vertex5, computedEasings.computedRotation, zAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, vertex5, vertex6, computedEasings.computedRotation, xAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, vertex6, vertex7, computedEasings.computedRotation, zAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, vertex7, vertex4, computedEasings.computedRotation, xAmount);

        // Vertical
        renderer.drawLine(this.particleEffect, step, objectDrawPos, vertex0, vertex4, computedEasings.computedRotation, yAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, vertex1, vertex5, computedEasings.computedRotation, yAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, vertex2, vertex6, computedEasings.computedRotation, yAmount);
        renderer.drawLine(this.particleEffect, step, objectDrawPos, vertex3, vertex7, computedEasings.computedRotation, yAmount);
    }

    public static class Builder<B extends Builder<B>> extends RenderableParticleObject.Builder<B, ParticleCuboid> {
        protected EasingCurve<Vector3f> size = new ConstantEasingCurve<>(new Vector3f());
        protected EasingCurve<Vector3i> amounts;

        private Builder() {}

        /**
         * Set the size on the builder.  This method is not cumulative; repeated calls to either {@code size} method
         * will overwrite the value.
         */
        public B size(EasingCurve<Vector3f> size) {
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
        public B amounts(EasingCurve<Vector3i> amount) {
            this.amounts = amount;
            return self();
        }

        @Override
        public B amount(int amount) {
            this.amounts = new ConstantEasingCurve<>(new Vector3i(amount));
            return self();
        }

        @Override
        public B amount(EasingCurve<Integer> amount) {
            throw new RuntimeException("[UNSUPPORTED], use a different builder parameter");
        }

        /**
         * Set the size on the builder to construct a cube (all edges will have equal length).  This method is not
         * cumulative; repeated calls to either {@code size} method will overwrite the value.
         */
        public B size(float size) {
            this.size = new ConstantEasingCurve<>(new Vector3f(size));
            return self();
        }

        /**
         * Set the size on the builder.  This method is not cumulative; repeated calls to either {@code size} method
         * will overwrite the value.
         */
        public B size(Vector3f size) {
            this.size = new ConstantEasingCurve<>(size);
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
            this.amounts = new ConstantEasingCurve<>(amount);
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
            this.amounts = new ConstantEasingCurve<>(new Vector3i(amount, amount, amount));
            return self();
        }

        @Override
        public ParticleCuboid build() {
            // Handle the amount being set via integer instead of Vector3i
            /*
            if (this.amounts.equals(new Vector3i()) && super.amount > 0) {
                this.amounts(new Vector3i(super.amount));
            }
             */
            return new ParticleCuboid(this);
        }
    }
}
