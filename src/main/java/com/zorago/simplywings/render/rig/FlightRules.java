package com.zorago.simplywings.render.rig;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import net.minecraft.util.Mth;

public final class FlightRules {
    public static final float HOVER_PLANE = 80f * Mth.DEG_TO_RAD;
    private static final float BLAST_STRIKE = 0.3f;
    private static final float BRAKE_BEAT = Mth.TWO_PI * (4f / 3f) / 20f;
    private static final float CHUTE_ARM_X = 0.679f;
    private static final float CHUTE_ARM_Y = 0.831f;
    private static final float CHUTE_ARM_Z = 0.149f;

    public record Beat(Cycle stroke, Cycle sweep, Cycle elbow, Cycle wrist, Cycle twist, Cycle fan, Cycle handTwist,
                       Cycle bendElbow, Cycle bendWrist) {
    }

    public static float chute(WingMotion m) {
        return m.w(WingMotion.BRAKE) * (1f - Mth.clamp(m.brakeBeat, 0f, 1f));
    }

    public static void chuteArm(Bone arm, float sign, float chute) {
        arm.rotate(chute * CHUTE_ARM_X, sign * chute * CHUTE_ARM_Y, sign * chute * CHUTE_ARM_Z);
    }

    public static void apply(WingMotion m, RigPose drive, Beat beat) {
        if (m.stall > 0.001f) {
            drive.set(RigPose.FOLD, Mth.lerp(m.stall, drive.get(RigPose.FOLD), 0.12f));
            drive.set(RigPose.STROKE, Mth.lerp(m.stall, drive.get(RigPose.STROKE), 0.4f));
            drive.set(RigPose.FAN, Mth.lerp(m.stall, drive.get(RigPose.FAN), 1.2f));
        }

        float time = m.time;
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            float a = 0.8f;
            float s = Mth.sin(ph);
            float c = Mth.cos(ph);
            float forward = Math.max(0f, s);
            float back = Math.max(0f, -s);
            drive.add(RigPose.SWEEP, hw * (0.2f + 0.42f * c));
            drive.add(RigPose.STROKE, hw * 0.12f * Mth.sin(2f * ph));
            drive.add(RigPose.TWIST, hw * 0.5f * s);
            drive.add(RigPose.ELBOW, hw * 0.35f * back);
            drive.add(RigPose.WRIST, hw * 0.5f * back);
            drive.add(RigPose.HAND_TWIST, hw * -0.55f * back);
            drive.add(RigPose.FAN, hw * (0.2f * forward - 0.2f * back));
            drive.add(RigPose.BEND_ELBOW, hw * 0.3f * Mth.cos(ph - 0.35f));
            drive.add(RigPose.BEND_WRIST, hw * 0.4f * Mth.cos(ph - 0.7f));
            drive.add(RigPose.BEND_TIPS, hw * a * 0.5f * (Mth.cos(ph - 1.0f) - c));
        }
        float charge = m.w(WingMotion.CHARGE);
        float shiver = charge * charge;
        drive.add(RigPose.STROKE, shiver * 0.035f * Mth.sin(time * 2.9f));
        drive.add(RigPose.SWEEP, shiver * 0.03f * Mth.sin(time * 3.7f + 1f));
        drive.add(RigPose.BEND_TIPS, shiver * 0.07f * Mth.sin(time * 4.3f));
        drive.add(RigPose.FAN, shiver * (0.05f * Mth.sin(time * 5.1f) + 0.1f * Mth.sin(time * 0.35f)));
        float blast = 0f;
        if (m.launch >= 0f) {
            float p = m.launch;
            float strike = Mth.clamp(p / BLAST_STRIKE, 0f, 1f);
            strike = strike * strike * (3f - 2f * strike);
            blast = p < BLAST_STRIKE ? 1f : 1f - smooth((p - BLAST_STRIKE) / (1f - BLAST_STRIKE));
            float settle = p < BLAST_STRIKE ? 0f : Mth.sin((float) Math.PI * (p - BLAST_STRIKE) / (1f - BLAST_STRIKE));
            drive.add(RigPose.STROKE, blast * Mth.lerp(strike, 1.1f, -0.15f));
            drive.add(RigPose.SWEEP, blast * Mth.lerp(strike, 0.55f, -0.15f));
            drive.add(RigPose.FAN, blast * 0.25f);
            drive.add(RigPose.TWIST, blast * Mth.lerp(strike, 0.15f, -0.25f));
            drive.add(RigPose.ELBOW, 0.45f * settle);
            drive.add(RigPose.WRIST, 0.55f * settle);
            drive.add(RigPose.BEND_ELBOW, blast * (0.35f * Mth.sin((float) Math.PI * strike) - 0.75f * strike));
            drive.add(RigPose.BEND_WRIST, blast * (0.45f * Mth.sin((float) Math.PI * strike) - 0.9f * strike));
            drive.add(RigPose.BEND_TIPS, blast * 0.75f * Mth.sin((float) Math.PI * strike));
        }
        if (m.beating()) {
            float t = m.beat;
            float a = m.beatWeight * (1f - blast);
            drive.add(RigPose.STROKE, a * beat.stroke().at(t));
            drive.add(RigPose.SWEEP, a * beat.sweep().at(t));
            drive.add(RigPose.ELBOW, a * beat.elbow().at(t));
            drive.add(RigPose.WRIST, a * beat.wrist().at(t));
            drive.add(RigPose.TWIST, a * beat.twist().at(t));
            drive.add(RigPose.FAN, a * beat.fan().at(t));
            drive.add(RigPose.HAND_TWIST, a * beat.handTwist().at(t));
            drive.add(RigPose.BEND_ELBOW, a * beat.bendElbow().at(t));
            drive.add(RigPose.BEND_WRIST, a * beat.bendWrist().at(t));
            drive.add(RigPose.BEND_TIPS, a * 0.25f * beat.bendWrist().lagged(t, 0.06f));
            drive.add(RigPose.CAMBER, a * 1.2f * beat.stroke().lagged(t, 0.06f));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            drive.add(RigPose.STROKE, sw * (0.25f + 0.3f * Mth.sin(time * 1.1f)));
            drive.add(RigPose.BEND_ELBOW, sw * 0.35f * Mth.sin(time * 1.1f - 0.6f));
            drive.add(RigPose.ELBOW, sw * 0.45f * (0.5f + 0.5f * Mth.sin(time * 1.7f)));
            drive.add(RigPose.TWIST, sw * 0.5f * Mth.sin(time * 0.9f));
            drive.add(RigPose.ASYM, sw * 0.35f * Mth.sin(time * 0.6f));
            drive.add(RigPose.FAN, sw * 0.1f);
        }
        float ground = m.w(WingMotion.GROUND);
        drive.add(RigPose.FOLD, ground * -0.025f * (1f + Mth.sin(time * 0.08f)));
        drive.add(RigPose.STROKE, ground * 0.02f * Mth.sin(time * 0.08f + 0.5f));
        if (m.rouse > 0f) {
            float shake = Mth.sin(time * 2.2f) * 0.06f * m.rouse;
            drive.add(RigPose.SWEEP, shake);
            drive.add(RigPose.BEND_TIPS, shake * 1.5f);
            drive.add(RigPose.FAN, 0.2f * m.rouse);
        }
        float glide = m.w(WingMotion.CRUISE);
        drive.add(RigPose.BEND_TIPS, glide * 0.05f * (Mth.sin(time * 0.37f) + Mth.sin(time * 0.23f)));
        drive.add(RigPose.STROKE, glide * 0.02f * Mth.sin(time * 0.31f));
        float brake = m.w(WingMotion.BRAKE);
        float slow = Mth.clamp(m.brakeBeat, 0f, 1f);
        float chute = brake * (1f - slow);
        drive.add(RigPose.STROKE, chute * -0.175f);
        drive.add(RigPose.SWEEP, chute * 0.104f);
        drive.add(RigPose.ELBOW, chute * 0.26f);
        drive.add(RigPose.WRIST, chute * 0.346f);
        drive.add(RigPose.BEND_ELBOW, chute * -0.349f);
        drive.add(RigPose.BEND_WRIST, chute * -0.218f);
        drive.add(RigPose.BEND_TIPS, chute * (-0.1f + 0.05f * Mth.sin(time * 4.7f) + 0.03f * Mth.sin(time * 7.3f)));
        drive.add(RigPose.FAN, chute * 0.03f * Mth.sin(time * 5.3f));
        brake *= slow;
        drive.add(RigPose.STROKE, brake * 0.6f);
        drive.add(RigPose.SWEEP, brake * -0.25f);
        float push = time * BRAKE_BEAT;
        float down = Mth.sin(push);
        float recover = Math.max(0f, -down);
        drive.add(RigPose.STROKE, brake * 0.25f * Mth.cos(push));
        drive.add(RigPose.SWEEP, brake * -0.12f * down);
        drive.add(RigPose.TWIST, brake * -0.2f * Math.max(0f, down));
        drive.add(RigPose.ELBOW, brake * 0.3f * recover);
        drive.add(RigPose.WRIST, brake * 0.4f * recover);
        drive.add(RigPose.BEND_TIPS, brake * 0.25f * Mth.sin(push - 0.6f));

        float airborne = Mth.clamp(1f - m.w(WingMotion.GROUND) - m.w(WingMotion.CHARGE), 0f, 1f);
        drive.add(RigPose.CAMBER, -0.15f * airborne * (1f - drive.get(RigPose.FOLD)));
        drive.add(RigPose.CAMBER, chute * (0.35f + 0.06f * Mth.sin(time * 5.9f)));
        if (hw > 0.001f) {
            drive.add(RigPose.CAMBER, hw * 0.3f * Mth.sin(m.hoverPhase));
        }

        drive.set(RigPose.FOLD, Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f));
        drive.set(RigPose.FAN, Math.max(0f, drive.get(RigPose.FAN)));
    }

    private static float smooth(float x) {
        x = Mth.clamp(x, 0f, 1f);
        return x * x * (3f - 2f * x);
    }

    private FlightRules() {
    }
}
