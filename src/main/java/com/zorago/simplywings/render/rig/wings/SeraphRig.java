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
    private static final int SPLIT = 2;
    private static final int[] IDLES = {70, 60, 56};
    private static final String[] MAJOR = {"", "second_"};
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
    private static final float SPLIT_UPPER = 0.32f;
    private static final float SPLIT_HOVER = 0.62f;
    private static final float SPLIT_LEVEL = 0.52f;
    private static final float CHUTE_UPPER = 0.3f;
    private static final float CHUTE_LOWER = -0.35f;
    private static final float SPLIT_LOWER = -0.08f;
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/seraph_glow.png");

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private final RigPose rudder = new RigPose();
    private final FeatherWing[][] wings = new FeatherWing[2][2];
    private final FeatherWing[] rudders = new FeatherWing[2];

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
        for (int t = 0; t < 2; t++) {
            wings[0][t] = new FeatherWing(bones, "right_" + MAJOR[t], 1f, 1.25f, 2.75f, 2.85f);
            wings[1][t] = new FeatherWing(bones, "left_" + MAJOR[t], -1f, 1.25f, 2.75f, 2.85f);
        }
        rudders[0] = new FeatherWing(bones, "right_rudder_", 1f, 1.1f, 2.6f, 2.7f);
        rudders[1] = new FeatherWing(bones, "left_rudder_", -1f, 1.1f, 2.6f, 2.7f);
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.12f, 0.75f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.2f, 0.7f);
        float launch = m.launch >= 0f ? 1f : 0f;
        float split = Math.max(Math.max(m.hoverBeat, m.beatWeight), Math.max(m.w(WingMotion.CHARGE), launch));
        memory.spring(SPLIT, Mth.clamp(split, 0f, 1f), 0.18f, 0.7f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 3;
        float p = m.rouseProgress;
        float env = idle * Mth.sin((float) Math.PI * p);

        float split = Mth.clamp(memory.get(SPLIT, pt), 0f, 1f);
        float idleSplit = 0f;
        float idleFan = 0f;
        float idleStroke = 0f;
        float rudderFlick = 0f;
        switch (variant) {
            case 0 -> {
                idleSplit = env;
                idleFan = 0.5f * env;
                idleStroke = 0.15f * env;
            }
            case 1 -> rudderFlick = env;
            default -> {
                idleSplit = 0.6f * env * (0.5f + 0.5f * Mth.sin(time * 0.6f));
                idleStroke = 0.1f * env * Mth.sin(time * 0.6f);
            }
        }
        split = Math.max(split, idleSplit * ground);

        drive.zero();
        drive.blendIn(ground, state.clear().set(RigPose.FOLD, 1f - 0.25f * idleSplit).set(RigPose.STROKE, 0.12f + idleStroke)
                .set(RigPose.FAN, idleFan));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.FAN, 1.3f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.15f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.1f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.1f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1.15f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.08f).set(RigPose.SWEEP, 0.04f)
                .set(RigPose.FAN, 1.15f).set(RigPose.BEND_TIPS, 0.3f).set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.25f, 0.7f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.2f, -0.6f)).set(RigPose.FAN, Mth.lerp(d, 0.5f, 0f))
                .set(RigPose.STROKE, 0.05f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.25f).set(RigPose.BEND_TIPS, 0.1f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.STROKE, 0.15f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);
        if (settle > 0.001f) {
            drive.add(RigPose.STROKE, settle * 0.08f * Mth.sin(time * 1.1f));
            drive.add(RigPose.FAN, settle * 0.15f);
        }
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.3f));
        float chute = FlightRules.chute(m);
        float fold = drive.get(RigPose.FOLD);
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.2f - 0.11f * k);
        float level = Mth.clamp(m.w(WingMotion.CRUISE) + m.w(WingMotion.DIVE) + m.w(WingMotion.TUCK), 0f, 1f);
        for (int t = 0; t < 2; t++) {
            float hover = Mth.clamp(m.hoverBeat, 0f, 1f) * (1f - level);
            float upper = Mth.lerp(hover, SPLIT_UPPER, SPLIT_HOVER);
            float offset = split * (t == 0 ? upper : SPLIT_LOWER) * (1f - fold * 0.6f);
            float raise = offset * (1f - level) + chute * (t == 0 ? CHUTE_UPPER : CHUTE_LOWER);
            float stroke = t == 0 ? split * SPLIT_LEVEL * (1f - fold * 0.6f) * level : 0f;
            drive.add(RigPose.SWEEP, raise);
            drive.add(RigPose.STROKE, stroke);
            for (int i = 0; i < 2; i++) {
                wings[i][t].apply(drive, tips);
                FlightRules.chuteArm(wings[i][t].arm, i == 0 ? 1f : -1f, chute);
            }
            drive.add(RigPose.SWEEP, -raise);
            drive.add(RigPose.STROKE, -stroke);
        }

        float flying = Mth.clamp(1f - ground - m.w(WingMotion.CHARGE), 0f, 1f);
        float flutter = split * flying;
        rudder.zero();
        rudder.set(RigPose.FOLD, Mth.lerp(flying, 1f - 0.6f * rudderFlick * Mth.sin((float) Math.PI * p), 0.15f));
        rudder.set(RigPose.STROKE, 0.1f + flutter * 0.18f * Mth.sin(time * 1.9f));
        rudder.set(RigPose.SWEEP, -0.2f * flying + 0.4f * rudderFlick * Mth.sin(time * 1.5f));
        rudder.set(RigPose.FAN, Mth.lerp(flying, 0.4f * rudderFlick, 1f));
        rudder.set(RigPose.ASYM, -0.8f * m.bank * flying);
        rudder.set(RigPose.CAMBER, -0.1f * flying);
        rudder.set(RigPose.BEND_TIPS, flutter * 0.12f * Mth.sin(time * 2.7f));
        FeatherWing.TipCurve rudderTips = (k, n) -> 1f;
        rudders[0].apply(rudder, rudderTips);
        rudders[1].apply(rudder, rudderTips);
    }
}
