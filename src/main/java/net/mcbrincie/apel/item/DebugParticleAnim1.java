package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.animators.PointAnimator;
import net.mcbrincie.apel.lib.objects.ParticleTetrahedron;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
        Vec3d[] vertices = {
                new Vec3d(0, 5, 5),
                new Vec3d(5, 0, 5),
                Vec3d.ZERO,
                new Vec3d(5, 5, 0),
        };
        ParticleTetrahedron tetrahedron = new ParticleTetrahedron(
                ParticleTypes.END_ROD, vertices, 100
        );
        tetrahedron.duringDrawIntercept = (InterceptData data, ParticleTetrahedron object) -> {
            object.setVertex1(object.getVertex1().add(0, 0.00001f, 0.00001f));
            object.setVertex2(object.getVertex2().add(0.00001f, 0, -0.00001f));
            object.setVertex4(object.getVertex4().add(-0.00001f, 0.00001f, 0));
            return new InterceptedResult<>(data, object);
        };
        PointAnimator pointAnimator = new PointAnimator(
                1, tetrahedron, Vec3d.ZERO, 1000
        );
        pointAnimator.beginAnimation(world);
    }
}
