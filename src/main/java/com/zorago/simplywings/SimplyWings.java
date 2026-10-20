package com.zorago.simplywings;

import com.mojang.logging.LogUtils;
import com.zorago.simplywings.command.WingsCommand;
import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.flight.ServerFlight;
import com.zorago.simplywings.item.ModItems;
import com.zorago.simplywings.network.ModNetwork;
import com.zorago.simplywings.player.ModAttachments;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(SimplyWings.MOD_ID)
public class SimplyWings {
    public static final String MOD_ID = "simplywings";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SimplyWings(IEventBus modBus, ModContainer container) {
        ModItems.register(modBus);
        ModAttachments.register(modBus);
        modBus.addListener(WingRegistries::onNewRegistry);
        modBus.addListener(ModNetwork::register);
        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        ServerFlight.register(NeoForge.EVENT_BUS);
        NeoForge.EVENT_BUS.addListener(WingsCommand::register);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
