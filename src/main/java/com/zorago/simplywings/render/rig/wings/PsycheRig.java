package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FlightRules;
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
    private static final Cycle STROKE = Cycle.of(KEYS, 0.05f, 0.62f, 1.15f, 1.12f, 0.4f, -0.05f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.2f, 0.05f, 0.22f, 0.2f, 0f, -0.22f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.32f, 0.14f, 0f, -0.28f, -0.1f);
    private static final float FLOOR = -5f * Mth.DEG_TO_RAD;
    private static final float HIND_LAG = 0.06f;
    private static final float CHUTE_STROKE = 0.31f;
    private static final float CHUTE_RAISE = 0.85f;
    private static final float CHUTE_PITCH = 0.48f;
    private static final String[] TIPS = {"right_fore_flame_3", "left_fore_flame_3"};

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
        float charge = m.w(WingMotion.CHARGE);
        float chute = FlightRules.chute(m);
        float slowBrake = brake - chute;
        float warm = charge * charge;
        float base = CLOSE * shut + 0.28f * cruise + 0.25f * hover + 0.3f * slowBrake + 0.35f * charge
                + 0.3f * m.w(WingMotion.AIR) + 0.3f * m.w(WingMotion.LANDING) + 0.12f * dive;
        float sweep = 0.12f * hover + 0.6f * charge - 0.45f * dive - 0.6f * tuck - 0.04f * cruise + 0.05f * shut
                + 0.1f * m.w(WingMotion.LANDING);
        float hindSweep = sweep * 0.8f - 0.1f * tuck;
        float twist = hover * FlightRules.HOVER_PLANE + 0.06f * shut - 0.2f * m.w(WingMotion.LANDING);
        float beatA = m.beating() ? m.beatWeight : 0f;
        float hw = m.hoverBeat;
        float flick = shiver * 0.18f * Mth.sin(time * 3.7f) + warm * 0.06f * Mth.sin(time * 3.6f);
        float breath = (1f - closed) * 0.03f * Mth.sin(time * 0.3f) + closed * (1f - open) * 0.02f * Mth.sin(time * 0.05f);
        float wobble = settle * 0.2f * Mth.sin(time * 1.3f);
        float blast = 0f;
        float strike = 0f;
        if (m.launch >= 0f) {
            float lp = m.launch;
            strike = smooth(Mth.clamp(lp / 0.3f, 0f, 1f));
            blast = lp < 0.3f ? 1f : 1f - (lp - 0.3f) / 0.7f;
        }
        float tremble = chute * 0.02f * Mth.sin(time * 4.7f);
        float push = time * Mth.TWO_PI * (4f / 3f) / 20f;

        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            float fStroke = base + breath + flick + wobble;
            float hStroke = base * 0.97f + breath + flick * 0.8f + wobble * 0.8f;
            float fTwist = twist;
            float hTwist = twist;
            float fSweep = sweep;
            float hSweep = hindSweep;
            if (blast > 0f) {
                fStroke += blast * Mth.lerp(strike, 1.1f, -0.05f - base);
                hStroke += blast * Mth.lerp(strike, 1f, -0.05f - base);
                fSweep += blast * Mth.lerp(strike, 0.3f, -0.25f);
                hSweep += blast * Mth.lerp(strike, 0.25f, -0.2f);
            }
            if (beatA > 0f) {
                float t = m.beat;
                float a = beatA * (1f - blast);
                fStroke += a * STROKE.at(t);
                hStroke += a * 0.95f * STROKE.at(t - HIND_LAG);
                fTwist += a * TWIST.at(t);
                hTwist += a * TWIST.at(t - HIND_LAG);
                fSweep += a * SWEEP.at(t);
                hSweep += a * 0.8f * SWEEP.at(t - HIND_LAG);
            }
            if (hw > 0.001f) {
                float ph = m.hoverPhase;
                fSweep += hw * (0.2f + 0.42f * Mth.cos(ph));
                hSweep += hw * (0.2f + 0.4f * Mth.cos(ph - 0.5f));
                fStroke += hw * 0.12f * Mth.sin(2f * ph);
                hStroke += hw * 0.12f * Mth.sin(2f * ph - 1f);
                fTwist += hw * 0.5f * Mth.sin(ph);
                hTwist += hw * 0.45f * Mth.sin(ph - 0.5f);
            }
            if (m.stall > 0.001f) {
                fStroke += m.stall * (0.25f + 0.3f * Mth.sin(time * 1.1f));
                hStroke += m.stall * (0.25f + 0.3f * Mth.sin(time * 1.1f - 0.5f));
            }
            if (slowBrake > 0.001f) {
                fStroke += slowBrake * 0.22f * Mth.cos(push);
                hStroke += slowBrake * 0.2f * Mth.cos(push - 0.4f);
            }
            fStroke = Mth.lerp(chute, fStroke, CHUTE_STROKE);
            hStroke = Mth.lerp(chute, hStroke, CHUTE_STROKE * 0.9f);
            fSweep = Mth.lerp(chute, fSweep, CHUTE_RAISE + tremble);
            hSweep = Mth.lerp(chute, hSweep, CHUTE_RAISE * 0.55f + tremble);
            fTwist = Mth.lerp(chute, fTwist, CHUTE_PITCH);
            hTwist = Mth.lerp(chute, hTwist, CHUTE_PITCH);
            float bank = m.bank * 0.25f;
            fore[i].rotate(fTwist, s * Math.max(FLOOR, fStroke - s * bank), s * fSweep);
            hind[i].rotate(hTwist, s * Math.max(FLOOR, hStroke - s * bank), s * hSweep);
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
