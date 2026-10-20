package com.zorago.simplywings.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public final class ModKeys {
    private static final String CATEGORY = "key.categories.simplywings";

    public static final KeyMapping TRAVEL = new KeyMapping("key.simplywings.travel", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, CATEGORY);
    public static final KeyMapping MENU = new KeyMapping("key.simplywings.menu", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, CATEGORY);
    public static final KeyMapping BRAKE = new KeyMapping("key.simplywings.brake", KeyConflictContext.IN_GAME,
            InputConstants.UNKNOWN, CATEGORY);

    static void register(RegisterKeyMappingsEvent event) {
        event.register(TRAVEL);
        event.register(BRAKE);
        event.register(MENU);
    }

    public static boolean brakeDown() {
        return BRAKE.isUnbound() ? Minecraft.getInstance().options.keyShift.isDown() : BRAKE.isDown();
    }

    private ModKeys() {
    }
}
