package com.zorago.simplywings.loot;

import com.mojang.serialization.MapCodec;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.gacha.SummoningLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModLoot {
    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SimplyWings.MOD_ID);

    static {
        MODIFIERS.register("winged_drops", () -> WingedLootModifier.CODEC);
        MODIFIERS.register("summoning_feathers", () -> SummoningLootModifier.CODEC);
    }

    public static void register(IEventBus modBus) {
        MODIFIERS.register(modBus);
    }

    private ModLoot() {
    }
}
