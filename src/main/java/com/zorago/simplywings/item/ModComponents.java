package com.zorago.simplywings.item;

import com.mojang.serialization.Codec;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.essence.EssenceContent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModComponents {
    private static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SimplyWings.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EssenceContent>> ESSENCE =
            COMPONENTS.registerComponentType("essence", builder -> builder
                    .persistent(EssenceContent.CODEC)
                    .networkSynchronized(EssenceContent.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> WINGED_ENERGY =
            COMPONENTS.registerComponentType("winged_energy", builder -> builder
                    .persistent(Codec.unit(Unit.INSTANCE))
                    .networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

    public static void register(IEventBus modBus) {
        COMPONENTS.register(modBus);
    }

    private ModComponents() {
    }
}
