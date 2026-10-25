package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class ShadowRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int[] IDLES = {60, 50, 46};
    private static final int EYES = 5;
    private static final float UP = 0.45f;
    private static final float DOWN = 0.14f;
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};
    private static final float[] KEYS = Cycle.beat(UP, DOWN);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.52f, 0.85f, 0.8f, 0.3f, -0.15f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.25f, 0.22f, 0.05f, -0.63f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.2f, 0.24f, 0.1f, -0.72f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.25f, 0.05f, 0.3f, 0.28f, 0f, -0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.25f, 0.55f, 0.2f, 0f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.45f, 0.65f, 0.25f, -0.05f, -0.1f, 0.1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.25f, -0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.1f, -0.2f, 0.1f, 0.25f, 0.2f, 0.05f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.5f, -0.2f, 0f, 0.15f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/shadow_glow.png");

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private Side right;
    private Side left;

    public ShadowRig() {
        super("shadow");
    }

    @Override
    public Material material() {
        return Material.TRANSLUCENT;
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
        final FeatherWing wing;
        final float s;
        final Bone[] eye = new Bone[EYES];

        Side(Bones b, String p, float s) {
            this.s = s;
            wing = new FeatherWing(b, p, s, 1.25f, 2.75f, 2.85f);
            for (int k = 0; k < EYES; k++) {
                eye[k] = b.get(p + "eye_" + (k + 1));
            }
        }
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    protected void onBind(Bones bones) {
        right = new Side(bones, "right_", 1f);
        left = new Side(bones, "left_", -1f);
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.15f, 0.75f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.25f, 0.65f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        java.util.Arrays.fill(drive.v, 0f);
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 3;
        float p = m.rouseProgress;

        float stare = 0f;
        float dissolve = 0f;
        float coil = 0f;
        if (idle > 0.001f) {
            float env = idle * Mth.sin((float) Math.PI * p);
            switch (variant) {
                case 0 -> stare = env;
                case 1 -> dissolve = env;
                default -> coil = env;
            }
        }
        add(ground, state.clear().set(RigPose.FOLD, 1f - 0.4f * dissolve).set(RigPose.STROKE, 0.12f + 0.2f * dissolve)
                .set(RigPose.FAN, 0.8f * dissolve));
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.FAN, 1.25f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.12f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.8f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.25f).set(RigPose.FAN, 1f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.07f).set(RigPose.SWEEP, -0.05f)
                .set(RigPose.FAN, 0.85f).set(RigPose.BEND_TIPS, 0.12f).set(RigPose.ASYM, m.bank * 0.4f));
        float dd = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.2f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.15f, -0.5f)).set(RigPose.FAN, Mth.lerp(dd, 0.6f, 0.1f)));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.25f).set(RigPose.BEND_TIPS, 0.1f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.06f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);
        if (settle > 0.001f) {
            drive.add(RigPose.FAN, settle * 0.3f);
            drive.add(RigPose.STROKE, settle * 0.08f * Mth.sin(time * 1.3f));
        }
        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.25f));

        float fold = drive.get(RigPose.FOLD);
        float chute = FlightRules.chute(m);
        float writhe = 0.08f + 0.06f * (1f - ground) + 0.18f * coil + 0.12f * dissolve + 0.1f * settle;
        float spread = 1f + 0.35f * dissolve + 0.15f * settle;
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.1f - 0.1f * k);
        for (Side side : new Side[]{right, left}) {
            side.wing.apply(drive, tips);
            float s = side.s;
            FlightRules.chuteArm(side.wing.arm, s, chute);
            for (int k = 0; k < side.wing.primary.size(); k++) {
                float w = writhe * Mth.sin(time * 0.55f - k * 0.7f + (s > 0 ? 0f : 1.7f));
                float c = coil * 0.6f * (0.5f + 0.1f * k);
                side.wing.primary.get(k).rotate(0f, s * w * 0.6f, s * (w - c)).scale(spread * (1f + 0.06f * Mth.sin(time * 0.4f + k)), 1f, 1f);
            }
            writhe(side.wing.secondary, s, time, writhe, coil, spread, 0.4f);
            writhe(side.wing.tertial, s, time, writhe, coil, spread, 2.2f);
            writhe(side.wing.handFeather, s, time, writhe, coil, spread, 1.3f);
            for (int k = 0; k < EYES; k++) {
                float blink = blink(time, k, s);
                float open = Mth.lerp(stare, blink, 1.3f) * (1f - 0.8f * fold * (1f - ground)) * (1f - 0.7f * dissolve);
                float lookX = 0.5f * Mth.sin(time * 0.05f + k * 2.1f) + stare * 0.6f * Mth.sin(time * 0.17f + k);
                float lookY = 0.3f * Mth.sin(time * 0.037f + k * 1.3f);
                side.eye[k].move(s * lookX, lookY, 0f).scale(1f, Math.max(0.05f, open), 1f);
            }
        }
    }

    private static float blink(float time, int k, float s) {
        float period = 70f + 23f * k + (s > 0 ? 0f : 11f);
        float t = (time + k * 37f) % period;
        if (t > 6f) {
            return 1f;
        }
        return Math.abs(t - 3f) / 3f;
    }

    private static void writhe(java.util.List<Bone> bones, float s, float time, float writhe, float coil, float spread, float seed) {
        for (int k = 0; k < bones.size(); k++) {
            float w = writhe * 1.3f * Mth.sin(time * 0.6f - k * 0.9f + seed);
            bones.get(k).rotate(w * 0.5f, 0f, s * (w + coil * 0.5f)).scale(1f, spread * (1f + 0.07f * Mth.sin(time * 0.45f + k + seed)), 1f);
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
}
