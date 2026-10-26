package com.zorago.simplywings.client;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.data.EffectiveStats;
import com.zorago.simplywings.data.WingDefinition;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.player.FlightMode;
import com.zorago.simplywings.sound.ModSounds;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class FlightLoops {
    private static final double BUZZ_RANGE_SQR = 48 * 48;
    private static Wind wind;
    private static final Int2ObjectMap<Buzz> BUZZ = new Int2ObjectOpenHashMap<>();

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            wind = null;
            BUZZ.clear();
            return;
        }
        if ((wind == null || wind.isStopped()) && ClientFlight.mode() == FlightMode.TRAVEL && speed(mc.player) > 8) {
            wind = new Wind();
            mc.getSoundManager().play(wind);
        }
        BUZZ.int2ObjectEntrySet().removeIf(e -> e.getValue().isStopped());
        for (Player player : mc.level.players()) {
            if (BUZZ.containsKey(player.getId()) || player.distanceToSqr(mc.player) > BUZZ_RANGE_SQR || !buzzing(player)) {
                continue;
            }
            Optional<ResourceLocation> look = look(player);
            SoundEvent sound = look.map(id -> SoundEvent.createVariableRangeEvent(SimplyWings.id("wing." + id.getPath() + ".buzz")))
                    .filter(s -> mc.getSoundManager().getSoundEvent(s.getLocation()) != null).orElse(null);
            if (sound != null) {
                Buzz buzz = new Buzz(player, sound);
                BUZZ.put(player.getId(), buzz);
                mc.getSoundManager().play(buzz);
            }
        }
    }

    private static double speed(Player player) {
        if (player == Minecraft.getInstance().player) {
            return player.getDeltaMovement().length() * 20;
        }
        return player.position().subtract(player.xo, player.yo, player.zo).length() * 20;
    }

    private static Optional<ResourceLocation> look(Player player) {
        if (player == Minecraft.getInstance().player) {
            return ClientWings.appearance().or(() -> ClientWings.stats().map(EffectiveStats::wing));
        }
        WingVisuals.Visual visual = WingVisuals.of(player);
        return visual == null ? Optional.empty() : visual.look();
    }

    private static boolean buzzing(Player player) {
        if (player.isInvisible() || player.isRemoved()) {
            return false;
        }
        FlightMode mode;
        boolean thrust;
        Optional<ResourceLocation> equipped;
        if (player == Minecraft.getInstance().player) {
            mode = ClientFlight.mode();
            thrust = (ClientFlight.flags() & FlightMode.FLAG_THRUST) != 0;
            equipped = ClientWings.stats().map(EffectiveStats::wing);
        } else {
            WingVisuals.Visual visual = WingVisuals.of(player);
            if (visual == null) {
                return false;
            }
            mode = visual.mode;
            thrust = visual.has(FlightMode.FLAG_THRUST);
            equipped = visual.equipped;
        }
        if (mode == FlightMode.NONE || (mode == FlightMode.TRAVEL && !thrust)) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        return equipped.flatMap(id -> WingRegistries.get(mc.level.registryAccess(), id)).map(WingDefinition::continuous).orElse(false);
    }

    private static final class Wind extends AbstractTickableSoundInstance {
        Wind() {
            super(ModSounds.FLIGHT_WIND.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
            looping = true;
            delay = 0;
            relative = true;
            attenuation = Attenuation.NONE;
            volume = 0.01f;
        }

        @Override
        public void tick() {
            Player player = Minecraft.getInstance().player;
            float target = 0;
            if (player != null && ClientFlight.mode() == FlightMode.TRAVEL) {
                double v = speed(player);
                target = (float) Mth.clamp((v - 8) / 52, 0, 1) * 0.75f;
                pitch = (float) (0.8 + 0.45 * Mth.clamp(v / 60, 0, 1));
            }
            volume += (target - volume) * 0.12f;
            if (target == 0 && volume < 0.01f) {
                stop();
            }
        }

        @Override
        public boolean canStartSilent() {
            return true;
        }
    }

    private static final class Buzz extends AbstractTickableSoundInstance {
        private final Player player;
        private final float basePitch;

        Buzz(Player player, SoundEvent sound) {
            super(sound, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
            this.player = player;
            looping = true;
            delay = 0;
            volume = 0.01f;
            basePitch = 0.97f + random.nextFloat() * 0.06f;
            pitch = basePitch;
            x = player.getX();
            y = player.getY() + 1;
            z = player.getZ();
        }

        @Override
        public void tick() {
            boolean on = buzzing(player);
            double v = speed(player);
            float target = on ? 0.45f + 0.35f * (float) Mth.clamp(v / 40, 0, 1) : 0;
            pitch = basePitch * (1f + 0.25f * (float) Mth.clamp(v / 40, 0, 1));
            volume += (target - volume) * (on ? 0.2f : 0.1f);
            x = player.getX();
            y = player.getY() + 1;
            z = player.getZ();
            if (!on && volume < 0.01f || player.isRemoved()) {
                stop();
            }
        }

        @Override
        public boolean canStartSilent() {
            return true;
        }
    }

    private FlightLoops() {
    }
}
