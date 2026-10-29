package com.zorago.simplywings.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public final class ModKeys {
    public static final Set<KeyMapping> SIMULATED = new HashSet<>();
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

    public static boolean down(KeyMapping key) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || key.isUnbound()) {
            return false;
        }
        InputConstants.Key code = key.getKey();
        long window = mc.getWindow().getWindow();
        boolean held = code.getType() == InputConstants.Type.MOUSE
                ? GLFW.glfwGetMouseButton(window, code.getValue()) == GLFW.GLFW_PRESS
                : code.getType() == InputConstants.Type.KEYSYM && InputConstants.isKeyDown(window, code.getValue());
        return held || SIMULATED.contains(key);
    }

    public static boolean accelerating() {
        return down(ACCELERATE) && !down(BRAKE);
    }

    public static boolean brakeDown() {
        return down(BRAKE) && !down(ACCELERATE);
    }

    public static int axis(KeyMapping positive, KeyMapping negative) {
        return (down(positive) ? 1 : 0) - (down(negative) ? 1 : 0);
    }

    private ModKeys() {
    }
}
