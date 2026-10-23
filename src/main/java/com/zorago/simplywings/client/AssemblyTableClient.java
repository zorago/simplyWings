package com.zorago.simplywings.client;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.block.ModBlocks;
import com.zorago.simplywings.client.gui.AssemblyTableScreen;
import com.zorago.simplywings.client.gui.CloudCompressorScreen;
import com.zorago.simplywings.client.gui.ForgeBenchScreen;
import com.zorago.simplywings.essence.EssenceContainerItem;
import com.zorago.simplywings.essence.EssenceType;
import com.zorago.simplywings.item.ModItems;
import com.zorago.simplywings.item.WingedEnergy;
import com.zorago.simplywings.render.CloudCompressorRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class AssemblyTableClient {
    public static void register(IEventBus modBus, IEventBus gameBus) {
        modBus.addListener(AssemblyTableClient::onScreens);
        modBus.addListener(AssemblyTableClient::onSetup);
        modBus.addListener(AssemblyTableClient::onColors);
        modBus.addListener(AssemblyTableClient::onRenderers);
        gameBus.addListener(AssemblyTableClient::onTooltip);
    }

    private static void onColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, layer) -> {
            EssenceType type = EssenceContainerItem.type(stack);
            return layer == 1 && type != null ? 0xFF000000 | type.liquidColor() : -1;
        }, ModItems.CONTAINERS.stream().map(DeferredItem::get).toArray(ItemLike[]::new));
    }

    private static void onRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlocks.CLOUD_COMPRESSOR_ENTITY.get(), CloudCompressorRenderer::new);
    }

    private static void onScreens(RegisterMenuScreensEvent event) {
        event.register(ModBlocks.ASSEMBLY_TABLE_MENU.get(), AssemblyTableScreen::new);
        event.register(ModBlocks.FORGE_BENCH_MENU.get(), ForgeBenchScreen::new);
        event.register(ModBlocks.CLOUD_COMPRESSOR_MENU.get(), CloudCompressorScreen::new);
    }

    private static void onSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModItems.CONTAINERS.forEach(container -> ItemProperties.register(container.get(), SimplyWings.id("fill"),
                    (stack, level, entity, seed) -> EssenceContainerItem.amount(stack) / (float) EssenceContainerItem.capacity(stack)));
            ModItems.COMPASSES.forEach(compass -> ItemProperties.register(compass.get(), SimplyWings.id("angle"),
                    new CompassItemPropertyFunction((level, stack, entity) -> {
                        LodestoneTracker tracker = stack.get(DataComponents.LODESTONE_TRACKER);
                        return tracker != null ? tracker.target().orElse(null) : null;
                    })));
        });
    }

    private static void onTooltip(ItemTooltipEvent event) {
        if (WingedEnergy.has(event.getItemStack())) {
            event.getToolTip().add(1, Component.translatable("tooltip.simplywings.winged_energy").withStyle(ChatFormatting.AQUA));
        }
    }

    private AssemblyTableClient() {
    }
}
