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
    public ParticleEffect particle;
    public DrawInterceptor<ParticleObject> afterCalcsIntercept;
    public DrawInterceptor<ParticleObject> beforeCalcsIntercept;
    public Vec3d rotation;

    public ParticleObject(ParticleEffect particle, Vec3d rotation) {
        this.particle = particle;
        double x = rotation.x % Math.TAU;
        double y = rotation.x % Math.TAU;
        double z = rotation.x % Math.TAU;
        this.rotation = new Vec3d(x, y, z);
    }

    public ParticleObject(ParticleEffect particle) {
        this.particle = particle;
        this.rotation = Vec3d.ZERO;
    }

    public ParticleObject(ParticleObject object) {
        this.particle = object.particle;
        this.rotation = object.rotation;
    }


    public void draw(ServerWorld world, int step, Vec3d pos) {
        InterceptedResult<ParticleObject> modifiedPairBefore =
                this.interceptDrawCalcBefore(world, pos, step, this);
        pos = (Vec3d) modifiedPairBefore.interceptData.get("position");
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
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(this, interceptData);
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
