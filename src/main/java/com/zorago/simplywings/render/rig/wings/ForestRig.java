package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.WingRig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;

public final class ForestRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int FOLD = 2;
    private static final int WIND = 3;
    private static final int[] IDLES = {64, 40, 70};
    private static final String[] WINGS = {"branch", "hind_branch"};
    private static final String[] SEGMENTS = {"", "_mid", "_tip"};
    private static final float[] KEYS = Cycle.beat(0.5f, 0.25f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.7f, 0.1f, 0.95f, 1f, -0.3f, -0.85f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.18f, -0.12f, -0.2f, -0.05f, 0.22f, 0.28f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.28f, 0.1f, -0.05f, -0.24f, -0.08f);

    private final Bone[][][] segment = new Bone[2][2][3];
    private final List<List<List<Bone>>> leaves = new ArrayList<>();
    private final List<List<List<Bone>>> flowers = new ArrayList<>();

    public ForestRig() {
        super("forest");
    }

    @Override
    public int[] idleLengths() {
        return IDLES;
    }

    @Override
    protected void onBind(Bones bones) {
        String[] side = {"right_", "left_"};
        leaves.clear();
        flowers.clear();
        for (int i = 0; i < 2; i++) {
            List<List<Bone>> sideLeaves = new ArrayList<>();
            List<List<Bone>> sideFlowers = new ArrayList<>();
            for (int w = 0; w < 2; w++) {
                String base = side[i] + WINGS[w];
                for (int k = 0; k < 3; k++) {
                    segment[i][w][k] = bones.get(base + SEGMENTS[k]);
                }
                List<Bone> l = new ArrayList<>();
                for (int k = 1; bones.has(base + "_leaf_" + k); k++) {
                    l.add(bones.get(base + "_leaf_" + k));
                }
                List<Bone> f = new ArrayList<>();
                for (int k = 1; bones.has(base + "_flower_" + k); k++) {
                    f.add(bones.get(base + "_flower_" + k));
                }
                sideLeaves.add(l);
                sideFlowers.add(f);
            }
            leaves.add(sideLeaves);
            flowers.add(sideFlowers);
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.15f, 0.75f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.3f, 0.6f);
        float fold = m.w(WingMotion.GROUND) + 0.8f * m.w(WingMotion.TUCK) + 0.45f * m.diveDepth * m.w(WingMotion.DIVE)
                + 0.3f * m.w(WingMotion.AIR);
        memory.spring(FOLD, Mth.clamp(fold, 0f, 1f), 0.12f, 0.7f);
        memory.spring(WIND, Mth.clamp(m.speed / 25f, 0f, 1.3f), 0.12f, 0.75f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float fold = Mth.clamp(memory.get(FOLD, pt), 0f, 1f);
        float wind = memory.get(WIND, pt);
        float hover = m.w(WingMotion.HOVER);
        float brake = m.w(WingMotion.BRAKE);
        float cruise = m.w(WingMotion.CRUISE);
        int variant = m.rouseVariant % 3;
        float p = m.rouseProgress;

        float bloom = 0f;
        float shiver = 0f;
        float sun = 0f;
        if (idle > 0.001f) {
            float env = idle * Mth.sin((float) Math.PI * p);
            switch (variant) {
                case 0 -> bloom = env;
                case 1 -> shiver = env;
                default -> sun = idle * smooth(Mth.clamp(p * 2f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 2f, 0f, 1f));
            }
        }
        float f = Mth.clamp(fold - 0.6f * sun, 0f, 1f);
        float lift = 0.8f * m.w(WingMotion.CHARGE) + 0.22f * hover + 0.06f * cruise + 0.35f * brake + 0.3f * m.w(WingMotion.AIR)
                + 0.35f * sun;
        float sweep = 0.12f * hover + 0.45f * brake - 0.35f * m.w(WingMotion.DIVE) * Mth.lerp(m.diveDepth, 0.4f, 1f)
                - 0.4f * m.w(WingMotion.TUCK) + 0.25f * m.w(WingMotion.CHARGE) + 0.2f * sun;
        float twist = -0.65f * brake;
        float rustleAmp = 0.06f + 0.1f * wind + 0.05f * hover + 0.12f * brake + 0.3f * shiver + 0.15f * settle + 0.2f * m.stall;
        float rustleRate = 0.35f + 0.6f * wind + 0.9f * shiver;
        float beatA = m.beating() ? m.beatWeight : 0f;
        float hw = m.hoverBeat;

        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            for (int w = 0; w < 2; w++) {
                float stroke = lift * (1f - f) * (w == 0 ? 1f : 0.85f);
                float sw = sweep * (1f - f) + (w == 0 ? -0.85f : -0.6f) * f;
                float tw = twist * (1f - f);
                if (beatA > 0f) {
                    float t = m.beat - w * 0.07f;
                    stroke += beatA * (w == 0 ? 1f : 0.85f) * STROKE.at(t);
                    sw += beatA * SWEEP.at(t);
                    tw += beatA * TWIST.at(t);
                }
                if (hw > 0.001f) {
                    float ph = m.hoverPhase - w * 0.6f;
                    stroke += hw * 0.6f * Mth.cos(ph);
                    tw += hw * 0.4f * Mth.sin(ph);
                }
                stroke += settle * 0.12f * Mth.sin(time * 1.4f + w);
                stroke += 0.015f * Mth.sin(time * 0.06f + w) * m.w(WingMotion.GROUND);
                Bone[] seg = segment[i][w];
                seg[0].rotate(tw, s * stroke - m.bank * 0.3f, s * sw);
                for (int k = 1; k < 3; k++) {
                    float whip = beatA > 0f ? beatA * 0.25f * STROKE.lagged(m.beat - w * 0.07f, 0.05f + 0.05f * k) : 0f;
                    float droop = -0.22f * f * k + 0.04f * Mth.sin(time * 0.2f + k + w) * (0.5f + wind);
                    seg[k].rotate(0f, s * whip, s * (droop + 0.08f * sun));
                }
                List<Bone> l = leaves.get(i).get(w);
                for (int k = 0; k < l.size(); k++) {
                    float ph = time * rustleRate + k * 1.37f + w * 0.9f + i * 0.4f;
                    float flutter = rustleAmp * (Mth.sin(ph) + 0.5f * Mth.sin(ph * 2.3f + 1f));
                    float flip = rustleAmp * 0.8f * Mth.sin(ph * 1.3f + 0.6f) + 0.65f * sun + 0.25f * cruise * wind;
                    float closed = 0.35f * f * (k % 2 == 0 ? 1f : 0.7f);
                    l.get(k).rotate(flip, s * flutter * 0.4f, s * (flutter + closed));
                }
                List<Bone> fl = flowers.get(i).get(w);
                for (int k = 0; k < fl.size(); k++) {
                    float open = 1f + 0.8f * bloom * (0.8f + 0.2f * Mth.sin(time * 0.3f + k));
                    float bob = 0.05f * Mth.sin(time * 0.4f + k * 2f) + rustleAmp * 0.3f * Mth.sin(time * rustleRate + k);
                    fl.get(k).rotate(0f, 0f, s * bob + bloom * 0.4f * (k % 2 == 0 ? 1f : -1f)).scale(open, open, open);
                }
            }
        }
    }

    private static float smooth(float x) {
        return x * x * (3f - 2f * x);
    }
}
