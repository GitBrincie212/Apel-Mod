package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelRenderer;
import net.minecraft.particle.ParticleEffect;
import org.joml.Vector3f;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class ParticleObject {
    protected ParticleEffect particleEffect;
    protected Vector3f rotation;
    protected Vector3f offset = new Vector3f(0, 0, 0);
    protected int amount = 1;

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
     * the previous rotation used
     *
     * @param rotation The new rotation (IN RADIANS)
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
     * This method allows for drawing a particle object given the world, the current step and the drawing position.
     * <b>The method is used for internal workings, its not meant to be used for outside use</b>. Path animators
     * are the ones who calculate the position, the step & give the server world instance
     *
     * @param renderer The server world instance
     * @param step     The current rendering step at
     * @param drawPos  The position to draw at
     */
    public abstract void draw(ApelRenderer renderer, int step, Vector3f drawPos);
    public void endDraw(ApelRenderer renderer, int step, Vector3f drawPos) {}

    protected Vector3f rigidTransformation(ApelRenderer renderer, Vector3f rotation, Vector3f offset, float x, float y, float z) {
        return new Vector3f(x, y, z)
                .rotateZ(rotation.z)
                .rotateY(rotation.y)
                .rotateX(rotation.x)
                .add(offset);
    }

    protected Vector3f rigidTransformation(ApelRenderer renderer, Vector3f rotation, Vector3f offset, Vector3f position) {
        return new Vector3f(position)
                .rotateZ(rotation.z)
                .rotateY(rotation.y)
                .rotateX(rotation.x)
                .add(offset);
    }

    protected void drawParticle(ApelRenderer renderer, int step, Vector3f drawPos) {
        this.drawParticle(this.particleEffect, renderer, step, drawPos);
    }

    protected void drawParticle(ParticleEffect particle, ApelRenderer renderer, int step, Vector3f drawPos) {
        renderer.drawParticle(particle, step, drawPos);
    }

    /**
     * Draws a line of particles from {@code start} to {@code end}.  The line will have {@code amount}
     * particles in it, inclusive of particles at both {@code start} and {@code end}.
     *
     * @param renderer The server world instance
     * @param start    The start point of the line
     * @param end      The end point of the line
     * @param amount   The number of particles in the line must be greater than 1.
     *
     * @throws ArithmeticException if amount == 1
     */
    protected void drawLine(ApelRenderer renderer, Vector3f start, Vector3f end, int step, int amount) {
        renderer.drawLine(this.particleEffect, step, start, end, amount);
    }

    /**
     * Draws a point along an ellipse shape that has a center {@code center}.
     *
     * @param renderer The renderer to use
     * @param r The radius
     * @param h The stretch / height
     * @param angle The angle value
     * @param center The center position
     * @param step The step
     * @return The coordinates of the point in the ellipse
     */
    protected Vector3f drawEllipsePoint(ApelRenderer renderer, float r, float h, float angle, Vector3f center, int step) {
        return renderer.drawEllipsePoint(
                this.particleEffect, r, h, angle, this.rotation, new Vector3f(center).add(this.offset), step
        );
    }
}
