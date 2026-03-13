package com.vinzy.cataddons.features.macrogui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.slot.SlotActionType;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class GuiMacro {

    public record MacroAction(int slotId, SlotActionType type, int button) {}

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File MACRO_DIR =
            new File(MinecraftClient.getInstance().runDirectory, "CatAddons/macros");

    public static boolean isRecording = false;
    private static boolean waitingForTargetGui = false;

    private static String currentMacroName = "";
    private static String targetGuiName = "Any";

    private static List<MacroAction> currentClicks = new ArrayList<>();

    public static void prepareRecording(String name, String guiName) {
        currentMacroName = name;
        targetGuiName = (guiName == null) ? "Any" : guiName;
        currentClicks.clear();

        isRecording = false;
        waitingForTargetGui = true;

        System.out.println("Recording will start when GUI opens: " + targetGuiName);
    }

    public static void checkScreen(Screen screen) {
        if (!waitingForTargetGui) return;
        if (screen == null) return;

        String title = screen.getTitle().getString();

        if (targetGuiName.equalsIgnoreCase("Any") ||
                title.toLowerCase().contains(targetGuiName.toLowerCase())) {

            isRecording = true;
            waitingForTargetGui = false;

            System.out.println("Started recording macro: " + currentMacroName);
        }
    }

    public static void recordClick(int slotId, SlotActionType type, int button) {
        if (!isRecording) return;
        currentClicks.add(new MacroAction(slotId, type, button));
    }

    public static void finalizeRecording() {
        if (!isRecording) return;

        isRecording = false;
        waitingForTargetGui = false;

        if (currentClicks.isEmpty()) {
            System.out.println("No clicks recorded.");
            return;
        }

        if (!MACRO_DIR.exists()) MACRO_DIR.mkdirs();

        try (FileWriter writer = new FileWriter(new File(MACRO_DIR, currentMacroName + ".json"))) {
            GSON.toJson(currentClicks, writer);
            System.out.println("Saved macro " + currentMacroName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runMacro(String name) {
        List<MacroAction> actions = getMacroActions(name);
        if (actions == null || actions.isEmpty()) {
            System.out.println("Macro not found.");
            return;
        }

        MacroManager.startMacro(name, actions);
    }

    public static List<MacroAction> getMacroActions(String name) {
        File file = new File(MACRO_DIR, name + ".json");
        if (!file.exists()) return null;

        try (FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, new TypeToken<List<MacroAction>>(){}.getType());
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> getList() {
        if (!MACRO_DIR.exists()) return Collections.emptyList();
        File[] files = MACRO_DIR.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return Collections.emptyList();

        return Arrays.stream(files)
                .map(f -> f.getName().replace(".json", ""))
                .collect(Collectors.toList());
    }

    public static void deleteMacro(String name) {
        File file = new File(MACRO_DIR, name + ".json");

        if (!file.exists()) {
            System.out.println("Macro not found: " + name);
            return;
        }

        if (file.delete()) {
            System.out.println("Deleted macro: " + name);
        } else {
            System.out.println("Failed to delete macro: " + name);
        }
    }
}
