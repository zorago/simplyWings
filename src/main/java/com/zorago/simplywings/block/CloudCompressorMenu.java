package com.zorago.simplywings.block;

import com.zorago.simplywings.essence.EssenceContainerItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CloudCompressorMenu extends AbstractContainerMenu {
    public static final int[][] SLOT_POS = {{26, 17}, {26, 39}, {26, 61}, {134, 39}};
    private static final int PLAYER_START = CloudCompressorBlockEntity.SIZE;
    private static final int PLAYER_END = PLAYER_START + 36;

    private final Container container;
    private final CloudCompressorBlockEntity compressor;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    public CloudCompressorMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, null, new SimpleContainer(CloudCompressorBlockEntity.SIZE));
        buf.readBlockPos();
    }

    public CloudCompressorMenu(int containerId, Inventory inventory, CloudCompressorBlockEntity compressor) {
        this(containerId, inventory, compressor, compressor.items());
    }

    private CloudCompressorMenu(int containerId, Inventory inventory, CloudCompressorBlockEntity compressor, Container container) {
        super(ModBlocks.CLOUD_COMPRESSOR_MENU.get(), containerId);
        this.compressor = compressor;
        this.container = container;
        this.access = compressor == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(compressor.getLevel(), compressor.getBlockPos());
        this.data = compressor == null ? new SimpleContainerData(2) : new ContainerData() {
            @Override
            public int get(int index) {
                return index == 0 ? compressor.progress() : compressor.running() ? 1 : 0;
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
        for (int slot = 0; slot < CloudCompressorBlockEntity.SIZE; slot++) {
            int index = slot;
            addSlot(new Slot(container, slot, SLOT_POS[slot][0], SLOT_POS[slot][1]) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return CloudCompressorBlockEntity.accepts(index, stack);
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            });
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 98 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 156));
        }
        addDataSlots(data);
        if (compressor != null) {
            compressor.addMenu(this);
        }
    }

    public int progress() {
        return data.get(0);
    }

    public boolean running() {
        return data.get(1) != 0;
    }

    public ItemStack slotItem(int slot) {
        return container.getItem(slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < PLAYER_START) {
            if (!moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!EssenceContainerItem.isContainer(stack)) {
                return ItemStack.EMPTY;
            }
            boolean moved = false;
            for (int target = 0; target < CloudCompressorBlockEntity.SIZE && !moved; target++) {
                if (CloudCompressorBlockEntity.accepts(target, stack) && !slots.get(target).hasItem()) {
                    moved = moveItemStackTo(stack, target, target + 1, false);
                }
            }
            if (!moved) {
                return ItemStack.EMPTY;
            }
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
        return compressor == null || access.evaluate((level, pos) -> level.getBlockEntity(pos) == compressor
                && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 2.5, pos.getZ() + 0.5) <= 100.0, true);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (compressor != null) {
            compressor.removeMenu(this);
        }
    }
}
