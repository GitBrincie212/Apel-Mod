package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.LinearAnimator;
import net.mcbrincie.apel.lib.animators.PointAnimator;
import net.mcbrincie.apel.lib.objects.ParticleCuboid;
import net.mcbrincie.apel.lib.objects.ParticleSphere;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Random;

public class DebugStick extends Item {
    public DebugStick(Settings settings) {
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
        ParticleSphere sphere = new ParticleSphere(
                ParticleTypes.END_ROD, 0.1f, new Vec3d(0, 0, 1), 500
        );
        sphere.beforeCalcsIntercept = (interceptData, obj) -> {
            obj.setRadius(obj.getRadius() + 0.0001f);
            // obj.setRotation(obj.getRotation().add(0.0001f, 0, 0));
            return new InterceptedResult<>(interceptData, obj);
        };
        PointAnimator animator = new PointAnimator(
                1, sphere, Vec3d.ZERO, 1000
        );
        ParticleSphere sphere2 = new ParticleSphere(sphere);
        Random rand = new Random();
        sphere2.setRotation(sphere2.getRotation().add(
                rand.nextFloat(0.5f), rand.nextFloat(0.5f), rand.nextFloat(0.5f)
        ));
        ParticleSphere sphere3 = new ParticleSphere(sphere);
        sphere3.setRotation(sphere2.getRotation().add(
                rand.nextFloat(0.5f), rand.nextFloat(0.5f), rand.nextFloat(0.5f)
        ));
        animator.setProcessingSpeed(2);
        PointAnimator animator2 = new PointAnimator(
                3, sphere2, Vec3d.ZERO, 1000
        );
        animator2.setProcessingSpeed(5);
        PointAnimator animator3 = new PointAnimator(
                7, sphere3, Vec3d.ZERO, 1000
        );
        animator3.setProcessingSpeed(6);
        animator.beginAnimation(world);
        animator2.beginAnimation(world);
        animator3.beginAnimation(world);
    }
}
