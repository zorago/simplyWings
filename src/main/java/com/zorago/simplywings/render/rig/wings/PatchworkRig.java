package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class PatchworkRig extends WingRig {
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.5f, 0.82f, 0.78f, 0.35f, -0.14f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.25f, 0.22f, 0.06f, -0.61f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.2f, 0.24f, 0.12f, -0.7f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.25f, 0.05f, 0.3f, 0.28f, 0f, -0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.2f, 0.5f, 0.2f, 0f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.35f, 0.6f, 0.25f, -0.05f, -0.1f, 0.05f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.25f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.1f, -0.2f, 0.05f, 0.15f, 0.15f, 0.05f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.5f, -0.2f, 0f, 0.15f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};
    private static final float LEFT_LAG = 0.035f;
    private static final float LEFT_AMPLITUDE = 0.92f;

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private Bone flap;

    public PatchworkRig() {
        super("patchwork");
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.85f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.85f);
        flap = bones.get("left_flap");
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float beat = m.beat;
        float weight = m.beatWeight;
        float hoverPhase = m.hoverPhase;
        float chute = FlightRules.chute(m);
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.1f - 0.09f * k);
        for (int i = 0; i < 2; i++) {
            boolean lagging = i == 1;
            float lag = lagging ? LEFT_LAG : 0f;
            m.beat = beat >= 0f && lagging ? Mth.positiveModulo(beat - lag, 1f) : beat;
            m.beatWeight = lagging ? weight * LEFT_AMPLITUDE : weight;
            m.hoverPhase = hoverPhase - lag * Mth.TWO_PI;
            build(m, lag);
            FeatherWing wing = lagging ? left : right;
            wing.apply(drive, tips);
            FlightRules.chuteArm(wing.arm, lagging ? -1f : 1f, chute);
        }
        m.beat = beat;
        m.beatWeight = weight;
        m.hoverPhase = hoverPhase;
        float time = m.time;
        float flying = 1f - m.w(WingMotion.GROUND);
        float wind = flying * (0.15f + 0.25f * Mth.clamp(m.speed / 20f, 0f, 1f));
        flap.rotate(wind * Mth.sin(time * 2.3f) + 0.1f * flying, 0f, -0.12f * wind * Mth.sin(time * 1.7f + 0.6f));
    }

    private void build(WingMotion m, float lag) {
        drive.zero();
        float time = m.time;
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, 1f).set(RigPose.STROKE, 0.12f));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.FAN, 1.15f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.15f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.8f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.3f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.25f).set(RigPose.FAN, 0.9f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1.1f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.08f).set(RigPose.SWEEP, 0.05f)
                .set(RigPose.FAN, 1.1f).set(RigPose.BEND_TIPS, 0.15f).set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.15f, 0.55f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.1f, -0.45f)).set(RigPose.FAN, Mth.lerp(d, 0.7f, 0.15f))
                .set(RigPose.STROKE, 0.05f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.15f).set(RigPose.BEND_TIPS, 0.1f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.82f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);
        if (m.beating()) {
            float hiccup = Math.max(0f, Mth.sin(time * 0.071f + lag * 40f) - 0.93f) * 8f;
            drive.add(RigPose.STROKE, -m.beatWeight * 0.1f * hiccup);
            drive.add(RigPose.ELBOW, m.beatWeight * 0.2f * hiccup);
        }
        float ground = m.w(WingMotion.GROUND);
        drive.add(RigPose.FOLD, ground * -0.02f * Mth.sin(time * 0.09f + lag * 30f));
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.BEND_TIPS, glide * 0.06f * Mth.sin(time * 0.5f + lag * 25f));
        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.2f));
    }
}
