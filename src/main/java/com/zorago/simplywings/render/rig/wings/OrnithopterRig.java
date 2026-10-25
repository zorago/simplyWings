package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.Membrane;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class OrnithopterRig extends WingRig {
    private static final int FOLD = 0;
    private static final float FLOOR = -5f * Mth.DEG_TO_RAD;
    private static final String[] TIPS = {"right_hand", "left_hand"};

    private final Bone[] shoulder = new Bone[2];
    private final Bone[] arm = new Bone[2];
    private final Bone[] forearm = new Bone[2];
    private final Bone[] hand = new Bone[2];
    private Bone pulley;

    public OrnithopterRig() {
        super("ornithopter");
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
            arm[i] = bones.get(side[i] + "arm");
            forearm[i] = bones.get(side[i] + "forearm");
            hand[i] = bones.get(side[i] + "hand");
        }
        pulley = bones.get("pulley");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        float fold = m.w(WingMotion.GROUND) * (1f - m.w(WingMotion.CHARGE)) + 0.5f * m.w(WingMotion.TUCK) + 0.3f * m.w(WingMotion.AIR)
                + 0.35f * m.diveDepth * m.w(WingMotion.DIVE);
        memory.ease(FOLD, Mth.clamp(fold, 0f, 1f), 0.12f);
    }

    private static float crank(float t) {
        return -Mth.cos(t * Mth.TWO_PI);
    }

    private static float stop(float v, float limit) {
        return Mth.clamp(v, -limit, limit);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float time = m.time;
        float fold = Mth.clamp(memory.get(FOLD, m.partialTick), 0f, 1f);
        float open = 1f - fold;
        float charge = m.w(WingMotion.CHARGE);
        float hover = m.w(WingMotion.HOVER);
        float chute = FlightRules.chute(m);
        float slowBrake = m.w(WingMotion.BRAKE) - chute;
        float warm = charge * charge;
        float stroke = 0.1f * m.w(WingMotion.CRUISE) + 0.4f * m.w(WingMotion.LANDING) + 0.3f * slowBrake
                + 0.25f * hover + 0.35f * charge + 0.05f * fold;
        float sweep = 0.2f * m.w(WingMotion.LANDING) + 0.15f * slowBrake - 0.35f * m.w(WingMotion.DIVE) + 0.6f * charge
                + 0.12f * hover;
        float twist = -0.25f * m.w(WingMotion.LANDING) + hover * FlightRules.HOVER_PLANE;
        float hinge = 0f;
        float crankAngle = 0f;
        float camber = 0f;
        crankAngle += warm * 0.6f * Mth.sin(time * 1.3f);
        stroke += warm * 0.035f * Math.signum(Mth.sin(time * 2.9f));
        hinge += warm * 0.06f * Mth.sin(time * 4.3f);
        float blast = 0f;
        if (m.launch >= 0f) {
            float p = m.launch;
            float strike = Mth.clamp(p / 0.3f, 0f, 1f);
            strike = strike * strike * (3f - 2f * strike);
            blast = p < 0.3f ? 1f : 1f - (p - 0.3f) / 0.7f;
            stroke += blast * Mth.lerp(strike, 1f, -0.05f - stroke);
            sweep += blast * Mth.lerp(strike, 0.3f, -0.15f);
            hinge += blast * stop(1.2f * Mth.sin((float) Math.PI * strike), 0.38f);
            crankAngle += blast * Mth.lerp(strike, 1f, -1f);
            camber += blast * 0.4f * Mth.sin((float) Math.PI * strike);
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight * (1f - blast);
            float c = crank(t);
            stroke += a * (0.45f + 0.5f * c);
            sweep += a * 0.2f * Mth.sin(t * Mth.TWO_PI);
            twist += a * 0.18f * Mth.sin(t * Mth.TWO_PI);
            hinge += a * stop(1.4f * (crank(t - 0.12f) - c), 0.38f);
            crankAngle += a * c;
            camber += a * 0.5f * (crank(t - 0.06f) - c);
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float c = -Mth.cos(ph);
            sweep += hw * (0.2f + 0.42f * c);
            stroke += hw * 0.08f * Mth.sin(2f * ph);
            twist += hw * 0.3f * Mth.sin(ph);
            hinge += hw * stop(1.2f * (-Mth.cos(ph - 0.7f) - c), 0.35f);
            crankAngle += hw * c;
            camber += hw * 0.3f * Mth.sin(ph);
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            stroke += sw * 0.25f * (1f + Mth.sin(time * 1.2f));
            hinge += sw * stop(0.5f * Mth.sin(time * 2.1f), 0.3f);
        }
        float glide = m.w(WingMotion.CRUISE);
        float creak = glide * 0.015f * Math.signum(Mth.sin(time * 0.31f)) + m.w(WingMotion.GROUND) * 0.01f * Math.signum(Mth.sin(time * 0.13f));
        stroke += creak;
        float rattle = chute * 0.02f * Math.signum(Mth.sin(time * 4.7f));
        stroke = Mth.lerp(chute, Math.max(FLOOR, stroke), -0.525f);
        sweep = Mth.lerp(chute, sweep, 0.704f + rattle);
        twist = Mth.lerp(chute, twist, -0.2f);
        hinge = Mth.lerp(chute, hinge, -0.218f + rattle);
        float elbow = chute * 0.26f;
        float wrist = chute * 0.346f;
        float bendElbow = -0.349f * chute;
        float airborne = Mth.clamp(1f - m.w(WingMotion.GROUND) - charge, 0f, 1f);
        Membrane skin = membrane();
        if (skin != null) {
            skin.billow(Mth.clamp(open * (0.35f * airborne + 0.9f * chute + 0.2f * warm + camber), -0.6f, 1.1f));
        }
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            shoulder[i].rotate(twist * open, s * stroke - m.bank * 0.25f * (1f - chute), s * sweep * open);
            arm[i].rotate(0f, 0f, s * -1.25f * fold).scale(1f, Mth.lerp(fold, 1f, 0.55f), 1f);
            forearm[i].rotate(0f, s * (hinge * 0.35f + bendElbow), s * (2.45f * fold - elbow)).scale(1f, Mth.lerp(fold, 1f, 0.7f), 1f);
            hand[i].rotate(0f, s * hinge, s * (-2.2f * fold - wrist));
            FlightRules.chuteArm(arm[i], s, chute);
        }
        pulley.rotate(0f, 0f, crankAngle * 1.6f + glide * 0.1f * Mth.sin(time * 0.31f));
    }
}
