package com.zorago.simplywings.item;

import com.zorago.simplywings.SimplyWings;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SimplyWings.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SimplyWings.MOD_ID);
    private static final List<DeferredItem<WingItem>> WINGS = new ArrayList<>();

    public static final DeferredItem<WingItem> COMMON_WINGS = wing("common", Rarity.COMMON);
    public static final DeferredItem<WingItem> BAT_WINGS = wing("bat", Rarity.RARE);
    public static final DeferredItem<WingItem> ANCIENT_WINGS = wing("ancient", Rarity.EPIC);
    public static final DeferredItem<WingItem> FIRE_WINGS = wing("fire", Rarity.EPIC);
    public static final DeferredItem<WingItem> WATER_WINGS = wing("water", Rarity.EPIC);
    public static final DeferredItem<WingItem> EARTH_WINGS = wing("earth", Rarity.EPIC);
    public static final DeferredItem<WingItem> AIR_WINGS = wing("air", Rarity.EPIC);
    public static final DeferredItem<WingItem> PTERO_WINGS = wing("ptero", Rarity.EPIC);
    public static final DeferredItem<WingItem> STORM_WINGS = wing("storm", Rarity.EPIC);
    public static final DeferredItem<WingItem> AURORA_WINGS = wing("aurora", Rarity.EPIC);
    public static final DeferredItem<WingItem> SHADOW_WINGS = wing("shadow", Rarity.EPIC);
    public static final DeferredItem<WingItem> NEBULA_WINGS = wing("nebula", Rarity.EPIC);
    public static final DeferredItem<WingItem> FOREST_WINGS = wing("forest", Rarity.EPIC);
    public static final DeferredItem<WingItem> ANGEL_WINGS = wing("angel", Rarity.EPIC);
    public static final DeferredItem<WingItem> PSYCHE_WINGS = wing("psyche", Rarity.EPIC);
    public static final DeferredItem<WingItem> DEMON_WINGS = wing("demon", Rarity.EPIC);
    public static final DeferredItem<WingItem> SERAPH_WINGS = wing("seraph", Rarity.EPIC);
    public static final DeferredItem<WingItem> DAEDALUS_WINGS = wing("daedalus", Rarity.EPIC);

    private static DeferredItem<WingItem> wing(String name, Rarity rarity) {
        DeferredItem<WingItem> item = ITEMS.register(name + "_wings",
                () -> new WingItem(new Item.Properties().stacksTo(1).rarity(rarity), SimplyWings.id(name)));
        WINGS.add(item);
        return item;
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("wings",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.simplywings"))
                    .icon(() -> ANGEL_WINGS.get().getDefaultInstance())
                    .displayItems((parameters, output) -> WINGS.forEach(output::accept))
                    .build());

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
        TABS.register(modBus);
    }

    private ModItems() {
    }
}
