package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class KiteRig extends WingRig {
    private static final int FOLD = 0;
    private static final int GUST = 1;
    private static final int TAIL = 5;
    private static final float[] KEYS = Cycle.beat(0.5f, 0.3f);
    private static final Cycle TUG = Cycle.of(KEYS, -0.25f, 0f, 0.3f, 0.32f, -0.05f, -0.28f);

    private final Bone[] shoulder = new Bone[2];
    private final Bone[] spar = new Bone[2];
    private final Bone[] strut = new Bone[2];
    private final Bone[] outer = new Bone[2];
    private final Bone[] innerSail = new Bone[2];
    private final Bone[] outerSail = new Bone[2];
    private final Bone[] tail = new Bone[TAIL];

    public KiteRig() {
        super("kite");
    }

    @Override
    protected void onBind(Bones bones) {
        String[] side = {"right_", "left_"};
        for (int i = 0; i < 2; i++) {
            shoulder[i] = bones.get(side[i] + "shoulder");
            spar[i] = bones.get(side[i] + "spar");
            strut[i] = bones.get(side[i] + "strut");
            outer[i] = bones.get(side[i] + "outer");
            innerSail[i] = bones.get(side[i] + "sail_inner");
            outerSail[i] = bones.get(side[i] + "sail_outer");
        }
        for (int k = 0; k < TAIL; k++) {
            tail[k] = bones.get("tail_" + (k + 1));
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        float fold = m.w(WingMotion.GROUND) + 0.5f * m.w(WingMotion.TUCK) + 0.25f * m.w(WingMotion.AIR);
        memory.spring(FOLD, Mth.clamp(fold, 0f, 1f), 0.14f, 0.72f);
        float wind = Mth.clamp(m.speed / 20f, 0f, 1f) * (1f - m.w(WingMotion.GROUND));
        memory.ease(GUST, wind, 0.08f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float time = m.time;
        float fold = Mth.clamp(memory.get(FOLD, m.partialTick), 0f, 1f);
        float wind = memory.get(GUST, m.partialTick);
        float open = 1f - fold;
        float flying = 1f - m.w(WingMotion.GROUND);
        float stroke = 0.15f * m.w(WingMotion.CRUISE) + 0.4f * m.w(WingMotion.LANDING) + 0.35f * m.w(WingMotion.BRAKE)
                + 0.2f * m.w(WingMotion.HOVER) + 0.4f * m.w(WingMotion.CHARGE);
        float sweep = 0.2f * m.w(WingMotion.LANDING) + 0.3f * m.w(WingMotion.BRAKE) - 0.4f * m.w(WingMotion.DIVE);
        float twist = -0.5f * m.w(WingMotion.BRAKE) - 0.25f * m.w(WingMotion.LANDING);
        if (m.beating()) {
            stroke += m.beatWeight * TUG.at(m.beat);
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            stroke += hw * 0.3f * Mth.cos(m.hoverPhase);
            twist += hw * 0.3f * Mth.sin(m.hoverPhase);
        }
        float sway = flying * (0.05f * Mth.sin(time * 0.23f) + 0.03f * Mth.sin(time * 0.57f + 1f));
        stroke += sway + m.stall * 0.3f * Mth.sin(time * 1.4f);
        twist += m.stall * 0.4f * Mth.sin(time * 1.1f);
        float flap = open * flying * (0.04f + 0.08f * wind);
        float snap = flap * (Mth.sin(time * 2.7f) + 0.6f * Mth.sin(time * 4.3f + 0.4f));
        float billow = open * (0.08f + 0.12f * wind) * flying;
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            shoulder[i].rotate(twist * open, s * stroke - m.bank * 0.3f, s * (sweep * open - 1.4f * fold));
            strut[i].rotate(0f, 0f, s * 0.75f * fold);
            outer[i].rotate(0f, s * snap * 0.6f, s * -0.5f * fold);
            innerSail[i].rotate(billow + snap * 0.3f, 0f, 0f).scale(1f, Mth.lerp(fold, 1f, 0.5f), 1f);
            outerSail[i].rotate(billow * 1.2f + snap, 0f, 0f).scale(1f, Mth.lerp(fold, 1f, 0.6f), 1f);
        }
        float swing = flying * (0.5f + 0.6f * wind);
        for (int k = 0; k < TAIL; k++) {
            float phase = time * (0.35f + 0.25f * wind) - k * 0.9f;
            float lateral = swing * (0.12f + 0.05f * k) * Mth.sin(phase);
            float lift = flying * (0.08f + 0.04f * k) * Mth.sin(phase * 0.7f + 0.5f) + m.w(WingMotion.GROUND) * (k == 0 ? 0.12f : 0.03f);
            tail[k].rotate(lift, 0f, lateral - (k == 0 ? m.bank * 0.4f * flying : 0f));
        }
    }
}
