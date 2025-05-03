package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.PointAnimator;
import net.mcbrincie.apel.lib.objects.ParticleBranchGen;
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

        ParticleBranchGen particleBranchGen = ParticleBranchGen.builder()
                .start(new Vector3f(0))
                .end(new Vector3f(0, 8, 0))
                .amount(100)
                .particleEffect(ParticleTypes.SCULK_SOUL)
                .minDimensions(new Vector3f(1, 1, 1))
                .maxDimensions(new Vector3f(3, 3, 3))
                .subDivisions(10)
                .build();

        PointAnimator pointAnimator = PointAnimator.builder()
                .point(new Vector3f(0))
                .particleObject(particleBranchGen)
                .renderingSteps(200)
                .build();

        ApelServerRenderer apelServerRenderer = ApelServerRenderer.client((ServerWorld) world);
        pointAnimator.beginAnimation(apelServerRenderer);

        return ActionResult.PASS;
    }
}
