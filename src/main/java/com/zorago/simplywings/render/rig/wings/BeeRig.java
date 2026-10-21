package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class BeeRig extends WingRig {
    private static final int ROUSE = 0;
    private static final int FOLD = 1;
    private static final float VISUAL_RATE = 3.4f;
    private static final float FORE_FOLD = -1.36f;
    private static final float HIND_FOLD = -1.08f;

    private final Bone[] fore = new Bone[2];
    private final Bone[] hind = new Bone[2];
    private final Bone[][] ghosts = new Bone[2][4];

    public BeeRig() {
        super("bee");
    }

    @Override
    public Material material() {
        return Material.TRANSLUCENT;
    }

    @Override
    protected void onBind(Bones bones) {
        String[] side = {"right_", "left_"};
        for (int i = 0; i < 2; i++) {
            fore[i] = bones.get(side[i] + "fore");
            hind[i] = bones.get(side[i] + "hind");
            ghosts[i][0] = bones.get(side[i] + "fore_ghost_a");
            ghosts[i][1] = bones.get(side[i] + "fore_ghost_b");
            ghosts[i][2] = bones.get(side[i] + "hind_ghost_a");
            ghosts[i][3] = bones.get(side[i] + "hind_ghost_b");
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean rousing = m.rouse > 0f;
        memory.spring(ROUSE, rousing ? 1f : 0f, rousing ? 0.3f : 0.2f, rousing ? 0.55f : 0.6f);
        float fold = m.w(WingMotion.GROUND) * (1f - 0.35f * (rousing ? 1f : 0f)) + 0.6f * m.w(WingMotion.TUCK)
                + m.diveDepth * 0.5f * m.w(WingMotion.DIVE) + 0.3f * m.w(WingMotion.AIR);
        memory.spring(FOLD, Mth.clamp(fold, 0f, 1f), 0.25f, 0.62f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float time = m.time;
        float fold = Mth.clamp(memory.get(FOLD, m.partialTick), 0f, 1f);
        float rouse = memory.get(ROUSE, m.partialTick);
        float hover = Mth.clamp(m.w(WingMotion.HOVER) + m.w(WingMotion.BRAKE) + m.w(WingMotion.CHARGE), 0f, 1f);
        float buzz = Math.max(m.buzz, 0.6f * rouse * m.w(WingMotion.GROUND)) * (1f - 0.8f * fold);
        float amplitude = buzz * Mth.lerp(hover, 0.45f, 0.8f);
        float ph = time * VISUAL_RATE;
        float swing = Mth.sin(ph) + 0.15f * Mth.sin(ph * 2.3f + 0.7f);
        float lift = 0.12f * m.w(WingMotion.HOVER) + 0.05f * m.w(WingMotion.CRUISE) - 0.05f * fold;
        float sweep = 0.25f * m.w(WingMotion.BRAKE) - 0.2f * m.w(WingMotion.CRUISE) + m.bank * 0.2f;
        float twist = buzz * 0.6f * Mth.cos(ph) - 0.35f * m.w(WingMotion.BRAKE);
        float stall = m.stall * 0.3f * Mth.sin(time * 1.4f);
        boolean show = buzz > 0.35f && fold < 0.2f;
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            float stroke = lift + amplitude * swing + stall;
            fore[i].rotate(twist, s * stroke, s * (FORE_FOLD * fold + sweep));
            hind[i].rotate(twist, s * stroke * 0.95f, s * (HIND_FOLD * fold + sweep * 0.8f));
            for (int g = 0; g < 4; g++) {
                float sign = g % 2 == 0 ? 1f : -1f;
                Bone ghost = ghosts[i][g];
                ghost.visible(show);
                ghost.rotate(sign * 0.5f * buzz, s * (lift + sign * amplitude * 1.05f), s * sweep * (g < 2 ? 1f : 0.8f));
            }
        }
    }
}
