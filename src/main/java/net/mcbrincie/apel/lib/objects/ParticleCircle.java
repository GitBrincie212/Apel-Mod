package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Optional;

/** The particle object class that represents a circle(2D shape) and not a 3D sphere.
 * It has a radius which dictates how large or small the circle is depending on the
 * radius value supplied
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCircle extends ParticleObject {
    protected float radius;
    private DrawInterceptor<ParticleCircle, AfterDrawData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleCircle, BeforeDrawData> beforeDraw = DrawInterceptor.identity();

    /** This data is used before calculations(it contains the iterated rotation) */
    public enum BeforeDrawData {
        ITERATED_ROTATION
    }

    /** This data is used after calculations(it contains the drawing position) */
    public enum AfterDrawData {
        DRAW_POSITION
    }

    /** Constructor for the particle circle which is a 2D shape. It accepts as parameters
     * the particle to use, the radius of the circle, the rotation to apply & the amount of particles.
     * There is also a simplified version for no rotation.
     *
     * @param particleEffect The particle to use
     * @param amount The amount of particles for the object
     * @param radius The radius of the circle(how big it is)
     * @param rotation The rotation to apply
     *
     * @see ParticleCircle#ParticleCircle(ParticleEffect, float, int)
    */
    public ParticleCircle(
            @NotNull ParticleEffect particleEffect, float radius, Vector3f rotation, int amount
    ) {
        super(particleEffect, rotation);
        this.radius = radius;
        this.amount = amount;
    }

    /** Constructor for the particle circle which is a 2D shape. It accepts as parameters
     * the particle to use, the radius of the circle, & the amount of particles.
     * There is also a version that allows for rotation.
     *
     * @param particleEffect The particle to use
     * @param amount The amount of particles for the object
     * @param radius The radius of the circle(how big it is)
     *
     * @see ParticleCircle#ParticleCircle(ParticleEffect, float, Vector3f, int)
    */
    public ParticleCircle(
            @NotNull ParticleEffect particleEffect, float radius, int amount
    ) {
        this(particleEffect, radius, new Vector3f(0), amount);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param circle The particle circle object to copy from
    */
    public ParticleCircle(ParticleCircle circle) {
        super(circle);
        this.radius = circle.radius;
        this.amount = circle.amount;
        this.afterDraw = circle.afterDraw;
        this.beforeDraw = circle.beforeDraw;
    }

    /** Gets the radius of the ParticleCircle and returns it.
     *
     * @return the radius of the ParticleCircle
     */
    public float getRadius() {
        return radius;
    }

    /** Set the radius of this ParticleCircle and returns the previous radius that was used.
     *
     * @param radius the new radius
     * @return the previously used radius
     */
    public float setRadius(float radius) {
        float prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    @Override
    public void draw(ServerWorld world, int step, Vector3f drawPos) {
        float angleInterval = 360 / (float) this.amount;
        for (int i = 0; i < (this.amount / 2); i++) {
            double currRot = angleInterval * i;
            InterceptedResult<ParticleCircle, BeforeDrawData> modifiedPairBefore = this.doBeforeDraw(world, step, drawPos, currRot);
            // Protect against `null` being placed into map (would NPE upon unboxing)
            currRot = (double) Optional.ofNullable(modifiedPairBefore.interceptData.getMetadata(BeforeDrawData.ITERATED_ROTATION)).orElse(currRot);
            float x = (float) Math.sin(currRot);
            float y = (float) Math.cos(currRot);
            Vector3f finalPosVec = new Vector3f(this.radius * x, this.radius * y, 0)
                    // Rotate
                    .rotateZ(this.rotation.z).rotateY(this.rotation.y).rotateX(this.rotation.x)
                    // Translate
                    .add(drawPos).add(this.offset);
            this.drawParticle(world, finalPosVec);
            InterceptedResult<ParticleCircle, AfterDrawData> modifiedPairAfter = this.doAfterDraw(world, step, finalPosVec, drawPos);
        }
        this.endDraw(world, step, drawPos);
    }

    /** Set the interceptor to run after drawing the circle.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position where the circle is rendered.  It will also have the position around the circle
     * at which the current particle was drawn.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public void setAfterDraw(DrawInterceptor<ParticleCircle, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private InterceptedResult<ParticleCircle, AfterDrawData> doAfterDraw(
            ServerWorld world, int step, Vector3f drawPos, Vector3f centerPos
    ) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, centerPos, step, AfterDrawData.class);
        interceptData.addMetadata(AfterDrawData.DRAW_POSITION, drawPos);
        return this.afterDraw.apply(interceptData, this);
    }

    /** Set the interceptor to run prior to drawing the circle.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position where the circle is rendered.  It will also have the angle around the circle at
     * which the current particle is.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing each particle
     */
    public void setBeforeDraw(DrawInterceptor<ParticleCircle, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private InterceptedResult<ParticleCircle, BeforeDrawData> doBeforeDraw(
            ServerWorld world, int step, Vector3f pos, double currRot
    ) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, pos, step, BeforeDrawData.class);
        interceptData.addMetadata(BeforeDrawData.ITERATED_ROTATION, currRot);
        return this.beforeDraw.apply(interceptData, this);
    }
}
