package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.Membrane;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class AuroraRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int[] IDLES = {60, 70, 52};
    private static final String[] CURTAINS = {"veil", "hind_veil"};
    private static final int[] SEGMENTS = {4, 3};
    private static final float[] KEYS = Cycle.beat(0.55f, 0.3f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.45f, 0.75f, 0.72f, 0.3f, -0.12f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.2f, 0.04f, 0.25f, 0.23f, 0f, -0.25f);
    private static final float FLOOR = -5f * Mth.DEG_TO_RAD;
    private static final float CHUTE_STROKE = 0.31f;
    private static final float CHUTE_RAISE = 0.85f;
    private static final float CHUTE_PITCH = 0.48f;
    private static final String[] TIPS = {"right_veil_rail_4", "left_veil_rail_4"};

    private final Bone[][] curtain = new Bone[2][2];
    private final Bone[][][] rail = new Bone[2][2][];
    private final Bone[][][] drape = new Bone[2][2][];

    public AuroraRig() {
        super("aurora");
    }

    @Override
    public Material material() {
        return Material.EMISSIVE;
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    public int[] idleLengths() {
        return IDLES;
    }

    @Override
    protected void onBind(Bones bones) {
        String[] side = {"right_", "left_"};
        for (int i = 0; i < 2; i++) {
            for (int c = 0; c < 2; c++) {
                String base = side[i] + CURTAINS[c];
                curtain[i][c] = bones.get(base);
                rail[i][c] = new Bone[SEGMENTS[c]];
                drape[i][c] = new Bone[SEGMENTS[c]];
                for (int k = 0; k < SEGMENTS[c]; k++) {
                    rail[i][c][k] = bones.get(base + "_rail_" + (k + 1));
                    drape[i][c][k] = bones.get(base + "_drape_" + (k + 1));
                }
            }
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.12f, 0.78f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.2f, 0.7f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float ground = m.w(WingMotion.GROUND);
        float hover = m.w(WingMotion.HOVER);
        float cruise = m.w(WingMotion.CRUISE);
        float brake = m.w(WingMotion.BRAKE);
        float dive = m.w(WingMotion.DIVE) * Mth.lerp(m.diveDepth, 0.4f, 1f);
        float tuck = m.w(WingMotion.TUCK);
        int variant = m.rouseVariant % 3;
        float p = m.rouseProgress;

        float corona = 0f;
        float rippleIdle = 0f;
        float pleat = 0f;
        if (idle > 0.001f) {
            float env = idle * Mth.sin((float) Math.PI * p);
            switch (variant) {
                case 0 -> corona = env;
                case 1 -> rippleIdle = env;
                default -> pleat = env;
            }
        }
        float charge = m.w(WingMotion.CHARGE);
        float chute = FlightRules.chute(m);
        float slowBrake = brake - chute;
        float blast = 0f;
        if (m.launch >= 0f) {
            float lp = m.launch;
            float strike = Mth.clamp(lp / 0.3f, 0f, 1f);
            blast = (lp < 0.3f ? 1f : 1f - (lp - 0.3f) / 0.7f) * (1f - 2f * strike);
        }
        float shiver = charge * charge;
        float droop = -0.42f * ground - 0.1f * m.w(WingMotion.AIR) + 0.12f * hover + 0.1f * slowBrake - 0.4f * dive
                - 0.7f * tuck + 0.75f * charge + 0.85f * corona + 0.4f * blast + 0.03f * shiver * Mth.sin(time * 3.7f);
        float lift = 0.25f * hover + 0.04f * cruise + 0.25f * slowBrake + 0.35f * charge + 0.15f * m.w(WingMotion.AIR)
                + 0.05f * ground + 0.15f * corona + 0.035f * shiver * Mth.sin(time * 2.9f);
        float twist = hover * FlightRules.HOVER_PLANE;
        float rippleAmp = 0.06f + 0.05f * cruise + 0.04f * hover + 0.12f * brake + 0.2f * rippleIdle + 0.15f * settle
                + 0.25f * m.stall;
        float rippleRate = 0.1f + 0.04f * cruise + 0.05f * rippleIdle;
        float breath = 1f + 0.06f * Mth.sin(time * 0.05f) + 0.2f * corona + 0.05f * rippleIdle + 0.12f * shiver;
        float airborne = Mth.clamp(1f - ground - charge, 0f, 1f);
        float billow = 0.4f * airborne + 0.9f * chute + 0.3f * shiver;
        float beatA = m.beating() ? m.beatWeight : 0f;
        float hw = m.hoverBeat;

        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            for (int c = 0; c < 2; c++) {
                float stroke = lift * (c == 0 ? 1f : 0.8f);
                float sweep = droop * (c == 0 ? 1f : 0.75f);
                float tw = twist;
                if (beatA > 0f) {
                    float t = m.beat - 0.08f * c;
                    stroke += beatA * (c == 0 ? 1f : 0.8f) * STROKE.at(t);
                    sweep += beatA * SWEEP.at(t);
                    if (c == 0 && i == 0) {
                        billow += beatA * 1.2f * STROKE.lagged(t, 0.06f);
                    }
                }
                if (hw > 0.001f) {
                    float ph = m.hoverPhase - 0.7f * c;
                    sweep += hw * (0.2f + 0.38f * Mth.cos(ph));
                    stroke += hw * 0.1f * Mth.sin(2f * ph);
                    tw += hw * 0.45f * Mth.sin(ph);
                }
                stroke = Mth.lerp(chute, stroke, CHUTE_STROKE * (c == 0 ? 1f : 0.9f));
                sweep = Mth.lerp(chute, sweep, CHUTE_RAISE * (c == 0 ? 1f : 0.6f)) + chute * 0.02f * Mth.sin(time * 4.7f + c);
                tw = Mth.lerp(chute, tw, CHUTE_PITCH);
                float roll = m.bank * 0.25f;
                curtain[i][c].rotate(tw, s * Math.max(FLOOR, stroke - s * roll), s * sweep);
                for (int k = 0; k < SEGMENTS[c]; k++) {
                    float phase = time * rippleRate * 2f - k * 0.85f - c * 1.4f + i * 0.6f;
                    float wave = rippleAmp * Mth.sin(phase);
                    float whip = beatA > 0f ? beatA * 0.22f * STROKE.lagged(m.beat - 0.08f * c, 0.05f + 0.04f * k) : 0f;
                    rail[i][c][k].rotate(0f, s * (wave * 0.6f + whip), s * (corona * 0.12f + settle * 0.06f * Mth.sin(time * 0.8f - k)));
                    float sway = rippleAmp * 1.4f * Mth.sin(phase - 0.6f) + 0.18f * cruise + 0.25f * dive;
                    float fold = pleat * 0.55f * (k % 2 == 0 ? 1f : -1f);
                    float shimmer = 1f + 0.05f * Mth.sin(time * 0.37f + k * 1.9f + c);
                    drape[i][c][k].rotate(sway, s * fold, 0f).scale(1f, breath * shimmer, 1f);
                }
            }
        }
        Membrane veil = membrane();
        if (veil != null) {
            veil.billow(Mth.clamp(billow, -0.6f, 1.2f));
        }
    }
}
