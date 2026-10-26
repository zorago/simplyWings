package com.zorago.simplywings.client.show;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class ShowRenderer {
    private static Matrix4f[] world = new Matrix4f[0];
    private static boolean[] hidden = new boolean[0];
    private static final float[] POS = new float[3];
    private static final float[] ROT = new float[3];
    private static final float[] SCL = new float[3];
    private static final Matrix4f TMP = new Matrix4f();
    private static final Vector3f V = new Vector3f();
    private static final Map<Integer, RenderType> EMISSIVE = new HashMap<>();
    private static final Map<Integer, RenderType> LIT = new HashMap<>();
    public static final Map<Integer, Vector3f> OFFSETS = new HashMap<>();

    public static void local(ShowModel.Bone b, ShowModel.Anim anim, int i, float time, Matrix4f out) {
        anim.sample(i, 0, time, POS, 0);
        anim.sample(i, 1, time, ROT, 0);
        anim.sample(i, 2, time, SCL, 1);
        Vector3f off = OFFSETS.isEmpty() ? null : OFFSETS.get(i);
        float ox = off == null ? 0 : off.x;
        float oy = off == null ? 0 : off.y;
        float oz = off == null ? 0 : off.z;
        out.identity().translate(b.px - POS[0] + ox, b.py + POS[1] + oy, b.pz + POS[2] + oz)
                .rotateZ((float) Math.toRadians(b.rz + ROT[2]))
                .rotateY((float) Math.toRadians(b.ry - ROT[1]))
                .rotateX((float) Math.toRadians(b.rx - ROT[0]))
                .scale(SCL[0], SCL[1], SCL[2])
                .translate(-b.px, -b.py, -b.pz);
    }

    private static boolean zero() {
        return Math.abs(SCL[0]) < 1e-4f || Math.abs(SCL[1]) < 1e-4f || Math.abs(SCL[2]) < 1e-4f;
    }

    public static void solve(ShowModel model, ShowModel.Anim anim, float time) {
        int n = model.bones.length;
        if (world.length != n) {
            world = new Matrix4f[n];
            for (int i = 0; i < n; i++) {
                world[i] = new Matrix4f();
            }
            hidden = new boolean[n];
        }
        for (int i = 0; i < n; i++) {
            ShowModel.Bone b = model.bones[i];
            if (b.parent >= 0 && hidden[b.parent]) {
                hidden[i] = true;
                continue;
            }
            local(b, anim, i, time, TMP);
            hidden[i] = zero();
            if (b.parent >= 0) {
                world[i].set(world[b.parent]).mul(TMP);
            } else {
                world[i].set(TMP);
            }
        }
    }

    public static Vector3f position(ShowModel model, ShowModel.Anim anim, int bone, float time, Vector3f out) {
        Matrix4f m = new Matrix4f();
        Matrix4f l = new Matrix4f();
        int[] chain = new int[64];
        int depth = 0;
        for (int i = bone; i >= 0 && depth < chain.length; i = model.bones[i].parent) {
            chain[depth++] = i;
        }
        for (int d = depth - 1; d >= 0; d--) {
            int i = chain[d];
            local(model.bones[i], anim, i, time, l);
            m.mul(l);
        }
        ShowModel.Bone b = model.bones[bone];
        return m.transformPosition(out.set(b.px, b.py, b.pz));
    }

    public static boolean visible(ShowModel model, ShowModel.Anim anim, int bone, float time) {
        for (int i = bone; i >= 0; i = model.bones[i].parent) {
            anim.sample(i, 2, time, SCL, 1);
            if (zero()) {
                return false;
            }
        }
        return true;
    }

    public static RenderType type(ShowModel model, int tex, boolean lit) {
        Map<Integer, RenderType> cache = lit ? LIT : EMISSIVE;
        return cache.computeIfAbsent(tex, t -> lit ? RenderType.entityTranslucent(model.textures[t].path())
                : RenderType.entityTranslucentEmissive(model.textures[t].path()));
    }

    public static void clearCache() {
        EMISSIVE.clear();
        LIT.clear();
    }

    public static void render(ShowModel model, ShowModel.Anim anim, float time, float clock, PoseStack poseStack, MultiBufferSource buffers,
                              IntUnaryOperator tint) {
        solve(model, anim, time);
        Matrix4f base = poseStack.last().pose();
        Matrix3f normalBase = poseStack.last().normal();
        Matrix4f m = new Matrix4f();
        Matrix3f nm = new Matrix3f();
        int light = LightTexture.FULL_BRIGHT;
        for (int i = 0; i < model.bones.length; i++) {
            ShowModel.Bone b = model.bones[i];
            if (hidden[i] || b.faces.length == 0) {
                continue;
            }
            int color = tint.applyAsInt(i);
            int cr = (color >> 16) & 0xFF;
            int cg = (color >> 8) & 0xFF;
            int cb = color & 0xFF;
            int ca = (color >>> 24) & 0xFF;
            m.set(base).mul(world[i]);
            nm.set(normalBase).mul(new Matrix3f(world[i]).normal());
            int lastTex = -1;
            VertexConsumer vc = null;
            float frameOffset = 0;
            float frameScale = 1;
            for (ShowModel.Face f : b.faces) {
                if (f.tex != lastTex) {
                    lastTex = f.tex;
                    vc = buffers.getBuffer(type(model, f.tex, b.lit));
                    ShowModel.Tex tx = model.textures[f.tex];
                    if (tx.frames() > 1) {
                        int frame = (int) Math.floor(clock * 20f / Math.max(1, tx.frameTime())) % tx.frames();
                        frameScale = 1f / tx.frames();
                        frameOffset = frame * frameScale;
                    } else {
                        frameOffset = 0;
                        frameScale = 1;
                    }
                }
                V.set(f.nx, f.ny, f.nz);
                nm.transform(V).normalize();
                float nx = V.x, ny = V.y, nz = V.z;
                for (int k = 0; k < 4; k++) {
                    m.transformPosition(f.pos[k * 3], f.pos[k * 3 + 1], f.pos[k * 3 + 2], V);
                    vc.addVertex(V.x, V.y, V.z).setColor(cr, cg, cb, ca)
                            .setUv(f.uv[k * 2], frameOffset + f.uv[k * 2 + 1] * frameScale)
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
                }
            }
        }
    }

    private ShowRenderer() {
    }
}
