package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class SeraphRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int[] IDLES = {90, 80, 70, 50, 76};
    private static final String[] TIERS = {"high_", "", "low_"};
    private static final float[] LAG = {-0.07f, 0f, 0.07f};
    private static final float[] LAUNCH_LAG = {0f, 0.06f, 0.12f};
    private static final float[] SIZE = {0.8f, 1f, 0.75f};
    private static final float UP = 0.55f;
    private static final float DOWN = 0.15f;
    private static final float[] KEYS = Cycle.beat(UP, DOWN);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.5f, 0.82f, 0.78f, 0.35f, -0.14f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.25f, 0.22f, 0.06f, -0.61f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.2f, 0.24f, 0.12f, -0.7f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.25f, 0.05f, 0.3f, 0.28f, 0f, -0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.25f, 0.5f, 0.15f, 0f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.45f, 0.65f, 0.2f, -0.05f, -0.1f, 0.1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.12f, -0.04f, -0.3f, -0.12f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.15f, -0.2f, 0.12f, 0.3f, 0.25f, 0.12f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.5f, -0.2f, 0f, 0.15f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/seraph_glow.png");

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private final FeatherWing[][] wings = new FeatherWing[2][3];
    private final WingMotion tier = new WingMotion();

    public SeraphRig() {
        super("seraph");
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
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    protected void onBind(Bones bones) {
        for (int t = 0; t < 3; t++) {
            wings[0][t] = new FeatherWing(bones, "right_" + TIERS[t], 1f, 1.25f, 2.75f, 2.85f);
            wings[1][t] = new FeatherWing(bones, "left_" + TIERS[t], -1f, 1.25f, 2.75f, 2.85f);
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.09f, 0.8f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.2f, 0.7f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 5;
        float p = m.rouseProgress;
        float hold = idle * smooth(Mth.clamp(p * 3f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 3f, 0f, 1f));
        float env = idle * Mth.sin((float) Math.PI * p);

        for (int t = 0; t < 3; t++) {
            drive.zero();
            float gFold = 1f, gStroke = 0.12f, gSweep = 0f, gFan = 0f, gTwist = 0f;
            if (t == 0) {
                gFold = 0.55f;
                gStroke = 0.45f;
                gSweep = 0.85f;
                gFan = 0.6f;
            } else if (t == 2) {
                gFold = 0.45f;
                gStroke = -0.45f;
                gSweep = -0.35f;
                gFan = 0.5f;
                gTwist = 0.2f;
            }
            switch (variant) {
                case 0 -> {
                    if (t == 0) {
                        gStroke = Mth.lerp(hold, gStroke, -0.9f);
                        gSweep = Mth.lerp(hold, gSweep, 0.6f);
                        gFold = Mth.lerp(hold, gFold, 0.3f);
                    } else if (t == 1) {
                        gFold = Mth.lerp(hold, gFold, 0.05f);
                        gStroke = Mth.lerp(hold, gStroke, 0.4f);
                        gFan = Mth.lerp(hold, gFan, 1.2f);
                    } else {
                        gStroke = Mth.lerp(hold, gStroke, -0.85f);
                        gFold = Mth.lerp(hold, gFold, 0.25f);
                    }
                }
                case 1 -> {
                    float local = smooth(Mth.clamp(p * 3f - t * 0.35f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 3f, 0f, 1f)) * idle;
                    gFold = Mth.lerp(local, gFold, 0.02f);
                    gStroke = Mth.lerp(local, gStroke, 0.5f);
                    gSweep = Mth.lerp(local, gSweep, 0.6f - 0.45f * t);
                    gFan = Mth.lerp(local, gFan, 1.25f);
                }
                case 2 -> {
                    gStroke += env * 0.25f * Mth.sin(time * 0.35f - t * 1.2f);
                    gFold -= env * 0.15f;
                }
                case 3 -> {
                    if (t == 0) {
                        gFan += env * 0.3f * Mth.sin(time * 2.4f);
                        gStroke += env * 0.06f * Mth.sin(time * 3.3f);
                    }
                }
                case 4 -> {
                    gStroke = Mth.lerp(hold, gStroke, -0.5f);
                    gSweep = Mth.lerp(hold, gSweep, gSweep - 0.4f);
                    gFold = Mth.lerp(hold, gFold, 0.6f);
                }
                default -> {
                }
            }
            float tierLift = t == 0 ? 0.25f : t == 2 ? -0.2f : 0f;
            drive.blendIn(ground, state.clear().set(RigPose.FOLD, gFold).set(RigPose.STROKE, gStroke)
                    .set(RigPose.SWEEP, gSweep).set(RigPose.FAN, gFan).set(RigPose.TWIST, gTwist));
            drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                    .set(RigPose.SWEEP, 0.75f + 1.6f * tierLift).set(RigPose.FAN, 1.3f).set(RigPose.TWIST, 0.1f)
                    .set(RigPose.BEND_TIPS, -0.15f));
            drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.32f)
                    .set(RigPose.SWEEP, 0.15f + tierLift).set(RigPose.FAN, 0.85f));
            drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.1f).set(RigPose.STROKE, 0.45f)
                    .set(RigPose.SWEEP, 0.3f + tierLift).set(RigPose.FAN, 1.1f));
            drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                    .set(RigPose.SWEEP, 0.12f + tierLift).set(RigPose.FAN, 1.15f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
            drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.08f).set(RigPose.SWEEP, 0.04f + 0.6f * tierLift)
                    .set(RigPose.FAN, 1.15f).set(RigPose.BEND_TIPS, 0.3f).set(RigPose.ASYM, m.bank * 0.3f));
            float d = m.diveDepth;
            drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.25f, 0.7f))
                    .set(RigPose.SWEEP, Mth.lerp(d, -0.2f, -0.6f) + 0.5f * tierLift).set(RigPose.FAN, Mth.lerp(d, 0.5f, 0f))
                    .set(RigPose.STROKE, 0.05f));
            drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                    .set(RigPose.SWEEP, 0.6f + 1.4f * tierLift).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.25f)
                    .set(RigPose.BEND_TIPS, 0.1f));
            drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.6f)
                    .set(RigPose.STROKE, 0.15f).set(RigPose.FAN, 0f));
            copy(m, t);
            FlightRules.apply(tier, drive, BEAT);
            if (settle > 0.001f) {
                drive.add(RigPose.STROKE, settle * 0.1f * Mth.sin(time * 1.1f - t * 0.8f));
                drive.add(RigPose.FAN, settle * 0.15f);
            }
            float fold = drive.get(RigPose.FOLD);
            drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.3f));
            float chute = FlightRules.chute(m);
            FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.2f - 0.11f * k);
            for (int i = 0; i < 2; i++) {
                wings[i][t].apply(drive, tips);
                FlightRules.chuteArm(wings[i][t].arm, i == 0 ? 1f : -1f, chute);
            }
        }
    }

    private void copy(WingMotion m, int t) {
        System.arraycopy(m.w, 0, tier.w, 0, WingMotion.COUNT);
        tier.time = m.time - t * 1.5f;
        tier.partialTick = m.partialTick;
        tier.stall = m.stall;
        tier.brakeBeat = m.brakeBeat;
        tier.launch = m.launch < 0f ? -1f : Mth.clamp(m.launch - LAUNCH_LAG[t], 0f, 1f);
        tier.beat = m.beating() ? Cycle.wrap(m.beat + LAG[t]) : -1f;
        tier.beatWeight = m.beatWeight * (t == 1 ? 1f : 0.9f);
        tier.hoverPhase = m.hoverPhase - t * 0.5f + 0.5f;
        tier.hoverBeat = m.hoverBeat * SIZE[t];
        tier.buzz = m.buzz;
        tier.bank = m.bank;
        tier.diveDepth = m.diveDepth;
        tier.rouse = m.rouse;
        tier.rouseProgress = m.rouseProgress;
        tier.rouseVariant = m.rouseVariant;
        tier.speed = m.speed;
        tier.climb = m.climb;
    }

    private static float smooth(float x) {
        return x * x * (3f - 2f * x);
    }
}
