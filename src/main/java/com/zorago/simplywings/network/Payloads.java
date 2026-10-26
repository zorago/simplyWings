package com.zorago.simplywings.network;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.gacha.GachaData;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public final class Payloads {
    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> payloadType(String name) {
        return new CustomPacketPayload.Type<>(SimplyWings.id(name));
    }

    public record SyncWings(List<ResourceLocation> collection, Optional<ResourceLocation> equipped,
                            Optional<ResourceLocation> appearance, float stamina, int rescueJumps)
            implements CustomPacketPayload {
        public static final Type<SyncWings> TYPE = payloadType("sync_wings");
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncWings> CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncWings::collection,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), SyncWings::equipped,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), SyncWings::appearance,
                ByteBufCodecs.FLOAT, SyncWings::stamina,
                ByteBufCodecs.VAR_INT, SyncWings::rescueJumps,
                SyncWings::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record Stamina(float stamina, int rescueJumps) implements CustomPacketPayload {
        public static final Type<Stamina> TYPE = payloadType("stamina");
        public static final StreamCodec<RegistryFriendlyByteBuf, Stamina> CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Stamina::stamina,
                ByteBufCodecs.VAR_INT, Stamina::rescueJumps,
                Stamina::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record FlightState(int entityId, Optional<ResourceLocation> equipped, Optional<ResourceLocation> appearance,
                              int mode, int flags) implements CustomPacketPayload {
        public static final Type<FlightState> TYPE = payloadType("flight_state");
        public static final StreamCodec<RegistryFriendlyByteBuf, FlightState> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, FlightState::entityId,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), FlightState::equipped,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), FlightState::appearance,
                ByteBufCodecs.VAR_INT, FlightState::mode,
                ByteBufCodecs.VAR_INT, FlightState::flags,
                FlightState::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record FlapEvent(int entityId) implements CustomPacketPayload {
        public static final Type<FlapEvent> TYPE = payloadType("flap_event");
        public static final StreamCodec<RegistryFriendlyByteBuf, FlapEvent> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, FlapEvent::entityId,
                FlapEvent::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record RequestMode(int mode, int flags) implements CustomPacketPayload {
        public static final Type<RequestMode> TYPE = payloadType("request_mode");
        public static final StreamCodec<RegistryFriendlyByteBuf, RequestMode> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, RequestMode::mode,
                ByteBufCodecs.VAR_INT, RequestMode::flags,
                RequestMode::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record Flap() implements CustomPacketPayload {
        public static final Type<Flap> TYPE = payloadType("flap");
        public static final StreamCodec<RegistryFriendlyByteBuf, Flap> CODEC = StreamCodec.unit(new Flap());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record RescueJump() implements CustomPacketPayload {
        public static final Type<RescueJump> TYPE = payloadType("rescue_jump");
        public static final StreamCodec<RegistryFriendlyByteBuf, RescueJump> CODEC = StreamCodec.unit(new RescueJump());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record EquipWing(Optional<ResourceLocation> wing) implements CustomPacketPayload {
        public static final Type<EquipWing> TYPE = payloadType("equip_wing");
        public static final StreamCodec<RegistryFriendlyByteBuf, EquipWing> CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), EquipWing::wing,
                EquipWing::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record EquipAppearance(Optional<ResourceLocation> wing) implements CustomPacketPayload {
        public static final Type<EquipAppearance> TYPE = payloadType("equip_appearance");
        public static final StreamCodec<RegistryFriendlyByteBuf, EquipAppearance> CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), EquipAppearance::wing,
                EquipAppearance::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record AirJump() implements CustomPacketPayload {
        public static final Type<AirJump> TYPE = payloadType("air_jump");
        public static final StreamCodec<RegistryFriendlyByteBuf, AirJump> CODEC = StreamCodec.unit(new AirJump());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record Impact(float speed) implements CustomPacketPayload {
        public static final Type<Impact> TYPE = payloadType("impact");
        public static final StreamCodec<RegistryFriendlyByteBuf, Impact> CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Impact::speed,
                Impact::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private Payloads() {
    }
    public record FuryCharge(boolean charging) implements CustomPacketPayload {
        public static final Type<FuryCharge> TYPE = payloadType("fury_charge");
        public static final StreamCodec<RegistryFriendlyByteBuf, FuryCharge> CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, FuryCharge::charging,
                FuryCharge::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record TakeoffCharge(boolean charging) implements CustomPacketPayload {
        public static final Type<TakeoffCharge> TYPE = payloadType("takeoff_charge");
        public static final StreamCodec<RegistryFriendlyByteBuf, TakeoffCharge> CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, TakeoffCharge::charging,
                TakeoffCharge::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record TakeoffEvent(int entityId, boolean charging) implements CustomPacketPayload {
        public static final Type<TakeoffEvent> TYPE = payloadType("takeoff_event");
        public static final StreamCodec<RegistryFriendlyByteBuf, TakeoffEvent> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, TakeoffEvent::entityId,
                ByteBufCodecs.BOOL, TakeoffEvent::charging,
                TakeoffEvent::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record FuryDash() implements CustomPacketPayload {
        public static final Type<FuryDash> TYPE = payloadType("fury_dash");
        public static final StreamCodec<RegistryFriendlyByteBuf, FuryDash> CODEC = StreamCodec.unit(new FuryDash());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record FuryEvent(int entityId, int kind) implements CustomPacketPayload {
        public static final int CHARGE_START = 0;
        public static final int CHARGE_STOP = 1;
        public static final int DASH = 2;
        public static final Type<FuryEvent> TYPE = payloadType("fury_event");
        public static final StreamCodec<RegistryFriendlyByteBuf, FuryEvent> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, FuryEvent::entityId,
                ByteBufCodecs.VAR_INT, FuryEvent::kind,
                FuryEvent::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record GachaPull(int count) implements CustomPacketPayload {
        public static final Type<GachaPull> TYPE = payloadType("gacha_pull");
        public static final StreamCodec<RegistryFriendlyByteBuf, GachaPull> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, GachaPull::count,
                GachaPull::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record GachaState(int pityS, int pitySS, int pitySSS, int total, int feathers, List<GachaData.Entry> history)
            implements CustomPacketPayload {
        public static final Type<GachaState> TYPE = payloadType("gacha_state");
        public static final StreamCodec<RegistryFriendlyByteBuf, GachaState> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, GachaState::pityS,
                ByteBufCodecs.VAR_INT, GachaState::pitySS,
                ByteBufCodecs.VAR_INT, GachaState::pitySSS,
                ByteBufCodecs.VAR_INT, GachaState::total,
                ByteBufCodecs.VAR_INT, GachaState::feathers,
                GachaData.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), GachaState::history,
                GachaState::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record GachaResults(List<GachaData.Entry> results, GachaState state) implements CustomPacketPayload {
        public static final Type<GachaResults> TYPE = payloadType("gacha_results");
        public static final StreamCodec<RegistryFriendlyByteBuf, GachaResults> CODEC = StreamCodec.composite(
                GachaData.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), GachaResults::results,
                GachaState.CODEC, GachaResults::state,
                GachaResults::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SummonShow(net.minecraft.core.BlockPos pos, int facing, String anim, long start, int seed, List<Integer> tiers,
                             int summoner) implements CustomPacketPayload {
        public static final Type<SummonShow> TYPE = payloadType("summon_show");
        public static final StreamCodec<RegistryFriendlyByteBuf, SummonShow> CODEC = StreamCodec.of((buf, v) -> {
            buf.writeBlockPos(v.pos());
            buf.writeVarInt(v.facing());
            buf.writeUtf(v.anim());
            buf.writeLong(v.start());
            buf.writeInt(v.seed());
            buf.writeVarInt(v.tiers().size());
            v.tiers().forEach(buf::writeVarInt);
            buf.writeVarInt(v.summoner());
        }, buf -> {
            net.minecraft.core.BlockPos pos = buf.readBlockPos();
            int facing = buf.readVarInt();
            String anim = buf.readUtf();
            long start = buf.readLong();
            int seed = buf.readInt();
            int n = buf.readVarInt();
            java.util.List<Integer> tiers = new java.util.ArrayList<>();
            for (int i = 0; i < n; i++) {
                tiers.add(buf.readVarInt());
            }
            return new SummonShow(pos, facing, anim, start, seed, tiers, buf.readVarInt());
        });

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
