package com.vinzy.cataddons.modules.glitcha;

import com.vinzy.cataddons.modules.Module;
import com.vinzy.cataddons.modules.settings.BooleanSetting;
import com.vinzy.cataddons.modules.settings.FloatSetting;

public class MacroGuiSettingsModule extends Module {
    public final FloatSetting firstClickDelaySetting = register(new FloatSetting("FirstClickDelay", 500f, 1f, 1000f));
    public final FloatSetting clickIntervalSetting = register(new FloatSetting("ClickInterval", 250f, 1f, 1000f));
    public final BooleanSetting isLoopingSetting = register(new BooleanSetting("Looping", false));

    public MacroGuiSettingsModule() {
        super("MacroGui", "Glitcha");
    }
}
