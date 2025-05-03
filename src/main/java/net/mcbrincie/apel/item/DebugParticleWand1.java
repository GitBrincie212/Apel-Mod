package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.*;
import net.mcbrincie.apel.lib.objects.ParticleCombiner;
import net.mcbrincie.apel.lib.objects.ParticleSphere;
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

public class DebugParticleWand1 extends Item {
    public DebugParticleWand1(Settings settings) {
        super(settings);
    }

    private BezierCurveAnimator createSculkBall(PlayerEntity user, Vector3f targetPos, Vector3f bendPoint) {
        Vector3f userPos = user.getPos().add(0, user.getHeight() / 2, 0).toVector3f();
        Vector3f bendPointPos = getPositionFromLookDir(user, bendPoint).sub(userPos).add(targetPos);

        ParticleSphere particleMainSphere = ParticleSphere.builder()
                .particleEffect(ParticleTypes.ENCHANT)
                .amount(20)
                .radius(0.8f)
                .build();

        ParticleSphere particleSphere2 = new ParticleSphere(particleMainSphere);
        particleSphere2.setParticleEffect(ParticleTypes.SQUID_INK);
        particleSphere2.setRadius(0.6f);

        ParticleSphere particleSphere3 = new ParticleSphere(particleMainSphere);
        particleSphere3.setParticleEffect(ParticleTypes.SCULK_SOUL);
        particleSphere3.setAmount(10);
        particleSphere3.setRadius(0.35f);

        ParticleSphere particleSphere4 = new ParticleSphere(particleMainSphere);
        particleSphere4.setParticleEffect(ParticleTypes.SMOKE);
        particleSphere4.setRadius(0.75f);
        particleSphere4.setAmount(30);
        particleSphere4.setAfterDraw((data, obj) -> {
            Box hitbox = Box.of(new Vec3d(data.getPosition()), 5f, 4f, 5f);
            List<LivingEntity> livingEntities = data.getWorld().getEntitiesByClass(
                    LivingEntity.class,
                    hitbox,
                    (entity) -> !entity.isPlayer()
            );
            for (LivingEntity target : livingEntities) {
                if (target.canTakeDamage()) {
                    data.getWorld().playSound(
                            null,
                            target.getPos().x,
                            target.getPos().y,
                            target.getPos().z,
                            SoundEvents.ENTITY_WITHER_HURT,
                            SoundCategory.MASTER,
                            1f,
                            0.8f
                    );
                }
                target.damage(data.getWorld(), new DamageSource(
                        data.getWorld()
                                .getRegistryManager()
                                .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                                .getEntry(DamageTypes.MAGIC.getValue())
                                .get()
                ), 10);
            }
        });

        ParticleCombiner particleCombiner = ParticleCombiner.builder()
                .object(particleMainSphere)
                .object(particleSphere2)
                .object(particleSphere3)
                .object(particleSphere4)
                .build();

        return BezierCurveAnimator.builder()
                .bezierCurve(new QuadraticBezierCurve(
                        userPos,
                        targetPos,
                        bendPointPos
                ))
                .intervalForAllCurves(0.3f)
                .delay(1)
                .beforeRender((animationContext, animator) -> {
                    if (animationContext.getCurrentStep() == 0) {
                        animationContext.getWorld().playSound(
                                null,
                                userPos.x,
                                userPos.y,
                                userPos.z,
                                SoundEvents.ENTITY_WITHER_SHOOT,
                                SoundCategory.MASTER,
                                1f,
                                1.2f
                        );
                    }
                    animator.setProcessingSpeed((animationContext.getCurrentStep() + 50) / 50);
                })
                .particleObject(particleCombiner)
                .build();
    }

    private Vector3f getPositionFromLookDir(PlayerEntity user, Vector3f dist) {
        Vec3d lookDir = user.getRotationVec(1.0F).normalize();
        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d right = lookDir.crossProduct(up).normalize();
        up = right.crossProduct(lookDir).normalize();
        Vec3d offset = right.multiply(dist.x)
                .add(up.multiply(dist.y))
                .add(lookDir.multiply(dist.z));

        Vec3d result = user.getPos().add(offset);
        return result.toVector3f();
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return ActionResult.PASS;
        HitResult hitResult = user.raycast(100, 1.0f, false);
        Vector3f pos = hitResult.getPos().toVector3f();
        BezierCurveAnimator bezierCurveAnimator1 = createSculkBall(user, pos, new Vector3f(0, 10f, -pos.z / 2));
        BezierCurveAnimator bezierCurveAnimator2 = createSculkBall(user, pos, new Vector3f(0, -10f, -pos.z / 2));
        BezierCurveAnimator bezierCurveAnimator3 = createSculkBall(user, pos, new Vector3f(10f, 0, -pos.z / 2));
        BezierCurveAnimator bezierCurveAnimator4 = createSculkBall(user, pos, new Vector3f(-10f, 0, -pos.z / 2));
        BezierCurveAnimator bezierCurveAnimator5 = createSculkBall(user, pos, new Vector3f(-5f, 5f, -pos.z / 2));
        BezierCurveAnimator bezierCurveAnimator6 = createSculkBall(user, pos, new Vector3f(5f, -5f, -pos.z / 2));
        BezierCurveAnimator bezierCurveAnimator7 = createSculkBall(user, pos, new Vector3f(-5f, -5f, -pos.z / 2));
        BezierCurveAnimator bezierCurveAnimator8 = createSculkBall(user, pos, new Vector3f(5f, 5f, -pos.z / 2));

        List<PathAnimatorBase<? extends PathAnimatorBase<?>>> bezierCurveAnimators = new ArrayList<>(List.of(
                bezierCurveAnimator1, bezierCurveAnimator2, bezierCurveAnimator3, bezierCurveAnimator4,
                bezierCurveAnimator5, bezierCurveAnimator6, bezierCurveAnimator7, bezierCurveAnimator8
        ));

        Collections.shuffle(bezierCurveAnimators);
        ParallelAnimator parallelAnimator = ParallelAnimator.builder()
                .animators(bezierCurveAnimators, List.of(0, 10, 20, 30, 40, 45, 55, 60))
                .build();

        ApelServerRenderer renderer = ApelServerRenderer.create((ServerWorld) world);
        parallelAnimator.beginAnimation(renderer);
        return ActionResult.PASS;
    }
}
