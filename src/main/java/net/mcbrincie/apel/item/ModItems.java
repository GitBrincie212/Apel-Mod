package net.mcbrincie.apel.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mcbrincie.apel.Apel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item WAND_1 = registerItem(
            "debug_particle_wand_1",
            new DebugParticleWand1(new Item.Settings())
    );

    public static void appendItems(FabricItemGroupEntries entries) {
        entries.add(WAND_1);
    }

    private static Item registerItem(String name, Item item) {
        Identifier id = new Identifier(Apel.MOD_ID, name);
        return Registry.register(Registries.ITEM, id, item);
    }

    public static void initItems() {
        Apel.LOGGER.info("Registering Modded Items");
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::appendItems);
    }
}
