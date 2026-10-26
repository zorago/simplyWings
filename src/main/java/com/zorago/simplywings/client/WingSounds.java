package com.zorago.simplywings.client;

import com.zorago.simplywings.SimplyWings;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public final class WingSounds {
    private static final Map<String, SoundEvent> CACHE = new HashMap<>();

    public static SoundEvent flap(Optional<ResourceLocation> pair, boolean strong) {
        String kind = strong ? "flap_strong" : "flap";
        String key = pair.map(ResourceLocation::getPath).orElse("common") + "." + kind;
        return CACHE.computeIfAbsent(key, k -> {
            ResourceLocation id = SimplyWings.id("wing." + k);
            if (Minecraft.getInstance().getSoundManager().getSoundEvent(id) == null) {
                id = SimplyWings.id("wing.common." + kind);
            }
            return SoundEvent.createVariableRangeEvent(id);
        });
    }

    public static void play(Player player, Optional<ResourceLocation> pair, boolean strong, float volume, float pitch) {
        player.level().playLocalSound(player.getX(), player.getY() + 1.2, player.getZ(), flap(pair, strong), SoundSource.PLAYERS,
                volume, pitch * (0.94f + player.getRandom().nextFloat() * 0.12f), false);
    }

    public static void clear() {
        CACHE.clear();
    }

    private WingSounds() {
    }
}
