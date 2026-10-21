package com.zorago.simplywings.block;

import com.zorago.simplywings.block.recipe.ComponentInput;
import com.zorago.simplywings.block.recipe.ComponentRecipe;
import com.zorago.simplywings.block.recipe.ModRecipes;
import com.zorago.simplywings.item.CloudFlaskItem;
import com.zorago.simplywings.item.WingedEnergy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class AssemblyTableMenu extends AbstractContainerMenu {
    public static final int TAB_ASSEMBLY = 0;
    public static final int TAB_COMPONENTS = 1;
    public static final int TAB_ENERGY = 2;
    public static final int TABS = 3;
    public static final int BUTTON_TAB_ASSEMBLY = 0;
    public static final int BUTTON_TAB_COMPONENTS = 1;
    public static final int BUTTON_ASSEMBLE = 2;
    public static final int BUTTON_ENERGY = 3;
    public static final int BUTTON_TAB_ENERGY = 4;
    public static final int DATA_TANK = 0;
    public static final int DATA_PROGRESS = 1;
    public static final int DATA_COMPONENT_COST = 2;
    public static final int DATA_ASSEMBLY_COST = 3;
    public static final int RESULT_SLOT = AssemblyTableBlockEntity.SIZE;
    private static final int PLAYER_START = RESULT_SLOT + 1;
    private static final int PLAYER_END = PLAYER_START + 36;

    private final Container container;
    private final AssemblyTableBlockEntity table;
    private final ContainerLevelAccess access;
    private final ContainerData data;
    private final ResultContainer result = new ResultContainer();
    private int tab = TAB_ASSEMBLY;
    private int lastTank = -1;
    private int componentCostCache = -1;
    private int assemblyCostCache = -1;

    public AssemblyTableMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, null, new SimpleContainer(AssemblyTableBlockEntity.SIZE));
        buf.readBlockPos();
    }

    public AssemblyTableMenu(int containerId, Inventory inventory, AssemblyTableBlockEntity table) {
        this(containerId, inventory, table, table.items());
    }

    private AssemblyTableMenu(int containerId, Inventory inventory, AssemblyTableBlockEntity table, Container container) {
        super(ModBlocks.ASSEMBLY_TABLE_MENU.get(), containerId);
        this.table = table;
        this.container = container;
        this.data = table == null ? new SimpleContainerData(4) : new ServerData();
        this.access = table == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(table.getLevel(), table.getBlockPos());

        addSlot(new Slot(container, AssemblyTableBlockEntity.FLASK, 8, 92) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof CloudFlaskItem;
            }
        });
        addSlot(new TabSlot(container, AssemblyTableBlockEntity.HARNESS, 80, 20, TAB_ASSEMBLY));
        addSlot(new TabSlot(container, AssemblyTableBlockEntity.CORE, 80, 44, TAB_ASSEMBLY));
        addSlot(new TabSlot(container, AssemblyTableBlockEntity.LEFT_WING, 56, 44, TAB_ASSEMBLY));
        addSlot(new TabSlot(container, AssemblyTableBlockEntity.RIGHT_WING, 104, 44, TAB_ASSEMBLY));
        addSlot(new TabSlot(container, AssemblyTableBlockEntity.OUTPUT, 150, 44, TAB_ASSEMBLY) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addSlot(new TabSlot(container, AssemblyTableBlockEntity.ENERGY, 80, 44, TAB_ENERGY));
        int cell = 0;
        for (int y = 0; y < ComponentInput.SIDE; y++) {
            for (int x = 0; x < ComponentInput.SIDE; x++) {
                if (!ComponentInput.isCorner(x, y)) {
                    addSlot(new TabSlot(container, AssemblyTableBlockEntity.GRID + cell++, 34 + x * 18, 18 + y * 18, TAB_COMPONENTS));
                }
            }
        }
        addSlot(new ResultSlot(150, 54));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 124 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 182));
        }
        addDataSlots(this.data);
        if (table != null) {
            table.addMenu(this);
            slotsChanged(container);
        }
    }

    public int tab() {
        return tab;
    }

    public void setTab(int tab) {
        this.tab = tab;
    }

    public int tank() {
        return data.get(DATA_TANK);
    }

    public int progress() {
        return data.get(DATA_PROGRESS);
    }

    public int componentCost() {
        return data.get(DATA_COMPONENT_COST);
    }

    public int assemblyCost() {
        return data.get(DATA_ASSEMBLY_COST);
    }

    public boolean outputEmpty() {
        return container.getItem(AssemblyTableBlockEntity.OUTPUT).isEmpty();
    }

    public ItemStack energyItem() {
        return container.getItem(AssemblyTableBlockEntity.ENERGY);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        switch (id) {
            case BUTTON_TAB_ASSEMBLY -> tab = TAB_ASSEMBLY;
            case BUTTON_TAB_COMPONENTS -> tab = TAB_COMPONENTS;
            case BUTTON_TAB_ENERGY -> tab = TAB_ENERGY;
            case BUTTON_ASSEMBLE -> {
                return table != null && table.startAssembly();
            }
            case BUTTON_ENERGY -> {
                ItemStack stack = container.getItem(AssemblyTableBlockEntity.ENERGY);
                if (stack.isEmpty()) {
                    return false;
                }
                ItemStack toggled = stack.copy();
                WingedEnergy.toggle(toggled);
                container.setItem(AssemblyTableBlockEntity.ENERGY, toggled);
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
        if (table != null) {
            updateResult();
        }
    }

    @Override
    public void broadcastChanges() {
        if (table != null && table.tank() != lastTank) {
            updateResult();
        }
        super.broadcastChanges();
    }

    private Optional<RecipeHolder<ComponentRecipe>> componentRecipe() {
        if (table == null || table.getLevel() == null) {
            return Optional.empty();
        }
        return table.getLevel().getRecipeManager().getRecipeFor(ModRecipes.COMPONENT.get(), componentInput(), table.getLevel());
    }

    private ComponentInput componentInput() {
        List<ItemStack> cells = new ArrayList<>(ComponentInput.SIDE * ComponentInput.SIDE);
        int cell = 0;
        for (int y = 0; y < ComponentInput.SIDE; y++) {
            for (int x = 0; x < ComponentInput.SIDE; x++) {
                cells.add(ComponentInput.isCorner(x, y) ? ItemStack.EMPTY : container.getItem(AssemblyTableBlockEntity.GRID + cell++));
            }
        }
        return new ComponentInput(cells);
    }

    private void updateResult() {
        lastTank = table.tank();
        assemblyCostCache = table.assemblyRecipe().map(r -> r.value().essence()).orElse(-1);
        Optional<RecipeHolder<ComponentRecipe>> recipe = componentRecipe();
        componentCostCache = recipe.map(r -> r.value().essence()).orElse(-1);
        ItemStack output = recipe.filter(r -> table.tank() >= r.value().essence())
                .map(r -> r.value().assemble(componentInput(), table.getLevel().registryAccess()))
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
        table.drain(recipe.get().value().essence());
        for (int cell = 0; cell < AssemblyTableBlockEntity.GRID_CELLS; cell++) {
            ItemStack stack = container.getItem(AssemblyTableBlockEntity.GRID + cell);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack remainder = stack.getCraftingRemainingItem();
            container.removeItem(AssemblyTableBlockEntity.GRID + cell, 1);
            if (!remainder.isEmpty()) {
                if (container.getItem(AssemblyTableBlockEntity.GRID + cell).isEmpty()) {
                    container.setItem(AssemblyTableBlockEntity.GRID + cell, remainder);
                } else if (!player.getInventory().add(remainder)) {
                    player.drop(remainder, false);
                }
            }
        }
        table.playComponentCraft();
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
            if (!moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, original);
            return original;
        }
        if (index < PLAYER_START) {
            if (!moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.getItem() instanceof CloudFlaskItem && moveItemStackTo(stack, 0, 1, false)) {
            slot.setChanged();
        } else if (tab == TAB_ASSEMBLY) {
            if (!moveItemStackTo(stack, 1, 5, false)) {
                return ItemStack.EMPTY;
            }
        } else if (tab == TAB_ENERGY) {
            if (!moveItemStackTo(stack, AssemblyTableBlockEntity.ENERGY, AssemblyTableBlockEntity.ENERGY + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, AssemblyTableBlockEntity.GRID, AssemblyTableBlockEntity.SIZE, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return table == null || stillValid(access, player, ModBlocks.ASSEMBLY_TABLE.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (table != null) {
            table.removeMenu(this);
        }
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
            if (table != null) {
                craftComponent(player);
            }
            super.onTake(player, stack);
        }
    }

    private final class ServerData implements ContainerData {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_TANK -> table.tank();
                case DATA_PROGRESS -> table.assemblyTicks();
                case DATA_COMPONENT_COST -> componentCostCache;
                case DATA_ASSEMBLY_COST -> assemblyCostCache;
                default -> -1;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
