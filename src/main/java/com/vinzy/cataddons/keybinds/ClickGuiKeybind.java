package com.vinzy.cataddons.keybinds;

import com.vinzy.cataddons.SharedVariables;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ClickGuiKeybind {
    public static KeyBinding keyBinding;
    static MinecraftClient mc = MinecraftClient.getInstance();

    private ClickGuiKeybind() {}

    public static void register() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "ClickGUI",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                SharedVariables.CATEGORY
        ));

        KeybindManager.addKeybind(keyBinding, ClickGuiKeybind::handleClick);
    }

    public static void handleClick() {
        if (mc.player != null) {
            Screen parentScreen = mc.currentScreen;
            SharedVariables.screenToOpen = new com.vinzy.cataddons.features.ClickGui(parentScreen);
        }
    }
}
