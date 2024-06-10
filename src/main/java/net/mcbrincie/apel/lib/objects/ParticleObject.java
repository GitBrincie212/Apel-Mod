package net.mcbrincie.apel.lib.objects;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class ParticleObject {
    protected ParticleEffect particleEffect;
    protected Vector3f rotation;
    protected Vector3f offset = new Vector3f(0, 0, 0);
    protected int amount = 1;

    /** Constructor for the particle object which is a point. It accepts as parameters
     * the particle to use and the rotation to apply(which has no effect. Only on the
     * path animators that extend this class). There is also a simplified version
     * for no rotation.
     *
     * @see ParticleObject#ParticleObject(ParticleObject)
     *
     * @param particleEffect The particle effect to use
     * @param rotation The rotation(IN RADIANS)
     */
    public ParticleObject(ParticleEffect particleEffect, Vector3f rotation) {
        this.particleEffect = particleEffect;
        this.rotation = this.normalizeRotation(rotation);
    }

    /** Constructor for the particle object which is a point. It accepts as parameters
     * the particle to use. It is a simplified version of the previous constructor
     * and is meant to be used when you want the object to not have a rotation offset.
     * In the case you do want there is a constructor for that(won't apply to this class)
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

    /** Sets the rotation to a new value. The rotation is calculated in radians and
     * when setting it rounds the rotation to match in the range of (-2π, 2π). It returns
     * the previous rotation used
     *
     * @param rotation The new rotation(IN RADIANS)
     * @return the previously used rotation
     */
    public Vector3f setRotation(Vector3f rotation) {
        Vector3f prevRotation = this.rotation;
        this.rotation = this.normalizeRotation(rotation);
        return prevRotation;
    }

    protected Vector3f normalizeRotation(Vector3f rotation) {
        float x = (float) (rotation.x % Math.TAU);
        float y = (float) (rotation.y % Math.TAU);
        float z = (float) (rotation.z % Math.TAU);
        return new Vector3f(x, y, z);
    }

    /** Sets the amount of particles to use for rendering the object. This
     * has no effect to this class but on shapes it does have an effect.
     * It returns the previously used amount of particles
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

    /** Gets the particle that is currently in use and returns it
     *
     * @return The currently used particle
     */
    public ParticleEffect getParticleEffect() {
        return this.particleEffect;
    }

    /** Gets the amount of particles that are currently in use and returns it
     *
     * @return The currently used amount of particles
     */
    public int getAmount() {
        return this.amount;
    }

    /** Gets the rotation that is currently in use and returns it
     *
     * @return The currently used rotation
     */
    public Vector3f getRotation() {
        return this.rotation;
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

    /** Gets the current offset value used. The offset position is added
     * with the drawing position.
     *
     * @return The offset
     */
    public Vector3f getOffset() {
        return this.offset;
    }

    /** This method allows for drawing a particle object given the world, the current step and the drawing position.
     * <b>The method is used for internal workings, its not meant to be used for outside use</b>. Path animators
     * are the ones who calculate the position, the step & give the server world instance
     *
     * @param world The server world instance
     * @param step The current rendering step at
     * @param drawPos The position to draw at
    */
    public abstract void draw(ServerWorld world, int step, Vector3f drawPos);
    public void endDraw(ServerWorld world, int step, Vector3f drawPos) {}

    public void drawParticle(ServerWorld world, Vector3f drawPos) {
        world.spawnParticles(
                this.particleEffect, drawPos.x, drawPos.y, drawPos.z, 0,
                0.0f, 0.0f, 0.0f, 1
        );
    }
}
