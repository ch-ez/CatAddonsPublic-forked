package com.vinzy.cataddons.features.macrogui;

import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.modules.glitcha.MacroGuiSettingsModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

import java.util.ArrayList;
import java.util.List;

public class MacroManager {

    private static List<GuiMacro.MacroAction> activeSequence = new ArrayList<>();
    private static int currentIndex = -1;
    private static String runningMacroName = "";

    private static long lastActionTime = 0;
    private static boolean waitingForFirstClick = false;

    private static MacroGuiSettingsModule getSettings() {
        return (MacroGuiSettingsModule) MainClient.MODULE_MANAGER.getModuleByName("MacroGui");
    }

    private static long firstClickDelay() {
        return getSettings().firstClickDelaySetting.getValue().longValue();
    }

    private static long clickInterval() {
        return getSettings().clickIntervalSetting.getValue().longValue();
    }

    private static boolean isLooping() {
        return getSettings().isLoopingSetting.getValue();
    }

    public static void startMacro(String name, List<GuiMacro.MacroAction> steps) {
        runningMacroName = name;
        activeSequence = new ArrayList<>(steps);
        currentIndex = 0;
        waitingForFirstClick = true;
        lastActionTime = System.currentTimeMillis();
    }

    public static void stop() {
        currentIndex = -1;
        activeSequence.clear();
        runningMacroName = "";
    }

    public static boolean isRunning() {
        return currentIndex != -1;
    }

    public static String getRunningName() {
        return runningMacroName;
    }

    public static void tick() {
        if (currentIndex == -1) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.interactionManager == null) return;

        if (client.currentScreen instanceof HandledScreen<?> screen) {

            long currentTime = System.currentTimeMillis();
            long delay = waitingForFirstClick ? firstClickDelay() : clickInterval();

            if (currentTime - lastActionTime >= delay) {

                if (currentIndex < activeSequence.size()) {

                    GuiMacro.MacroAction action = activeSequence.get(currentIndex);

                    int slotId = action.slotId();
                    int maxSlots = screen.getScreenHandler().slots.size();

                    if (slotId < 0 || slotId >= maxSlots) {
                        System.out.println("Macro attempted to click invalid slot " + slotId + " (max " + maxSlots + ")");
                        currentIndex++;
                        return;
                    }

                    client.interactionManager.clickSlot(
                            screen.getScreenHandler().syncId,
                            action.slotId(),
                            action.button(),
                            action.type(),
                            client.player
                    );

                    currentIndex++;
                    waitingForFirstClick = false;
                    lastActionTime = currentTime;

                } else {
                    if (isLooping()) {
                        currentIndex = 0;
                        lastActionTime = currentTime;
                    } else {
                        stop();
                    }
                }
            }
        }
    }
}