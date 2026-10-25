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

public final class CrowRig extends WingRig {
    private static final int ROUSE = 0;
    private static final int ALULA = 1;
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.5f, 0.85f, 0.82f, 0.32f, -0.14f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.25f, 0.22f, 0.06f, -0.61f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.2f, 0.24f, 0.12f, -0.7f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.34f, 0.08f, 0.4f, 0.38f, 0f, -0.4f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.3f, 0.62f, 0.25f, 0f, -0.05f, 0.08f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.5f, 0.85f, 0.35f, -0.08f, -0.1f, 0.12f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.32f, 0.1f, -0.05f, -0.28f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.15f, -0.35f, 0.05f, 0.25f, 0.25f, 0.15f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.6f, -0.25f, 0f, 0.15f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private Bone rightAlula;
    private Bone leftAlula;

    public CrowRig() {
        super("crow");
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.85f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.85f);
        rightAlula = bones.get("right_alula");
        leftAlula = bones.get("left_alula");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean rousing = m.rouse > 0f;
        memory.spring(ROUSE, rousing ? 1f : 0f, rousing ? 0.22f : 0.14f, rousing ? 0.6f : 0.66f);
        float slow = m.w(WingMotion.BRAKE) + m.w(WingMotion.LANDING) + m.stall + 0.5f * m.w(WingMotion.CHARGE);
        memory.spring(ALULA, Math.min(1f, slow), 0.25f, 0.6f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float rouse = memory.get(ROUSE, m.partialTick);
        float flick = m.rouse > 0f ? Math.max(0f, Mth.sin(m.rouseProgress * Mth.TWO_PI * 2f)) : 0f;
        float time = m.time;
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, 1f - 0.35f * flick * rouse)
                .set(RigPose.STROKE, 0.1f + 0.25f * flick * rouse).set(RigPose.FAN, 0.2f * flick)
                .set(RigPose.SWEEP, 0.1f * flick));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.FAN, 1.25f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.15f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.8f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.3f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.05f).set(RigPose.BEND_TIPS, -0.15f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1.1f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.1f).set(RigPose.SWEEP, 0.02f)
                .set(RigPose.FAN, 1.12f).set(RigPose.BEND_TIPS, 0.3f).set(RigPose.ASYM, m.bank * 0.35f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.2f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.15f, -0.5f)).set(RigPose.FAN, Mth.lerp(d, 0.6f, 0.1f))
                .set(RigPose.STROKE, 0.05f).set(RigPose.BEND_TIPS, 0.1f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.22f).set(RigPose.BEND_TIPS, 0.1f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.82f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);

        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.BEND_TIPS, glide * 0.06f * (Mth.sin(time * 0.41f) + Mth.sin(time * 0.27f)));
        float slow = m.w(WingMotion.BRAKE) - FlightRules.chute(m) + m.w(WingMotion.LANDING);
        drive.add(RigPose.BEND_TIPS, slow * 0.12f * Mth.sin(time * 2.8f));
        drive.add(RigPose.FAN, slow * 0.06f * Mth.sin(time * 3.3f));

        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        drive.set(RigPose.FOLD, fold);
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.25f));
        FeatherWing.TipCurve tips = (k, n) -> k < 6 ? 1.2f - 0.08f * k : 0.5f;
        right.apply(drive, tips);
        left.apply(drive, tips);
        float chute = FlightRules.chute(m);
        FlightRules.chuteArm(right.arm, 1f, chute);
        FlightRules.chuteArm(left.arm, -1f, chute);
        float alula = memory.get(ALULA, m.partialTick) * (1f - fold);
        rightAlula.rotate(0f, 0.35f * alula, 0.55f * alula);
        leftAlula.rotate(0f, -0.35f * alula, -0.55f * alula);
    }
}
