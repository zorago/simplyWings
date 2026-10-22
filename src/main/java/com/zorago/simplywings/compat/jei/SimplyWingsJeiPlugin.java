package com.zorago.simplywings.compat.jei;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.block.AssemblyTableMenu;
import com.zorago.simplywings.block.ModBlocks;
import com.zorago.simplywings.block.recipe.AssemblyRecipe;
import com.zorago.simplywings.block.recipe.ComponentRecipe;
import com.zorago.simplywings.block.recipe.ModRecipes;
import com.zorago.simplywings.client.gui.AssemblyTableScreen;
import com.zorago.simplywings.item.ModItems;
import com.zorago.simplywings.loot.LootSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

@JeiPlugin
public class SimplyWingsJeiPlugin implements IModPlugin {
    static final RecipeType<RecipeHolder<AssemblyRecipe>> ASSEMBLY = RecipeType.createRecipeHolderType(SimplyWings.id("assembly"));
    static final RecipeType<RecipeHolder<ComponentRecipe>> COMPONENTS = RecipeType.createRecipeHolderType(SimplyWings.id("components"));
    static final RecipeType<ItemStack> ENERGY = RecipeType.create(SimplyWings.MOD_ID, "winged_energy", ItemStack.class);
    static final RecipeType<LootSource> MOB_DROPS = RecipeType.create(SimplyWings.MOD_ID, "winged_mob_drops", LootSource.class);
    static final RecipeType<LootSource> BLOCK_DROPS = RecipeType.create(SimplyWings.MOD_ID, "winged_block_drops", LootSource.class);
    static final RecipeType<LootSource> FISHING = RecipeType.create(SimplyWings.MOD_ID, "winged_fishing", LootSource.class);
    private static final int FIRST_ASSEMBLY_SLOT = 1;
    private static final int FIRST_GRID_SLOT = 7;
    private static final int GRID_SLOTS = 21;
    private static final int FIRST_INVENTORY_SLOT = 29;
    private static final int INVENTORY_SLOTS = 36;

    @Override
    public ResourceLocation getPluginUid() {
        return SimplyWings.id("jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new AssemblyCategory(gui), new ComponentCategory(gui), new EnergyCategory(gui),
                new DropCategory(gui, LootSource.Kind.MOB), new DropCategory(gui, LootSource.Kind.BLOCK),
                new DropCategory(gui, LootSource.Kind.FISH));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        var recipes = level.getRecipeManager();
        registration.addRecipes(ASSEMBLY, recipes.getAllRecipesFor(ModRecipes.ASSEMBLY.get()));
        registration.addRecipes(COMPONENTS, recipes.getAllRecipesFor(ModRecipes.COMPONENT.get()));
        registration.addRecipes(ENERGY, List.of(new ItemStack(Items.IRON_SWORD), new ItemStack(Items.IRON_PICKAXE),
                new ItemStack(Items.IRON_AXE), new ItemStack(Items.IRON_SHOVEL), new ItemStack(Items.FISHING_ROD),
                new ItemStack(Items.BOW)));
        List<LootSource> drops = new ArrayList<>();
        LootSource.registry(level.registryAccess()).ifPresent(r -> r.forEach(drops::add));
        registration.addRecipes(MOB_DROPS, drops.stream().filter(d -> d.kind() == LootSource.Kind.MOB).toList());
        registration.addRecipes(BLOCK_DROPS, drops.stream().filter(d -> d.kind() == LootSource.Kind.BLOCK).toList());
        registration.addRecipes(FISHING, drops.stream().filter(d -> d.kind() == LootSource.Kind.FISH).toList());
        registration.addItemStackInfo(new ItemStack(ModBlocks.ASSEMBLY_TABLE_ITEM.get()),
                Component.translatable("jei.simplywings.info.assembly_table"));
        registration.addItemStackInfo(new ItemStack(ModItems.FORGE_COMPASS.get()),
                Component.translatable("jei.simplywings.info.forge_compass"));
        registration.addItemStackInfo(List.of(new ItemStack(ModItems.CLOUD_FLASK.get()), new ItemStack(ModItems.CLOUD_ABSORBER.get())),
                Component.translatable("jei.simplywings.info.cloud_essence"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.ASSEMBLY_TABLE_ITEM.get(), ASSEMBLY, COMPONENTS, ENERGY);
        registration.addRecipeCatalyst(DropCategory.tool(LootSource.Kind.MOB), MOB_DROPS);
        registration.addRecipeCatalyst(DropCategory.tool(LootSource.Kind.BLOCK), BLOCK_DROPS);
        registration.addRecipeCatalyst(DropCategory.tool(LootSource.Kind.FISH), FISHING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(AssemblyTableScreen.class, new IGuiContainerHandler<>() {
            @Override
            public java.util.Collection<IGuiClickableArea> getGuiClickableAreas(AssemblyTableScreen screen, double mouseX, double mouseY) {
                return switch (screen.getMenu().tab()) {
                    case AssemblyTableMenu.TAB_ASSEMBLY -> List.of(IGuiClickableArea.createBasic(125, 45, 20, 15, ASSEMBLY));
                    case AssemblyTableMenu.TAB_COMPONENTS -> List.of(IGuiClickableArea.createBasic(124, 55, 22, 15, COMPONENTS));
                    default -> List.of(IGuiClickableArea.createBasic(100, 44, 20, 15, ENERGY));
                };
            }
        });
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new TabTransfer<>(ASSEMBLY, AssemblyTableMenu.TAB_ASSEMBLY, FIRST_ASSEMBLY_SLOT, 4));
        registration.addRecipeTransferHandler(new TabTransfer<>(COMPONENTS, AssemblyTableMenu.TAB_COMPONENTS, FIRST_GRID_SLOT, GRID_SLOTS));
    }

    private record TabTransfer<R>(RecipeType<R> type, int tab, int first, int count)
            implements IRecipeTransferInfo<AssemblyTableMenu, R> {
        @Override
        public Class<? extends AssemblyTableMenu> getContainerClass() {
            return AssemblyTableMenu.class;
        }

        @Override
        public Optional<MenuType<AssemblyTableMenu>> getMenuType() {
            return Optional.of(ModBlocks.ASSEMBLY_TABLE_MENU.get());
        }

        @Override
        public RecipeType<R> getRecipeType() {
            return type;
        }

        @Override
        public boolean canHandle(AssemblyTableMenu menu, R recipe) {
            return menu.tab() == tab;
        }

        @Override
        public List<Slot> getRecipeSlots(AssemblyTableMenu menu, R recipe) {
            return menu.slots.subList(first, first + count);
        }

        @Override
        public List<Slot> getInventorySlots(AssemblyTableMenu menu, R recipe) {
            return menu.slots.subList(FIRST_INVENTORY_SLOT, FIRST_INVENTORY_SLOT + INVENTORY_SLOTS);
        }
    }

}
