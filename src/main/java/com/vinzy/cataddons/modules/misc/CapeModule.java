package com.vinzy.cataddons.modules.misc;

import com.vinzy.cataddons.features.CapeManager;
import com.vinzy.cataddons.modules.Module;
import com.vinzy.cataddons.modules.settings.ModeSetting;

import java.util.ArrayList;
import java.util.List;

public class CapeModule extends Module {
    private final ModeSetting cape = register(new ModeSetting("Cape", "disabled", "disabled"));

    public CapeModule() {
        super("CustomCape", "Misc");
    }

    public void cycleNext() {
        List<String> options = buildOptions();
        int idx = options.indexOf(cape.getValue());
        if (idx == -1) idx = 0;
        String next = options.get((idx + 1) % options.size());
        applyRaw(next);
    }

    public void cyclePrev() {
        List<String> options = buildOptions();
        int idx = options.indexOf(cape.getValue());
        if (idx == -1) idx = 0;
        String prev = options.get((idx - 1 + options.size()) % options.size());
        applyRaw(prev);
    }

    public String getCurrentValue() {
        return cape.getValue();
    }

    public void refreshOptions() {
        List<String> opts = new ArrayList<>();
        opts.add("disabled");
        opts.addAll(CapeManager.CAPES.keySet());
        cape.setOptions(opts.toArray(new String[0]));
    }

    private List<String> buildOptions() {
        List<String> options = new ArrayList<>();
        options.add("disabled");
        options.addAll(CapeManager.CAPES.keySet());
        return options;
    }

    private void applyRaw(String value) {
        try {
            var field = cape.getClass().getSuperclass().getDeclaredField("value");
            field.setAccessible(true);
            field.set(cape, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        apply();
    }

    @Override
    protected void onEnable()  { apply(); }

    @Override
    protected void onDisable() { CapeManager.disableCape(); }

    @Override
    public void onTick() {
        if (isEnabled()) apply();
    }

    private void apply() {
        String selected = cape.getValue();
        if (selected.equals("disabled")) {
            CapeManager.disableCape();
        } else {
            CapeManager.setCape(selected);
        }
    }
}