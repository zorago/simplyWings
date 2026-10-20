package com.zorago.simplywings.item;

import com.zorago.simplywings.flight.ServerFlight;
import com.zorago.simplywings.player.ModAttachments;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class WingItem extends Item {
    private final ResourceLocation wing;

    public WingItem(Properties properties, ResourceLocation wing) {
        super(properties);
        this.wing = wing;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            if (ModAttachments.get(serverPlayer).collection().contains(wing)) {
                ServerFlight.equip(serverPlayer, Optional.of(wing));
                serverPlayer.displayClientMessage(Component.translatable("message.simplywings.already_unlocked"), true);
                return InteractionResultHolder.fail(stack);
            }
            ServerFlight.unlockAndEquip(serverPlayer, wing);
            serverPlayer.displayClientMessage(Component.translatable("message.simplywings.unlocked",
                    Component.translatable(translationKey(wing))), true);
            stack.consume(1, player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.simplywings.wing.tooltip").withStyle(ChatFormatting.GRAY));
    }

    public static String translationKey(ResourceLocation wing) {
        return "wing." + wing.getNamespace() + "." + wing.getPath();
    }
}
