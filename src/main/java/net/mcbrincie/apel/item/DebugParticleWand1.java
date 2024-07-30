package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.LinearAnimator;
import net.mcbrincie.apel.lib.animators.PointAnimator;
import net.mcbrincie.apel.lib.ease.LinearEase;
import net.mcbrincie.apel.lib.objects.*;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class DebugParticleWand1 extends Item {
    public DebugParticleWand1(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(user.getMainHandStack());
        animators((ServerWorld) world, user);
        return TypedActionResult.pass(user.getMainHandStack());
    }

    // When a user right-clicks the wand, this gets triggered
    private void animators(ServerWorld world, PlayerEntity user) {
        ParticleSphere sphere = ParticleSphere.builder()
                .amount(100)
                .radius(new LinearEase<>(0.1f, 0.0f))
                .particleEffect(ParticleTypes.END_ROD)
                .build();
        PointAnimator pointAnimator = PointAnimator.builder()
                .delay(1)
                .renderingSteps(500)
                .point(new Vector3f())
                .particleObject(sphere)
                .build();
        pointAnimator.beginAnimation(ApelServerRenderer.create(world));
    }
}
