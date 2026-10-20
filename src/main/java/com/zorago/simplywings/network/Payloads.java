package com.zorago.simplywings.network;

import com.zorago.simplywings.SimplyWings;
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

    public record SyncWings(List<ResourceLocation> collection, Optional<ResourceLocation> equipped, float stamina,
                            int rescueJumps) implements CustomPacketPayload {
        public static final Type<SyncWings> TYPE = payloadType("sync_wings");
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncWings> CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncWings::collection,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), SyncWings::equipped,
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

    public record FlightState(int entityId, Optional<ResourceLocation> equipped, int mode, int flags)
            implements CustomPacketPayload {
        public static final Type<FlightState> TYPE = payloadType("flight_state");
        public static final StreamCodec<RegistryFriendlyByteBuf, FlightState> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, FlightState::entityId,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), FlightState::equipped,
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
}
