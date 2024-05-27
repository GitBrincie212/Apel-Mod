package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.LinearAnimator;
import net.mcbrincie.apel.lib.objects.ParticleSphere;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
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
        Random rand = new Random();
        ParticleSphere sphere = new ParticleSphere(
                ParticleTypes.END_ROD, 5.5f, new Vec3d(0, 0, 1), 100
        );
        sphere.beforeCalcsIntercept = (interceptData, obj) -> {
            obj.setRotation(obj.getRotation().add(0.0001f, 0, 0));
            return new InterceptedResult<>(interceptData, obj);
        };
        LinearAnimator animator = new LinearAnimator(
                0, Vec3d.ZERO, new Vec3d(
                    rand.nextFloat(-7.5f, 7.5f),
                    rand.nextFloat(-7.5f, 7.5f),
                    rand.nextFloat(-7.5f, 7.5f)
                ), sphere, 3000
        );
        animator.beginAnimation(world);
    }
}
