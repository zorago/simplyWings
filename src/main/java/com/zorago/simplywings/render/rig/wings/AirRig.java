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
    private static final int[] IDLES = {34, 56, 46};
    private static final int[] RIBBONS = {4, 3};
    private static final int SEGMENTS = 3;
    private static final String[] SAILS = {"sail", "hind_sail"};
    private static final float[] KEYS = Cycle.beat(0.42f, 0.2f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.5f, 0.82f, 0.78f, 0.35f, -0.14f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.22f, 0.05f, 0.28f, 0.26f, 0f, -0.28f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.25f, -0.1f);
    private static final float FLOOR = -5f * Mth.DEG_TO_RAD;
    private static final float CHUTE_STROKE = 0.31f;
    private static final float CHUTE_RAISE = 0.85f;
    private static final float CHUTE_PITCH = 0.48f;
    private static final String[] TIPS = {"right_sail", "left_sail"};

    private final Bone[][] sail = new Bone[2][2];
    private final Bone[][][][] ribbon = new Bone[2][2][][];

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
            for (int w = 0; w < 2; w++) {
                sail[i][w] = bones.get(side[i] + SAILS[w]);
                ribbon[i][w] = new Bone[RIBBONS[w]][SEGMENTS];
                for (int r = 0; r < RIBBONS[w]; r++) {
                    for (int j = 0; j < SEGMENTS; j++) {
                        ribbon[i][w][r][j] = bones.get(side[i] + SAILS[w] + "_ribbon_" + (r + 1) + "_" + (j + 1));
                    }
                }
            }
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.3f, 0.6f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.3f, 0.6f);
        float fold = m.w(WingMotion.GROUND) + 0.8f * m.w(WingMotion.TUCK) + 0.5f * m.diveDepth * m.w(WingMotion.DIVE)
                + 0.25f * m.w(WingMotion.AIR);
        memory.spring(FOLD, Mth.clamp(fold, 0f, 1f), 0.14f, 0.66f);
        memory.spring(GUST, Mth.clamp(m.speed / 30f, 0f, 1.2f), 0.15f, 0.7f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float fold = Mth.clamp(memory.get(FOLD, pt), 0f, 1f);
        float gust = memory.get(GUST, pt);
        float ground = m.w(WingMotion.GROUND);
        float hover = m.w(WingMotion.HOVER);
        float brake = m.w(WingMotion.BRAKE);
        float cruise = m.w(WingMotion.CRUISE);
        int variant = m.rouseVariant % 3;
        float p = m.rouseProgress;

        float gustIdle = 0f;
        float whirl = 0f;
        float puff = 0f;
        if (idle > 0.001f) {
            switch (variant) {
                case 0 -> gustIdle = idle * Mth.sin((float) Math.PI * Mth.clamp(p * 1.4f, 0f, 1f));
                case 1 -> whirl = idle * Mth.sin((float) Math.PI * p);
                default -> puff = idle * Mth.sin((float) Math.PI * p);
            }
        }
        float f = Mth.clamp(fold - 0.5f * puff, 0f, 1f);
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
        float flutterAmp = 0.08f + 0.12f * gust + 0.08f * hover + 0.15f * slowBrake + 0.06f * chute + 0.25f * gustIdle
                + 0.2f * m.stall + 0.1f * settle + 0.12f * shiver;
        float flutterRate = 0.35f + 0.5f * gust + 0.3f * gustIdle + 0.2f * hover + 0.4f * shiver;
        float lift = 0.35f * charge + 0.25f * hover + 0.06f * cruise + 0.25f * slowBrake + 0.3f * m.w(WingMotion.AIR)
                + 0.3f * puff + 0.5f * Math.max(0f, blast) + 0.035f * shiver * Mth.sin(time * 2.9f);
        float sweep = 0.12f * hover + 0.1f * slowBrake - 0.35f * m.w(WingMotion.DIVE) * Mth.lerp(m.diveDepth, 0.4f, 1f)
                - 0.45f * m.w(WingMotion.TUCK) + 0.75f * charge - 0.06f * cruise * gust + 0.4f * blast
                + 0.03f * shiver * Mth.sin(time * 3.7f);
        float twist = hover * FlightRules.HOVER_PLANE;
        float beatA = m.beating() ? m.beatWeight : 0f;
        float hw = m.hoverBeat;
        float airborne = Mth.clamp(1f - ground - charge, 0f, 1f);
        float billow = (0.35f * airborne + 0.9f * chute + 0.3f * shiver) * (1f - f);
        if (beatA > 0f) {
            billow += beatA * 1.1f * STROKE.lagged(m.beat, 0.06f);
        }

        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            for (int w = 0; w < 2; w++) {
                float lag = w * 0.06f;
                float stroke = lift * (1f - f) * (w == 0 ? 1f : 0.85f);
                float sw = sweep * (1f - f) + (w == 0 ? -0.95f : -0.75f) * f + puff * 0.2f;
                float tw = twist * (1f - f);
                if (beatA > 0f) {
                    float t = m.beat - lag;
                    stroke += beatA * (w == 0 ? 1f : 0.85f) * STROKE.at(t);
                    sw += beatA * SWEEP.at(t);
                    tw += beatA * TWIST.at(t);
                }
                if (hw > 0.001f) {
                    float ph = m.hoverPhase - w * 0.6f;
                    sw += hw * (0.2f + 0.4f * Mth.cos(ph));
                    stroke += hw * 0.1f * Mth.sin(2f * ph);
                    tw += hw * 0.45f * Mth.sin(ph);
                }
                stroke += settle * 0.1f * Mth.sin(time * 1.7f + w);
                stroke = Mth.lerp(chute, stroke, CHUTE_STROKE * (w == 0 ? 1f : 0.9f));
                sw = Mth.lerp(chute, sw, CHUTE_RAISE * (w == 0 ? 1f : 0.5f)) + chute * 0.02f * Mth.sin(time * 4.7f + w);
                tw = Mth.lerp(chute, tw, CHUTE_PITCH);
                float roll = m.bank * 0.3f;
                sail[i][w].rotate(tw, s * Math.max(FLOOR, stroke - s * roll), s * sw);
                Bone[][] rib = ribbon[i][w];
                for (int r = 0; r < rib.length; r++) {
                    for (int j = 0; j < SEGMENTS; j++) {
                        float reach = (j + 1f) / SEGMENTS;
                        float phase = time * flutterRate * 2f - j * 0.95f - r * 0.7f - w * 1.3f + i * 0.5f;
                        float wave = flutterAmp * reach * Mth.sin(phase);
                        float ripple = flutterAmp * 0.45f * reach * Mth.sin(phase * 1.7f + 0.8f);
                        float whip = beatA > 0f ? beatA * 0.25f * STROKE.lagged(m.beat - lag, 0.05f + 0.04f * j) : 0f;
                        float curl = f * (-0.22f - 0.05f * r) + gustIdle * 0.2f * Mth.sin(time * 1.3f + r);
                        float spin = whirl * (0.6f * Mth.sin(time * 0.45f - j * 0.8f - r * 0.5f));
                        Bone b = rib[r][j];
                        b.rotate(spin + ripple * 0.5f, s * (wave + whip), s * (curl + ripple * 0.4f));
                        float shrink = 1f - 0.22f * f * reach;
                        b.scale(shrink, 1f, 1f);
                    }
                }
            }
        }
        Membrane skin = membrane();
        if (skin != null) {
            skin.billow(Mth.clamp(billow, -0.6f, 1.2f));
        }
    }
}
