package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class WaterRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int FOLD = 2;
    private static final int SEGMENTS = 5;
    private static final int DROPS = 3;
    private static final int[] IDLES = {50, 60, 44};
    private static final float D = Mth.DEG_TO_RAD;
    private static final float[] FOLD_Z = {-6f * D, 14f * D, 36f * D, 46f * D, 52f * D};
    private static final float[] FOLD_Y = {0f, -6f * D, -6f * D, -6f * D, -4f * D};
    private static final float FOLD_SHOULDER_Z = -70f * D;
    private static final float FOLD_SHOULDER_Y = 16f * D;
    private static final float[] KEYS = Cycle.beat(0.5f, 0.3f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.75f, 0.1f, 0.95f, 1f, -0.25f, -0.85f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.15f, -0.1f, -0.18f, -0.05f, 0.2f, 0.25f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.25f, 0.1f, -0.05f, -0.22f, -0.08f);

    private final Bone[][] seg = new Bone[2][SEGMENTS];
    private final Bone[] shoulder = new Bone[2];
    private final Bone[][] drop = new Bone[2][DROPS];

    public WaterRig() {
        super("water");
    }

    @Override
    public Material material() {
        return Material.TRANSLUCENT;
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
            for (int k = 0; k < DROPS; k++) {
                drop[i][k] = bones.get(side[i] + "drop_" + (k + 1));
            }
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.2f, 0.7f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.3f, 0.62f);
        float fold = m.w(WingMotion.GROUND) + 0.75f * m.w(WingMotion.TUCK) + 0.3f * m.w(WingMotion.AIR)
                + 0.4f * m.diveDepth * m.w(WingMotion.DIVE) + 0.35f * m.w(WingMotion.LANDING);
        memory.spring(FOLD, Mth.clamp(fold, 0f, 1f), 0.12f, 0.7f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float fold = Mth.clamp(memory.get(FOLD, pt), 0f, 1f);
        float ground = m.w(WingMotion.GROUND);
        float hover = m.w(WingMotion.HOVER);
        float cruise = m.w(WingMotion.CRUISE);
        float brake = m.w(WingMotion.BRAKE);
        float dive = m.w(WingMotion.DIVE);
        int variant = m.rouseVariant % 3;
        float p = m.rouseProgress;

        float lift = 0.8f * m.w(WingMotion.CHARGE) + 0.25f * hover + 0.06f * cruise + 0.35f * brake
                + 0.3f * m.w(WingMotion.AIR) + 0.04f * dive;
        lift = Math.min(lift, 0.95f);
        float sweep = 0.12f * hover + 0.04f * cruise + 0.5f * brake - 0.4f * dive * Mth.lerp(m.diveDepth, 0.4f, 1f)
                - 0.5f * m.w(WingMotion.TUCK) + 0.3f * m.w(WingMotion.CHARGE);
        float twist = -0.7f * brake + 0.1f * m.w(WingMotion.CHARGE);

        float waveAmp = 0.05f * ground + 0.09f * cruise + 0.04f * dive;
        float waveRate = 0.12f + 0.06f * cruise;
        float waveLag = 0.7f;
        float curlBoost = 0f;
        float unfold = 0f;
        float pour = 0f;
        if (idle > 0.001f) {
            switch (variant) {
                case 0 -> {
                    waveAmp += idle * 0.22f;
                    waveRate += idle * 0.1f;
                    curlBoost = idle * 0.25f * Mth.sin(time * 0.24f);
                }
                case 1 -> pour = idle * Mth.sin((float) Math.PI * p);
                default -> {
                    unfold = idle * Mth.sin((float) Math.PI * Mth.clamp(p * 1.1f, 0f, 1f));
                    waveAmp += unfold * 0.18f;
                    waveRate += unfold * 0.15f;
                }
            }
        }
        float f = Mth.clamp(fold - 0.55f * unfold * ground, 0f, 1f);
        float hw = m.hoverBeat;
        float beatA = m.beating() ? m.beatWeight : 0f;
        float bank = m.bank * 0.35f;

        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            float shoulderStroke = lift * (1f - f) + pour * 0.55f + settle * 0.15f * Mth.sin(time * 1.5f);
            float shoulderSweep = sweep * (1f - f) + pour * 0.35f;
            float shoulderTwist = twist * (1f - f) - pour * 0.25f;
            if (beatA > 0f) {
                float t = m.beat;
                shoulderStroke += beatA * STROKE.at(t);
                shoulderSweep += beatA * SWEEP.at(t);
                shoulderTwist += beatA * TWIST.at(t);
            }
            if (hw > 0.001f) {
                float ph = m.hoverPhase;
                shoulderStroke += hw * 0.55f * Mth.cos(ph);
                shoulderTwist += hw * 0.35f * Mth.sin(ph);
            }
            shoulderStroke += m.stall * 0.5f * Mth.sin(time * 1.2f + i);
            shoulder[i].rotate(shoulderTwist, s * (shoulderStroke + FOLD_SHOULDER_Y * f) - bank,
                    s * (shoulderSweep + FOLD_SHOULDER_Z * f));
            for (int k = 0; k < SEGMENTS; k++) {
                float reach = (k + 1f) / SEGMENTS;
                float wave = waveAmp * Mth.sin(time * waveRate * 2f - k * waveLag + i * 0.4f);
                float whip = 0f;
                if (beatA > 0f) {
                    whip = beatA * 0.32f * STROKE.lagged(m.beat, 0.05f + 0.035f * k);
                }
                float swim = 0f;
                if (hw > 0.001f) {
                    swim = hw * 0.32f * Mth.sin(m.hoverPhase - 0.9f * (k + 1));
                }
                float ripple = (hover * 0.06f + brake * 0.1f) * Mth.sin(time * 1.9f - k * 1.1f);
                float splash = settle * 0.28f * reach * Mth.sin(time * 1.4f - k * 0.8f);
                float curl = FOLD_Z[k] * f * (1f + curlBoost * reach) + pour * -0.12f * reach;
                float bend = FOLD_Y[k] * f;
                seg[i][k].rotate(ripple * 0.5f, s * (wave + whip + swim + ripple + bend + splash * 0.4f), s * (curl + splash * 0.6f));
            }
            for (int k = 0; k < DROPS; k++) {
                float e = Cycle.wrap(time * (0.03f + 0.007f * k) + k * 0.37f + i * 0.5f);
                float life = Mth.sin((float) Math.PI * e);
                Bone d = drop[i][k];
                float flying = 1f - ground;
                d.move(0f, 9f * e * e, 4f * e * flying);
                float sz = life * (0.7f + 0.3f * Mth.sin(time * 0.5f + k));
                d.scale(sz, sz * (1f + 0.6f * e), sz);
            }
        }
    }
}
