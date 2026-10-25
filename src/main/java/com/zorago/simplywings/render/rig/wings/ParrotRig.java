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

public final class ParrotRig extends WingRig {
    private static final int DANCE = 0;
    private static final int TURN = 1;
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.52f, 0.86f, 0.82f, 0.3f, -0.14f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.25f, 0.22f, 0.06f, -0.61f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.2f, 0.24f, 0.12f, -0.7f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.25f, 0.05f, 0.3f, 0.28f, 0f, -0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.25f, 0.55f, 0.2f, 0f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.45f, 0.7f, 0.25f, -0.08f, -0.1f, 0.1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.25f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.1f, -0.2f, 0.1f, 0.2f, 0.2f, 0.05f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.5f, -0.2f, 0f, 0.15f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);

    private final RigPose drive = new RigPose();
    private final RigPose side = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private Bone rightAlula;
    private Bone leftAlula;

    public ParrotRig() {
        super("parrot");
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
        boolean dancing = m.rouse > 0f && m.w(WingMotion.GROUND) > 0.5f;
        memory.spring(DANCE, dancing ? 1f : 0f, dancing ? 0.2f : 0.12f, dancing ? 0.65f : 0.7f);
        memory.spring(TURN, Mth.clamp(m.bank * 2.2f, -1f, 1f), 0.15f, 0.7f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        float dance = memory.get(DANCE, m.partialTick);
        float hop = Mth.sin(time * 1.25f);
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, Mth.lerp(dance, 1f, 0.45f))
                .set(RigPose.STROKE, Mth.lerp(dance, 0.1f, 0.35f + 0.2f * hop)).set(RigPose.SWEEP, 0.15f * dance)
                .set(RigPose.FAN, 0.7f * dance).set(RigPose.TWIST, 0.15f * dance * hop));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.FAN, 1.2f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.15f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.12f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.15f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1.1f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.1f)
                .set(RigPose.SWEEP, 0.02f).set(RigPose.FAN, 1.05f).set(RigPose.BEND_TIPS, 0.15f)
                .set(RigPose.ASYM, m.bank * 0.4f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.2f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.15f, -0.5f)).set(RigPose.FAN, Mth.lerp(d, 0.7f, 0.15f))
                .set(RigPose.STROKE, 0.05f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.2f).set(RigPose.BEND_TIPS, 0.1f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.82f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);

        float ground = m.w(WingMotion.GROUND);
        drive.add(RigPose.STROKE, ground * dance * 0.2f * (1f + Mth.sin(time * 2.5f)));
        drive.add(RigPose.BEND_TIPS, ground * dance * 0.2f * Mth.sin(time * 2.5f - 0.6f));
        float airborne = Mth.clamp(1f - ground - m.w(WingMotion.CHARGE), 0f, 1f) * (1f - FlightRules.chute(m));
        float nervous = airborne * (0.6f + 0.4f * m.beatWeight);
        drive.add(RigPose.BEND_TIPS, nervous * 0.06f * Mth.sin(time * 3.1f) * Mth.sin(time * 0.7f));
        drive.add(RigPose.FAN, nervous * 0.08f * Mth.sin(time * 2.3f + 1f));
        drive.add(RigPose.TWIST, nervous * 0.03f * Mth.sin(time * 4.1f));
        float slow = m.w(WingMotion.BRAKE) - FlightRules.chute(m);
        drive.add(RigPose.BEND_TIPS, slow * 0.12f * Mth.sin(time * 2.8f));

        float turn = memory.get(TURN, m.partialTick) * (1f - ground);
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.1f - 0.09f * k);
        float chute = FlightRules.chute(m);
        finish(right, Math.max(0f, turn), Math.max(0f, -turn), tips);
        finish(left, Math.max(0f, -turn), Math.max(0f, turn), tips);
        FlightRules.chuteArm(right.arm, 1f, chute);
        FlightRules.chuteArm(left.arm, -1f, chute);
        float alula = (m.w(WingMotion.BRAKE) + m.w(WingMotion.LANDING)) * (1f - drive.get(RigPose.FOLD));
        rightAlula.rotate(0f, 0.3f * alula, 0.5f * alula);
        leftAlula.rotate(0f, -0.3f * alula, -0.5f * alula);
    }

    private void finish(FeatherWing wing, float inner, float outer, FeatherWing.TipCurve tips) {
        side.copy(drive);
        side.add(RigPose.ELBOW, 0.3f * inner - 0.08f * outer);
        side.add(RigPose.WRIST, 0.45f * inner - 0.1f * outer);
        side.add(RigPose.FAN, -0.2f * inner + 0.1f * outer);
        side.add(RigPose.SWEEP, 0.1f * outer);
        side.set(RigPose.FOLD, Mth.clamp(side.get(RigPose.FOLD), 0f, 1f));
        side.set(RigPose.FAN, Mth.clamp(side.get(RigPose.FAN), 0f, 1.2f));
        wing.apply(side, tips);
    }
}
