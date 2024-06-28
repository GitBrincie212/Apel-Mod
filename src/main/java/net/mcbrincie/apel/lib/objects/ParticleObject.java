package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.math.TrigTable;
import net.minecraft.particle.ParticleEffect;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

/**
 * ParticleObject is the base class for all particle-based constructions that are animated by APEL.  Particle objects
 * are rendered in the client using particles registered with the particle registry.  APEL provides several common
 * 2D and 3D shapes that are ready for immediate use.
 *
 * <p>All particle objects share some common properties, and those are provided on the base class.  The
 * {@link #particleEffect} is used to render all particles in the object.  All objects allow for specifying a
 * {@link #rotation} and {@link #offset}.  These will be applied prior to translating the object to the {@code drawPos}
 * passed to {@link #draw(ApelServerRenderer, int, Vector3f)}.  Objects also have an {@link #amount} that indicates
 * the number of particles to render.  APEL native objects will spread these particles evenly throughout the shape
 * unless otherwise indicated on specific shapes.
 *
 * <h2>Subclassing</h2>
 * <p>Custom shapes only need to implement the {@code draw} method, and it should call methods on the
 * {@link ApelServerRenderer renderer} to cause particles to be rendered.  The renderer supports several basic
 * geometries which are documented there.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class ParticleObject {
    protected ParticleEffect particleEffect;
    protected Vector3f rotation;
    protected Vector3f offset = new Vector3f(0, 0, 0);
    protected int amount = 1;

    protected static TrigTable trigTable = Apel.TRIG_TABLE;

    /** Constructor for the particle object which is a point. It accepts as parameters
     * the particle to use and the rotation to apply (which has no effect. Only on the
     * path animators that extend this class). There is also a simplified version
     * for no rotation.
     *
     * @see ParticleObject#ParticleObject(ParticleObject)
     *
     * @param particleEffect The particle effect to use
     * @param rotation The rotation (IN RADIANS)
     */
    public ParticleObject(ParticleEffect particleEffect, Vector3f rotation) {
        this.particleEffect = particleEffect;
        this.rotation = this.normalizeRotation(rotation);
    }

    /** Constructor for the particle object which is a point. It accepts as parameters
     * the particle effect to use. It is a simplified version of the previous constructor
     * and is meant to be used when you want the object to not have a rotation offset.
     * In the case you do want, there is a constructor for that (won't apply to this class)
     *
     * @see ParticleObject#ParticleObject(ParticleEffect, Vector3f)
     *
     * @param particleEffect The particle effect to use
    */
    public ParticleObject(ParticleEffect particleEffect) {
        this(particleEffect, new Vector3f(0, 0, 0));
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param object The particle object to copy from
     */
    public ParticleObject(ParticleObject object) {
        this.particleEffect = object.particleEffect;
        this.rotation = object.rotation;
        this.amount = object.amount;
        this.offset = object.offset;
    }

    /** Gets the particle which is currently in use and returns it
     *
     * @return The currently used particle
     */
    public ParticleEffect getParticleEffect() {
        return this.particleEffect;
    }

    /** Sets the particle to use to a new value and returns the previous
     *  particle that was used
     *
     * @param particle The new particle
     * @return The previously used particle
     */
    public ParticleEffect setParticleEffect(ParticleEffect particle) {
        ParticleEffect prevParticle = this.particleEffect;
        this.particleEffect = particle;
        return prevParticle;
    }

    /** Gets the rotation which is currently in use and returns it
     *
     * @return The currently used rotation
     */
    public Vector3f getRotation() {
        return this.rotation;
    }

    /** Sets the rotation to a new value. The rotation is calculated in radians and
     * when setting it rounds the rotation to match in the range of (-2π, 2π). It returns
     * the previous rotation used.
     *
     * <p>This implementation uses {@link #normalizeRotation(Vector3f)} to do the rounding which uses
     * the modulo operator on each member of the {@code Vector3f} to produce a result in (-2π, 2π).
     *
     * @param rotation The new rotation (IN RADIANS)
     * @return the previously used rotation
     */
    public Vector3f setRotation(Vector3f rotation) {
        Vector3f prevRotation = this.rotation;
        this.rotation = this.normalizeRotation(rotation);
        return prevRotation;
    }

    /**
     * Removes full rotations from each component of the provided {@code rotation} vector such that each component
     * maintains its direction but has a magnitude in the range {@code (-2π, 0]} or {@code [0, 2π)}.  Returns a new
     * vector containing the resulting partial rotation components.
     *
     * <p><b>Note:</b> This is called by {@link #setRotation(Vector3f)}, so overrides must maintain this behavior.
     *
     * @param rotation The existing rotation vector
     * @return A new vector with partial rotation components
     */
    protected Vector3f normalizeRotation(Vector3f rotation) {
        float x = (float) (rotation.x % Math.TAU);
        float y = (float) (rotation.y % Math.TAU);
        float z = (float) (rotation.z % Math.TAU);
        return new Vector3f(x, y, z);
    }

    /** Gets the current offset value used. The offset position is added
     * with the drawing position.
     *
     * @return The offset
     */
    public Vector3f getOffset() {
        return this.offset;
    }

    /** Sets the offset to a new value. The offset position is added with the drawing position.
     * Returns the previous offset that was used
     *
     * @param offset The new offset value
     * @return The previous offset
     */
    public Vector3f setOffset(Vector3f offset) {
        Vector3f prevOffset = this.offset;
        this.offset = offset;
        return prevOffset;
    }

    /** Gets the number of particles that are currently in use and returns it
     *
     * @return The currently used number of particles
     */
    public int getAmount() {
        return this.amount;
    }

    /** Sets the number of particles to use for rendering the object.
     * This has no effect on this class, but on shapes it does have an effect.
     * It returns the previously used number of particles
     *
     * @param amount The new particle
     * @return The previously used amount
     */
    public int setAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount of particles has to be above 0");
        }
        int prevAmount = this.amount;
        this.amount = amount;
        return prevAmount;
    }

    /**
     * This method allows for drawing a particle object using the provided {@link ApelServerRenderer}, the current
     * step, and the drawing position.
     *
     * <p><b>The method should not be called directly.</b>  It will be called by {@code PathAnimatorBase} subclasses
     * to draw objects along the animation path or at an animation point.  These animators will provide the renderer
     * and calculate the current {@code step} and the {@code drawPos}.  The renderer will have access to the
     * {@code ServerWorld}.  Implementations should call methods on the {@code renderer} that cause drawing to occur.
     *
     * <p><b>Important:</b> Implementations must also take care <em>not</em> to modify the {@code drawPos}, as many
     * operations on {@link Vector3f} instances are in-place.
     *
     * <p>The provided subclasses include interceptors that allow developers to perform actions both before and after
     * drawing the object.
     *
     * @param renderer The server world instance
     * @param step     The current rendering step at
     * @param drawPos  The position to draw at
     */
    public abstract void draw(ApelServerRenderer renderer, int step, Vector3f drawPos);

    public void endDraw(ApelServerRenderer renderer, int step, Vector3f drawPos) {}

    /**
     * Transforms the point at {@code (x, y, z)} according to the {@code quaternion} and {@code translation}.
     *
     * <p>Does not modify the {@code quaternion} or {@code translation}.
     *
     * @param x The x-coordinate of the point to transform
     * @param y The y-coordinate of the point to transform
     * @param z The z-coordinate of the point to transform
     * @param quaternion The rotation to apply
     * @param translation The translation to apply
     * @return The transformed point
     */
    protected final Vector3f rigidTransformation(float x, float y, float z, Quaternionfc quaternion, Vector3f translation) {
        return new Vector3f(x, y, z).rotate(quaternion).add(translation);
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
}
