package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class ParticleSphere extends ParticleObject {
    protected float radius;
    public float insideAmount = 0;
    public @NotNull ParticleEffect insideParticle;
    public DrawInterceptor<ParticleSphere> afterCalcsIntercept;
    public DrawInterceptor<ParticleSphere> beforeCalcsIntercept;

    public ParticleSphere(
            @NotNull ParticleEffect particle, float radius,
            Vec3d rotation, int amount
    ) {
        super(particle, rotation);
        this.setRadius(radius);
        this.setAmount(amount);
        this.insideParticle = particle;
    }

    public ParticleSphere(
            @NotNull ParticleEffect particle, float radius, int amount
    ) {
        super(particle, Vec3d.ZERO);
        this.setRadius(radius);
        this.setAmount(amount);
        this.insideParticle = particle;
    }

    public ParticleSphere(
            @NotNull ParticleEffect particle, float radius, Vec3d rotation, int amount,
            int insideAmount, @NotNull ParticleEffect insideParticle
    ) {
        super(particle, rotation);
        this.setRadius(radius);
        this.setAmount(amount);
        this.insideAmount = insideAmount;
        this.insideParticle = insideParticle;
    }

    public ParticleSphere(
            @NotNull ParticleEffect particle, float radius, int amount,
            int insideAmount, @NotNull ParticleEffect insideParticle
    ) {
        super(particle, Vec3d.ZERO);
        this.setRadius(radius);
        this.setAmount(amount);
        this.insideAmount = insideAmount;
        this.insideParticle = insideParticle;
    }

    public ParticleSphere(
            @NotNull ParticleEffect particle, float radius,
            Vec3d rotation, int amount, int insideAmount
    ) {
        super(particle, rotation);
        this.setRadius(radius);
        this.setAmount(amount);
        this.insideAmount = insideAmount;
        this.insideParticle = particle;
    }

    public ParticleSphere(
            @NotNull ParticleEffect particle, float radius, int amount,
            int insideAmount
    ) {
        super(particle, Vec3d.ZERO);
        this.setRadius(radius);
        this.setAmount(amount);
        this.insideAmount = insideAmount;
        this.insideParticle = particle;
    }

    public ParticleSphere(ParticleSphere circle) {
        super(circle);
        this.insideParticle = circle.insideParticle;
        this.radius = circle.radius;
        this.amount = circle.amount;
        this.insideAmount = circle.insideAmount;
        this.afterCalcsIntercept = circle.afterCalcsIntercept;
        this.beforeCalcsIntercept = circle.beforeCalcsIntercept;
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
            InterceptedResult<ParticleSphere> modifiedResult =
                    this.interceptDrawCalcBefore(world, pos, i, step, this);
            ParticleSphere objectInUse = modifiedResult.object;
            float k = i + .5f;
            double phi = Math.acos(1f - ((2f * k) / objectInUse.amount));
            double theta = Math.PI * k * (1 + Math.sqrt(5));
            double x = Math.cos(theta) * Math.sin(phi) * objectInUse.radius;
            double y = Math.sin(theta) * Math.sin(phi) * objectInUse.radius;
            double z = Math.cos(phi) * objectInUse.radius;
            Vec3d point = objectInUse.applyRotation(x, y, z).add(pos);
            world.spawnParticles(
                    this.particle, point.x, point.y, point.z,
                    0, 0.0f, 0.0f, 0.0f, 1
            );
        }
    }

    private Vec3d applyRotation(double x, double y, double z) {
        double rotX = this.rotation.x;
        double rotY = this.rotation.y;
        double rotZ = this.rotation.z;
        double cosYaw = Math.cos(rotY);
        double sinYaw = Math.sin(rotY);
        double cosPitch = Math.cos(rotX);
        double sinPitch = Math.sin(rotX);
        double cosRoll = Math.cos(rotZ);
        double sinRoll = Math.sin(rotZ);

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
            ServerWorld world, Vec3d pos, Vec3d drawPos, int step, ParticleSphere obj
    ) {
        InterceptData interceptData = new InterceptData(world, pos, step);
        interceptData.put("draw_position", drawPos);
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
