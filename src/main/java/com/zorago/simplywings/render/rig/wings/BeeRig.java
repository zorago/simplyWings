package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class BeeRig extends WingRig {
    private static final int ROUSE = 0;
    private static final int FOLD = 1;
    private static final float VISUAL_RATE = 3.4f;
    private static final float FORE_FOLD = -1.36f;
    private static final float HIND_FOLD = -1.08f;
    private static final float FLOOR = -5f * Mth.DEG_TO_RAD;
    private static final float CHUTE_STROKE = 0.31f;
    private static final float CHUTE_RAISE = 0.85f;
    private static final float CHUTE_PITCH = 0.48f;
    private static final String[] TIPS = {"right_fore", "left_fore"};

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
    public String[] tipBones() {
        return TIPS;
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
        float hover = m.w(WingMotion.HOVER);
        float cruise = m.w(WingMotion.CRUISE);
        float charge = m.w(WingMotion.CHARGE);
        float brake = m.w(WingMotion.BRAKE);
        float slow = Mth.clamp(m.brakeBeat, 0f, 1f);
        float chute = FlightRules.chute(m);
        float slowBrake = brake * slow;

        float blast = 0f;
        if (m.launch >= 0f) {
            float p = m.launch;
            blast = p < 0.15f ? p / 0.15f : (1f - p) * (1f - p) / 0.7225f;
        }
        float shiver = charge * charge;
        float buzz = Math.max(Math.max(m.buzz, 0.6f * rouse * m.w(WingMotion.GROUND)), Math.max(shiver * 0.5f, blast))
                * (1f - 0.8f * fold) * (1f - chute);
        float amplitude = buzz * (0.45f * cruise + 0.55f * hover + 0.25f * slowBrake + 0.2f * charge
                + 0.45f * (1f - Mth.clamp(cruise + hover + brake + charge, 0f, 1f))) + 0.5f * blast;
        float ph = time * VISUAL_RATE;
        float swing = Mth.sin(ph) + 0.15f * Mth.sin(ph * 2.3f + 0.7f);

        float center = FLOOR * Mth.clamp(amplitude * 10f, 0f, 1f) + amplitude * 1.15f;
        float stroke = Mth.lerp(chute, center + 0.05f * cruise + 0.2f * slowBrake + 0.35f * charge, CHUTE_STROKE)
                + amplitude * swing + m.stall * 0.25f * (1f + Mth.sin(time * 1.4f));
        float raise = chute * CHUTE_RAISE + charge * 0.6f + 0.12f * hover - 0.2f * cruise + m.bank * 0.2f
                + chute * 0.02f * Mth.sin(time * 4.7f);
        float twist = hover * (FlightRules.HOVER_PLANE + 0.55f * buzz * Mth.cos(ph))
                + (1f - hover) * buzz * 0.6f * Mth.cos(ph) + chute * (CHUTE_PITCH + 0.03f * Mth.sin(time * 5.3f));
        boolean show = buzz > 0.35f && fold < 0.2f;
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            fore[i].rotate(twist, s * stroke, s * (FORE_FOLD * fold + raise));
            hind[i].rotate(twist, s * stroke * 0.95f, s * (HIND_FOLD * fold + raise * 0.8f));
            for (int g = 0; g < 4; g++) {
                float sign = g % 2 == 0 ? 1f : -1f;
                Bone ghost = ghosts[i][g];
                ghost.visible(show);
                float ghostStroke = Math.max(FLOOR, center + sign * amplitude * 1.05f);
                ghost.rotate(twist + sign * 0.5f * buzz * (1f - hover), s * ghostStroke, s * raise * (g < 2 ? 1f : 0.8f));
            }
        }
    }
}
