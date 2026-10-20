package com.zorago.simplywings.flight;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.data.EffectiveStats;
import com.zorago.simplywings.network.Payloads;
import com.zorago.simplywings.player.FlightMode;
import com.zorago.simplywings.player.ModAttachments;
import com.zorago.simplywings.player.WingsData;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ServerFlight {
    private static final ResourceLocation GRAVITY_MODIFIER = SimplyWings.id("flight_gravity");
    private static final int CRASH_WINDOW_TICKS = 10;
    private static final int AIR_JUMP_REFUND_TICKS = 8;

    public static void register(IEventBus bus) {
        bus.addListener(ServerFlight::onPlayerTick);
        bus.addListener(ServerFlight::onFall);
        bus.addListener(ServerFlight::onLogin);
        bus.addListener(ServerFlight::onRespawn);
        bus.addListener(ServerFlight::onChangeDimension);
        bus.addListener(ServerFlight::onStartTracking);
    }

    public static boolean unlockAndEquip(ServerPlayer player, ResourceLocation wing) {
        WingsData data = ModAttachments.get(player);
        boolean added = data.unlock(wing);
        equip(player, Optional.of(wing));
        return added;
    }

    public static void equip(ServerPlayer player, Optional<ResourceLocation> wing) {
        WingsData data = ModAttachments.get(player);
        boolean first = data.equipped().isEmpty();
        data.equip(wing);
        Optional<EffectiveStats> stats = data.stats(player.registryAccess());
        if (stats.isEmpty()) {
            setMode(player, data, FlightMode.NONE, 0, true);
        } else if (first) {
            data.setStamina(stats.get().stamina());
        } else {
            data.setStamina(Math.min(data.stamina(), stats.get().stamina()));
        }
        syncAll(player);
    }

    public static void onEquipRequest(ServerPlayer player, Payloads.EquipWing request) {
        WingsData data = ModAttachments.get(player);
        if (request.wing().isPresent() && !data.collection().contains(request.wing().get())) {
            syncAll(player);
            return;
        }
        if (!request.wing().equals(data.equipped())) {
            equip(player, request.wing());
        }
    }

    public static void clear(ServerPlayer player) {
        WingsData data = ModAttachments.get(player);
        data.clearCollection();
        setMode(player, data, FlightMode.NONE, 0, true);
        syncAll(player);
    }

    public static void setStamina(ServerPlayer player, float value) {
        WingsData data = ModAttachments.get(player);
        float max = data.stats(player.registryAccess()).map(EffectiveStats::stamina).orElse(0f);
        data.setStamina(Math.max(0, Math.min(max, value)));
        sendStamina(player, data);
    }

    public static void syncAll(ServerPlayer player) {
        WingsData data = ModAttachments.get(player);
        PacketDistributor.sendToPlayer(player, new Payloads.SyncWings(List.copyOf(data.collection()), data.equipped(),
                data.stamina(), data.rescueJumps()));
        data.setLastSentStamina(data.stamina());
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, state(player, data));
    }

    private static Payloads.FlightState state(Player player, WingsData data) {
        return new Payloads.FlightState(player.getId(), data.equipped(), data.mode().ordinal(), data.flags());
    }

    private static boolean canFly(ServerPlayer player) {
        return player.isAlive() && !player.isSpectator() && !player.isPassenger() && !player.isSleeping()
                && !player.getAbilities().flying;
    }

    private static boolean infinite(Player player) {
        return player.getAbilities().instabuild;
    }

    public static void onRequestMode(ServerPlayer player, Payloads.RequestMode request) {
        WingsData data = ModAttachments.get(player);
        FlightMode wanted = FlightMode.byId(request.mode());
        FlightMode current = data.mode();
        Optional<EffectiveStats> stats = data.stats(player.registryAccess());
        boolean allowed;
        if (wanted == FlightMode.NONE) {
            allowed = true;
        } else if (stats.isEmpty() || !canFly(player)) {
            allowed = false;
        } else if (wanted == current) {
            allowed = true;
        } else if (wanted == FlightMode.TRAVEL) {
            float cost = ServerConfig.FLAP_COST.get().floatValue();
            allowed = infinite(player) || data.stamina() >= cost;
            if (allowed && !infinite(player)) {
                data.setStamina(data.stamina() - cost);
                data.setLastFlapTick(player.level().getGameTime());
            }
        } else {
            allowed = infinite(player) || data.stamina() > 0;
            long now = player.level().getGameTime();
            if (allowed && current == FlightMode.NONE && now - data.lastAirJumpTick() <= AIR_JUMP_REFUND_TICKS) {
                float max = stats.get().stamina();
                data.setStamina(Math.min(max, data.stamina() + ServerConfig.AIR_JUMP_COST.get().floatValue()));
                data.setLastAirJumpTick(Long.MIN_VALUE / 2);
            }
        }
        if (allowed) {
            setMode(player, data, wanted, request.flags(), false);
        } else {
            PacketDistributor.sendToPlayer(player, state(player, data));
            sendStamina(player, data);
        }
    }

    public static void onFlap(ServerPlayer player) {
        WingsData data = ModAttachments.get(player);
        Optional<EffectiveStats> stats = data.stats(player.registryAccess());
        if (stats.isEmpty() || data.mode() != FlightMode.TRAVEL) {
            return;
        }
        long now = player.level().getGameTime();
        float cost = stats.get().definition().beatCost(ServerConfig.FLAP_COST.get().floatValue());
        double minInterval = 20.0 / stats.get().flapRate() * 0.6;
        if (now - data.lastFlapTick() < minInterval || (!infinite(player) && data.stamina() < cost)) {
            sendStamina(player, data);
            return;
        }
        if (!infinite(player)) {
            data.setStamina(data.stamina() - cost);
        }
        data.setLastFlapTick(now);
        player.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.PHANTOM_FLAP,
                SoundSource.PLAYERS, 0.6f, 1.3f);
        PacketDistributor.sendToPlayersTrackingEntity(player, new Payloads.FlapEvent(player.getId()));
    }

    public static void onAirJump(ServerPlayer player) {
        WingsData data = ModAttachments.get(player);
        float cost = ServerConfig.AIR_JUMP_COST.get().floatValue();
        boolean valid = data.equipped().isPresent() && data.mode() == FlightMode.NONE && !player.onGround()
                && canFly(player) && (infinite(player) || data.stamina() >= cost);
        if (!valid) {
            sendStamina(player, data);
            return;
        }
        if (!infinite(player)) {
            data.setStamina(data.stamina() - cost);
        }
        data.setLastAirJumpTick(player.level().getGameTime());
        player.resetFallDistance();
        player.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.PHANTOM_FLAP,
                SoundSource.PLAYERS, 0.5f, 1.2f);
        PacketDistributor.sendToPlayersTrackingEntity(player, new Payloads.FlapEvent(player.getId()));
    }

    public static void onRescueJump(ServerPlayer player) {
        WingsData data = ModAttachments.get(player);
        if (data.equipped().isPresent() && data.mode() == FlightMode.NONE && !player.onGround() && data.rescueJumps() > 0) {
            data.setRescueJumps(data.rescueJumps() - 1);
            player.resetFallDistance();
        }
        sendStamina(player, data);
    }

    public static void onImpact(ServerPlayer player, Payloads.Impact impact) {
        WingsData data = ModAttachments.get(player);
        if (player.level().getGameTime() - data.lastTravelTick() > CRASH_WINDOW_TICKS) {
            return;
        }
        double max = ServerConfig.TERMINAL_VELOCITY.get() * 1.5;
        double speed = Math.min(max, Math.max(0, impact.speed()));
        double damage = ServerConfig.CRASH_DAMAGE_PER_SPEED.get() * Math.max(0, speed - ServerConfig.CRASH_THRESHOLD.get());
        if (damage > 0) {
            player.hurt(player.damageSources().flyIntoWall(), (float) damage);
        }
    }

    private static void setMode(ServerPlayer player, WingsData data, FlightMode mode, int flags, boolean notifySelf) {
        FlightMode previous = data.mode();
        int previousFlags = data.flags();
        data.setMode(mode, flags);
        applyGravity(player, mode.flying());
        if (mode.flying()) {
            player.stopFallFlying();
            player.resetFallDistance();
        }
        if (mode == FlightMode.TRAVEL || previous == FlightMode.TRAVEL) {
            data.setLastTravelTick(player.level().getGameTime());
        }
        if (previous != mode || previousFlags != data.flags()) {
            PacketDistributor.sendToPlayersTrackingEntity(player, state(player, data));
        }
        if (notifySelf) {
            PacketDistributor.sendToPlayer(player, state(player, data));
        }
    }

    private static void applyGravity(ServerPlayer player, boolean flying) {
        AttributeInstance gravity = player.getAttribute(Attributes.GRAVITY);
        if (gravity == null) {
            return;
        }
        if (flying) {
            if (!gravity.hasModifier(GRAVITY_MODIFIER)) {
                gravity.addTransientModifier(new AttributeModifier(GRAVITY_MODIFIER, -1.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        } else {
            gravity.removeModifier(GRAVITY_MODIFIER);
        }
    }

    private static void sendStamina(ServerPlayer player, WingsData data) {
        PacketDistributor.sendToPlayer(player, new Payloads.Stamina(data.stamina(), data.rescueJumps()));
        data.setLastSentStamina(data.stamina());
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        WingsData data = ModAttachments.get(player);
        Optional<EffectiveStats> stats = data.stats(player.registryAccess());
        if (stats.isEmpty()) {
            if (data.mode().flying()) {
                setMode(player, data, FlightMode.NONE, 0, true);
            }
            return;
        }
        if (data.mode().flying() && !canFly(player)) {
            setMode(player, data, FlightMode.NONE, 0, true);
        }
        float max = stats.get().stamina();
        float stamina = data.stamina();
        long now = player.level().getGameTime();
        int rescueMax = ServerConfig.RESCUE_JUMPS.get();
        if (player.onGround() && data.rescueJumps() != rescueMax) {
            data.setRescueJumps(rescueMax);
            sendStamina(player, data);
        }
        if (infinite(player)) {
            stamina = max;
        } else {
            switch (data.mode()) {
                case STABILIZED -> stamina -= ServerConfig.STABILIZED_DRAIN.get().floatValue() / 20f;
                case TRAVEL -> {
                    boolean thrust = (data.flags() & FlightMode.FLAG_THRUST) != 0 && stats.get().definition().continuous();
                    if (thrust) {
                        stamina -= stats.get().definition().continuousCostPerSecond(
                                ServerConfig.FLAP_COST.get().floatValue()) / 20f;
                        data.setLastFlapTick(now);
                    } else if (now - data.lastFlapTick() > 10 && (data.flags() & FlightMode.FLAG_BRAKE) == 0) {
                        stamina += ServerConfig.GLIDE_REGEN.get().floatValue() / 20f;
                    }
                }
                case NONE -> {
                    if (player.onGround()) {
                        stamina += ServerConfig.GROUND_REGEN.get().floatValue() / 20f;
                    }
                }
            }
        }
        stamina = Math.max(0, Math.min(max, stamina));
        data.setStamina(stamina);
        if (data.mode() == FlightMode.STABILIZED && stamina <= 0) {
            setMode(player, data, FlightMode.NONE, 0, true);
        }
        if (data.mode().flying()) {
            player.resetFallDistance();
            if (data.mode() == FlightMode.TRAVEL) {
                data.setLastTravelTick(now);
            }
        }
        float drift = Math.abs(stamina - data.lastSentStamina());
        boolean boundary = (stamina <= 0 || stamina >= max) && stamina != data.lastSentStamina();
        if (drift >= max * ServerConfig.STAMINA_SYNC_FRACTION.get() || boundary) {
            sendStamina(player, data);
        }
    }

    private static void onFall(LivingFallEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WingsData data = ModAttachments.get(player);
            boolean recentTravel = player.level().getGameTime() - data.lastTravelTick() <= CRASH_WINDOW_TICKS;
            if (data.mode().flying() || recentTravel) {
                event.setCanceled(true);
            }
        }
    }

    private static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WingsData data = ModAttachments.get(player);
            setMode(player, data, FlightMode.NONE, 0, false);
            syncAll(player);
        }
    }

    private static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WingsData data = ModAttachments.get(player);
            setMode(player, data, FlightMode.NONE, 0, false);
            syncAll(player);
        }
    }

    private static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WingsData data = ModAttachments.get(player);
            setMode(player, data, FlightMode.NONE, 0, false);
            syncAll(player);
        }
    }

    private static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer target && event.getEntity() instanceof ServerPlayer viewer) {
            WingsData data = ModAttachments.get(target);
            if (data.equipped().isPresent()) {
                PacketDistributor.sendToPlayer(viewer, state(target, data));
            }
        }
    }

    private ServerFlight() {
    }
}
