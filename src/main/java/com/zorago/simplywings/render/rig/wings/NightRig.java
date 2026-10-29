package com.zorago.simplywings.render.rig.wings;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.anim.Cycle;
import com.zorago.simplywings.anim.RigMemory;
import com.zorago.simplywings.anim.RigPose;
import com.zorago.simplywings.anim.WingMotion;
import com.zorago.simplywings.render.rig.Bone;
import com.zorago.simplywings.render.rig.Bones;
import com.zorago.simplywings.render.rig.DragonTail;
import com.zorago.simplywings.render.rig.DragonWing;
import com.zorago.simplywings.render.rig.FlightRules;
import com.zorago.simplywings.render.rig.Membrane;
import com.zorago.simplywings.render.rig.WingRig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class NightRig extends WingRig {
    private static final float MAIN_SCALE = 1.15f;
    private static final int IDLE = 0;
    private static final int SETTLE = 1;
    private static final int PLASMA = 2;
    private static final int SPINES = 3;
    private static final int PROSTHETIC = 4;
    private static final int TAIL = 10;
    private static final int TAIL_SEGMENTS = 8;
    private static final int[] IDLES = {70, 36, 60, 64, 50, 80, 100, 48};
    private static final float UP = 0.5f;
    private static final float DOWN = 0.2f;
    private static final float[] KEYS = Cycle.beat(UP, DOWN);
    private static final Cycle STROKE = Cycle.of(KEYS, 0.09f, 0.52f, 0.85f, 0.8f, 0.32f, -0.15f);
    private static final Cycle BEND_ELBOW = Cycle.of(KEYS, -0.44f, 0f, 0.25f, 0.22f, 0.05f, -0.63f);
    private static final Cycle BEND_WRIST = Cycle.of(KEYS, -0.61f, -0.17f, 0.2f, 0.24f, 0.1f, -0.72f);
    private static final Cycle SWEEP = Cycle.of(KEYS, -0.25f, 0.05f, 0.3f, 0.28f, 0f, -0.32f);
    private static final Cycle ELBOW = Cycle.of(KEYS, 0.3f, 0.6f, 0.2f, 0f, -0.05f, 0.05f);
    private static final Cycle WRIST = Cycle.of(KEYS, 0.5f, 0.75f, 0.25f, -0.05f, -0.1f, 0.1f);
    private static final Cycle FAN = Cycle.of(KEYS, -0.12f, -0.3f, -0.05f, 0.05f, 0.05f, 0f);
    private static final Cycle TWIST = Cycle.of(KEYS, 0f, 0.35f, 0.12f, -0.06f, -0.32f, -0.1f);
    private static final Cycle HAND_TWIST = Cycle.of(KEYS, 0f, -0.42f, -0.15f, 0f, 0.12f, 0.05f);
    private static final FlightRules.Beat BEAT = new FlightRules.Beat(STROKE, SWEEP, ELBOW, WRIST, TWIST, FAN, HAND_TWIST,
            BEND_ELBOW, BEND_WRIST);
    private static final float GLIDE_BILLOW = 0.5f;
    private static final String[] TIPS = {"right_finger_1_tip", "left_finger_1_tip"};
    private static final float[] TAIL_GROUND = {0.55f, 0.5f, 0.42f, 0.08f, 0.04f, 0.04f, 0.03f, 0.03f};
    private static final float[] TAIL_CURL = {0f, 0.02f, 0.1f, 0.24f, 0.3f, 0.34f, 0.36f, 0.38f};
    private static final float[] TAIL_HANG = {0.35f, 0.1f, 0.1f, 0.08f, 0.06f, 0.05f, 0.04f, 0.04f};
    private static final ResourceLocation GLOW = SimplyWings.id("textures/entity/night_glow.png");

    private final RigPose drive = new RigPose();
    private final RigPose state = new RigPose();
    private final RigPose side = new RigPose();
    private final RigPose hind = new RigPose();
    private final float[] tailPitch = new float[TAIL_SEGMENTS];
    private final float[] tailYaw = new float[TAIL_SEGMENTS];
    private final float[] extraPitch = new float[TAIL_SEGMENTS];
    private final float[] extraYaw = new float[TAIL_SEGMENTS];
    private final List<Bone> spines = new ArrayList<>();
    private final List<Bone> spineGlow = new ArrayList<>();
    private DragonWing[] main;
    private DragonWing[] back;
    private DragonTail tail;

    public NightRig() {
        super("night");
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
        main = new DragonWing[]{new DragonWing(bones, "right_", 1f, 1.26f, 2.74f, 2.79f),
                new DragonWing(bones, "left_", -1f, 1.26f, 2.74f, 2.79f)};
        back = new DragonWing[]{new DragonWing(bones, "right_hind_", 1f, 1.2f, 2.6f, 2.6f),
                new DragonWing(bones, "left_hind_", -1f, 1.2f, 2.6f, 2.6f)};
        tail = new DragonTail(bones, TAIL, 2, TAIL_SEGMENTS - 1);
        spines.clear();
        spineGlow.clear();
        for (int k = 1; bones.has("spine_" + k); k++) {
            spines.add(bones.get("spine_" + k));
            spineGlow.add(bones.get("spine_" + k + "_glow"));
        }
    }

    @Override
    public void tick(WingMotion m, RigMemory memory) {
        int variant = m.rouseVariant % 8;
        boolean idleCharge = m.rouse > 0f && variant == 5;
        memory.spring(IDLE, m.rouse, 0.16f, 0.7f);
        memory.spring(SETTLE, m.w(WingMotion.LANDING), 0.3f, 0.6f);
        float plasma = memory.now(PLASMA);
        if (m.w(WingMotion.BRAKE) > 0.6f || idleCharge) {
            plasma = Math.min(1f, plasma + (idleCharge ? 0.03f : 0.02f));
        } else {
            plasma = Math.max(0f, plasma - 0.06f);
        }
        memory.set(PLASMA, plasma);
        boolean bristle = m.rouse > 0f && (variant == 4 || variant == 5);
        float raise = 0.35f * m.w(WingMotion.CHARGE) + 0.3f * m.w(WingMotion.BRAKE) + 0.4f * plasma + (bristle ? 0.5f * m.rouse : 0f)
                - 0.5f * m.w(WingMotion.DIVE) - 0.6f * m.w(WingMotion.TUCK);
        memory.spring(SPINES, raise, 0.25f, 0.6f);
        boolean flick = m.rouse > 0f && variant == 7;
        float target = flick ? (Mth.sin(m.rouseProgress * Mth.PI * 6f) > 0f ? 0.9f : 0.1f) : 0.35f + 0.6f * m.w(WingMotion.BRAKE);
        memory.spring(PROSTHETIC, target, 0.55f, 0.45f);

        float ground = m.w(WingMotion.GROUND);
        float hang = m.w(WingMotion.HOVER) + m.w(WingMotion.AIR) + m.w(WingMotion.LANDING);
        float travel = m.w(WingMotion.CRUISE) + m.w(WingMotion.DIVE) + m.w(WingMotion.TUCK);
        float brake = m.w(WingMotion.BRAKE);
        float charge = m.w(WingMotion.CHARGE);
        float walk = Mth.clamp(m.speed / 4.5f, 0f, 1f) * ground;
        float time = m.time;
        for (int k = 0; k < TAIL_SEGMENTS; k++) {
            float wave = Mth.sin(time * 0.25f - k * 0.8f);
            tailPitch[k] = ground * (TAIL_GROUND[k] + 0.05f * Mth.sin(time * 0.07f - k * 0.7f) + 0.08f * walk * Mth.sin(time * 1.24f - k * 0.6f))
                    + hang * TAIL_HANG[k] + travel * (k == 0 ? 0.18f : 0.02f + 0.03f * wave)
                    + brake * (k == 0 ? 0.35f : 0f) + (m.launch >= 0f ? (k == 0 ? 0.45f : 0.12f) : 0f) + charge * (k == 0 ? 0.9f : k < 3 ? 0.3f : 0.05f);
            tailYaw[k] = ground * (TAIL_CURL[k]
                    + (1f - walk) * (0.12f * Mth.sin(time * 0.05f - k * 0.6f) + 0.05f * Mth.sin(time * 0.13f - k * 0.9f)))
                    + hang * 0.1f * Mth.sin(time * 0.1f - k * 0.5f)
                    + travel * (-m.bank * 0.28f * (1f + 0.3f * k) + 0.05f * wave)
                    + brake * 0.08f * Mth.sin(time * 0.3f - k);
        }
        tail.follow(memory, tailPitch, tailYaw, 0.32f, 0.62f);
        tail.swing(memory, walk, Mth.clamp((m.speed - 4.5f) / 1.2f, 0f, 1f) * ground);
    }

    @Override
    protected void pose(WingMotion m, RigMemory memory) {
        drive.zero();
        float pt = m.partialTick;
        float time = m.time;
        float idle = Mth.clamp(memory.get(IDLE, pt), 0f, 1f);
        float settle = memory.get(SETTLE, pt);
        float plasma = Mth.clamp(memory.get(PLASMA, pt), 0f, 1f);
        float spineRaise = memory.get(SPINES, pt);
        float prosthetic = memory.get(PROSTHETIC, pt);
        float ground = m.w(WingMotion.GROUND);
        int variant = m.rouseVariant % 8;
        float p = m.rouseProgress;
        float env = idle * Mth.sin((float) Math.PI * p);
        float hold = idle * smooth(Mth.clamp(p * 3f, 0f, 1f)) * smooth(Mth.clamp((1f - p) * 3f, 0f, 1f));
        float billow = 0f;

        float gFold = 1f, gStroke = 0.1f, gSweep = 0f, gFan = 0f, gTwist = 0f;
        float shake = 0f, wag = 0f, curl = 0f;
        switch (variant) {
            case 0 -> {
                gFold = Mth.lerp(hold, 1f, 0.15f);
                gStroke = Mth.lerp(hold, 0.1f, 0.75f);
                gSweep = 0.45f * hold;
                gFan = 1f * hold;
                gTwist = -0.15f * hold;
            }
            case 1 -> shake = env;
            case 2 -> wag = env;
            case 6 -> {
                gFold = 1f;
                gStroke = Mth.lerp(hold, 0.1f, -0.55f);
                curl = hold;
            }
            default -> {
            }
        }
        drive.blendIn(ground, state.clear().set(RigPose.FOLD, gFold).set(RigPose.STROKE, gStroke).set(RigPose.SWEEP, gSweep)
                .set(RigPose.FAN, gFan).set(RigPose.TWIST, gTwist));
        drive.blendIn(m.w(WingMotion.CHARGE), state.clear().set(RigPose.FOLD, 0f).set(RigPose.STROKE, 0.35f)
                .set(RigPose.SWEEP, 0.75f).set(RigPose.FAN, 1.1f).set(RigPose.TWIST, 0.1f).set(RigPose.BEND_TIPS, -0.15f));
        drive.blendIn(m.w(WingMotion.AIR), state.clear().set(RigPose.FOLD, 0.3f).set(RigPose.STROKE, 0.3f)
                .set(RigPose.SWEEP, 0.15f).set(RigPose.FAN, 0.85f));
        drive.blendIn(m.w(WingMotion.LANDING), state.clear().set(RigPose.FOLD, 0.15f).set(RigPose.STROKE, 0.45f)
                .set(RigPose.SWEEP, 0.3f).set(RigPose.FAN, 0.95f));
        drive.blendIn(m.w(WingMotion.HOVER), state.clear().set(RigPose.FOLD, 0.04f).set(RigPose.STROKE, 0.25f)
                .set(RigPose.SWEEP, 0.12f).set(RigPose.FAN, 1f).set(RigPose.TWIST, FlightRules.HOVER_PLANE));
        drive.blendIn(m.w(WingMotion.CRUISE), state.clear().set(RigPose.STROKE, 0.08f).set(RigPose.SWEEP, 0.03f)
                .set(RigPose.FAN, 1f).set(RigPose.BEND_TIPS, 0.15f).set(RigPose.ASYM, m.bank * 0.4f));
        float dd = m.diveDepth;
        drive.blendIn(m.w(WingMotion.DIVE), state.clear().set(RigPose.FOLD, Mth.lerp(dd, 0.25f, 0.7f))
                .set(RigPose.SWEEP, Mth.lerp(dd, -0.2f, -0.6f)).set(RigPose.FAN, Mth.lerp(dd, 0.8f, 0.2f))
                .set(RigPose.STROKE, 0.04f));
        drive.blendIn(m.w(WingMotion.BRAKE), state.clear().set(RigPose.FOLD, 0.02f).set(RigPose.STROKE, -0.35f)
                .set(RigPose.SWEEP, 0.6f).set(RigPose.TWIST, -0.2f).set(RigPose.FAN, 1.1f).set(RigPose.BEND_TIPS, 0.1f));
        drive.blendIn(m.w(WingMotion.TUCK), state.clear().set(RigPose.FOLD, 0.9f).set(RigPose.SWEEP, -0.65f)
                .set(RigPose.STROKE, 0.05f).set(RigPose.FAN, 0f));
        FlightRules.apply(m, drive, BEAT);
        if (settle > 0.001f) {
            drive.add(RigPose.STROKE, settle * 0.12f * Mth.sin(time * 1.6f));
        }
        float fold = Mth.clamp(drive.get(RigPose.FOLD), 0f, 1f);
        float airborne = Mth.clamp(1f - ground - m.w(WingMotion.CHARGE), 0f, 1f);
        float lift = airborne * (1f - fold);
        billow = GLIDE_BILLOW * lift + 2f * (drive.get(RigPose.CAMBER) + 0.15f * lift) + settle * 0.35f * Mth.sin(time * 2.3f);
        float flutter = shake * 0.35f * Mth.sin(time * 5.3f);
        billow += shake * 0.4f * Mth.sin(time * 4.1f);
        Membrane membrane = membrane();
        if (membrane != null) {
            membrane.billow(Mth.clamp(billow, -0.8f, 1.2f));
        }
        float chute = FlightRules.chute(m);

        float scratch = variant == 3 ? idle : 0f;
        for (int i = 0; i < 2; i++) {
            side.copy(drive);
            if (scratch > 0.001f) {
                float w = scratch * Mth.sin((float) Math.PI * Mth.clamp(i == 0 ? p * 2f : p * 2f - 1f, 0f, 1f)) * ground;
                side.add(RigPose.FOLD, -0.5f * w);
                side.add(RigPose.STROKE, 0.25f * w);
                side.add(RigPose.SWEEP, 0.35f * w);
                side.add(RigPose.BEND_TIPS, w * 0.25f * Mth.sin(time * 2.1f));
            }
            if (shake > 0.001f) {
                side.add(RigPose.STROKE, shake * 0.08f * Mth.sin(time * 5.7f + i));
                side.add(RigPose.FOLD, -shake * 0.2f);
            }
            side.set(RigPose.FOLD, Mth.clamp(side.get(RigPose.FOLD), 0f, 1f));
            float thumb = variant == 3 ? scratch * 0.3f * Math.max(0f, Mth.sin(time * 1.4f)) : 0f;
            main[i].apply(side, billow, flutter, thumb);
            main[i].shoulder.scale(MAIN_SCALE, MAIN_SCALE, MAIN_SCALE);
            FlightRules.chuteArm(main[i].arm, i == 0 ? 1f : -1f, chute);
            hind.copy(side);
            if (m.beating()) {
                float t = m.beat - 0.08f;
                float a = m.beatWeight;
                hind.add(RigPose.STROKE, a * 0.9f * (STROKE.at(t) - STROKE.at(m.beat)));
                hind.add(RigPose.BEND_ELBOW, a * 0.9f * (BEND_ELBOW.at(t) - BEND_ELBOW.at(m.beat)));
            }
            hind.add(RigPose.SWEEP, chute * -0.5f);
            hind.add(RigPose.SWEEP, -0.15f * (1f - ground));
            hind.set(RigPose.FOLD, Mth.clamp(hind.get(RigPose.FOLD) + 0.05f, 0f, 1f));
            back[i].apply(hind, billow * 0.8f, flutter * 1.2f, 0f);
        }

        for (int k = 0; k < TAIL_SEGMENTS; k++) {
            extraPitch[k] = curl * (k < 3 ? 0.15f : 0.05f);
            extraYaw[k] = wag * 0.45f * Mth.sin(time * 1.3f - k * 0.6f) + curl * 0.35f + shake * 0.25f * Mth.sin(time * 4.7f - k);
        }
        tail.apply(memory, pt, extraPitch, extraYaw);
        float turn = Mth.clamp(m.bank * 2f, -1f, 1f) * (1f - ground);
        float flap = 0.08f * Mth.sin(time * 0.7f) + shake * 0.3f * Mth.sin(time * 6.1f) + m.w(WingMotion.BRAKE) * 0.1f * Mth.sin(time * 3f);
        tail.fins(0.15f + 0.25f * m.w(WingMotion.BRAKE), 0.2f + 0.45f * Math.max(0f, turn) + 0.3f * m.w(WingMotion.BRAKE),
                prosthetic + 0.35f * Math.max(0f, -turn), flap);

        for (int k = 0; k < spines.size(); k++) {
            float ripple = 0.08f * Mth.sin(time * 0.3f - k * 0.7f) * ground;
            spines.get(k).rotate(-spineRaise * 0.8f + ripple, 0f, 0f);
            Bone glow = spineGlow.get(k);
            float lit = Mth.clamp(plasma * 1.4f - k * 0.12f, 0f, 1f) * (0.85f + 0.15f * Mth.sin(time * 1.9f + k));
            glow.visible(lit > 0.03f).scale(lit, lit, lit);
        }
    }

    private static float smooth(float x) {
        return x * x * (3f - 2f * x);
    }
}
