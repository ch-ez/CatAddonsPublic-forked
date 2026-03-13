package com.vinzy.cataddons.features.ssidLogin;

import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.modules.glitcha.NickModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

import java.util.Optional;
import java.util.UUID;

public class SessionManager {
    public static Session originalSession;
    public static Session currentSession;
    public static boolean overrideSession;

    public static void restoreSession() {
        if (MainClient.MODULE_MANAGER.isEnabled("Nick")) {
            NickModule mod = (NickModule) MainClient.MODULE_MANAGER.getModuleByName("Nick");
            mod.username = originalSession.getUsername();
        }
        SessionManager.currentSession = SessionManager.originalSession;
    }

    public static Session getSession() {
        return MinecraftClient.getInstance().getSession();
    }

    public static String getUsername() {
        return MinecraftClient.getInstance().getSession().getUsername();
    }

    public static Session createSession(String username, String uuidString, String ssid) {
        if (uuidString.length() == 32) {
            uuidString =
                    uuidString.substring(0, 8) + "-" +
                            uuidString.substring(8, 12) + "-" +
                            uuidString.substring(12, 16) + "-" +
                            uuidString.substring(16, 20) + "-" +
                            uuidString.substring(20, 32);
        }

        //update username in nick idt this is very smart but like wtv bro
        if (MainClient.MODULE_MANAGER.isEnabled("Nick")) {
            NickModule mod = (NickModule) MainClient.MODULE_MANAGER.getModuleByName("Nick");
            mod.username = username;
        }

        return new Session(
                username,
                UUID.fromString(uuidString),
                ssid,
                Optional.empty(),
                Optional.empty()
        );
    }

    public static Session createSession(String username, UUID uuid, String ssid) {
        return new Session(username, uuid, ssid, Optional.empty(), Optional.empty());
    }

    public static void setSession(Session session) {
        currentSession = session;
    }

}
