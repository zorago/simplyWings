package com.zorago.simplywings.client;

import com.zorago.simplywings.data.WingDefinition;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.network.Payloads;
import com.zorago.simplywings.player.FlightMode;
import net.minecraft.client.Minecraft;

public final class ClientNet {
    public static void onSync(Payloads.SyncWings payload) {
        ClientWings.apply(payload.collection(), payload.equipped(), payload.stamina(), payload.rescueJumps());
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
        WingVisuals.update(payload.entityId(), payload.equipped(), FlightMode.byId(payload.mode()), payload.flags());
    }

    public static void onFlapEvent(Payloads.FlapEvent payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        WingVisuals.Visual visual = mc.level.getEntity(payload.entityId()) instanceof net.minecraft.world.entity.player.Player p
                ? WingVisuals.of(p) : null;
        float rate = visual == null ? 2f : visual.equipped
                .flatMap(id -> WingRegistries.get(mc.level.registryAccess(), id))
                .map(WingDefinition::flapRate).orElse(2f);
        WingVisuals.flap(payload.entityId(), rate);
    }

    private ClientNet() {
    }
}
