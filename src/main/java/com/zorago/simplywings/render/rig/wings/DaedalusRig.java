package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class DaedalusRig extends WingRig {
    private static final int IDLE = 0;
    private static final int FOLD = 1;
    private static final int THRUST = 2;
    private static final int[] IDLES = {50, 64, 56};
    private static final float BANK_ROLL = 0.35f;
    private static final float BANK_PITCH = 0.45f;
    private static final float BANK_SWEEP = 0.2f;
    private static final float FOLDED_SPLAY = 0.4f;
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/daedalus_glow.png");

    private final RigPose drive = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private Bone pack;
    private Bone thrusterLeft;
    private Bone thrusterRight;
    private Bone flameLeft;
    private Bone flameRight;

    public DaedalusRig() {
        super("daedalus");
    }

    @Override
    public ResourceLocation glow() {
        return GLOW;
    }

    @Override
    public int[] idleLengths() {
        return IDLES;
    }

    @Override
    public String[] tipBones() {
        return TIPS;
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.85f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.85f);
        pack = bones.get("pack");
        thrusterLeft = bones.get("thruster_left");
        thrusterRight = bones.get("thruster_right");
        flameLeft = bones.get("flame_left");
        flameRight = bones.get("flame_right");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.2f, 0.65f);
        float chute = FlightRules.chute(m);
        float open = m.w(WingMotion.CRUISE) + m.w(WingMotion.DIVE) + chute;
        boolean check = m.rouse > 0f && m.rouseVariant % 3 == 1;
        if (check) {
            open = Math.max(open, 0.55f * Mth.sin((float) Math.PI * m.rouseProgress));
        }
        memory.spring(FOLD, 1f - Mth.clamp(open, 0f, 1f), 0.14f, 0.68f);
        float launch = m.launch >= 0f ? 1f : 0f;
        float thrust = Math.max(Math.max(m.buzz, m.w(WingMotion.HOVER)), Math.max(m.w(WingMotion.CHARGE), launch));
        thrust = Math.max(thrust, 0.15f * (1f - m.w(WingMotion.GROUND)));
        memory.spring(THRUST, thrust, 0.3f, 0.6f);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float fold = Mth.clamp(memory.get(FOLD, pt), 0f, 1f);
        float thrust = Mth.clamp(memory.get(THRUST, pt), 0f, 1.2f);
        float chute = FlightRules.chute(m);
        float charge = m.w(WingMotion.CHARGE);
        float brake = m.w(WingMotion.BRAKE);
        float hover = m.w(WingMotion.HOVER);
        int variant = m.rouseVariant % 3;
        float env = idle * Mth.sin((float) Math.PI * m.rouseProgress);
        float sputter = variant == 0 ? env * Math.max(0f, Mth.sin(time * 1.7f)) : 0f;
        float nozzleTest = variant == 2 ? env : 0f;
        float blast = 0f;
        if (m.launch >= 0f) {
            float lp = m.launch;
            blast = lp < 0.15f ? lp / 0.15f : (1f - lp) * (1f - lp) / 0.7225f;
        }
        float glide = 1f - chute;
        float bank = m.bank * (1f - fold) * glide;

        drive.zero();
        drive.set(RigPose.FOLD, fold);
        drive.set(RigPose.STROKE, Mth.lerp(chute, 0.1f, -0.525f));
        drive.set(RigPose.SWEEP, Mth.lerp(chute, -0.05f, 0.704f) + FOLDED_SPLAY * fold);
        drive.set(RigPose.TWIST, -0.2f * chute + 0.02f * chute * Mth.sin(time * 5.3f));
        drive.set(RigPose.ELBOW, 0.26f * chute);
        drive.set(RigPose.WRIST, 0.346f * chute);
        drive.set(RigPose.BEND_ELBOW, -0.349f * chute);
        drive.set(RigPose.BEND_WRIST, -0.218f * chute);
        drive.set(RigPose.BEND_TIPS, 0.08f * glide - 0.1f * chute + 0.04f * chute * Mth.sin(time * 4.7f));
        drive.set(RigPose.FAN, 1.1f);
        drive.set(RigPose.ASYM, BANK_ROLL * bank);
        drive.set(RigPose.CAMBER, (1f - fold) * (-0.1f * glide + 0.35f * chute));
        FeatherWing.TipCurve tips = (k, n) -> Math.max(0.4f, 1.1f - 0.1f * k);
        right.apply(drive, tips);
        left.apply(drive, tips);
        FlightRules.chuteArm(right.arm, 1f, chute);
        FlightRules.chuteArm(left.arm, -1f, chute);
        right.shoulder.rotate(BANK_PITCH * bank, 0f, -BANK_SWEEP * bank);
        left.shoulder.rotate(-BANK_PITCH * bank, 0f, -BANK_SWEEP * bank);

        float hum = thrust * 0.012f * Mth.sin(time * 9.1f) + charge * charge * 0.035f * Mth.sin(time * 7.3f)
                + blast * 0.03f * Mth.sin(time * 11.1f);
        float pitchVector = -0.35f * Mth.clamp(m.climb / 15f, -1f, 1f) * (1f - hover) + 0.8f * brake - 0.7f * blast
                - 0.5f * m.w(WingMotion.CRUISE);
        float diff = m.bank * 0.5f;
        float circle = nozzleTest * 0.5f;
        thrusterRight.rotate(pitchVector + hum + circle * Mth.sin(time * 0.4f), 0f, diff + circle * Mth.cos(time * 0.4f));
        thrusterLeft.rotate(pitchVector + hum + circle * Mth.sin(time * 0.4f + 1f), 0f, -diff + circle * Mth.cos(time * 0.4f + 1f));
        float flicker = 1f + 0.12f * Mth.sin(time * 7.7f) + 0.08f * Mth.sin(time * 13.3f);
        float jet = Math.max(0f, thrust * flicker + 0.6f * sputter) * (1f + 1.6f * blast);
        float jetR = jet * (1f + 0.25f * Mth.clamp(-diff, 0f, 1f));
        float jetL = jet * (1f + 0.25f * Mth.clamp(diff, 0f, 1f));
        flameRight.visible(jetR > 0.04f).scale(0.7f + 0.3f * Math.min(1f, jetR), Math.max(0.05f, jetR), 0.7f + 0.3f * Math.min(1f, jetR));
        flameLeft.visible(jetL > 0.04f).scale(0.7f + 0.3f * Math.min(1f, jetL), Math.max(0.05f, jetL), 0.7f + 0.3f * Math.min(1f, jetL));
        pack.rotate(hum * 0.5f, 0f, 0f);
    }
}
