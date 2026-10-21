package com.zorago.simplywings.client;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.WingStyle;
import com.zorago.simplywings.data.WingDefinition;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.network.Payloads;
import com.zorago.simplywings.player.FlightMode;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public final class ClientNet {
    public static void onSync(Payloads.SyncWings payload) {
        ClientWings.apply(payload.collection(), payload.equipped(), payload.appearance(), payload.stamina(),
                payload.rescueJumps());
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
        WingStyle style = WingStyle.of(visual.look().flatMap(id -> WingRegistries.get(mc.level.registryAccess(), id))
                .map(WingDefinition::model).orElse(SimplyWings.id("basic")));
        if (visual.mode == FlightMode.TRAVEL) {
            visual.animator.strike(rate, style.strikeAt());
        } else {
            visual.animator.flap(rate * 1.5f);
        }
        if (mc.level.getEntity(payload.entityId()) instanceof Player target) {
            WingEffects.strike(target, style, visual.mode);
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
