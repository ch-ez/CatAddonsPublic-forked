package com.vinzy.cataddons.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class PacketPauseManager {
    private static boolean paused = false;
    private static final Queue<Packet<?>> packetQueue = new ConcurrentLinkedQueue<>();

    private PacketPauseManager() {}

    public static boolean isPaused() {
        return paused;
    }

    public static void toggle(ClientPlayNetworkHandler handler) {
        paused = !paused;
        if (!paused && handler != null) {
            flush(handler);
        }
    }

    public static void pause() {
        if (!paused) {
            paused = true;
        }
    }

    public static void resume() {
        if (paused) {
            paused = false;
            ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
            if (handler != null) {
                flush(handler);
            }
        }
    }

    public static void queue(Packet<?> packet) {
        packetQueue.add(packet);
    }

    public static void flush(ClientPlayNetworkHandler handler) {
        Packet<?> packet;
        while ((packet = packetQueue.poll()) != null) {
            handler.sendPacket(packet);
        }
    }

    public static Queue<Packet<?>> getPacketQueue() {
        return packetQueue;
    }

    public static void clear() {
        packetQueue.clear();
    }
}
