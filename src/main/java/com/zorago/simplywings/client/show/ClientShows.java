package com.zorago.simplywings.client.show;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zorago.simplywings.gacha.GachaTier;
import com.zorago.simplywings.gacha.ModParticles;
import com.zorago.simplywings.network.Payloads;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;

public final class ClientShows {
    private static final String[] ELEMENTS = {"fire", "dark", "earth", "water", "light", "air"};
    private static final String[] ELEMENT_PARTICLE = {"ember", "wisp", "pebble", "drop", "spark", "gust"};
    private static final float[] FLAME_HEIGHT = {66, 70, 56, 64, 72, 74};
    private static final Vector3f PLACEHOLDER = new Vector3f(0, 0, 72);
    private static final List<Show> SHOWS = new ArrayList<>();
    private static final Map<BlockPos, long[]> IDLE = new HashMap<>();
    private static Marker camera;
    private static Entity previousCamera;
    private static CameraType previousType;
    private static boolean previousHideGui;
    private static Show cameraShow;
    private static long ticks;
    private static ShowModel warmed;

    static final class Show {
        final Payloads.SummonShow payload;
        final ShowModel.Anim anim;
        final Vec3 origin;
        final float yaw;
        final RandomSource random;
        final boolean summoner;
        final int[] tint;
        float lastTime = -1;
        long startTick;
        boolean holding;
        float exitAt = -1;
        boolean skipped;

        Show(Payloads.SummonShow payload, ShowModel model, ShowModel.Anim anim, boolean summoner) {
            this.payload = payload;
            this.anim = anim;
            BlockPos p = payload.pos();
            this.origin = new Vec3(p.getX() + 0.5, p.getY() - 2, p.getZ() + 0.5);
            this.yaw = -90f * payload.facing();
            this.random = RandomSource.create(payload.seed());
            this.summoner = summoner;
            this.tint = new int[model.bones.length];
            java.util.Arrays.fill(tint, 0xFFFFFFFF);
            for (int i = 0; i < payload.tiers().size() && i < 10; i++) {
                int root = model.bone("multi_star_" + i);
                if (root >= 0) {
                    int color = 0xFF000000 | (GachaTier.of(payload.tiers().get(i)).color() & 0xFFFFFF);
                    for (int b = 0; b < model.bones.length; b++) {
                        for (int q = b; q >= 0; q = model.bones[q].parent) {
                            if (q == root) {
                                tint[b] = color;
                                break;
                            }
                        }
                    }
                }
            }
        }

        float time(float partial) {
            return (ticks - startTick + partial) / 20f;
        }
    }

    public static void start(Payloads.SummonShow payload) {
        Minecraft mc = Minecraft.getInstance();
        ShowModel model = ShowModel.get().orElse(null);
        if (model == null || mc.level == null) {
            return;
        }
        ShowModel.Anim anim = model.anims.get(payload.anim());
        if (anim == null) {
            return;
        }
        SHOWS.removeIf(s -> {
            if (s.payload.pos().equals(payload.pos())) {
                if (s == cameraShow) {
                    releaseCamera();
                }
                return true;
            }
            return false;
        });
        boolean summoner = mc.player != null && mc.player.getId() == payload.summoner();
        Show show = new Show(payload, model, anim, summoner);
        show.startTick = ticks + (payload.start() - mc.level.getGameTime());
        SHOWS.add(show);
    }

