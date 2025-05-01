package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
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
    protected Vector3i gridSize = new Vector3i(1);
    protected Vector3f spacing = new Vector3f(4.0f);

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
    public Vector3f getSpacing() {
        return new Vector3f(this.spacing);
    }

    /**
     * Set the spacing along each axis for the particle array. The spacing is the amount of space between elements.
     *
     * @param spacing The new spacing between the elements
     * @return The previous spacing between the elements
     */
    public Vector3f setSpacing(Vector3f spacing) {
        Vector3f prevSpacings = this.spacing;
        this.spacing = new Vector3f(spacing);
        return prevSpacings;
    }

    /**
     * Get the number of particle objects along each axis.
     *
     * @return the vector value representing the elements for this axis
     */
    public Vector3i getGridSize() {
        return new Vector3i(this.gridSize);
    }

    /** Sets the sizing to use for different the axis values for the particle array
     * to use. The sizing measures how many copies to place in each row
     *
     * @param gridSize The new grid size
     * @return The previous grid size
     */
    public Vector3i setGridSize(Vector3i gridSize) {
        if (gridSize.x <= 0 || gridSize.y <= 0 || gridSize.z <= 0) {
            throw new IllegalStateException("Grid size values must be positive");
        }
        Vector3i prevGridSize = this.gridSize;
        this.gridSize = new Vector3i(gridSize);
        return prevGridSize;
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext data) {
        DrawContext childContext = new DrawContext(renderer, data);
        this.particleObject.prepareContext(childContext);
        // Call interceptors once
        this.particleObject.beforeDraw.apply(childContext, this.particleObject);

        int xGaps = this.gridSize.x - 1;
        int yGaps = this.gridSize.y - 1;
        int zGaps = this.gridSize.z - 1;
        for (int x = -xGaps; x <= xGaps; x += 2) {
            for (int y = -yGaps; y <= yGaps; y += 2) {
                for (int z = -zGaps; z <= zGaps; z += 2) {
                    Vector3f arrayOffset = new Vector3f(x, y, z).mul(this.spacing).div(2f);
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
        protected Vector3f spacing = new Vector3f(1.0f);
        protected Vector3i gridSize = new Vector3i(1);

        private Builder(T particleObject) {
            this.particleObject = particleObject;
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
            this.spacing = spacing;
            return self();
        }

        /**
         * Set the size of the grid.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B gridSize(Vector3i gridSize) {
            this.gridSize = gridSize;
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
