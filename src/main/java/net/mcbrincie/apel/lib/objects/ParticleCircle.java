package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class ParticleCircle extends ParticleObject {
    public float radius;
    public float insideAmount = 0;
    public @NotNull ParticleEffect insideParticle;
    public DrawInterceptor<ParticleCircle> afterCalcsIntercept;
    public DrawInterceptor<ParticleCircle> beforeCalcsIntercept;

    public ParticleCircle(
            @NotNull ParticleEffect particle, float radius, Vec3d rotation, int amount
    ) {
        super(particle, rotation);
        this.radius = radius;
        this.amount = amount;
        this.insideParticle = particle;
    }

    public ParticleCircle(
            @NotNull ParticleEffect particle, float radius, int amount
    ) {
        super(particle, Vec3d.ZERO);
        this.radius = radius;
        this.amount = amount;
        this.insideParticle = particle;
    }

    public ParticleCircle(
            @NotNull ParticleEffect particle, float radius, Vec3d rotation, int amount,
            int insideAmount, @NotNull ParticleEffect insideParticle
    ) {
        super(particle, rotation);
        this.radius = radius;
        this.insideAmount = insideAmount;
        this.amount = amount;
        this.insideParticle = insideParticle;
    }

    public ParticleCircle(
            @NotNull ParticleEffect particle, float radius, int amount,
            int insideAmount, @NotNull ParticleEffect insideParticle
    ) {
        super(particle, Vec3d.ZERO);
        this.radius = radius;
        this.amount = amount;
        this.insideAmount = insideAmount;
        this.insideParticle = insideParticle;
    }

    public ParticleCircle(
            @NotNull ParticleEffect particle, float radius,
            Vec3d rotation, int amount, int insideAmount
    ) {
        super(particle, rotation);
        this.radius = radius;
        this.insideAmount = insideAmount;
        this.amount = amount;
        this.insideParticle = particle;
    }

    public ParticleCircle(
            @NotNull ParticleEffect particle, float radius, int amount,
            int insideAmount
    ) {
        super(particle, Vec3d.ZERO);
        this.radius = radius;
        this.amount = amount;
        this.insideAmount = insideAmount;
        this.insideParticle = particle;
    }

    public ParticleCircle(ParticleCircle circle) {
        super(circle);
        this.insideParticle = circle.insideParticle;
        this.radius = circle.radius;
        this.amount = circle.amount;
        this.insideAmount = circle.insideAmount;
        this.afterCalcsIntercept = circle.afterCalcsIntercept;
        this.beforeCalcsIntercept = circle.beforeCalcsIntercept;
    }

    @Override
    public void draw(ServerWorld world, int step, Vec3d pos) {
        float angleInterval = 360 / (float) this.amount;
        for (int i = 0; i < (this.amount / 2); i++) {
            double currRot = angleInterval * i;
            InterceptedResult<ParticleCircle> modifiedPairBefore =
                    this.interceptDrawCalcBefore(world, pos, currRot, step, this);
            ParticleCircle objectToUse = modifiedPairBefore.object;
            currRot = (double) (modifiedPairBefore.interceptData.get("iterated_rotation"));
            double x = Math.sin(currRot);
            double y = Math.cos(currRot);
            Vec3d circVec = new Vec3d(
                    objectToUse.radius * x,
                    objectToUse.radius * y,
                    0
            );
            circVec = circVec
                    .rotateZ((float) objectToUse.rotation.z)
                    .rotateY((float) objectToUse.rotation.y)
                    .rotateX((float) objectToUse.rotation.x);
            Vec3d finalPosVec = circVec.add(pos);
            InterceptedResult<ParticleCircle> modifiedPairAfter =
                    objectToUse.interceptDrawCalcAfter(world, pos, finalPosVec, step, objectToUse);
            finalPosVec = (Vec3d) (modifiedPairAfter.interceptData.get("draw_position"));
            world.spawnParticles(
                    objectToUse.particle, finalPosVec.x, finalPosVec.y, finalPosVec.z, 0,
                    0.0f, 0.0f, 0.0f, 1.0f
            );
        }
    }

    private InterceptedResult<ParticleCircle> interceptDrawCalcAfter(
            ServerWorld world, Vec3d pos, Vec3d drawPos, int step, ParticleCircle obj
    ) {
        InterceptData interceptData = new InterceptData(world, pos, step);
        interceptData.put("draw_position", drawPos);
        if (this.afterCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.afterCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleCircle> interceptDrawCalcBefore(
            ServerWorld world, Vec3d pos, double currRot, int step, ParticleCircle obj
    ) {
        InterceptData interceptData = new InterceptData(world, pos, step);
        interceptData.put("iterated_rotation", currRot);
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeCalcsIntercept.apply(interceptData, obj);
    }
}
