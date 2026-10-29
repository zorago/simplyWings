package com.zorago.simplywings.client;

import net.minecraft.sounds.SoundSource;
import com.zorago.simplywings.sound.ModSounds;
import net.minecraft.resources.ResourceLocation;
import com.zorago.simplywings.client.gui.WingsScreen;
import com.zorago.simplywings.anim.WingStyle;
import com.zorago.simplywings.config.ClientConfig;
import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.essence.EssenceReserve;
import com.zorago.simplywings.data.EffectiveStats;
import com.zorago.simplywings.data.FurySet;
import com.zorago.simplywings.data.WingDefinition;
import com.zorago.simplywings.data.WingRegistries;
import com.zorago.simplywings.network.Payloads;
import com.zorago.simplywings.player.FlightMode;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ClientFlight {
    private static final double DT = 0.05;
    private static final int AIR_JUMP_REFUND_TICKS = 6;
    private static final int TAKEOFF_KEY_LOCK_TICKS = 30;
    private static final int TURN_SUBSTEPS = 4;
    private static final double BRAKE_STEERING = 0.1;
    private static final int IMPACT_TICKS = 3;
    private static final double[] RECENT_SPEED = new double[IMPACT_TICKS];
    private static int recentCount;
    private static double lastImpact;
    private static int sinceHit = IMPACT_TICKS;
    private static final RandomSource RANDOM = RandomSource.create();

    private static FlightMode mode = FlightMode.NONE;
    private static int flags;
    private static int reserveSeen = -1;
    private static int reservePredicted;

    private static float heading;
    private static float pitch;
    private static float prevHeading;
    private static float prevPitch;
    private static double speed;
    private static double stallVy;
    private static float aimYaw;
    private static float aimPitch;
    private static float yawRate;
    private static float pitchRate;
    private static int chargeTicks;
    private static boolean chargeShared;
    private static int flapCooldown;
    private static int ticksSinceFlap;
    private static boolean stalled;
    private static boolean braking;
    private static boolean tucked;
    private static boolean thrusting;
    private static int pendingStrike = -1;
    private static double swayPhase;
    private static float bank;
    private static float prevBank;

    private static Vec3 stabVel = Vec3.ZERO;
    private static Vec3 drift = Vec3.ZERO;
    private static Vec3 buffet = Vec3.ZERO;
    private static final int BUFFET_TICKS = 14;
    private static Vec3 buffetTarget = Vec3.ZERO;
    private static int holdH;
    private static int holdV;
    private static double phase;

    private static Vec3 lastVelocity = Vec3.ZERO;
    private static boolean jumpWasDown;
    private static int ticksSinceJump = 100;
    private static int ticksSinceAirJump = 100;
    private static int travelKeyLock;
    private static int furyCharge;
    private static int furyWindow;
    private static int furyCooldown;
    private static boolean furyCharging;
    private static boolean sprintWasDown;
    private static double cruiseMax;
    private static int hoverSoundTicks;
    private static int reserveTicks;

    public static FlightMode mode() {
        return mode;
    }

    public static int flags() {
        return flags;
    }

    public static float takeoffProgress() {
        return chargeTicks / (float) ServerConfig.TAKEOFF_CHARGE_TICKS.get();
    }

    public static boolean terminalDive() {
        return mode == FlightMode.TRAVEL && !braking
                && (tucked || (speed > cruiseMax && pitch >= ServerConfig.TUCK_MIN_PITCH.get()));
    }

    public static float chargeProgress() {
        float fury = furyWindow > 0 ? 1f : furyCharge / (float) FurySet.CHARGE_TICKS;
        return Math.max(takeoffProgress(), fury);
    }

    public static float bank(float partialTick) {
        return Mth.lerp(partialTick, prevBank, bank);
    }

    private static boolean creative(LocalPlayer player) {
        return player.getAbilities().instabuild || FurySet.infiniteStamina(ClientWings.collection());
    }

    private static float flapCost() {
        return ServerConfig.FLAP_COST.get().floatValue();
    }

    public static String debugState() {
        return String.format(java.util.Locale.ROOT, "mode=%s flags=%d speed=%.2f heading=%.1f pitch=%.1f aim=%.1f,%.1f braking=%s stalled=%s tucked=%s thrust=%s furyCharge=%d furyWindow=%d reserve=%d impact=%.2f",
                mode, flags, speed, heading, pitch, aimYaw, aimPitch, braking, stalled, tucked, thrusting, furyCharge, furyWindow, reserveTicks, lastImpact);
    }

    public static boolean reserveActive() {
        return reserveTicks > 0;
    }

    private static void refill(LocalPlayer player, EffectiveStats stats) {
        if (reserveTicks > 0) {
            reserveTicks--;
        }
        if (creative(player)) {
            reserveSeen = -1;
            return;
        }
        int available = EssenceReserve.available(player);
        if (available != reserveSeen) {
            reserveSeen = available;
            reservePredicted = 0;
        }
        float stamina = ClientWings.stamina();
        float need = EssenceReserve.need(stats.definition().beatCost(flapCost()));
        if (EssenceReserve.wants(player, stamina, stats.stamina(), need)) {
            int got = Math.min(available - reservePredicted, EssenceReserve.packet(stamina, stats.stamina(), need));
            if (got > 0) {
                reservePredicted += got;
                reserveTicks = 40;
                ClientWings.setStamina(stamina + got);
            }
        }
    }

    private static void checkImpact(LocalPlayer player) {
        double actual = player.getDeltaMovement().length() * 20;
        sinceHit = player.horizontalCollision || player.verticalCollision || player.onGround() ? 0 : sinceHit + 1;
        if (actual > ServerConfig.TERMINAL_VELOCITY.get() * 2) {
            recentCount = 0;
            return;
        }
        double peak = actual;
        for (int i = 0; i < Math.min(recentCount, IMPACT_TICKS); i++) {
            peak = Math.max(peak, RECENT_SPEED[i]);
        }
        RECENT_SPEED[recentCount % IMPACT_TICKS] = actual;
        recentCount++;
        double drop = peak - actual;
        if (drop > ServerConfig.CRASH_THRESHOLD.get() && sinceHit < IMPACT_TICKS) {
            PacketDistributor.sendToServer(new Payloads.Impact((float) drop));
            lastImpact = drop;
            recentCount = 0;
        }
    }

    private static void spend(LocalPlayer player, float amount) {
        if (!creative(player)) {
            ClientWings.setStamina(Math.max(0, ClientWings.stamina() - amount));
        }
    }

    private static void setMode(LocalPlayer player, FlightMode next, boolean send) {
        FlightMode previous = mode;
        if (previous == next) {
            return;
        }
        mode = next;
        recentCount = 0;
        sinceHit = IMPACT_TICKS;
        if (next != FlightMode.TRAVEL) {
            flags = 0;
            bank = 0;
            prevBank = 0;
        }
        if (previous == FlightMode.TRAVEL) {
            if (next == FlightMode.STABILIZED) {
                FlightCamera.intoHead(player);
            } else {
                FlightCamera.stop(player);
            }
        }
        if (next == FlightMode.TRAVEL) {
            FlightCamera.start(player);
        }
        if (next.flying()) {
            player.stopFallFlying();
        }
        if (send) {
            PacketDistributor.sendToServer(new Payloads.RequestMode(next.ordinal(), flags));
        }
    }

    static void forceMode(FlightMode next) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || next == mode) {
            return;
        }
        switch (next) {
            case STABILIZED -> initStabilized(player, false);
            case TRAVEL -> ClientWings.stats().ifPresent(stats -> initTravel(player, stats));
            default -> {
            }
        }
        setMode(player, next, false);
    }

    static void reset() {
        mode = FlightMode.NONE;
        flags = 0;
        furyCharging = false;
        furyCharge = 0;
        furyWindow = 0;
        FlightCamera.reset();
    }

    static void onMovementInput(MovementInputUpdateEvent event) {
        if (!mode.flying()) {
            return;
        }
        Input input = event.getInput();
        input.forwardImpulse = 0;
        input.leftImpulse = 0;
        input.up = false;
        input.down = false;
        input.left = false;
        input.right = false;
        input.jumping = false;
        input.shiftKeyDown = false;
    }

    static void preTick(LocalPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        while (ModKeys.MENU.consumeClick()) {
            if (mc.screen == null) {
                mc.setScreen(new WingsScreen());
            }
        }
        Optional<EffectiveStats> maybeStats = ClientWings.stats();
        boolean jumpDown = Minecraft.getInstance().options.keyJump.isDown();
        boolean jumpPressed = jumpDown && !jumpWasDown;
        jumpWasDown = jumpDown;
        ticksSinceJump++;
        ticksSinceAirJump++;
        if (maybeStats.isEmpty()) {
            if (mode.flying()) {
                setMode(player, FlightMode.NONE, true);
            }
            while (ModKeys.TRAVEL.consumeClick()) {
            }
            chargeTicks = 0;
            return;
        }
        EffectiveStats stats = maybeStats.get();
        if (mode.flying() && !canFly(player)) {
            setMode(player, FlightMode.NONE, true);
        }
        refill(player, stats);
        if (jumpPressed) {
            handleJump(player, stats);
        }
        handleTravelKey(player, stats);
        switch (mode) {
            case STABILIZED -> tickStabilized(player, stats);
            case TRAVEL -> tickTravel(player, stats);
            case NONE -> {
                if (player.onGround() && !creative(player)) {
                    ClientWings.setStamina(Math.min(stats.stamina(),
                            ClientWings.stamina() + ServerConfig.GROUND_REGEN.get().floatValue() / 20f));
                }
            }
        }
        tickFury(player, stats);
        if (creative(player)) {
            ClientWings.setStamina(stats.stamina());
        }
    }

    private static void tickFury(LocalPlayer player, EffectiveStats stats) {
        boolean sprint = ModKeys.down(ModKeys.ACCELERATE);
        boolean sprintPressed = sprint && !sprintWasDown;
        sprintWasDown = sprint;
        if (furyCooldown > 0) {
            furyCooldown--;
        }
        if (!FurySet.isFury(ClientWings.equipped()) || mode != FlightMode.TRAVEL) {
            cancelFury();
            return;
        }
        if (furyWindow > 0) {
            if (sprintPressed) {
                furyDash(player, stats);
            } else if (--furyWindow == 0) {
                cancelFury();
                furyCooldown = FurySet.DASH_COOLDOWN_TICKS;
            }
            return;
        }
        if (furyCooldown > 0) {
            return;
        }
        if (ModKeys.brakeDown()) {
            if (furyCharge == 0) {
                furyCharging = true;
                PacketDistributor.sendToServer(new Payloads.FuryCharge(true));
            }
            if (++furyCharge >= FurySet.CHARGE_TICKS) {
                furyWindow = FurySet.DASH_WINDOW_TICKS;
            }
        } else if (furyCharge > 0) {
            cancelFury();
        }
    }

    private static void cancelFury() {
        if (furyCharging) {
            PacketDistributor.sendToServer(new Payloads.FuryCharge(false));
        }
        furyCharging = false;
        furyCharge = 0;
        furyWindow = 0;
    }

    private static void furyDash(LocalPlayer player, EffectiveStats stats) {
        furyCooldown = FurySet.DASH_COOLDOWN_TICKS;
        furyCharging = false;
        furyCharge = 0;
        furyWindow = 0;
        heading = player.getYRot();
        pitch = (float) FlightMath.clamp(player.getXRot(), -80, 85);
        prevHeading = heading;
        prevPitch = pitch;
        aimYaw = heading;
        aimPitch = pitch;
        yawRate = 0;
        pitchRate = 0;
        stalled = false;
        stallVy = 0;
        speed = ServerConfig.TERMINAL_VELOCITY.get();
        FuryEffects.dashLocal(player);
        PacketDistributor.sendToServer(new Payloads.FuryDash());
    }

    private static boolean canFly(LocalPlayer player) {
        return player.isAlive() && !player.isSpectator() && !player.isPassenger() && !player.isSleeping()
                && !player.getAbilities().flying && !player.isInWater() && !player.isInLava();
    }

    private static float airJumpCost() {
        return ServerConfig.AIR_JUMP_COST.get().floatValue();
    }

    private static void handleJump(LocalPlayer player, EffectiveStats stats) {
        boolean doubleTap = ticksSinceJump <= ClientConfig.DOUBLE_TAP_TICKS.get();
        ticksSinceJump = 0;
        if (player.getAbilities().mayfly || !canFly(player)) {
            return;
        }
        boolean creative = creative(player);
        if (mode == FlightMode.NONE && !player.onGround()) {
            if (doubleTap && (creative || ClientWings.stamina() > 0)) {
                if (ticksSinceAirJump <= AIR_JUMP_REFUND_TICKS && !creative) {
                    ClientWings.setStamina(Math.min(stats.stamina(), ClientWings.stamina() + airJumpCost()));
                }
                ticksSinceAirJump = 100;
                initStabilized(player, false);
                setMode(player, FlightMode.STABILIZED, true);
                ticksSinceJump = 100;
            } else if (creative || ClientWings.stamina() >= airJumpCost()) {
                airJump(player, stats);
            } else if (ClientWings.rescueJumps() > 0) {
                rescueJump(player);
            }
        } else if (mode == FlightMode.STABILIZED && doubleTap) {
            setMode(player, FlightMode.NONE, true);
            ticksSinceJump = 100;
        }
    }

    private static void airJump(LocalPlayer player, EffectiveStats stats) {
        Vec3 motion = player.getDeltaMovement();
        double jump = (ServerConfig.AIR_JUMP_BASE.get() + stats.beatGain() * ServerConfig.AIR_JUMP_PER_BEAT.get()) * DT;
        player.setDeltaMovement(motion.x, Math.max(motion.y, jump), motion.z);
        player.resetFallDistance();
        spend(player, airJumpCost());
        ticksSinceAirJump = 0;
        WingVisuals.local().animator.flap(look(stats).flapRate() * 1.5f);
        WingEffects.strike(player, style(stats), FlightMode.NONE);
        WingSounds.play(player, lookId(), true, 0.6f, 1.1f);
        PacketDistributor.sendToServer(new Payloads.AirJump());
    }

    private static void rescueJump(LocalPlayer player) {
        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x, 0, motion.z);
        player.resetFallDistance();
        ClientWings.setRescueJumps(ClientWings.rescueJumps() - 1);
        WingVisuals.local().animator.flap(3f);
        WingSounds.play(player, lookId(), false, 0.6f, 1.3f);
        PacketDistributor.sendToServer(new Payloads.RescueJump());
    }

    private static void shareCharge() {
        boolean charging = chargeTicks > 0;
        if (charging != chargeShared) {
            chargeShared = charging;
            PacketDistributor.sendToServer(new Payloads.TakeoffCharge(charging));
        }
    }

    private static void handleTravelKey(LocalPlayer player, EffectiveStats stats) {
        handleTravelKeyInner(player, stats);
        shareCharge();
    }

    private static void handleTravelKeyInner(LocalPlayer player, EffectiveStats stats) {
        if (travelKeyLock > 0) {
            travelKeyLock--;
            while (ModKeys.TRAVEL.consumeClick()) {
            }
            chargeTicks = 0;
            return;
        }
        if (mode == FlightMode.NONE && player.onGround()) {
            while (ModKeys.TRAVEL.consumeClick()) {
            }
            boolean affordable = creative(player) || ClientWings.stamina() >= flapCost();
            if (ModKeys.down(ModKeys.TRAVEL) && canFly(player) && affordable) {
                if (++chargeTicks >= ServerConfig.TAKEOFF_CHARGE_TICKS.get()) {
                    chargeTicks = 0;
                    takeoff(player, stats);
                }
            } else {
                chargeTicks = 0;
            }
            return;
        }
        chargeTicks = 0;
        while (ModKeys.TRAVEL.consumeClick()) {
            toggleTravel(player, stats);
        }
    }

    private static void takeoff(LocalPlayer player, EffectiveStats stats) {
        initTravel(player, stats);
        pitch = (float) -ServerConfig.TAKEOFF_PITCH.get().doubleValue();
        prevPitch = pitch;
        aimPitch = pitch;
        spend(player, flapCost());
        flapCooldown = 0;
        ticksSinceFlap = 0;
        WingVisuals.local().animator.strike(look(stats).flapRate(), style(stats).strikeAt());
        WingEffects.strike(player, style(stats), FlightMode.TRAVEL);
        WingSounds.layer(player, lookId(), 0.7f);
        travelKeyLock = TAKEOFF_KEY_LOCK_TICKS;
        setMode(player, FlightMode.TRAVEL, true);
    }

    private static void toggleTravel(LocalPlayer player, EffectiveStats stats) {
        if (!canFly(player)) {
            return;
        }
        if (mode == FlightMode.TRAVEL) {
            if (creative(player) || ClientWings.stamina() > 0) {
                stopTravel(player);
            }
            return;
        }
        if (!creative(player) && ClientWings.stamina() < flapCost()) {
            return;
        }
        initTravel(player, stats);
        spend(player, flapCost());
        flapCooldown = 0;
        ticksSinceFlap = 0;
        WingVisuals.local().animator.strike(look(stats).flapRate(), style(stats).strikeAt());
        WingEffects.strike(player, style(stats), FlightMode.TRAVEL);
        WingSounds.play(player, lookId(), true, 0.7f, 1f);
        setMode(player, FlightMode.TRAVEL, true);
    }

    private static void stopTravel(LocalPlayer player) {
        WingVisuals.local().animator.flap(3f);
        WingSounds.play(player, lookId(), false, 0.8f, 0.9f);
        initStabilized(player, true);
        setMode(player, FlightMode.STABILIZED, true);
    }

    private static int flapInterval(EffectiveStats stats) {
        return Math.max(1, Math.round(20f / Math.max(0.1f, stats.flapRate())));
    }

    private static boolean transmog(EffectiveStats stats) {
        return ClientWings.appearance().filter(id -> !id.equals(stats.wing())).isPresent();
    }

    private static WingDefinition look(EffectiveStats stats) {
        Minecraft mc = Minecraft.getInstance();
        return ClientWings.appearance().flatMap(id -> WingRegistries.get(mc.level.registryAccess(), id))
                .orElse(stats.definition());
    }

    private static void land(LocalPlayer player, double speed) {
        boolean hard = speed > 10;
        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(),
                hard ? ModSounds.LANDING_HARD.get() : ModSounds.LANDING_SOFT.get(), SoundSource.PLAYERS,
                hard ? (float) Math.min(1.0, 0.5 + speed / 60) : 0.6f, 0.9f + player.getRandom().nextFloat() * 0.2f, false);
        WingSounds.play(player, lookId(), false, hard ? 0.5f : 0.35f, 0.8f);
    }

    private static Optional<ResourceLocation> lookId() {
        return ClientWings.appearance().or(() -> ClientWings.stats().map(EffectiveStats::wing));
    }

    private static WingStyle style(EffectiveStats stats) {
        return WingStyle.of(look(stats).model());
    }

    private static void initStabilized(LocalPlayer player, boolean stopped) {
        double v = ClientWings.stats().map(EffectiveStats::hoverSpeed).orElse(8f);
        Vec3 motion = player.getDeltaMovement().scale(20);
        stabVel = stopped ? Vec3.ZERO : new Vec3(motion.x, Mth.clamp(motion.y, -v, v), motion.z);
        drift = Vec3.ZERO;
        buffet = Vec3.ZERO;
        buffetTarget = Vec3.ZERO;
        holdH = 0;
        holdV = 0;
    }

    private static void initTravel(LocalPlayer player, EffectiveStats stats) {
        heading = player.getYRot();
        pitch = player.onGround() ? -15f : (float) FlightMath.clamp(player.getXRot(), -35, 35);
        prevHeading = heading;
        prevPitch = pitch;
        aimYaw = heading;
        aimPitch = pitch;
        double launch = Math.max(stats.minSpeed(), stats.maxSpeed() * ServerConfig.LAUNCH_SPEED_FRACTION.get());
        speed = Math.max(launch, player.getDeltaMovement().length() * 20);
        stallVy = 0;
        stalled = false;
        braking = false;
        thrusting = false;
        pendingStrike = -1;
        flapCooldown = 0;
        yawRate = 0;
        pitchRate = 0;
        bank = 0;
        prevBank = 0;
    }

    private static void tickStabilized(LocalPlayer player, EffectiveStats stats) {
        Options o = Minecraft.getInstance().options;
        Options keys = Minecraft.getInstance().options;
        double forward = (keys.keyUp.isDown() ? 1 : 0) - (keys.keyDown.isDown() ? 1 : 0);
        double strafe = (keys.keyLeft.isDown() ? 1 : 0) - (keys.keyRight.isDown() ? 1 : 0);
        double vertical = (keys.keyJump.isDown() ? 1 : 0) - (keys.keyShift.isDown() ? 1 : 0);
        double s = stats.stabilization();
        double ramp = ServerConfig.STABILIZED_RAMP_TICKS.get() + (10 - s) * ServerConfig.STABILIZED_RAMP_TICKS_PER_POINT.get();
        ClientConfig.RampCurve curve = ClientConfig.STABILIZED_CURVE.get();
        double hSpeed = stats.hoverSpeed();
        double vSpeed = stats.hoverSpeed();

        double yaw = Math.toRadians(player.getYRot());
        Vec3 fwd = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
        Vec3 left = new Vec3(Math.cos(yaw), 0, Math.sin(yaw));
        Vec3 horizontal = new Vec3(stabVel.x, 0, stabVel.z);
        if (forward != 0 || strafe != 0) {
            holdH++;
            Vec3 dir = fwd.scale(forward).add(left.scale(strafe)).normalize();
            double target = hSpeed * curve.apply(holdH / ramp);
            horizontal = horizontal.add(dir.scale(target).subtract(horizontal).scale(0.5));
        } else {
            holdH = 0;
            horizontal = horizontal.scale(0.6);
        }
        double vy = stabVel.y;
        if (vertical != 0) {
            holdV++;
            double target = vertical * vSpeed * curve.apply(holdV / ramp);
            vy += (target - vy) * 0.5;
        } else {
            holdV = 0;
            vy *= 0.6;
        }
        stabVel = new Vec3(horizontal.x, vy, horizontal.z);

        double amp = (10 - s) / 10;
        double hoverRate = stats.hoverRate();
        boolean continuous = hoverRate >= WingDefinition.CONTINUOUS_FLAP_RATE;
        phase += 2 * Math.PI * hoverRate * DT;
        swayPhase += Math.PI * hoverRate * DT;
        if (swayPhase >= 2 * Math.PI) {
            swayPhase -= 2 * Math.PI;
        }
        if (phase >= 2 * Math.PI) {
            phase -= 2 * Math.PI;
            if (!continuous) {
                double angle = RANDOM.nextDouble() * 2 * Math.PI;
                double kick = amp * ServerConfig.DRIFT_AMPLITUDE.get() * Math.min(1, 2 / hoverRate);
                drift = drift.add(Math.cos(angle) * kick, 0, Math.sin(angle) * kick);
            }
        }
        drift = drift.scale(0.9);
        if (!continuous && --hoverSoundTicks <= 0) {
            hoverSoundTicks = Math.max(6, Math.round(20f / (float) hoverRate * (0.8f + RANDOM.nextFloat() * 0.5f)));
            WingSounds.soft(player, lookId(), 0.12f);
        }
        Vec3 sway = Vec3.ZERO;
        double swing = amp * ServerConfig.PERTURBATION_SWING.get();
        if (continuous) {
            if (RANDOM.nextInt(BUFFET_TICKS) == 0) {
                double speed = swing * 2 * Math.PI * 20.0 / (BUFFET_TICKS * 2);
                buffetTarget = new Vec3(RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian()).scale(speed);
            }
            buffet = buffet.add(buffetTarget.subtract(buffet).scale(0.15));
            sway = buffet;
        } else {
            double omega = 2 * Math.PI * hoverRate;
            double heave = swing * omega * Math.cos(phase);
            double surge = 0.6 * swing * omega * 0.5 * Math.cos(swayPhase);
            sway = fwd.scale(surge).add(0, heave, 0);
        }
        Vec3 out = stabVel.add(drift).add(sway);
        player.setDeltaMovement(out.scale(DT));
        lastVelocity = out;

        spend(player, ServerConfig.STABILIZED_DRAIN.get().floatValue() / 20f);
        if (!creative(player) && ClientWings.stamina() <= 0) {
            setMode(player, FlightMode.NONE, true);
        }
    }

    private static void tickTravel(LocalPlayer player, EffectiveStats stats) {
        Options o = Minecraft.getInstance().options;
        boolean creative = creative(player);
        double g = ServerConfig.GRAVITY.get();
        double vTerm = ServerConfig.TERMINAL_VELOCITY.get();
        double vMin = stats.minSpeed();
        double vMax = Math.max(stats.maxSpeed(), vMin + 1);
        double accelStat = stats.acceleration();
        cruiseMax = vMax;

        double steer = ModKeys.brakeDown() && speed > 0 ? BRAKE_STEERING : 1;
        double pitchIn = ModKeys.axis(ModKeys.DOWN, ModKeys.UP) * steer;
        double yawIn = ModKeys.axis(ModKeys.RIGHT, ModKeys.LEFT) * steer;
        float handling = stats.handling();
        aimYaw += (float) (yawIn * handling * DT);
        aimPitch = (float) FlightMath.clamp(aimPitch + pitchIn * handling * DT, -80, 85);
        prevHeading = heading;
        prevPitch = pitch;
        double omega = 3.0 + handling / 15.0;
        double h = DT / TURN_SUBSTEPS;
        for (int i = 0; i < TURN_SUBSTEPS; i++) {
            yawRate += (float) ((omega * omega * (aimYaw - heading) - 2 * omega * yawRate) * h);
            heading += (float) (yawRate * h);
            pitchRate += (float) ((omega * omega * (aimPitch - pitch) - 2 * omega * pitchRate) * h);
            pitch += (float) (pitchRate * h);
        }
        pitch = (float) FlightMath.clamp(pitch, -80, 85);

        double sin = Math.sin(Math.toRadians(pitch));
        double vCap = vMax + (vTerm - vMax) * Math.max(0, sin);
        double glideSpeed = ServerConfig.GLIDE_DRAG_SPEED.get();
        double diveSpeed = ServerConfig.DIVE_DRAG_SPEED.get();
        double glideDrag = g / (glideSpeed * glideSpeed);
        double diveDrag = g / (diveSpeed * diveSpeed);
        double drag = (glideDrag + (diveDrag - glideDrag) * Math.max(0, sin)) * ServerConfig.AIR_DRAG.get()
                * stats.drag();

        boolean sprint = ModKeys.accelerating();
        braking = ModKeys.brakeDown() && speed > 0;
        tucked = sprint && !braking && speed > vMax && pitch >= ServerConfig.TUCK_MIN_PITCH.get();
        double dragFactor = tucked ? ServerConfig.TUCK_DRAG_FACTOR.get() : 1.0;
        double overspeed = ServerConfig.OVERSPEED_FRICTION.get() * (tucked ? 5.0 : 1.0);
        double accel = g * sin - drag * dragFactor * speed * speed - overspeed * Math.max(0, speed - vCap);
        if (braking) {
            accel -= accelStat * ServerConfig.BRAKE_FACTOR.get();
        }
        ticksSinceFlap++;
        if (flapCooldown > 0) {
            flapCooldown--;
        }
        WingDefinition definition = stats.definition();
        boolean flapUseful = speed < vMax;
        boolean wantsBeat = sprint && flapUseful && !braking;
        float beatCost = definition.beatCost(flapCost());
        thrusting = false;
        if (definition.continuous()) {
            if (wantsBeat && (creative || ClientWings.stamina() > 0)) {
                thrusting = true;
                double ratio = speed / vCap;
                speed += accelStat * DT * (1 - ratio * ratio * ratio);
                spend(player, definition.continuousCostPerSecond(flapCost()) * (float) DT);
                ticksSinceFlap = 0;
            }
        } else {
            if (pendingStrike > 0) {
                pendingStrike--;
            }
            if (pendingStrike == 0) {
                pendingStrike = -1;
                if (!braking && speed < vMax && (creative || ClientWings.stamina() >= beatCost)) {
                    double ratio = speed / vCap;
                    speed += stats.beatGain() * (1 - ratio * ratio * ratio);
                    spend(player, beatCost);
                    ticksSinceFlap = 0;
                    WingSounds.play(player, lookId(), false, 0.7f, 1f);
                    WingEffects.strike(player, style(stats), FlightMode.TRAVEL);
                    PacketDistributor.sendToServer(new Payloads.Flap());
                }
            }
            if (wantsBeat && flapCooldown <= 0 && pendingStrike < 0 && (creative || ClientWings.stamina() >= beatCost)) {
                int duration = flapInterval(stats);
                flapCooldown = duration;
                pendingStrike = Math.max(1, Math.round(duration * style(stats).strikeAt()));
                if (transmog(stats)) {
                    WingVisuals.local().animator.beat(look(stats).flapRate(), duration + 2);
                } else {
                    WingVisuals.local().animator.flap(definition.flapRate());
                }
            }
        }
        if (!thrusting && !creative && ticksSinceFlap > 10 && !braking) {
            ClientWings.setStamina(Math.min(stats.stamina(),
                    ClientWings.stamina() + ServerConfig.GLIDE_REGEN.get().floatValue() / 20f));
        }
        speed = Math.max(0, speed + accel * DT);

        double stopSpeed = vMin * ServerConfig.STOP_SPEED_FACTOR.get();
        boolean noStamina = !creative && ClientWings.stamina() <= 0;
        double sink = 0;
        if (braking && speed <= stopSpeed && (furyCharge > 0 || furyWindow > 0)) {
            speed = stopSpeed;
        } else if (braking && speed <= stopSpeed) {
            if (!noStamina) {
                lastVelocity = Vec3.ZERO;
                player.setDeltaMovement(Vec3.ZERO);
                stopTravel(player);
                return;
            }
            speed = stopSpeed;
            sink = -ServerConfig.ZERO_STAMINA_SINK_SPEED.get();
        }
        if (!braking && speed < vMin) {
            stalled = true;
            stallVy = Math.max(-vTerm, stallVy - g * DT);
        } else if (stalled) {
            stallVy *= 0.8;
            if (Math.abs(stallVy) < 0.3 && speed >= vMin) {
                stallVy = 0;
                stalled = false;
            }
        }

        Vec3 velocity = FlightMath.look(heading, pitch).scale(speed).add(0, stallVy + sink, 0);
        player.setDeltaMovement(velocity.scale(DT));
        lastVelocity = velocity;

        int nextFlags = (braking ? FlightMode.FLAG_BRAKE : 0) | (stalled ? FlightMode.FLAG_STALL : 0)
                | (tucked ? FlightMode.FLAG_TUCK : 0) | (thrusting ? FlightMode.FLAG_THRUST : 0)
                | (braking && speed < vMin ? FlightMode.FLAG_SLOW : 0);
        if (nextFlags != flags) {
            flags = nextFlags;
            PacketDistributor.sendToServer(new Payloads.RequestMode(FlightMode.TRAVEL.ordinal(), flags));
        }
        prevBank = bank;
        float targetBank = (float) FlightMath.clamp(yawRate * 0.5, -45, 45);
        bank += (targetBank - bank) * 0.25f;
    }

    static void postTick(LocalPlayer player) {
        switch (mode) {
            case TRAVEL -> {
                checkImpact(player);
                player.setYRot(heading);
                player.yRotO = prevHeading;
                player.setXRot(pitch);
                player.xRotO = prevPitch;
                player.yBodyRot = heading;
                player.yBodyRotO = prevHeading;
                player.yHeadRot = heading;
                player.yHeadRotO = prevHeading;
                if (player.onGround() || player.verticalCollisionBelow) {
                    land(player, lastVelocity.length());
                    setMode(player, FlightMode.NONE, true);
                } else if (player.horizontalCollision) {
                    Vec3 after = player.getDeltaMovement();
                    double lostX = after.x == 0 ? lastVelocity.x : 0;
                    double lostZ = after.z == 0 ? lastVelocity.z : 0;
                    speed = lastVelocity.subtract(lostX, 0, lostZ).length();
                }
            }
            case STABILIZED -> {
                boolean descending = !Minecraft.getInstance().options.keyJump.isDown();
                if (player.onGround() && descending && lastVelocity.y <= 0.05) {
                    land(player, 0);
                    setMode(player, FlightMode.NONE, true);
                }
            }
            default -> {
            }
        }
        if (player.onGround() && mode == FlightMode.NONE) {
            ClientWings.setRescueJumps(ServerConfig.RESCUE_JUMPS.get());
        }
        FlightCamera.tick(player);
    }

    private ClientFlight() {
    }
}
