package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class CopperRig extends WingRig {
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/copper_glow.png");
    private static final int RATTLE = 0;
    private static final int LAST_BEAT = 1;
    private static final float[] KEYS = {0f, 0.25f, 0.5f, 0.55f, 0.62f, 0.7f, 1f};
    private static final Cycle STROKE = Cycle.of(KEYS, -0.8f, 0.15f, 1f, 1.02f, 0.1f, -0.95f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.15f, -0.1f, -0.15f, -0.05f, 0.2f, 0.25f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.05f, 0.3f, 0.15f, 0f, 0f, 0f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.05f, 0.4f, 0.2f, 0f, 0f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.25f, 0.1f, 0f, -0.25f, -0.1f);

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;

    public CopperRig() {
        super("copper");
    }

    @Override
    public ResourceLocation glow() {
        return GLOW;
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.85f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.85f);
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        float beat = m.beating() ? m.beat : -1f;
        float last = memory.now(LAST_BEAT);
        boolean hit = beat >= 0f && last >= 0f && ((last < KEYS[5] && beat >= KEYS[5]) || (last < KEYS[2] && beat >= KEYS[2]));
        memory.set(RATTLE, hit ? 1f : memory.now(RATTLE) * 0.8f);
        memory.set(LAST_BEAT, beat);
    }

    private static float notch(float v, float step) {
        return Math.round(v / step) * step;
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, 1f).set(RigPose.STROKE, 0.12f));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0.08f).set(RigPose.STROKE, 0.9f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.15f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.35f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.15f).set(RigPose.STROKE, 0.5f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.TWIST, -0.35f).set(RigPose.FAN, 1.1f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.1f).set(RigPose.SWEEP, 0.03f)
                .set(RigPose.FAN, 1.05f).set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.2f, 0.6f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.15f, -0.5f)).set(RigPose.FAN, Mth.lerp(d, 0.7f, 0.15f))
                .set(RigPose.STROKE, 0.05f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, 0.4f)
                .set(RigPose.SWEEP, 0.55f).set(RigPose.TWIST, -0.75f).set(RigPose.FAN, 1.2f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.82f).set(RigPose.SWEEP, -0.55f)
                .set(RigPose.STROKE, 0.08f).set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.12f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.4f));
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            drive.add(RigPose.STROKE, hw * 0.6f * Mth.cos(ph));
            drive.add(RigPose.TWIST, hw * 0.4f * Mth.sin(ph));
            drive.add(RigPose.WRIST, hw * 0.3f * Math.max(0f, -Mth.sin(ph)));
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            drive.add(RigPose.STROKE, a * STROKE.at(t));
            drive.add(RigPose.SWEEP, a * SWEEP.at(t));
            drive.add(RigPose.ELBOW, a * ELBOW.at(t));
            drive.add(RigPose.WRIST, a * WRIST.at(t));
            drive.add(RigPose.TWIST, a * TWIST.at(t));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.STROKE, sw * 0.45f * Mth.sin(time * 1.2f));
            drive.add(RigPose.TWIST, sw * 0.4f * Mth.sin(time * 0.9f));
        }
        float rattle = memory.get(RATTLE, m.partialTick);
        drive.add(RigPose.FAN, rattle * 0.06f * Mth.sin(time * 7.3f));
        drive.add(RigPose.BEND_TIPS, rattle * 0.05f * Mth.sin(time * 8.9f + 0.7f));
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.STROKE, glide * notch(0.04f * Mth.sin(time * 0.29f), 0.02f));
        drive.add(RigPose.BEND_TIPS, glide * notch(0.05f * Mth.sin(time * 0.41f), 0.025f));

        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        drive.set(RigPose.FOLD, fold);
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.2f));
        FeatherWing.TipCurve tips = (k, n) -> 0.8f;
        right.apply(drive, tips);
        left.apply(drive, tips);
        right.stack(fold, 0.6f, 0.5f, 0.4f);
        left.stack(fold, 0.6f, 0.5f, 0.4f);
    }
}
