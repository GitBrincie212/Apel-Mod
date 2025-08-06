package net.mcbrincie.apel.item;

import net.minecraft.item.Item;

@FunctionalInterface
public interface DebugParticleWandFactory<T extends Item> {
    T create(
            Item.Settings settings
    );
}
