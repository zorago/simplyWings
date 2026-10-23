package com.zorago.simplywings.block;

import com.zorago.simplywings.block.recipe.ComponentInput;
import com.zorago.simplywings.block.recipe.ComponentRecipe;
import com.zorago.simplywings.block.recipe.ModRecipes;
import com.zorago.simplywings.essence.EssenceCost;
import com.zorago.simplywings.item.WingedEnergy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;

public class ForgeBenchMenu extends EssenceBenchMenu {
    public static final int TAB_COMPONENTS = 0;
    public static final int TAB_ENERGY = 1;
    public static final int TABS = 2;
    public static final int BUTTON_TAB_COMPONENTS = 0;
    public static final int BUTTON_TAB_ENERGY = 1;
    public static final int BUTTON_ENERGY = 2;
    public static final int ENERGY_SLOT = 1;
    public static final int FIRST_GRID_SLOT = 2;
    public static final int RESULT_SLOT = FIRST_GRID_SLOT + ForgeBenchBlockEntity.GRID_CELLS;

    private final ForgeBenchBlockEntity forge;
    private final ResultContainer result = new ResultContainer();
    private int tab = TAB_COMPONENTS;
    private EssenceCost costCache;
    private int lastFill = -1;

    public ForgeBenchMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, null, new SimpleContainer(ForgeBenchBlockEntity.SIZE));
        buf.readBlockPos();
    }

    public ForgeBenchMenu(int containerId, Inventory inventory, ForgeBenchBlockEntity forge) {
        this(containerId, inventory, forge, forge.items());
    }

    private ForgeBenchMenu(int containerId, Inventory inventory, ForgeBenchBlockEntity forge, Container container) {
        super(ModBlocks.FORGE_BENCH_MENU.get(), containerId, forge, container);
        this.forge = forge;
        addSlot(new TabSlot(container, ForgeBenchBlockEntity.ENERGY, 80, 44, TAB_ENERGY));
        int cell = 0;
        for (int y = 0; y < ComponentInput.SIDE; y++) {
            for (int x = 0; x < ComponentInput.SIDE; x++) {
                if (!ComponentInput.isCorner(x, y)) {
                    addSlot(new TabSlot(container, ForgeBenchBlockEntity.GRID + cell++, 34 + x * 18, 18 + y * 18, TAB_COMPONENTS));
                }
            }
        }
        addSlot(new ResultSlot(150, 54));
        finish(inventory);
    }

    public int tab() {
        return tab;
    }

    public void setTab(int tab) {
        this.tab = tab;
    }

    public ItemStack energyItem() {
        return container.getItem(ForgeBenchBlockEntity.ENERGY);
    }

    @Override
    protected EssenceCost currentCost() {
        return tab == TAB_COMPONENTS ? costCache : null;
    }

    @Override
    protected Block block() {
        return ModBlocks.FORGE_BENCH.get();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        switch (id) {
            case BUTTON_TAB_COMPONENTS -> tab = TAB_COMPONENTS;
            case BUTTON_TAB_ENERGY -> tab = TAB_ENERGY;
            case BUTTON_ENERGY -> {
                ItemStack stack = container.getItem(ForgeBenchBlockEntity.ENERGY);
                if (stack.isEmpty()) {
                    return false;
                }
                ItemStack toggled = stack.copy();
                WingedEnergy.toggle(toggled);
                container.setItem(ForgeBenchBlockEntity.ENERGY, toggled);
                if (forge != null) {
                    forge.playEnergy();
                }
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public void slotsChanged(Container changed) {
        super.slotsChanged(changed);
        if (forge != null) {
            updateResult();
        }
    }

    @Override
    public void broadcastChanges() {
        if (forge != null && Math.round(forge.fill() * 100000) != lastFill) {
            updateResult();
        }
        super.broadcastChanges();
    }

    private Optional<RecipeHolder<ComponentRecipe>> componentRecipe() {
        if (forge == null || forge.getLevel() == null) {
            return Optional.empty();
        }
        return forge.getLevel().getRecipeManager().getRecipeFor(ModRecipes.COMPONENT.get(), componentInput(), forge.getLevel());
    }

    private ComponentInput componentInput() {
        List<ItemStack> cells = new ArrayList<>(ComponentInput.SIDE * ComponentInput.SIDE);
        int cell = 0;
        for (int y = 0; y < ComponentInput.SIDE; y++) {
            for (int x = 0; x < ComponentInput.SIDE; x++) {
                cells.add(ComponentInput.isCorner(x, y) ? ItemStack.EMPTY : container.getItem(ForgeBenchBlockEntity.GRID + cell++));
            }
        }
        return new ComponentInput(cells);
    }

    private void updateResult() {
        lastFill = Math.round(forge.fill() * 100000);
        Optional<RecipeHolder<ComponentRecipe>> recipe = componentRecipe();
        costCache = recipe.map(r -> r.value().essence()).orElse(null);
        ItemStack output = recipe.filter(r -> forge.canPay(r.value().essence()))
                .map(r -> r.value().assemble(componentInput(), forge.getLevel().registryAccess()))
                .orElse(ItemStack.EMPTY);
        if (!ItemStack.matches(output, result.getItem(0))) {
            result.setItem(0, output);
        }
    }

    private void craftComponent(Player player) {
        Optional<RecipeHolder<ComponentRecipe>> recipe = componentRecipe();
        if (recipe.isEmpty()) {
            return;
        }
        forge.pay(recipe.get().value().essence());
        for (int cell = 0; cell < ForgeBenchBlockEntity.GRID_CELLS; cell++) {
            int index = ForgeBenchBlockEntity.GRID + cell;
            ItemStack stack = container.getItem(index);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack remainder = stack.getCraftingRemainingItem();
            container.removeItem(index, 1);
            if (!remainder.isEmpty()) {
                if (container.getItem(index).isEmpty()) {
                    container.setItem(index, remainder);
                } else if (!player.getInventory().add(remainder)) {
                    player.drop(remainder, false);
                }
            }
        }
        forge.playComponentCraft();
        updateResult();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == RESULT_SLOT) {
            if (!moveItemStackTo(stack, playerStart(), playerEnd(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, original);
            return original;
        }
        if (index < playerStart()) {
            if (!moveItemStackTo(stack, playerStart(), playerEnd(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (moveToFlask(stack)) {
            slot.setChanged();
        } else if (tab == TAB_ENERGY) {
            if (!moveItemStackTo(stack, ENERGY_SLOT, ENERGY_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, FIRST_GRID_SLOT, RESULT_SLOT, false)) {
            return ItemStack.EMPTY;
        }
        return finishQuickMove(player, slot, stack, original);
    }

    private class TabSlot extends Slot {
        private final int slotTab;

        TabSlot(Container container, int index, int x, int y, int slotTab) {
            super(container, index, x, y);
            this.slotTab = slotTab;
        }

        @Override
        public boolean isActive() {
            return tab == slotTab;
        }
    }

    private class ResultSlot extends Slot {
        ResultSlot(int x, int y) {
            super(result, 0, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean isActive() {
            return tab == TAB_COMPONENTS;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            if (forge != null) {
                craftComponent(player);
            }
            super.onTake(player, stack);
        }
    }
}
