package com.zorago.simplywings.client;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.block.ModBlocks;
import com.zorago.simplywings.client.gui.AssemblyTableScreen;
import com.zorago.simplywings.item.CloudFlaskItem;
import com.zorago.simplywings.item.ModItems;
import com.zorago.simplywings.item.WingedEnergy;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class AssemblyTableClient {
    public static void register(IEventBus modBus, IEventBus gameBus) {
        modBus.addListener(AssemblyTableClient::onScreens);
        modBus.addListener(AssemblyTableClient::onSetup);
        gameBus.addListener(AssemblyTableClient::onTooltip);
    }

    private static void onScreens(RegisterMenuScreensEvent event) {
        event.register(ModBlocks.ASSEMBLY_TABLE_MENU.get(), AssemblyTableScreen::new);
    }

    private static void onSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(ModItems.CLOUD_FLASK.get(), SimplyWings.id("fill"),
                (stack, level, entity, seed) -> CloudFlaskItem.essence(stack) / (float) CloudFlaskItem.CAPACITY));
    }

    private static void onTooltip(ItemTooltipEvent event) {
        if (WingedEnergy.has(event.getItemStack())) {
            event.getToolTip().add(1, Component.translatable("tooltip.simplywings.winged_energy").withStyle(ChatFormatting.AQUA));
        }
    }

    private AssemblyTableClient() {
    }
}
