package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.Optional;


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
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleObject {
    protected ParticleEffect particleEffect;
    protected Vector3f rotation;
    protected int amount = 1;

    private DrawInterceptor<ParticleObject, BeforeDrawData> beforeDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleObject, AfterDrawData> afterDraw = DrawInterceptor.identity();

    public enum BeforeDrawData {
        DRAW_POSITION
    }
    public enum AfterDrawData {}


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
        this.beforeDraw = object.beforeDraw;
        this.afterDraw = object.afterDraw;
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
    public Vector3f getRotation() {
        return this.rotation;
    }

    /** This method allows for drawing a particle point given the world, the current step and the drawing position.
     * <b>The method is used for internal workings, its not meant to be used for outside use</b>. Path animators
     * are the ones who calculate the position, the step & give the server world instance
     * 
     * @param world The server world instance
     * @param step The current rendering step at
     * @param drawPos The position to draw at
     */
    public void draw(ServerWorld world, int step, Vector3f drawPos) {
        InterceptedResult<ParticleObject, BeforeDrawData> modifiedResult = this.doBeforeDraw(world, drawPos, step);
        Vector3f objectDrawPosition = (Vector3f) modifiedResult.interceptData.getMetadata(BeforeDrawData.DRAW_POSITION);
        this.drawParticle(world, objectDrawPosition);
        this.doAfterDraw(world, objectDrawPosition, step);
        this.endDraw(world, step, objectDrawPosition);
    }

    public void endDraw(ServerWorld world, int step, Vector3f pos) {
    }

    public void drawParticle(ServerWorld world, Vector3f position) {
        world.spawnParticles(
                this.particleEffect, position.x, position.y, position.z, 0,
                0.0f, 0.0f, 0.0f, 1
        );
    }

    public void setBeforeDraw(DrawInterceptor<ParticleObject, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    public void setAfterDraw(DrawInterceptor<ParticleObject, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private InterceptedResult<ParticleObject, BeforeDrawData> doBeforeDraw(ServerWorld world, Vector3f drawPos, int step) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, drawPos, step, BeforeDrawData.class);
        interceptData.addMetadata(BeforeDrawData.DRAW_POSITION, drawPos);
        return this.beforeDraw.apply(interceptData, this);
    }

    private InterceptedResult<ParticleObject, AfterDrawData> doAfterDraw(ServerWorld world, Vector3f drawPos, int step) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, drawPos, step, AfterDrawData.class);
        return this.afterDraw.apply(interceptData, this);
    }
}
