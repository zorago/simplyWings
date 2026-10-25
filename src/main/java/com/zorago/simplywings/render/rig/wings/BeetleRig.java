package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class BeetleRig extends WingRig {
    private static final int OPEN = 0;
    private static final int UNFOLD = 1;
    private static final float VISUAL_RATE = 2.9f;
    private static final float FLOOR = -5f * Mth.DEG_TO_RAD;
    private static final float CHUTE_STROKE = 0.31f;
    private static final float CHUTE_RAISE = 0.85f;
    private static final float CHUTE_PITCH = 0.48f;
    private static final String[] TIPS = {"right_wing_tip", "left_wing_tip"};

    private final Bone[] shell = new Bone[2];
    private final Bone[] wing = new Bone[2];
    private final Bone[] tip = new Bone[2];
    private final Bone[][] ghosts = new Bone[2][2];

    public BeetleRig() {
        super("beetle");
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
            shell[i] = bones.get(side[i] + "shell");
            wing[i] = bones.get(side[i] + "wing");
            tip[i] = bones.get(side[i] + "wing_tip");
            ghosts[i][0] = bones.get(side[i] + "ghost_a");
            ghosts[i][1] = bones.get(side[i] + "ghost_b");
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        float closed = m.w(WingMotion.GROUND) * (m.rouse > 0f ? 0.6f : 1f) * (1f - m.w(WingMotion.CHARGE)) + m.w(WingMotion.TUCK);
        float open = Mth.clamp(1f - closed, 0f, 1f);
        memory.spring(OPEN, open, 0.2f, 0.62f);
        memory.spring(UNFOLD, Mth.clamp(memory.now(OPEN) * 1.4f - 0.4f, 0f, 1f), 0.25f, 0.6f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float time = m.time;
        float open = memory.get(OPEN, m.partialTick);
        float unfold = Mth.clamp(memory.get(UNFOLD, m.partialTick), 0f, 1f);
        float hover = m.w(WingMotion.HOVER);
        float cruise = m.w(WingMotion.CRUISE);
        float charge = m.w(WingMotion.CHARGE);
        float brake = m.w(WingMotion.BRAKE);
        float chute = FlightRules.chute(m);
        float slowBrake = brake * Mth.clamp(m.brakeBeat, 0f, 1f);

        float blast = 0f;
        if (m.launch >= 0f) {
            float p = m.launch;
            blast = p < 0.15f ? p / 0.15f : (1f - p) * (1f - p) / 0.7225f;
        }
        float shiver = charge * charge;
        float buzz = Math.max(m.buzz, Math.max(shiver * 0.5f, blast)) * unfold * (1f - chute);
        float amplitude = buzz * (0.4f * cruise + 0.5f * hover + 0.25f * slowBrake + 0.2f * charge
                + 0.4f * (1f - Mth.clamp(cruise + hover + brake + charge, 0f, 1f))) + 0.45f * blast * unfold;
        float ph = time * VISUAL_RATE;
        float swing = Mth.sin(ph) + 0.2f * Mth.sin(ph * 2.1f + 0.5f);
        float center = FLOOR * Mth.clamp(amplitude * 10f, 0f, 1f) + amplitude * 1.2f;

        float dive = m.w(WingMotion.DIVE) * m.diveDepth;
        float lift = 0.9f * open - 0.3f * dive + 0.35f * chute + 0.15f * charge;
        float spread = 0.5f * open + 0.35f * chute;
        float rattle = open * (buzz * 0.03f * Mth.sin(time * 4.3f) + chute * 0.025f * Mth.sin(time * 5.1f));
        float raise = chute * CHUTE_RAISE + charge * 0.5f + 0.1f * hover - 0.25f * cruise - 0.4f * dive + m.bank * 0.15f
                + chute * 0.02f * Mth.sin(time * 4.7f);
        float stroke = Mth.lerp(chute, center + 0.2f * slowBrake + 0.3f * charge, CHUTE_STROKE) + amplitude * swing
                + m.stall * 0.25f * (1f + Mth.sin(time * 1.3f));
        float twist = hover * (FlightRules.HOVER_PLANE + 0.5f * buzz * Mth.cos(ph))
                + (1f - hover) * buzz * 0.5f * Mth.cos(ph) + chute * (CHUTE_PITCH + 0.03f * Mth.sin(time * 5.3f));
        boolean show = buzz > 0.35f && unfold > 0.85f;
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            shell[i].rotate(lift + rattle, 0f, s * (spread + rattle));
            float folded = 1f - unfold;
            wing[i].rotate(twist * unfold, s * stroke * unfold, s * (-1.45f * folded + raise * unfold));
            wing[i].scale(Mth.lerp(unfold, 0.5f, 1f), 1f, 1f);
            tip[i].rotate(0f, s * 0.25f * buzz * Mth.cos(ph - 0.6f), s * 0.6f * folded);
            for (int g = 0; g < 2; g++) {
                float sign = g == 0 ? 1f : -1f;
                ghosts[i][g].visible(show);
                float ghostStroke = Math.max(FLOOR, center + sign * amplitude * 1.05f);
                ghosts[i][g].rotate(twist + sign * 0.45f * buzz * (1f - hover), s * ghostStroke, s * raise);
            }
        }
    }
}
