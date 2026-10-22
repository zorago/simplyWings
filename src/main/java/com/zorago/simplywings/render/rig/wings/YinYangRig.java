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

public final class YinYangRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int TURN = 2;
    private static final int[] IDLES = {80, 70, 90, 96, 100};
    private static final float[] KEYS = Cycle.beat(0.5f, 0.2f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.75f, 0.15f, 1.05f, 1.1f, -0.3f, -0.9f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.15f, -0.15f, -0.22f, -0.08f, 0.28f, 0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.1f, 0.5f, 0.25f, 0f, -0.05f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.1f, 0.62f, 0.3f, -0.08f, -0.1f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.28f, 0.1f, -0.04f, -0.26f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.15f, -0.2f, 0.1f, 0.25f, 0.22f, 0.1f);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private final RigPose side = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private Bone taiji;

    public YinYangRig() {
        super("yin_yang");
    }

    @Override
    public int[] idleLengths() {
        return IDLES;
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.9f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.9f);
        taiji = bones.get("taiji");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.08f, 0.82f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.18f, 0.72f);
        boolean spin = m.rouse > 0f && m.rouseVariant % 5 == 0;
        float speed = 0.006f + 0.012f * (1f - m.w(WingMotion.HOVER)) * (1f - m.w(WingMotion.GROUND))
                + (spin ? 0.075f * m.rouse : 0f);
        if (memory.now(TURN) > Mth.TWO_PI) {
            memory.snap(TURN, memory.now(TURN) - Mth.TWO_PI);
        }
        memory.set(TURN, memory.now(TURN) + speed);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 5;
        float p = m.rouseProgress;
        float hold = idle * smooth(Mth.clamp(p * 3f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 3f, 0f, 1f));

        float gFold = 1f, gStroke = 0.14f, gSweep = -0.08f, gWrist = -0.12f, gFan = 0f;
        switch (variant) {
            case 1 -> {
                gSweep -= 0.25f * hold;
                gWrist -= 0.3f * hold;
                gStroke += 0.15f * hold;
            }
            case 2 -> {
                float breath = idle * (0.5f - 0.5f * Mth.cos(p * Mth.TWO_PI * 2f));
                gFold = Mth.lerp(breath, 1f, 0.35f);
                gStroke += 0.2f * breath;
                gFan = 0.9f * breath;
            }
            case 4 -> {
                gFold = Mth.lerp(hold, 1f, 0.2f);
                gStroke = Mth.lerp(hold, gStroke, 0.45f);
                gSweep = Mth.lerp(hold, gSweep, 0.9f);
                gFan = 1f * hold;
            }
            default -> {
            }
        }
        drive.blendIn(ground, state.clear().set(RigPose.FOLD, gFold).set(RigPose.STROKE, gStroke).set(RigPose.SWEEP, gSweep)
                .set(RigPose.WRIST, gWrist).set(RigPose.FAN, gFan));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.06f).set(RigPose.STROKE, 0.95f)
                .set(RigPose.SWEEP, 0.35f).set(RigPose.FAN, 1.2f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.1f).set(RigPose.STROKE, 0.55f)
                .set(RigPose.SWEEP, 0.38f).set(RigPose.TWIST, -0.4f).set(RigPose.FAN, 1.2f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.03f).set(RigPose.STROKE, 0.22f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1.15f).set(RigPose.BEND_TIPS, 0.2f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.1f).set(RigPose.SWEEP, 0.04f)
                .set(RigPose.FAN, 1.15f).set(RigPose.BEND_TIPS, 0.3f).set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.25f, 0.7f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.2f, -0.6f)).set(RigPose.FAN, Mth.lerp(d, 0.5f, 0f))
                .set(RigPose.STROKE, Mth.lerp(d, 0.1f, 0.2f)));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.55f).set(RigPose.TWIST, -0.78f).set(RigPose.FAN, 1.25f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.STROKE, 0.18f).set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.1f));
            drive.add(RigPose.STROKE, m.stall * 0.3f * Mth.sin(time * 0.8f));
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float s = Mth.sin(ph);
            float c = Mth.cos(ph);
            float back = Math.max(0f, -s);
            drive.add(RigPose.STROKE, hw * 0.6f * c);
            drive.add(RigPose.SWEEP, hw * 0.12f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.35f * s);
            drive.add(RigPose.ELBOW, hw * 0.25f * back);
            drive.add(RigPose.WRIST, hw * 0.4f * back);
            drive.add(RigPose.HAND_TWIST, hw * -0.4f * back);
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
            drive.add(RigPose.BEND_ELBOW, a * 0.18f * STROKE.lagged(t, 0.04f));
            drive.add(RigPose.BEND_WRIST, a * 0.28f * STROKE.lagged(t, 0.07f));
            drive.add(RigPose.BEND_TIPS, a * 0.4f * STROKE.lagged(t, 0.1f));
        }
        if (settle > 0.001f) {
            drive.add(RigPose.STROKE, settle * 0.08f * Mth.sin(time * 1f));
            drive.add(RigPose.FAN, settle * 0.12f);
        }
        drive.add(RigPose.STROKE, ground * (1f - idle) * 0.018f * Mth.sin(time * 0.05f));
        drive.add(RigPose.BEND_TIPS, (m.w(WingMotion.CRUISE) + 0.5f * m.w(WingMotion.HOVER)) * 0.04f * Mth.sin(time * 0.4f));

        float scale = variant == 0 ? idle * Mth.sin((float) Math.PI * p) : 0f;
        float exchange = variant == 3 ? idle * Mth.sin(p * Mth.TWO_PI) : 0f;
        for (int i = 0; i < 2; i++) {
            float sgn = i == 0 ? 1f : -1f;
            side.copy(drive);
            if (scale > 0.001f) {
                side.add(RigPose.STROKE, sgn * 0.22f * scale * Mth.sin(time * 0.12f));
                side.add(RigPose.FOLD, -0.2f * scale);
            }
            if (Math.abs(exchange) > 0.001f) {
                float open = Math.max(0f, sgn * exchange) * ground;
                side.add(RigPose.FOLD, -0.75f * open);
                side.add(RigPose.STROKE, 0.3f * open);
                side.add(RigPose.SWEEP, 0.3f * open);
                side.add(RigPose.FAN, 1f * open);
            }
            float fold = Mth.clamp(side.get(RigPose.FOLD), 0f, 1f);
            side.set(RigPose.FOLD, fold);
            side.set(RigPose.FAN, Mth.clamp(side.get(RigPose.FAN), 0f, 1.25f));
            FeatherWing wing = i == 0 ? right : left;
            FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.15f - 0.1f * k);
            wing.apply(side, tips);
            wing.stack(fold, 0.68f, 0.55f, 0.45f);
        }
        float turn = memory.get(TURN, pt);
        float glow = 1f + 0.08f * (variant == 4 ? hold : 0f);
        taiji.rotate(0f, 0f, turn).scale(glow, glow, 1f);
    }

    private static float smooth(float x) {
        return x * x * (3f - 2f * x);
    }
}
