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

public final class KiteRig extends WingRig {
    private static final int FOLD = 0;
    private static final int GUST = 1;
    private static final int TAIL = 5;
    private static final float[] KEYS = Cycle.beat(0.5f, 0.3f);
    private static final Cycle TUG = Cycle.of(KEYS, 0f, 0.15f, 0.32f, 0.33f, 0.1f, -0.06f);
    private static final Cycle LIFT = Cycle.of(KEYS, -0.1f, 0.02f, 0.12f, 0.1f, 0f, -0.12f);
    private static final float FLOOR = -5f * Mth.DEG_TO_RAD;
    private static final float CHUTE_STROKE = 0.31f;
    private static final float CHUTE_RAISE = 0.85f;
    private static final float CHUTE_PITCH = 0.48f;
    private static final String[] TIPS = {"right_outer", "left_outer"};

    private final Bone[] shoulder = new Bone[2];
    private final Bone[] strut = new Bone[2];
    private final Bone[] outer = new Bone[2];
    private final Bone[] tail = new Bone[TAIL];

    public KiteRig() {
        super("kite");
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    protected void onBind(Bones bones) {
        String[] side = {"right_", "left_"};
        for (int i = 0; i < 2; i++) {
            shoulder[i] = bones.get(side[i] + "shoulder");
            strut[i] = bones.get(side[i] + "strut");
            outer[i] = bones.get(side[i] + "outer");
        }
        for (int k = 0; k < TAIL; k++) {
            tail[k] = bones.get("tail_" + (k + 1));
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        float fold = m.w(WingMotion.GROUND) * (1f - m.w(WingMotion.CHARGE)) + 0.5f * m.w(WingMotion.TUCK) + 0.25f * m.w(WingMotion.AIR);
        memory.spring(FOLD, Mth.clamp(fold, 0f, 1f), 0.14f, 0.72f);
        float wind = Mth.clamp(m.speed / 20f, 0f, 1f) * (1f - m.w(WingMotion.GROUND));
        memory.ease(GUST, wind, 0.08f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float time = m.time;
        float fold = Mth.clamp(memory.get(FOLD, m.partialTick), 0f, 1f);
        float wind = memory.get(GUST, m.partialTick);
        float open = 1f - fold;
        float flying = 1f - m.w(WingMotion.GROUND);
        float charge = m.w(WingMotion.CHARGE);
        float hover = m.w(WingMotion.HOVER);
        float chute = FlightRules.chute(m);
        float slowBrake = m.w(WingMotion.BRAKE) - chute;
        float warm = charge * charge;
        float stroke = 0.12f * m.w(WingMotion.CRUISE) + 0.4f * m.w(WingMotion.LANDING) + 0.3f * slowBrake
                + 0.2f * hover + 0.3f * charge + warm * 0.03f * Mth.sin(time * 2.9f);
        float sweep = 0.2f * m.w(WingMotion.LANDING) + 0.15f * slowBrake - 0.4f * m.w(WingMotion.DIVE) + 0.6f * charge
                + 0.12f * hover + warm * 0.03f * Mth.sin(time * 3.7f);
        float twist = -0.25f * m.w(WingMotion.LANDING) + hover * FlightRules.HOVER_PLANE;
        float pull = 0f;
        float blast = 0f;
        if (m.launch >= 0f) {
            float p = m.launch;
            float strike = Mth.clamp(p / 0.3f, 0f, 1f);
            strike = strike * strike * (3f - 2f * strike);
            blast = p < 0.3f ? 1f : 1f - (p - 0.3f) / 0.7f;
            stroke += blast * Mth.lerp(strike, 0.6f, -0.05f - stroke);
            sweep += blast * Mth.lerp(strike, 0.25f, -0.15f);
            pull += blast * 0.5f * Mth.sin((float) Math.PI * strike);
        }
        if (m.beating()) {
            float a = m.beatWeight * (1f - blast);
            stroke += a * TUG.at(m.beat);
            sweep += a * LIFT.at(m.beat);
            pull += a * 0.4f * TUG.lagged(m.beat, 0.08f);
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            sweep += hw * (0.15f + 0.3f * Mth.cos(ph));
            stroke += hw * 0.06f * Mth.sin(2f * ph);
            twist += hw * 0.3f * Mth.sin(ph);
            pull += hw * 0.25f * Mth.sin(ph);
        }
        float sway = flying * (0.05f * Mth.sin(time * 0.23f) + 0.03f * Mth.sin(time * 0.57f + 1f));
        stroke += sway + m.stall * 0.25f * (1f + Mth.sin(time * 1.4f));
        twist += m.stall * 0.4f * Mth.sin(time * 1.1f);
        float flap = open * flying * (1f - chute) * (0.04f + 0.08f * wind);
        float snap = flap * (Mth.sin(time * 2.7f) + 0.6f * Mth.sin(time * 4.3f + 0.4f));
        float tremble = chute * 0.02f * Mth.sin(time * 4.7f);
        stroke = Mth.lerp(chute, stroke, CHUTE_STROKE);
        sweep = Mth.lerp(chute, sweep, CHUTE_RAISE + tremble);
        twist = Mth.lerp(chute, twist, CHUTE_PITCH + 0.03f * Mth.sin(time * 5.3f));
        Membrane skin = membrane();
        if (skin != null) {
            float billow = open * (flying * (0.25f + 0.35f * wind) + 0.9f * chute + 0.3f * warm + pull) + snap * 0.6f;
            skin.billow(Mth.clamp(billow, -0.6f, 1.2f));
        }
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            shoulder[i].rotate(twist * open, s * Math.max(FLOOR, stroke - s * m.bank * 0.3f), s * (sweep * open - 1.4f * fold));
            strut[i].rotate(0f, 0f, s * 0.75f * fold);
            outer[i].rotate(0f, s * (snap * 0.6f + 0.15f * pull), s * (-0.5f * fold + tremble));
        }
        float swing = flying * (0.5f + 0.6f * wind);
        for (int k = 0; k < TAIL; k++) {
            float phase = time * (0.35f + 0.25f * wind) - k * 0.9f;
            float lateral = swing * (0.12f + 0.05f * k) * Mth.sin(phase);
            float lift = flying * (0.08f + 0.04f * k) * Mth.sin(phase * 0.7f + 0.5f) + m.w(WingMotion.GROUND) * (k == 0 ? 0.12f : 0.03f)
                    + chute * 0.15f;
            tail[k].rotate(lift, 0f, lateral - (k == 0 ? m.bank * 0.4f * flying : 0f));
        }
    }
}
