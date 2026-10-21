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

public final class PigeonRig extends WingRig {
    private static final int CLAP = 0;
    private static final int ROUSE = 1;
    private static final float[] KEYS = Cycle.beat(0.45f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.8f, 0.1f, 1.05f, 1.1f, -0.3f, -0.95f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.2f, -0.15f, -0.2f, -0.05f, 0.25f, 0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.1f, 0.5f, 0.25f, 0f, -0.05f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.1f, 0.65f, 0.3f, -0.1f, -0.1f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.25f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.1f, -0.2f, 0.1f, 0.2f, 0.15f, 0.05f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.5f, -0.2f, 0f, 0.15f, 0.05f);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private Bone rightAlula;
    private Bone leftAlula;

    public PigeonRig() {
        super("pigeon");
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
        float slow = 1f - Mth.clamp((m.speed - 6f) / 12f, 0f, 1f);
        float target = Math.max(slow, Math.max(m.w(WingMotion.CHARGE), m.w(WingMotion.AIR)));
        memory.ease(CLAP, target, 0.15f);
        boolean rousing = m.rouse > 0f;
        memory.spring(ROUSE, rousing ? 1f : 0f, rousing ? 0.16f : 0.1f, rousing ? 0.68f : 0.72f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        float rouse = memory.get(ROUSE, m.partialTick);
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, Mth.lerp(rouse, 1f, 0.7f))
                .set(RigPose.STROKE, Mth.lerp(rouse, 0.12f, 0.35f)).set(RigPose.FAN, 0.3f * rouse));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.06f).set(RigPose.STROKE, 1.25f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.15f).set(RigPose.TWIST, 0.15f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.15f).set(RigPose.STROKE, 0.65f)
                .set(RigPose.SWEEP, 0.35f).set(RigPose.TWIST, -0.35f).set(RigPose.FAN, 1.1f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.15f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.45f).set(RigPose.SWEEP, 0.02f)
                .set(RigPose.FAN, 1.05f).set(RigPose.BEND_TIPS, 0.12f).set(RigPose.ASYM, m.bank * 0.35f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.2f, 0.55f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.15f, -0.5f)).set(RigPose.FAN, Mth.lerp(d, 0.7f, 0.2f))
                .set(RigPose.STROKE, Mth.lerp(d, 0.2f, 0.35f)));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, 0.5f)
                .set(RigPose.SWEEP, 0.55f).set(RigPose.TWIST, -0.75f).set(RigPose.FAN, 1.2f).set(RigPose.BEND_TIPS, -0.1f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.82f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.1f).set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.12f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.45f));
            drive.set(RigPose.FAN, Mth.lerp(m.stall, drive.get(RigPose.FAN), 1.2f));
        }

        float clap = memory.get(CLAP, m.partialTick);
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float s = Mth.sin(ph);
            float c = Mth.cos(ph);
            float back = Math.max(0f, -s);
            drive.add(RigPose.STROKE, hw * 0.85f * c + hw * 0.25f * clapBump(Cycle.wrap(ph / Mth.TWO_PI), 0.0f));
            drive.add(RigPose.SWEEP, hw * 0.2f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.55f * s);
            drive.add(RigPose.ELBOW, hw * 0.35f * back);
            drive.add(RigPose.WRIST, hw * 0.5f * back);
            drive.add(RigPose.HAND_TWIST, hw * -0.5f * back);
            drive.add(RigPose.BEND_TIPS, hw * 0.5f * (Mth.cos(ph - 0.5f) - c));
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            float bump = clapBump(t, 0.44f);
            drive.add(RigPose.STROKE, a * (STROKE.at(t) + 0.4f * clap * bump));
            drive.add(RigPose.SWEEP, a * (SWEEP.at(t) - 0.15f * clap * bump));
            drive.add(RigPose.ELBOW, a * ELBOW.at(t) * (1f - 0.5f * clap * bump));
            drive.add(RigPose.WRIST, a * WRIST.at(t) * (1f - 0.6f * clap * bump));
            drive.add(RigPose.TWIST, a * TWIST.at(t));
            drive.add(RigPose.FAN, a * FAN.at(t));
            drive.add(RigPose.HAND_TWIST, a * HAND_TWIST.at(t));
            drive.add(RigPose.BEND_ELBOW, a * 0.2f * STROKE.lagged(t, 0.04f));
            drive.add(RigPose.BEND_WRIST, a * 0.3f * STROKE.lagged(t, 0.07f));
            drive.add(RigPose.BEND_TIPS, a * 0.35f * STROKE.lagged(t, 0.1f));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.STROKE, sw * 0.55f * Mth.sin(time * 1.2f));
            drive.add(RigPose.ELBOW, sw * 0.45f * (0.5f + 0.5f * Mth.sin(time * 1.7f)));
            drive.add(RigPose.TWIST, sw * 0.5f * Mth.sin(time * 0.9f));
            drive.add(RigPose.ASYM, sw * 0.35f * Mth.sin(time * 0.6f));
        }
        float ground = m.w(WingMotion.GROUND);
        drive.add(RigPose.FOLD, ground * -0.02f * (1f + Mth.sin(time * 0.1f)));
        drive.add(RigPose.STROKE, ground * 0.02f * Mth.sin(time * 0.1f + 0.5f));
        if (m.rouse > 0f) {
            float shake = Mth.sin(time * 2.4f) * 0.07f * m.rouse;
            drive.add(RigPose.SWEEP, shake);
            drive.add(RigPose.BEND_TIPS, shake * 1.4f);
        }
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.STROKE, glide * 0.03f * Mth.sin(time * 0.35f));
        drive.add(RigPose.BEND_TIPS, glide * 0.04f * Mth.sin(time * 0.53f));
        float brake = m.w(WingMotion.BRAKE);
        drive.add(RigPose.BEND_TIPS, brake * 0.12f * Mth.sin(time * 2.7f));

        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        drive.set(RigPose.FOLD, fold);
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.2f));
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.1f - 0.09f * k);
        right.apply(drive, tips);
        left.apply(drive, tips);
        right.stack(fold, 0.68f, 0.5f, 0.4f);
        left.stack(fold, 0.68f, 0.5f, 0.4f);
        float alula = (m.w(WingMotion.BRAKE) + m.w(WingMotion.LANDING)) * (1f - fold);
        rightAlula.rotate(0f, 0.3f * alula, 0.5f * alula);
        leftAlula.rotate(0f, -0.3f * alula, -0.5f * alula);
    }

    private static float clapBump(float t, float at) {
        float x = (Cycle.wrap(t - at + 0.5f) - 0.5f) / 0.045f;
        return (float) Math.exp(-x * x);
    }
}
