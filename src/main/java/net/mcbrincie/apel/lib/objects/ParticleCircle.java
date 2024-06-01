package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3i;

/** The particle object class that represents a circle(2D shape) and not a 3D sphere.
 * It has a radius which dictates how large or small the circle is depending on the
 * radius value supplied
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCircle extends ParticleObject {
    public float radius;
    public DrawInterceptor<ParticleCircle, afterCalc> afterCalcsIntercept;
    public DrawInterceptor<ParticleCircle, beforeCalc> beforeCalcsIntercept;

    /** This data is used before calculations(it contains the iterated rotation) */
    public enum beforeCalc {
        ITERATED_ROTATION
    }

    /** This data is used after calculations(it contains the drawing position) */
    public enum afterCalc {
        DRAW_POSITION
    }

    /** Constructor for the particle circle which is a 2D shape. It accepts as parameters
     * the particle to use, the radius of the circle, the rotation to apply & the amount of particles.
     * There is also a simplified version for no rotation.
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param radius The radius of the circle(how big it is)
     * @param rotation The rotation to apply
     *
     * @see ParticleCircle#ParticleCircle(ParticleEffect, float, int)
    */
    public ParticleCircle(
            @NotNull ParticleEffect particle, float radius, Vector3f rotation, int amount
    ) {
        super(particle, rotation);
        this.radius = radius;
        this.amount = amount;
    }

    /** Constructor for the particle circle which is a 2D shape. It accepts as parameters
     * the particle to use, the radius of the circle, & the amount of particles.
     * There is also a version that allows for rotation.
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param radius The radius of the circle(how big it is)
     *
     * @see ParticleCircle#ParticleCircle(ParticleEffect, float, Vector3f, int)
    */
    public ParticleCircle(
            @NotNull ParticleEffect particle, float radius, int amount
    ) {
        super(particle);
        this.radius = radius;
        this.amount = amount;
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
        this.afterCalcsIntercept = circle.afterCalcsIntercept;
        this.beforeCalcsIntercept = circle.beforeCalcsIntercept;
    }

    @Override
    public void draw(ServerWorld world, int step, Vector3f pos) {
        float angleInterval = 360 / (float) this.amount;
        for (int i = 0; i < (this.amount / 2); i++) {
            double currRot = angleInterval * i;
            InterceptedResult<ParticleCircle, beforeCalc> modifiedPairBefore =
                    this.interceptDrawCalcBefore(world, pos, currRot, step, this);
            ParticleCircle objectToUse = modifiedPairBefore.object;
            currRot = (double) (modifiedPairBefore.interceptData.getMetadata(beforeCalc.ITERATED_ROTATION));
            float x = (float) Math.sin(currRot);
            float y = (float) Math.cos(currRot);
            Vector3f circumferenceVec = new Vector3f(
                    objectToUse.radius * x,
                    objectToUse.radius * y,
                    0
            );
            circumferenceVec = circumferenceVec
                    .rotateZ(objectToUse.rotation.z)
                    .rotateY(objectToUse.rotation.y)
                    .rotateX(objectToUse.rotation.x);
            Vector3f finalPosVec = circumferenceVec.add(pos);
            InterceptedResult<ParticleCircle, afterCalc> modifiedPairAfter =
                    objectToUse.interceptDrawCalcAfter(world, pos, finalPosVec, step, objectToUse);
            finalPosVec = (Vector3f) (modifiedPairAfter.interceptData.getMetadata(afterCalc.DRAW_POSITION));
            this.drawParticle(world, finalPosVec);
        }
        this.endDraw(world, step, pos);
    }

    private InterceptedResult<ParticleCircle, afterCalc> interceptDrawCalcAfter(
            ServerWorld world, Vector3f pos, Vector3f drawPos, int step, ParticleCircle obj
    ) {
        InterceptData<afterCalc> interceptData = new InterceptData<>(world, pos, step, afterCalc.class);
        interceptData.addMetadata(afterCalc.DRAW_POSITION, drawPos);
        if (this.afterCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.afterCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleCircle, beforeCalc> interceptDrawCalcBefore(
            ServerWorld world, Vector3f pos, double currRot, int step, ParticleCircle obj
    ) {
        InterceptData<beforeCalc> interceptData = new InterceptData<>(world, pos, step, beforeCalc.class);
        interceptData.addMetadata(beforeCalc.ITERATED_ROTATION, currRot);
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeCalcsIntercept.apply(interceptData, obj);
    }
}
