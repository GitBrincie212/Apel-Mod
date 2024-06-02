package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.PointAnimator;
import net.mcbrincie.apel.lib.objects.ParticleSphere;
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
        ParticleSphere sphere1 = new ParticleSphere(
                ParticleTypes.END_ROD, 6.5f,
                new Vector3f(), 5000
        );
        ParticleSphere sphere2 = new ParticleSphere(sphere1);
        sphere2.setRadius(4.5f);
        sphere2.setParticleEffect(ParticleTypes.FLAME);
        PointAnimator pointAnimator1 = new PointAnimator(
                1, sphere1, new Vector3f(), 1000
        );
        PointAnimator pointAnimator2 = new PointAnimator(
                2, sphere2, new Vector3f(), 1000
        );
        pointAnimator1.beginAnimation(world);
        pointAnimator2.beginAnimation(world);
    }
}
