package com.vinzy.cataddons;

import com.vinzy.cataddons.commands.CommandCat;
import com.vinzy.cataddons.events.ChatEvent;
import com.vinzy.cataddons.events.GuiEvent;
import com.vinzy.cataddons.events.TickEvent;
import com.vinzy.cataddons.events.WorldEvent;
import com.vinzy.cataddons.features.ConfigManager;
import com.vinzy.cataddons.features.HudOverlay;
import com.vinzy.cataddons.features.ssidLogin.SessionManager;
import com.vinzy.cataddons.keybinds.*;
import com.vinzy.cataddons.modules.*;
import com.vinzy.cataddons.modules.Module;
import com.vinzy.cataddons.modules.glitcha.*;
import com.vinzy.cataddons.modules.misc.CapeModule;
import com.vinzy.cataddons.modules.movement.AutoSprintModule;
import com.vinzy.cataddons.modules.render.*;
import net.fabricmc.api.ModInitializer;
import static com.vinzy.cataddons.features.ssidLogin.SessionManager.*;

public class MainClient implements ModInitializer {

    public static ModuleManager MODULE_MANAGER;

    @Override
    public void onInitialize() {
        //SSID Login
        originalSession = SessionManager.getSession();
        currentSession = originalSession;
        overrideSession = true;

        //modules
        MODULE_MANAGER = new ModuleManager();
        MODULE_MANAGER.register(new EspModule());
        MODULE_MANAGER.register(new FullBrightModule());
        MODULE_MANAGER.register(new AutoSprintModule());
        MODULE_MANAGER.register(new PacketLoggerModule());
        MODULE_MANAGER.register(new CapeModule());
        MODULE_MANAGER.register(new MacroGuiSettingsModule());
        MODULE_MANAGER.register(new WatermarkModule());
        MODULE_MANAGER.register(new HidePlayersModule());
        MODULE_MANAGER.register(new AnySignModule());
        MODULE_MANAGER.register(new NickModule());
        MODULE_MANAGER.register(new GuiUtilsModule());
        MODULE_MANAGER.register(new TpsCounterModule());
        MODULE_MANAGER.register(new FreeLookModule());
        MODULE_MANAGER.register(new FreecamModule());

        //config
        ConfigManager.load();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Module freecam = MODULE_MANAGER.getModuleByName("Freecam");
            if (freecam != null && freecam.isEnabled()) freecam.setEnabled(false);
        }));
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::save));

        //events
        WorldEvent.register();
        GuiEvent.register();
        TickEvent.register();
        ChatEvent.register();

        //keybinds
        KeybindManager.registerTickHandler();
        PacketPauseKeybind.register();
        SaveGuiKeybind.register();
        RestoreGuiKeybind.register();
        GhostBlockKeybind.register();
        ClickGuiKeybind.register();
        FreeLookKeybind.register();

        //other
        HudOverlay.init();
        CommandCat.register();
    }
}
