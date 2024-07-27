package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.Optional;

/**
 * ParticleObject is the base class for all particle-based constructions that are animated by APEL.  Particle objects
 * are rendered in the client using particles registered with the particle registry.  APEL provides several common
 * 2D and 3D shapes that are ready for immediate use.
 *
 * <p>All particle objects share some common properties, and those are provided on the base class.  The
 * {@link #particleEffect} is used to render all particles in the object.  All objects allow for specifying a
 * {@link #rotation} and {@link #offset}.  These will be applied prior to translating the object to the {@code drawPos}
 * passed to {@link #draw(ApelServerRenderer, DrawContext)}.  Objects also have an {@link #amount} that indicates
 * the number of particles to render.  APEL native objects will spread these particles evenly throughout the shape
 * unless otherwise indicated on specific shapes.
 *
 * <p>The provided subclasses include interceptors that allow for modification before and after each call to
 * {@code draw}.
 *
 * <p><strong>Note:</strong> Rotation calculations are in radians and not in degrees.  When rotation values exceed the
 * (-2π, 2π), they are wrapped using modulo to remain in the range (-2π, 2π).
 *
 * <h2>Builders</h2>
 * <p>ParticleObject and its subclasses use a parallel hierarchy of nested classes to provide a fluent approach to
 * constructing instances of ParticleObject subclasses.  The builders are typed to allow specifying properties in any
 * order, regardless of whether they are on a superclass or subclass.  Each builder is templated with a builder that
 * extends itself, using the <a href="https://nuah.livejournal.com/328187.html">Curiously Recurring Template Pattern
 * (CRTP)</a>, and extends the Builder class from the outer class' parent.
 *
 * <h2>Subclassing</h2>
 * <p>Custom shapes only need to implement the {@code draw} method, and it should perform the necessary transformations
 * (scaling, rotation, translation) on the object before calling methods on the
 * {@link ApelServerRenderer renderer} to cause particles to be rendered.  The renderer supports several basic
 * geometries which are documented there.
 *
 * <p>Subclasses should consider mimicking the interceptor behavior so developers using the subclasses have the
 * opportunity to modify the object between each rendering.  These modifications may change rotation, translation,
 * scaling, vertex positions, particle amounts, or any other property of the subclass.
 *
 * <p>Subclasses should also provide a builder that mimics those provided by APEL-native ParticleObject subclasses.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class ParticleObject<T extends ParticleObject<T>> {
    protected ParticleEffect particleEffect;
    protected Vector3f rotation;
    protected Vector3f offset = new Vector3f(0, 0, 0);
    protected int amount = 1;
    protected DrawInterceptor<T> afterDraw = DrawInterceptor.identity();
    protected DrawInterceptor<T> beforeDraw = DrawInterceptor.identity();

    /**
     * Used by subclasses to when constructing themselves to set the properties shared by all ParticleObjects.
     *
     * @param particleEffect The particle effect to use
     * @param rotation The rotation to apply
     * @param offset The offset to apply
     * @param amount The amount of particles to use when rendering the object (subject to specific subclass
     *         usage)
     * @param beforeDraw The interceptor to call before drawing the object
     * @param afterDraw The interceptor to call after drawing the object
     */
    protected ParticleObject(ParticleEffect particleEffect, Vector3f rotation, Vector3f offset, int amount,
                             DrawInterceptor<T> beforeDraw, DrawInterceptor<T> afterDraw
    ) {
        this.setParticleEffect(particleEffect);
        this.setRotation(rotation);
        this.setOffset(offset);
        this.setAmount(amount);
        this.setBeforeDraw(beforeDraw);
        this.setAfterDraw(afterDraw);
    }

    /**
     * Used by subclasses when their copy constructors are invoked.  Rotation and offset are copied to new vectors to
     * prevent inadvertent modification impacting multiple objects.
     *
     * @param object The particle object to copy from
     */
    protected ParticleObject(ParticleObject<T> object) {
        this.particleEffect = object.particleEffect;
        this.rotation = new Vector3f(object.rotation);
        this.offset = new Vector3f(object.offset);
        this.amount = object.amount;
        this.beforeDraw = object.beforeDraw;
        this.afterDraw = object.afterDraw;
    }

    /** Gets the particle which is currently in use and returns it.
     *
     * @return The currently used particle
     */
    public ParticleEffect getParticleEffect() {
        return this.particleEffect;
    }

    /**
     * Sets the particle to use to a new value and returns the previous particle that was used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param particle The new particle
     * @return The previously used particle
     */
    public final ParticleEffect setParticleEffect(ParticleEffect particle) {
        ParticleEffect prevParticle = this.particleEffect;
        this.particleEffect = particle;
        return prevParticle;
    }

    /** Gets the rotation which is currently in use and returns it.
     *
     * @return The currently used rotation
     */
    public Vector3f getRotation() {
        return this.rotation;
    }

    /**
     * Sets the rotation to a new value. The rotation is calculated in radians and
     * when setting it wraps the rotation to be in the range of (-2π, 2π).  The rotation components will have the same
     * signs as they do in the parameter.  It returns the previous rotation used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param rotation The new rotation (IN RADIANS)
     * @return the previously used rotation
     */
    public final Vector3f setRotation(Vector3f rotation) {
        Vector3f prevRotation = this.rotation;
        this.rotation = this.normalizeRotation(rotation);
        return prevRotation;
    }

    /**
     * Removes full rotations from each component of the provided {@code rotation} vector such that each component
     * maintains its direction but has a magnitude in the range {@code (-2π, 0]} or {@code [0, 2π)}.  Returns a new
     * vector containing the resulting partial rotation components with the same signs as the parameter's components.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param rotation The existing rotation vector
     * @return A new vector with partial rotation components
     */
    protected final Vector3f normalizeRotation(Vector3f rotation) {
        float x = (float) (rotation.x % Math.TAU);
        float y = (float) (rotation.y % Math.TAU);
        float z = (float) (rotation.z % Math.TAU);
        return new Vector3f(x, y, z);
    }

    /** Gets the current offset value used. The offset position is added with the drawing position.
     *
     * @return The offset
     */
    public Vector3f getOffset() {
        return this.offset;
    }

    /**
     * Sets the offset to a new value. The offset position is added with the drawing position.
     * Returns the previous offset that was used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param offset The new offset value
     * @return The previous offset
     */
    public final Vector3f setOffset(Vector3f offset) {
        Vector3f prevOffset = this.offset;
        this.offset = offset;
        return prevOffset;
    }

    /** Gets the number of particles that are currently in use and returns it.
     *
     * @return The currently used number of particles
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Sets the number of particles to use for rendering the object. This has no effect on this class, but on shapes it
     * does have an effect. It returns the previously used number of particles.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param amount The new particle
     * @return The previously used amount
     */
    public final int setAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount of particles has to be above 0");
        }
        int prevAmount = this.amount;
        this.amount = amount;
        return prevAmount;
    }

    /**
     * Set the interceptor to run prior to drawing the object.  The interceptor will be provided with references to the
     * {@link ServerWorld}, an "origin" point from which the object should be drawn, the step number of the animation,
     * and any metadata defined by {@link DrawContext.Key>}s defined in the specific subclass.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing each particle
     */
    public final void setBeforeDraw(DrawInterceptor<T> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    /**
     * Set the interceptor to run after drawing the object.  The interceptor will be provided with references to the
     * {@link ServerWorld}, an "origin" point from which the object should be drawn, the step number of the animation,
     * and any metadata defined by {@link DrawContext.Key>}s defined in the specific subclass.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public final void setAfterDraw(DrawInterceptor<T> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    /**
     * This method allows for drawing a particle object using the provided {@link ApelServerRenderer}, the current
     * step, and the drawing position.
     *
     * <p><b>The method should not be called directly.</b>  It will be called via
     * {@link #doDraw(ApelServerRenderer, int, Vector3f)} by {@code PathAnimatorBase} subclasses to draw objects along
     * the animation path or at an animation point.  These animators will provide the renderer and calculate the
     * current {@code step} and the {@code drawPos}.  The renderer will have access to the {@code ServerWorld}.
     * <p>
     * <strong>Implementation Notes:</strong>
     * <ul>
     *     <li>Implementations should call methods on the {@code renderer} that cause drawing to occur.</li>
     *     <li><b>Important:</b> Implementations must also take care <em>not</em> to modify the {@code drawPos}, as many operations on {@link Vector3f} instances are in-place.</li>
     *     <li>The provided subclasses include interceptors that allow developers to perform actions both before and after drawing the object.</li>
     * </ul>
     *
     * @param renderer The server world instance
     * @param data The InterceptData
     */
    public abstract void draw(ApelServerRenderer renderer, DrawContext data);

    public final void doDraw(ApelServerRenderer renderer, int step, Vector3f drawPos) {
        DrawContext drawContext = new DrawContext(renderer.getServerWorld(), drawPos, step);
        this.prepareContext(drawContext);
        //noinspection unchecked
        this.beforeDraw.apply(drawContext, (T) this);
        this.draw(renderer, drawContext);
        //noinspection unchecked
        this.afterDraw.apply(drawContext, (T) this);
    }

    /**
     * Subclasses should override to provide metadata into the {@code interceptData}.  The default implementation does
     * nothing.
     *
     * @param drawContext the data holder to modify
     */
    protected void prepareContext(DrawContext drawContext) {
        // Default implementation does nothing
    }

    /**
     * Transforms a copy of the point at {@code position} according to the {@code quaternion} and {@code translation}.
     *
     * <p>Does not modify the {@code quaternion} or {@code translation}.
     *
     * @param position The x-coordinate of the point to transform.
     * @param quaternion The rotation to apply
     * @param translation The translation to apply
     * @return The transformed point in a new Vector3f
     */
    protected final Vector3f rigidTransformation(Vector3f position, Quaternionfc quaternion, Vector3f translation) {
        return new Vector3f(position).rotate(quaternion).add(translation);
    }

    /**
     * Provides a base for ParticleObject subclasses to extend when creating their builders.
     * <p>
     * Properties in this class are protected: this allows easy access from subclasses, but subclasses should take care
     * not to hide these fields by reusing fields of the same name.
     * <p>
     * Methods in this class are final: these are specifically setting properties for the ParticleObject class, so
     * they must maintain the invariants of ParticleObject.
     *
     * @param <B> the type being built, uses the curiously recurring type pattern
     */
    public static abstract class Builder<B extends Builder<B, T>, T extends ParticleObject<T>> {
        protected ParticleEffect particleEffect;
        protected Vector3f rotation = new Vector3f(0);
        protected Vector3f offset = new Vector3f(0);
        protected int amount = 1;
        protected DrawInterceptor<T> beforeDraw;
        protected DrawInterceptor<T> afterDraw;

        @SuppressWarnings({"unchecked"})
        public final B self() {
            return (B) this;
        }

        /**
         * Set the particle effect on the builder.  This method is not cumulative; repeated calls will overwrite the
         * value.
         */
        public final B particleEffect(ParticleEffect particleEffect) {
            this.particleEffect = particleEffect;
            return self();
        }

        /**
         * Set the rotation on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public final B rotation(Vector3f rotation) {
            this.rotation = rotation;
            return self();
        }

        /**
         * Set the offset on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public final B offset(Vector3f offset) {
            this.offset = offset;
            return self();
        }

        /**
         * Set the particle amount on the builder.  This method is not cumulative; repeated calls will overwrite the
         * value.
         */
        public B amount(int amount) {
            this.amount = amount;
            return self();
        }

        /**
         * Sets the interceptor to run before drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleCuboid#setBeforeDraw(DrawInterceptor)
         */
        public final B beforeDraw(DrawInterceptor<T> beforeDraw) {
            this.beforeDraw = beforeDraw;
            return self();
        }

        /**
         * Sets the interceptor to run after drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleCuboid#setAfterDraw(DrawInterceptor)
         */
        public final B afterDraw(DrawInterceptor<T> afterDraw) {
            this.afterDraw = afterDraw;
            return self();
        }

        public abstract ParticleObject<T> build();
    }
}
