package com.zorago.simplywings.anim;

import com.zorago.simplywings.player.FlightMode;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class WingAnimator {
    private static final float TWO_PI = (float) (Math.PI * 2);
    private static final float HALF_PI = (float) (Math.PI / 2);
    private static final int ROUSE_TICKS = 26;

    private final WingPose base = new WingPose();
    private final WingPose prevBase = new WingPose();
    private final WingPose baseTarget = new WingPose();
    private final WingPose scratch = new WingPose();

    private double hoverPhase;
    private double prevHoverPhase;
    private float hoverWeight;
    private float prevHoverWeight;
    private float stallWeight;
    private float prevStallWeight;
    private float vibrationWeight;
    private float prevVibrationWeight;
    private float strokeProgress = -1;
    private float prevStrokeProgress = -1;
    private float strokeDuration = 10;
    private float loopRate;
    private int loopTicks;
    private int age;
    private boolean initialized;
    private float bank;
    private float prevBank;
    private float charge;
    private boolean idle;
    private boolean glide;
    private boolean brake;
    private final float[] weight = new float[WingMotion.COUNT];
    private final float[] prevWeight = new float[WingMotion.COUNT];
    private final float[] weightVelocity = new float[WingMotion.COUNT];
    private final java.util.Random random = new java.util.Random();
    private boolean weightsInitialized;
    private float stallRig;
    private float prevStallRig;
    private float diveDepth;
    private float prevDiveDepth;
    private float speed;
    private float climb;
    private float flapRate = 2f;
    private float hoverRate = 1f;
    private int forcedState = -1;
    private boolean forcedStall;
    private float forcedBeat = -1f;
    private float forcedHover = Float.NaN;
    private float forcedTime = Float.NaN;
    private float forcedDive = 1f;
    private float forcedCharge = 0f;
    private boolean wasFlying;
    private int landingTicks;
    private int rouseTicks;
    private int nextRouse = 300;

    public void flap(float flapRate) {
        this.flapRate = flapRate;
        strokeDuration = Math.max(4f, 20f / Math.max(0.1f, flapRate));
        strokeProgress = 0;
        prevStrokeProgress = 0;
    }

    public void beat(float flapRate, int holdTicks) {
        loopRate = flapRate;
        loopTicks = Math.max(loopTicks, holdTicks);
        if (strokeProgress < 0) {
            flap(flapRate);
        }
    }

    public void strike(float flapRate, float strikeAt) {
        flap(flapRate);
        strokeProgress = strikeAt;
        prevStrokeProgress = strikeAt;
    }

    public void setCharge(float charge) {
        this.charge = Mth.clamp(charge, 0f, 1f);
    }

    public float bank(float partialTick) {
        return Mth.lerp(partialTick, prevBank, bank);
    }

    public void tick(Player player, FlightMode mode, int flags, WingStyle style, float hoverRate) {
        age++;
        prevBank = bank;
        float turnRate = Mth.wrapDegrees(player.yBodyRot - player.yBodyRotO) * 20f;
        float targetBank = mode == FlightMode.TRAVEL ? Mth.clamp(turnRate * 0.5f, -45f, 45f) : 0f;
        bank += (targetBank - bank) * 0.25f;

        brake = (flags & FlightMode.FLAG_BRAKE) != 0;
        boolean stall = (flags & FlightMode.FLAG_STALL) != 0;
        boolean tuck = (flags & FlightMode.FLAG_TUCK) != 0;
        boolean thrust = (flags & FlightMode.FLAG_THRUST) != 0 || loopTicks > 0;
        boolean grounded = player.onGround() || player.isPassenger();
        float pitch = player.getXRot();
        float rate = 0.3f;
        boolean hovering = false;
        idle = mode == FlightMode.NONE && grounded;
        glide = mode == FlightMode.TRAVEL && !brake && !tuck;

        switch (mode) {
            case STABILIZED -> {
                baseTarget.set(0.9f, 0.15f, 0.25f, 0f, 0f, 0.05f);
                hovering = true;
            }
            case TRAVEL -> {
                if (brake) {
                    baseTarget.set(1f, 0.55f, 0.45f, -0.25f, -0.35f, 1.1f);
                    rate = 0.35f;
                } else if (tuck) {
                    baseTarget.set(0.05f, 0f, 0f, 0f, 0f, 0f);
                    rate = 0.25f;
                } else if (pitch > 35f) {
                    float dive = Mth.clamp((pitch - 35f) / 40f, 0f, 1f);
                    baseTarget.set(1f, Mth.lerp(dive, -0.05f, -0.25f), 0.06f, 0f, -0.05f, 0f);
                } else {
                    baseTarget.set(1f, -0.05f, 0.06f, 0f, -0.05f, 0f);
                }
                baseTarget.withAxis(1f);
            }
            case NONE -> {
                if (grounded) {
                    baseTarget.set(charge * 0.95f, charge * -0.2f, charge * 0.7f, 0f, 0f, 0f);
                    rate = charge > 0 ? 0.3f : 0.2f;
                } else {
                    baseTarget.set(0.55f, 0.1f, 0.5f, 0.1f, 0f, 0.2f);
                }
            }
        }
        if (!style.folds() && baseTarget.spread < 1f) {
            float fold = 1f - baseTarget.spread;
            baseTarget.spread = 1f;
            baseTarget.sweep -= 0.55f * fold;
            baseTarget.stroke -= 0.1f * fold;
        }

        if (!initialized) {
            base.copy(baseTarget);
            initialized = true;
        }
        prevBase.copy(base);
        base.approach(baseTarget, rate);

        prevHoverWeight = hoverWeight;
        prevStallWeight = stallWeight;
        prevVibrationWeight = vibrationWeight;
        boolean hoverBeats = hovering && style.hoverAmplitude() > 0;
        boolean vibrating = style.vibration() > 0 && (hovering || thrust || (mode == FlightMode.TRAVEL && !tuck));
        float vibrationTarget = vibrating ? (hovering || thrust ? 1f : 0.3f) : 0f;
        hoverWeight += ((hoverBeats ? 1f : 0f) - hoverWeight) * 0.2f;
        stallWeight += ((stall ? 1f : 0f) - stallWeight) * 0.25f;
        vibrationWeight += (vibrationTarget - vibrationWeight) * 0.3f;

        prevHoverPhase = hoverPhase;
        hoverPhase += TWO_PI * hoverRate / 20f;
        if (hoverPhase > TWO_PI * 64) {
            hoverPhase -= TWO_PI * 64;
            prevHoverPhase -= TWO_PI * 64;
        }

        prevStrokeProgress = strokeProgress;
        if (strokeProgress >= 0) {
            strokeProgress += 1f / strokeDuration;
            if (strokeProgress >= 1f && loopTicks > 0) {
                strokeDuration = Math.max(4f, 20f / Math.max(0.1f, loopRate));
                strokeProgress -= 1f;
                prevStrokeProgress = 0;
            } else if (strokeProgress >= 1f) {
                strokeProgress = -1;
                prevStrokeProgress = -1;
            }
        }
        if (loopTicks > 0) {
            loopTicks--;
        }
        this.hoverRate = hoverRate;
        net.minecraft.world.phys.Vec3 motion = player.position().subtract(player.xo, player.yo, player.zo);
        speed = (float) motion.length() * 20f;
        climb = (float) motion.y * 20f;
        tickWeights(mode, grounded, pitch, stall, tuck);
    }

    private void tickWeights(FlightMode mode, boolean grounded, float pitch, boolean stall, boolean tuck) {
        boolean flying = mode != FlightMode.NONE || !grounded;
        if (wasFlying && !flying) {
            landingTicks = 14;
        }
        wasFlying = flying;
        if (landingTicks > 0) {
            landingTicks--;
        }
        if (rouseTicks > 0) {
            rouseTicks--;
        }
        if (idle && charge <= 0f && landingTicks == 0 && forcedState < 0) {
            if (--nextRouse <= 0) {
                rouseTicks = ROUSE_TICKS;
                nextRouse = 400 + random.nextInt(700);
            }
        } else if (forcedState < 0) {
            rouseTicks = 0;
        }

        float[] target = new float[WingMotion.COUNT];
        float stiffness = 0.16f;
        float damping = 0.68f;
        float depth = 0f;
        int state = forcedState >= 0 ? forcedState : switch (mode) {
            case STABILIZED -> WingMotion.HOVER;
            case TRAVEL -> brake ? WingMotion.BRAKE : tuck ? WingMotion.TUCK : pitch > 35f ? WingMotion.DIVE : WingMotion.CRUISE;
            case NONE -> !grounded ? WingMotion.AIR : landingTicks > 0 ? WingMotion.LANDING : WingMotion.GROUND;
        };
        if (state == WingMotion.BRAKE || state == WingMotion.TUCK) {
            stiffness = 0.22f;
            damping = 0.62f;
        }
        if (state == WingMotion.DIVE) {
            depth = forcedState >= 0 ? forcedDive : Mth.clamp((pitch - 35f) / 40f, 0f, 1f);
        }
        if (state == WingMotion.GROUND || state == WingMotion.CHARGE) {
            float c = forcedState >= 0 ? (state == WingMotion.CHARGE ? forcedCharge : 0f) : charge;
            c = c * c * (3f - 2f * c);
            target[WingMotion.GROUND] = 1f - c;
            target[WingMotion.CHARGE] = c;
            if (c <= 0f) {
                stiffness = 0.1f;
                damping = 0.72f;
            }
        } else {
            target[state] = 1f;
        }

        prevStallRig = stallRig;
        prevDiveDepth = diveDepth;
        boolean stallNow = forcedState >= 0 ? forcedStall : stall && mode == FlightMode.TRAVEL;
        if (!weightsInitialized) {
            System.arraycopy(target, 0, weight, 0, WingMotion.COUNT);
            stallRig = stallNow ? 1f : 0f;
            diveDepth = depth;
            weightsInitialized = true;
        }
        System.arraycopy(weight, 0, prevWeight, 0, WingMotion.COUNT);
        for (int i = 0; i < WingMotion.COUNT; i++) {
            weightVelocity[i] = (weightVelocity[i] + (target[i] - weight[i]) * stiffness) * damping;
            weight[i] += weightVelocity[i];
        }
        stallRig += ((stallNow ? 1f : 0f) - stallRig) * 0.3f;
        diveDepth += (depth - diveDepth) * 0.25f;
        if (forcedState >= 0) {
            System.arraycopy(target, 0, weight, 0, WingMotion.COUNT);
            System.arraycopy(target, 0, prevWeight, 0, WingMotion.COUNT);
            java.util.Arrays.fill(weightVelocity, 0f);
            stallRig = prevStallRig = stallNow ? 1f : 0f;
            diveDepth = prevDiveDepth = depth;
            idle = state == WingMotion.GROUND || state == WingMotion.CHARGE;
            glide = state == WingMotion.CRUISE;
            brake = state == WingMotion.BRAKE;
        }
    }

    public WingMotion motion(float partialTick, WingStyle style, WingMotion out) {
        boolean forced = forcedState >= 0;
        out.time = Float.isNaN(forcedTime) ? age + partialTick : forcedTime;
        out.partialTick = partialTick;
        for (int i = 0; i < WingMotion.COUNT; i++) {
            out.w[i] = Mth.lerp(partialTick, prevWeight[i], weight[i]);
        }
        out.stall = Mth.lerp(partialTick, prevStallRig, stallRig);
        out.diveDepth = Mth.lerp(partialTick, prevDiveDepth, diveDepth);
        float hoverRaw = Mth.lerp(partialTick, prevHoverWeight, hoverWeight);
        out.hoverBeat = forced ? (Float.isNaN(forcedHover) ? 0f : 1f) : hoverRaw;
        out.hoverPhase = Float.isNaN(forcedHover) ? (float) Mth.lerp(partialTick, prevHoverPhase, hoverPhase) : forcedHover;
        if (forcedBeat >= 0f) {
            out.beat = forcedBeat;
            out.beatWeight = 1f;
        } else if (!forced && strokeProgress >= 0 && prevStrokeProgress >= 0) {
            out.beat = wrap(Mth.lerp(partialTick, prevStrokeProgress, strokeProgress));
            out.beatWeight = 1f - hoverRaw;
        } else {
            out.beat = -1f;
            out.beatWeight = 0f;
        }
        out.buzz = forced ? (out.hoverBeat > 0f || forcedBeat >= 0f ? 1f : 0f)
                : Mth.lerp(partialTick, prevVibrationWeight, vibrationWeight);
        out.bank = forced ? 0f : bank(partialTick) * Mth.DEG_TO_RAD;
        out.rouse = 0f;
        out.rouseProgress = 0f;
        if (rouseTicks > 0) {
            out.rouseProgress = Mth.clamp(1f - (rouseTicks - partialTick) / ROUSE_TICKS, 0f, 1f);
            out.rouse = Mth.sin((float) Math.PI * out.rouseProgress);
        }
        out.speed = forced ? 0f : speed;
        out.climb = forced ? 0f : climb;
        out.flapRate = flapRate;
        out.hoverRate = hoverRate;
        out.upFraction = style.upFraction();
        out.downFraction = style.downFraction();
        out.idle = idle;
        return out;
    }

    public void force(int state, float amount, boolean stall) {
        forcedState = state;
        forcedStall = stall;
        forcedDive = amount;
        forcedCharge = amount;
    }

    public void forceBeat(float t) {
        forcedBeat = t;
    }

    public void forceHover(float phase) {
        forcedHover = phase;
    }

    public void forceTime(float time) {
        forcedTime = time;
    }

    public void forceRouse(float progress) {
        rouseTicks = Math.max(1, Math.round(ROUSE_TICKS * (1f - progress)));
    }

    public void release() {
        forcedState = -1;
        forcedStall = false;
        forcedBeat = -1f;
        forcedHover = Float.NaN;
        forcedTime = Float.NaN;
        rouseTicks = 0;
    }

    public WingPose pose(float partialTick, WingStyle style, WingStyle.Slot slot, WingPose out) {
        out.lerp(prevBase, base, partialTick);
        float time = age + partialTick;
        float hoverRaw = Mth.lerp(partialTick, prevHoverWeight, hoverWeight);
        float hw = hoverRaw * slot.amplitude();
        if (hw > 0.001f) {
            float ph = (float) Mth.lerp(partialTick, prevHoverPhase, hoverPhase) + slot.phase();
            float a = style.hoverAmplitude();
            scratch.set(-0.18f * Math.max(0f, Mth.cos(ph)), 0f, a * Mth.sin(ph), 0.55f * a * Mth.sin(ph - 0.9f),
                    0.4f * a * Mth.sin(ph - 1.5f), 0.12f * Mth.cos(ph));
            out.addScaled(scratch, hw);
        }
        if (strokeProgress >= 0 && prevStrokeProgress >= 0) {
            float t = wrap(Mth.lerp(partialTick, prevStrokeProgress, strokeProgress) + slot.phase() / TWO_PI);
            float a = style.strokeAmplitude() * slot.amplitude() * (1f - hoverRaw);
            out.stroke += a * style.stroke(t);
            out.elbow += 0.6f * a * style.stroke(wrap(t - 0.07f));
            out.tip += 0.45f * a * style.stroke(wrap(t - 0.14f));
            out.curl += 0.45f * style.curl(t) * slot.amplitude() * (1f - hoverRaw);
        }
        float sw = Mth.lerp(partialTick, prevStallWeight, stallWeight) * slot.amplitude();
        if (sw > 0.001f) {
            float a = time * 1.4f + slot.phase();
            scratch.set(-0.2f, 0.2f * Mth.sin(time * 0.5f), 0.3f + 0.6f * Mth.sin(a), 0.4f * Mth.sin(a - 1f),
                    0.3f * Mth.sin(a - 1.8f), 0.4f * Mth.sin(time * 0.7f));
            out.addScaled(scratch, sw);
        }
        float vw = Mth.lerp(partialTick, prevVibrationWeight, vibrationWeight) * slot.amplitude();
        if (vw > 0.001f) {
            float v = style.vibration() * Mth.sin(time * TWO_PI * style.vibrationRate() / 20f + slot.phase());
            out.stroke += vw * v;
            out.twist += vw * v * 0.3f;
        }
        if (idle || !style.folds()) {
            out.stroke += style.breath() * Mth.sin(time * 0.08f + slot.phase());
            out.sweep += style.breath() * 0.4f * Mth.sin(time * 0.06f);
        } else if (glide) {
            out.stroke += 0.03f * (Mth.sin(time * 0.37f) + Mth.sin(time * 0.23f));
        } else if (brake) {
            out.stroke += 0.04f * Mth.sin(time * 2.3f);
        }
        float offsetWeight = style.folds() ? Mth.clamp(out.spread, 0f, 1f) : 1f;
        out.sweep += slot.sweep() * offsetWeight;
        out.stroke += slot.lift() * offsetWeight;
        if (style.flat()) {
            out.twist += HALF_PI * (1f - out.axis);
        }
        out.spread = Mth.clamp(out.spread, 0f, 1.1f);
        return out;
    }

    private static float wrap(float t) {
        return t - (float) Math.floor(t);
    }
}
