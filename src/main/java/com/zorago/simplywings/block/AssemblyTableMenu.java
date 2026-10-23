package com.zorago.simplywings.block;

import com.zorago.simplywings.essence.EssenceCost;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class AssemblyTableMenu extends EssenceBenchMenu {
    public static final int BUTTON_ASSEMBLE = 0;
    public static final int FIRST_INPUT_SLOT = 1;

    private final AssemblyTableBlockEntity table;
    private EssenceCost costCache;

    public AssemblyTableMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, null, new SimpleContainer(AssemblyTableBlockEntity.SIZE));
        buf.readBlockPos();
    }

    public AssemblyTableMenu(int containerId, Inventory inventory, AssemblyTableBlockEntity table) {
        this(containerId, inventory, table, table.items());
    }

    private AssemblyTableMenu(int containerId, Inventory inventory, AssemblyTableBlockEntity table, Container container) {
        super(ModBlocks.ASSEMBLY_TABLE_MENU.get(), containerId, table, container);
        this.table = table;
        addSlot(new Slot(container, AssemblyTableBlockEntity.HARNESS, 80, 20));
        addSlot(new Slot(container, AssemblyTableBlockEntity.CORE, 80, 44));
        addSlot(new Slot(container, AssemblyTableBlockEntity.LEFT_WING, 56, 44));
        addSlot(new Slot(container, AssemblyTableBlockEntity.RIGHT_WING, 104, 44));
        addSlot(new Slot(container, AssemblyTableBlockEntity.OUTPUT, 150, 44) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        finish(inventory);
    }

    public boolean outputEmpty() {
        return container.getItem(AssemblyTableBlockEntity.OUTPUT).isEmpty();
    }

    @Override
    public void slotsChanged(Container changed) {
        super.slotsChanged(changed);
        if (table != null) {
            costCache = table.assemblyRecipe().map(r -> r.value().essence()).orElse(null);
        }
    }

    @Override
    protected EssenceCost currentCost() {
        return costCache;
    }

    @Override
    protected int serverProgress() {
        return table.assemblyTicks();
    }

    @Override
    protected Block block() {
        return ModBlocks.ASSEMBLY_TABLE.get();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        return id == BUTTON_ASSEMBLE && table != null && table.startAssembly();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < playerStart()) {
            if (!moveItemStackTo(stack, playerStart(), playerEnd(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveToFlask(stack) && !moveItemStackTo(stack, FIRST_INPUT_SLOT, FIRST_INPUT_SLOT + 4, false)) {
            return ItemStack.EMPTY;
        }
        return finishQuickMove(player, slot, stack, original);
    }
}
