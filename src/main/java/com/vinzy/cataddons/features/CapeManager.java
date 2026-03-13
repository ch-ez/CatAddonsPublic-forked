package com.vinzy.cataddons.features;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CapeManager {

    public static Identifier customCape = null;
    public static final Map<String, Identifier> CAPES = new HashMap<>();


    public static void reload(ResourceManager rm) {
        CAPES.clear();

        try {
            rm.findResources("textures/capes",
                            id -> id.getNamespace().equals("cataddons") && id.getPath().endsWith(".png"))
                    .forEach((id, resource) -> {

                        String path = id.getPath();
                        String file = path.substring(path.lastIndexOf('/') + 1);
                        String clean = file.substring(0, file.lastIndexOf('.')).toLowerCase();

                        CAPES.put(clean, id);
                    });

            System.out.println("loaded capes → " + CAPES.keySet());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Identifier getIdentifier(String name) {
        return CAPES.get(name.toLowerCase());
    }

    public static void setCape(String cape) {
        customCape = CAPES.get(cape.toLowerCase());
    }

    public static void disableCape() {
        customCape = null;
    }
}