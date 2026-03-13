package com.vinzy.cataddons.events;

import com.vinzy.cataddons.MainClient;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;

public class ChatEvent {
    public static void register() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
        });
    }
}
