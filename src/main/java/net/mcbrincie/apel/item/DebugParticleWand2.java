package net.mcbrincie.apel.item;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DebugParticleWand2 extends Item {
    public DebugParticleWand2(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return ActionResult.PASS;

        ApelServerRenderer renderer = ApelServerRenderer.client((ServerWorld) world);
        return ActionResult.PASS;
    }
}
