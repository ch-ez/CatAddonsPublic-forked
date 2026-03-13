package com.vinzy.cataddons.modules.movement;

import com.vinzy.cataddons.modules.Module;
import com.vinzy.cataddons.modules.settings.BooleanSetting;
import net.minecraft.client.MinecraftClient;

public class AutoSprintModule extends Module {

    MinecraftClient mc = MinecraftClient.getInstance();
    private boolean wasSprinting = false;

    private final BooleanSetting waterCheck = register(new BooleanSetting("WaterCheck", true));

    public AutoSprintModule() {
        super("AutoSprint", "Movement");
    }

    @Override
    protected void onDisable() {
        wasSprinting = false;
    }

    @Override
    public void onTick() {
        if (isEnabled()) {
            if (mc.player != null) {
                if (waterCheck.getValue() && mc.player.isTouchingWater()) {
                    if (wasSprinting) {
                        mc.options.sprintKey.setPressed(false);
                        wasSprinting = false;
                    }
                    return;
                }
                mc.options.sprintKey.setPressed(true);
                wasSprinting = true;
            }
        }
    }
}