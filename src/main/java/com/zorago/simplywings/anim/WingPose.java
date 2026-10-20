package com.zorago.simplywings.anim;

import net.minecraft.util.Mth;

public final class WingPose {
    public float spread;
    public float sweep;
    public float stroke;
    public float elbow;
    public float tip;
    public float twist;

    public WingPose set(float spread, float sweep, float stroke, float elbow, float tip, float twist) {
        this.spread = spread;
        this.sweep = sweep;
        this.stroke = stroke;
        this.elbow = elbow;
        this.tip = tip;
        this.twist = twist;
        return this;
    }

    public WingPose copy(WingPose o) {
        return set(o.spread, o.sweep, o.stroke, o.elbow, o.tip, o.twist);
    }

    public WingPose approach(WingPose target, float rate) {
        spread += (target.spread - spread) * rate;
        sweep += (target.sweep - sweep) * rate;
        stroke += (target.stroke - stroke) * rate;
        elbow += (target.elbow - elbow) * rate;
        tip += (target.tip - tip) * rate;
        twist += (target.twist - twist) * rate;
        return this;
    }

    public WingPose addScaled(WingPose o, float scale) {
        spread += o.spread * scale;
        sweep += o.sweep * scale;
        stroke += o.stroke * scale;
        elbow += o.elbow * scale;
        tip += o.tip * scale;
        twist += o.twist * scale;
        return this;
    }

    public WingPose lerp(WingPose a, WingPose b, float t) {
        return set(Mth.lerp(t, a.spread, b.spread), Mth.lerp(t, a.sweep, b.sweep), Mth.lerp(t, a.stroke, b.stroke),
                Mth.lerp(t, a.elbow, b.elbow), Mth.lerp(t, a.tip, b.tip), Mth.lerp(t, a.twist, b.twist));
    }
}
