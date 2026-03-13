package com.vinzy.cataddons.modules.render;

import com.vinzy.cataddons.modules.Module;
import com.vinzy.cataddons.modules.settings.StringSetting;

public class WatermarkModule extends Module {
    public final StringSetting watermarkText = register(new StringSetting("Text", "&dcataddons ᓚᘏᗢ"));

    public WatermarkModule(){
        super("Watermark", "Render");
    }
}
