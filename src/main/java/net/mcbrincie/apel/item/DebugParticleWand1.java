package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.LinearAnimator;
import net.mcbrincie.apel.lib.animators.PointAnimator;
import net.mcbrincie.apel.lib.objects.ParticleEllipsoid;
import net.mcbrincie.apel.lib.objects.ParticleSphere;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class DebugParticleWand1 extends Item {
    public DebugParticleWand1(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return ActionResult.PASS;
        ParticleSphere particleSphere = ParticleSphere.builder()
                .particleEffect(ParticleTypes.END_ROD)
                .amount(200)
                .radius(2.0f)
                .build();
        PointAnimator pointAnimator = PointAnimator.builder()
                .point(new Vector3f(0f, 0f, 0f))
                .renderingSteps(100)
                .particleObject(particleSphere)
                .build();
        ApelServerRenderer renderer = ApelServerRenderer.client((ServerWorld) world);
        pointAnimator.beginAnimation(renderer);
        return ActionResult.PASS;
    }
}
