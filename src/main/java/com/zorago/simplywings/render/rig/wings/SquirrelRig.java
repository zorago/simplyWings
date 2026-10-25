package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zorago.simplywings.render.rig.Bones;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.Membrane;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.util.Mth;

public final class SquirrelRig extends WingRig {
    private static final int FOLD = 0;
    private static final int TAIL_SWAY = 1;
    private static final int TAIL_LIFT = 2;
    private static final float[] KEYS = Cycle.beat(0.5f, 0.3f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.05f, 0.22f, 0.4f, 0.38f, 0.15f, -0.05f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.12f, 0.02f, 0.15f, 0.14f, 0f, -0.15f);
    private static final float FLOOR = -5f * Mth.DEG_TO_RAD;
    private static final float CHUTE_STROKE = 0.31f;
    private static final float CHUTE_RAISE = 0.85f;
    private static final float CHUTE_PITCH = 0.48f;
    private static final String[] TIPS = {"right_spur", "left_spur"};
    private static final Cycle PULL = Cycle.of(KEYS, 0.1f, 0.2f, 0.08f, 0f, 0f, 0.03f);

    private static final int IK_STEPS = 16;
    private static final float IK_MAX_STEP = 0.25f;
    private static final float IK_EPS = 1e-3f;
    private static final float IK_DAMP = 0.02f;
    private static final float SPUR = 3f;
    private static final float ELBOW_BIAS = 0.5f;
    private static final float ELBOW_MIN = 0.05f;
    private PartPose rightHand;
    private PartPose leftHand;
    private final float[] limbPose = new float[3];
    private final Bone[] shoulder = new Bone[2];
    private final Bone[] arm = new Bone[2];
    private final Bone[] forearm = new Bone[2];
    private final Bone[] spur = new Bone[2];
    private Bone tail;
    private Bone tailTip;

