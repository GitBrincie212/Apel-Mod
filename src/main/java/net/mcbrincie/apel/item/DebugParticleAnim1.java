package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.PointAnimator;
import net.mcbrincie.apel.lib.objects.ParticleLine;
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
        ParticleLine line = new ParticleLine(
                ParticleTypes.END_ROD, new Vector3f(-5, -5, -5),
                new Vector3f(5, 5, 5), 50
        );
        PointAnimator pointAnimator = new PointAnimator(1, line, new Vector3f(), 10);
        pointAnimator.beginAnimation(world);
    }
}
