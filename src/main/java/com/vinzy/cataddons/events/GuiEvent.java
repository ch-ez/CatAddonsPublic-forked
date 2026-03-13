package com.vinzy.cataddons.events;

import com.vinzy.cataddons.commands.CommandCat;
import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.features.PacketPauseManager;
import com.vinzy.cataddons.features.SaveGuiManager;
import com.vinzy.cataddons.keybinds.PacketPauseKeybind;
import com.vinzy.cataddons.mixin.accessor.ScreenAccessor;
import com.vinzy.cataddons.modules.glitcha.GuiUtilsModule;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class GuiEvent {
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (MainClient.MODULE_MANAGER == null) return;
            if (MainClient.MODULE_MANAGER.isEnabled("GUIUtils")) {
                GuiUtilsModule mod = (GuiUtilsModule) MainClient.MODULE_MANAGER.getModuleByName("GUIUtils");
                if (!shouldAttachToScreen(screen)) {
                    return;
                }

                int x = 10;
                int y = 10;

                if(mod.saveGuiSetting.getValue()) {
                    ButtonWidget softCloseButton = ButtonWidget.builder(
                                    Text.literal("§aSave GUI and Close"),
                                    button -> SaveGuiManager.saveAndCloseGui()
                            )
                            .dimensions(x, y, 110, 20)
                            .tooltip(Tooltip.of(Text.of("Closes your GUI clientside and saves it to reopen later.")))
                            .build();
                    ((ScreenAccessor) screen).cataddons$addDrawableChild(softCloseButton);
                }

                if(mod.clearGuiSetting.getValue()) {
                    ButtonWidget clearGuiCacheButton = ButtonWidget.builder(
                                    Text.literal("§dClear GUI Cache"),
                                    button -> {
                                        Screen previousScreen = SaveGuiManager.savedScreen;
                                        if (previousScreen != null) {
                                            SaveGuiManager.savedScreen = null;
                                            SaveGuiManager.deadGui = false;
                                            CommandCat.sendMessage("Removed §b" + previousScreen.getTitle().getString() + "§f from saved screens.", true);
                                        } else {
                                            CommandCat.sendMessage("You do not have a currently saved GUI!", true);
                                        }

                                    }
                            )
                            .dimensions(x, y + 25, 110, 20)
                            .tooltip(Tooltip.of(Text.literal("Clears your saved GUI.")))
                            .build();
                    ((ScreenAccessor) screen).cataddons$addDrawableChild(clearGuiCacheButton);
                }

                if(mod.disconnectAndSendSetting.getValue()) {
                    ButtonWidget disconnectAndSendButton = ButtonWidget.builder(
                                    Text.literal("§cDC & Send Packets"),
                                    btn -> {
                                        if (client.getNetworkHandler() == null) return;

                                        if (PacketPauseManager.isPaused()) {
                                            PacketPauseKeybind.handleToggle();
                                        }

                                        TickEvent.pendingDisconnectTicks = 1;
                                    }
                            ).dimensions(x, y + 50, 110, 20)
                            .tooltip(Tooltip.of(Text.literal("Sends all currently queued packets (if there's any) and disconnects you from the server.")))
                            .build();
                    ((ScreenAccessor) screen).cataddons$addDrawableChild(disconnectAndSendButton);
                }
            }
        });
    }


    private static boolean shouldAttachToScreen(Screen screen) {
        return screen instanceof HandledScreen<?>
                || screen instanceof SignEditScreen
                || screen instanceof BookScreen
                || screen instanceof BookEditScreen;
    }
}
