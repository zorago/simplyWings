package com.zorago.simplywings.item;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.block.ModBlocks;
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
    public static final DeferredItem<WingItem> CROW_WINGS = wing("crow", Rarity.RARE);
    public static final DeferredItem<WingItem> OWL_WINGS = wing("owl", Rarity.RARE);
    public static final DeferredItem<WingItem> GULL_WINGS = wing("gull", Rarity.RARE);
    public static final DeferredItem<WingItem> HUMMINGBIRD_WINGS = wing("hummingbird", Rarity.RARE);
    public static final DeferredItem<WingItem> EAGLE_WINGS = wing("eagle", Rarity.RARE);
    public static final DeferredItem<WingItem> SWIFT_WINGS = wing("swift", Rarity.RARE);
    public static final DeferredItem<WingItem> PIGEON_WINGS = wing("pigeon", Rarity.RARE);
    public static final DeferredItem<WingItem> PEACOCK_WINGS = wing("peacock", Rarity.RARE);
    public static final DeferredItem<WingItem> PARROT_WINGS = wing("parrot", Rarity.RARE);
    public static final DeferredItem<WingItem> BEE_WINGS = wing("bee", Rarity.RARE);
    public static final DeferredItem<WingItem> MOTH_WINGS = wing("moth", Rarity.RARE);
    public static final DeferredItem<WingItem> BEETLE_WINGS = wing("beetle", Rarity.RARE);
    public static final DeferredItem<WingItem> SQUIRREL_WINGS = wing("squirrel", Rarity.RARE);
    public static final DeferredItem<WingItem> FLYING_FISH_WINGS = wing("flying_fish", Rarity.RARE);
    public static final DeferredItem<WingItem> PHANTOM_WINGS = wing("phantom", Rarity.RARE);
    public static final DeferredItem<WingItem> AMETHYST_WINGS = wing("amethyst", Rarity.RARE);
    public static final DeferredItem<WingItem> ORNITHOPTER_WINGS = wing("ornithopter", Rarity.RARE);
    public static final DeferredItem<WingItem> KITE_WINGS = wing("kite", Rarity.RARE);
    public static final DeferredItem<WingItem> COPPER_WINGS = wing("copper", Rarity.RARE);
    public static final DeferredItem<WingItem> PATCHWORK_WINGS = wing("patchwork", Rarity.RARE);
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

    public static final DeferredItem<WingItem> NIGHT_WINGS = wing("night", Rarity.EPIC);
    public static final DeferredItem<WingItem> LIGHT_WINGS = wing("light", Rarity.EPIC);

    public static final DeferredItem<CloudFlaskItem> CLOUD_FLASK = ITEMS.register("cloud_flask",
            () -> new CloudFlaskItem(new Item.Properties().stacksTo(16)));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("wings",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.simplywings"))
                    .icon(() -> ANGEL_WINGS.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.ASSEMBLY_TABLE_ITEM.get());
                        output.accept(CLOUD_FLASK.get());
                        output.accept(CloudFlaskItem.filled(CLOUD_FLASK.get(), CloudFlaskItem.CAPACITY));
                        WINGS.forEach(output::accept);
                    })
                    .build());

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
        TABS.register(modBus);
    }

    private ModItems() {
    }
}
