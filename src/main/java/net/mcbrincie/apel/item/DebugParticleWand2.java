package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.PointAnimator;
import net.mcbrincie.apel.lib.easing.LinearEasingCurve;
import net.mcbrincie.apel.lib.objects.ParticleBranchGen;
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

public class DebugParticleWand2 extends Item {
    public DebugParticleWand2(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return ActionResult.PASS;

        LinearEasingCurve<Float> linearEasingCurve = new LinearEasingCurve<>(3f, 8f);
        linearEasingCurve.setEaseProgressFactor(1.2f);
        ParticleSphere particleSphere = ParticleSphere.builder()
                .radius(linearEasingCurve)
                .amount(100)
                .particleEffect(ParticleTypes.END_ROD)
                .build();

        PointAnimator pointAnimator = PointAnimator.builder()
                .point(new Vector3f(0))
                .particleObject(particleSphere)
                .renderingSteps(300)
                .build();

        ApelServerRenderer apelServerRenderer = ApelServerRenderer.client((ServerWorld) world);
        pointAnimator.beginAnimation(apelServerRenderer);

        return ActionResult.PASS;
    }
}
