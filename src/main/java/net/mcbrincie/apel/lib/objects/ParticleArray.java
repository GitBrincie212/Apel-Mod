package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * ParticleArray is a utility class that copies a single particle object across a three-dimensional grid.
 * Objects will repeat as described in the {@code gridSize} property which allows for repeating the object in each of
 * the X, Y, and Z dimensions.  The spacing along each axis is customizable via the {@code spacingPerAxis} property.
 * <p>
 * The ParticleArray will run the interceptor for the repeated object once before drawing it in each position of the
 * array.  After drawing the object as many times as the array requires, the {@code afterDraw} interceptor will run.
 * <p>
 * The array will be centered around the {@link DrawContext}'s {@code position} property, though the entire array
 * can be adjusted with the {@code offset} property, as usual.  The individual object offset will be consistent across
 * all renderings.  The entire array may also be rotated by using the {@code rotation} property.  Individual objects
 * will be rotated consistently per the object's {@code rotation} value.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleArray<O extends ParticleObject<O>> extends ParticleObject<ParticleArray<O>> {
    protected O particleObject;
    protected EasingCurve<Vector3i> gridSize;
    protected EasingCurve<Vector3f> spacing;

    public static <B extends Builder<B, T>, T extends ParticleObject<T>> Builder<B, T> builder(T particleObject) {
        return new Builder<>(particleObject);
    }

    private <B extends Builder<B, O>> ParticleArray(Builder<B, O> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setParticleObject(builder.particleObject);
        this.setSpacing(builder.spacing);
        this.setGridSize(builder.gridSize);
    }

    /**
     * Gets the particle object that is used
     *
     * @return The particle object
     */
    public ParticleObject<O> getParticleObject() {
        return this.particleObject;
    }

    /**
     * Sets the target object
     *
     * @param newObject The new particle object pattern
     * @return The previous particle object pattern
     */
    public ParticleObject<O> setParticleObject(O newObject) {
        if (newObject == null) {
            throw new NullPointerException("The provided particle object is null");
        }
        ParticleObject<O> prevParticleObj = this.particleObject;
        this.particleObject = newObject;
        return prevParticleObj;
    }

    /**
     * Get the spacings for each axis represented as a vector
     *
     * @return The vector that represents the spacing between elements
     */
    public EasingCurve<Vector3f> getSpacing() {
        return this.spacing;
    }

    /**
     * Set the spacing along each axis for the particle array. The spacing is the amount of space between elements.
     * This method overload will set a constant value for the spacing
     *
     * @param spacing The new spacing between the elements
     * @return The previous spacing between the elements
     */
    public EasingCurve<Vector3f> setSpacing(Vector3f spacing) {
        return this.setSpacing(new ConstantEasingCurve<>(spacing));
    }

    /**
     * Set the spacing along each axis for the particle array. The spacing is the amount of space between elements.
     * This method overload will set an ease curve value for the spacing
     *
     * @param spacing The new spacing between the elements
     * @return The previous spacing between the elements
     */
    public EasingCurve<Vector3f> setSpacing(EasingCurve<Vector3f> spacing) {
        EasingCurve<Vector3f> prevSpacings = this.spacing;
        this.spacing = spacing;
        return prevSpacings;
    }

    /**
     * Get the number of particle objects along each axis.
     *
     * @return the vector value representing the elements for this axis
     */
    public EasingCurve<Vector3i> getGridSize() {
        return this.gridSize;
    }

    /** Sets the sizing to use for different the axis values for the particle array
     * to use. The sizing measures how many copies to place in each row. This method
     * overload will set a constant value for the grid
     *
     * @param gridSize The new grid size
     * @return The previous grid size
     */
    public EasingCurve<Vector3i> setGridSize(Vector3i gridSize) {
        return this.setGridSize(new ConstantEasingCurve<>(gridSize));
    }

    /** Sets the sizing to use for different the axis values for the particle array
     * to use. The sizing measures how many copies to place in each row. This method
     * overload will set a constant value for the grid
     *
     * @param gridSize The new grid size
     * @return The previous grid size
     */
    public EasingCurve<Vector3i> setGridSize(EasingCurve<Vector3i> gridSize) {
        EasingCurve<Vector3i> prevGridSize = this.gridSize;
        this.gridSize = gridSize;
        return prevGridSize;
    }

    @Override
    protected ComputedEasingPO computeAdditionalEasings(ComputedEasingPO container) {
        return container.addComputedField("gridSize", this.gridSize)
                .addComputedField("spacing", this.spacing);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext data) {
        ComputedEasingPO mainComputedEasings = data.getComputedEasings();
        ComputedEasingPO childComputedEasings = new ComputedEasingPO(this.particleObject, data.getCurrentStep(), data.getNumberOfStep());
        DrawContext childContext = new DrawContext(renderer, data, childComputedEasings);
        this.particleObject.prepareContext(childContext);
        // Call interceptors once
        this.particleObject.beforeDraw.apply(childContext, this.particleObject);
        Vector3i currGridSize = (Vector3i) mainComputedEasings.getComputedField("gridSize");
        Vector3f currSpacing = (Vector3f) mainComputedEasings.getComputedField("spacing");

        int xGaps = currGridSize.x - 1;
        int yGaps = currGridSize.y - 1;
        int zGaps = currGridSize.z - 1;
        for (int x = -xGaps; x <= xGaps; x += 2) {
            for (int y = -yGaps; y <= yGaps; y += 2) {
                for (int z = -zGaps; z <= zGaps; z += 2) {
                    Vector3f arrayOffset = new Vector3f(x, y, z).mul(currSpacing).div(2f);
                    // Debating between this and modifying the `particleObject` offset (for baking purposes)
                    childContext.getPosition().add(arrayOffset);
                    this.particleObject.draw(renderer, childContext);
                    childContext.getPosition().sub(arrayOffset);
                }
            }
        }
        this.particleObject.afterDraw.apply(childContext, this.particleObject);
    }

    // The 'T' here is deliberately different from the 'O' in the ParticleArray class.
    public static class Builder<B extends Builder<B, T>, T extends ParticleObject<T>> extends ParticleObject.Builder<B, ParticleArray<T>> {
        protected T particleObject;
        protected EasingCurve<Vector3f> spacing;
        protected EasingCurve<Vector3i> gridSize;

        private Builder(T particleObject) {
            this.particleObject = particleObject;
        }

        /**
         * Set the distance between elements along each axis.  This method is not cumulative; repeated calls will
         * overwrite the value.
         */
        public B spacing(EasingCurve<Vector3f> spacing) {
            this.spacing = spacing;
            return self();
        }

        /**
         * Set the size of the grid.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B gridSize(EasingCurve<Vector3i> gridSize) {
            this.gridSize = gridSize;
            return self();
        }

        /**
         * Set the particle object to repeat.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B particleObject(T object) {
            this.particleObject = object;
            return self();
        }

        /**
         * Set the distance between elements along each axis.  This method is not cumulative; repeated calls will
         * overwrite the value.
         */
        public B spacing(Vector3f spacing) {
            this.spacing = new ConstantEasingCurve<>(spacing);
            return self();
        }

        /**
         * Set the size of the grid.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B gridSize(Vector3i gridSize) {
            this.gridSize = new ConstantEasingCurve<>(gridSize);
            return self();
        }

        @Override
        public ParticleArray<T> build() {
            if (this.gridSize == null) {
                throw new IllegalStateException("GridSize must be provided");
            }
            if (this.spacing == null) {
                throw new IllegalStateException("Spacing must be provided");
            }
            if (this.particleObject == null) {
                throw new IllegalStateException("Particle Object must be provided");
            }
            return new ParticleArray<>(this);
        }
    }
}
