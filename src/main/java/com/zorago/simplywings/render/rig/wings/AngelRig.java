package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.FeatherWing;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.WingRig;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class AngelRig extends WingRig {
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int ALULA = 2;
    private static final int HALO = 3;
    private static final float HALO_HEIGHT = -9.5f;
    private static final int[] IDLES = {80, 96, 84, 40, 90};
    private static final float UP = 0.62f;
    private static final float DOWN = 0.1f;
    private static final String[] TIPS = {"right_primary_1", "left_primary_1"};
    private static final float[] KEYS = Cycle.beat(UP, DOWN);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.58f, 0.92f, 0.88f, 0.32f, -0.14f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.27f, 0.24f, 0.06f, -0.63f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.24f, 0.28f, 0.12f, -0.72f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.26f, 0.05f, 0.32f, 0.3f, 0f, -0.32f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.22f, 0.6f, 0.3f, 0.05f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.4f, 0.75f, 0.35f, 0f, -0.15f, 0.1f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.3f, 0.15f, 0f, -0.32f, -0.12f);
    private static final Cycle FAN = Cycle.of(KEYS, 0.2f, -0.25f, 0.1f, 0.3f, 0.3f, 0.2f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.55f, -0.2f, 0f, 0.18f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/angel_glow.png");

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private final RigPose side = new RigPose();
    private FeatherWing right;
    private FeatherWing left;
    private Bone rightAlula;
    private Bone leftAlula;
    private Bone halo;
    private final Quaternionf bodyRot = new Quaternionf();
    private final Quaternionf headRot = new Quaternionf();
    private final Vector3f bodyPos = new Vector3f();
    private final Vector3f headPos = new Vector3f();

    public AngelRig() {
        super("angel");
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
    public void playerHead(ModelPart body, ModelPart head) {
        bodyPos.set(body.x, body.y, body.z);
        bodyRot.rotationZYX(body.zRot, body.yRot, body.xRot);
        headPos.set(head.x, head.y, head.z);
        headRot.rotationZYX(head.zRot, head.yRot, head.xRot);
    }

    @Override
    protected void onBind(Bones bones) {
        right = new FeatherWing(bones, "right_", 1f, 1.25f, 2.75f, 2.9f);
        left = new FeatherWing(bones, "left_", -1f, 1.25f, 2.75f, 2.9f);
        rightAlula = bones.get("right_alula");
        leftAlula = bones.get("left_alula");
        halo = bones.get("aureole");
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        memory.spring(IDLE, m.rouse, 0.09f, 0.8f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.18f, 0.72f);
        float slow = m.w(WingMotion.BRAKE) + m.w(WingMotion.LANDING) + m.stall + 0.6f * m.w(WingMotion.HOVER);
        memory.spring(ALULA, Math.min(1f, slow), 0.2f, 0.65f);
        boolean heaven = m.rouse > 0f && m.rouseVariant % 5 == 4;
        float speed = 0.012f + (heaven ? 0.05f * m.rouse : 0f) + 0.01f * Mth.clamp(m.speed / 40f, 0f, 1f);
        if (memory.now(HALO) > Mth.TWO_PI) {
            memory.snap(HALO, memory.now(HALO) - Mth.TWO_PI);
        }
        memory.set(HALO, memory.now(HALO) + speed);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 5;
        float p = m.rouseProgress;
        float env = idle * Mth.sin((float) Math.PI * p);
        float hold = idle * smooth(Mth.clamp(p * 3f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 3f, 0f, 1f));

        float gFold = 1f, gStroke = 0.12f, gSweep = 0f, gFan = 0f, gTwist = 0f, gTips = 0f;
        float ruffle = 0f;
        float glory = 0f;
        switch (variant) {
            case 0 -> {
                gFold = Mth.lerp(hold, 1f, 0.02f);
                gStroke = Mth.lerp(hold, 0.12f, 0.55f);
                gSweep = 0.3f * hold;
                gFan = 1.25f * hold;
                gTips = 0.35f * hold;
                glory = hold;
            }
            case 2 -> {
                gFold = Mth.lerp(hold, 1f, 0.3f);
                gStroke = Mth.lerp(hold, 0.12f, -0.08f);
                gSweep = 0.25f * hold;
                gFan = 0.9f * hold;
                gTwist = 0.3f * hold;
            }
            case 3 -> ruffle = env;
            case 4 -> {
                gFold = Mth.lerp(hold, 1f, 0.12f);
                gStroke = Mth.lerp(hold, 0.12f, 0.35f);
                gSweep = 0.55f * hold;
                gFan = 1.1f * hold;
                gTips = 0.5f * hold;
                glory = hold;
            }
            default -> {
            }
        }
        drive.blendIn(ground, state.clear().set(RigPose.FOLD, gFold).set(RigPose.STROKE, gStroke).set(RigPose.SWEEP, gSweep)
                .set(RigPose.FAN, gFan).set(RigPose.TWIST, gTwist).set(RigPose.BEND_TIPS, gTips));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.FAN, 1.3f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.12f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.3f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.18f).set(RigPose.FAN, 0.9f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.08f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 1.2f).set(RigPose.BEND_TIPS, 0.2f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.03f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1.2f).set(RigPose.TWIST, FlightRules.HOVER_PLANE)
                .set(RigPose.BEND_TIPS, 0.2f));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.1f).set(RigPose.SWEEP, 0.06f)
                .set(RigPose.FAN, 1.2f).set(RigPose.BEND_TIPS, 0.4f).set(RigPose.ASYM, m.bank * 0.3f));
        float d = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(d, 0.25f, 0.68f))
                .set(RigPose.SWEEP, Mth.lerp(d, -0.2f, -0.6f)).set(RigPose.FAN, Mth.lerp(d, 0.5f, 0f))
                .set(RigPose.STROKE, Mth.lerp(d, 0.06f, 0.18f)));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.25f).set(RigPose.BEND_TIPS, 0.1f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.85f).set(RigPose.SWEEP, -0.6f)
                .set(RigPose.STROKE, 0.15f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);
        if (settle > 0.001f) {
            drive.add(RigPose.STROKE, settle * 0.1f * Mth.sin(time * 0.9f));
            drive.add(RigPose.BEND_TIPS, settle * 0.25f * Mth.sin(time * 1.6f));
            drive.add(RigPose.FAN, settle * 0.15f * Mth.sin(time * 1.1f));
        }
        float glide = m.w(WingMotion.CRUISE) + 0.5f * m.w(WingMotion.HOVER);
        drive.add(RigPose.BEND_TIPS, glide * 0.06f * (Mth.sin(time * 0.43f) + 0.6f * Mth.sin(time * 1.1f)));
        drive.add(RigPose.FAN, glide * 0.03f * Mth.sin(time * 0.31f));
        float chute = FlightRules.chute(m);
        float landing = m.w(WingMotion.BRAKE) + m.w(WingMotion.LANDING) - chute;
        drive.add(RigPose.BEND_TIPS, landing * 0.14f * Mth.sin(time * 2.6f));

        float alula = memory.get(ALULA, pt);
        float preen = variant == 1 ? idle : 0f;
        for (int i = 0; i < 2; i++) {
            FeatherWing wing = i == 0 ? right : left;
            side.copy(drive);
            if (preen > 0.001f) {
                float local = Mth.clamp(i == 0 ? p * 2f : p * 2f - 1f, 0f, 1f);
                float w = preen * Mth.sin((float) Math.PI * local) * ground;
                side.add(RigPose.FOLD, -0.6f * w);
                side.add(RigPose.STROKE, -0.2f * w);
                side.add(RigPose.SWEEP, 0.35f * w);
                side.add(RigPose.TWIST, 0.45f * w);
                side.add(RigPose.FAN, 0.9f * w);
                side.add(RigPose.BEND_TIPS, w * 0.2f * Mth.sin(time * 1.7f));
            }
            if (ruffle > 0.001f) {
                side.add(RigPose.FAN, ruffle * 0.35f * Mth.sin(time * 2.3f + i));
                side.add(RigPose.STROKE, ruffle * 0.06f * Mth.sin(time * 3.1f + i));
                side.add(RigPose.FOLD, -ruffle * 0.15f);
            }
            float fold = Mth.clamp(side.get(RigPose.FOLD), 0f, 1f);
            side.set(RigPose.FOLD, fold);
            side.set(RigPose.FAN, Mth.clamp(side.get(RigPose.FAN), 0f, 1.25f));
            FeatherWing.TipCurve tips = (k, n) -> k < 6 ? 1.3f - 0.1f * k : 0.45f;
            wing.apply(side, tips);
            float s = wing.sign;
            FlightRules.chuteArm(wing.arm, s, chute);
            if (ruffle > 0.001f) {
                for (int k = 0; k < wing.primary.size(); k++) {
                    float wave = ruffle * 0.12f * Mth.sin(time * 2.8f - k * 0.7f + i);
                    wing.primary.get(k).rotate(0f, s * wave, s * wave * 0.5f);
                }
            }
            float a = alula * (1f - fold);
            (i == 0 ? rightAlula : leftAlula).rotate(0f, s * 0.35f * a, s * 0.55f * a);
        }
        float spin = memory.get(HALO, pt);
        float breathe = 1f + 0.04f * Mth.sin(time * 0.05f) + 0.15f * glory;
        Quaternionf toBody = new Quaternionf(bodyRot).conjugate();
        Vector3f center = headRot.transform(new Vector3f(0f, HALO_HEIGHT + 0.4f * Mth.sin(time * 0.04f), 0f)).add(headPos).sub(bodyPos);
        toBody.transform(center);
        Vector3f angles = toBody.mul(headRot).rotateX(Mth.HALF_PI).rotateZ(spin).getEulerAnglesZYX(new Vector3f());
        halo.part().x = center.x;
        halo.part().y = center.y;
        halo.part().z = center.z;
        halo.rotation(angles.x, angles.y, angles.z).scale(breathe, breathe, 1f);
    }

    private static float smooth(float x) {
        return x * x * (3f - 2f * x);
    }
}
