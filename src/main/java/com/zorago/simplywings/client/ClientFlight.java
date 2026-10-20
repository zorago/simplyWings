package com.zorago.simplywings.client;

import com.zorago.simplywings.config.ClientConfig;
import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.data.EffectiveStats;
import com.zorago.simplywings.network.Payloads;
import com.zorago.simplywings.player.FlightMode;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ClientFlight {
    private static final double DT = 0.05;
    private static final int AIR_JUMP_REFUND_TICKS = 6;
    private static final int TAKEOFF_KEY_LOCK_TICKS = 30;
    private static final int TURN_SUBSTEPS = 4;
    private static final RandomSource RANDOM = RandomSource.create();

    private static FlightMode mode = FlightMode.NONE;
    private static int flags;

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
    private static int flapCooldown;
    private static int ticksSinceFlap;
    private static boolean stalled;
    private static boolean braking;
    private static float bank;
    private static float prevBank;

    private static Vec3 stabVel = Vec3.ZERO;
    private static Vec3 drift = Vec3.ZERO;
    private static int holdH;
    private static int holdV;
    private static double phase;

    private static Vec3 lastVelocity = Vec3.ZERO;
    private static boolean jumpWasDown;
    private static int ticksSinceJump = 100;
    private static int ticksSinceAirJump = 100;
    private static int travelKeyLock;

    public static FlightMode mode() {
        return mode;
    }

    public static int flags() {
        return flags;
    }

    public static float takeoffProgress() {
        return chargeTicks / (float) ServerConfig.TAKEOFF_CHARGE_TICKS.get();
    }

    public static float bank(float partialTick) {
        return Mth.lerp(partialTick, prevBank, bank);
    }

    private static boolean creative(LocalPlayer player) {
        return player.getAbilities().instabuild;
    }

    private static float flapCost() {
        return ServerConfig.FLAP_COST.get().floatValue();
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
            case TRAVEL -> initTravel(player, ClientWings.stats().map(EffectiveStats::minSpeed).orElse(5f));
            default -> {
            }
        }
        setMode(player, next, false);
    }

    static void reset() {
        mode = FlightMode.NONE;
        flags = 0;
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
        if (creative(player)) {
            ClientWings.setStamina(stats.stamina());
        }
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
        player.setDeltaMovement(motion.x, Math.max(motion.y, player.getAttributeValue(Attributes.JUMP_STRENGTH) + 0.1), motion.z);
        player.resetFallDistance();
        spend(player, airJumpCost());
        ticksSinceAirJump = 0;
        WingVisuals.local().animator.flap(stats.flapRate() * 1.5f);
        player.playSound(SoundEvents.PHANTOM_FLAP, 0.7f, 1.2f);
        PacketDistributor.sendToServer(new Payloads.AirJump());
    }

    private static void rescueJump(LocalPlayer player) {
        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x, 0, motion.z);
        player.resetFallDistance();
        ClientWings.setRescueJumps(ClientWings.rescueJumps() - 1);
        WingVisuals.local().animator.flap(3f);
        player.playSound(SoundEvents.PHANTOM_FLAP, 0.8f, 1.4f);
        PacketDistributor.sendToServer(new Payloads.RescueJump());
    }

    private static void handleTravelKey(LocalPlayer player, EffectiveStats stats) {
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
            if (ModKeys.TRAVEL.isDown() && canFly(player) && affordable) {
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
        initTravel(player, stats.minSpeed());
        pitch = (float) -ServerConfig.TAKEOFF_PITCH.get().doubleValue();
        prevPitch = pitch;
        aimPitch = pitch;
        Vec3 motion = player.getDeltaMovement();
        double horizontal = Math.sqrt(motion.x * motion.x + motion.z * motion.z) * 20;
        speed = Math.max(stats.minSpeed() * ServerConfig.TAKEOFF_SPEED_FACTOR.get(), horizontal);
        spend(player, flapCost());
        flapCooldown = flapInterval(stats);
        ticksSinceFlap = 0;
        WingVisuals.local().animator.flap(stats.flapRate());
        player.playSound(SoundEvents.PHANTOM_FLAP, 1f, 0.9f);
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
        initTravel(player, stats.minSpeed());
        spend(player, flapCost());
        flapCooldown = flapInterval(stats);
        ticksSinceFlap = 0;
        WingVisuals.local().animator.flap(stats.flapRate());
        player.playSound(SoundEvents.PHANTOM_FLAP, 0.8f, 1.1f);
        setMode(player, FlightMode.TRAVEL, true);
    }

    private static void stopTravel(LocalPlayer player) {
        WingVisuals.local().animator.flap(3f);
        player.playSound(SoundEvents.PHANTOM_FLAP, 0.8f, 0.9f);
        initStabilized(player, true);
        setMode(player, FlightMode.STABILIZED, true);
    }

    private static int flapInterval(EffectiveStats stats) {
        return Math.max(2, Math.round(20f / Math.max(0.1f, stats.flapRate())));
    }

    private static void initStabilized(LocalPlayer player, boolean stopped) {
        double v = ClientWings.stats().map(EffectiveStats::hoverVerticalSpeed).orElse(5f);
        Vec3 motion = player.getDeltaMovement().scale(20);
        stabVel = stopped ? Vec3.ZERO : new Vec3(motion.x, Mth.clamp(motion.y, -v, v), motion.z);
        drift = Vec3.ZERO;
        holdH = 0;
        holdV = 0;
    }

    private static void initTravel(LocalPlayer player, float minSpeed) {
        heading = player.getYRot();
        pitch = player.onGround() ? -15f : (float) FlightMath.clamp(player.getXRot(), -35, 35);
        prevHeading = heading;
        prevPitch = pitch;
        aimYaw = heading;
        aimPitch = pitch;
        speed = Math.max(minSpeed, player.getDeltaMovement().length() * 20);
        stallVy = 0;
        stalled = false;
        braking = false;
        yawRate = 0;
        pitchRate = 0;
        bank = 0;
        prevBank = 0;
    }

    private static void tickStabilized(LocalPlayer player, EffectiveStats stats) {
        Options o = Minecraft.getInstance().options;
        double forward = (o.keyUp.isDown() ? 1 : 0) - (o.keyDown.isDown() ? 1 : 0);
        double strafe = (o.keyLeft.isDown() ? 1 : 0) - (o.keyRight.isDown() ? 1 : 0);
        double vertical = (o.keyJump.isDown() ? 1 : 0) - (o.keyShift.isDown() ? 1 : 0);
        double s = stats.stabilization();
        double ramp = ServerConfig.STABILIZED_RAMP_TICKS.get() + (10 - s) * ServerConfig.STABILIZED_RAMP_TICKS_PER_POINT.get();
        ClientConfig.RampCurve curve = ClientConfig.STABILIZED_CURVE.get();
        double hSpeed = stats.hoverSpeed();
        double vSpeed = stats.hoverVerticalSpeed();

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
        phase += 2 * Math.PI * stats.hoverRate() * DT;
        if (phase >= 2 * Math.PI) {
            phase -= 2 * Math.PI;
            double angle = RANDOM.nextDouble() * 2 * Math.PI;
            double kick = amp * ServerConfig.DRIFT_AMPLITUDE.get();
            drift = drift.add(Math.cos(angle) * kick, 0, Math.sin(angle) * kick);
        }
        drift = drift.scale(0.9);
        double oscillation = amp * ServerConfig.PERTURBATION_AMPLITUDE.get() * Math.cos(phase);
        Vec3 out = stabVel.add(drift).add(0, oscillation, 0);
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

        double invert = ClientConfig.INVERT_PITCH.get() ? -1 : 1;
        double pitchIn = ((o.keyUp.isDown() ? 1 : 0) - (o.keyDown.isDown() ? 1 : 0)) * invert;
        double yawIn = (o.keyRight.isDown() ? 1 : 0) - (o.keyLeft.isDown() ? 1 : 0);
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
        double drag = g / (vTerm * vTerm);
        double accel = g * sin - drag * speed * speed - ServerConfig.OVERSPEED_FRICTION.get() * Math.max(0, speed - vCap);

        braking = ModKeys.brakeDown() && speed > 0;
        if (braking) {
            accel -= accelStat * ServerConfig.BRAKE_FACTOR.get();
        }
        ticksSinceFlap++;
        if (flapCooldown > 0) {
            flapCooldown--;
        }
        if (o.keySprint.isDown() && !braking && flapCooldown <= 0 && (creative || ClientWings.stamina() >= flapCost())) {
            if (speed < vCap) {
                speed += accelStat * (1 - speed / vCap);
            }
            flapCooldown = flapInterval(stats);
            ticksSinceFlap = 0;
            spend(player, flapCost());
            WingVisuals.local().animator.flap(stats.flapRate());
            player.playSound(SoundEvents.PHANTOM_FLAP, 0.6f, 1.3f);
            PacketDistributor.sendToServer(new Payloads.Flap());
        } else if (!creative && ticksSinceFlap > 10 && !braking) {
            ClientWings.setStamina(Math.min(stats.stamina(),
                    ClientWings.stamina() + ServerConfig.GLIDE_REGEN.get().floatValue() / 20f));
        }
        speed = Math.max(0, speed + accel * DT);

        double stopSpeed = vMin * ServerConfig.STOP_SPEED_FACTOR.get();
        boolean noStamina = !creative && ClientWings.stamina() <= 0;
        double sink = 0;
        if (braking && speed <= stopSpeed) {
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

        int nextFlags = (braking ? FlightMode.FLAG_BRAKE : 0) | (stalled ? FlightMode.FLAG_STALL : 0);
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
                player.setYRot(heading);
                player.yRotO = prevHeading;
                player.setXRot(pitch);
                player.xRotO = prevPitch;
                player.yBodyRot = heading;
                player.yBodyRotO = prevHeading;
                player.yHeadRot = heading;
                player.yHeadRotO = prevHeading;
                if (player.onGround() || player.verticalCollisionBelow) {
                    PacketDistributor.sendToServer(new Payloads.Impact((float) Math.max(0, -lastVelocity.y)));
                    setMode(player, FlightMode.NONE, true);
                } else if (player.horizontalCollision) {
                    Vec3 after = player.getDeltaMovement();
                    double lostX = after.x == 0 ? lastVelocity.x : 0;
                    double lostZ = after.z == 0 ? lastVelocity.z : 0;
                    double impact = Math.sqrt(lostX * lostX + lostZ * lostZ);
                    if (impact > 0.5) {
                        PacketDistributor.sendToServer(new Payloads.Impact((float) impact));
                    }
                    speed = lastVelocity.subtract(lostX, 0, lostZ).length();
                }
            }
            case STABILIZED -> {
                boolean descending = !Minecraft.getInstance().options.keyJump.isDown();
                if (player.onGround() && descending && lastVelocity.y <= 0.05) {
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
