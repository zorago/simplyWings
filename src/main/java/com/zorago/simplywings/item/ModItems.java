package com.zorago.simplywings.item;

import com.zorago.simplywings.SimplyWings;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SimplyWings.MOD_ID);

    public static final DeferredItem<WingItem> COMMON_WINGS = ITEMS.register("common_wings",
            () -> new WingItem(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON), SimplyWings.id("common")));
    public static final DeferredItem<WingItem> ANGEL_WINGS = ITEMS.register("angel_wings",
            () -> new WingItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), SimplyWings.id("angel")));

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(COMMON_WINGS);
            event.accept(ANGEL_WINGS);
        }
    }

    private ModItems() {
    }
}
