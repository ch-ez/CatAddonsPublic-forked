package com.vinzy.cataddons.features;

import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.features.macrogui.MacroManager;
import com.vinzy.cataddons.modules.render.WatermarkModule;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

import static com.vinzy.cataddons.features.TPSDisplay.*;

public final class HudOverlay {

    private HudOverlay() {}

    public static void init() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;
            int x = 4;
            int y = 4;

            WatermarkModule watermarkMod = (WatermarkModule) MainClient.MODULE_MANAGER.getModuleByName("Watermark");
            if(watermarkMod.isEnabled()){
                drawContext.drawText(
                        client.textRenderer,
                        watermarkMod.watermarkText.getValue().replace("&", "§"),
                        x,
                        y,
                        0xFFFFFFFF,
                        true
                );
            }

            if (MacroManager.isRunning()) {
                drawContext.drawText(
                        client.textRenderer,
                        "§9Active GUI Macro",
                        x,
                        y + 21,
                        0xFFFFFFFF,
                        true
                );

                drawContext.drawText(
                        client.textRenderer,
                        "§7> §3" + MacroManager.getRunningName(),
                        x,
                        y + 33,
                        0xFFFFFFFF,
                        true
                );
            }

            if (SaveGuiManager.savedScreen != null) {
                drawContext.drawText(
                        client.textRenderer,
                        "§9Saved GUI",
                        x,
                        y + 57,
                        0xFFFFFFFF,
                        true
                );

                drawContext.drawText(
                        client.textRenderer,
                        "§7> " + (SaveGuiManager.deadGui ? "§c" : "§3") + SaveGuiManager.guiName + (SaveGuiManager.deadGui ? "§c (clientside only)" : ""),
                        x,
                        y + 69,
                        0xFFFFFFFF,
                        true
                );
            }

            if (MainClient.MODULE_MANAGER.isEnabled("TPSCounter")) {
                if (lastPacketTime == -1) return;
                long timeSinceUpdate = System.currentTimeMillis() - lastPacketTime;
                double seconds = timeSinceUpdate / 1000.0;

                String text;
                int color;

                String prefix = "§9Server TPS: "; // i kinda like this colour for the tps counter if u dont you can change it idm

                if (seconds > 10.0) {
                    text = String.format("%s§l§cFROZEN §r§7(§f%.1fs§7)", prefix, seconds); // dark red = servers fuckin fried rip
                    color = 0xFFAA0000;
                } else if (seconds > 3.5) {
                    text = String.format("%s§l§5FROZEN §r§7(§f%.1fs§7)", prefix, seconds); // slight freeze
                    color = 0xFFFF5555;
                } else {
                    String tpsColorCode = getTpsColorCode(tps);
                    text = String.format("%s%s%.1f", prefix, tpsColorCode, tps);
                    color = 0xFFFFFFFF;
                }

                drawContext.drawText(client.textRenderer, text, x, y + 93, color, true);
            }
        });
    }
}