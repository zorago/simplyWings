package com.zorago.simplywings.network;

import com.zorago.simplywings.client.ClientNet;
import com.zorago.simplywings.flight.ServerFlight;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetwork {
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar r = event.registrar("1");

        r.playToClient(Payloads.SyncWings.TYPE, Payloads.SyncWings.CODEC, (p, ctx) -> ClientNet.onSync(p));
        r.playToClient(Payloads.Stamina.TYPE, Payloads.Stamina.CODEC, (p, ctx) -> ClientNet.onStamina(p));
        r.playToClient(Payloads.FlightState.TYPE, Payloads.FlightState.CODEC, (p, ctx) -> ClientNet.onFlightState(p));
        r.playToClient(Payloads.FlapEvent.TYPE, Payloads.FlapEvent.CODEC, (p, ctx) -> ClientNet.onFlapEvent(p));

        r.playToServer(Payloads.RequestMode.TYPE, Payloads.RequestMode.CODEC,
                (p, ctx) -> ServerFlight.onRequestMode((ServerPlayer) ctx.player(), p));
        r.playToServer(Payloads.Flap.TYPE, Payloads.Flap.CODEC,
                (p, ctx) -> ServerFlight.onFlap((ServerPlayer) ctx.player()));
        r.playToServer(Payloads.RescueJump.TYPE, Payloads.RescueJump.CODEC,
                (p, ctx) -> ServerFlight.onRescueJump((ServerPlayer) ctx.player()));
        r.playToServer(Payloads.Impact.TYPE, Payloads.Impact.CODEC,
                (p, ctx) -> ServerFlight.onImpact((ServerPlayer) ctx.player(), p));
    }

    private ModNetwork() {
    }
}
