package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.ParallelAnimator;
import net.mcbrincie.apel.lib.animators.PathAnimatorBase;
import net.mcbrincie.apel.lib.animators.PointAnimator;
import net.mcbrincie.apel.lib.objects.ParticleCuboid;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

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
        ParticleCuboid cuboid1 = new ParticleCuboid(
                ParticleTypes.END_ROD, 50,
                new Vector3f(2f, 2f, 2f)
        );

        ParticleCuboid cuboid2 = new ParticleCuboid(cuboid1);
        cuboid2.setParticleEffect(ParticleTypes.FLAME);
        cuboid2.setSize(new Vector3f(4.5f, 4.5f, 4.5f));

        ParticleCuboid cuboid3 = new ParticleCuboid(cuboid1);
        cuboid3.setSize(new Vector3f(6f, 6f, 6f));
        cuboid3.setParticleEffect(ParticleTypes.SOUL_FIRE_FLAME);

        cuboid1.beforeCalcsIntercept = (data, obj) -> {
            obj.setRotation(obj.getRotation().add(0.002f, 0, 0));
            return new InterceptedResult<>(data, obj);
        };
        cuboid2.beforeCalcsIntercept = (data, obj) -> {
            obj.setRotation(obj.getRotation().add(0, 0, 0.004f));
            return new InterceptedResult<>(data, obj);
        };
        cuboid3.beforeCalcsIntercept = (data, obj) -> {
            obj.setRotation(obj.getRotation().add(0, 0.008f, 0));
            return new InterceptedResult<>(data, obj);
        };
        ParallelAnimator animator = getParallelAnimator(cuboid1, cuboid2, cuboid3);
        animator.beginAnimation(world);
    }

    private static @NotNull ParallelAnimator getParallelAnimator(ParticleCuboid cuboid1, ParticleCuboid cuboid2, ParticleCuboid cuboid3) {
        PointAnimator pointAnimator1 = new PointAnimator(
                1, cuboid1, new Vector3f(), 1000
        );
        PointAnimator pointAnimator2 = new PointAnimator(
                1, cuboid2, new Vector3f(), 1000
        );
        PointAnimator pointAnimator3 = new PointAnimator(
                1, cuboid3, new Vector3f(), 1000
        );
        List<Integer> delays = new ArrayList<>();
        List<PathAnimatorBase> pathAnimatorList = new ArrayList<>();
        pathAnimatorList.add(pointAnimator1);
        pathAnimatorList.add(pointAnimator2);
        pathAnimatorList.add(pointAnimator3);
        delays.add(0);
        delays.add(20);
        delays.add(60);
        return new ParallelAnimator(delays, pathAnimatorList);
    }
}
