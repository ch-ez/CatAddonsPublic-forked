package com.vinzy.cataddons.features;

import com.vinzy.cataddons.commands.CommandCat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

public class SaveGuiManager {
    static MinecraftClient mc = MinecraftClient.getInstance();
    public static String guiName;
    public static Screen savedScreen = null;
    public static ScreenHandler savedScreenHandler = null;
    public static boolean deadGui;

    private SaveGuiManager() {}

    public static void saveAndCloseGui() {
        if (mc.player != null) {
            savedScreen = mc.currentScreen;
            savedScreenHandler = mc.player.currentScreenHandler;
            if (savedScreen == null) {
                CommandCat.sendMessage("§cNo GUI found to save.", true);
                return;
            }
            deadGui = false;
            mc.setScreen(null);
            if (savedScreen instanceof HandledScreen<?> handled) {
                String title = handled.getTitle().getString();
                CommandCat.sendMessage("Saved " + title + "§f GUI.", true);
                guiName = title;
            } else {
                String classSimpleName = savedScreen.getClass().getSimpleName();
                CommandCat.sendMessage("Saved " + classSimpleName + " GUI.", true);
                guiName = classSimpleName;
            }}
    }

    public static void restoreGui() {
        if (savedScreen != null && savedScreenHandler != null && mc.player != null) {
            deadGui = false;
            mc.setScreen(savedScreen);
            mc.player.currentScreenHandler = savedScreenHandler;
            savedScreen = null;
            savedScreenHandler = null;
            CommandCat.sendMessage("§aRestored §b" + guiName + "§a GUI.", true);
        } else {
            CommandCat.sendMessage("§cNo saved GUI.", true);
        }
    }
}
