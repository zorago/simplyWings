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

public final class YinYangRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int TURN = 2;
    private static final int[] IDLES = {80, 70, 90, 96, 100};
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};
    private static final float[] KEYS = Cycle.beat(0.5f, 0.2f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.55f, 0.88f, 0.84f, 0.35f, -0.14f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.25f, 0.22f, 0.06f, -0.61f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.2f, 0.24f, 0.12f, -0.7f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.25f, 0.05f, 0.3f, 0.28f, 0f, -0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.25f, 0.5f, 0.15f, 0f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.45f, 0.62f, 0.2f, -0.05f, -0.1f, 0.1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.28f, 0.1f, -0.04f, -0.26f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.15f, -0.2f, 0.1f, 0.25f, 0.22f, 0.1f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.5f, -0.2f, 0f, 0.15f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);
    private static final float YIN_LAG = 0.06f;
    private static final float YIN_HOVER_LAG = 0.35f;
    private static final float BALANCE = 0.06f;

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
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
    public String[] tipBones() {
        return TIPS;
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
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 5;
        float p = m.rouseProgress;
        float hold = idle * smooth(Mth.clamp(p * 3f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 3f, 0f, 1f));
        float scale = variant == 0 ? idle * Mth.sin((float) Math.PI * p) : 0f;
        float exchange = variant == 3 ? idle * Mth.sin(p * Mth.TWO_PI) : 0f;
        float chute = FlightRules.chute(m);
        float cruise = m.w(WingMotion.CRUISE);
        float hoverBeat = Mth.clamp(m.hoverBeat, 0f, 1f);
        float beat = m.beat;
        float hoverPhase = m.hoverPhase;
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.15f - 0.1f * k);

        for (int i = 0; i < 2; i++) {
            float sgn = i == 0 ? 1f : -1f;
            boolean yin = i == 1;
            m.beat = beat >= 0f && yin ? Mth.positiveModulo(beat - YIN_LAG, 1f) : beat;
            m.hoverPhase = yin ? hoverPhase - YIN_HOVER_LAG : hoverPhase;
            base(m, variant, hold, idle);
            FlightRules.apply(m, drive, BEAT);

            float balance = cruise * (1f - m.beatWeight) * Mth.sin(time * 0.25f);
            drive.add(RigPose.BEND_TIPS, sgn * BALANCE * balance);
            drive.add(RigPose.STROKE, sgn * 0.03f * balance);
            drive.add(RigPose.TWIST, sgn * 0.04f * balance);
            if (settle > 0.001f) {
                drive.add(RigPose.STROKE, settle * 0.08f * Mth.sin(time + (yin ? 1.2f : 0f)));
                drive.add(RigPose.FAN, settle * 0.12f);
            }
            if (scale > 0.001f) {
                drive.add(RigPose.STROKE, sgn * 0.22f * scale * Mth.sin(time * 0.12f));
                drive.add(RigPose.FOLD, -0.2f * scale);
            }
            if (Math.abs(exchange) > 0.001f) {
                float open = Math.max(0f, sgn * exchange) * ground;
                drive.add(RigPose.FOLD, -0.75f * open);
                drive.add(RigPose.STROKE, 0.3f * open);
                drive.add(RigPose.SWEEP, 0.3f * open);
                drive.add(RigPose.FAN, open);
            }
            drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
            drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.3f));
            FeatherWing wing = yin ? left : right;
            wing.apply(drive, tips);
            FlightRules.chuteArm(wing.arm, sgn, chute);
        }
        m.beat = beat;
        m.hoverPhase = hoverPhase;

        float turn = memory.get(TURN, pt);
        float glow = 1f + 0.08f * (variant == 4 ? hold : 0f) + 0.05f * hoverBeat;
        taiji.rotate(0f, 0f, turn).scale(glow, glow, 1f);
    }

    private void base(WingMotion m, int variant, float hold, float idle) {
        drive.zero();
        float gFold = 1f, gStroke = 0.12f, gSweep = -0.05f, gWrist = -0.1f, gFan = 0f;
        switch (variant) {
            case 1 -> {
                gSweep -= 0.2f * hold;
                gWrist -= 0.3f * hold;
                gStroke += 0.15f * hold;
            }
            case 2 -> {
                float breath = idle * (0.5f - 0.5f * Mth.cos(m.rouseProgress * Mth.TWO_PI * 2f));
                gFold = Mth.lerp(breath, 1f, 0.35f);
                gStroke += 0.2f * breath;
                gFan = 0.9f * breath;
            }
            case 4 -> {
                gFold = Mth.lerp(hold, 1f, 0.25f);
                gStroke = Mth.lerp(hold, gStroke, 0.4f);
                gSweep = Mth.lerp(hold, gSweep, 0.55f);
                gFan = hold;
            }
            default -> {
            }
        }
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, gFold).set(RigPose.STROKE, gStroke)
                .set(RigPose.SWEEP, gSweep).set(RigPose.WRIST, gWrist).set(RigPose.FAN, gFan));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.FAN, 1.3f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.15f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.1f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.15f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.03f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1.15f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.08f).set(RigPose.SWEEP, 0.04f)
                .set(RigPose.FAN, 1.15f).set(RigPose.BEND_TIPS, 0.28f).set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.2f, 0.65f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.15f, -0.55f)).set(RigPose.FAN, Mth.lerp(d, 0.6f, 0.05f))
                .set(RigPose.STROKE, 0.05f).set(RigPose.BEND_TIPS, 0.1f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.25f).set(RigPose.BEND_TIPS, 0.1f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.STROKE, 0.12f).set(RigPose.FAN, 0f));
    }

    private static float smooth(float x) {
        return x * x * (3f - 2f * x);
    }
}
