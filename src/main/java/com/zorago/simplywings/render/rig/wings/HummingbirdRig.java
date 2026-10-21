package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class HummingbirdRig extends WingRig {
    private static final int ROUSE = 0;
    private static final float VISUAL_RATE = 3.2f;

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private final Bone[] ghosts = new Bone[4];

    public HummingbirdRig() {
        super("hummingbird");
    }

    @Override
    public Material material() {
        return Material.TRANSLUCENT;
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.3f, 2.8f, 2.9f);
        left = new FeatherWing(bones, "left_", -1f, 1.3f, 2.8f, 2.9f);
        ghosts[0] = bones.get("right_ghost_a");
        ghosts[1] = bones.get("right_ghost_b");
        ghosts[2] = bones.get("left_ghost_a");
        ghosts[3] = bones.get("left_ghost_b");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean rousing = m.rouse > 0f;
        memory.spring(ROUSE, rousing ? 1f : 0f, rousing ? 0.3f : 0.2f, rousing ? 0.55f : 0.6f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float time = m.time;
        float rouse = memory.get(ROUSE, m.partialTick);
        float hover = m.w(WingMotion.HOVER) + m.w(WingMotion.BRAKE) + m.w(WingMotion.CHARGE);
        drive.blendIn(m.w(WingMotion.GROUND), state.clear().set(RigPose.FOLD, 1f - 0.75f * rouse)
                .set(RigPose.STROKE, 0.1f + 0.2f * rouse));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.STROKE, 0.3f).set(RigPose.SWEEP, 0.2f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.25f).set(RigPose.STROKE, 0.2f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.1f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.15f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.STROKE, 0.1f).set(RigPose.SWEEP, 0.12f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.15f).set(RigPose.SWEEP, -0.1f)
                .set(RigPose.ASYM, m.bank * 0.25f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.3f, 0.75f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.3f, -0.6f)).set(RigPose.FAN, Mth.lerp(d, 0.6f, 0f)));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.STROKE, 0.25f).set(RigPose.SWEEP, 0.35f)
                .set(RigPose.TWIST, -0.4f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.9f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.FAN, 0f));
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.05f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.2f));
        }

        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        float buzz = Math.max(m.buzz, 0.9f * rouse * m.w(WingMotion.GROUND)) * (1f - fold);
        float amplitude = buzz * Mth.lerp(Mth.clamp(hover, 0f, 1f), 0.5f, 0.85f);
        float ph = time * VISUAL_RATE;
        float swing = Mth.sin(ph) + 0.18f * Mth.sin(ph * 2.7f + 1f);
        float baseStroke = drive.get(RigPose.STROKE);
        float baseSweep = drive.get(RigPose.SWEEP);
        float baseTwist = drive.get(RigPose.TWIST);
        drive.add(RigPose.STROKE, amplitude * swing);
        drive.add(RigPose.TWIST, buzz * 0.55f * Mth.cos(ph));
        drive.add(RigPose.SWEEP, buzz * 0.12f * Mth.sin(2f * ph));
        drive.add(RigPose.BEND_TIPS, buzz * 0.15f * Mth.cos(ph));
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.TWIST, sw * 0.4f * Mth.sin(time * 1.3f));
            drive.add(RigPose.ASYM, sw * 0.3f * Mth.sin(time * 0.8f));
        }
        float ground = m.w(WingMotion.GROUND);
        drive.add(RigPose.FOLD, ground * -0.015f * (1f + Mth.sin(time * 0.15f)));
        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        drive.set(RigPose.FAN, Mth.clamp(drive.get(RigPose.FAN), 0f, 1.05f));
        FeatherWing.TipCurve tips = (k, n) -> 1f - 0.05f * k;
        right.apply(drive, tips);
        left.apply(drive, tips);
        right.stack(fold, 0.6f, 0.5f, 0.4f);
        left.stack(fold, 0.6f, 0.5f, 0.4f);

        boolean show = buzz > 0.35f && fold < 0.15f;
        float reach = amplitude * 1.05f;
        for (int i = 0; i < 4; i++) {
            float s = i < 2 ? 1f : -1f;
            float sign = i % 2 == 0 ? 1f : -1f;
            ghosts[i].visible(show);
            ghosts[i].rotate(baseTwist + sign * 0.5f * buzz, s * (baseStroke + sign * reach), s * baseSweep);
        }
    }
}
