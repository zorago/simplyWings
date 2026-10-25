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
import com.zorago.simplywings.render.rig.Membrane;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class NebulaRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int SPIN = 2;
    private static final int[] IDLES = {56, 60, 72};
    private static final int TERTIALS = 4;
    private static final int SECONDARIES = 4;
    private static final int STARS = 6;
    private static final float[] KEYS = Cycle.beat(0.55f, 0.2f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.5f, 0.82f, 0.78f, 0.35f, -0.14f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.25f, 0.22f, 0.06f, -0.61f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.2f, 0.24f, 0.12f, -0.7f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.25f, 0.05f, 0.3f, 0.28f, 0f, -0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.2f, 0.5f, 0.22f, 0f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.35f, 0.6f, 0.28f, -0.08f, -0.08f, 0.05f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.28f, 0.1f, -0.05f, -0.22f, -0.08f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.1f, -0.2f, 0.1f, 0.25f, 0.2f, 0.05f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.5f, -0.2f, 0f, 0.15f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);
    private static final String[] TIPS = {"right_primary_2", "left_primary_2"};
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/nebula_glow.png");

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private Side right;
    private Side left;

    public NebulaRig() {
        super("nebula");
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
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    public int[] idleLengths() {
        return IDLES;
    }

    private static final class Side {
        final FeatherWing wing;
        final float s;
        final Bone galaxy;
        final Bone[] tertial = new Bone[TERTIALS];
        final Bone[] secondary = new Bone[SECONDARIES];
        final Bone[] star = new Bone[STARS];

        Side(Bones b, String p, float s) {
            this.s = s;
            wing = new FeatherWing(b, p, s, 1.25f, 2.75f, 2.85f);
            galaxy = b.get(p + "galaxy");
            for (int k = 0; k < TERTIALS; k++) {
                tertial[k] = b.get(p + "tertial_feather_" + (k + 1));
            }
            for (int k = 0; k < SECONDARIES; k++) {
                secondary[k] = b.get(p + "secondary_feather_" + (k + 1));
            }
            for (int k = 0; k < STARS; k++) {
                star[k] = b.get(p + "star_" + (k + 1));
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
        memory.spring(IDLE, m.rouse, 0.1f, 0.8f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.2f, 0.7f);
        float spin = 1f + 2f * Mth.clamp(m.speed / 30f, 0f, 1f) + (m.rouse > 0f && m.rouseVariant % 3 == 1 ? 5f * m.rouse : 0f);
        if (memory.now(SPIN) > Mth.TWO_PI) {
            memory.snap(SPIN, memory.now(SPIN) - Mth.TWO_PI);
        }
        memory.set(SPIN, memory.now(SPIN) + 0.02f * spin);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        java.util.Arrays.fill(drive.v, 0f);
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float spin = memory.get(SPIN, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 3;
        float p = m.rouseProgress;

        float constellation = 0f;
        float vortex = 0f;
        float open = 0f;
        if (idle > 0.001f) {
            float env = idle * Mth.sin((float) Math.PI * p);
            switch (variant) {
                case 0 -> constellation = env;
                case 1 -> vortex = env;
                default -> open = idle * smooth(Mth.clamp(p * 2.2f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 2.2f, 0f, 1f));
            }
        }
        add(ground, state.clear().set(RigPose.FOLD, Mth.lerp(open, 1f, 0.05f)).set(RigPose.STROKE, Mth.lerp(open, 0.12f, 0.5f))
                .set(RigPose.SWEEP, 0.2f * open).set(RigPose.FAN, 1.1f * open));
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.FAN, 1.2f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.15f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.4f)
                .set(RigPose.SWEEP, 0.2f).set(RigPose.FAN, 0.9f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.06f).set(RigPose.SWEEP, 0.02f)
                .set(RigPose.FAN, 1f).set(RigPose.BEND_TIPS, 0.18f).set(RigPose.ASYM, m.bank * 0.25f));
        float dd = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.2f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.12f, -0.45f)).set(RigPose.FAN, Mth.lerp(dd, 0.7f, 0.15f)));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.25f).set(RigPose.BEND_TIPS, 0.1f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.5f)
                .set(RigPose.STROKE, 0.06f).set(RigPose.FAN, 0f));

        FlightRules.apply(m, drive, BEAT);
        if (settle > 0.001f) {
            drive.add(RigPose.STROKE, settle * 0.07f * Mth.sin(time * 1.1f));
            drive.add(RigPose.FAN, settle * 0.2f);
        }
        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.25f));

        float fold = drive.get(RigPose.FOLD);
        Membrane veil = membrane();
        if (veil != null) {
            float billow = 0.4f * m.w(WingMotion.CRUISE) + FlightRules.chute(m) + 0.3f * m.w(WingMotion.HOVER)
                    + 0.06f * Mth.sin(time * 0.4f) + 0.6f * drive.get(RigPose.CAMBER);
            veil.billow(Mth.clamp(billow * (1f - fold), -0.4f, 1f));
        }
        float drift = 0.04f + 0.03f * (1f - ground);
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.45f, 1.1f - 0.1f * k);
        for (Side side : new Side[]{right, left}) {
            side.wing.apply(drive, tips);
            FlightRules.chuteArm(side.wing.arm, side.s, FlightRules.chute(m));
            float s = side.s;
            for (int k = 0; k < side.wing.primary.size(); k++) {
                float w = drift * Mth.sin(time * 0.3f - k * 0.6f + (s > 0 ? 0f : 1.1f));
                side.wing.primary.get(k).rotate(0f, s * w * 0.5f, s * w);
            }
            for (int k = 0; k < SECONDARIES; k++) {
                side.secondary[k].rotate(0f, 0f, s * drift * Mth.sin(time * 0.33f - k));
            }
            for (int k = 0; k < TERTIALS; k++) {
                side.tertial[k].rotate(0f, 0f, s * drift * Mth.sin(time * 0.29f - k + 2f));
            }
            float g = 1f + 0.6f * vortex;
            side.galaxy.rotate(0f, 0f, s * spin).scale(g, g, 1f);
            for (int k = 0; k < STARS; k++) {
                float tw = 0.55f + 0.45f * Mth.sin(time * (0.21f + 0.04f * k) + k * 1.7f + (s > 0 ? 0f : 2f));
                float glow = tw * (1f + 0.6f * constellation) * (1f - 0.6f * fold * (1f - ground));
                float out = constellation * 1.5f;
                side.star[k].move(s * out * Mth.cos(k * 1.3f), -out * Mth.sin(k * 1.3f), 0f)
                        .scale(glow, glow, 1f).rotate(0f, 0f, time * 0.02f * (k % 2 == 0 ? 1f : -1f));
            }
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
