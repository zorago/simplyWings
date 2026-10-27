package com.zorago.simplywings.client;

import com.zorago.simplywings.SimplyWings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public final class WingSounds {
    private static final Map<String, SoundEvent> CACHE = new HashMap<>();
    private static final Map<String, List<SoundEvent>> LAYERS = new HashMap<>();

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
        layer(player, pair, volume);
    }

    public static void layer(Player player, Optional<ResourceLocation> pair, float volume) {
        for (SoundEvent sound : layers(pair)) {
            player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, volume / 0.7f, 1f, false);
        }
    }

    private static List<SoundEvent> layers(Optional<ResourceLocation> pair) {
        String name = pair.map(ResourceLocation::getPath).orElse("common");
        return LAYERS.computeIfAbsent(name, k -> {
            List<SoundEvent> out = new ArrayList<>();
            for (int i = 1; ; i++) {
                ResourceLocation id = SimplyWings.id("wing." + k + ".layer" + (i == 1 ? "" : "_" + i));
                if (Minecraft.getInstance().getSoundManager().getSoundEvent(id) == null) {
                    return out;
                }
                out.add(SoundEvent.createVariableRangeEvent(id));
            }
        });
    }

    public static void clear() {
        CACHE.clear();
        LAYERS.clear();
    }

    private WingSounds() {
    }
}
