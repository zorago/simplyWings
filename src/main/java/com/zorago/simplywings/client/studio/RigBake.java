package com.zorago.simplywings.client.studio;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.anim.WingStyle;
import com.zorago.simplywings.data.WingDefinition;
import com.zorago.simplywings.player.FlightMode;
import com.zorago.simplywings.render.FlightPose;
import com.zorago.simplywings.render.RigLoader;
import com.zorago.simplywings.render.rig.WingRig;
import com.zorago.simplywings.render.rig.WingRigs;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;

public final class RigBake {
    private static final int PREROLL = 80;
    private static final float[] SUBSTEPS = {0f, 0.5f};

    private final WingRig rig;
    private final WingStyle style;
    private final float flapRate;
    private final float hoverRate;
    private final String pair;
    private final boolean bench;
    private final Map<String, ModelPart> bones = new LinkedHashMap<>();
    private final PlayerModel<?> player;
    private final Map<String, ModelPart> body = new LinkedHashMap<>();

    private RigBake(String name, Path root, boolean bench) throws IOException {
        this.bench = bench;
        rig = WingRigs.byName(name);
        if (rig == null) {
            throw new IllegalArgumentException("Unknown rig " + name);
        }
        Path file = root.resolve("src/main/resources/assets/simplywings/rigs/" + name + ".bbmodel");
        RigLoader.Rig loaded;
        Path sheet = root.resolve("src/main/resources/assets/simplywings/rigs/" + name + ".membranes.json");
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
             Reader membranes = Files.exists(sheet) ? Files.newBufferedReader(sheet, StandardCharsets.UTF_8) : null) {
            loaded = RigLoader.parse(reader, membranes);
        }
        ModelPart model = loaded.layer().bakeRoot();
        rig.bind(model, loaded);
        for (Map.Entry<String, List<String>> entry : loaded.paths().entrySet()) {
            ModelPart part = model;
            for (String step : entry.getValue()) {
                part = part.getChild(step);
            }
            bones.put(entry.getKey(), part);
        }
        JsonObject definition = definition(root, name);
        flapRate = definition == null ? 2f : definition.get("flap_rate").getAsFloat();
        hoverRate = definition == null ? 1.2f : definition.get("hover_rate").getAsFloat();
        pair = definition == null ? null : definition.get("id").getAsString();
        style = WingStyle.of(SimplyWings.id(name));
        player = new PlayerModel<>(LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64).bakeRoot(), false);
        body.put("head", player.head);
        body.put("body", player.body);
        body.put("right_arm", player.rightArm);
        body.put("left_arm", player.leftArm);
        body.put("right_leg", player.rightLeg);
        body.put("left_leg", player.leftLeg);
    }

    private static List<JsonObject> definitions(Path root) throws IOException {
        Path dir = root.resolve("src/main/resources/data/simplywings/simplywings/wing");
        List<JsonObject> out = new ArrayList<>();
        try (Stream<Path> files = Files.list(dir)) {
            for (Path p : files.sorted().toList()) {
                JsonObject json = JsonParser.parseString(Files.readString(p)).getAsJsonObject();
                String file = p.getFileName().toString();
                json.addProperty("id", file.substring(0, file.length() - ".json".length()));
                out.add(json);
            }
        }
        return out;
    }

    private static JsonObject definition(Path root, String name) throws IOException {
        for (JsonObject json : definitions(root)) {
            if (json.get("model").getAsString().equals("simplywings:" + name)) {
                return json;
            }
        }
        return null;
    }

    private static final class Step {
        FlightMode mode = FlightMode.NONE;
        int state = WingMotion.GROUND;
        float walk;
        float charge;
        boolean beating;
        boolean stall;
        boolean slow;
        boolean launch;
        float dive;
        float pitch;
        float bank;
        boolean rouse;
        int variant;
        boolean airJump;
        boolean hard;
    }

    private final class Sim {
        final float[] weight = new float[WingMotion.COUNT];
        final float[] prevWeight = new float[WingMotion.COUNT];
        final float[] velocity = new float[WingMotion.COUNT];
        final RigMemory memory = new RigMemory();
        final WingMotion motion = new WingMotion();
        Step step = new Step();
        float stall, prevStall, dive, prevDive, bank, prevBank, pitch, prevPitch;
        float hoverWeight, prevHoverWeight, vibration, prevVibration, slowWeight, prevSlowWeight;
        double hoverPhase, prevHoverPhase;
        float phase, prevPhase, amp, prevAmp, ampVelocity;
        int rouseTicks, rouseLength = 1;
        int launchTicks;
        int age;

        Sim() {
            memory.claim(rig.name());
        }

        void enter(Step next, boolean snap) {
            if (next.beating && !step.beating && amp < 0.12f) {
                float shift = -(phase - Mth.floor(phase));
                phase += shift;
                prevPhase += shift;
            }
            if (next.mode != step.mode) {
                pitch = prevPitch = next.pitch;
            }
            if (next.launch) {
                launchTicks = com.zorago.simplywings.anim.WingAnimator.LAUNCH_TICKS;
            }
            if (next.rouse) {
                int[] lengths = rig.idleLengths();
                rouseLength = lengths[next.variant % lengths.length];
                rouseTicks = rouseLength;
            }
            step = next;
            if (snap) {
                float[] target = target();
                System.arraycopy(target, 0, weight, 0, WingMotion.COUNT);
                System.arraycopy(target, 0, prevWeight, 0, WingMotion.COUNT);
                java.util.Arrays.fill(velocity, 0f);
                stall = prevStall = next.stall ? 1f : 0f;
                slowWeight = prevSlowWeight = next.slow ? 1f : 0f;
                dive = prevDive = next.dive;
                bank = prevBank = next.bank;
                pitch = prevPitch = next.pitch;
            }
        }

        float[] target() {
            float[] target = new float[WingMotion.COUNT];
            if (step.state == WingMotion.GROUND || step.state == WingMotion.CHARGE) {
                float c = step.charge;
                c = c * c * (3f - 2f * c);
                target[WingMotion.GROUND] = 1f - c;
                target[WingMotion.CHARGE] = c;
            } else {
                target[step.state] = 1f;
            }
            return target;
        }

        void tick() {
            age++;
            float stiffness = 0.16f;
            float damping = 0.68f;
            if (step.state == WingMotion.BRAKE || step.state == WingMotion.TUCK) {
                stiffness = 0.22f;
                damping = 0.62f;
            }
            if (step.state == WingMotion.GROUND && step.charge <= 0f) {
                stiffness = 0.1f;
                damping = 0.72f;
            }
            float[] target = target();
            System.arraycopy(weight, 0, prevWeight, 0, WingMotion.COUNT);
            for (int i = 0; i < WingMotion.COUNT; i++) {
                velocity[i] = (velocity[i] + (target[i] - weight[i]) * stiffness) * damping;
                weight[i] += velocity[i];
            }
            prevStall = stall;
            prevDive = dive;
            prevBank = bank;
            prevPitch = pitch;
            stall += ((step.stall ? 1f : 0f) - stall) * 0.3f;
            prevSlowWeight = slowWeight;
            slowWeight += ((step.slow && step.state == WingMotion.BRAKE ? 1f : 0f) - slowWeight) * 0.25f;
            dive += (step.dive - dive) * 0.25f;
            bank += (step.bank - bank) * 0.25f;
            pitch += (step.pitch - pitch) * 0.5f;
            if (rouseTicks > 0) {
                rouseTicks--;
            }
            if (launchTicks > 0) {
                launchTicks--;
            }

            boolean hovering = step.mode == FlightMode.STABILIZED;
            boolean thrust = step.beating;
            prevHoverWeight = hoverWeight;
            prevVibration = vibration;
            boolean vibrating = style.vibration() > 0 && (hovering || thrust || (step.mode == FlightMode.TRAVEL
                    && step.state != WingMotion.TUCK));
            hoverWeight += ((hovering && style.hoverAmplitude() > 0 ? 1f : 0f) - hoverWeight) * 0.2f;
            vibration += ((vibrating ? (hovering || thrust ? 1f : 0.3f) : 0f) - vibration) * 0.3f;
            prevHoverPhase = hoverPhase;
            hoverPhase += Mth.TWO_PI * hoverRate / 20f;

            prevPhase = phase;
            prevAmp = amp;
            float cycle = Math.max(4f, 20f / Math.max(0.1f, flapRate));
            float stepSize = 1f / cycle;
            phase += stepSize * (thrust ? 1f : 0.35f + 0.65f * amp);
            float ampTarget = thrust ? 1f : 0f;
            ampVelocity = (ampVelocity + (ampTarget - amp) * (thrust ? 0.45f : 0.28f)) * (thrust ? 0.55f : 0.6f);
            amp = Mth.clamp(amp + ampVelocity, 0f, 1.08f);
            if (!thrust && amp < 0.002f) {
                amp = 0f;
                ampVelocity = 0f;
            }

            memory.begin();
            rig.tick(motion(1f), memory);
        }

        WingMotion motion(float pt) {
            WingMotion out = motion;
            out.time = age + pt;
            out.partialTick = pt;
            for (int i = 0; i < WingMotion.COUNT; i++) {
                out.w[i] = Mth.lerp(pt, prevWeight[i], weight[i]);
            }
            out.stall = Mth.lerp(pt, prevStall, stall);
            out.brakeBeat = Mth.lerp(pt, prevSlowWeight, slowWeight);
            out.diveDepth = Mth.lerp(pt, prevDive, dive);
            float hoverRaw = Mth.lerp(pt, prevHoverWeight, hoverWeight);
            out.hoverBeat = hoverRaw;
            out.hoverPhase = (float) Mth.lerp(pt, prevHoverPhase, hoverPhase);
            if (amp > 0f || prevAmp > 0f) {
                float b = Mth.lerp(pt, prevPhase, phase);
                out.beat = b - Mth.floor(b);
                out.beatWeight = Mth.lerp(pt, prevAmp, amp) * (1f - hoverRaw);
            } else {
                out.beat = -1f;
                out.beatWeight = 0f;
            }
            out.buzz = Mth.lerp(pt, prevVibration, vibration);
            out.bank = Mth.lerp(pt, prevBank, bank) * Mth.DEG_TO_RAD;
            out.launch = launchTicks > 0 ? Mth.clamp(1f - (launchTicks - pt) / com.zorago.simplywings.anim.WingAnimator.LAUNCH_TICKS, 0f, 1f) : -1f;
            out.rouse = 0f;
            out.rouseProgress = 0f;
            out.rouseVariant = step.variant;
            if (rouseTicks > 0) {
                out.rouseProgress = Mth.clamp(1f - (rouseTicks - pt) / rouseLength, 0f, 1f);
                out.rouse = Mth.sin((float) Math.PI * out.rouseProgress);
            }
            boolean travel = step.mode == FlightMode.TRAVEL;
            out.speed = travel ? (step.state == WingMotion.DIVE ? 25f : step.state == WingMotion.BRAKE ? 6f : 15f) : step.walk;
            out.climb = travel && step.state == WingMotion.DIVE ? -15f : 0f;
            out.flapRate = flapRate;
            out.hoverRate = hoverRate;
            out.upFraction = style.upFraction();
            out.downFraction = style.downFraction();
            out.idle = step.mode == FlightMode.NONE && (step.state == WingMotion.GROUND || step.state == WingMotion.CHARGE);
            return out;
        }
    }

    private record Segment(int ticks, Step step) {
    }

    private final class Cues {
        final JsonArray events = new JsonArray();
        final int interval = Math.max(1, Math.round(20f / Math.max(0.1f, flapRate)));
        final int strike = Math.max(1, Math.round(interval * style.strikeAt()));
        final boolean continuous = flapRate >= WingDefinition.CONTINUOUS_FLAP_RATE;
        Step step;
        int cooldown;
        int pending = -1;
        boolean buzzing;
        int hoverFrom;

        void add(float ticks, String type, float volume, float pitch) {
            JsonObject event = new JsonObject();
            event.addProperty("t", round(ticks / 20f));
            event.addProperty("type", type);
            if (volume > 0f) {
                event.addProperty("volume", volume);
                event.addProperty("pitch", pitch);
            }
            events.add(event);
        }

        JsonObject last() {
            return events.get(events.size() - 1).getAsJsonObject();
        }

        void enter(Step next, int tick) {
            Step prev = step;
            step = next;
            if (next.launch) {
                add(tick, "launch", 1.1f, 1f);
                add(tick, "strike_strong", 0.7f, 1f);
            } else if (prev != null && next.mode == FlightMode.TRAVEL && prev.mode == FlightMode.STABILIZED) {
                add(tick, "mode_travel", 0.7f, 1f);
                last().addProperty("strong", true);
            }
            if (prev != null && next.mode == FlightMode.STABILIZED && prev.mode == FlightMode.TRAVEL) {
                add(tick, "mode_hover", 0.8f, 0.9f);
            }
            if (next.mode == FlightMode.STABILIZED && (prev == null || prev.mode != FlightMode.STABILIZED)) {
                hoverFrom = tick;
            }
            if (next.airJump) {
                add(tick, "air_jump", 0.6f, 1.1f);
                last().addProperty("strong", true);
            }
            if (next.state == WingMotion.LANDING && (prev == null || prev.state != WingMotion.LANDING)) {
                add(tick, next.hard ? "land_hard" : "land_soft", next.hard ? 0.75f : 0.6f, 1f);
                last().addProperty("flap_volume", next.hard ? 0.5f : 0.35f);
                last().addProperty("flap_pitch", 0.8f);
            }
            if (next.state == WingMotion.TUCK && (prev == null || prev.state != WingMotion.TUCK)) {
                add(tick, "tuck", 0f, 1f);
            }
            if (next.state == WingMotion.BRAKE && (prev == null || prev.state != WingMotion.BRAKE)) {
                add(tick, "brake", 0f, 1f);
            }
            if (next.stall && (prev == null || !prev.stall)) {
                add(tick, "stall", 0f, 1f);
            }
            if (next.rouse) {
                add(tick, "rouse", 0f, 1f);
                last().addProperty("variant", next.variant);
            }
            if (continuous && next.beating != buzzing) {
                buzzing = next.beating;
                add(tick, buzzing ? "buzz_start" : "buzz_stop", 0f, 1f);
            }
        }

        void tick(int tick) {
            if (continuous) {
                return;
            }
            if (cooldown > 0) {
                cooldown--;
            }
            if (pending > 0) {
                pending--;
            }
            if (pending == 0) {
                pending = -1;
                if (step.state != WingMotion.BRAKE) {
                    add(tick, "strike", 0.7f, 1f);
                }
            }
            if (step.beating && step.mode == FlightMode.TRAVEL && cooldown <= 0 && pending < 0) {
                cooldown = interval;
                pending = strike;
                add(tick, "beat", 0f, 1f);
            }
        }

        void hover(Sim sim, int tick) {
            if (sim.step.mode != FlightMode.STABILIZED) {
                return;
            }
            double from = Math.floor(sim.prevHoverPhase / Mth.TWO_PI);
            double to = Math.floor(sim.hoverPhase / Mth.TWO_PI);
            if (to > from && tick - 1 + (to * Mth.TWO_PI - sim.prevHoverPhase) / (sim.hoverPhase - sim.prevHoverPhase) >= hoverFrom) {
                double frac = (to * Mth.TWO_PI - sim.prevHoverPhase) / (sim.hoverPhase - sim.prevHoverPhase);
                add((float) (tick - 1 + frac), "hover_flap", 0f, 1f);
                last().addProperty("silent", true);
            }
        }
    }

    private static float round(float v) {
        return Math.round(v * 100000f) / 100000f;
    }

    private static float speed(Step step) {
        if (step.mode != FlightMode.TRAVEL) {
            return 0f;
        }
        return step.state == WingMotion.DIVE ? 25f : step.state == WingMotion.BRAKE ? 6f : 15f;
    }

    private JsonObject clip(String name, String loop, float lengthTicks, boolean preroll, List<Segment> segments) {
        Sim sim = new Sim();
        sim.enter(segments.get(0).step(), true);
        if (preroll) {
            for (int i = 0; i < PREROLL; i++) {
                sim.tick();
            }
            if (segments.get(0).step().beating) {
                float shift = -(sim.phase - Mth.floor(sim.phase));
                sim.phase += shift;
                sim.prevPhase += shift;
            }
            sim.hoverPhase -= Math.floor(sim.hoverPhase / Mth.TWO_PI) * Mth.TWO_PI;
            sim.prevHoverPhase = sim.hoverPhase - Mth.TWO_PI * hoverRate / 20f;
        }
        Cues cues = bench ? new Cues() : null;
        if (cues != null) {
            cues.enter(segments.get(0).step(), 0);
        }
        JsonArray frames = new JsonArray();
        int tick = 0;
        int last = (int) Math.ceil(lengthTicks);
        int segment = 0;
        int segmentEnd = segments.get(0).ticks();
        boolean first = true;
        while (tick <= last) {
            if (!first) {
                sim.tick();
                if (cues != null) {
                    cues.hover(sim, tick);
                }
            }
            first = false;
            if (cues != null && tick <= lengthTicks + 1e-4f) {
                cues.tick(tick);
            }
            for (float sub : SUBSTEPS) {
                float t = tick + sub;
                if (t > lengthTicks + 1e-4f) {
                    break;
                }
                frames.add(frame(sim, t / 20f, sub));
            }
            tick++;
            if (tick >= segmentEnd && segment + 1 < segments.size()) {
                segment++;
                segmentEnd += segments.get(segment).ticks();
                sim.enter(segments.get(segment).step(), false);
                if (cues != null && tick <= lengthTicks + 1e-4f) {
                    cues.enter(segments.get(segment).step(), tick);
                }
            }
        }
        JsonObject clip = new JsonObject();
        clip.addProperty("name", name);
        clip.addProperty("loop", loop);
        clip.addProperty("length", lengthTicks / 20f);
        if (cues != null) {
            clip.addProperty("flap_rate", flapRate);
            clip.addProperty("hover_rate", hoverRate);
            clip.addProperty("strike_at", round(style.strikeAt()));
            clip.addProperty("continuous", cues.continuous);
            clip.addProperty("flap_interval", cues.interval);
            clip.addProperty("strike_tick", cues.strike);
            clip.addProperty("speed", speed(segments.get(0).step()));
            List<JsonObject> sorted = new ArrayList<>();
            cues.events.forEach(e -> sorted.add(e.getAsJsonObject()));
            sorted.sort(java.util.Comparator.comparingDouble(e -> e.get("t").getAsDouble()));
            JsonArray events = new JsonArray();
            sorted.forEach(events::add);
            clip.add("events", events);
        }
        clip.add("frames", frames);
        return clip;
    }

    private JsonObject frame(Sim sim, float seconds, float pt) {
        WingMotion motion = sim.motion(pt);
        for (ModelPart part : body.values()) {
            part.resetPose();
        }
        Step step = sim.step;
        FlightPose.pose(player, step.mode, motion, Mth.lerp(pt, sim.prevBank, sim.bank), motion.time, rig);
        rig.playerArms(player.body, player.rightArm, player.leftArm);
        rig.setup(motion, sim.memory);
        JsonObject frame = new JsonObject();
        frame.addProperty("t", seconds);
        JsonObject wing = new JsonObject();
        for (Map.Entry<String, ModelPart> entry : bones.entrySet()) {
            wing.add(entry.getKey(), state(entry.getValue()));
        }
        frame.add("bones", wing);
        JsonObject parts = new JsonObject();
        for (Map.Entry<String, ModelPart> entry : body.entrySet()) {
            parts.add(entry.getKey(), state(entry.getValue()));
        }
        frame.add("player", parts);
        float brake = Mth.clamp(motion.w(WingMotion.BRAKE), 0f, 1f);
        float pitch = step.mode == FlightMode.TRAVEL ? com.zorago.simplywings.render.BodyPose.angle(Mth.lerp(pt, sim.prevPitch, sim.pitch), brake) : 0f;
        float roll = step.mode == FlightMode.TRAVEL ? Mth.lerp(pt, sim.prevBank, sim.bank) : 0f;
        frame.addProperty("pitch", pitch);
        frame.addProperty("billow", rig.membraneBillow());
        frame.addProperty("roll", roll);
        if (bench) {
            frame.addProperty("speed", speed(step));
        }
        return frame;
    }

    private static JsonArray state(ModelPart part) {
        JsonArray a = new JsonArray();
        float scale = part.visible ? 1f : 0f;
        for (float v : new float[]{part.xRot, part.yRot, part.zRot, part.x, part.y, part.z,
                part.xScale * scale, part.yScale * scale, part.zScale * scale}) {
            a.add(Math.round(v * 100000f) / 100000f);
        }
        return a;
    }

    private static Step step(FlightMode mode, int state) {
        Step s = new Step();
        s.mode = mode;
        s.state = state;
        return s;
    }

    private static Step travel(int state, float pitch) {
        Step s = step(FlightMode.TRAVEL, state);
        s.pitch = pitch;
        return s;
    }

    private JsonArray clips() {
        float cycle = Math.max(4f, 20f / Math.max(0.1f, flapRate));
        float flapLoop = cycle * Math.max(1, Math.round(20f / cycle));
        float hoverCycle = 20f / hoverRate;
        float hoverLoop = hoverCycle * Math.max(1, Math.round(50f / hoverCycle));
        JsonArray clips = new JsonArray();

        clips.add(clip("idle_ground", "loop", 157, true, List.of(new Segment(1, step(FlightMode.NONE, WingMotion.GROUND)))));
        Step walk = step(FlightMode.NONE, WingMotion.GROUND);
        walk.walk = 4.3f;
        clips.add(clip("walk", "loop", 74, true, List.of(new Segment(1, walk))));
        Step run = step(FlightMode.NONE, WingMotion.GROUND);
        run.walk = 5.6f;
        clips.add(clip("run", "loop", 48, true, List.of(new Segment(1, run))));
        int[] lengths = rig.idleLengths();
        for (int v = 0; v < lengths.length; v++) {
            Step rest = step(FlightMode.NONE, WingMotion.GROUND);
            Step rouse = step(FlightMode.NONE, WingMotion.GROUND);
            rouse.rouse = true;
            rouse.variant = v;
            rest.variant = v;
            clips.add(clip(lengths.length == 1 ? "idle_rouse" : "idle_rouse_" + (v + 1), "once", lengths[v] + 20, true,
                    List.of(new Segment(5, rest), new Segment(1, rouse))));
        }
        List<Segment> charge = new ArrayList<>();
        charge.add(new Segment(5, step(FlightMode.NONE, WingMotion.GROUND)));
        for (int i = 1; i <= 20; i++) {
            Step s = step(FlightMode.NONE, WingMotion.CHARGE);
            s.charge = i / 20f;
            charge.add(new Segment(1, s));
        }
        Step launch = travel(WingMotion.CRUISE, -40f);
        launch.launch = true;
        charge.add(new Segment(1, launch));
        charge.add(new Segment(30, travel(WingMotion.CRUISE, -40f)));
        clips.add(clip("takeoff", "once", 62, true, charge));
        Step jump = step(FlightMode.NONE, WingMotion.AIR);
        jump.airJump = true;
        clips.add(clip("air_jump", "loop", 40, true, List.of(new Segment(1, jump))));
        clips.add(clip("landing", "once", 40, true, List.of(new Segment(1, step(FlightMode.NONE, WingMotion.AIR)),
                new Segment(14, step(FlightMode.NONE, WingMotion.LANDING)), new Segment(1, step(FlightMode.NONE, WingMotion.GROUND)))));
        if (bench) {
            Step hard = step(FlightMode.NONE, WingMotion.LANDING);
            hard.hard = true;
            clips.add(clip("landing_hard", "once", 40, true, List.of(new Segment(1, step(FlightMode.NONE, WingMotion.AIR)),
                    new Segment(14, hard), new Segment(1, step(FlightMode.NONE, WingMotion.GROUND)))));
        }

        Step flap = travel(WingMotion.CRUISE, 0f);
        flap.beating = true;
        clips.add(clip("cruise_flap", "loop", flapLoop, true, List.of(new Segment(1, flap))));
        clips.add(clip("cruise_glide", "loop", 200, true, List.of(new Segment(1, travel(WingMotion.CRUISE, 0f)))));
        Step left = travel(WingMotion.CRUISE, 0f);
        left.bank = -35f;
        clips.add(clip("glide_bank_left", "loop", 80, true, List.of(new Segment(1, left))));
        Step right = travel(WingMotion.CRUISE, 0f);
        right.bank = 35f;
        clips.add(clip("glide_bank_right", "loop", 80, true, List.of(new Segment(1, right))));
        Step stall = travel(WingMotion.CRUISE, -10f);
        stall.stall = true;
        clips.add(clip("stall", "loop", 126, true, List.of(new Segment(1, stall))));
        Step shallow = travel(WingMotion.DIVE, 47f);
        shallow.dive = 0.3f;
        clips.add(clip("dive_shallow", "loop", 60, true, List.of(new Segment(1, shallow))));
        Step deep = travel(WingMotion.DIVE, 75f);
        deep.dive = 1f;
        clips.add(clip("dive_deep", "loop", 60, true, List.of(new Segment(1, deep))));
        clips.add(clip("tuck", "loop", 60, true, List.of(new Segment(1, travel(WingMotion.TUCK, 15f)))));
        clips.add(clip("brake", "loop", 60, true, List.of(new Segment(1, travel(WingMotion.BRAKE, 0f)))));
        Step slow = travel(WingMotion.BRAKE, 0f);
        slow.slow = true;
        clips.add(clip("brake_slow", "loop", 45, true, List.of(new Segment(1, slow))));
        clips.add(clip("brake_to_hover", "once", 100, true, List.of(new Segment(30, travel(WingMotion.BRAKE, 0f)),
                new Segment(30, slow), new Segment(1, step(FlightMode.STABILIZED, WingMotion.HOVER)))));
        clips.add(clip("hover", "loop", hoverLoop, true, List.of(new Segment(1, step(FlightMode.STABILIZED, WingMotion.HOVER)))));

        Step seqFlap = travel(WingMotion.CRUISE, -10f);
        seqFlap.beating = true;
        Step seqBank = travel(WingMotion.CRUISE, 0f);
        seqBank.bank = -35f;
        Step seqDive = travel(WingMotion.DIVE, 60f);
        seqDive.dive = 0.6f;
        List<Segment> sequence = new ArrayList<>();
        sequence.add(new Segment(20, step(FlightMode.NONE, WingMotion.GROUND)));
        for (int i = 1; i <= 20; i++) {
            Step s = step(FlightMode.NONE, WingMotion.CHARGE);
            s.charge = i / 20f;
            sequence.add(new Segment(1, s));
        }
        sequence.add(new Segment(1, launch));
        sequence.add(new Segment(20, travel(WingMotion.CRUISE, -40f)));
        sequence.add(new Segment(50, seqFlap));
        sequence.add(new Segment(40, travel(WingMotion.CRUISE, 0f)));
        sequence.add(new Segment(40, seqBank));
        sequence.add(new Segment(40, seqDive));
        sequence.add(new Segment(30, travel(WingMotion.BRAKE, 0f)));
        sequence.add(new Segment(25, slow));
        sequence.add(new Segment(60, step(FlightMode.STABILIZED, WingMotion.HOVER)));
        sequence.add(new Segment(12, step(FlightMode.NONE, WingMotion.AIR)));
        sequence.add(new Segment(14, step(FlightMode.NONE, WingMotion.LANDING)));
        sequence.add(new Segment(1, step(FlightMode.NONE, WingMotion.GROUND)));
        clips.add(clip("full_flight", "once", 406, false, sequence));
        return clips;
    }

    private JsonObject bake(String name) {
        JsonObject out = new JsonObject();
        out.addProperty("rig", name);
        out.addProperty("flap_rate", flapRate);
        out.addProperty("hover_rate", hoverRate);
        if (bench) {
            out.addProperty("pair", pair);
            out.addProperty("strike_at", round(style.strikeAt()));
            out.addProperty("continuous", flapRate >= WingDefinition.CONTINUOUS_FLAP_RATE);
            out.addProperty("fps", 40);
        }
        JsonObject rest = new JsonObject();
        for (Map.Entry<String, ModelPart> entry : bones.entrySet()) {
            ModelPart part = entry.getValue();
            part.loadPose(part.getInitialPose());
            rest.add(entry.getKey(), state(part));
        }
        out.add("rest", rest);
        out.add("clips", clips());
        return out;
    }

    private static void write(Path target, JsonObject json) throws IOException {
        Files.createDirectories(target.toAbsolutePath().getParent());
        Files.writeString(target, new GsonBuilder().create().toJson(json), StandardCharsets.UTF_8);
    }

    private static void all(Path dir, Path root) throws IOException {
        List<JsonObject> definitions = definitions(root);
        JsonArray models = new JsonArray();
        for (String name : WingRigs.names()) {
            RigBake bake = new RigBake(name, root, true);
            write(dir.resolve(name + ".bench.json"), bake.bake(name));
            JsonObject model = new JsonObject();
            model.addProperty("model", name);
            model.addProperty("file", name + ".bench.json");
            model.addProperty("baked_pair", bake.pair);
            JsonArray pairs = new JsonArray();
            for (JsonObject json : definitions) {
                if (json.get("model").getAsString().equals("simplywings:" + name)) {
                    float rate = json.get("flap_rate").getAsFloat();
                    JsonObject p = new JsonObject();
                    p.addProperty("id", json.get("id").getAsString());
                    p.addProperty("rarity", json.has("rarity") ? json.get("rarity").getAsString() : null);
                    p.addProperty("flap_rate", rate);
                    p.addProperty("hover_rate", json.get("hover_rate").getAsFloat());
                    p.addProperty("continuous", rate >= WingDefinition.CONTINUOUS_FLAP_RATE);
                    pairs.add(p);
                }
            }
            model.add("pairs", pairs);
            models.add(model);
            System.out.println("Baked " + name + " bench");
        }
        JsonObject index = new JsonObject();
        index.addProperty("fps", 40);
        index.add("models", models);
        write(dir.resolve("index.json"), index);
        System.out.println("Wrote " + models.size() + " rigs to " + dir);
    }

    public static void main(String[] args) throws IOException {
        Path root = Path.of(args.length > 2 ? args[2] : ".").toAbsolutePath();
        boolean bench = args.length > 3 && args[3].equals("bench");
        if (args[0].equals("all")) {
            all(Path.of(args[1]), root);
            return;
        }
        RigBake bake = new RigBake(args[0], root, bench);
        Path target = Path.of(args[1]);
        write(target, bake.bake(args[0]));
        System.out.println("Baked " + args[0] + " to " + target);
    }
}
