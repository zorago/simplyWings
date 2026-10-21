package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class SquirrelRig extends WingRig {
    private static final int FOLD = 0;
    private static final int TAIL_SWAY = 1;
    private static final int TAIL_LIFT = 2;
    private static final float[] KEYS = Cycle.beat(0.5f, 0.3f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.3f, 0.05f, 0.35f, 0.38f, -0.1f, -0.35f);
    private static final Cycle PULL = Cycle.of(KEYS, 0.1f, 0.2f, 0.08f, 0f, 0f, 0.03f);

    private final Bone[] shoulder = new Bone[2];
    private final Bone[] arm = new Bone[2];
    private final Bone[] forearm = new Bone[2];
    private final Bone[] inner = new Bone[2];
    private final Bone[] outer = new Bone[2];
    private final Bone[] spur = new Bone[2];
    private Bone tail;
    private Bone tailTip;

    public SquirrelRig() {
        super("squirrel");
    }

    @Override
    protected void onBind(Bones bones) {
        String[] side = {"right_", "left_"};
        for (int i = 0; i < 2; i++) {
            shoulder[i] = bones.get(side[i] + "shoulder");
            arm[i] = bones.get(side[i] + "arm");
            forearm[i] = bones.get(side[i] + "forearm");
            inner[i] = bones.get(side[i] + "inner_membrane");
            outer[i] = bones.get(side[i] + "outer_membrane");
            spur[i] = bones.get(side[i] + "spur");
        }
        tail = bones.get("tail");
        tailTip = bones.get("tail_tip");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        float fold = m.w(WingMotion.GROUND) + 0.45f * m.w(WingMotion.TUCK) + 0.35f * m.w(WingMotion.AIR)
                + 0.3f * m.diveDepth * m.w(WingMotion.DIVE);
        memory.spring(FOLD, Mth.clamp(fold, 0f, 1f), 0.18f, 0.68f);
        memory.spring(TAIL_SWAY, Mth.clamp(-m.bank * 2f, -1f, 1f), 0.1f, 0.78f);
        float lift = m.w(WingMotion.LANDING) + m.w(WingMotion.BRAKE) + 0.5f * m.stall;
        memory.spring(TAIL_LIFT, Mth.clamp(lift, 0f, 1f), 0.14f, 0.7f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float time = m.time;
        float fold = Mth.clamp(memory.get(FOLD, m.partialTick), 0f, 1f);
        float open = 1f - fold;
        float ground = m.w(WingMotion.GROUND);
        float stroke = 0.08f * m.w(WingMotion.CRUISE) + 0.45f * m.w(WingMotion.LANDING) + 0.35f * m.w(WingMotion.BRAKE)
                + 0.15f * m.w(WingMotion.HOVER) + 0.3f * m.w(WingMotion.CHARGE) + 0.05f * ground;
        float twist = -0.55f * m.w(WingMotion.LANDING) - 0.7f * m.w(WingMotion.BRAKE);
        float sweep = 0.2f * m.w(WingMotion.LANDING) + 0.25f * m.w(WingMotion.BRAKE) - 0.25f * m.w(WingMotion.DIVE)
                + 0.15f * m.w(WingMotion.CHARGE);
        float billow = 0.12f * m.w(WingMotion.CRUISE) + 0.4f * m.w(WingMotion.LANDING) + 0.45f * m.w(WingMotion.BRAKE);
        float pull = 0f;
        if (m.beating()) {
            float t = m.beat;
            stroke += m.beatWeight * STROKE.at(t);
            pull += m.beatWeight * PULL.at(t);
            billow += m.beatWeight * 0.15f * Math.max(0f, -STROKE.at(t));
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            stroke += hw * 0.35f * Mth.cos(ph);
            twist += hw * 0.3f * Mth.sin(ph);
            pull += hw * 0.15f * Math.max(0f, -Mth.sin(ph));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            stroke += sw * 0.4f * Mth.sin(time * 1.5f);
            twist += sw * 0.4f * Mth.sin(time * 1.1f);
        }
        float glide = m.w(WingMotion.CRUISE);
        billow += glide * 0.05f * Mth.sin(time * 1.7f);
        float bank = m.bank;
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            float inside = Math.max(0f, s * bank) * 1.2f;
            float armFold = -1.05f * fold + 0.25f * pull + 0.2f * inside;
            shoulder[i].rotate(twist - 0.25f * inside, s * stroke - bank * 0.35f, s * sweep);
            arm[i].rotate(0f, 0f, s * armFold);
            forearm[i].rotate(0f, s * -0.1f * inside, s * (2.05f * fold + 0.15f * pull));
            spur[i].rotate(0f, 0f, s * (0.55f * open * (glide + m.w(WingMotion.LANDING)) - 0.9f * fold));
            float gather = Mth.lerp(fold, 1f, 0.35f);
            inner[i].rotate(billow, 0f, s * 0.75f * fold).scale(1f, gather, 1f);
            outer[i].rotate(billow * 1.2f, 0f, s * -1.2f * fold).scale(Mth.lerp(fold, 1f, 0.6f), gather, 1f);
        }
        float sway = memory.get(TAIL_SWAY, m.partialTick);
        float lift = memory.get(TAIL_LIFT, m.partialTick);
        float flying = 1f - ground;
        tail.rotate(-0.1f * flying + 0.9f * lift + ground * 0.3f, 0f, flying * 0.35f * sway + 0.04f * Mth.sin(time * 0.21f));
        tailTip.rotate(0.5f * lift + ground * 0.4f, 0f, flying * (0.25f * sway + 0.06f * Mth.sin(time * 0.6f - 0.8f)));
    }
}
