package com.vinzy.cataddons.keybinds;

import com.vinzy.cataddons.commands.CommandCat;
import com.vinzy.cataddons.SharedVariables;
import com.vinzy.cataddons.features.PacketPauseManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class PacketPauseKeybind {
    public static KeyBinding keyBinding;
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static long blinkStartTime = 0;

    public static long getBlinkStartTime() {
        return blinkStartTime;
    }

    private PacketPauseKeybind() {}

    public static void register() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Blink",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                SharedVariables.CATEGORY
        ));



        KeybindManager.addKeybind(
                keyBinding,
                PacketPauseKeybind::handleToggle
        );
    }

    public static void handleToggle() {
        if (client.getNetworkHandler() == null) return;
        if (!isShiftDown()) {
            boolean wasPaused = PacketPauseManager.isPaused();
            int packetCount = PacketPauseManager.getPacketQueue().size();
            PacketPauseManager.toggle(client.getNetworkHandler());
            if (client.player != null) {
                if (wasPaused) {
                    blinkStartTime = 0;
                    CommandCat.sendMessage("Sent §b" + packetCount + "§f packets.", true);
                    CommandCat.sendMessage("Packets are now §aresumed.", true);
                } else {
                    CommandCat.sendMessage("Packets are now §cpaused.", true);
                    blinkStartTime = System.currentTimeMillis();
                }
            }
        } else {
            handleCancel();
        }
    }

    private static boolean isShiftDown() {
        long window = client.getWindow().getHandle();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }

    public static void handleCancel() {
        if (!PacketPauseManager.isPaused()) {
            if (client.player != null) {
                CommandCat.sendMessage("Cannot cancel packets because blink is not active.", true);
            }
            return;
        }
        int packetCount = PacketPauseManager.getPacketQueue().size();
        PacketPauseManager.clear();
        PacketPauseManager.toggle(client.getNetworkHandler());
        blinkStartTime = 0;
        if (client.player != null) {
            CommandCat.sendMessage("§rBlink cancelled, cleared §b" + packetCount + "§f packets", true);
        }

    }
}
