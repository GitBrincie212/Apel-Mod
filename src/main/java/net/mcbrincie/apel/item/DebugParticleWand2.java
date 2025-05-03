package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.BezierCurveAnimator;
import net.mcbrincie.apel.lib.animators.ParallelAnimator;
import net.mcbrincie.apel.lib.animators.PathAnimatorBase;
import net.mcbrincie.apel.lib.animators.PointAnimator;
import net.mcbrincie.apel.lib.objects.*;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.math.bezier.QuadraticBezierCurve;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
