package com.vinzy.cataddons.keybinds;

import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.SharedVariables;
import com.vinzy.cataddons.modules.Module;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static com.vinzy.cataddons.commands.CommandCat.sendMessage;

public class FreeLookKeybind {
    public static KeyBinding keyBinding;
    static MinecraftClient mc = MinecraftClient.getInstance();

    private FreeLookKeybind() {}

    public static void register() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cataddons.freeLook",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                SharedVariables.CATEGORY
        ));

        KeybindManager.addKeybind(keyBinding, FreeLookKeybind::handleToggle);
    }

    private static void handleToggle() {
        if (mc.player != null) {
            Module module = MainClient.MODULE_MANAGER.getModuleByName("FreeLook");
            module.toggle();
            sendMessage(
                    "§bFreeLook§f is now " + (module.isEnabled() ? "§aenabled" : "§cdisabled") + ".",
                    true
            );
        }
    }
}
