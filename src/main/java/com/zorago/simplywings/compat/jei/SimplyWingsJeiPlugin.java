package com.zorago.simplywings.compat.jei;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.block.AssemblyTableMenu;
import com.zorago.simplywings.block.EssenceBenchMenu;
import com.zorago.simplywings.block.ForgeBenchBlockEntity;
import com.zorago.simplywings.block.ForgeBenchMenu;
import com.zorago.simplywings.block.ModBlocks;
import com.zorago.simplywings.block.recipe.AssemblyRecipe;
import com.zorago.simplywings.block.recipe.ComponentRecipe;
import com.zorago.simplywings.block.recipe.ModRecipes;
import com.zorago.simplywings.client.gui.AssemblyTableScreen;
import com.zorago.simplywings.client.gui.ForgeBenchScreen;
import com.zorago.simplywings.essence.EssenceType;
import com.zorago.simplywings.item.ModItems;
import com.zorago.simplywings.loot.LootSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
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
    private static IJeiRuntime runtime;
    static final RecipeType<RecipeHolder<AssemblyRecipe>> ASSEMBLY = RecipeType.createRecipeHolderType(SimplyWings.id("assembly"));
    static final RecipeType<RecipeHolder<ComponentRecipe>> COMPONENTS = RecipeType.createRecipeHolderType(SimplyWings.id("components"));
    static final RecipeType<EssenceType> COMPRESSOR = RecipeType.create(SimplyWings.MOD_ID, "cloud_compression", EssenceType.class);
    static final RecipeType<ItemStack> ENERGY = RecipeType.create(SimplyWings.MOD_ID, "winged_energy", ItemStack.class);
    static final RecipeType<LootSource> MOB_DROPS = RecipeType.create(SimplyWings.MOD_ID, "winged_mob_drops", LootSource.class);
    static final RecipeType<LootSource> BLOCK_DROPS = RecipeType.create(SimplyWings.MOD_ID, "winged_block_drops", LootSource.class);
    static final RecipeType<LootSource> FISHING = RecipeType.create(SimplyWings.MOD_ID, "winged_fishing", LootSource.class);
    private static final int INVENTORY_SLOTS = 36;

    @Override
    public ResourceLocation getPluginUid() {
        return SimplyWings.id("jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new AssemblyCategory(gui), new ComponentCategory(gui), new EnergyCategory(gui), new CompressorCategory(gui),
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
        registration.addRecipes(COMPRESSOR, List.of(EssenceType.INTENSIFIED));
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
        registration.addItemStackInfo(new ItemStack(ModBlocks.FORGE_BENCH_ITEM.get()),
                Component.translatable("jei.simplywings.info.forge_bench"));
        registration.addItemStackInfo(new ItemStack(ModBlocks.CLOUD_COMPRESSOR_ITEM.get()),
                Component.translatable("jei.simplywings.info.cloud_compressor"));
        registration.addItemStackInfo(new ItemStack(ModBlocks.DRACONIC_ALTAR_ITEM.get()),
                Component.translatable("jei.simplywings.info.draconic_altar"));
        registration.addItemStackInfo(new ItemStack(ModBlocks.OFFERING_FOUNTAIN_ITEM.get()),
                Component.translatable("jei.simplywings.info.offering_fountain"));
        registration.addItemStackInfo(new ItemStack(ModItems.SUMMONING_FEATHER.get()),
                Component.translatable("jei.simplywings.info.summoning_feather"));
        ModItems.COMPASSES.forEach(compass -> registration.addItemStackInfo(new ItemStack(compass.get()),
                Component.translatable("jei.simplywings.info." + compass.getId().getPath())));
        List<ItemStack> essence = new ArrayList<>();
        ModItems.CONTAINERS.forEach(container -> essence.add(new ItemStack(container.get())));
        ModItems.ABSORBERS.forEach(absorber -> essence.add(new ItemStack(absorber.get())));
        registration.addItemStackInfo(essence, Component.translatable("jei.simplywings.info.cloud_essence"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.ASSEMBLY_TABLE_ITEM.get(), ASSEMBLY);
        registration.addRecipeCatalyst(ModBlocks.FORGE_BENCH_ITEM.get(), COMPONENTS, ENERGY);
        registration.addRecipeCatalyst(ModBlocks.CLOUD_COMPRESSOR_ITEM.get(), COMPRESSOR);
        registration.addRecipeCatalyst(DropCategory.tool(LootSource.Kind.MOB), MOB_DROPS);
        registration.addRecipeCatalyst(DropCategory.tool(LootSource.Kind.BLOCK), BLOCK_DROPS);
        registration.addRecipeCatalyst(DropCategory.tool(LootSource.Kind.FISH), FISHING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(AssemblyTableScreen.class, new IGuiContainerHandler<>() {
            @Override
            public java.util.Collection<IGuiClickableArea> getGuiClickableAreas(AssemblyTableScreen screen, double mouseX, double mouseY) {
                return List.of(IGuiClickableArea.createBasic(125, 45, 20, 15, ASSEMBLY));
            }
        });
        registration.addGuiContainerHandler(ForgeBenchScreen.class, new IGuiContainerHandler<>() {
            @Override
            public java.util.Collection<IGuiClickableArea> getGuiClickableAreas(ForgeBenchScreen screen, double mouseX, double mouseY) {
                return screen.getMenu().tab() == ForgeBenchMenu.TAB_COMPONENTS
                        ? List.of(IGuiClickableArea.createBasic(124, 55, 22, 15, COMPONENTS))
                        : List.of(IGuiClickableArea.createBasic(100, 44, 20, 15, ENERGY));
            }
        });
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new Transfer<>(AssemblyTableMenu.class, ModBlocks.ASSEMBLY_TABLE_MENU.get(), ASSEMBLY,
                menu -> true, AssemblyTableMenu.FIRST_INPUT_SLOT, 4));
        registration.addRecipeTransferHandler(new Transfer<>(ForgeBenchMenu.class, ModBlocks.FORGE_BENCH_MENU.get(), COMPONENTS,
                menu -> menu.tab() == ForgeBenchMenu.TAB_COMPONENTS, ForgeBenchMenu.FIRST_GRID_SLOT, ForgeBenchBlockEntity.GRID_CELLS));
    }

    private record Transfer<M extends EssenceBenchMenu, R>(Class<M> menuClass, MenuType<M> menuType, RecipeType<R> type,
                                                           Predicate<M> ready, int first, int count)
            implements IRecipeTransferInfo<M, R> {
        @Override
        public Class<? extends M> getContainerClass() {
            return menuClass;
        }

        @Override
        public Optional<MenuType<M>> getMenuType() {
            return Optional.of(menuType);
        }

        @Override
        public RecipeType<R> getRecipeType() {
            return type;
        }

        @Override
        public boolean canHandle(M menu, R recipe) {
            return ready.test(menu);
        }

        @Override
        public List<Slot> getRecipeSlots(M menu, R recipe) {
            return menu.slots.subList(first, first + count);
        }

        @Override
        public List<Slot> getInventorySlots(M menu, R recipe) {
            int start = menu.slots.size() - INVENTORY_SLOTS;
            return menu.slots.subList(start, start + INVENTORY_SLOTS);
        }
    }


    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    public static boolean showItem(ItemStack stack, boolean uses) {
        if (runtime == null) {
            return false;
        }
        RecipeIngredientRole role = uses ? RecipeIngredientRole.INPUT : RecipeIngredientRole.OUTPUT;
        runtime.getRecipesGui().show(runtime.getJeiHelpers().getFocusFactory().createFocus(role, VanillaTypes.ITEM_STACK, stack));
        return true;
    }

    public static boolean showCategory(String name) {
        if (runtime == null) {
            return false;
        }
        RecipeType<?> type = switch (name) {
            case "assembly" -> ASSEMBLY;
            case "components" -> COMPONENTS;
            case "energy" -> ENERGY;
            case "compressor" -> COMPRESSOR;
            case "mob" -> MOB_DROPS;
            case "block" -> BLOCK_DROPS;
            case "fishing" -> FISHING;
            default -> null;
        };
        if (type == null) {
            return false;
        }
        runtime.getRecipesGui().showTypes(List.of(type));
        return true;
    }
}
