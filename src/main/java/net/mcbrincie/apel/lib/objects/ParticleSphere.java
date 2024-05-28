package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleSphere extends ParticleObject {
    protected float radius;
    public DrawInterceptor<ParticleSphere> afterCalcsIntercept;
    public DrawInterceptor<ParticleSphere> beforeCalcsIntercept;

    // Caching the trig function
    private Vec2f cachedYaw = Vec2f.ZERO;
    private Vec2f cachedPitch = Vec2f.ZERO;
    private Vec2f cachedRoll = Vec2f.ZERO;
    private Vec3d prevRotation = null;

    private float cachedValI = -1.0f;
    private float cachedAmount = -1.0f;
    private Vec3d cachedCoords;

    public ParticleSphere(
            @NotNull ParticleEffect particle, float radius,
            Vec3d rotation, int amount
    ) {
        super(particle, rotation);
        this.setRadius(radius);
        this.setAmount(amount);
    }

    public ParticleSphere(
            @NotNull ParticleEffect particle, float radius, int amount
    ) {
        super(particle, Vec3d.ZERO);
        this.setRadius(radius);
        this.setAmount(amount);
    }

    public ParticleSphere(ParticleSphere circle) {
        super(circle);
        this.radius = circle.radius;
        this.amount = circle.amount;
        this.afterCalcsIntercept = circle.afterCalcsIntercept;
        this.beforeCalcsIntercept = circle.beforeCalcsIntercept;
        this.cachedCoords = circle.cachedCoords;
        this.cachedValI = circle.cachedValI;
        this.cachedAmount = circle.cachedAmount;
        this.cachedYaw = circle.cachedYaw;
        this.cachedPitch = circle.cachedPitch;
        this.cachedRoll = circle.cachedRoll;
        this.prevRotation = circle.prevRotation;
    }

    public float setRadius(float radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius cannot be below or equal to 0");
        }
        float prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    public float getRadius() {
        return this.radius;
    }

    @Override
    public void draw(ServerWorld world, int step, Vec3d pos) {
        for (int i = 0; i < this.amount; i++) {
            InterceptedResult<ParticleSphere> modifiedResultBefore =
                    this.interceptDrawCalcBefore(world, pos, i, step, this);
            ParticleSphere objectInUse = modifiedResultBefore.object;
            Vec3d drawPos = objectInUse.computeCoords(i);
            drawPos = objectInUse.applyRotation(drawPos.x, drawPos.y, drawPos.z).add(pos);
            InterceptedResult<ParticleSphere> modifiedResultAfter =
                    this.interceptDrawCalcAfter(world, pos, drawPos, step, i, objectInUse);
            drawPos = (Vec3d) modifiedResultAfter.interceptData.get("draw_position");
            ParticleSphere objectInUseAfter = modifiedResultAfter.object;
            world.spawnParticles(
                    objectInUseAfter.particle, drawPos.x, drawPos.y, drawPos.z,
                    0, 0.0f, 0.0f, 0.0f, 1
            );
        }
    }

    private Vec3d computeCoords(int i) {
        if (i == this.cachedValI && this.amount == this.cachedAmount) {
            return cachedCoords.multiply(this.radius);
        }
        float k = i + .5f;
        double phi = Math.acos(1f - ((2f * k) / this.amount));
        double theta = Math.PI * k * 3.23606;
        double sinPhi = Math.sin(phi);
        double x = Math.cos(theta) * sinPhi;
        double y = Math.sin(theta) * sinPhi;
        double z = Math.cos(phi);
        this.cachedValI = i;
        this.cachedAmount = this.amount;
        Vec3d pos = new Vec3d(x, y, z);
        this.cachedCoords = pos;
        return pos.multiply(this.radius);
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

    private Vec3d applyRotation(double x, double y, double z) {
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
        double z2 = -x * sinYaw + z1 * cosYaw;

        // Apply roll (rotation around Z-axis)
        double x2 = x1 * cosRoll - y1 * sinRoll;
        double y2 = x1 * sinRoll + y1 * cosRoll;

        return new Vec3d(x2, y2, z2);
    }

    private InterceptedResult<ParticleSphere> interceptDrawCalcAfter(
            ServerWorld world, Vec3d pos, Vec3d drawPos,
            int step, int currAmount, ParticleSphere obj
    ) {
        InterceptData interceptData = new InterceptData(world, pos, step);
        interceptData.put("draw_position", drawPos);
        interceptData.put("current_amount", currAmount);
        if (this.afterCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.afterCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleSphere> interceptDrawCalcBefore(
            ServerWorld world, Vec3d pos, int currAmount, int step, ParticleSphere obj
    ) {
        InterceptData interceptData = new InterceptData(world, pos, step);
        interceptData.put("current_amount", currAmount);
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeCalcsIntercept.apply(interceptData, obj);
    }
}
