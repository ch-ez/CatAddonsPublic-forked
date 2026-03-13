package com.vinzy.cataddons.modules.render;

import com.vinzy.cataddons.modules.Module;
import com.vinzy.cataddons.modules.settings.BooleanSetting;

public class EspModule extends Module {
    public final BooleanSetting playerEsp = register(new BooleanSetting("Players", true));
    public final BooleanSetting modEsp = register(new BooleanSetting("Mobs", false));
    public final BooleanSetting droppedItemsEsp = register(new BooleanSetting("Items", false));

    public EspModule() {
        super("ESP", "Render");
    }
}
