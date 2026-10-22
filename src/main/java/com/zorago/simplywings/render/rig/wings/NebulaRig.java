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

public final class NebulaRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int SPIN = 2;
    private static final int[] IDLES = {56, 60, 72};
    private static final int TERTIALS = 2;
    private static final int SECONDARIES = 3;
    private static final int STARS = 6;
    private static final float[] KEYS = Cycle.beat(0.55f, 0.2f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.7f, 0.1f, 1.05f, 1.1f, -0.3f, -0.9f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.2f, -0.12f, -0.22f, -0.06f, 0.25f, 0.3f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.1f, 0.5f, 0.22f, 0f, -0.05f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.1f, 0.6f, 0.28f, -0.08f, -0.08f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.28f, 0.1f, -0.05f, -0.22f, -0.08f);
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
                tertial[k] = b.get(p + "tertial_" + (k + 1));
            }
            for (int k = 0; k < SECONDARIES; k++) {
                secondary[k] = b.get(p + "secondary_" + (k + 1));
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
        add(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.12f).set(RigPose.STROKE, 0.8f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.15f));
        add(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        add(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.4f).set(RigPose.STROKE, 0.4f)
                .set(RigPose.SWEEP, 0.2f).set(RigPose.FAN, 0.9f));
        add(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.22f)
                .set(RigPose.SWEEP, 0.1f).set(RigPose.FAN, 1f));
        add(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.06f).set(RigPose.SWEEP, 0.02f)
                .set(RigPose.FAN, 1f).set(RigPose.BEND_TIPS, 0.18f).set(RigPose.ASYM, m.bank * 0.25f));
        float dd = m.diveDepth;
        add(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.2f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.12f, -0.45f)).set(RigPose.FAN, Mth.lerp(dd, 0.7f, 0.15f)));
        add(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.5f).set(RigPose.TWIST, -0.7f).set(RigPose.FAN, 1.25f));
        add(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.5f)
                .set(RigPose.STROKE, 0.06f).set(RigPose.FAN, 0f));

        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.12f));
            drive.add(RigPose.STROKE, m.stall * 0.45f * Mth.sin(time * 0.9f));
            drive.add(RigPose.TWIST, m.stall * 0.35f * Mth.sin(time * 0.7f));
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float sn = Mth.sin(ph);
            float cs = Mth.cos(ph);
            float back = Math.max(0f, -sn);
            drive.add(RigPose.STROKE, hw * 0.7f * cs);
            drive.add(RigPose.SWEEP, hw * 0.18f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.45f * sn);
            drive.add(RigPose.ELBOW, hw * 0.3f * back);
            drive.add(RigPose.WRIST, hw * 0.45f * back);
            drive.add(RigPose.BEND_TIPS, hw * 0.45f * (Mth.cos(ph - 0.5f) - cs));
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            drive.add(RigPose.STROKE, a * STROKE.at(t));
            drive.add(RigPose.SWEEP, a * SWEEP.at(t));
            drive.add(RigPose.ELBOW, a * ELBOW.at(t));
            drive.add(RigPose.WRIST, a * WRIST.at(t));
            drive.add(RigPose.TWIST, a * TWIST.at(t));
            drive.add(RigPose.BEND_ELBOW, a * 0.18f * STROKE.lagged(t, 0.05f));
            drive.add(RigPose.BEND_WRIST, a * 0.28f * STROKE.lagged(t, 0.09f));
            drive.add(RigPose.BEND_TIPS, a * 0.45f * STROKE.lagged(t, 0.13f));
        }
        if (settle > 0.001f) {
            drive.add(RigPose.STROKE, settle * 0.07f * Mth.sin(time * 1.1f));
            drive.add(RigPose.FAN, settle * 0.2f);
        }
        drive.add(RigPose.FOLD, ground * (1f - idle) * -0.025f * (1f + Mth.sin(time * 0.06f)));
        drive.add(RigPose.STROKE, ground * (1f - idle) * 0.02f * Mth.sin(time * 0.06f + 0.5f));
        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.25f));

        float fold = drive.get(RigPose.FOLD);
        float drift = 0.04f + 0.03f * (1f - ground);
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.45f, 1.1f - 0.1f * k);
        for (Side side : new Side[]{right, left}) {
            side.wing.apply(drive, tips);
            side.wing.stack(fold, 0.55f, 0.5f, 0.4f);
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
