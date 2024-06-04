package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.PointAnimator;
import net.mcbrincie.apel.lib.objects.ParticleCombiner;
import net.mcbrincie.apel.lib.objects.ParticleCuboid;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class DebugParticleAnim1 extends Item {
    public DebugParticleAnim1(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(user.getMainHandStack());
        animators((ServerWorld) world);
        // combineAnimator.beginAnimation((ServerWorld) world);
        return TypedActionResult.pass(user.getMainHandStack());
    }

    private void animators(ServerWorld world) {
        ParticleCuboid cuboid1 = new ParticleCuboid(
                ParticleTypes.END_ROD, 50,
                new Vector3f(2f, 2f, 2f)
        );

        ParticleCuboid cuboid2 = new ParticleCuboid(cuboid1);
        cuboid2.setParticleEffect(ParticleTypes.FLAME);
        cuboid2.setSize(new Vector3f(4.5f, 4.5f, 4.5f));

        ParticleCuboid cuboid3 = new ParticleCuboid(cuboid1);
        cuboid3.setSize(new Vector3f(6f, 6f, 6f));
        cuboid3.setParticleEffect(ParticleTypes.SOUL_FIRE_FLAME);

        ParticleCuboid cuboid4 = new ParticleCuboid(cuboid1);
        cuboid4.setParticleEffect(ParticleTypes.COMPOSTER);
        cuboid4.setSize(new Vector3f(7.5f, 7.5f, 7.5f));

        ParticleCuboid cuboid5 = new ParticleCuboid(cuboid1);
        cuboid5.setSize(new Vector3f(9f, 9f, 9f));
        cuboid5.setParticleEffect(ParticleTypes.ENCHANTED_HIT);

        ParticleCuboid cuboid6 = new ParticleCuboid(cuboid1);
        cuboid6.setSize(new Vector3f(11f, 11f, 11f));
        cuboid6.setParticleEffect(ParticleTypes.DRAGON_BREATH);

        ParticleCombiner<ParticleCuboid> combinedObj = new ParticleCombiner<>(cuboid1, cuboid2, cuboid3);
        ParticleCombiner<ParticleCuboid> combinedObj2 = new ParticleCombiner<>(cuboid4, cuboid5, cuboid6);

        combinedObj.beforeChildRenderIntercept = (data, obj) -> {
            ParticleCuboid cuboidInUse = (ParticleCuboid) data.getMetadata(
                    ParticleCombiner.beforeChildRenderData.OBJECT_IN_USE
            );
            Vector3f rot = new Vector3f();
            if (cuboidInUse.equals(cuboid1)) rot.add(0.002f, 0, 0);
            else if (cuboidInUse.equals(cuboid2)) rot.add(0, 0, 0.004f);
            else if (cuboidInUse.equals(cuboid3)) rot.add(0, 0.008f, 0);
            cuboidInUse.setRotation(cuboidInUse.getRotation().add(rot));
            return new InterceptedResult<>(data, obj);
        };

        combinedObj2.beforeChildRenderIntercept = (data, obj) -> {
            ParticleCuboid cuboidInUse = (ParticleCuboid) data.getMetadata(
                    ParticleCombiner.beforeChildRenderData.OBJECT_IN_USE
            );
            Vector3f rot = new Vector3f();
            if (cuboidInUse.equals(cuboid4)) rot.add(0.002f, 0, 0.002f);
            else if (cuboidInUse.equals(cuboid5)) rot.add(-0.004f, 0, 0.004f);
            else if (cuboidInUse.equals(cuboid6)) rot.add(0.008f, 0, -0.008f);
            cuboidInUse.setRotation(cuboidInUse.getRotation().add(rot));
            return new InterceptedResult<>(data, obj);
        };

        ParticleCombiner<?> wholeObj = new ParticleCombiner<>(combinedObj, combinedObj2);

        wholeObj.afterChildRenderIntercept = (data, obj) -> {
            if (data.currentStep >= 200 && data.currentStep <= 210) {
                obj.setParticleEffectRecursively(ParticleTypes.CRIT);
            }
            return new InterceptedResult<>(data, obj);
        };

        PointAnimator pointAnimator1 = new PointAnimator(
                1, wholeObj, new Vector3f(), 1000
        );
        pointAnimator1.beginAnimation(world);
    }
}
