package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.WingRig;
import java.util.Arrays;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class DemonRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int SNAP = 2;
    private static final int FINGERS = 4;
    private static final int[] IDLES = {64, 90, 84, 56, 48};
    private static final float FOLD_ARM = 1.26f;
    private static final float FOLD_ELBOW = 2.74f;
    private static final float FOLD_WRIST = 2.79f;
    private static final float FINGER_CLOSED = -0.06f;
    private static final float UP = 0.55f;
    private static final float DOWN = 0.08f;
    private static final float[] KEYS = {0f, 0.3f, UP, UP + 0.02f, UP + DOWN, UP + DOWN + 0.1f, 1f};
    private static final Cycle STROKE = Cycle.of(KEYS, -0.9f, 0.2f, 1.2f, 1.25f, -0.55f, -1.05f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.2f, -0.2f, -0.3f, -0.1f, 0.4f, 0.4f);
    private static final Cycle FLEX = Cycle.of(KEYS, 0.2f, 0.5f, 0.2f, 0f, 0f, 0.06f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.95f, 0.7f, 0.9f, 1f, 1f, 1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.35f, 0.12f, -0.08f, -0.35f, -0.1f);
    private static final Cycle BILLOW = Cycle.of(KEYS, 0.05f, -0.2f, -0.15f, 0.2f, 0.7f, 0.25f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.45f, -0.15f, 0f, 0.12f, 0.05f);
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/demon_glow.png");

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private final RigPose side = new RigPose();
    private float billow;
    private float flutter;
    private Side right;
    private Side left;

    public DemonRig() {
        super("demon");
    }

    @Override
    public ResourceLocation glow() {
        return GLOW;
    }

    @Override
    public int[] idleLengths() {
        return IDLES;
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

        void apply(RigPose d, float billow, float flutter, float tap) {
            float fold = d.get(RigPose.FOLD);
            float fan = Mth.clamp(d.get(RigPose.FAN), 0f, 1f) * (1f - fold);
            float spread = 1f - 0.62f * fold;
            shoulder.rotate(d.get(RigPose.TWIST), s * d.get(RigPose.STROKE) - d.get(RigPose.ASYM), s * d.get(RigPose.SWEEP));
            arm.rotate(0f, 0f, s * -FOLD_ARM * fold);
            forearm.rotate(0f, s * d.get(RigPose.BEND_ELBOW), s * (FOLD_ELBOW * fold - d.get(RigPose.ELBOW)));
            hand.rotate(d.get(RigPose.HAND_TWIST), s * d.get(RigPose.BEND_WRIST), s * (-FOLD_WRIST * fold - d.get(RigPose.WRIST)));
            thumb.rotate(0f, 0f, s * (0.45f * fold + tap));
            armWeb.rotate(billow * 0.3f, 0f, s * 0.35f * fold).scale(1f, spread, 1f);
            forearmWeb.rotate(billow * 0.3f, 0f, s * -0.2f * fold).scale(1f, spread, 1f);
            for (int k = 0; k < FINGERS; k++) {
                Bone f = finger[k];
                f.rotation(f.rest().xRot, f.rest().yRot, f.rest().zRot * fan + s * FINGER_CLOSED * (1f - fan));
                float reach = 1f - 0.15f * k;
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
        memory.spring(IDLE, m.rouse, 0.14f, 0.72f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.3f, 0.6f);
        boolean strike = m.beating() && m.beat >= UP && m.beat < UP + DOWN + 0.06f;
        memory.spring(SNAP, strike ? m.beatWeight : 0f, strike ? 0.7f : 0.35f, 0.4f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        Arrays.fill(drive.v, 0f);
        billow = 0f;
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float snap = memory.get(SNAP, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 5;
        float p = m.rouseProgress;
        float env = idle * Mth.sin((float) Math.PI * p);
        float hold = idle * smooth(Mth.clamp(p * 3f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 3f, 0f, 1f));

        float gFold = 1f, gStroke = 0.1f, gSweep = 0f, gFan = 0f, gTwist = 0f;
        float ember = 0f;
        float tap = 0f;
        switch (variant) {
            case 0 -> {
                gFold = Mth.lerp(hold, 1f, 0.05f);
                gStroke = Mth.lerp(hold, 0.1f, 0.7f);
                gSweep = 0.45f * hold;
                gFan = 1f * hold;
                gTwist = -0.2f * hold;
                billow += hold * 0.2f * Mth.sin(time * 2.1f);
            }
            case 2 -> {
                gFold = Mth.lerp(hold, 1f, 0.35f);
                gStroke = Mth.lerp(hold, 0.1f, -0.8f);
                gSweep = 0.15f * hold;
                gFan = 0.8f * hold;
                gTwist = 0.25f * hold;
            }
            case 3 -> ember = env;
            case 4 -> tap = env * 0.35f * Math.max(0f, Mth.sin(time * 0.9f));
            default -> {
            }
        }
        add(ground, state.clear().set(RigPose.FOLD, gFold).set(RigPose.STROKE, gStroke).set(RigPose.SWEEP, gSweep)
                .set(RigPose.FAN, gFan).set(RigPose.TWIST, gTwist));
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 1.05f)
                .set(RigPose.SWEEP, 0.35f).set(RigPose.TWIST, 0.15f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.32f).set(RigPose.STROKE, 0.38f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.2f).set(RigPose.STROKE, 0.55f)
                .set(RigPose.SWEEP, 0.35f).set(RigPose.TWIST, -0.35f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.05f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.15f));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.1f).set(RigPose.SWEEP, 0.02f)
                .set(RigPose.BEND_TIPS, -0.12f).set(RigPose.ASYM, m.bank * 0.35f));
        float dd = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.2f, 0.62f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.15f, -0.5f)).set(RigPose.FAN, Mth.lerp(dd, 0.85f, 0.3f))
                .set(RigPose.STROKE, 0.05f));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.STROKE, 0.45f).set(RigPose.SWEEP, 0.62f)
                .set(RigPose.TWIST, -0.8f).set(RigPose.BEND_TIPS, 0.2f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.STROKE, 0.06f).set(RigPose.FAN, 0f));
        billow += 0.3f * m.w(WingMotion.LANDING) + 0.25f * m.w(WingMotion.CRUISE) + 0.6f * m.w(WingMotion.BRAKE);
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.15f));
            drive.add(RigPose.STROKE, m.stall * 0.6f * Mth.sin(time * 1.3f));
            drive.add(RigPose.TWIST, m.stall * 0.5f * Mth.sin(time * 1f));
            drive.add(RigPose.ASYM, m.stall * 0.4f * Mth.sin(time * 0.7f));
            billow += m.stall * 0.3f * Mth.sin(time * 2.3f);
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
            drive.add(RigPose.BEND_ELBOW, a * 0.12f * STROKE.lagged(t, 0.03f));
            drive.add(RigPose.BEND_WRIST, a * 0.25f * STROKE.lagged(t, 0.06f));
            drive.add(RigPose.BEND_TIPS, a * 0.4f * STROKE.lagged(t, 0.09f));
            billow += a * BILLOW.at(t);
        }
        billow += snap * 0.35f * Mth.sin(time * 4.2f);
        if (settle > 0.001f) {
            drive.add(RigPose.STROKE, settle * 0.14f * Mth.sin(time * 1.5f));
            billow += settle * 0.4f * Mth.sin(time * 2.2f);
        }
        drive.add(RigPose.FOLD, ground * (1f - idle) * -0.025f * (1f + Mth.sin(time * 0.08f)));
        drive.add(RigPose.STROKE, ground * (1f - idle) * 0.03f * Mth.sin(time * 0.08f + 0.5f));
        billow += ember * 0.25f * Mth.sin(time * 1.6f);
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.STROKE, glide * 0.02f * Mth.sin(time * 0.31f));
        flutter = glide * 0.06f * (Mth.sin(time * 2.9f) + 0.6f * Mth.sin(time * 4.7f))
                + m.w(WingMotion.BRAKE) * 0.1f * Mth.sin(time * 3.3f)
                + m.w(WingMotion.DIVE) * 0.08f * Mth.sin(time * 5.1f)
                + ember * 0.08f * Mth.sin(time * 6.3f);

        float stretch = variant == 1 ? idle : 0f;
        for (int i = 0; i < 2; i++) {
            side.copy(drive);
            if (stretch > 0.001f) {
                float local = Mth.clamp(i == 0 ? p * 2f : p * 2f - 1f, 0f, 1f);
                float w = stretch * Mth.sin((float) Math.PI * local) * ground;
                side.add(RigPose.FOLD, -0.85f * w);
                side.add(RigPose.STROKE, 0.35f * w);
                side.add(RigPose.SWEEP, 0.5f * w);
                side.add(RigPose.FAN, 1f * w);
                side.add(RigPose.BEND_TIPS, -0.2f * w);
            }
            side.set(RigPose.FOLD, Mth.clamp(side.get(RigPose.FOLD), 0f, 1f));
            (i == 0 ? right : left).apply(side, billow, flutter, tap);
        }
    }

    private void add(float weight, RigPose pose) {
        if (weight == 0f) {
            return;
        }
        for (int i = 0; i < RigPose.SIZE; i++) {
            drive.v[i] += weight * pose.v[i];
        }
    }

    private static float smooth(float x) {
        return x * x * (3f - 2f * x);
    }
}