    public SquirrelRig() {
        super("squirrel");
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
            spur[i] = bones.get(side[i] + "spur");
        }
        tail = bones.get("tail");
        tailTip = bones.get("tail_tip");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        float fold = m.w(WingMotion.GROUND) * (1f - m.w(WingMotion.CHARGE)) + 0.45f * m.w(WingMotion.TUCK) + 0.35f * m.w(WingMotion.AIR)
                + 0.3f * m.diveDepth * m.w(WingMotion.DIVE);
        memory.spring(FOLD, Mth.clamp(fold, 0f, 1f), 0.18f, 0.68f);
        memory.spring(TAIL_SWAY, Mth.clamp(-m.bank * 2f, -1f, 1f), 0.1f, 0.78f);
        float lift = m.w(WingMotion.LANDING) + m.w(WingMotion.BRAKE) + 0.5f * m.stall;
        memory.spring(TAIL_LIFT, Mth.clamp(lift, 0f, 1f), 0.14f, 0.7f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float time = m.time;
        float fold = Mth.clamp(memory.get(FOLD, m.partialTick), 0f, 1f);
        float open = 1f - fold;
        float ground = m.w(WingMotion.GROUND);
        float charge = m.w(WingMotion.CHARGE);
        float brake = m.w(WingMotion.BRAKE);
        float chute = FlightRules.chute(m);
        float slowBrake = brake - chute;
        float hover = m.w(WingMotion.HOVER);
        float warm = charge * charge;
        float blast = 0f;
        if (m.launch >= 0f) {
            float lp = m.launch;
            float strike = Mth.clamp(lp / 0.3f, 0f, 1f);
            blast = (lp < 0.3f ? 1f : 1f - (lp - 0.3f) / 0.7f) * (1f - 2f * strike);
        }
        float stroke = 0.08f * m.w(WingMotion.CRUISE) + 0.45f * m.w(WingMotion.LANDING) + 0.25f * slowBrake
                + 0.25f * hover + 0.35f * charge + 0.05f * ground + 0.5f * Math.max(0f, blast) + 0.03f * warm * Mth.sin(time * 2.9f);
        float twist = -0.55f * m.w(WingMotion.LANDING) + hover * FlightRules.HOVER_PLANE;
        float sweep = 0.2f * m.w(WingMotion.LANDING) - 0.25f * m.w(WingMotion.DIVE) + 0.6f * charge + 0.12f * hover
                + 0.03f * warm * Mth.sin(time * 3.7f);
        float billow = 0.3f * m.w(WingMotion.CRUISE) + 0.6f * m.w(WingMotion.LANDING) + 1f * chute + 0.25f * warm;
        float pull = 0f;
        if (m.beating()) {
            float t = m.beat;
            stroke += m.beatWeight * STROKE.at(t);
            sweep += m.beatWeight * SWEEP.at(t);
            pull += m.beatWeight * PULL.at(t);
            billow += m.beatWeight * 0.15f * Math.max(0f, -STROKE.at(t));
        }
        float hw = m.hoverBeat;
        if (hw > 0.001f) {
            float ph = m.hoverPhase;
            sweep += hw * (0.12f + 0.3f * Mth.cos(ph));
            twist += hw * 0.4f * Mth.sin(ph);
            pull += hw * 0.15f * Math.max(0f, -Mth.sin(ph));
        }
        float sw = m.stall;
        if (sw > 0.001f) {
            stroke += sw * (0.2f + 0.25f * Mth.sin(time * 1.5f));
            twist += sw * 0.4f * Mth.sin(time * 1.1f);
        }
        float glide = m.w(WingMotion.CRUISE);
        billow += glide * 0.05f * Mth.sin(time * 1.7f);
        float bank = m.bank;
        stroke = Mth.lerp(chute, stroke, CHUTE_STROKE);
        sweep = Mth.lerp(chute, sweep, CHUTE_RAISE) + chute * 0.02f * Mth.sin(time * 4.7f);
        twist = Mth.lerp(chute, twist, CHUTE_PITCH);
        Membrane skin = membrane();
        if (skin != null) {
            skin.billow(Mth.clamp(billow * (1f - fold), -0.6f, 1.2f));
        }
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            float inside = Math.max(0f, s * bank) * 1.2f;
            float armFold = -1.05f * fold + 0.25f * pull + 0.2f * inside;
            shoulder[i].rotate(twist - 0.25f * inside, s * Math.max(FLOOR, stroke - s * bank * 0.35f), s * sweep);
            arm[i].rotate(0f, 0f, s * armFold);
            forearm[i].rotate(0f, s * -0.1f * inside, s * (2.05f * fold + 0.15f * pull));
            spur[i].rotate(0f, 0f, s * (0.55f * open * (glide + m.w(WingMotion.LANDING)) - 0.9f * fold));
        }
        float sway = memory.get(TAIL_SWAY, m.partialTick);
        float lift = memory.get(TAIL_LIFT, m.partialTick);
        float flying = 1f - ground;
        tail.rotate(0.3f * flying + 0.9f * lift + ground * 0.3f, 0f, flying * 0.35f * sway + 0.04f * Mth.sin(time * 0.21f));
        tailTip.rotate(0.35f * flying + 0.5f * lift + ground * 0.4f, 0f, flying * (0.25f * sway + 0.06f * Mth.sin(time * 0.6f - 0.8f)));

        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            PartPose held = i == 0 ? rightHand : leftHand;
            Vector3f hand = new Vector3f(-s * 1f, 10f, 0f);
            if (held != null) {
                new Matrix4f().rotationZYX(held.zRot, held.yRot, held.xRot).transformPosition(hand);
                hand.add(held.x, held.y, held.z).div(16f);
            } else {
                limb(m, s, limbPose);
                new Matrix4f().rotationZYX(limbPose[2], limbPose[1], limbPose[0]).transformPosition(hand);
                hand.add(-s * 5f, 2f, 0f).div(16f);
            }
            reach(i, s, hand, 1f);
        }
    }

    @Override
    public void playerArms(ModelPart right, ModelPart left) {
        rightHand = right == null ? null : right.storePose();
        leftHand = left == null ? null : left.storePose();
    }

    private void reach(int i, float s, Vector3f target, float weight) {
        ModelPart[] chain = {root(), shoulder[i].part(), arm[i].part(), forearm[i].part(), spur[i].part()};
        if (chain[1] == null || chain[3] == null || chain[4] == null) {
            return;
        }
        float baseY = chain[1].yRot;
        float baseZ = chain[1].zRot;
        float baseE = chain[3].zRot;
        float[] q = {0f, 0f, s * ELBOW_BIAS};
        Vector3f tip = new Vector3f();
        for (int it = 0; it < IK_STEPS; it++) {
            tip(chain, s, baseY, baseZ, baseE, q, tip);
            Vector3f r = new Vector3f(tip).sub(target);
            if (r.lengthSquared() < 1e-8f) {
                break;
            }
            Matrix3f j = new Matrix3f();
            for (int k = 0; k < 3; k++) {
                q[k] += IK_EPS;
                Vector3f moved = new Vector3f();
                tip(chain, s, baseY, baseZ, baseE, q, moved);
                q[k] -= IK_EPS;
                moved.sub(tip).div(IK_EPS);
                j.setColumn(k, moved);
            }
            Matrix3f jt = new Matrix3f(j).transpose();
            Matrix3f normal = new Matrix3f(jt).mul(j);
            normal.m00 += IK_DAMP;
            normal.m11 += IK_DAMP;
            normal.m22 += IK_DAMP;
            Vector3f step = jt.transform(new Vector3f(r)).negate();
            normal.invert().transform(step);
            float len = step.length();
            if (len > IK_MAX_STEP) {
                step.mul(IK_MAX_STEP / len);
            }
            q[0] = Mth.clamp(q[0] + step.x, -3f, 3f);
            q[1] = Mth.clamp(q[1] + step.y, -3f, 3f);
            q[2] = Mth.clamp(q[2] + step.z, -2.6f, 2.6f);
            if (s * (baseE + q[2]) < ELBOW_MIN) {
                q[2] = s * ELBOW_MIN - baseE;
            }
        }
        chain[1].yRot = baseY + weight * q[0];
        chain[1].zRot = baseZ + weight * q[1];
        chain[3].zRot = baseE + weight * q[2];
    }

    private static void tip(ModelPart[] chain, float s, float baseY, float baseZ, float baseE, float[] q, Vector3f out) {
        chain[1].yRot = baseY + q[0];
        chain[1].zRot = baseZ + q[1];
        chain[3].zRot = baseE + q[2];
        PoseStack stack = new PoseStack();
        for (ModelPart part : chain) {
            part.translateAndRotate(stack);
        }
        stack.last().pose().transformPosition(-s * SPUR / 16f, 0f, 0f, out);
    }

    private void limb(WingMotion m, float s, float[] out) {
        float cruise = m.w(WingMotion.CRUISE);
        float hover = m.w(WingMotion.HOVER);
        float chute = FlightRules.chute(m);
        float slow = m.w(WingMotion.BRAKE) - chute;
        float dive = m.w(WingMotion.DIVE);
        float tuck = m.w(WingMotion.TUCK);
        float air = m.w(WingMotion.AIR) + m.w(WingMotion.LANDING);
        float charge = m.w(WingMotion.CHARGE);
        float total = Math.max(0.001f, cruise + hover + chute + slow + dive + tuck + air + charge);
        float spread = (1.45f * cruise + 1.3f * hover + 1.35f * chute + 1.2f * slow + Mth.lerp(m.diveDepth, 0.9f, 0.45f) * dive
                + 0.3f * tuck + 1.1f * air + 1.25f * charge) / total;
        float pitch = (-0.1f * cruise - 0.2f * hover - 0.7f * chute - 0.35f * slow + 0.25f * dive + 0.3f * tuck - 0.45f * air
                - 0.35f * charge) / total;
        spread += charge * charge * 0.04f * Mth.sin(m.time * 3.3f);
        if (m.beating()) {
            spread += m.beatWeight * 0.6f * (STROKE.at(m.beat) - 0.2f);
            pitch += m.beatWeight * 0.25f * SWEEP.at(m.beat);
        }
        if (m.hoverBeat > 0.001f) {
            spread += m.hoverBeat * 0.15f * Mth.cos(m.hoverPhase);
            pitch += m.hoverBeat * 0.2f * Mth.sin(m.hoverPhase);
        }
        if (m.launch >= 0f) {
            spread = Mth.lerp(Mth.clamp(m.launch * 2f, 0f, 1f), 0.4f, spread);
        }
        float inside = Math.max(0f, s * m.bank) * 1.2f;
        spread -= 0.6f * inside;
        spread += chute * 0.03f * Mth.sin(m.time * 4.7f);
        out[0] = pitch;
        out[1] = s * 0.05f;
        out[2] = s * spread;
    }

    @Override
    public void poseBody(PlayerModel<?> model, WingMotion m) {
        float w = Mth.clamp(1f - m.w(WingMotion.GROUND), 0f, 1f);
        if (w <= 0.001f) {
            return;
        }
        limb(m, 1f, limbPose);
        blend(model.rightArm, limbPose, w);
        limb(m, -1f, limbPose);
        blend(model.leftArm, limbPose, w);
        float legs = 0.28f * w * (1f - m.w(WingMotion.TUCK));
        model.rightLeg.zRot = Mth.lerp(w, model.rightLeg.zRot, legs);
        model.leftLeg.zRot = Mth.lerp(w, model.leftLeg.zRot, -legs);
    }

    private static void blend(ModelPart part, float[] target, float w) {
        part.xRot = Mth.lerp(w, part.xRot, target[0]);
        part.yRot = Mth.lerp(w, part.yRot, target[1]);
        part.zRot = Mth.lerp(w, part.zRot, target[2]);
    }
}
