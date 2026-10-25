package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class SwiftRig extends WingRig {
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.06f, 0.3f, 0.52f, 0.5f, 0.2f, -0.12f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, 0f, 0f, 0f, 0f, 0f, 0f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.2f, -0.05f, 0.1f, 0.12f, 0.04f, -0.26f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.12f, 0.02f, 0.14f, 0.13f, 0f, -0.15f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0f, 0f, 0f, 0f, 0f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.05f, 0.2f, 0.1f, 0f, 0f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.2f, 0.05f, 0f, -0.15f, -0.05f);
    private static final Cycle FAN = Cycle.of(KEYS, 0f, -0.05f, 0f, 0.05f, 0.05f, 0f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.25f, -0.1f, 0f, 0.05f, 0f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;

    public SwiftRig() {
        super("swift");
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 3.55f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 3.55f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, 1f).set(RigPose.STROKE, 0.1f));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.FAN, 1f).set(RigPose.TWIST, 0.1f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, -0.1f).set(RigPose.FAN, 0.8f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.15f).set(RigPose.STROKE, 0.4f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.95f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.2f)
                .set(RigPose.FAN, 0.95f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.FOLD, 0.08f).set(RigPose.STROKE, 0.06f)
                .set(RigPose.SWEEP, -0.35f).set(RigPose.WRIST, 0.15f).set(RigPose.FAN, 0.85f)
                .set(RigPose.ASYM, m.bank * 0.45f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.2f, 0.45f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.5f, -0.75f)).set(RigPose.FAN, Mth.lerp(d, 0.6f, 0.2f))
                .set(RigPose.STROKE, 0.12f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.05f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.7f).set(RigPose.SWEEP, -0.75f)
                .set(RigPose.STROKE, 0.1f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);

        float glide = m.w(WingMotion.CRUISE);
        float burst = Mth.sin(time * 0.083f) + 0.5f * Mth.sin(time * 0.031f);
        float flick = Math.max(0f, burst - 0.9f) * 1.6f;
        drive.add(RigPose.STROKE, glide * flick * 0.15f * (1f + Mth.sin(time * 2.6f)));
        drive.add(RigPose.SWEEP, glide * flick * 0.15f);
        drive.add(RigPose.BEND_TIPS, glide * 0.04f * Mth.sin(time * 0.47f));
        drive.set(RigPose.ELBOW, drive.get(RigPose.ELBOW) * (1f - glide));

        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        drive.set(RigPose.FOLD, fold);
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.1f));
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1f - 0.08f * k);
        right.apply(drive, tips);
        left.apply(drive, tips);
        float chute = FlightRules.chute(m);
        FlightRules.chuteArm(right.arm, 1f, chute);
        FlightRules.chuteArm(left.arm, -1f, chute);
        right.shoulder.move(0f, 0f, 0.35f * fold);
    }
}
