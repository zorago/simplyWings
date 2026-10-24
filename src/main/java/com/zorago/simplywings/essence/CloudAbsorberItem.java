package com.zorago.simplywings.essence;

import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.item.ModItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class CloudAbsorberItem extends Item {
    private final int tier;

    public CloudAbsorberItem(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    public int tier() {
        return tier;
    }

    public static double fillSeconds(int tier) {
        return ServerConfig.ABSORBER_BASE_SECONDS.get() * Math.pow(2, tier - 1);
    }

    public static double perSecond(int tier) {
        int capacity = ModItems.CONTAINERS.get(tier - 1).get().capacity();
        return capacity / fillSeconds(tier);
    }

    public static int best(Inventory inventory) {
        int best = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).getItem() instanceof CloudAbsorberItem absorber) {
                best = Math.max(best, absorber.tier);
            }
        }
        return best;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String rate = String.format(java.util.Locale.ROOT, "%.1f", perSecond(tier)).replace(".0", "");
        tooltip.add(Component.translatable("tooltip.simplywings.absorber", rate).withStyle(ChatFormatting.AQUA));
    }
}
