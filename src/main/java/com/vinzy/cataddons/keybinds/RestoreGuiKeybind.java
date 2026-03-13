package com.vinzy.cataddons.keybinds;

import com.vinzy.cataddons.SharedVariables;
import com.vinzy.cataddons.features.SaveGuiManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class RestoreGuiKeybind {
    public static KeyBinding keyBinding;
    static MinecraftClient mc = MinecraftClient.getInstance();

    private RestoreGuiKeybind() {}

    public static void register() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Restore GUI",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                SharedVariables.CATEGORY
        ));

        KeybindManager.addKeybind(keyBinding, RestoreGuiKeybind::handleRestore);
    }

    public static void handleRestore() {
        if (mc.player != null) {
            SaveGuiManager.restoreGui();
        }
    }

}
