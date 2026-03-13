package com.vinzy.cataddons.modules;

import com.vinzy.cataddons.modules.settings.Setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Module {

    private final String name;
    private final String category;
    private boolean enabled;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String category) {
        this.name     = name;
        this.category = category;
    }

    protected <T extends Setting<?>> T register(T setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting<?>> getSettings() {
        return Collections.unmodifiableList(settings);
    }

    public Setting<?> getSettingByName(String name) {
        return settings.stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public String getName()     { return name;     }
    public String getCategory() { return category; }
    public boolean isEnabled()  { return enabled;  }

    public void setEnabled(boolean state) {
        if (enabled == state) return;
        enabled = state;
        if (enabled) onEnable();
        else         onDisable();
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    protected void onEnable()  {}
    protected void onDisable() {}
    public    void onTick()    {}
}