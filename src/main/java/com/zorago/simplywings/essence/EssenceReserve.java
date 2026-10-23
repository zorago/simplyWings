package com.zorago.simplywings.essence;

import com.zorago.simplywings.config.ServerConfig;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class EssenceReserve {
    public static final int PACKET = 20;

    public static int available(Player player) {
        int total = 0;
        for (ItemStack stack : containers(player)) {
            total += EssenceContainerItem.amount(stack);
        }
        return total;
    }

    public static float need(float beatCost) {
        return Math.max(ServerConfig.AIR_JUMP_COST.get().floatValue(), beatCost);
    }

    public static boolean wants(Player player, float stamina, float max, float need) {
        return !player.onGround() && stamina < Math.min(max, need);
    }

    public static int packet(float stamina, float max, float need) {
        return Math.max(0, Math.min(Math.max(PACKET, (int) Math.ceil(need)), (int) Math.ceil(max - stamina)));
    }

    public static int drain(Player player, int amount) {
        int taken = 0;
        for (ItemStack stack : containers(player)) {
            if (taken >= amount) {
                break;
            }
            taken += EssenceContainerItem.drain(stack, amount - taken);
        }
        return taken;
    }

    private static List<ItemStack> containers(Player player) {
        Inventory inventory = player.getInventory();
        List<ItemStack> found = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (EssenceContainerItem.type(stack) == EssenceType.INTENSIFIED) {
                found.add(stack);
            }
        }
        found.sort(Comparator.comparingInt(EssenceContainerItem::capacity));
        return found;
    }

    private EssenceReserve() {
    }
}
