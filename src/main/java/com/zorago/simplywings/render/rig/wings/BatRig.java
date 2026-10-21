package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.WingRig;
import java.util.Arrays;
import net.minecraft.util.Mth;

public final class BatRig extends WingRig {
    private static final int ROUSE = 0;
    private static final int FINGERS = 4;
    private static final float FOLD_ARM = 1.26f;
    private static final float FOLD_ELBOW = 2.74f;
    private static final float FOLD_WRIST = 2.79f;
    private static final float FINGER_CLOSED = -0.06f;
    private static final float[] KEYS = Cycle.beat(0.4f, 0.22f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.8f, 0.1f, 1.1f, 1.15f, -0.25f, -1.0f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.2f, -0.2f, -0.3f, -0.05f, 0.35f, 0.4f);
    private static final Cycle FLEX = Cycle.of(KEYS, 0.18f, 0.48f, 0.22f, 0f, 0f, 0.06f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.95f, 0.75f, 0.9f, 1f, 1f, 1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.35f, 0.1f, -0.05f, -0.3f, -0.1f);
    private static final Cycle BILLOW = Cycle.of(KEYS, 0.05f, -0.15f, -0.1f, 0.35f, 0.5f, 0.2f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.4f, -0.15f, 0f, 0.1f, 0.05f);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private float billow;
    private float flutter;
    private Side right;
    private Side left;

    public BatRig() {
        super("bat");
    }

    private static final class Side {
        final float s;
        final Bone shoulder;
        final Bone arm;
        final Bone armWeb;
        final Bone forearm;
        final Bone forearmWeb;
        final Bone hand;
        final Bone thumb;
        final Bone[] finger = new Bone[FINGERS];
        final Bone[] tip = new Bone[FINGERS];
        final Bone[] web = new Bone[FINGERS];
        final Bone[] tipWeb = new Bone[FINGERS];

        Side(Bones b, String p, float s) {
            this.s = s;
            shoulder = b.get(p + "shoulder");
            arm = b.get(p + "arm");
            armWeb = b.get(p + "arm_membrane");
            forearm = b.get(p + "forearm");
            forearmWeb = b.get(p + "forearm_membrane");
            hand = b.get(p + "hand");
            thumb = b.get(p + "thumb");
            for (int k = 0; k < FINGERS; k++) {
                String f = p + "finger_" + (k + 1);
                finger[k] = b.get(f);
                tip[k] = b.get(f + "_tip");
                web[k] = b.get(f + "_web");
                tipWeb[k] = b.get(f + "_tip_web");
            }
        }

        void apply(RigPose d, float billow, float flutter) {
            float fold = d.get(RigPose.FOLD);
            float fan = Mth.clamp(d.get(RigPose.FAN), 0f, 1f) * (1f - fold);
            float spread = 1f - 0.62f * fold;
            shoulder.rotate(d.get(RigPose.TWIST), s * d.get(RigPose.STROKE) - d.get(RigPose.ASYM), s * d.get(RigPose.SWEEP));
            arm.rotate(0f, 0f, s * -FOLD_ARM * fold);
            forearm.rotate(0f, s * d.get(RigPose.BEND_ELBOW), s * (FOLD_ELBOW * fold - d.get(RigPose.ELBOW)));
            hand.rotate(d.get(RigPose.HAND_TWIST), s * d.get(RigPose.BEND_WRIST), s * (-FOLD_WRIST * fold - d.get(RigPose.WRIST)));
            thumb.rotate(0f, 0f, s * 0.5f * fold);
            armWeb.rotate(billow * 0.3f, 0f, s * 0.35f * fold).scale(1f, spread, 1f);
            forearmWeb.rotate(billow * 0.3f, 0f, s * -0.2f * fold).scale(1f, spread, 1f);
            for (int k = 0; k < FINGERS; k++) {
                Bone f = finger[k];
                f.rotation(f.rest().xRot, f.rest().yRot, f.rest().zRot * fan + s * FINGER_CLOSED * (1f - fan));
                float reach = 1f - 0.18f * k;
                tip[k].rotate(0f, s * d.get(RigPose.BEND_TIPS) * reach, s * -0.25f * fold);
                web[k].rotate(billow * 0.2f, 0f, 0f).scale(1f, spread, 1f);
                tipWeb[k].rotate(billow * 0.25f + flutter * (0.6f + 0.3f * k), 0f, 0f).scale(1f, spread, 1f);
            }
        }
    }

