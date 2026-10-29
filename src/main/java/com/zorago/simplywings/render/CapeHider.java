package com.zorago.simplywings.render;

import com.zorago.simplywings.client.WingVisuals;
import java.lang.reflect.Field;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

public final class CapeHider {
    private static final Field CLOAK = ObfuscationReflectionHelper.findField(PlayerModel.class, "cloak");

    public static void onPre(RenderPlayerEvent.Pre event) {
        WingVisuals.Visual visual = WingVisuals.of(event.getEntity());
        if (visual != null && visual.look().isPresent()) {
            cloak(event.getRenderer().getModel()).visible = false;
        }
    }

    public static void onPost(RenderPlayerEvent.Post event) {
        cloak(event.getRenderer().getModel()).visible = true;
    }

    private static ModelPart cloak(PlayerModel<?> model) {
        try {
            return (ModelPart) CLOAK.get(model);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private CapeHider() {
    }
}
