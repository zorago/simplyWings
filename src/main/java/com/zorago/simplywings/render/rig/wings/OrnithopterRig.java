package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class OrnithopterRig extends WingRig {
    private static final int FOLD = 0;
    private static final int CRANK = 1;

    private final Bone[] shoulder = new Bone[2];
    private final Bone[] arm = new Bone[2];
    private final Bone[] forearm = new Bone[2];
    private final Bone[] hand = new Bone[2];
    private Bone pulley;

    public OrnithopterRig() {
        super("ornithopter");
    }

    @Override
    protected void onBind(Bones bones) {
        String[] side = {"right_", "left_"};
        for (int i = 0; i < 2; i++) {
            shoulder[i] = bones.get(side[i] + "shoulder");
            arm[i] = bones.get(side[i] + "arm");
            forearm[i] = bones.get(side[i] + "forearm");
            hand[i] = bones.get(side[i] + "hand");
        }
        pulley = bones.get("pulley");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        float fold = m.w(WingMotion.GROUND) + 0.5f * m.w(WingMotion.TUCK) + 0.3f * m.w(WingMotion.AIR)
                + 0.35f * m.diveDepth * m.w(WingMotion.DIVE);
        memory.ease(FOLD, Mth.clamp(fold, 0f, 1f), 0.12f);
    }

    private static float crank(float t) {
        return -Mth.cos(t * Mth.TWO_PI);
    }

    private static float stop(float v, float limit) {
        return Mth.clamp(v, -limit, limit);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float time = m.time;
        float fold = Mth.clamp(memory.get(FOLD, m.partialTick), 0f, 1f);
        float open = 1f - fold;
        float stroke = 0.1f * m.w(WingMotion.CRUISE) + 0.4f * m.w(WingMotion.LANDING) + 0.35f * m.w(WingMotion.BRAKE)
                + 0.2f * m.w(WingMotion.HOVER) + 0.6f * m.w(WingMotion.CHARGE) + 0.05f * fold;
        float sweep = 0.2f * m.w(WingMotion.LANDING) + 0.35f * m.w(WingMotion.BRAKE) - 0.35f * m.w(WingMotion.DIVE);
        float twist = -0.55f * m.w(WingMotion.BRAKE) - 0.25f * m.w(WingMotion.LANDING);
        float hinge = 0f;
        float crankAngle = 0f;
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight;
            float c = crank(t);
            stroke += a * 0.75f * c;
            twist += a * 0.18f * Mth.sin(t * Mth.TWO_PI);
            hinge += a * stop(1.4f * (crank(t - 0.12f) - c), 0.38f);
            crankAngle += a * c;
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float c = -Mth.cos(ph);
            stroke += hw * 0.55f * c;
            twist += hw * 0.3f * Mth.sin(ph);
            hinge += hw * stop(1.2f * (-Mth.cos(ph - 0.7f) - c), 0.35f);
            crankAngle += hw * c;
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            stroke += sw * 0.35f * Mth.sin(time * 1.2f);
            hinge += sw * stop(0.5f * Mth.sin(time * 2.1f), 0.3f);
        }
        float glide = m.w(WingMotion.CRUISE);
        float creak = glide * 0.015f * Math.signum(Mth.sin(time * 0.31f)) + m.w(WingMotion.GROUND) * 0.01f * Math.signum(Mth.sin(time * 0.13f));
        stroke += creak;
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            shoulder[i].rotate(twist * open, s * stroke - m.bank * 0.25f, s * sweep * open);
            arm[i].rotate(0f, 0f, s * -1.25f * fold).scale(1f, Mth.lerp(fold, 1f, 0.55f), 1f);
            forearm[i].rotate(0f, s * hinge * 0.35f, s * 2.45f * fold).scale(1f, Mth.lerp(fold, 1f, 0.7f), 1f);
            hand[i].rotate(0f, s * hinge, s * -2.2f * fold);
        }
        pulley.rotate(0f, 0f, crankAngle * 1.6f + glide * 0.1f * Mth.sin(time * 0.31f));
    }
}
