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

public final class AirRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int FOLD = 2;
    private static final int GUST = 3;
    private static final int SEGMENTS = 5;
    private static final int[] IDLES = {34, 56, 46};
    private static final float D = Mth.DEG_TO_RAD;
    private static final float[] FOLD_Z = {-4f * D, -3f * D, 0f, 4f * D, 7f * D};
    private static final float[] FOLD_Y = {0f, -4f * D, -4f * D, -3f * D, -2f * D};
    private static final float FOLD_SHOULDER_Z = -72f * D;
    private static final float FOLD_SHOULDER_Y = 14f * D;
    private static final float FOLD_OVERLAP = 10f * D;
    private static final float[] KEYS = Cycle.beat(0.42f, 0.2f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.5f, 0.82f, 0.78f, 0.35f, -0.14f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.25f, 0.05f, 0.3f, 0.28f, 0f, -0.3f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.28f, 0.1f, -0.05f, -0.24f, -0.08f);
    private static final float FLOOR = -5f * D;
    private static final float CHUTE_STROKE = 0.31f;
    private static final float CHUTE_RAISE = 0.85f;
    private static final float CHUTE_PITCH = 0.48f;
    private static final String[] TIPS = {"right_seg_5", "left_seg_5"};

    private final Bone[][] seg = new Bone[2][SEGMENTS];
    private final Bone[] shoulder = new Bone[2];

    public AirRig() {
        super("air");
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
    public int[] idleLengths() {
        return IDLES;
    }

    @Override
    protected void onBind(Bones bones) {
        String[] side = {"right_", "left_"};
        for (int i = 0; i < 2; i++) {
            shoulder[i] = bones.get(side[i] + "shoulder");
            for (int k = 0; k < SEGMENTS; k++) {
                seg[i][k] = bones.get(side[i] + "seg_" + (k + 1));
            }
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.2f, 0.7f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.3f, 0.62f);
        float fold = m.w(WingMotion.GROUND) * (1f - m.w(WingMotion.CHARGE)) + 0.75f * m.w(WingMotion.TUCK) + 0.3f * m.w(WingMotion.AIR)
                + 0.4f * m.diveDepth * m.w(WingMotion.DIVE) + 0.35f * m.w(WingMotion.LANDING);
        memory.spring(FOLD, Mth.clamp(fold, 0f, 1f), 0.12f, 0.7f);
        memory.spring(GUST, Mth.clamp(m.speed / 40f, 0f, 1f), 0.08f, 0.8f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float fold = Mth.clamp(memory.get(FOLD, pt), 0f, 1f);
        float wind = Mth.clamp(memory.get(GUST, pt), 0f, 1f);
        float ground = m.w(WingMotion.GROUND);
        float hover = m.w(WingMotion.HOVER);
        float cruise = m.w(WingMotion.CRUISE);
        float brake = m.w(WingMotion.BRAKE);
        float dive = m.w(WingMotion.DIVE);
        int variant = m.rouseVariant % 3;
        float p = m.rouseProgress;

        float charge = m.w(WingMotion.CHARGE);
        float chute = FlightRules.chute(m);
        float slowBrake = brake - chute;
        float shiver = charge * charge;
        float blast = 0f;
        if (m.launch >= 0f) {
            float lp = m.launch;
            float strike = Mth.clamp(lp / 0.3f, 0f, 1f);
            blast = (lp < 0.3f ? 1f : 1f - (lp - 0.3f) / 0.7f) * (1f - 2f * strike);
        }
        float lift = 0.35f * charge + 0.25f * hover + 0.06f * cruise + 0.3f * slowBrake
                + 0.3f * m.w(WingMotion.AIR) + 0.04f * dive + 0.7f * Math.max(0f, blast) + 0.035f * shiver * Mth.sin(time * 2.9f);
        float sweep = 0.12f * hover + 0.04f * cruise - 0.4f * dive * Mth.lerp(m.diveDepth, 0.4f, 1f)
                - 0.5f * m.w(WingMotion.TUCK) + 0.75f * charge + 0.03f * shiver * Mth.sin(time * 3.7f);
        float twist = hover * FlightRules.HOVER_PLANE + 0.1f * charge;

        float flutterAmp = 0.012f * ground + (0.02f + 0.05f * wind) * (1f - ground) + 0.04f * shiver;
        float flutterRate = 1.6f + 1.8f * wind;
        float waveAmp = 0.04f * ground + 0.07f * cruise + 0.04f * dive + 0.06f * shiver;
        float waveRate = 0.16f + 0.1f * cruise + 0.08f * wind;
        float swirl = 0f;
        float breathe = 0f;
        float gustIdle = 0f;
        if (idle > 0.001f) {
            float env = idle * Mth.sin((float) Math.PI * p);
            switch (variant) {
                case 0 -> {
                    gustIdle = env;
                    flutterAmp += env * 0.05f;
                    flutterRate += env * 1.5f;
                    waveAmp += env * 0.12f;
                    waveRate += env * 0.2f;
                }
                case 1 -> swirl = env;
                default -> breathe = env;
            }
        }
        float f = Mth.clamp(fold - 0.5f * breathe * ground - 0.3f * gustIdle * ground, 0f, 1f);
        float hw = m.hoverBeat;
        float beatA = m.beating() ? m.beatWeight : 0f;
        float bank = m.bank * 0.35f;
        Membrane sheet = membrane();
        if (sheet != null) {
            float airborne = Mth.clamp(1f - ground - charge, 0f, 1f);
            float camber = beatA > 0f ? beatA * 1.2f * STROKE.lagged(m.beat, 0.06f) : 0f;
            float puff = (0.25f + 0.2f * wind) * airborne + 0.9f * chute + camber + 0.4f * breathe
                    + 0.12f * (airborne + gustIdle) * Mth.sin(time * flutterRate * 1.3f);
            sheet.billow(Mth.clamp(puff * (1f - 0.8f * f), -0.6f, 1.2f));
        }

        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            float shoulderStroke = lift * (1f - f) + breathe * 0.3f + settle * 0.15f * Mth.sin(time * 1.5f);
            float shoulderSweep = sweep * (1f - f) + breathe * 0.25f + gustIdle * 0.15f;
            float shoulderTwist = twist * (1f - f);
            if (beatA > 0f) {
                float t = m.beat;
                shoulderStroke += beatA * STROKE.at(t);
                shoulderSweep += beatA * SWEEP.at(t);
                shoulderTwist += beatA * TWIST.at(t);
            }
            if (hw > 0.001f) {
                float ph = m.hoverPhase;
                shoulderSweep += hw * (0.2f + 0.4f * Mth.cos(ph));
                shoulderStroke += hw * 0.12f * Mth.sin(2f * ph);
                shoulderTwist += hw * 0.45f * Mth.sin(ph);
            }
            shoulderStroke += m.stall * (0.25f + 0.3f * Mth.sin(time * 1.2f + i));
            shoulderStroke = Mth.lerp(chute, shoulderStroke, CHUTE_STROKE);
            shoulderSweep = Mth.lerp(chute, shoulderSweep, CHUTE_RAISE) + chute * 0.02f * Mth.sin(time * 4.7f);
            shoulderTwist = Mth.lerp(chute, shoulderTwist, CHUTE_PITCH);
            float rest = Math.max(FLOOR, shoulderStroke - s * bank);
            float overlap = i == 1 ? FOLD_OVERLAP * f : 0f;
            shoulder[i].rotate(shoulderTwist + overlap, s * (rest + FOLD_SHOULDER_Y * f),
                    s * (shoulderSweep + FOLD_SHOULDER_Z * f));
            for (int k = 0; k < SEGMENTS; k++) {
                float reach = (k + 1f) / SEGMENTS;
                float wave = waveAmp * Mth.sin(time * waveRate * 2f - k * 0.8f + i * 0.5f);
                float flutter = flutterAmp * reach * Mth.sin(time * flutterRate - k * 1.3f + i * 0.9f);
                float whip = beatA > 0f ? beatA * 0.3f * STROKE.lagged(m.beat, 0.05f + 0.035f * k) : 0f;
                float swim = hw > 0.001f ? hw * 0.3f * Mth.sin(m.hoverPhase - 0.9f * (k + 1)) : 0f;
                float curl = swirl * (0.1f + 0.06f * k) * Mth.sin(time * 0.35f - k * 0.6f);
                float splash = settle * 0.25f * reach * Mth.sin(time * 1.4f - k * 0.8f);
                seg[i][k].rotate(flutter * 0.6f, s * (wave + whip + swim + flutter + splash * 0.4f + FOLD_Y[k] * f),
                        s * (FOLD_Z[k] * f + curl + splash * 0.6f));
            }
        }
    }
}
