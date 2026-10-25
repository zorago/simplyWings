package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.Membrane;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class SquirrelRig extends WingRig {
    private static final int FOLD = 0;
    private static final int TAIL_SWAY = 1;
    private static final int TAIL_LIFT = 2;
    private static final float[] KEYS = Cycle.beat(0.5f, 0.3f);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.05f, 0.22f, 0.4f, 0.38f, 0.15f, -0.05f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.12f, 0.02f, 0.15f, 0.14f, 0f, -0.15f);
    private static final String[] TIPS = {"right_arm", "left_arm"};

    private PartPose body;
    private PartPose rightHand;
    private PartPose leftHand;
    private final float[] limbPose = new float[3];
    private final Bone[] arm = new Bone[2];
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
        arm[0] = bones.get("right_arm");
        arm[1] = bones.get("left_arm");
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
        float ground = m.w(WingMotion.GROUND);
        float chute = FlightRules.chute(m);
        float warm = m.w(WingMotion.CHARGE) * m.w(WingMotion.CHARGE);
        float billow = 0.3f * m.w(WingMotion.CRUISE) + 0.6f * m.w(WingMotion.LANDING) + chute + 0.25f * warm
                + 0.05f * m.w(WingMotion.CRUISE) * Mth.sin(time * 1.7f);
        if (m.beating()) {
            billow += m.beatWeight * 0.15f * Math.max(0f, -STROKE.at(m.beat));
        }
        Membrane skin = membrane();
        if (skin != null) {
            skin.billow(Mth.clamp(billow * (1f - fold), -0.6f, 1.2f));
        }
        Matrix4f inverseBody = body == null ? new Matrix4f() : matrix(body).invert();
        for (int i = 0; i < 2; i++) {
            float s = i == 0 ? 1f : -1f;
            ModelPart part = arm[i].part();
            if (part == null) {
                continue;
            }
            PartPose held = i == 0 ? rightHand : leftHand;
            if (held == null) {
                limb(m, s, limbPose);
                held = PartPose.offsetAndRotation(-s * 5f, 2f, 0f, limbPose[0], limbPose[1], limbPose[2]);
            }
            Matrix4f local = new Matrix4f(inverseBody).mul(matrix(held));
            Vector3f at = local.getTranslation(new Vector3f());
            Vector3f angles = local.getEulerAnglesZYX(new Vector3f());
            part.x = at.x;
            part.y = at.y;
            part.z = at.z;
            part.xRot = angles.x;
            part.yRot = angles.y;
            part.zRot = angles.z;
        }
        float sway = memory.get(TAIL_SWAY, m.partialTick);
        float lift = memory.get(TAIL_LIFT, m.partialTick);
        float flying = 1f - ground;
        tail.rotate(0.08f * flying + 0.9f * lift + ground * 0.3f, 0f, flying * 0.35f * sway + 0.04f * Mth.sin(time * 0.21f));
        tailTip.rotate(0.12f * flying + 0.5f * lift + ground * 0.4f, 0f, flying * (0.25f * sway + 0.06f * Mth.sin(time * 0.6f - 0.8f)));
    }

    private static Matrix4f matrix(PartPose pose) {
        return new Matrix4f().translation(pose.x, pose.y, pose.z).rotateZYX(pose.zRot, pose.yRot, pose.xRot);
    }

    @Override
    public void playerArms(ModelPart body, ModelPart right, ModelPart left) {
        this.body = body == null ? null : body.storePose();
        rightHand = right == null ? null : right.storePose();
        leftHand = left == null ? null : left.storePose();
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
