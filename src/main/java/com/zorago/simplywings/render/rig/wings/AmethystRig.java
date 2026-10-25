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

public final class AmethystRig extends WingRig {
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/amethyst_glow.png");
    private static final int RING = 0;
    private static final int LAST_BEAT = 1;
    private static final float[] KEYS = Cycle.beat(0.5f, 0.2f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.45f, 0.75f, 0.74f, 0.3f, -0.12f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.25f, 0f, 0.12f, 0.12f, 0.04f, -0.35f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.35f, -0.08f, 0.12f, 0.14f, 0.06f, -0.4f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.2f, 0.05f, 0.25f, 0.24f, 0f, -0.24f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.1f, 0.3f, 0.12f, 0f, 0f, 0.03f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.15f, 0.35f, 0.12f, 0f, -0.05f, 0.05f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.2f, 0.08f, 0f, -0.18f, -0.08f);
    private static final Cycle FAN = Cycle.of(KEYS, 0f, -0.12f, 0f, 0.08f, 0.08f, 0.03f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.3f, -0.1f, 0f, 0.1f, 0f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);
    private static final String[] TIPS = {"right_primary_2", "left_primary_2"};

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;

    public AmethystRig() {
        super("amethyst");
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
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.2f, 2.65f, 2.75f);
        left = new FeatherWing(bones, "left_", -1f, 1.2f, 2.65f, 2.75f);
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        float beat = m.beating() ? m.beat : -1f;
        float strike = KEYS[5];
        float last = memory.now(LAST_BEAT);
        if (beat >= 0f && last >= 0f && last < strike && beat >= strike) {
            memory.snap(RING, 1f);
        } else {
            memory.set(RING, memory.now(RING) * 0.86f);
        }
        memory.set(LAST_BEAT, beat);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, 1f).set(RigPose.STROKE, 0.12f));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.FAN, 1.1f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.1f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.15f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.03f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1.05f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.12f).set(RigPose.SWEEP, 0.05f)
                .set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.2f, 0.55f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.15f, -0.45f)).set(RigPose.FAN, Mth.lerp(d, 0.7f, 0.2f))
                .set(RigPose.STROKE, 0.05f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.1f).set(RigPose.BEND_TIPS, 0.05f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.82f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);
        float ring = memory.get(RING, m.partialTick);
        float chime = ring * 0.05f * Mth.sin(time * 9.7f);
        drive.add(RigPose.BEND_TIPS, chime);
        drive.add(RigPose.FAN, chime * 0.6f);

        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        drive.set(RigPose.FOLD, fold);
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.1f));
        FeatherWing.TipCurve tips = (k, n) -> 1f;
        right.apply(drive, tips);
        left.apply(drive, tips);
        float chute = FlightRules.chute(m);
        FlightRules.chuteArm(right.arm, 1f, chute);
        FlightRules.chuteArm(left.arm, -1f, chute);
        float drift = (1f - fold) * (0.6f * m.w(WingMotion.HOVER) + 0.25f * m.w(WingMotion.CRUISE)) + 0.15f * m.w(WingMotion.GROUND);
        for (FeatherWing w : new FeatherWing[]{right, left}) {
            for (int k = 0; k < w.primary.size(); k++) {
                Bone shard = w.primary.get(k);
                float phase = time * (0.11f + 0.017f * k) + k * 1.7f + (w == right ? 0f : 2.3f);
                shard.rotate(drift * 0.04f * Mth.sin(phase), 0f, w.sign * drift * 0.03f * Mth.sin(phase * 1.3f + 0.5f));
            }
        }
    }
}