    @Override
    protected void onBind(Bones bones) {
        right = new Side(bones, "right_", 1f);
        left = new Side(bones, "left_", -1f);
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean rousing = m.rouse > 0f;
        memory.spring(ROUSE, rousing ? 1f : 0f, rousing ? 0.14f : 0.09f, rousing ? 0.7f : 0.74f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        Arrays.fill(drive.v, 0f);
        billow = 0f;
        float rouse = memory.get(ROUSE, m.partialTick);
        float time = m.time;
        float ground = m.w(WingMotion.GROUND);
        add(ground, state.clear().set(RigPose.FOLD, Mth.lerp(rouse, 1f, 0.45f))
                .set(RigPose.STROKE, Mth.lerp(rouse, 0.08f, 0.45f)).set(RigPose.SWEEP, 0.25f * rouse)
                .set(RigPose.FAN, 0.9f * rouse).set(RigPose.TWIST, -0.25f * rouse));
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.95f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.TWIST, 0.15f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.25f).set(RigPose.STROKE, 0.5f)
                .set(RigPose.SWEEP, 0.3f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.06f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.12f).set(RigPose.SWEEP, 0.05f)
                .set(RigPose.BEND_TIPS, -0.12f).set(RigPose.ASYM, m.bank * 0.35f));
        float dd = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.2f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.15f, -0.5f)).set(RigPose.FAN, Mth.lerp(dd, 0.85f, 0.3f))
                .set(RigPose.STROKE, 0.05f));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.STROKE, 0.4f).set(RigPose.SWEEP, 0.6f)
                .set(RigPose.TWIST, -0.75f).set(RigPose.BEND_TIPS, 0.2f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.STROKE, 0.06f).set(RigPose.FAN, 0f));
        billow += 0.3f * m.w(WingMotion.LANDING) + 0.25f * m.w(WingMotion.CRUISE) + 0.6f * m.w(WingMotion.BRAKE);
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.15f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.4f));
            drive.set(RigPose.FAN, Mth.lerp(m.stall, drive.get(RigPose.FAN), 1f));
        }

        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float sn = Mth.sin(ph);
            float cs = Mth.cos(ph);
            float back = Math.max(0f, -sn);
            drive.add(RigPose.STROKE, hw * 0.75f * cs);
            drive.add(RigPose.SWEEP, hw * 0.22f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.55f * sn);
            drive.add(RigPose.FOLD, hw * 0.3f * back);
            drive.add(RigPose.HAND_TWIST, hw * -0.5f * back);
            drive.add(RigPose.BEND_TIPS, hw * 0.5f * (Mth.cos(ph - 0.5f) - cs));
            billow += hw * 0.4f * Math.max(0f, sn);
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            drive.add(RigPose.STROKE, a * STROKE.at(t));
            drive.add(RigPose.SWEEP, a * SWEEP.at(t));
            drive.add(RigPose.FOLD, a * FLEX.at(t));
            drive.add(RigPose.TWIST, a * TWIST.at(t));
            drive.add(RigPose.FAN, a * (FAN.at(t) - 1f));
            drive.add(RigPose.HAND_TWIST, a * HAND_TWIST.at(t));
            drive.add(RigPose.BEND_ELBOW, a * 0.15f * STROKE.lagged(t, 0.04f));
            drive.add(RigPose.BEND_WRIST, a * 0.3f * STROKE.lagged(t, 0.08f));
            drive.add(RigPose.BEND_TIPS, a * 0.45f * STROKE.lagged(t, 0.12f));
            billow += a * BILLOW.at(t);
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.STROKE, sw * 0.6f * Mth.sin(time * 1.3f));
            drive.add(RigPose.FOLD, sw * 0.25f * (0.5f + 0.5f * Mth.sin(time * 1.9f)));
            drive.add(RigPose.TWIST, sw * 0.5f * Mth.sin(time * 1.0f));
            drive.add(RigPose.ASYM, sw * 0.4f * Mth.sin(time * 0.7f));
            billow += sw * 0.3f * Mth.sin(time * 2.3f);
        }
        drive.add(RigPose.FOLD, ground * -0.02f * (1f + Mth.sin(time * 0.09f)));
        drive.add(RigPose.STROKE, ground * 0.025f * Mth.sin(time * 0.09f + 0.5f));
        if (m.rouse > 0f) {
            float shake = Mth.sin(time * 1.7f) * 0.08f * m.rouse;
            drive.add(RigPose.SWEEP, shake);
            billow += shake * 2f;
        }
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.STROKE, glide * 0.02f * Mth.sin(time * 0.31f));
        flutter = glide * 0.06f * (Mth.sin(time * 2.9f) + 0.6f * Mth.sin(time * 4.7f))
                + m.w(WingMotion.BRAKE) * 0.1f * Mth.sin(time * 3.3f)
                + m.w(WingMotion.DIVE) * 0.08f * Mth.sin(time * 5.1f);

        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        right.apply(drive, billow, flutter);
        left.apply(drive, billow, flutter);
    }

    private void add(float weight, RigPose pose) {
        if (weight == 0f) {
            return;
        }
        for (int i = 0; i < RigPose.SIZE; i++) {
            drive.v[i] += weight * pose.v[i];
        }
    }
}
