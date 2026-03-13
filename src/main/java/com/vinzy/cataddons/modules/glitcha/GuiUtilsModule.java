package com.vinzy.cataddons.modules.glitcha;

import com.vinzy.cataddons.modules.Module;
import com.vinzy.cataddons.modules.settings.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class GuiUtilsModule extends Module {

    public final BooleanSetting saveGuiSetting = register(new BooleanSetting("SaveGui", true));
    public final BooleanSetting clearGuiSetting = register(new BooleanSetting("ClearGui", true));
    public final BooleanSetting disconnectAndSendSetting = register(new BooleanSetting("Disconnect", true));
    public final BooleanSetting drawSlotIds = register(new BooleanSetting("SlotIds", false));

    public void drawSlotId(DrawContext context, Slot slot) {
        var matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.scale(0.5f, 0.5f);
        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                Text.literal(String.valueOf(slot.id)),
                slot.x * 2,
                slot.y * 2,
                0xFFFF00FF,
                true
        );
        matrices.popMatrix();
    }

    public GuiUtilsModule() {
        super("GUIUtils", "Glitcha");
    }

}
