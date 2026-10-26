package com.zorago.simplywings.network;

import com.zorago.simplywings.client.ClientNet;
import com.zorago.simplywings.flight.FuryServer;
import com.zorago.simplywings.gacha.Gacha;
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
        r.playToClient(Payloads.FuryEvent.TYPE, Payloads.FuryEvent.CODEC, (p, ctx) -> ClientNet.onFuryEvent(p));
        r.playToClient(Payloads.TakeoffEvent.TYPE, Payloads.TakeoffEvent.CODEC, (p, ctx) -> ClientNet.onTakeoffEvent(p));
        r.playToClient(Payloads.GachaState.TYPE, Payloads.GachaState.CODEC, (p, ctx) -> ClientNet.onGachaState(p));
        r.playToClient(Payloads.GachaResults.TYPE, Payloads.GachaResults.CODEC, (p, ctx) -> ClientNet.onGachaResults(p));
        r.playToClient(Payloads.SummonShow.TYPE, Payloads.SummonShow.CODEC, (p, ctx) -> ClientNet.onSummonShow(p));

        r.playToServer(Payloads.RequestMode.TYPE, Payloads.RequestMode.CODEC,
                (p, ctx) -> ServerFlight.onRequestMode((ServerPlayer) ctx.player(), p));
        r.playToServer(Payloads.Flap.TYPE, Payloads.Flap.CODEC,
                (p, ctx) -> ServerFlight.onFlap((ServerPlayer) ctx.player()));
        r.playToServer(Payloads.RescueJump.TYPE, Payloads.RescueJump.CODEC,
                (p, ctx) -> ServerFlight.onRescueJump((ServerPlayer) ctx.player()));
        r.playToServer(Payloads.EquipWing.TYPE, Payloads.EquipWing.CODEC,
                (p, ctx) -> ServerFlight.onEquipRequest((ServerPlayer) ctx.player(), p));
        r.playToServer(Payloads.EquipAppearance.TYPE, Payloads.EquipAppearance.CODEC,
                (p, ctx) -> ServerFlight.onAppearanceRequest((ServerPlayer) ctx.player(), p));
        r.playToServer(Payloads.AirJump.TYPE, Payloads.AirJump.CODEC,
                (p, ctx) -> ServerFlight.onAirJump((ServerPlayer) ctx.player()));
        r.playToServer(Payloads.TakeoffCharge.TYPE, Payloads.TakeoffCharge.CODEC,
                (p, ctx) -> ServerFlight.onTakeoffCharge((ServerPlayer) ctx.player(), p));
        r.playToServer(Payloads.FuryCharge.TYPE, Payloads.FuryCharge.CODEC,
                (p, ctx) -> FuryServer.onCharge((ServerPlayer) ctx.player(), p));
        r.playToServer(Payloads.FuryDash.TYPE, Payloads.FuryDash.CODEC,
                (p, ctx) -> FuryServer.onDash((ServerPlayer) ctx.player()));
        r.playToServer(Payloads.GachaPull.TYPE, Payloads.GachaPull.CODEC,
                (p, ctx) -> Gacha.onPull((ServerPlayer) ctx.player(), p));
        r.playToServer(Payloads.GachaClaim.TYPE, Payloads.GachaClaim.CODEC,
                (p, ctx) -> Gacha.claim((ServerPlayer) ctx.player()));
        r.playToServer(Payloads.Impact.TYPE, Payloads.Impact.CODEC,
                (p, ctx) -> ServerFlight.onImpact((ServerPlayer) ctx.player(), p));
    }

    private ModNetwork() {
    }
}
