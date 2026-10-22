package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
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
    private static final Cycle STROKE = Cycle.of(KEYS, -0.75f, 0.1f, 1f, 1.05f, -0.3f, -0.9f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.2f, -0.12f, -0.2f, -0.05f, 0.25f, 0.3f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.1f, -0.05f, -0.25f, -0.1f);

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
        float flutterAmp = 0.08f + 0.12f * gust + 0.08f * hover + 0.15f * brake + 0.25f * gustIdle + 0.2f * m.stall
                + 0.1f * settle;
        float flutterRate = 0.35f + 0.5f * gust + 0.3f * gustIdle + 0.2f * hover;
        float lift = 0.85f * m.w(WingMotion.CHARGE) + 0.22f * hover + 0.06f * cruise + 0.35f * brake
                + 0.3f * m.w(WingMotion.AIR) + 0.3f * puff;
        float sweep = 0.12f * hover + 0.45f * brake - 0.35f * m.w(WingMotion.DIVE) * Mth.lerp(m.diveDepth, 0.4f, 1f)
                - 0.45f * m.w(WingMotion.TUCK) + 0.2f * m.w(WingMotion.CHARGE) - 0.06f * cruise * gust;
        float twist = -0.65f * brake;
        float beatA = m.beating() ? m.beatWeight : 0f;
        float hw = m.hoverBeat;
        float jitter = hover * 0.05f * (Mth.sin(time * 2.7f) + Mth.sin(time * 4.1f + 1f));

        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            for (int w = 0; w < 2; w++) {
                float lag = w * 0.06f;
                float stroke = lift * (1f - f) * (w == 0 ? 1f : 0.85f) + jitter;
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
                    stroke += hw * 0.6f * Mth.cos(ph);
                    tw += hw * 0.4f * Mth.sin(ph);
                }
                stroke += settle * 0.1f * Mth.sin(time * 1.7f + w);
                sail[i][w].rotate(tw, s * stroke - m.bank * 0.3f, s * sw);
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
    }
}
