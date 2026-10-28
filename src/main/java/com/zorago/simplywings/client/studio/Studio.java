package com.zorago.simplywings.client.studio;

import com.mojang.blaze3d.platform.NativeImage;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.client.WingVisuals;
import com.zorago.simplywings.player.FlightMode;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.bus.api.EventPriority;

public final class Studio {
    private static final boolean ENABLED = Boolean.getBoolean("simplywings.studio");
    private static final Deque<String> QUEUE = new ArrayDeque<>();
    private static final List<String> STATES = List.of("ground", "charge", "air", "landing", "hover", "cruise", "dive",
            "brake", "tuck");

    private static boolean active;
    private static FlightMode mode = FlightMode.NONE;
    private static int forcedState = -1;
    private static float amount = 1f;
    private static boolean stall;
    private static float beat = -1f;
    private static float hover = Float.NaN;
    private static float time = Float.NaN;
    private static float rouse = -1f;
    private static int variant = -1;
    private static float camYaw = 25f;
    private static float camPitch = 10f;
    private static float camDistance = 4f;
    private static float camHeight = 1.1f;
    private static float bodyYaw;
    private static Vec3 anchor;
    private static ArmorStand eye;
    private static CameraType savedCamera;
    private static int wait;
    private static String pendingShot;
    private static int frames;

    public static boolean enabled() {
        return ENABLED;
    }

    public static void register(IEventBus game) {
        if (ENABLED) {
            game.addListener(Studio::onTick);
            game.addListener(Studio::onFrame);
            game.addListener(EventPriority.LOWEST, Studio::onAngles);
            game.addListener((net.neoforged.neoforge.client.event.ClientChatReceivedEvent e) -> StudioTest.chat(e, Studio::log));
            game.addListener((net.neoforged.neoforge.client.event.sound.PlaySoundEvent e) -> StudioTest.sound(e, Studio::log));
            SimplyWings.LOGGER.info("Wing studio enabled, reading {}", dir().resolve("commands.txt"));
        }
    }

    public static FlightMode mode(FlightMode fallback) {
        return active && forcedState >= 0 ? mode : fallback;
    }

