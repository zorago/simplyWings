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

public final class PeacockRig extends WingRig {
    private static final int DISPLAY = 0;
    private static final int SWAY = 1;
    private static final int TRAIN = 4;
    private static final float[] REST = {-60f, -70f, -80f, -90f};
    private static final float[] OPEN = {80f, 60f, 39f, 18f};
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.85f, 0.1f, 1.05f, 1.1f, -0.35f, -1f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.15f, -0.15f, -0.2f, -0.05f, 0.25f, 0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.15f, 0.65f, 0.35f, 0f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.15f, 0.8f, 0.4f, -0.05f, -0.1f, 0.05f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.3f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.1f, -0.25f, 0.05f, 0.2f, 0.15f, 0.1f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.55f, -0.2f, 0f, 0.15f, 0.05f);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private final Bone[] train = new Bone[TRAIN * 2];
    private Bone rightTrain;
    private Bone leftTrain;

    public PeacockRig() {
        super("peacock");
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.85f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.85f);
        rightTrain = bones.get("right_train");
        leftTrain = bones.get("left_train");
        for (int k = 0; k < TRAIN; k++) {
            train[k] = bones.get("right_train_" + (k + 1));
            train[TRAIN + k] = bones.get("left_train_" + (k + 1));
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        float show = Math.max(m.w(WingMotion.HOVER), Math.max(m.rouse > 0f ? m.w(WingMotion.GROUND) : 0f,
                0.4f * m.w(WingMotion.BRAKE)));
        memory.spring(DISPLAY, show, show > 0.5f ? 0.09f : 0.12f, show > 0.5f ? 0.76f : 0.7f);
        memory.spring(SWAY, Mth.clamp(m.bank * 1.5f - m.climb * 0.03f, -1f, 1f), 0.08f, 0.82f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        float display = Mth.clamp(memory.get(DISPLAY, m.partialTick), 0f, 1.1f);
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, Mth.lerp(display, 1f, 0.75f))
                .set(RigPose.STROKE, Mth.lerp(display, 0.1f, -0.2f)).set(RigPose.SWEEP, -0.15f * display)
                .set(RigPose.FAN, 0.3f * display));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.1f).set(RigPose.STROKE, 0.9f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.1f).set(RigPose.TWIST, 0.1f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.8f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.2f).set(RigPose.STROKE, 0.55f)
                .set(RigPose.SWEEP, 0.35f).set(RigPose.TWIST, -0.35f).set(RigPose.FAN, 1.05f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.15f).set(RigPose.STROKE, 0.15f)
                .set(RigPose.SWEEP, -0.1f).set(RigPose.FAN, 0.9f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, -0.08f).set(RigPose.SWEEP, 0.05f)
                .set(RigPose.FAN, 1f).set(RigPose.BEND_TIPS, -0.05f).set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.25f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.15f, -0.45f)).set(RigPose.FAN, Mth.lerp(d, 0.7f, 0.2f))
                .set(RigPose.STROKE, 0.05f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.4f)
                .set(RigPose.SWEEP, 0.5f).set(RigPose.TWIST, -0.7f).set(RigPose.FAN, 1.15f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.8f).set(RigPose.SWEEP, -0.5f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.15f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.45f));
            drive.set(RigPose.FAN, Mth.lerp(m.stall, drive.get(RigPose.FAN), 1.15f));
        }

        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float s = Mth.sin(ph);
            float c = Mth.cos(ph);
            float back = Math.max(0f, -s);
            drive.add(RigPose.STROKE, hw * 0.4f * c);
            drive.add(RigPose.TWIST, hw * 0.35f * s);
            drive.add(RigPose.ELBOW, hw * 0.3f * back);
            drive.add(RigPose.WRIST, hw * 0.4f * back);
            drive.add(RigPose.BEND_TIPS, hw * 0.35f * (Mth.cos(ph - 0.5f) - c));
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
            drive.add(RigPose.BEND_ELBOW, a * 0.2f * STROKE.lagged(t, 0.05f));
            drive.add(RigPose.BEND_WRIST, a * 0.3f * STROKE.lagged(t, 0.08f));
            drive.add(RigPose.BEND_TIPS, a * 0.35f * STROKE.lagged(t, 0.11f));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.STROKE, sw * 0.6f * Mth.sin(time * 1.1f));
            drive.add(RigPose.ELBOW, sw * 0.5f * (0.5f + 0.5f * Mth.sin(time * 1.6f)));
            drive.add(RigPose.TWIST, sw * 0.5f * Mth.sin(time * 0.8f));
            drive.add(RigPose.ASYM, sw * 0.4f * Mth.sin(time * 0.55f));
        }
        float ground = m.w(WingMotion.GROUND);
        drive.add(RigPose.FOLD, ground * -0.02f * (1f + Mth.sin(time * 0.08f)));
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.STROKE, glide * 0.03f * Mth.sin(time * 0.4f));

        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        drive.set(RigPose.FOLD, fold);
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.2f));
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.1f - 0.08f * k);
        right.apply(drive, tips);
        left.apply(drive, tips);
        right.stack(fold, 0.68f, 0.5f, 0.4f);
        left.stack(fold, 0.68f, 0.5f, 0.4f);

        float open = Mth.clamp(display, 0f, 1f);
        float rattle = open * open * 0.045f * Mth.sin(time * 4.1f) * (0.6f + 0.4f * Mth.sin(time * 0.37f));
        float sway = memory.get(SWAY, m.partialTick);
        float flying = 1f - ground;
        float length = Mth.lerp(Math.max(open, 0.6f * flying), 0.72f, 1f);
        rightTrain.rotate(-0.25f * open, 0f, flying * 0.12f * sway);
        leftTrain.rotate(-0.25f * open, 0f, flying * 0.12f * sway);
        for (int k = 0; k < TRAIN; k++) {
            float delta = (OPEN[k] - REST[k]) * Mth.DEG_TO_RAD * open;
            float trail = flying * (1f - open) * (REST[k] + 90f) * -0.6f * Mth.DEG_TO_RAD;
            float wave = flying * 0.05f * Mth.sin(time * 0.6f - k * 0.7f);
            train[k].rotate(0f, 0f, delta + trail + wave + rattle).scale(length, 1f, 1f);
            train[TRAIN + k].rotate(0f, 0f, -(delta + trail + wave + rattle)).scale(length, 1f, 1f);
        }
    }
}
