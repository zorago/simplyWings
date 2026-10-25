package com.zorago.simplywings.block;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.block.recipe.ModRecipes;
import com.zorago.simplywings.gacha.AltarMenu;
import com.zorago.simplywings.gacha.DraconicAltarBlock;
import com.zorago.simplywings.gacha.FountainMenu;
import com.zorago.simplywings.gacha.OfferingFountainBlock;
import net.minecraft.world.item.Rarity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SimplyWings.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SimplyWings.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SimplyWings.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SimplyWings.MOD_ID);

    public static final DeferredBlock<AssemblyTableBlock> ASSEMBLY_TABLE = BLOCKS.register("assembly_table",
            () -> new AssemblyTableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(3.0F, 9.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .noOcclusion()));
    public static final DeferredItem<BlockItem> ASSEMBLY_TABLE_ITEM = ITEMS.register("assembly_table",
            () -> new BlockItem(ASSEMBLY_TABLE.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AssemblyTableBlockEntity>> ASSEMBLY_TABLE_ENTITY =
            BLOCK_ENTITIES.register("assembly_table",
                    () -> BlockEntityType.Builder.of(AssemblyTableBlockEntity::new, ASSEMBLY_TABLE.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<AssemblyTableMenu>> ASSEMBLY_TABLE_MENU =
            MENUS.register("assembly_table", () -> IMenuTypeExtension.create(AssemblyTableMenu::new));

    public static final DeferredBlock<ForgeBenchBlock> FORGE_BENCH = BLOCKS.register("forge_bench",
            () -> new ForgeBenchBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0F, 12.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.ANVIL)
                    .noOcclusion()));
    public static final DeferredItem<BlockItem> FORGE_BENCH_ITEM = ITEMS.register("forge_bench",
            () -> new BlockItem(FORGE_BENCH.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ForgeBenchBlockEntity>> FORGE_BENCH_ENTITY =
            BLOCK_ENTITIES.register("forge_bench",
                    () -> BlockEntityType.Builder.of(ForgeBenchBlockEntity::new, FORGE_BENCH.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<ForgeBenchMenu>> FORGE_BENCH_MENU =
            MENUS.register("forge_bench", () -> IMenuTypeExtension.create(ForgeBenchMenu::new));

    public static final DeferredBlock<CloudCompressorBlock> CLOUD_COMPRESSOR = BLOCKS.register("cloud_compressor",
            () -> new CloudCompressorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(5.0F, 12.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .dynamicShape()));
    public static final DeferredItem<BlockItem> CLOUD_COMPRESSOR_ITEM = ITEMS.register("cloud_compressor",
            () -> new BlockItem(CLOUD_COMPRESSOR.get(), new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CloudCompressorBlockEntity>> CLOUD_COMPRESSOR_ENTITY =
            BLOCK_ENTITIES.register("cloud_compressor",
                    () -> BlockEntityType.Builder.of(CloudCompressorBlockEntity::new, CLOUD_COMPRESSOR.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<CloudCompressorMenu>> CLOUD_COMPRESSOR_MENU =
            MENUS.register("cloud_compressor", () -> IMenuTypeExtension.create(CloudCompressorMenu::new));

    public static final DeferredBlock<DraconicAltarBlock> DRACONIC_ALTAR = BLOCKS.register("draconic_altar",
            () -> new DraconicAltarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()
                    .lightLevel(state -> 11)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .noOcclusion()));
    public static final DeferredItem<BlockItem> DRACONIC_ALTAR_ITEM = ITEMS.register("draconic_altar",
            () -> new BlockItem(DRACONIC_ALTAR.get(), new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredHolder<MenuType<?>, MenuType<AltarMenu>> ALTAR_MENU =
            MENUS.register("draconic_altar", () -> IMenuTypeExtension.create(AltarMenu::new));

    public static final DeferredBlock<OfferingFountainBlock> OFFERING_FOUNTAIN = BLOCKS.register("offering_fountain",
            () -> new OfferingFountainBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()
                    .lightLevel(state -> 13)
                    .sound(SoundType.AMETHYST)
                    .noOcclusion()));
    public static final DeferredItem<BlockItem> OFFERING_FOUNTAIN_ITEM = ITEMS.register("offering_fountain",
            () -> new BlockItem(OFFERING_FOUNTAIN.get(), new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredHolder<MenuType<?>, MenuType<FountainMenu>> FOUNTAIN_MENU =
            MENUS.register("offering_fountain", () -> IMenuTypeExtension.create(FountainMenu::new));

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        MENUS.register(modBus);
        ModRecipes.register(modBus);
    }

    private ModBlocks() {
    }
}
