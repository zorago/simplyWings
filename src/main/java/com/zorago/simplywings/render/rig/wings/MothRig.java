package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class MothRig extends WingRig {
    private static final int SHIVER = 0;
    private static final float FLOOR = -5f * Mth.DEG_TO_RAD;
    private static final float[] KEYS = Cycle.beat(0.5f, 0.3f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.05f, 0.6f, 1.12f, 1.1f, 0.4f, -0.05f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.35f, 0.15f, 0f, -0.3f, -0.1f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.2f, 0.05f, 0.22f, 0.2f, 0f, -0.22f);
    private static final float HIND_LAG = 0.08f;
    private static final float CHUTE_STROKE = 0.31f;
    private static final float CHUTE_RAISE = 0.85f;
    private static final float CHUTE_PITCH = 0.48f;
    private static final String[] TIPS = {"right_fore", "left_fore"};

    private final Bone[] fore = new Bone[2];
    private final Bone[] hind = new Bone[2];

    public MothRig() {
        super("moth");
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    protected void onBind(Bones bones) {
        fore[0] = bones.get("right_fore");
        fore[1] = bones.get("left_fore");
        hind[0] = bones.get("right_hind");
        hind[1] = bones.get("left_hind");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        boolean shivering = m.rouse > 0f || m.w(WingMotion.CHARGE) > 0.3f;
        memory.spring(SHIVER, shivering ? 1f : 0f, 0.2f, 0.6f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float time = m.time;
        float ground = m.w(WingMotion.GROUND);
        float charge = m.w(WingMotion.CHARGE);
        float hover = m.w(WingMotion.HOVER);
        float brake = m.w(WingMotion.BRAKE);
        float slow = Mth.clamp(m.brakeBeat, 0f, 1f);
        float chute = FlightRules.chute(m);
        float slowBrake = brake * slow;
        float shiver = memory.get(SHIVER, m.partialTick);
        float airborne = Mth.clamp(1f - ground - charge, 0f, 1f);

        float stroke = 0.35f * charge + 0.2f * m.w(WingMotion.AIR) + 0.3f * m.w(WingMotion.LANDING)
                + 0.25f * hover + 0.08f * m.w(WingMotion.CRUISE) + 0.3f * slowBrake - 0.03f * ground + 0.05f * m.w(WingMotion.DIVE);
        float sweep = -0.32f * ground + 0.6f * charge + 0.1f * m.w(WingMotion.LANDING) + 0.12f * hover
                - 0.45f * m.w(WingMotion.DIVE) * Mth.lerp(m.diveDepth, 0.5f, 1f) - 0.6f * m.w(WingMotion.TUCK)
                - 0.05f * m.w(WingMotion.CRUISE);
        float twist = hover * FlightRules.HOVER_PLANE - 0.2f * m.w(WingMotion.LANDING);
        float jitter = airborne * (1f - chute) * (0.06f * Mth.sin(time * 0.93f) + 0.04f * Mth.sin(time * 2.17f + 1f));
        stroke += jitter + ground * 0.015f * Mth.sin(time * 0.07f);
        twist += airborne * (1f - chute) * 0.06f * Mth.sin(time * 1.31f);
        float tremor = shiver * (0.04f + 0.06f * charge * charge) * Mth.sin(time * 3.6f);
        stroke += tremor;
        stroke += m.w(WingMotion.CRUISE) * 0.04f * Mth.sin(time * 0.45f);

        float foreStroke = stroke;
        float hindStroke = stroke * 0.9f;
        float foreTwist = twist;
        float hindTwist = twist;
        float foreSweep = sweep;
        float hindSweep = sweep * 0.7f - 0.15f * m.w(WingMotion.TUCK);
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            foreSweep += hw * (0.2f + 0.42f * Mth.cos(ph));
            hindSweep += hw * (0.2f + 0.4f * Mth.cos(ph - 0.5f));
            foreStroke += hw * 0.12f * Mth.sin(2f * ph);
            hindStroke += hw * 0.12f * Mth.sin(2f * ph - 1f);
            foreTwist += hw * 0.5f * Mth.sin(ph);
            hindTwist += hw * 0.45f * Mth.sin(ph - 0.5f);
        }
        float blast = 0f;
        if (m.launch >= 0f) {
            float p = m.launch;
            float strike = Mth.clamp(p / 0.3f, 0f, 1f);
            strike = strike * strike * (3f - 2f * strike);
            blast = p < 0.3f ? 1f : 1f - (p - 0.3f) / 0.7f;
            foreStroke += blast * Mth.lerp(strike, 1.1f, -0.05f - stroke);
            hindStroke += blast * Mth.lerp(strike, 1.0f, -0.05f - stroke);
            foreSweep += blast * Mth.lerp(strike, 0.3f, -0.25f);
            hindSweep += blast * Mth.lerp(strike, 0.25f, -0.2f);
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight * (1f - blast);
            foreStroke += a * STROKE.at(t);
            hindStroke += a * 0.92f * STROKE.at(t - HIND_LAG);
            foreTwist += a * TWIST.at(t);
            hindTwist += a * TWIST.at(t - HIND_LAG);
            foreSweep += a * SWEEP.at(t);
            hindSweep += a * 0.8f * SWEEP.at(t - HIND_LAG);
        }
        if (m.stall > 0.001f) {
            foreStroke += m.stall * (0.25f + 0.3f * Mth.sin(time * 1.2f));
            hindStroke += m.stall * (0.25f + 0.3f * Mth.sin(time * 1.2f - 0.4f));
            foreTwist += m.stall * 0.4f * Mth.sin(time * 0.9f);
        }
        if (slowBrake > 0.001f) {
            float push = time * Mth.TWO_PI * (4f / 3f) / 20f;
            foreStroke += slowBrake * 0.22f * Mth.cos(push);
            hindStroke += slowBrake * 0.2f * Mth.cos(push - 0.4f);
        }
        float tremble = chute * 0.02f * Mth.sin(time * 4.7f);
        foreStroke = Mth.lerp(chute, foreStroke, CHUTE_STROKE);
        hindStroke = Mth.lerp(chute, hindStroke, CHUTE_STROKE * 0.9f);
        foreSweep = Mth.lerp(chute, foreSweep, CHUTE_RAISE + tremble);
        hindSweep = Mth.lerp(chute, hindSweep, CHUTE_RAISE * 0.55f + tremble);
        foreTwist = Mth.lerp(chute, foreTwist, CHUTE_PITCH);
        hindTwist = Mth.lerp(chute, hindTwist, CHUTE_PITCH);
        float bank = m.bank * 0.3f;
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            fore[i].rotate(foreTwist, s * Math.max(FLOOR, foreStroke - s * bank), s * foreSweep);
            hind[i].rotate(hindTwist, s * Math.max(FLOOR, hindStroke - s * bank), s * hindSweep);
        }
    }
}
