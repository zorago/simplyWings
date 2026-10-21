package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class ParrotRig extends WingRig {
    private static final int DANCE = 0;
    private static final int TURN = 1;
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.8f, 0.05f, 1f, 1.05f, -0.3f, -0.95f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.2f, -0.15f, -0.2f, -0.05f, 0.25f, 0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.1f, 0.55f, 0.25f, 0f, -0.05f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.1f, 0.7f, 0.35f, -0.1f, -0.1f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.25f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.1f, -0.2f, 0.1f, 0.2f, 0.2f, 0.05f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.5f, -0.2f, 0f, 0.15f, 0.05f);

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
                .set(RigPose.STROKE, Mth.lerp(dance, 0.1f, 0.35f + 0.25f * hop)).set(RigPose.SWEEP, 0.15f * dance)
                .set(RigPose.FAN, 0.7f * dance).set(RigPose.TWIST, 0.15f * dance * hop));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.06f).set(RigPose.STROKE, 0.9f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.15f).set(RigPose.TWIST, 0.1f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.12f).set(RigPose.STROKE, 0.7f)
                .set(RigPose.SWEEP, 0.35f).set(RigPose.TWIST, -0.4f).set(RigPose.FAN, 1.15f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.1f)
                .set(RigPose.SWEEP, 0.02f).set(RigPose.FAN, 1.05f).set(RigPose.BEND_TIPS, 0.15f)
                .set(RigPose.ASYM, m.bank * 0.4f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.2f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.15f, -0.5f)).set(RigPose.FAN, Mth.lerp(d, 0.7f, 0.15f))
                .set(RigPose.STROKE, 0.05f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, 0.4f)
                .set(RigPose.SWEEP, 0.55f).set(RigPose.TWIST, -0.75f).set(RigPose.FAN, 1.2f).set(RigPose.BEND_TIPS, -0.1f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.82f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.12f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.4f));
            drive.set(RigPose.FAN, Mth.lerp(m.stall, drive.get(RigPose.FAN), 1.2f));
        }

        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float s = Mth.sin(ph);
            float c = Mth.cos(ph);
            float back = Math.max(0f, -s);
            drive.add(RigPose.STROKE, hw * 0.8f * c);
            drive.add(RigPose.SWEEP, hw * 0.2f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.5f * s);
            drive.add(RigPose.ELBOW, hw * 0.35f * back);
            drive.add(RigPose.WRIST, hw * 0.55f * back);
            drive.add(RigPose.HAND_TWIST, hw * -0.55f * back);
            drive.add(RigPose.BEND_TIPS, hw * 0.5f * (Mth.cos(ph - 0.5f) - c));
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            drive.add(RigPose.STROKE, a * STROKE.at(t));
            drive.add(RigPose.SWEEP, a * SWEEP.at(t));
            drive.add(RigPose.ELBOW, a * ELBOW.at(t));
            drive.add(RigPose.WRIST, a * WRIST.at(t));
            drive.add(RigPose.TWIST, a * TWIST.at(t));
            drive.add(RigPose.FAN, a * FAN.at(t));
            drive.add(RigPose.HAND_TWIST, a * HAND_TWIST.at(t));
            drive.add(RigPose.BEND_ELBOW, a * 0.2f * STROKE.lagged(t, 0.04f));
            drive.add(RigPose.BEND_WRIST, a * 0.3f * STROKE.lagged(t, 0.07f));
            drive.add(RigPose.BEND_TIPS, a * 0.35f * STROKE.lagged(t, 0.1f));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.STROKE, sw * 0.55f * Mth.sin(time * 1.3f));
            drive.add(RigPose.ELBOW, sw * 0.45f * (0.5f + 0.5f * Mth.sin(time * 1.9f)));
            drive.add(RigPose.TWIST, sw * 0.5f * Mth.sin(time * 1f));
            drive.add(RigPose.ASYM, sw * 0.35f * Mth.sin(time * 0.65f));
        }
        float ground = m.w(WingMotion.GROUND);
        drive.add(RigPose.FOLD, ground * -0.02f * (1f + Mth.sin(time * 0.11f)));
        drive.add(RigPose.STROKE, ground * dance * 0.35f * Mth.sin(time * 2.5f));
        drive.add(RigPose.BEND_TIPS, ground * dance * 0.2f * Mth.sin(time * 2.5f - 0.6f));
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.BEND_TIPS, glide * 0.05f * Mth.sin(time * 0.6f));
        float brake = m.w(WingMotion.BRAKE);
        drive.add(RigPose.BEND_TIPS, brake * 0.12f * Mth.sin(time * 2.8f));

        float turn = memory.get(TURN, m.partialTick) * (1f - ground);
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.1f - 0.09f * k);
        finish(right, Math.max(0f, turn), Math.max(0f, -turn), tips);
        finish(left, Math.max(0f, -turn), Math.max(0f, turn), tips);
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
        float fold = Mth.clamp(side.get(RigPose.FOLD), 0f, 1f);
        side.set(RigPose.FOLD, fold);
        side.set(RigPose.FAN, Mth.clamp(side.get(RigPose.FAN), 0f, 1.2f));
        wing.apply(side, tips);
        wing.stack(fold, 0.68f, 0.5f, 0.4f);
    }
}
