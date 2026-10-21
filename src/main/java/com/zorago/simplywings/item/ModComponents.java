package com.zorago.simplywings.item;

import com.mojang.serialization.Codec;
import com.zorago.simplywings.SimplyWings;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModComponents {
    private static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SimplyWings.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CLOUD_ESSENCE =
            COMPONENTS.registerComponentType("cloud_essence", builder -> builder
                    .persistent(ExtraCodecs.NON_NEGATIVE_INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));
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
