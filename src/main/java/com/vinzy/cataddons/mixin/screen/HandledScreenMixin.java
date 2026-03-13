package com.vinzy.cataddons.mixin.screen;

import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.commands.CommandCat;
import com.vinzy.cataddons.features.macrogui.GuiMacro;
import com.vinzy.cataddons.modules.glitcha.GuiUtilsModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Shadow protected int x;
    @Shadow protected int y;

    @Inject(
            method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
            at = @At("HEAD")
    )
    private void cataddons$onMouseClick(
            Slot slot,
            int slotId,
            int button,
            SlotActionType actionType,
            CallbackInfo ci
    ) {
        if (!GuiMacro.isRecording) return;
        if (slot == null) return;

        GuiMacro.recordClick(slot.id, actionType, button);
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void onGuiClosed(CallbackInfo ci) {
        if (GuiMacro.isRecording) {
            CommandCat.sendMessage("§cEnded§f recording. ", true);
            GuiMacro.finalizeRecording();
        }
    }
    @Inject(method = "drawSlot", at = @At("TAIL"))
    private void onDrawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        GuiUtilsModule mod = (GuiUtilsModule) MainClient.MODULE_MANAGER.getModuleByName("GUIUtils");
        if (mod == null) return;

        if (mod.isEnabled() && (boolean) mod.getSettingByName("SlotIds").getValue()) {
            mod.drawSlotId(context, slot);
            // CommandCat.sendMessage("drawing the slot id twin", true); it did draw
        }
    }
}