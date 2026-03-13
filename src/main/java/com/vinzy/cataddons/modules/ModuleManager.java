package com.vinzy.cataddons.modules;

import com.vinzy.cataddons.modules.settings.Setting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public void register(Module module) {
        modules.add(module);
    }

    public void registerAll(Module... mods) {
        for (Module m : mods) register(m);
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public Module getModuleByName(String name) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public boolean isEnabled(String name) {
        Module m = getModuleByName(name);
        return m != null && m.isEnabled();
    }

    public Setting<?> getSetting(String moduleName, String settingName) {
        Module m = getModuleByName(moduleName);
        if (m == null) return null;
        return m.getSettingByName(settingName);
    }

    public void onTick() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }
}