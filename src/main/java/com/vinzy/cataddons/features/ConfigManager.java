package com.vinzy.cataddons.features;

import com.google.gson.*;
import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.modules.Module;
import com.vinzy.cataddons.modules.settings.*;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigManager {

    private static final File CONFIG_FILE = new File(
            MinecraftClient.getInstance().runDirectory,
            "CatAddons/config.json"
    );

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void save() {
        JsonObject root = new JsonObject();

        for (Module module : MainClient.MODULE_MANAGER.getModules()) {
            JsonObject modObj = new JsonObject();

            modObj.addProperty("enabled", module.isEnabled());

            if (!module.getSettings().isEmpty()) {
                JsonObject settingsObj = new JsonObject();
                for (Setting<?> s : module.getSettings()) {
                    if (s instanceof FloatSetting fs) {
                        settingsObj.addProperty(s.getName(), fs.getValue());
                    } else if (s instanceof BooleanSetting bs) {
                        settingsObj.addProperty(s.getName(), bs.getValue());
                    } else if (s instanceof ModeSetting ms) {
                        settingsObj.addProperty(s.getName(), ms.getValue());
                    } else if (s instanceof StringSetting ss) {
                        settingsObj.addProperty(s.getName(), ss.getValue());
                    }
                }
                modObj.add("settings", settingsObj);
            }

            root.add(module.getName(), modObj);
        }

        try {
            CONFIG_FILE.getParentFile().mkdirs();

            try (Writer writer = new OutputStreamWriter(
                    new FileOutputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
                GSON.toJson(root, writer);
            }
        } catch (IOException e) {
            System.err.println("[CatAddons] Failed to save config: " + e.getMessage());
        }
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) return;

        try (Reader reader = new InputStreamReader(
                new FileInputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {

            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            for (Module module : MainClient.MODULE_MANAGER.getModules()) {
                if (!root.has(module.getName())) continue;

                JsonObject modObj = root.getAsJsonObject(module.getName());

                if (modObj.has("enabled")) {
                    module.setEnabled(modObj.get("enabled").getAsBoolean());
                }

                if (modObj.has("settings")) {
                    JsonObject settingsObj = modObj.getAsJsonObject("settings");
                    for (Setting<?> s : module.getSettings()) {
                        if (!settingsObj.has(s.getName())) continue;
                        JsonElement el = settingsObj.get(s.getName());
                        try {
                            if (s instanceof FloatSetting fs) {
                                fs.setValue(el.getAsFloat());
                            } else if (s instanceof BooleanSetting bs) {
                                bs.setValue(el.getAsBoolean());
                            } else if (s instanceof ModeSetting ms) {
                                ms.setValue(el.getAsString());
                            } else if (s instanceof StringSetting ss) {
                                ss.setValue(el.getAsString());
                            }
                        } catch (Exception e) {
                            System.err.println("[CatAddons] Bad value for "
                                    + module.getName() + "." + s.getName() + ": " + e.getMessage());
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("[CatAddons] Failed to load config: " + e.getMessage());
        }
    }
}