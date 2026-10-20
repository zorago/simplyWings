package com.zorago.simplywings.anim;

import net.minecraft.util.Mth;

public final class WingPose {
    public float spread;
    public float sweep;
    public float stroke;
    public float elbow;
    public float tip;
    public float twist;
    public float axis;
    public float curl;

    public WingPose set(float spread, float sweep, float stroke, float elbow, float tip, float twist) {
        this.spread = spread;
        this.sweep = sweep;
        this.stroke = stroke;
        this.elbow = elbow;
        this.tip = tip;
        this.twist = twist;
        this.axis = 0f;
        this.curl = 0f;
        return this;
    }

    public WingPose withAxis(float axis) {
        this.axis = axis;
        return this;
    }

    public WingPose withCurl(float curl) {
        this.curl = curl;
        return this;
    }

    public WingPose copy(WingPose o) {
        set(o.spread, o.sweep, o.stroke, o.elbow, o.tip, o.twist);
        axis = o.axis;
        curl = o.curl;
        return this;
    }

    public WingPose approach(WingPose target, float rate) {
        spread += (target.spread - spread) * rate;
        sweep += (target.sweep - sweep) * rate;
        stroke += (target.stroke - stroke) * rate;
        elbow += (target.elbow - elbow) * rate;
        tip += (target.tip - tip) * rate;
        twist += (target.twist - twist) * rate;
        axis += (target.axis - axis) * rate;
        curl += (target.curl - curl) * rate;
        return this;
    }

    public WingPose addScaled(WingPose o, float scale) {
        spread += o.spread * scale;
        sweep += o.sweep * scale;
        stroke += o.stroke * scale;
        elbow += o.elbow * scale;
        tip += o.tip * scale;
        twist += o.twist * scale;
        curl += o.curl * scale;
        return this;
    }

    public WingPose lerp(WingPose a, WingPose b, float t) {
        set(Mth.lerp(t, a.spread, b.spread), Mth.lerp(t, a.sweep, b.sweep), Mth.lerp(t, a.stroke, b.stroke),
                Mth.lerp(t, a.elbow, b.elbow), Mth.lerp(t, a.tip, b.tip), Mth.lerp(t, a.twist, b.twist));
        axis = Mth.lerp(t, a.axis, b.axis);
        curl = Mth.lerp(t, a.curl, b.curl);
        return this;
    }
}