    public static boolean active(BlockPos pos) {
        for (Show s : SHOWS) {
            if (s.payload.pos().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    public static float holdTime(Show show) {
        return show.anim.length - show.anim.meta("post", 0);
    }

    public static Show summonerShow() {
        for (Show s : SHOWS) {
            if (s.summoner && !s.skipped) {
                return s;
            }
        }
        return null;
    }

    public static float summonerTime() {
        Show s = summonerShow();
        return s == null ? -1 : s.time(0);
    }

    public static float summonerCardsAt() {
        Show s = summonerShow();
        return s == null ? -1 : s.anim.meta("reveal", s.anim.length - 6) + 0.9f;
    }

    public static void hold(boolean hold) {
        Show s = summonerShow();
        if (s != null) {
            s.holding = hold;
        }
    }

    public static void release() {
        Show s = summonerShow();
        if (s == null) {
            return;
        }
        float now = s.time(0);
        if (s.holding && now > holdTime(s)) {
            s.exitAt = now;
        }
        s.holding = false;
    }

    static boolean frozen(Show s, float raw) {
        return (s.holding || s.exitAt >= 0) && raw > holdTime(s);
    }

    public static boolean summonerShowRunning() {
        for (Show s : SHOWS) {
            if (s.summoner && !s.skipped) {
                return true;
            }
        }
        return false;
    }

    public static void skip() {
        for (Show s : SHOWS) {
            if (s.summoner) {
                s.skipped = true;
            }
        }
        releaseCamera();
    }

    public static void markIdle(BlockPos pos, int facing) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            IDLE.put(pos.immutable(), new long[]{level.getGameTime(), facing});
        }
    }

    static Vec3 toWorld(Show show, Vector3f local) {
        Vector3f v = new Vector3f(local).div(16f).rotateY((float) Math.toRadians(show.yaw));
        return show.origin.add(v.x, v.y, v.z);
    }

    static Vector3f toLocal(Show show, Vec3 world) {
        Vector3f v = new Vector3f((float) (world.x - show.origin.x), (float) (world.y - show.origin.y), (float) (world.z - show.origin.z));
        return v.rotateY((float) Math.toRadians(-show.yaw)).mul(16f);
    }

    private static Vector3f playerDelta(Show show) {
        ClientLevel level = Minecraft.getInstance().level;
        Entity e = level == null ? null : level.getEntity(show.payload.summoner());
        if (e == null) {
            return new Vector3f();
        }
        return toLocal(show, e.position()).sub(PLACEHOLDER);
    }

    private static float smooth(float t) {
        t = Math.max(0, Math.min(1, t));
        return t * t * (3 - 2 * t);
    }

    private static void applyOffsets(ShowModel model, Show show, float time) {
        ShowRenderer.OFFSETS.clear();
        Vector3f delta = playerDelta(show);
        if (delta.lengthSquared() < 1e-3f) {
            return;
        }
        float w = time < 1.05f ? 1f : 1f - smooth((time - 1.05f) / 0.75f);
        if (w > 0) {
            for (int i = 0; i < 10; i++) {
                int b = model.bone("offering_" + i);
                if (b >= 0) {
                    ShowRenderer.OFFSETS.put(b, new Vector3f(delta).mul(w));
                }
            }
        }
        float pre = show.anim.meta("pre", 0);
        float post = show.anim.meta("post", 0);
        float cw = time < 1.7f ? 1f : time < pre ? 1f - smooth((time - 1.7f) / Math.max(0.1f, pre - 1.7f))
                : time > show.anim.length - post ? smooth((time - (show.anim.length - post)) / Math.max(0.1f, post)) : 0f;
        if (cw > 0) {
            int c = model.bone("camera");
            int t = model.bone("camera_target");
            if (c >= 0) {
                ShowRenderer.OFFSETS.put(c, new Vector3f(delta).mul(cw));
            }
            if (t >= 0) {
                ShowRenderer.OFFSETS.put(t, new Vector3f(delta).mul(cw * 0.6f));
            }
        }
    }

    public static void onRenderStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES || SHOWS.isEmpty()) {
            return;
        }
        ShowModel model = ShowModel.get().orElse(null);
        Minecraft mc = Minecraft.getInstance();
        if (model == null || mc.level == null) {
            return;
        }
        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 cam = event.getCamera().getPosition();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        PoseStack poseStack = event.getPoseStack();
        float fogStart = RenderSystem.getShaderFogStart();
        float fogEnd = RenderSystem.getShaderFogEnd();
        for (Show show : SHOWS) {
            float raw = show.time(partial);
            if (raw < 0 || raw > show.anim.length && !frozen(show, raw)) {
                continue;
            }
            boolean freeze = frozen(show, raw);
            float time = freeze ? holdTime(show) : raw;
            if (show.origin.distanceToSqr(cam) > 1536 * 1536) {
                continue;
            }
            poseStack.pushPose();
            poseStack.translate(show.origin.x - cam.x, show.origin.y - cam.y, show.origin.z - cam.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(show.yaw));
            poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
            applyOffsets(model, show, time);
            if (freeze) {
                java.util.Set<Integer> roots = flameRoots(model);
                ShowRenderer.render(model, show.anim, time, raw, poseStack, buffers, i -> show.tint[i], event.getFrustum(), cam,
                        r -> !roots.contains(r));
                ShowModel.Anim idle = model.anims.get("idle");
                if (idle != null) {
                    ShowRenderer.render(model, idle, raw % idle.length, raw, poseStack, buffers, i -> 0xFFFFFFFF, event.getFrustum(), cam,
                            roots::contains);
                }
            } else {
                ShowRenderer.render(model, show.anim, time, time, poseStack, buffers, i -> show.tint[i], event.getFrustum(), cam, null);
            }
            ShowRenderer.OFFSETS.clear();
            poseStack.popPose();
        }
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);
        RenderSystem.setShaderFogEnd(Float.MAX_VALUE);
        buffers.endBatch();
        RenderSystem.setShaderFogStart(fogStart);
        RenderSystem.setShaderFogEnd(fogEnd);
    }

    private static java.util.Set<Integer> flameRootSet;
    private static ShowModel flameModel;

    private static java.util.Set<Integer> flameRoots(ShowModel model) {
        if (flameModel != model) {
            flameModel = model;
            flameRootSet = new java.util.HashSet<>();
            for (String e : ELEMENTS) {
                int b = model.bone("flame_" + e);
                if (b >= 0) {
                    flameRootSet.add(b);
                }
            }
        }
        return flameRootSet;
    }

    private static Vec3[] cameraAt(ShowModel model, Show show, float time) {
        int c = model.bone("camera");
        int t = model.bone("camera_target");
        applyOffsets(model, show, time);
        Vec3 pos = toWorld(show, ShowRenderer.position(model, show.anim, c, time, new Vector3f()));
        Vec3 target = toWorld(show, ShowRenderer.position(model, show.anim, t, time, new Vector3f()));
        ShowRenderer.OFFSETS.clear();
        return new Vec3[]{pos, target};
    }

    private static Vec3 spin(Show show, Vec3 p, float angle) {
        Vec3 d = p.subtract(show.origin);
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return show.origin.add(d.x * c - d.z * s, d.y, d.x * s + d.z * c);
    }

    public static void onRenderFrame(RenderFrameEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Show show = null;
        for (Show s : SHOWS) {
            if (s.summoner && !s.skipped) {
                show = s;
            }
        }
        ShowModel model = ShowModel.get().orElse(null);
        if (show == null || model == null || mc.level == null || mc.player == null) {
            if (cameraShow != null) {
                releaseCamera();
            }
            return;
        }
        float time = show.time(event.getPartialTick().getGameTimeDeltaPartialTick(false));
        if (time < 0 || time > show.anim.length && !frozen(show, time)) {
            return;
        }
        if (model.bone("camera") < 0 || model.bone("camera_target") < 0) {
            return;
        }
        Vec3 pos;
        Vec3 target;
        if (frozen(show, time)) {
            float hold = holdTime(show);
            Vec3[] base = cameraAt(model, show, hold);
            float orbit = 0.16f * (time - hold);
            pos = spin(show, base[0], orbit);
            target = spin(show, base[1], orbit);
            if (show.exitAt >= 0) {
                float post = show.anim.meta("post", 2.4f);
                float u = smooth((time - show.exitAt) / post);
                Vec3[] end = cameraAt(model, show, show.anim.length);
                pos = pos.lerp(end[0], u);
                target = target.lerp(end[1], u);
            }
        } else {
            Vec3[] at = cameraAt(model, show, time);
            pos = at[0];
            target = at[1];
        }
        if (camera == null || camera.level() != mc.level) {
            camera = new Marker(EntityType.MARKER, mc.level);
        }
        if (cameraShow != show) {
            if (cameraShow == null) {
                previousCamera = mc.getCameraEntity();
                previousType = mc.options.getCameraType();
                previousHideGui = mc.options.hideGui;
            }
            cameraShow = show;
            mc.options.setCameraType(CameraType.FIRST_PERSON);
            mc.options.hideGui = true;
        }
        double dx = target.x - pos.x;
        double dy = target.y - pos.y;
        double dz = target.z - pos.z;
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
        camera.setPos(pos.x, pos.y, pos.z);
        camera.xo = camera.xOld = pos.x;
        camera.yo = camera.yOld = pos.y;
        camera.zo = camera.zOld = pos.z;
        camera.setYRot(yaw);
        camera.setXRot(pitch);
        camera.yRotO = yaw;
        camera.xRotO = pitch;
        if (mc.getCameraEntity() != camera) {
            mc.setCameraEntity(camera);
        }
    }

    private static void releaseCamera() {
        Minecraft mc = Minecraft.getInstance();
        if (cameraShow == null) {
            return;
        }
        cameraShow = null;
        mc.setCameraEntity(previousCamera != null && previousCamera.isAlive() ? previousCamera : mc.player);
        if (previousType != null) {
            mc.options.setCameraType(previousType);
        }
        mc.options.hideGui = previousHideGui;
    }

    private static float particleScale() {
        ParticleStatus status = Minecraft.getInstance().options.particles().get();
        return status == ParticleStatus.ALL ? 1f : status == ParticleStatus.DECREASED ? 0.45f : 0.15f;
    }

    private static int count(RandomSource random, float n) {
        float v = n * particleScale();
        int whole = (int) v;
        return whole + (random.nextFloat() < v - whole ? 1 : 0);
    }

    private static void emit(String kind, Vec3 p, double vx, double vy, double vz) {
        Minecraft.getInstance().particleEngine.createParticle(ModParticles.get(kind), p.x, p.y, p.z, vx, vy, vz);
    }

    static Vec3 bone(ShowModel model, Show show, String name, float time) {
        int b = model.bone(name);
        return b < 0 ? show.origin : toWorld(show, ShowRenderer.position(model, show.anim, b, time, new Vector3f()));
    }

    static boolean crossed(Show show, float t, float now) {
        return show.lastTime < t && now >= t;
    }

    static void sound(Show show, Vec3 at, SoundEvent event, float volume, float pitch) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            level.playLocalSound(at.x, at.y, at.z, event, SoundSource.BLOCKS, volume, pitch, false);
        }
    }

    private static void burst(Show show, Vec3 at, String kind, int n, double speed, double lift) {
        RandomSource r = show.random;
        for (int i = count(r, n); i > 0; i--) {
            double a = r.nextDouble() * Math.PI * 2;
            double e = (r.nextDouble() * 2 - 1);
            double h = Math.sqrt(1 - e * e);
            double s = speed * (0.5 + r.nextDouble() * 0.7);
            emit(kind, at, Math.cos(a) * h * s, e * s + lift, Math.sin(a) * h * s);
        }
    }

    private static void ring(Show show, Vec3 at, String kind, int n, double speed) {
        RandomSource r = show.random;
        int c = count(r, n);
        for (int i = 0; i < c; i++) {
            double a = Math.PI * 2 * i / Math.max(1, c) + r.nextDouble() * 0.05;
            emit(kind, at, Math.cos(a) * speed, 0.02 + r.nextDouble() * 0.04, Math.sin(a) * speed);
        }
    }

    private static void flames(Show show, ShowModel model, float time, float rate) {
        RandomSource r = show.random;
        for (int e = 0; e < ELEMENTS.length; e++) {
            int b = model.bone("flame_" + ELEMENTS[e]);
            if (b < 0) {
                continue;
            }
            ShowModel.Bone bone = model.bones[b];
            Vec3 base = toWorld(show, new Vector3f(bone.px, bone.py, bone.pz));
            for (int i = count(r, rate); i > 0; i--) {
                double h = FLAME_HEIGHT[e] / 16.0 * (0.35 + r.nextDouble() * 0.6);
                Vec3 p = base.add((r.nextDouble() - 0.5) * 0.7, h, (r.nextDouble() - 0.5) * 0.7);
                emit(ELEMENT_PARTICLE[e], p, (r.nextDouble() - 0.5) * 0.03, 0.03 + r.nextDouble() * 0.06, (r.nextDouble() - 0.5) * 0.03);
            }
        }
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        ShowModel model = ShowModel.get().orElse(null);
        if (mc.level == null || model == null || mc.isPaused()) {
            if (mc.level == null) {
                SHOWS.clear();
                IDLE.clear();
                releaseCamera();
            }
            return;
        }
        ticks++;
        if (warmed != model) {
            warmed = model;
            ShowRenderer.warmUp(model);
        }
        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        Iterator<Show> it = SHOWS.iterator();
        while (it.hasNext()) {
            Show show = it.next();
            float now = show.time(0);
            boolean finished = show.exitAt >= 0 ? now > show.exitAt + show.anim.meta("post", 2.4f) + 0.1f
                    : !show.holding && now > show.anim.length + 0.5f;
            if (finished) {
                if (show == cameraShow) {
                    releaseCamera();
                }
                it.remove();
                continue;
            }
            if (now >= 0 && show.origin.distanceToSqr(cam) < 200 * 200) {
                tick(show, model, now);
            }
            show.lastTime = now;
        }
        long time = mc.level.getGameTime();
        IDLE.entrySet().removeIf(e -> time - e.getValue()[0] > 40);
        if (time % 3 == 0) {
            ShowModel.Anim idle = model.anims.get("idle");
            for (Map.Entry<BlockPos, long[]> e : IDLE.entrySet()) {
                if (idle == null || active(e.getKey()) || cam.distanceToSqr(Vec3.atCenterOf(e.getKey())) > 64 * 64) {
                    continue;
                }
                Payloads.SummonShow fake = new Payloads.SummonShow(e.getKey(), (int) e.getValue()[1], "idle", 0, (int) time, List.of(), -1);
                flames(new Show(fake, model, idle, false), model, 0, 0.6f);
            }
        }
    }

    private static void tick(Show show, ShowModel model, float now) {
        ShowModel.Anim a = show.anim;
        RandomSource r = show.random;
        float pre = a.meta("pre", 0);
        float rank = a.meta("rank", 0);
        float power = 0.35f + 0.65f * rank / 4f + (a.meta.has("mythic") && !a.meta.get("mythic").getAsString().isEmpty() ? 0.25f : 0f);
        Vec3 crystal = toWorld(show, new Vector3f(0, 50, 0));
        Vec3 altar = toWorld(show, new Vector3f(0, 16, 0));
        Vec3 hover = toWorld(show, new Vector3f(0, 96, 0));
        flames(show, model, now, now < pre ? 0.3f : 1.1f);
        if (now > 1.7f && now < 2.05f) {
            burst(show, crystal, "spark", 8, 0.18, 0.05);
            if (crossed(show, 1.8f, now)) {
                burst(show, crystal, "flare", 2, 0.0, 0);
            }
        }
        float[] ignite = a.metaArray("ignite");
        for (int i = 0; i < ignite.length && i < ELEMENTS.length; i++) {
            if (crossed(show, ignite[i] + 0.1f, now)) {
                int b = model.bone("flame_" + ELEMENTS[i]);
                ShowModel.Bone bone = model.bones[Math.max(0, b)];
                Vec3 base = toWorld(show, new Vector3f(bone.px, bone.py, bone.pz)).add(0, 1.6, 0);
                burst(show, base, ELEMENT_PARTICLE[i], 24, 0.22, 0.12);
            }
        }
        if (crossed(show, pre + 4.0f, now)) {
            ring(show, altar, "spark", 90, 0.9);
            burst(show, altar, "flare", 3, 0.0, 0);
        }
        float settle = a.meta("settle", a.length);
        float pillarH = a.meta("pillarH", 26);
        if (now > pre + 4.0f && now < settle) {
            for (int i = count(r, 5 * power + 1); i > 0; i--) {
                double ang = r.nextDouble() * Math.PI * 2;
                double rad = 0.4 + r.nextDouble() * 2.6;
                Vec3 p = altar.add(Math.cos(ang) * rad, r.nextDouble() * pillarH * 0.5, Math.sin(ang) * rad);
                emit("mote", p, 0, 0.35 + r.nextDouble() * 0.6, 0);
            }
        }
        float birth = a.meta("birth", 0);
        float desc = a.meta("desc", 0);
        float hoverT = a.meta("hover", 0);
        float burstT = a.meta("burst", a.length);
        float reveal = a.meta("reveal", a.length);
        if (crossed(show, birth + 1.0f, now)) {
            Vec3 top = toWorld(show, new Vector3f(0, a.meta("top", 384), 0));
            burst(show, top, "spark", 60, 0.6, 0);
            burst(show, top, "flare", 3, 0, 0);
        }
        if (now > desc && now < hoverT && model.bone("star") >= 0 && ShowRenderer.visible(model, a, model.bone("star"), now)) {
            Vec3 star = bone(model, show, "star", now);
            for (int i = count(r, 4 * power + 1); i > 0; i--) {
                emit(i % 3 == 0 ? "flare" : "spark", star.add((r.nextDouble() - 0.5) * 1.2, (r.nextDouble() - 0.5) * 1.2, (r.nextDouble() - 0.5) * 1.2),
                        (r.nextDouble() - 0.5) * 0.1, 0.05 + r.nextDouble() * 0.1, (r.nextDouble() - 0.5) * 0.1);
            }
        }
        float[] beats = a.metaArray("beats");
        for (int k = 0; k < beats.length; k++) {
            if (crossed(show, beats[k], now)) {
                Vec3 star = bone(model, show, "star", beats[k]);
                burst(show, star, "spark", 70 + 30 * k, 0.9 + 0.2 * k, 0);
                burst(show, star, "flare", 4 + k, 0.05, 0);
            }
        }
        float[] spawn = a.metaArray("spawn");
        for (int i = 0; i < spawn.length; i++) {
            int tier = i < show.payload.tiers().size() ? show.payload.tiers().get(i) : 0;
            if (crossed(show, spawn[i] + 0.3f, now)) {
                Vec3 star = bone(model, show, "multi_star_" + i, spawn[i] + 0.4f);
                burst(show, star, "spark", 20 + 25 * tier, 0.5 + 0.15 * tier, 0);
            }
        }
        if (a.meta.has("flights")) {
            var flights = a.meta.getAsJsonArray("flights");
            for (int i = 0; i < flights.size(); i++) {
                var f = flights.get(i).getAsJsonArray();
                float start = f.get(0).getAsFloat();
                float dur = f.get(1).getAsFloat();
                for (int c = 0; c < 2; c++) {
                    float at = c == 0 ? start : start + dur;
                    if (crossed(show, at - 0.1f, now)) {
                        Vec3 cloud = bone(model, show, "cloud_" + (i * 2 + c), at);
                        burst(show, cloud, "smoke", 10, 0.12, 0.02);
                        burst(show, cloud, "gust", 8, 0.3, 0);
                    }
                }
            }
        }
        if (crossed(show, burstT, now)) {
            burst(show, hover, "spark", (int) (300 * power), 1.6, 0.1);
            burst(show, hover, "shard", (int) (130 * power), 1.1, 0.25);
            burst(show, hover, "smoke", (int) (60 * power), 0.25, 0.02);
            burst(show, hover, "flare", (int) (8 * power) + 2, 0.15, 0);
            ring(show, altar.add(0, 0.4, 0), "spark", (int) (160 * power), 1.7);
            ring(show, altar.add(0, 1.2, 0), "gust", (int) (60 * power), 1.1);
            for (int i = count(r, 100 * power); i > 0; i--) {
                emit("mote", hover.add((r.nextDouble() - 0.5) * 2, 0, (r.nextDouble() - 0.5) * 2), (r.nextDouble() - 0.5) * 0.2,
                        1.0 + r.nextDouble() * 1.6, (r.nextDouble() - 0.5) * 0.2);
            }
        }
        float wings = a.meta("wings", 0);
        if (wings > 0 && now > reveal + 1.0f && now < reveal + 3.6f) {
            Vec3 w = toWorld(show, new Vector3f(0, 14 * 16, 0));
            for (int i = count(r, 4 * wings); i > 0; i--) {
                emit("feather", w.add((r.nextDouble() - 0.5) * 48 * wings, (r.nextDouble() - 0.3) * 6, (r.nextDouble() - 0.5) * 4),
                        (r.nextDouble() - 0.5) * 0.05, -0.05, (r.nextDouble() - 0.5) * 0.05);
            }
        }
        ShowSounds.tick(show, model, now, power);
    }

    private ClientShows() {
    }
}
