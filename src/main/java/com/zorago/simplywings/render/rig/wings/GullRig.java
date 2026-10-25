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

public final class GullRig extends WingRig {
    private static final int ROUSE = 0;
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.07f, 0.38f, 0.66f, 0.63f, 0.24f, -0.12f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.3f, 0f, 0.18f, 0.16f, 0.04f, -0.42f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.45f, -0.1f, 0.18f, 0.2f, 0.08f, -0.55f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.18f, 0.03f, 0.22f, 0.2f, 0f, -0.22f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.15f, 0.35f, 0.12f, -0.05f, -0.1f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.3f, 0.55f, 0.25f, 0.05f, 0.05f, 0.15f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.2f, 0.05f, 0f, -0.15f, -0.05f);
    private static final Cycle FAN = Cycle.of(KEYS, 0f, -0.15f, 0f, 0.05f, 0.05f, 0f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.35f, -0.1f, 0f, 0.05f, 0f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;

    public GullRig() {
        super("gull");
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.85f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.85f);
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean rousing = m.rouse > 0f;
        memory.spring(ROUSE, rousing ? 1f : 0f, rousing ? 0.12f : 0.09f, rousing ? 0.72f : 0.75f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        float rouse = memory.get(ROUSE, m.partialTick);
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, Mth.lerp(rouse, 1f, 0.15f))
                .set(RigPose.STROKE, Mth.lerp(rouse, 0.12f, 0.5f)).set(RigPose.SWEEP, 0.3f * rouse)
                .set(RigPose.WRIST, 0.35f * rouse).set(RigPose.FAN, rouse));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.7f).set(RigPose.WRIST, 0.2f).set(RigPose.FAN, 1.1f).set(RigPose.TWIST, 0.1f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.1f).set(RigPose.FAN, 0.85f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.12f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.ELBOW, -0.15f).set(RigPose.WRIST, 0.3f).set(RigPose.FAN, 1f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.06f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.1f).set(RigPose.FAN, 1f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.22f).set(RigPose.ELBOW, -0.25f)
                .set(RigPose.WRIST, 0.45f).set(RigPose.BEND_WRIST, -0.6f).set(RigPose.BEND_TIPS, -0.1f)
                .set(RigPose.FAN, 1f).set(RigPose.ASYM, m.bank * 0.4f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.2f, 0.55f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.2f, -0.5f)).set(RigPose.WRIST, Mth.lerp(d, 0.4f, 0.2f))
                .set(RigPose.FAN, Mth.lerp(d, 0.8f, 0.3f)).set(RigPose.STROKE, 0.08f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.03f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.1f).set(RigPose.BEND_TIPS, 0.05f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);

        float glide = m.w(WingMotion.CRUISE);
        float gust = Mth.sin(time * 0.19f) + 0.6f * Mth.sin(time * 0.43f + 1f);
        drive.add(RigPose.WRIST, glide * 0.08f * gust);
        drive.add(RigPose.BEND_WRIST, glide * -0.06f * gust);
        drive.add(RigPose.BEND_TIPS, glide * 0.04f * Mth.sin(time * 0.61f));
        float slow = m.w(WingMotion.BRAKE) - FlightRules.chute(m);
        drive.add(RigPose.BEND_TIPS, slow * 0.1f * Mth.sin(time * 2.4f));

        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        drive.set(RigPose.FOLD, fold);
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.1f));
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1f - 0.08f * k);
        right.apply(drive, tips);
        left.apply(drive, tips);
        float chute = FlightRules.chute(m);
        FlightRules.chuteArm(right.arm, 1f, chute);
        FlightRules.chuteArm(left.arm, -1f, chute);
    }
}
