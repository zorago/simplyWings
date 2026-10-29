package com.zorago.simplywings.client;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.WingStyle;
import com.zorago.simplywings.client.gui.AltarScreen;
import com.zorago.simplywings.client.show.ClientShows;
import com.zorago.simplywings.data.WingDefinition;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.network.Payloads;
import com.zorago.simplywings.player.FlightMode;
import com.zorago.simplywings.sound.ModSounds;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.player.Player;

public final class ClientNet {
    public static void onSync(Payloads.SyncWings payload) {
        boolean unlocked = ClientWings.synced() && !ClientWings.collection().containsAll(payload.collection());
        ClientWings.apply(payload.collection(), payload.equipped(), payload.appearance(), payload.stamina(),
                payload.rescueJumps());
        Minecraft mc = Minecraft.getInstance();
        if (unlocked && !ClientShows.summonerShowRunning() && !(mc.screen instanceof AltarScreen)) {
            mc.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.UI_UNLOCK.get(), 1f, 1f));
        }
    }

    public static void onGachaState(Payloads.GachaState payload) {
        ClientGacha.onState(payload);
    }

    public static void onGachaResults(Payloads.GachaResults payload) {
        ClientGacha.onResults(payload);
    }

    public static void onCardSound(Payloads.CardSoundEvent payload) {
        com.zorago.simplywings.client.show.ShowSounds.remoteCard(payload);
    }

    public static void onSummonShow(Payloads.SummonShow payload) {
        com.zorago.simplywings.client.show.ClientShows.start(payload);
    }

    public static void onStamina(Payloads.Stamina payload) {
        ClientWings.setStamina(payload.stamina());
        ClientWings.setRescueJumps(payload.rescueJumps());
    }

    public static void onFlightState(Payloads.FlightState payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && payload.entityId() == mc.player.getId()) {
            ClientFlight.forceMode(FlightMode.byId(payload.mode()));
            return;
        }
        WingVisuals.update(payload.entityId(), payload.equipped(), payload.appearance(), FlightMode.byId(payload.mode()),
                payload.flags());
    }

    public static void onFlapEvent(Payloads.FlapEvent payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        WingVisuals.Visual visual = mc.level.getEntity(payload.entityId()) instanceof Player p ? WingVisuals.of(p) : null;
        if (visual == null) {
            return;
        }
        Optional<WingDefinition> definition = visual.equipped
                .flatMap(id -> WingRegistries.get(mc.level.registryAccess(), id));
        float rate = definition.map(WingDefinition::flapRate).orElse(2f);
        Optional<WingDefinition> look = visual.look().flatMap(id -> WingRegistries.get(mc.level.registryAccess(), id));
        float lookRate = look.map(WingDefinition::flapRate).orElse(rate);
        WingStyle style = WingStyle.of(look.map(WingDefinition::model).orElse(SimplyWings.id("basic")));
        boolean transmog = !visual.look().equals(visual.equipped);
        if (visual.mode == FlightMode.TRAVEL && transmog) {
            visual.animator.beat(lookRate, Math.round(20f / Math.max(0.1f, rate)) + 2);
        } else if (visual.mode == FlightMode.TRAVEL) {
            visual.animator.strike(rate, style.strikeAt());
        } else {
            visual.animator.flap(lookRate * 1.5f);
        }
        if (mc.level.getEntity(payload.entityId()) instanceof Player target) {
            WingEffects.strike(target, style, visual.mode);
            if (!target.isInvisible()) {
                WingSounds.play(target, visual.look(), false, 0.7f, 1f);
            }
        }
    }

    public static void onTakeoffEvent(Payloads.TakeoffEvent payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.level.getEntity(payload.entityId()) instanceof Player player) {
            WingVisuals.Visual visual = WingVisuals.of(player);
            if (visual != null) {
                visual.animator.remoteCharge(payload.charging());
            }
        }
    }

    public static void onFuryEvent(Payloads.FuryEvent payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.level.getEntity(payload.entityId()) instanceof Player player) {
            FuryEffects.onEvent(player, payload.kind());
        }
    }

    private ClientNet() {
    }
}
