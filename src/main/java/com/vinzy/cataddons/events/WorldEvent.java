package com.vinzy.cataddons.events;

import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.features.CapeManager;
import com.vinzy.cataddons.features.GhostBlock;
import com.vinzy.cataddons.features.PacketPauseManager;
import com.vinzy.cataddons.modules.render.FreecamModule;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class WorldEvent {
    static MinecraftClient mc =  MinecraftClient.getInstance();
    public static void register() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleSynchronousResourceReloadListener() {

                    @Override
                    public Identifier getFabricId() {
                        return Identifier.of("cataddons", "cape_loader");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        CapeManager.reload(manager);
                    }
                });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (PacketPauseManager.isPaused()) {
                PacketPauseManager.toggle(mc.getNetworkHandler());
            }
            GhostBlock.clearGhosts();
            FreecamModule mod = (FreecamModule) MainClient.MODULE_MANAGER.getModuleByName("Freecam");
            if (mod != null && mod.isEnabled()) mod.setEnabled(false);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (PacketPauseManager.isPaused()) {
                PacketPauseManager.toggle(mc.getNetworkHandler());
            }
            GhostBlock.clearGhosts();
            FreecamModule mod = (FreecamModule) MainClient.MODULE_MANAGER.getModuleByName("Freecam");
            if (mod != null && mod.isEnabled()) mod.setEnabled(false);
        });
    }
}
