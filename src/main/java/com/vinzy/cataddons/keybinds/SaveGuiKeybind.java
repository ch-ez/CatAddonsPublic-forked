package com.vinzy.cataddons.keybinds;

import com.vinzy.cataddons.SharedVariables;
import com.vinzy.cataddons.features.SaveGuiManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class SaveGuiKeybind {
    public static KeyBinding keyBinding;
    static MinecraftClient mc = MinecraftClient.getInstance();

    private SaveGuiKeybind() {}

    public static void register() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cataddons.saveGui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                SharedVariables.CATEGORY
        ));

        KeybindManager.addKeybind(keyBinding, SaveGuiKeybind::handleSave);
    }

    public static void handleSave() {
        if (mc.player != null) {
            SaveGuiManager.saveAndCloseGui();
        }
    }

}
