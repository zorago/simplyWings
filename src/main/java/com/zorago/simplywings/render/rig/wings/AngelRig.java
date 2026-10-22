package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class AngelRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int ALULA = 2;
    private static final int HALO = 3;
    private static final int[] IDLES = {80, 96, 84, 40, 90};
    private static final float UP = 0.62f;
    private static final float DOWN = 0.1f;
    private static final float[] KEYS = {0f, 0.32f, UP, UP + 0.04f, UP + DOWN, UP + DOWN + 0.12f, 1f};
    private static final Cycle STROKE = Cycle.of(KEYS, -0.95f, 0.25f, 1.25f, 1.3f, -0.2f, -1.05f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.12f, -0.2f, -0.28f, -0.12f, 0.35f, 0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.12f, 0.6f, 0.3f, 0.05f, -0.05f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.12f, 0.75f, 0.35f, 0f, -0.15f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.15f, 0f, -0.38f, -0.15f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.2f, -0.25f, 0.1f, 0.3f, 0.3f, 0.2f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.55f, -0.2f, 0f, 0.18f, 0.05f);
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/angel_glow.png");

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private final RigPose side = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private Bone rightAlula;
    private Bone leftAlula;
    private Bone halo;

    public AngelRig() {
        super("angel");
    }

    @Override
    public ResourceLocation glow() {
        return GLOW;
    }

    @Override
    public int[] idleLengths() {
        return IDLES;
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.9f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.9f);
        rightAlula = bones.get("right_alula");
        leftAlula = bones.get("left_alula");
        halo = bones.get("aureole");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.09f, 0.8f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.18f, 0.72f);
        float slow = m.w(WingMotion.BRAKE) + m.w(WingMotion.LANDING) + m.stall + 0.6f * m.w(WingMotion.HOVER);
        memory.spring(ALULA, Math.min(1f, slow), 0.2f, 0.65f);
        boolean heaven = m.rouse > 0f && m.rouseVariant % 5 == 4;
        float speed = 0.012f + (heaven ? 0.05f * m.rouse : 0f) + 0.01f * Mth.clamp(m.speed / 40f, 0f, 1f);
        if (memory.now(HALO) > Mth.TWO_PI) {
            memory.snap(HALO, memory.now(HALO) - Mth.TWO_PI);
        }
        memory.set(HALO, memory.now(HALO) + speed);
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
        float env = idle * Mth.sin((float) Math.PI * p);
        float hold = idle * smooth(Mth.clamp(p * 3f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 3f, 0f, 1f));

        float gFold = 1f, gStroke = 0.12f, gSweep = 0f, gFan = 0f, gTwist = 0f, gTips = 0f;
        float ruffle = 0f;
        float glory = 0f;
        switch (variant) {
            case 0 -> {
                gFold = Mth.lerp(hold, 1f, 0.02f);
                gStroke = Mth.lerp(hold, 0.12f, 0.55f);
                gSweep = 0.3f * hold;
                gFan = 1.25f * hold;
                gTips = 0.35f * hold;
                glory = hold;
            }
            case 2 -> {
                gFold = Mth.lerp(hold, 1f, 0.3f);
                gStroke = Mth.lerp(hold, 0.12f, -0.75f);
                gSweep = 0.25f * hold;
                gFan = 0.9f * hold;
                gTwist = 0.3f * hold;
            }
            case 3 -> ruffle = env;
            case 4 -> {
                gFold = Mth.lerp(hold, 1f, 0.12f);
                gStroke = Mth.lerp(hold, 0.12f, 0.35f);
                gSweep = 1.05f * hold;
                gFan = 1.1f * hold;
                gTips = 0.5f * hold;
                glory = hold;
            }
            default -> {
            }
        }
        drive.blendIn(ground, state.clear().set(RigPose.FOLD, gFold).set(RigPose.STROKE, gStroke).set(RigPose.SWEEP, gSweep)
                .set(RigPose.FAN, gFan).set(RigPose.TWIST, gTwist).set(RigPose.BEND_TIPS, gTips));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 1.1f)
                .set(RigPose.SWEEP, 0.4f).set(RigPose.FAN, 1.25f).set(RigPose.TWIST, 0.15f).set(RigPose.BEND_TIPS, 0.25f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.3f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.18f).set(RigPose.FAN, 0.9f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.06f).set(RigPose.STROKE, 0.65f)
                .set(RigPose.SWEEP, 0.45f).set(RigPose.TWIST, -0.45f).set(RigPose.FAN, 1.25f).set(RigPose.BEND_TIPS, 0.2f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.03f).set(RigPose.STROKE, 0.22f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1.2f).set(RigPose.BEND_TIPS, 0.3f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.12f).set(RigPose.SWEEP, 0.06f)
                .set(RigPose.FAN, 1.2f).set(RigPose.BEND_TIPS, 0.4f).set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.25f, 0.68f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.2f, -0.6f)).set(RigPose.FAN, Mth.lerp(d, 0.5f, 0f))
                .set(RigPose.STROKE, Mth.lerp(d, 0.1f, 0.22f)));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, 0.5f)
                .set(RigPose.SWEEP, 0.62f).set(RigPose.TWIST, -0.82f).set(RigPose.FAN, 1.25f).set(RigPose.BEND_TIPS, 0.15f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.STROKE, 0.2f).set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.1f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.45f));
            drive.set(RigPose.FAN, Mth.lerp(m.stall, drive.get(RigPose.FAN), 1.25f));
            drive.add(RigPose.STROKE, m.stall * 0.45f * Mth.sin(time * 0.8f));
            drive.add(RigPose.TWIST, m.stall * 0.45f * Mth.sin(time * 0.6f));
            drive.add(RigPose.ASYM, m.stall * 0.3f * Mth.sin(time * 0.4f));
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float s = Mth.sin(ph);
            float c = Mth.cos(ph);
            float back = Math.max(0f, -s);
            drive.add(RigPose.STROKE, hw * 0.7f * c);
            drive.add(RigPose.SWEEP, hw * 0.18f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.5f * s);
            drive.add(RigPose.ELBOW, hw * 0.35f * back);
            drive.add(RigPose.WRIST, hw * 0.5f * back);
            drive.add(RigPose.HAND_TWIST, hw * -0.5f * back);
            drive.add(RigPose.BEND_TIPS, hw * 0.55f * (Mth.cos(ph - 0.6f) - c));
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
            drive.add(RigPose.BEND_ELBOW, a * 0.22f * STROKE.lagged(t, 0.03f));
            drive.add(RigPose.BEND_WRIST, a * 0.32f * STROKE.lagged(t, 0.06f));
            drive.add(RigPose.BEND_TIPS, a * 0.5f * STROKE.lagged(t, 0.1f));
        }
        if (settle > 0.001f) {
            drive.add(RigPose.STROKE, settle * 0.1f * Mth.sin(time * 0.9f));
            drive.add(RigPose.BEND_TIPS, settle * 0.25f * Mth.sin(time * 1.6f));
            drive.add(RigPose.FAN, settle * 0.15f * Mth.sin(time * 1.1f));
        }
        drive.add(RigPose.FOLD, ground * (1f - idle) * -0.02f * (1f + Mth.sin(time * 0.06f)));
        drive.add(RigPose.STROKE, ground * (1f - idle) * 0.02f * Mth.sin(time * 0.06f + 0.5f));
        float glide = m.w(WingMotion.CRUISE) + 0.5f * m.w(WingMotion.HOVER);
        drive.add(RigPose.BEND_TIPS, glide * 0.06f * (Mth.sin(time * 0.43f) + 0.6f * Mth.sin(time * 1.1f)));
        drive.add(RigPose.FAN, glide * 0.03f * Mth.sin(time * 0.31f));
        drive.add(RigPose.STROKE, glide * 0.02f * Mth.sin(time * 0.23f));
        float brake = m.w(WingMotion.BRAKE) + m.w(WingMotion.LANDING);
        drive.add(RigPose.BEND_TIPS, brake * 0.14f * Mth.sin(time * 2.6f));

        float alula = memory.get(ALULA, pt);
        float preen = variant == 1 ? idle : 0f;
        for (int i = 0; i < 2; i++) {
            FeatherWing wing = i == 0 ? right : left;
            side.copy(drive);
            if (preen > 0.001f) {
                float local = Mth.clamp(i == 0 ? p * 2f : p * 2f - 1f, 0f, 1f);
                float w = preen * Mth.sin((float) Math.PI * local) * ground;
                side.add(RigPose.FOLD, -0.6f * w);
                side.add(RigPose.STROKE, -0.2f * w);
                side.add(RigPose.SWEEP, 0.35f * w);
                side.add(RigPose.TWIST, 0.45f * w);
                side.add(RigPose.FAN, 0.9f * w);
                side.add(RigPose.BEND_TIPS, w * 0.2f * Mth.sin(time * 1.7f));
            }
            if (ruffle > 0.001f) {
                side.add(RigPose.FAN, ruffle * 0.35f * Mth.sin(time * 2.3f + i));
                side.add(RigPose.STROKE, ruffle * 0.06f * Mth.sin(time * 3.1f + i));
                side.add(RigPose.FOLD, -ruffle * 0.15f);
            }
            float fold = Mth.clamp(side.get(RigPose.FOLD), 0f, 1f);
            side.set(RigPose.FOLD, fold);
            side.set(RigPose.FAN, Mth.clamp(side.get(RigPose.FAN), 0f, 1.25f));
            FeatherWing.TipCurve tips = (k, n) -> k < 6 ? 1.3f - 0.1f * k : 0.45f;
            wing.apply(side, tips);
            wing.stack(fold, 0.7f, 0.55f, 0.45f);
            float s = wing.sign;
            if (ruffle > 0.001f) {
                for (int k = 0; k < wing.primary.size(); k++) {
                    float wave = ruffle * 0.12f * Mth.sin(time * 2.8f - k * 0.7f + i);
                    wing.primary.get(k).rotate(0f, s * wave, s * wave * 0.5f);
                }
            }
            float a = alula * (1f - fold);
            (i == 0 ? rightAlula : leftAlula).rotate(0f, s * 0.35f * a, s * 0.55f * a);
        }
        float spin = memory.get(HALO, pt);
        float breathe = 1f + 0.04f * Mth.sin(time * 0.05f) + 0.15f * glory;
        halo.rotate(0f, 0f, spin).scale(breathe, breathe, 1f).move(0f, -0.4f * Mth.sin(time * 0.04f), 0f);
    }

    private static float smooth(float x) {
        return x * x * (3f - 2f * x);
    }
}
