package net.mcbrincie.apel.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mcbrincie.apel.Apel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item WAND_1 = registerWand(
            "debug_particle_wand_1",
            DebugParticleWand1::new
    );

    public static void appendItems(FabricItemGroupEntries entries) {
        entries.add(WAND_1);
    }

    private static <T extends Item> T registerWand(String name, DebugParticleWandFactory<T> wandFactory) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Apel.MOD_ID, name));

        return Registry.register(
                Registries.ITEM,
                key,
                wandFactory.create(new Item.Settings().registryKey(key))
        );
    }

    public static void initItems() {
        Apel.LOGGER.info("Registering Modded Items");
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::appendItems);
    }
}
