package com.zorago.simplywings.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public final class ModKeys {
    private static final String CATEGORY = "key.categories.simplywings";
    private static final IKeyConflictContext FLIGHT = new IKeyConflictContext() {
        @Override
        public boolean isActive() {
            return KeyConflictContext.IN_GAME.isActive();
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return false;
        }
    };

    public static final KeyMapping TRAVEL = new KeyMapping("key.simplywings.travel", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, CATEGORY);
    public static final KeyMapping MENU = new KeyMapping("key.simplywings.menu", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, CATEGORY);
    public static final KeyMapping UP = flight("up", GLFW.GLFW_KEY_W);
    public static final KeyMapping DOWN = flight("down", GLFW.GLFW_KEY_S);
    public static final KeyMapping LEFT = flight("left", GLFW.GLFW_KEY_A);
    public static final KeyMapping RIGHT = flight("right", GLFW.GLFW_KEY_D);
    public static final KeyMapping ACCELERATE = flight("accelerate", GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyMapping BRAKE = flight("brake", GLFW.GLFW_KEY_LEFT_SHIFT);

    private static KeyMapping flight(String name, int key) {
        return new KeyMapping("key.simplywings." + name, FLIGHT, InputConstants.Type.KEYSYM, key, CATEGORY);
    }

    static void register(RegisterKeyMappingsEvent event) {
        event.register(TRAVEL);
        event.register(MENU);
        event.register(UP);
        event.register(DOWN);
        event.register(LEFT);
        event.register(RIGHT);
        event.register(ACCELERATE);
        event.register(BRAKE);
    }

    public static boolean accelerating() {
        return ACCELERATE.isDown() && !BRAKE.isDown();
    }

    public static boolean brakeDown() {
        return BRAKE.isDown() && !ACCELERATE.isDown();
    }

    public static int axis(KeyMapping positive, KeyMapping negative) {
        return (positive.isDown() ? 1 : 0) - (negative.isDown() ? 1 : 0);
    }

    private ModKeys() {
    }
}
