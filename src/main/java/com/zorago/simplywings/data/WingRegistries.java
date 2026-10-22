package com.zorago.simplywings.data;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.loot.LootSource;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class WingRegistries {
    public static final ResourceKey<Registry<WingDefinition>> WING = ResourceKey.createRegistryKey(SimplyWings.id("wing"));

    public static void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(WING, WingDefinition.CODEC, WingDefinition.CODEC);
        event.dataPackRegistry(LootSource.KEY, LootSource.CODEC, LootSource.NETWORK_CODEC);
    }

    public static Optional<WingDefinition> get(RegistryAccess access, ResourceLocation id) {
        return access.registry(WING).flatMap(r -> r.getOptional(id));
    }

    private WingRegistries() {
    }
}
