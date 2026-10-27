package com.zorago.simplywings.client;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.data.EffectiveStats;
import com.zorago.simplywings.data.WingDefinition;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.essence.CloudAbsorberItem;
import com.zorago.simplywings.essence.EssenceType;
import com.zorago.simplywings.flight.CloudHarvest;
import com.zorago.simplywings.item.ModItems;
import com.zorago.simplywings.player.FlightMode;
import com.zorago.simplywings.sound.ModSounds;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class FlightLoops {
    private static final double BUZZ_RANGE_SQR = 48 * 48;
    private static final double FLYBY_RANGE_SQR = 5 * 5;
    private static final double FLYBY_SPEED = 14;
    private static final int FLYBY_COOLDOWN = 40;
    private static Wind wind;
    private static Reserve reserve;
    private static int dripTicks;
    private static Item held;
    private static final Int2ObjectMap<Buzz> BUZZ = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<Brake> BRAKE = new Int2ObjectOpenHashMap<>();
    private static final Int2IntMap FLYBY = new Int2IntOpenHashMap();

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            wind = null;
            reserve = null;
            held = null;
            BUZZ.clear();
            BRAKE.clear();
            FLYBY.clear();
            return;
        }
        if ((wind == null || wind.isStopped()) && ClientFlight.mode() == FlightMode.TRAVEL && speed(mc.player) > 8) {
            wind = new Wind();
            mc.getSoundManager().play(wind);
        }
        if ((reserve == null || reserve.isStopped()) && ClientFlight.reserveActive()) {
            reserve = new Reserve();
            mc.getSoundManager().play(reserve);
        }
        drip(mc.player);
        compass(mc.player);
        BRAKE.int2ObjectEntrySet().removeIf(e -> e.getValue().isStopped());
        int now = mc.player.tickCount;
        FLYBY.int2IntEntrySet().removeIf(e -> e.getIntValue() < now - FLYBY_COOLDOWN || e.getIntValue() > now);
        for (Player player : mc.level.players()) {
            if (player.distanceToSqr(mc.player) > BUZZ_RANGE_SQR) {
                continue;
            }
            if (!BRAKE.containsKey(player.getId()) && braking(player) && speed(player) > 4) {
                WingSounds.play(player, look(player), true, 1f, 0.9f);
                Brake brake = new Brake(player);
                BRAKE.put(player.getId(), brake);
                mc.getSoundManager().play(brake);
            }
            if (player != mc.player && !FLYBY.containsKey(player.getId()) && flying(player)
                    && player.distanceToSqr(mc.player) < FLYBY_RANGE_SQR && relativeSpeed(player, mc.player) > FLYBY_SPEED) {
                FLYBY.put(player.getId(), now);
                mc.level.playLocalSound(player.getX(), player.getY() + 1, player.getZ(), ModSounds.FLIGHT_FLYBY.get(), SoundSource.PLAYERS,
                        1f, 0.9f + mc.player.getRandom().nextFloat() * 0.2f, false);
            }
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

    private static void drip(LocalPlayer player) {
        if (dripTicks > 0) {
            dripTicks--;
            return;
        }
        FlightMode mode = ClientFlight.mode();
        if (mode == FlightMode.NONE || mode == FlightMode.STABILIZED && !ServerConfig.CLOUD_HOVER_HARVEST.get()) {
            return;
        }
        EssenceType type = CloudHarvest.zone(player);
        if (type == null || CloudAbsorberItem.best(player.getInventory()) == 0 || !CloudHarvest.fillable(player.getInventory(), type)) {
            return;
        }
        RandomSource random = player.getRandom();
        dripTicks = 2 + random.nextInt(7);
        player.level().playLocalSound(player.getX(), player.getY() + 1, player.getZ(), ModSounds.ESSENCE_DRIP.get(), SoundSource.PLAYERS,
                0.12f + random.nextFloat() * 0.1f, 0.8f + random.nextFloat() * 0.7f, false);
    }

    private static void compass(LocalPlayer player) {
        Item item = player.getMainHandItem().getItem();
        if (item != held && ModItems.COMPASSES.stream().anyMatch(c -> c.get() == item)) {
            player.level().playLocalSound(player.getX(), player.getY() + 1, player.getZ(), ModSounds.COMPASS.get(), SoundSource.PLAYERS,
                    1f, 1f, false);
        }
        held = item;
    }

    private static boolean braking(Player player) {
        if (player == Minecraft.getInstance().player) {
            return ClientFlight.mode() == FlightMode.TRAVEL && (ClientFlight.flags() & FlightMode.FLAG_BRAKE) != 0;
        }
        WingVisuals.Visual visual = WingVisuals.of(player);
        return visual != null && visual.mode == FlightMode.TRAVEL && visual.has(FlightMode.FLAG_BRAKE);
    }

    private static boolean flying(Player player) {
        WingVisuals.Visual visual = WingVisuals.of(player);
        return visual != null && visual.mode != FlightMode.NONE && !player.isInvisible();
    }

    private static double relativeSpeed(Player other, Player self) {
        Vec3 moved = other.position().subtract(other.xo, other.yo, other.zo);
        return moved.subtract(self.getDeltaMovement()).length() * 20;
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

    private static final class Brake extends AbstractTickableSoundInstance {
        private final Player player;
        private final double start;

        Brake(Player player) {
            super(ModSounds.FLIGHT_BRAKE.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
            this.player = player;
            this.start = Math.max(1, speed(player));
            looping = true;
            delay = 0;
            volume = 0.8f;
            x = player.getX();
            y = player.getY() + 1;
            z = player.getZ();
        }

        @Override
        public void tick() {
            boolean on = braking(player);
            float target = on ? 0.8f * (float) Mth.clamp(speed(player) / start, 0, 1) : 0;
            volume += (target - volume) * (on ? 0.3f : 0.15f);
            x = player.getX();
            y = player.getY() + 1;
            z = player.getZ();
            if (volume < 0.02f && target < 0.02f || player.isRemoved()) {
                stop();
            }
        }
    }

    private static final class Reserve extends AbstractTickableSoundInstance {
        private float targetVolume = 0.4f;
        private float targetPitch = 1f;

        Reserve() {
            super(ModSounds.ESSENCE_RESERVE.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
            looping = true;
            delay = 0;
            relative = true;
            attenuation = Attenuation.NONE;
            volume = 0.01f;
        }

        @Override
        public void tick() {
            if (random.nextInt(15) == 0) {
                targetVolume = 0.25f + random.nextFloat() * 0.3f;
                targetPitch = 0.85f + random.nextFloat() * 0.3f;
            }
            boolean on = ClientFlight.reserveActive();
            volume += ((on ? targetVolume : 0) - volume) * (on ? 0.1f : 0.08f);
            pitch += (targetPitch - pitch) * 0.05f;
            if (!on && volume < 0.01f) {
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
