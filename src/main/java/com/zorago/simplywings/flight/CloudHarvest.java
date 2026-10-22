package com.zorago.simplywings.flight;

import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.item.CloudFlaskItem;
import com.zorago.simplywings.item.ModItems;
import com.zorago.simplywings.player.FlightMode;
import com.zorago.simplywings.player.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class CloudHarvest {
    private static final int INTERVAL_TICKS = 20;

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % INTERVAL_TICKS != 0) {
            return;
        }
        if (player.level().dimension() != Level.OVERWORLD) {
            return;
        }
        double y = player.getY();
        if (y < ServerConfig.CLOUD_MIN_Y.get() || y > ServerConfig.CLOUD_MAX_Y.get()) {
            return;
        }
        FlightMode mode = ModAttachments.get(player).mode();
        if (mode == FlightMode.NONE || mode == FlightMode.STABILIZED && !ServerConfig.CLOUD_HOVER_HARVEST.get()) {
            return;
        }
        Inventory inventory = player.getInventory();
        if (!inventory.contains(stack -> stack.is(ModItems.CLOUD_ABSORBER.get()))) {
            return;
        }
        fill(player, inventory, ServerConfig.CLOUD_ESSENCE_PER_SECOND.get());
    }

    private static void fill(ServerPlayer player, Inventory inventory, int amount) {
        if (amount <= 0) {
            return;
        }
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            int essence = stack.is(ModItems.CLOUD_FLASK.get()) ? CloudFlaskItem.essence(stack) : 0;
            if (essence > 0 && essence < CloudFlaskItem.CAPACITY) {
                CloudFlaskItem.setEssence(stack, essence + amount);
                return;
            }
        }
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.is(ModItems.CLOUD_FLASK.get()) || CloudFlaskItem.essence(stack) > 0) {
                continue;
            }
            if (stack.getCount() == 1) {
                CloudFlaskItem.setEssence(stack, amount);
            } else {
                stack.shrink(1);
                ItemStack filled = CloudFlaskItem.filled(ModItems.CLOUD_FLASK.get(), amount);
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
