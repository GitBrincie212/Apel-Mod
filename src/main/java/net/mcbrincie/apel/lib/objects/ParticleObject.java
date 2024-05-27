package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;


/** The base class for any particle object. Particle objects are the things
 * that will be rendered. This can be a cube, a sphere, a 2D circle, a cat, a
 * dog.... etc. The calculation logic is done in the draw method which the
 * handler system periodically calls each render step. You can inherit
 * from the class and even use it(it will be a point), it has common interceptors
 * like the before calculation interceptor & the after calculation interceptor.
 * <br><br>
 * <strong>Note</strong> rotation calculations are in radians and not in degrees.
 * As well as if the rotation on one or multiple axis exceeds 2π then it is rounded
 * to the scope for that (-2π, 2π)
*/
public class ParticleObject {
    protected ParticleEffect particle;
    protected Vec3d rotation;
    protected int amount = 0;

    public DrawInterceptor<ParticleObject> afterCalcsIntercept;
    public DrawInterceptor<ParticleObject> beforeCalcsIntercept;


    /** Constructor for the particle object which is a point. It accepts as parameters
     * the particle to use and the rotation to apply(which has no effect. Only on the
     * path animators that extend this class). There is also a simplified version
     * for no rotation.
     *
     * @see ParticleObject#ParticleObject(ParticleObject)
     *
     * @param particle The particle effect to use
     * @param rotation The rotation(IN RADIANS)
    */
    public ParticleObject(ParticleEffect particle, Vec3d rotation) {
        this.particle = particle;
        this.setRotation(rotation);
    }

    /** Constructor for the particle object which is a point. It accepts as parameters
     * the particle to use. It is a simplified version of the previous constructor
     * and is meant to be used when you want the object to not have a rotation offset.
     * In the case you do want there is a constructor for that(won't apply to this class)
     *
     * @see ParticleObject#ParticleObject(ParticleEffect, Vec3d)
     *
     * @param particle The particle effect to use
     */
    public ParticleObject(ParticleEffect particle) {
        this.particle = particle;
        this.rotation = Vec3d.ZERO;
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param object The particle object to copy from
     */
    public ParticleObject(ParticleObject object) {
        this.particle = object.particle;
        this.rotation = object.rotation;
        this.beforeCalcsIntercept = object.beforeCalcsIntercept;
        this.afterCalcsIntercept = object.afterCalcsIntercept;
    }

    /** Sets the rotation to a new value. The rotation is calculated in radians and
     * when setting it rounds the rotation to match in the range of (-2π, 2π). It returns
     * the previous rotation used
     *
     * @param rotation The new rotation(IN RADIANS)
     * @return the previously used rotation
     */
    public Vec3d setRotation(Vec3d rotation) {
        Vec3d prevRotation = this.rotation;
        double x = rotation.x % Math.TAU;
        double y = rotation.y % Math.TAU;
        double z = rotation.z % Math.TAU;
        this.rotation = new Vec3d(x, y, z);
        return prevRotation;
    }

    /** Sets the particle to use to a new value and returns the previous
     *  particle that was used
     *
     * @param particle The new particle
     * @return The previously used particle
     */
    public ParticleEffect setParticleEffect(ParticleEffect particle) {
        ParticleEffect prevParticle = this.particle;
        this.particle = particle;
        return prevParticle;
    }

    /** Gets the particle that is currently in use and returns it
     *
     * @return The currently used particle
     */
    public ParticleEffect getParticleEffect() {
        return this.particle;
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
    public Vec3d getRotation() {
        return this.rotation;
    }

    /** This method isn't meant to be used by the user and serves as a method for the system to use.
     *  It calls this method to tell the object to render how it looks on every rendering step. It
     *  supplies it with the server world, the current step and the position to draw at. And returns
     *  nothing back. Its best practice to support the interceptors API
     *
     * @param world The server world instance
     * @param step The current rendering step at
     * @param pos The position to draw at
     */
    public void draw(ServerWorld world, int step, Vec3d pos) {
        InterceptedResult<ParticleObject> modifiedResult =
                this.interceptDrawCalcBefore(world, pos, step, this);
        pos = (Vec3d) modifiedResult.interceptData.get("position");
        world.spawnParticles(
                this.particle, pos.x, pos.y, pos.z, 0,
                0.0f, 0.0f, 0.0f, 1.0f
        );
        this.interceptDrawCalcAfter(world, pos, step, this);
    }

    private InterceptedResult<ParticleObject> interceptDrawCalcBefore(
            ServerWorld world, Vec3d pos, int step, ParticleObject obj
    ) {
        InterceptData interceptData = new InterceptData(world, pos, step);
        interceptData.put("position", pos);
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeCalcsIntercept.apply(interceptData, obj);
    }

    private void interceptDrawCalcAfter(
            ServerWorld world, Vec3d pos, int step, ParticleObject obj
    ) {
        InterceptData interceptData = new InterceptData(world, pos, step);
        if (this.beforeCalcsIntercept == null) return;
        this.beforeCalcsIntercept.apply(interceptData, obj);
    }
}
