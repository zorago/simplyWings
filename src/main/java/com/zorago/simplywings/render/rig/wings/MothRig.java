package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class MothRig extends WingRig {
    private static final int SHIVER = 0;
    private static final float[] KEYS = Cycle.beat(0.5f, 0.3f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.9f, 0f, 1.1f, 1.15f, -0.2f, -1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.35f, 0.15f, 0f, -0.3f, -0.1f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.1f, -0.1f, -0.15f, 0f, 0.15f, 0.2f);
    private static final float HIND_LAG = 0.08f;

    private final Bone[] fore = new Bone[2];
    private final Bone[] hind = new Bone[2];

    public MothRig() {
        super("moth");
    }

    @Override
    protected void onBind(Bones bones) {
        fore[0] = bones.get("right_fore");
        fore[1] = bones.get("left_fore");
        hind[0] = bones.get("right_hind");
        hind[1] = bones.get("left_hind");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean shivering = m.rouse > 0f || m.w(WingMotion.CHARGE) > 0.3f;
        memory.spring(SHIVER, shivering ? 1f : 0f, 0.2f, 0.6f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float time = m.time;
        float ground = m.w(WingMotion.GROUND);
        float shiver = memory.get(SHIVER, m.partialTick);
        float stroke = 0.15f * m.w(WingMotion.CHARGE) + 0.2f * m.w(WingMotion.AIR) + 0.3f * m.w(WingMotion.LANDING)
                + 0.2f * m.w(WingMotion.HOVER) + 0.12f * m.w(WingMotion.CRUISE) + 0.35f * m.w(WingMotion.BRAKE)
                - 0.05f * ground + 0.1f * m.w(WingMotion.DIVE);
        float sweep = -0.32f * ground + 0.1f * m.w(WingMotion.LANDING) + 0.4f * m.w(WingMotion.BRAKE)
                - 0.45f * m.w(WingMotion.DIVE) * Mth.lerp(m.diveDepth, 0.5f, 1f) - 0.6f * m.w(WingMotion.TUCK)
                - 0.05f * m.w(WingMotion.CRUISE);
        float twist = -0.5f * m.w(WingMotion.BRAKE) - 0.2f * m.w(WingMotion.LANDING);
        float hindSweep = sweep * 0.7f - 0.15f * m.w(WingMotion.TUCK);
        float jitter = (1f - ground) * (0.08f * Mth.sin(time * 0.93f) + 0.05f * Mth.sin(time * 2.17f + 1f));
        stroke += jitter + ground * 0.015f * Mth.sin(time * 0.07f);
        twist += (1f - ground) * 0.06f * Mth.sin(time * 1.31f);
        float tremor = shiver * 0.07f * Mth.sin(time * 3.6f);
        stroke += tremor;
        float glide = m.w(WingMotion.CRUISE);
        stroke += glide * 0.04f * Mth.sin(time * 0.45f);

        float foreStroke = stroke;
        float hindStroke = stroke * 0.9f;
        float foreTwist = twist;
        float hindTwist = twist;
        float foreSweep = sweep;
        float hindSweepOut = hindSweep;
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            foreStroke += hw * 0.75f * Mth.cos(ph);
            hindStroke += hw * 0.7f * Mth.cos(ph - 0.5f);
            foreTwist += hw * 0.45f * Mth.sin(ph);
            hindTwist += hw * 0.4f * Mth.sin(ph - 0.5f);
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            foreStroke += a * STROKE.at(t);
            hindStroke += a * 0.92f * STROKE.at(t - HIND_LAG);
            foreTwist += a * TWIST.at(t);
            hindTwist += a * TWIST.at(t - HIND_LAG);
            foreSweep += a * SWEEP.at(t);
            hindSweepOut += a * 0.8f * SWEEP.at(t - HIND_LAG);
        }
        if (m.stall > 0.001f) {
            foreStroke += m.stall * 0.5f * Mth.sin(time * 1.2f);
            hindStroke += m.stall * 0.5f * Mth.sin(time * 1.2f - 0.4f);
            foreTwist += m.stall * 0.4f * Mth.sin(time * 0.9f);
        }
        float bank = m.bank * 0.3f;
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            fore[i].rotate(foreTwist, s * foreStroke - bank, s * foreSweep);
            hind[i].rotate(hindTwist, s * hindStroke - bank, s * hindSweepOut);
        }
    }
}
