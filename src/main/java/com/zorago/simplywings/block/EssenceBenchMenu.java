package com.zorago.simplywings.block;

import com.zorago.simplywings.essence.EssenceContainerItem;
import com.zorago.simplywings.essence.EssenceCost;
import com.zorago.simplywings.essence.EssenceType;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public abstract class EssenceBenchMenu extends AbstractContainerMenu {
    public static final int FLASK_X = 8;
    public static final int FLASK_Y = 92;
    private static final int DATA_TANKS = 0;
    private static final int DATA_COST = DATA_TANKS + EssenceType.COUNT;
    private static final int DATA_HAS_COST = DATA_COST + EssenceType.COUNT;
    private static final int DATA_PROGRESS = DATA_HAS_COST + 1;
    private static final int DATA_COUNT = DATA_PROGRESS + 1;

    protected final Container container;
    protected final EssenceBenchBlockEntity bench;
    private final ContainerLevelAccess access;
    private final ContainerData data;
    private int playerStart;

    protected EssenceBenchMenu(MenuType<?> type, int containerId, EssenceBenchBlockEntity bench, Container container) {
        super(type, containerId);
        this.bench = bench;
        this.container = container;
        this.data = bench == null ? new SimpleContainerData(DATA_COUNT) : new ServerData();
        this.access = bench == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(bench.getLevel(), bench.getBlockPos());
        addSlot(new Slot(container, EssenceBenchBlockEntity.FLASK, FLASK_X, FLASK_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return EssenceContainerItem.isContainer(stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
    }

    protected void finish(Inventory inventory) {
        playerStart = slots.size();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 124 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 182));
        }
        addDataSlots(data);
        if (bench != null) {
            bench.addMenu(this);
            slotsChanged(container);
        }
    }

    protected int playerStart() {
        return playerStart;
    }

    protected int playerEnd() {
        return playerStart + 36;
    }

    protected abstract EssenceCost currentCost();

    protected int serverProgress() {
        return -1;
    }

    protected abstract Block block();

    public int tank(EssenceType type) {
        return data.get(DATA_TANKS + type.ordinal());
    }

    public EssenceCost cost() {
        if (data.get(DATA_HAS_COST) == 0) {
            return null;
        }
        return new EssenceCost(data.get(DATA_COST), data.get(DATA_COST + 1), data.get(DATA_COST + 2), data.get(DATA_COST + 3));
    }

    public boolean affordable() {
        EssenceCost cost = cost();
        if (cost == null) {
            return false;
        }
        for (EssenceType type : EssenceType.values()) {
            if (tank(type) < cost.get(type)) {
                return false;
            }
        }
        return true;
    }

    public int progress() {
        return data.get(DATA_PROGRESS);
    }

    protected boolean moveToFlask(ItemStack stack) {
        return EssenceContainerItem.isContainer(stack) && container.getItem(EssenceBenchBlockEntity.FLASK).isEmpty()
                && moveItemStackTo(stack, 0, 1, false);
    }

    protected ItemStack finishQuickMove(Player player, Slot slot, ItemStack stack, ItemStack original) {
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
        return bench == null || stillValid(access, player, block());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (bench != null) {
            bench.removeMenu(this);
        }
    }

    private final class ServerData implements ContainerData {
        @Override
        public int get(int index) {
            if (index < DATA_COST) {
                return bench.tank(EssenceType.values()[index - DATA_TANKS]);
            }
            if (index < DATA_HAS_COST) {
                EssenceCost cost = currentCost();
                return cost == null ? 0 : cost.get(EssenceType.values()[index - DATA_COST]);
            }
            if (index == DATA_HAS_COST) {
                return currentCost() == null ? 0 : 1;
            }
            return serverProgress();
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    }
}