    private static Path dir() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("studio");
    }

    private static void onTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        readCommands();
        StudioTest.tick();
        if (active) {
            hold(mc, player);
        }
        if (pendingShot != null) {
            if (wait > 0) {
                wait--;
            }
            return;
        }
        if (wait > 0) {
            wait--;
            return;
        }
        while (!QUEUE.isEmpty() && wait == 0 && pendingShot == null) {
            run(mc, player, QUEUE.poll());
        }
        if (QUEUE.isEmpty() && wait == 0 && pendingShot == null) {
            Path idle = dir().resolve("idle");
            if (!Files.exists(idle)) {
                write(idle, "idle");
            }
        }
    }

    private static void readCommands() {
        Path file = dir().resolve("commands.txt");
        if (!Files.exists(file)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            Files.delete(file);
            Files.deleteIfExists(dir().resolve("idle"));
            for (String line : lines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                    QUEUE.add(trimmed);
                }
            }
        } catch (IOException e) {
            SimplyWings.LOGGER.warn("Studio could not read {}", file, e);
        }
    }

    private static void run(Minecraft mc, LocalPlayer player, String line) {
        String[] a = line.split("\\s+");
        WingVisuals.Visual visual = WingVisuals.local();
        try {
            switch (a[0].toLowerCase(Locale.ROOT)) {
                case "begin" -> begin(mc, player);
                case "end" -> end(mc, player);
                case "wing" -> WingVisuals.setPreview(Optional.of(ResourceLocation.parse(a[1].contains(":") ? a[1] : SimplyWings.MOD_ID + ":" + a[1])));
                case "state" -> {
                    forcedState = STATES.indexOf(a[1]);
                    if (forcedState < 0) {
                        throw new IllegalArgumentException("Unknown state " + a[1]);
                    }
                    amount = a.length > 2 ? Float.parseFloat(a[2]) : 1f;
                    stall = a.length > 3 && a[3].equals("stall");
                    mode = switch (forcedState) {
                        case WingMotion.HOVER -> FlightMode.STABILIZED;
                        case WingMotion.CRUISE, WingMotion.DIVE, WingMotion.BRAKE, WingMotion.TUCK -> FlightMode.TRAVEL;
                        default -> FlightMode.NONE;
                    };
                }
                case "beat" -> beat = a[1].equals("off") ? -1f : Float.parseFloat(a[1]);
                case "hover" -> hover = a[1].equals("off") ? Float.NaN : Float.parseFloat(a[1]) * Mth.TWO_PI;
                case "time" -> time = a[1].equals("off") ? Float.NaN : Float.parseFloat(a[1]);
                case "rouse" -> {
                    rouse = a[1].equals("off") ? -1f : Float.parseFloat(a[1]);
                    variant = a.length > 2 ? Integer.parseInt(a[2]) : -1;
                }
                case "live" -> {
                    forcedState = -1;
                    beat = -1f;
                    hover = Float.NaN;
                    time = Float.NaN;
                    rouse = -1f;
                    variant = -1;
                    visual.animator.release();
                }
                case "cam" -> {
                    camYaw = Float.parseFloat(a[1]);
                    camPitch = Float.parseFloat(a[2]);
                    camDistance = Float.parseFloat(a[3]);
                    if (a.length > 4) {
                        camHeight = Float.parseFloat(a[4]);
                    }
                }
                case "cmd" -> player.connection.sendCommand(line.substring(4).trim());
                case "reload" -> {
                    mc.reloadResourcePacks();
                    wait = 40;
                }
                case "wait" -> wait = Integer.parseInt(a[1]);
                case "quit" -> mc.stop();
                case "mark" -> write(dir().resolve("log.txt"), "mark " + a[1]);
                case "shot" -> {
                    pendingShot = a[1];
                    wait = 4;
                    frames = 0;
                }
                case "newworld" -> {
                    StudioTest.run(mc, player, a, line, Studio::log);
                    wait = 200;
                }
                default -> {
                    if (!StudioTest.run(mc, player, a, line, Studio::log)) {
                        throw new IllegalArgumentException("Unknown command");
                    }
                }
            }
        } catch (RuntimeException e) {
            write(dir().resolve("errors.txt"), line + " -> " + e.getMessage());
        }
    }

    private static void begin(Minecraft mc, LocalPlayer player) {
        active = true;
        anchor = player.position();
        bodyYaw = 0f;
        savedCamera = mc.options.getCameraType();
        mc.options.setCameraType(CameraType.FIRST_PERSON);
        mc.options.hideGui = true;
        mc.options.pauseOnLostFocus = false;
        eye = new ArmorStand(EntityType.ARMOR_STAND, player.level());
        eye.setInvisible(true);
        mc.setCameraEntity(eye);
    }

    private static void end(Minecraft mc, LocalPlayer player) {
        active = false;
        forcedState = -1;
        WingVisuals.local().animator.release();
        WingVisuals.setPreview(null);
        mc.setCameraEntity(player);
        mc.options.hideGui = false;
        if (savedCamera != null) {
            mc.options.setCameraType(savedCamera);
        }
        eye = null;
    }

    private static void hold(Minecraft mc, LocalPlayer player) {
        if (mc.screen instanceof net.minecraft.client.gui.screens.PauseScreen) {
            mc.setScreen(null);
        }
        player.getAbilities().flying = true;
        player.setPos(anchor);
        player.setDeltaMovement(Vec3.ZERO);
        player.xo = anchor.x;
        player.yo = anchor.y;
        player.zo = anchor.z;
        player.setYRot(bodyYaw);
        player.yRotO = bodyYaw;
        player.setYBodyRot(bodyYaw);
        player.yBodyRotO = bodyYaw;
        player.setYHeadRot(bodyYaw);
        player.yHeadRotO = bodyYaw;
        player.setXRot(0f);
        player.xRotO = 0f;
        WingVisuals.Visual visual = WingVisuals.local();
        if (forcedState >= 0) {
            visual.animator.force(forcedState, amount, stall);
        }
        visual.animator.forceBeat(beat);
        visual.animator.forceHover(hover);
        visual.animator.forceTime(time);
        visual.animator.forceVariant(variant);
        if (rouse >= 0f) {
            visual.animator.forceRouse(rouse);
        }
        if (eye != null) {
            Vec3 center = anchor.add(0, camHeight, 0);
            float yaw = (bodyYaw + camYaw) * Mth.DEG_TO_RAD;
            float pitch = camPitch * Mth.DEG_TO_RAD;
            Vec3 dir = new Vec3(-Mth.sin(yaw) * Mth.cos(pitch), -Mth.sin(pitch), Mth.cos(yaw) * Mth.cos(pitch));
            Vec3 pos = center.subtract(dir.scale(camDistance)).subtract(0, eye.getEyeHeight(), 0);
            eye.setPos(pos);
            eye.xo = pos.x;
            eye.yo = pos.y;
            eye.zo = pos.z;
            eye.xOld = pos.x;
            eye.yOld = pos.y;
            eye.zOld = pos.z;
            eye.setYRot(bodyYaw + camYaw);
            eye.yRotO = eye.getYRot();
            eye.setXRot(camPitch);
            eye.xRotO = camPitch;
        }
    }

    private static void onAngles(ViewportEvent.ComputeCameraAngles event) {
        if (active && eye != null) {
            event.setYaw(bodyYaw + camYaw);
            event.setPitch(camPitch);
            event.setRoll(0f);
        }
    }

    private static void onFrame(RenderFrameEvent.Post event) {
        if (pendingShot == null || wait > 0 || ++frames < 4) {
            return;
        }
        capture(Minecraft.getInstance(), pendingShot);
        pendingShot = null;
    }

    private static void capture(Minecraft mc, String name) {
        Path out = dir().resolve("shots").resolve(name + ".png");
        try (NativeImage image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
            Files.createDirectories(out.getParent());
            image.writeToFile(out);
            write(dir().resolve("log.txt"), "shot " + name);
        } catch (IOException e) {
            write(dir().resolve("errors.txt"), "shot " + name + " -> " + e.getMessage());
        }
    }

    private static void log(String line) {
        write(dir().resolve("log.txt"), line);
    }

    private static void write(Path file, String line) {
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, line + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            SimplyWings.LOGGER.warn("Studio could not write {}", file, e);
        }
    }

    private Studio() {
    }
}
