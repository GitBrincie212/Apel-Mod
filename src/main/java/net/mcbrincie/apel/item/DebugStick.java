package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.CircularAnimator;
import net.mcbrincie.apel.lib.animators.CombinativeAnimator;
import net.mcbrincie.apel.lib.animators.PointAnimator;
import net.mcbrincie.apel.lib.objects.ParticleCuboid;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class DebugStick extends Item {
    public DebugStick(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(user.getMainHandStack());
        ParticleObject particleCircleObj = new ParticleObject(ParticleTypes.END_ROD);
        ParticleCuboid particleCuboid = new ParticleCuboid(
                ParticleTypes.END_ROD, new Vec3i(90, 10, 90), new Vec3d(3, 7, 3), new Vec3d(0, 0, 0)
        );
        PointAnimator centerPoint = new PointAnimator(
                1, particleCuboid, new Vec3d(0, 0, 0), 250
        );
        centerPoint.beginAnimation((ServerWorld) world);
        // CombinativeAnimator<CircularAnimator> combineAnimator = getAnimators((ServerWorld) world, particleCircleObj, false);
        // CombinativeAnimator<CircularAnimator> combineAnimator2 = getAnimators((ServerWorld) world, particleCircleObj, true);
        // combineAnimator.beginAnimation((ServerWorld) world);
        return TypedActionResult.pass(user.getMainHandStack());
    }

    private CombinativeAnimator<CircularAnimator> getAnimators(ServerWorld world, ParticleObject particleCircleObj, boolean speed) {
        CircularAnimator animator = new CircularAnimator(
                1, 3, speed ? Vec3d.ZERO : new Vec3d(0, 10, 0),
                new Vec3d(Math.PI / 2, 0, 0), particleCircleObj, 350
        );
        if (speed) animator.setProcessingSpeed(4);
        CircularAnimator animator2 = new CircularAnimator(animator);
        animator2.rotate(Math.PI, 0, 0);
        CircularAnimator animator3 = new CircularAnimator(animator);
        animator3.rotate(Math.PI / 3, 0, 0);
        CircularAnimator animator4 = new CircularAnimator(animator);
        animator4.rotate(Math.TAU / 3, 0, 0);
        float veryCloseToStart = (float) (Math.TAU - 0.00001f);
        for (int i = 0; i < 300; i++) {
            CircularAnimator animatorRedudant = new CircularAnimator(animator);
            animatorRedudant.rotate(i * 10, i * 2, i * 9);
            animatorRedudant.beginAnimation(world, 0, veryCloseToStart, false);
        }
        animator.beginAnimation(world, 0, veryCloseToStart, false);
        animator2.beginAnimation(world, 0, veryCloseToStart, false);
        animator3.beginAnimation(world, 0, veryCloseToStart, false);
        animator4.beginAnimation(world, 0, veryCloseToStart, false);
        return new CombinativeAnimator<>(
                1, 5
        );
    }
}
