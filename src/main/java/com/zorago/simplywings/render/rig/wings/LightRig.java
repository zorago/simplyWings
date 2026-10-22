package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.DragonTail;
import com.zorago.simplywings.render.rig.DragonWing;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class LightRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int GLEAM = 2;
    private static final int TAIL = 10;
    private static final int[] IDLES = {84, 60, 90, 76, 70, 96, 110};
    private static final float UP = 0.5f;
    private static final float DOWN = 0.2f;
    private static final float[] KEYS = Cycle.beat(UP, DOWN);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.8f, 0.2f, 1.1f, 1.15f, -0.3f, -0.95f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.18f, -0.15f, -0.24f, -0.06f, 0.3f, 0.34f);
    private static final Cycle FLEX = Cycle.of(KEYS, 0.18f, 0.42f, 0.18f, 0f, 0f, 0.05f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.96f, 0.78f, 0.92f, 1f, 1f, 1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.28f, -0.08f);
    private static final Cycle BILLOW = Cycle.of(KEYS, 0.05f, -0.15f, -0.1f, 0.25f, 0.45f, 0.18f);
    private static final float[] TAIL_GROUND = {0.6f, 0.45f, 0.4f, 0.12f, 0.06f, 0.04f};
    private static final float[] TAIL_CURL = {0f, 0.05f, 0.15f, 0.22f, 0.26f, 0.28f};
    private static final float[] TAIL_HANG = {0.14f, 0.08f, 0.08f, 0.07f, 0.05f, 0.05f};
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/light_glow.png");

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private final RigPose side = new RigPose();
    private final RigPose hind = new RigPose();
    private final float[] tailPitch = new float[6];
    private final float[] tailYaw = new float[6];
    private final float[] extraPitch = new float[6];
    private final float[] extraYaw = new float[6];
    private DragonWing[] main;
    private DragonWing[] back;
    private DragonTail tail;

    public LightRig() {
        super("light");
    }

    @Override
    public ResourceLocation glow() {
        return GLOW;
    }

    @Override
    public int[] idleLengths() {
        return IDLES;
    }

    @Override
    protected void onBind(Bones bones) {
        main = new DragonWing[]{new DragonWing(bones, "right_", 1f, 1.22f, 2.7f, 2.75f),
                new DragonWing(bones, "left_", -1f, 1.22f, 2.7f, 2.75f)};
        back = new DragonWing[]{new DragonWing(bones, "right_hind_", 1f, 1.15f, 2.55f, 2.55f),
                new DragonWing(bones, "left_hind_", -1f, 1.15f, 2.55f, 2.55f)};
        tail = new DragonTail(bones, TAIL, 2, 5);
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.1f, 0.78f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.22f, 0.68f);
        boolean shimmer = m.rouse > 0f && m.rouseVariant % 7 == 1;
        memory.spring(GLEAM, shimmer ? m.rouse : m.w(WingMotion.BRAKE) * 0.5f, 0.2f, 0.7f);
        float ground = m.w(WingMotion.GROUND);
        float hang = m.w(WingMotion.HOVER) + m.w(WingMotion.AIR) + m.w(WingMotion.LANDING);
        float travel = m.w(WingMotion.CRUISE) + m.w(WingMotion.DIVE) + m.w(WingMotion.TUCK);
        float brake = m.w(WingMotion.BRAKE);
        float charge = m.w(WingMotion.CHARGE);
        float walk = Mth.clamp(m.speed / 4.5f, 0f, 1f) * ground;
        float time = m.time;
        boolean coil = m.rouse > 0f && m.rouseVariant % 7 == 2;
        for (int k = 0; k < 6; k++) {
            float wave = Mth.sin(time * 0.2f - k * 0.7f);
            tailPitch[k] = ground * TAIL_GROUND[k] + hang * TAIL_HANG[k] + travel * (k == 0 ? 0.05f : 0.02f + 0.04f * wave)
                    + brake * (k == 0 ? -0.4f : -0.06f) + charge * (k == 0 ? 0.75f : k < 3 ? 0.25f : 0.05f)
                    + (coil ? m.rouse * (k == 0 ? -0.2f : 0.25f) : 0f);
            tailYaw[k] = ground * (TAIL_CURL[k] + 0.14f * walk * Mth.sin(time * 0.5f - k * 0.5f) + 0.05f * Mth.sin(time * 0.04f - k * 0.5f))
                    + hang * 0.12f * Mth.sin(time * 0.08f - k * 0.45f)
                    + travel * (-m.bank * 0.24f * (1f + 0.3f * k) + 0.07f * wave)
                    + (coil ? m.rouse * 0.42f : 0f);
        }
        tail.follow(memory, tailPitch, tailYaw, 0.24f, 0.7f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float gleam = memory.get(GLEAM, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 7;
        float p = m.rouseProgress;
        float env = idle * Mth.sin((float) Math.PI * p);
        float hold = idle * smooth(Mth.clamp(p * 3f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 3f, 0f, 1f));
        float billow = 0f;

        float gFold = 1f, gStroke = 0.12f, gSweep = 0.05f, gFan = 0f, gTwist = 0f;
        float sway = 0f;
        switch (variant) {
            case 0 -> {
                gFold = Mth.lerp(hold, 1f, 0.1f);
                gStroke = Mth.lerp(hold, 0.12f, 0.6f);
                gSweep = Mth.lerp(hold, 0.05f, 0.6f);
                gFan = 1.1f * hold;
            }
            case 4 -> {
                gFold = Mth.lerp(hold, 1f, 0.3f);
                gStroke = Mth.lerp(hold, 0.12f, 0.35f);
                gSweep = Mth.lerp(hold, 0.05f, 1f);
                gFan = 0.9f * hold;
            }
            case 5 -> sway = env;
            case 6 -> {
                gStroke = Mth.lerp(hold, 0.12f, -0.5f);
                gTwist = 0.2f * hold;
            }
            default -> {
            }
        }
        drive.blendIn(ground, state.clear().set(RigPose.FOLD, gFold).set(RigPose.STROKE, gStroke).set(RigPose.SWEEP, gSweep)
                .set(RigPose.FAN, gFan).set(RigPose.TWIST, gTwist));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.95f)
                .set(RigPose.SWEEP, 0.4f).set(RigPose.TWIST, 0.12f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.25f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.2f).set(RigPose.FAN, 0.9f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.12f).set(RigPose.STROKE, 0.5f)
                .set(RigPose.SWEEP, 0.4f).set(RigPose.TWIST, -0.3f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.03f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.16f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.1f).set(RigPose.SWEEP, 0.06f)
                .set(RigPose.BEND_TIPS, -0.1f).set(RigPose.ASYM, m.bank * 0.35f));
        float dd = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.2f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.15f, -0.55f)).set(RigPose.FAN, Mth.lerp(dd, 0.85f, 0.3f))
                .set(RigPose.STROKE, 0.05f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.STROKE, 0.42f).set(RigPose.SWEEP, 0.6f)
                .set(RigPose.TWIST, -0.75f).set(RigPose.BEND_TIPS, 0.18f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.88f).set(RigPose.SWEEP, -0.62f)
                .set(RigPose.STROKE, 0.06f).set(RigPose.FAN, 0f));
        billow += 0.3f * m.w(WingMotion.LANDING) + 0.25f * m.w(WingMotion.CRUISE) + 0.55f * m.w(WingMotion.BRAKE);
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.15f));
            drive.add(RigPose.STROKE, m.stall * 0.45f * Mth.sin(time * 1.1f));
            drive.add(RigPose.TWIST, m.stall * 0.4f * Mth.sin(time * 0.9f));
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float sn = Mth.sin(ph);
            float cs = Mth.cos(ph);
            float back = Math.max(0f, -sn);
            drive.add(RigPose.STROKE, hw * 0.65f * cs);
            drive.add(RigPose.SWEEP, hw * 0.2f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.45f * sn);
            drive.add(RigPose.FOLD, hw * 0.25f * back);
            drive.add(RigPose.BEND_TIPS, hw * 0.45f * (Mth.cos(ph - 0.6f) - cs));
            billow += hw * 0.45f * Math.max(0f, sn);
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            drive.add(RigPose.STROKE, a * STROKE.at(t));
            drive.add(RigPose.SWEEP, a * SWEEP.at(t));
            drive.add(RigPose.FOLD, a * FLEX.at(t));
            drive.add(RigPose.TWIST, a * TWIST.at(t));
            drive.add(RigPose.FAN, a * (FAN.at(t) - 1f));
            drive.add(RigPose.BEND_ELBOW, a * 0.16f * STROKE.lagged(t, 0.04f));
            drive.add(RigPose.BEND_WRIST, a * 0.3f * STROKE.lagged(t, 0.07f));
            drive.add(RigPose.BEND_TIPS, a * 0.5f * STROKE.lagged(t, 0.11f));
            billow += a * BILLOW.at(t);
        }
        if (settle > 0.001f) {
            drive.add(RigPose.STROKE, settle * 0.1f * Mth.sin(time * 1.2f));
            billow += settle * 0.3f * Mth.sin(time * 1.9f);
        }
        drive.add(RigPose.FOLD, ground * (1f - idle) * -0.02f * (1f + Mth.sin(time * 0.06f)));
        drive.add(RigPose.STROKE, ground * (1f - idle) * 0.025f * Mth.sin(time * 0.06f + 0.5f));
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.STROKE, glide * 0.025f * Mth.sin(time * 0.25f));
        float flutter = glide * 0.06f * (Mth.sin(time * 2.3f) + 0.6f * Mth.sin(time * 3.9f))
                + m.w(WingMotion.BRAKE) * 0.09f * Mth.sin(time * 3f) + gleam * 0.12f * Mth.sin(time * 7.3f);

        float preen = variant == 3 ? idle : 0f;
        for (int i = 0; i < 2; i++) {
            side.copy(drive);
            if (preen > 0.001f) {
                float w = preen * Mth.sin((float) Math.PI * Mth.clamp(i == 0 ? p * 2f : p * 2f - 1f, 0f, 1f)) * ground;
                side.add(RigPose.FOLD, -0.55f * w);
                side.add(RigPose.STROKE, -0.25f * w);
                side.add(RigPose.SWEEP, 0.4f * w);
                side.add(RigPose.TWIST, 0.4f * w);
            }
            if (sway > 0.001f) {
                float b = (i == 0 ? 1f : -1f) * sway * 0.25f * Mth.sin(time * 0.15f);
                side.add(RigPose.STROKE, b);
                side.add(RigPose.FOLD, -0.25f * sway);
            }
            side.add(RigPose.STROKE, gleam * 0.03f * Mth.sin(time * 6.1f + i));
            side.set(RigPose.FOLD, Mth.clamp(side.get(RigPose.FOLD), 0f, 1f));
            main[i].apply(side, billow, flutter, 0f);
            hind.copy(side);
            if (m.beating()) {
                float t = m.beat - 0.1f;
                hind.add(RigPose.STROKE, m.beatWeight * 0.9f * (STROKE.at(t) - STROKE.at(m.beat)));
            }
            hind.add(RigPose.SWEEP, -0.12f * (1f - ground));
            hind.set(RigPose.FOLD, Mth.clamp(hind.get(RigPose.FOLD) + 0.05f, 0f, 1f));
            back[i].apply(hind, billow * 0.85f, flutter * 1.1f, 0f);
        }
        for (int k = 0; k < 6; k++) {
            extraPitch[k] = 0f;
            extraYaw[k] = sway * 0.2f * Mth.sin(time * 0.15f - k * 0.4f) + gleam * 0.03f * Mth.sin(time * 5.3f - k);
        }
        tail.apply(memory, pt, extraPitch, extraYaw);
        float turn = Mth.clamp(m.bank * 2f, -1f, 1f) * (1f - ground);
        float ripple = 0.1f * Mth.sin(time * 0.5f) + 0.12f * m.w(WingMotion.CRUISE) * Mth.sin(time * 1.7f) + gleam * 0.2f * Mth.sin(time * 6.7f);
        tail.fins(0.2f + 0.2f * m.w(WingMotion.BRAKE), 0.3f + 0.4f * Math.max(0f, turn) + 0.3f * m.w(WingMotion.BRAKE),
                0.3f + 0.4f * Math.max(0f, -turn) + 0.3f * m.w(WingMotion.BRAKE), ripple);
    }

    private static float smooth(float x) {
        return x * x * (3f - 2f * x);
    }
}
