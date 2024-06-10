package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/** The particle object class that represents a sphere(3D shape) and not a 2D circle.
 * It has a radius which dictates how large or small the sphere is depending on the
 * radius value supplied. And it uses the Fibonacci point distribution algorithm
 * to place the particles around the sphere(which makes it convincing)
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleSphere extends ParticleObject {
    protected float radius;
    public DrawInterceptor<ParticleSphere, afterCalc> afterCalcsIntercept;
    public DrawInterceptor<ParticleSphere, beforeCalc> beforeCalcsIntercept;

    /** This data is used before calculations(it contains the amount of particles) */
    public enum beforeCalc {
        AMOUNT
    }

    /** This data is used after calculations(it contains the drawing position & the amount of particles) */
    public enum afterCalc {
        DRAWING_POSITION, AMOUNT
    }

    // Caching the trig function
    private Vec2f cachedYaw = Vec2f.ZERO;
    private Vec2f cachedPitch = Vec2f.ZERO;
    private Vec2f cachedRoll = Vec2f.ZERO;
    private Vector3f prevRotation = null;

    private float cachedValI = -1.0f;
    private float cachedAmount = -1.0f;
    private Vector3f cachedCoords;

    /** Constructor for the particle sphere which is a 3D shape. It accepts as parameters
     * the particle to use, the radius of the sphere, the rotation to apply & the amount of particles.
     * There is also a simplified version for no rotation.
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param radius The radius of the sphere
     * @param rotation The rotation to apply
     *
     * @see ParticleSphere#ParticleSphere(ParticleEffect, float, int)
    */
    public ParticleSphere(
            @NotNull ParticleEffect particle, float radius,
            Vector3f rotation, int amount
    ) {
        super(particle, rotation);
        this.setRadius(radius);
        this.setAmount(amount);
    }

    /** Constructor for the particle cuboid which is a 3D shape. It accepts as parameters
     * the particle to use, the radius of the sphere & the amount of particles. It is a simplified version
     * for the case when no rotation is meant to be applied. For rotation offset you can
     * use another constructor
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param radius The radius of the sphere
     *
     * @see ParticleSphere#ParticleSphere(ParticleEffect, float, Vector3f, int)
    */
    public ParticleSphere(
            @NotNull ParticleEffect particle, float radius, int amount
    ) {
        super(particle);
        this.setRadius(radius);
        this.setAmount(amount);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param sphere The particle sphere object to copy from
    */
    public ParticleSphere(ParticleSphere sphere) {
        super(sphere);
        this.radius = sphere.radius;
        this.amount = sphere.amount;
        this.afterCalcsIntercept = sphere.afterCalcsIntercept;
        this.beforeCalcsIntercept = sphere.beforeCalcsIntercept;
        this.cachedCoords = sphere.cachedCoords;
        this.cachedValI = sphere.cachedValI;
        this.cachedAmount = sphere.cachedAmount;
        this.cachedYaw = sphere.cachedYaw;
        this.cachedPitch = sphere.cachedPitch;
        this.cachedRoll = sphere.cachedRoll;
        this.prevRotation = sphere.prevRotation;
    }

    /** Sets the radius of the sphere
     *
     * @param radius The radius of the sphere
     * @return The previous radius used
    */
    public float setRadius(float radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius cannot be below or equal to 0");
        }
        float prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    /** Gets the radius of the sphere
     *
     * @return the radius of the sphere
     */
    public float getRadius() {
        return this.radius;
    }

    @Override
    public void draw(ServerWorld world, int step, Vector3f pos) {
        for (int i = 0; i < this.amount; i++) {
            InterceptedResult<ParticleSphere, beforeCalc> modifiedResultBefore =
                    this.interceptDrawCalcBefore(world, pos, i, step, this);
            ParticleSphere objectInUse = modifiedResultBefore.object;
            Vector3f drawPos = objectInUse.computeCoords(i);
            drawPos = objectInUse.applyRotation(drawPos.x, drawPos.y, drawPos.z).add(pos);
            InterceptedResult<ParticleSphere, afterCalc> modifiedResultAfter =
                    this.interceptDrawCalcAfter(world, pos, drawPos, step, i, objectInUse);
            drawPos = (Vector3f) modifiedResultAfter.interceptData.getMetadata(afterCalc.DRAWING_POSITION);
            ParticleSphere objectInUseAfter = modifiedResultAfter.object;
            this.drawParticle(world, drawPos);
        }
    }

    private Vector3f computeCoords(int i) {
        if (i == this.cachedValI && this.amount == this.cachedAmount) {
            return cachedCoords.mul(this.radius);
        }
        float k = i + .5f;
        double phi = Math.acos(1f - ((2f * k) / this.amount));
        double theta = Math.PI * k * 3.23606;
        double sinPhi = Math.sin(phi);
        float x = (float) (Math.cos(theta) * sinPhi);
        float y = (float) (Math.sin(theta) * sinPhi);
        float z = (float) Math.cos(phi);
        this.cachedValI = i;
        this.cachedAmount = this.amount;
        Vector3f pos = new Vector3f(x, y, z);
        this.cachedCoords = pos;
        return pos.mul(this.radius);
    }

    private Vec2f computeYaw() {
        double rotY = this.rotation.y;
        if (this.prevRotation != null && rotY == this.prevRotation.y) {
            return this.cachedYaw;
        }
        float cosYaw = (float) Math.cos(rotY);
        float sinYaw = (float) Math.sin(rotY);
        Vec2f yawVec = new Vec2f(cosYaw, sinYaw);
        this.cachedYaw = yawVec;
        return yawVec;
    }

    private Vec2f computePitch() {
        double rotX = this.rotation.x;
        if (this.prevRotation != null && rotX == this.prevRotation.x) {
            return this.cachedPitch;
        }
        float cosPitch = (float) Math.cos(rotX);
        float sinPitch = (float) Math.sin(rotX);
        Vec2f pitchVec = new Vec2f(cosPitch, sinPitch);
        this.cachedPitch = pitchVec;
        return pitchVec;
    }

    private Vec2f computeRoll() {
        double rotZ = this.rotation.z;
        if (this.prevRotation != null && rotZ == this.prevRotation.z) {
            return this.cachedRoll;
        }
        float cosRoll = (float) Math.cos(rotZ);
        float sinRoll = (float) Math.sin(rotZ);
        Vec2f rollVec = new Vec2f(cosRoll, sinRoll);
        this.cachedRoll = rollVec;
        return rollVec;
    }

    private Vector3f applyRotation(double x, double y, double z) {
        Vec2f pitchVec = computePitch();
        Vec2f yawVec = computeYaw();
        Vec2f rollVec = computeRoll();
        double cosYaw = yawVec.x;
        double sinYaw = yawVec.y;
        double cosPitch = pitchVec.x;
        double sinPitch = pitchVec.y;
        double cosRoll = rollVec.x;
        double sinRoll = rollVec.y;
        this.prevRotation = this.rotation;

        // Apply pitch (rotation around X-axis)
        double y1 = y * cosPitch - z * sinPitch;
        double z1 = y * sinPitch + z * cosPitch;

        // Apply yaw (rotation around Y-axis)
        double x1 = x * cosYaw + z1 * sinYaw;
        float z2 = (float) (-x * sinYaw + z1 * cosYaw);

        // Apply roll (rotation around Z-axis)
        float x2 = (float) (x1 * cosRoll - y1 * sinRoll);
        float y2 = (float) (x1 * sinRoll + y1 * cosRoll);

        return new Vector3f(x2, y2, z2);
    }

    private InterceptedResult<ParticleSphere, afterCalc> interceptDrawCalcAfter(
            ServerWorld world, Vector3f pos, Vector3f drawPos,
            int step, int currAmount, ParticleSphere obj
    ) {
        InterceptData<afterCalc> interceptData = new InterceptData<>(world, pos, step, afterCalc.class);
        interceptData.addMetadata(afterCalc.DRAWING_POSITION, drawPos);
        interceptData.addMetadata(afterCalc.AMOUNT, currAmount);
        if (this.afterCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.afterCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleSphere, beforeCalc> interceptDrawCalcBefore(
            ServerWorld world, Vector3f pos, int currAmount, int step, ParticleSphere obj
    ) {
        InterceptData<beforeCalc> interceptData = new InterceptData<>(world, pos, step, beforeCalc.class);
        interceptData.addMetadata(beforeCalc.AMOUNT, currAmount);
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeCalcsIntercept.apply(interceptData, obj);
    }
}
