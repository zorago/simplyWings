package com.zorago.simplywings.client;

import com.zorago.simplywings.gacha.GachaData;
import com.zorago.simplywings.network.Payloads;
import java.util.ArrayList;
import java.util.List;

public final class ClientGacha {
    private static Payloads.GachaState state = new Payloads.GachaState(0, 0, 0, 0, 0, List.of());
    private static List<GachaData.Entry> pending = List.of();
    private static int serial;

    public static Payloads.GachaState state() {
        return state;
    }

    public static void onState(Payloads.GachaState payload) {
        state = payload;
    }

    public static void onResults(Payloads.GachaResults payload) {
        state = payload.state();
        pending = new ArrayList<>(payload.results());
        serial++;
    }

    public static int serial() {
        return serial;
    }

    public static List<GachaData.Entry> pending() {
        return pending;
    }

    private ClientGacha() {
    }
}
