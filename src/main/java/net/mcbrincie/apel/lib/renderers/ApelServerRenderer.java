package net.mcbrincie.apel.lib.renderers;

import net.minecraft.server.world.ServerWorld;

/**
 * This extends the {@link ApelRenderer} to provide server-side functionality that requires the {@link ServerWorld}.
 */
public interface ApelServerRenderer extends ApelRenderer {

    static ApelServerRenderer create(ServerWorld world) {
        return new DefaultApelRenderer(world);
    }

    static ApelServerRenderer client(ServerWorld world) {
        return new ApelNetworkRenderer(world);
    }

    static ApelBakingRenderer baking(ServerWorld world, String animationName) {
        return new ApelBakingRenderer(world, animationName);
    }

    ServerWorld getServerWorld();
}
