package com.zorago.simplywings.flight;

import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.essence.EssenceContainerItem;
import com.zorago.simplywings.essence.EssenceType;
import com.zorago.simplywings.item.ModItems;
import com.zorago.simplywings.player.FlightMode;
import com.zorago.simplywings.player.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class CloudHarvest {
    private static final int INTERVAL_TICKS = 20;

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % INTERVAL_TICKS != 0) {
            return;
        }
        FlightMode mode = ModAttachments.get(player).mode();
        if (mode == FlightMode.NONE || mode == FlightMode.STABILIZED && !ServerConfig.CLOUD_HOVER_HARVEST.get()) {
            return;
        }
        EssenceType type = zone(player);
        if (type == null) {
            return;
        }
        Inventory inventory = player.getInventory();
        if (!inventory.contains(stack -> stack.is(ModItems.CLOUD_ABSORBER.get()))) {
            return;
        }
        fill(player, inventory, type, ServerConfig.CLOUD_ESSENCE_PER_SECOND.get());
    }

    public static EssenceType zone(ServerPlayer player) {
        Level level = player.level();
        if (level.dimension() == Level.OVERWORLD) {
            double y = player.getY();
            return y >= ServerConfig.CLOUD_MIN_Y.get() && y <= ServerConfig.CLOUD_MAX_Y.get() ? EssenceType.CLOUD : null;
        }
        if (level.dimension() == Level.NETHER) {
            return aboveLava(level, player.blockPosition(), ServerConfig.STEAM_HEIGHT.get()) ? EssenceType.STEAM : null;
        }
        if (level.dimension() == Level.END) {
            double distance = ServerConfig.ETHEREAL_MIN_DISTANCE.get();
            if (player.getX() * player.getX() + player.getZ() * player.getZ() < distance * distance) {
                return null;
            }
            return overVoid(level, player.blockPosition()) ? EssenceType.ETHEREAL : null;
        }
        return null;
    }

    private static boolean aboveLava(Level level, BlockPos feet, int height) {
        BlockPos.MutableBlockPos pos = feet.mutable();
        for (int i = 0; i <= height && pos.getY() > level.getMinBuildHeight(); i++) {
            BlockState state = level.getBlockState(pos);
            if (state.getFluidState().is(FluidTags.LAVA)) {
                return true;
            }
            if (!state.isAir()) {
                return false;
            }
            pos.move(0, -1, 0);
        }
        return false;
    }

    private static boolean overVoid(Level level, BlockPos feet) {
        BlockPos.MutableBlockPos pos = feet.mutable();
        while (pos.getY() >= level.getMinBuildHeight()) {
            if (!level.getBlockState(pos).isAir()) {
                return false;
            }
            pos.move(0, -1, 0);
        }
        return true;
    }

    private static void fill(ServerPlayer player, Inventory inventory, EssenceType type, int amount) {
        if (amount <= 0) {
            return;
        }
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (EssenceContainerItem.type(stack) == type && EssenceContainerItem.fill(stack, type, amount) > 0) {
                return;
            }
        }
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!EssenceContainerItem.isContainer(stack) || EssenceContainerItem.type(stack) != null) {
                continue;
            }
            if (stack.getCount() == 1) {
                EssenceContainerItem.fill(stack, type, amount);
            } else {
                ItemStack filled = stack.split(1);
                EssenceContainerItem.fill(filled, type, amount);
                if (!inventory.add(filled)) {
                    player.drop(filled, false);
                }
            }
            return;
        }
    }

    private CloudHarvest() {
    }
}
