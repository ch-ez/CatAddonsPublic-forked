package com.vinzy.cataddons.keybinds;

import com.vinzy.cataddons.mixin.accessor.KeyBindingAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.BookSigningScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class KeybindManager {

    private static final Map<KeyBinding, KeybindAction> keybinds = new HashMap<>();
    private static final Set<Integer> heldKeys = new HashSet<>();

    private KeybindManager() {}

    public static void registerTickHandler() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Screen screen = MinecraftClient.getInstance().currentScreen;
            if (screen instanceof ChatScreen) return;
            if (screen instanceof SignEditScreen) return;
            if (screen instanceof BookScreen) return;
            if (screen instanceof BookEditScreen) return;
            if (screen instanceof BookSigningScreen) return;
            long window = client.getWindow().getHandle();
            for (Map.Entry<KeyBinding, KeybindAction> entry : keybinds.entrySet()) {
                int glfwKey = ((KeyBindingAccessor) entry.getKey()).getBoundKey().getCode();
                if (glfwKey == GLFW.GLFW_KEY_UNKNOWN) continue;
                int keyState = GLFW.glfwGetKey(window, glfwKey);
                if (keyState == GLFW.GLFW_PRESS && !heldKeys.contains(glfwKey)) {
                    heldKeys.add(glfwKey);
                    entry.getValue().run();
                } else if (keyState == GLFW.GLFW_RELEASE) {
                    heldKeys.remove(glfwKey);
                }
            }
        });
    }

    public static void addKeybind(KeyBinding key, Runnable onPress) {
        if (onPress != null) keybinds.put(key, onPress::run);
    }

    public static Map<KeyBinding, KeybindAction> getKeybinds() {
        return keybinds;
    }

    @FunctionalInterface
    public interface KeybindAction {
        void run();
    }
}