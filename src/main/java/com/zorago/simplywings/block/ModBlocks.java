package com.zorago.simplywings.block;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.block.recipe.ModRecipes;
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
