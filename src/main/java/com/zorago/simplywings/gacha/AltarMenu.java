package com.zorago.simplywings.gacha;

import com.zorago.simplywings.block.ModBlocks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

public class AltarMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public AltarMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
        buf.readBlockPos();
    }

    public AltarMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModBlocks.ALTAR_MENU.get(), containerId);
        this.access = access;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.DRACONIC_ALTAR.get());
    }
}
