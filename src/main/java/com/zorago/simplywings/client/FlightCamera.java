package com.zorago.simplywings.client;

import com.zorago.simplywings.config.ClientConfig;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.client.event.CalculatePlayerTurnEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public final class FlightCamera {
    private enum Phase { OFF, FREEZE, CATCH_UP, FOLLOW, INTO_HEAD }

    private static Phase phase = Phase.OFF;
    private static CameraType saved = CameraType.FIRST_PERSON;
    private static float camYaw;
    private static float camPitch;
    private static Vec3 frozen = Vec3.ZERO;
    private static Vec3 rel = Vec3.ZERO;
    private static Vec3 prevRel = Vec3.ZERO;
    private static Vec3 relVel = Vec3.ZERO;
    private static int ticks;
    private static float headStart;
    private static float headDistance;
    private static float prevHeadDistance;
    private static float frameDistance = 4f;

    static void start(LocalPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        if (phase == Phase.OFF) {
            saved = mc.options.getCameraType();
        }
        camYaw = player.getViewYRot(1f);
        camPitch = player.getViewXRot(1f);
        frozen = player.getEyePosition();
        rel = Vec3.ZERO;
        prevRel = Vec3.ZERO;
        relVel = Vec3.ZERO;
        ticks = 0;
        phase = ClientConfig.FREEZE_TICKS.get() > 0 ? Phase.FREEZE : Phase.CATCH_UP;
        mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
    }

    static void intoHead(LocalPlayer player) {
        if (phase == Phase.OFF || phase == Phase.INTO_HEAD) {
            return;
        }
        alignPlayer(player);
        headStart = (float) (phase == Phase.FOLLOW ? distance() : rel.length());
        headDistance = headStart;
        prevHeadDistance = headStart;
        ticks = 0;
        phase = Phase.INTO_HEAD;
    }

    static void stop(LocalPlayer player) {
        if (phase == Phase.OFF) {
            return;
        }
        if (phase != Phase.INTO_HEAD) {
            alignPlayer(player);
        }
        restore();
    }

    static void reset() {
        if (phase != Phase.OFF) {
            restore();
        }
    }

    private static void alignPlayer(LocalPlayer player) {
        player.setYRot(camYaw);
        player.yRotO = camYaw;
        player.setXRot(camPitch);
        player.xRotO = camPitch;
        player.yHeadRot = camYaw;
        player.yHeadRotO = camYaw;
    }

    private static void restore() {
        Minecraft.getInstance().options.setCameraType(saved);
        phase = Phase.OFF;
    }

    private static double distance() {
        return ClientConfig.CAMERA_DISTANCE.get();
    }

    static boolean decoupled() {
        return phase == Phase.FREEZE || phase == Phase.CATCH_UP || phase == Phase.FOLLOW;
    }

    static void tick(LocalPlayer player) {
        if (phase == Phase.OFF) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (phase != Phase.INTO_HEAD && mc.options.getCameraType() != CameraType.THIRD_PERSON_BACK) {
            mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }
        switch (phase) {
            case FREEZE -> {
                prevRel = rel;
                rel = frozen.subtract(player.getEyePosition());
                if (++ticks >= ClientConfig.FREEZE_TICKS.get()) {
                    relVel = rel.subtract(prevRel).scale(20);
                    ticks = 0;
                    phase = Phase.CATCH_UP;
                }
            }
            case CATCH_UP -> {
                prevRel = rel;
                Vec3 target = FlightMath.look(camYaw, camPitch).scale(-distance());
                double omega = 4.6 / (ClientConfig.CATCH_UP_TICKS.get() * 0.05);
                double dt = 0.05;
                Vec3 accel = target.subtract(rel).scale(omega * omega).subtract(relVel.scale(2 * omega));
                relVel = relVel.add(accel.scale(dt));
                rel = rel.add(relVel.scale(dt));
                if (++ticks >= ClientConfig.CATCH_UP_TICKS.get() * 2 || rel.distanceTo(target) < 0.05) {
                    phase = Phase.FOLLOW;
                }
            }
            case INTO_HEAD -> {
                prevHeadDistance = headDistance;
                double t = ++ticks / (double) ClientConfig.INTO_HEAD_TICKS.get();
                headDistance = (float) (headStart * (1 - Math.min(1, t * t)));
                if (t >= 1) {
                    restore();
                }
            }
            default -> {
            }
        }
    }

    static void onComputeAngles(ViewportEvent.ComputeCameraAngles event) {
        if (!decoupled()) {
            return;
        }
        float pt = (float) event.getPartialTick();
        if (phase == Phase.FOLLOW) {
            event.setYaw(camYaw);
            event.setPitch(camPitch);
            frameDistance = (float) distance();
        } else {
            Vec3 r = prevRel.lerp(rel, pt);
            double len = r.length();
            if (len < 0.05) {
                event.setYaw(camYaw);
                event.setPitch(camPitch);
                frameDistance = 0.05f;
            } else {
                Vec3 dir = r.scale(-1 / len);
                event.setYaw(FlightMath.yawOf(dir));
                event.setPitch(FlightMath.pitchOf(dir));
                frameDistance = (float) len;
            }
        }
        event.setRoll(ClientFlight.bank(pt) * ClientConfig.CAMERA_ROLL.get().floatValue());
    }

    static void onDistance(CalculateDetachedCameraDistanceEvent event) {
        if (phase == Phase.OFF) {
            return;
        }
        if (phase == Phase.INTO_HEAD) {
            float pt = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
            event.setDistance(Math.max(0.01f, Mth.lerp(pt, prevHeadDistance, headDistance)));
        } else {
            event.setDistance(frameDistance);
        }
    }

    static void onTurn(CalculatePlayerTurnEvent event) {
        if (!decoupled()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        MouseHandler mouse = mc.mouseHandler;
        double d = event.getMouseSensitivity() * 0.6 + 0.2;
        double factor = d * d * d * 8.0 * 0.15;
        int invert = mc.options.invertYMouse().get() ? -1 : 1;
        camYaw += (float) (mouse.getXVelocity() * factor);
        camPitch = Mth.clamp(camPitch + (float) (mouse.getYVelocity() * factor * invert), -90f, 90f);
        event.setMouseSensitivity(-1.0 / 3.0);
        event.setCinematicCameraEnabled(false);
    }

    private FlightCamera() {
    }
}
