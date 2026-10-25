package com.zorago.simplywings.gacha;

import com.zorago.simplywings.block.ModBlocks;
import com.zorago.simplywings.item.ModItems;
import com.zorago.simplywings.player.ModAttachments;
import java.util.Locale;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public class FountainMenu extends AbstractContainerMenu {
    public static final int SIZE = 9;
    public static final int BUTTON_OFFER = 0;
    public static final int SCALE = 1000;

    private final ContainerLevelAccess access;
    private final Player player;
    private final ContainerData data = new SimpleContainerData(3);
    private final SimpleContainer offering = new SimpleContainer(SIZE);

    public FountainMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
        buf.readBlockPos();
    }

    public FountainMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModBlocks.FOUNTAIN_MENU.get(), containerId);
        this.access = access;
        this.player = inventory.player;
        offering.addListener(this::slotsChanged);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(offering, col + row * 3, 82 + col * 18, 20 + row * 18));
            }
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
        refresh();
    }

    public double offered() {
        return ((data.get(0) & 0xFFFF) | (data.get(1) & 0xFFFF) << 16) / (double) SCALE;
    }

    public double reserve() {
        return (data.get(2) & 0xFFFF) / (double) SCALE;
    }

    private void refresh() {
        if (player instanceof ServerPlayer) {
            double total = 0;
            for (int i = 0; i < SIZE; i++) {
                total += OfferingValues.value(offering.getItem(i));
            }
            int offered = (int) Math.min(Integer.MAX_VALUE, Math.round(total * SCALE));
            data.set(0, offered & 0xFFFF);
            data.set(1, offered >>> 16);
            data.set(2, (int) Math.round(ModAttachments.gacha(player).reserve() * SCALE));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        refresh();
    }

    @Override
    public boolean clickMenuButton(Player clicker, int id) {
        if (id != BUTTON_OFFER || !(clicker instanceof ServerPlayer server)) {
            return false;
        }
        GachaData gacha = ModAttachments.gacha(server);
        double total = gacha.reserve();
        boolean any = false;
        for (int i = 0; i < SIZE; i++) {
            ItemStack stack = offering.getItem(i);
            double value = OfferingValues.value(stack);
            if (value > 0) {
                total += value;
                offering.setItem(i, ItemStack.EMPTY);
                any = true;
            }
        }
        if (!any) {
            return false;
        }
        int feathers = (int) Math.floor(total + 1e-9);
        gacha.setReserve(Math.max(0, total - feathers));
        int given = feathers;
        while (feathers > 0) {
            int n = Math.min(64, feathers);
            ItemStack out = new ItemStack(ModItems.SUMMONING_FEATHER.get(), n);
            if (!server.getInventory().add(out)) {
                server.drop(out, false);
            }
            feathers -= n;
        }
        server.level().playSound(null, server.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1f, 1.2f);
        if (given > 0) {
            server.level().playSound(null, server.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.6f, 1.4f);
        }
        server.displayClientMessage(Component.translatable("message.simplywings.fountain_offered", given,
                String.format(Locale.ROOT, "%.2f", gacha.reserve())), true);
        refresh();
        return true;
    }

    @Override
    public void removed(Player who) {
        super.removed(who);
        if (!who.level().isClientSide) {
            clearContainer(who, offering);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player who, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < SIZE) {
            if (!moveItemStackTo(stack, SIZE, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 0, SIZE, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    @Override
    public boolean stillValid(Player who) {
        return stillValid(access, who, ModBlocks.OFFERING_FOUNTAIN.get());
    }
}
