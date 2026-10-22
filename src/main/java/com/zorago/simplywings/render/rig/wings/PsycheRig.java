package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class PsycheRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int CLOSED = 2;
    private static final int SWING = 3;
    private static final int[] IDLES = {120, 80, 46, 90, 70};
    private static final int FORE_FLAMES = 6;
    private static final int HIND_FLAMES = 4;
    private static final float CLOSE = 1.42f;
    private static final float[] KEYS = Cycle.beat(0.5f, 0.3f);
    private static final Cycle STROKE = Cycle.of(KEYS, -0.85f, 0.1f, 1.25f, 1.3f, -0.2f, -1f);
    private static final Cycle SWEEP = Cycle.of(KEYS, 0.12f, -0.08f, -0.15f, -0.02f, 0.18f, 0.2f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.12f, 0f, -0.28f, -0.1f);

    private final Bone[] fore = new Bone[2];
    private final Bone[] hind = new Bone[2];
    private final Bone[] tail = new Bone[2];
    private final Bone[] tailTip = new Bone[2];
    private final Bone[][] foreFlame = new Bone[2][FORE_FLAMES];
    private final Bone[][] hindFlame = new Bone[2][HIND_FLAMES];
    private Bone lamp;

    public PsycheRig() {
        super("psyche");
    }

    @Override
    public Material material() {
        return Material.EMISSIVE;
    }

    @Override
    public int[] idleLengths() {
        return IDLES;
    }

    @Override
    protected void onBind(Bones bones) {
        String[] side = {"right_", "left_"};
        for (int i = 0; i < 2; i++) {
            fore[i] = bones.get(side[i] + "fore");
            hind[i] = bones.get(side[i] + "hind");
            tail[i] = bones.get(side[i] + "hind_tail");
            tailTip[i] = bones.get(side[i] + "hind_tail_tip");
            for (int k = 0; k < FORE_FLAMES; k++) {
                foreFlame[i][k] = bones.get(side[i] + "fore_flame_" + (k + 1));
            }
            for (int k = 0; k < HIND_FLAMES; k++) {
                hindFlame[i][k] = bones.get(side[i] + "hind_flame_" + (k + 1));
            }
        }
        lamp = bones.get("lamp");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.07f, 0.82f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.2f, 0.7f);
        memory.spring(CLOSED, m.w(WingMotion.GROUND), 0.08f, 0.78f);
        float push = Mth.clamp(m.speed / 30f, 0f, 1f) * 0.5f + m.bank * 0.6f + m.climb * 0.02f;
        memory.spring(SWING, push, 0.06f, 0.92f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float closed = Mth.clamp(memory.get(CLOSED, pt), 0f, 1f);
        float swing = memory.get(SWING, pt);
        float hover = m.w(WingMotion.HOVER);
        float cruise = m.w(WingMotion.CRUISE);
        float brake = m.w(WingMotion.BRAKE);
        float dive = m.w(WingMotion.DIVE) * Mth.lerp(m.diveDepth, 0.4f, 1f);
        float tuck = m.w(WingMotion.TUCK);
        int variant = m.rouseVariant % 5;
        float p = m.rouseProgress;
        float env = idle * Mth.sin((float) Math.PI * p);
        float hold = idle * smooth(Mth.clamp(p * 3f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 3f, 0f, 1f));

        float open = 0f;
        float flare = 0f;
        float shiver = 0f;
        float lampGlow = 0f;
        switch (variant) {
            case 0 -> open = idle * (0.5f - 0.5f * Mth.cos(p * Mth.TWO_PI * 2f)) * 0.75f;
            case 1 -> {
                open = hold;
                flare = hold;
            }
            case 2 -> shiver = env;
            case 3 -> {
                open = 0.45f * hold;
                lampGlow = env;
            }
            default -> {
                float rise = idle * Mth.sin((float) Math.PI * Mth.clamp(p * 1.3f, 0f, 1f));
                open = rise;
                flare = 0.4f * rise;
            }
        }
        float shut = closed * (1f - open);
        float base = CLOSE * shut + 0.28f * cruise + 0.15f * hover + 0.35f * brake + 0.85f * m.w(WingMotion.CHARGE)
                + 0.3f * m.w(WingMotion.AIR) + 0.12f * dive;
        float sweep = 0.08f * hover + 0.35f * brake - 0.45f * dive - 0.6f * tuck - 0.04f * cruise + 0.05f * shut;
        float hindSweep = sweep * 0.8f - 0.1f * tuck;
        float twist = -0.45f * brake + 0.06f * shut;
        float beatA = m.beating() ? m.beatWeight : 0f;
        float hw = m.hoverBeat;
        float flick = shiver * 0.18f * Mth.sin(time * 3.7f);
        float breath = (1f - closed) * 0.03f * Mth.sin(time * 0.3f) + closed * (1f - open) * 0.02f * Mth.sin(time * 0.05f);
        float wobble = settle * 0.2f * Mth.sin(time * 1.3f);

        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            float fStroke = base + breath + flick + wobble;
            float hStroke = base * 0.97f + breath + flick * 0.8f + wobble * 0.8f;
            float fTwist = twist;
            float hTwist = twist;
            float fSweep = sweep;
            float hSweep = hindSweep;
            if (beatA > 0f) {
                float t = m.beat;
                fStroke += beatA * STROKE.at(t);
                hStroke += beatA * 0.95f * STROKE.at(t - 0.04f);
                fTwist += beatA * TWIST.at(t);
                hTwist += beatA * TWIST.at(t - 0.04f);
                fSweep += beatA * SWEEP.at(t);
                hSweep += beatA * 0.8f * SWEEP.at(t - 0.04f);
            }
            if (hw > 0.001f) {
                float ph = m.hoverPhase;
                fStroke += hw * 0.65f * Mth.cos(ph);
                hStroke += hw * 0.6f * Mth.cos(ph - 0.35f);
                fTwist += hw * 0.35f * Mth.sin(ph);
                hTwist += hw * 0.3f * Mth.sin(ph - 0.35f);
            }
            if (m.stall > 0.001f) {
                fStroke += m.stall * 0.4f * Mth.sin(time * 1.1f);
                hStroke += m.stall * 0.4f * Mth.sin(time * 1.1f - 0.5f);
            }
            float bank = m.bank * 0.25f;
            fore[i].rotate(fTwist, s * fStroke - bank, s * fSweep);
            hind[i].rotate(hTwist, s * hStroke - bank, s * hSweep);
            float trail = 0.25f * cruise + 0.3f * dive + 0.15f * Mth.clamp(m.speed / 30f, 0f, 1f);
            tail[i].rotate(trail * 0.5f + 0.1f * Mth.sin(time * 0.5f + i), s * 0.08f * Mth.sin(time * 0.7f), s * 0.06f * Mth.sin(time * 0.43f));
            tailTip[i].rotate(trail * 0.4f + 0.15f * Mth.sin(time * 0.8f + i + 1f), 0f, s * 0.1f * Mth.sin(time * 0.6f + 1f));
            float size = 1f + 0.5f * flare + 0.25f * settle;
            flames(foreFlame[i], s, time, size, trail, 0.3f);
            flames(hindFlame[i], s, time, size, trail, 2.1f);
        }
        float sway = 0.12f * Mth.sin(time * 0.06f) + 0.5f * swing + 0.25f * lampGlow * Mth.sin(time * 0.2f);
        float bright = 1f + 0.25f * lampGlow + 0.05f * Mth.sin(time * 0.9f);
        lamp.rotate(0.2f * swing, 0f, sway).scale(bright, bright, bright);
    }

    private static void flames(Bone[] bones, float s, float time, float size, float trail, float seed) {
        for (int k = 0; k < bones.length; k++) {
            float n = 0.09f * Mth.sin(time * 1.7f + k * 1.9f + seed) + 0.05f * Mth.sin(time * 3.9f + k + seed);
            float len = size * (1f + 0.15f * Mth.sin(time * 2.2f + k * 1.3f + seed) + 0.08f * Mth.sin(time * 5.3f + k));
            bones[k].rotate(0f, 0f, s * (n - trail * 0.4f)).scale(Math.max(0.3f, len), 1f + 0.1f * n, 1f);
        }
    }

    private static float smooth(float x) {
        return x * x * (3f - 2f * x);
    }
}
