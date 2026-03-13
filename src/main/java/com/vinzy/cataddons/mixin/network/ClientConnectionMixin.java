package com.vinzy.cataddons.mixin.network;

import com.vinzy.cataddons.commands.CommandCat;
import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.modules.render.FreecamModule;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static com.vinzy.cataddons.features.SaveGuiManager.deadGui;
import static com.vinzy.cataddons.features.SaveGuiManager.savedScreen;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    private static final Map<Class<? extends Packet<?>>, String> SEND_PACKETS = Map.of(
            CloseHandledScreenC2SPacket.class, "CloseHandledScreen",
            ClickSlotC2SPacket.class, "ClickSlot",
            UpdateSignC2SPacket.class, "UpdateSign"
    );

    private static final Map<Class<? extends Packet<?>>, String> RECEIVE_PACKETS = Map.of(
            OpenScreenS2CPacket.class, "OpenScreen",
            ScreenHandlerSlotUpdateS2CPacket.class, "SlotUpdate",
            InventoryS2CPacket.class, "InventorySync",
            SetPlayerInventoryS2CPacket.class, "SetPlayerInventory",
            SetCursorItemS2CPacket.class, "SetCursorItem",
            CloseScreenS2CPacket.class, "CloseScreen",
            OpenWrittenBookS2CPacket.class, "OpenWrittenBook"
    );
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    private void onSend(Packet<?> packet, CallbackInfo ci) {

        if (packet instanceof CloseHandledScreenC2SPacket && savedScreen != null) {
            MinecraftClient.getInstance().execute(() -> {
            if (!deadGui) {
                deadGui = true;
                CommandCat.sendMessage("§cYour saved GUI was closed by the client.", true);
                }
            });
        }

        if (MainClient.MODULE_MANAGER.getModuleByName("packetLogger").isEnabled()) {
            String name = SEND_PACKETS.get(packet.getClass());
            if (name == null) return;

            MinecraftClient.getInstance().execute(() ->
                    CommandCat.sendMessage("§c[C2S] " + name, true)
            );
        }
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
            at = @At("HEAD"))
    private void onReceive(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {

        if (packet instanceof DeathMessageS2CPacket death) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null && death.playerId() == mc.player.getId()) {
                mc.execute(() -> {
                    FreecamModule mod = (FreecamModule) MainClient.MODULE_MANAGER.getModuleByName("Freecam");
                    if (mod != null && mod.isEnabled()) mod.setEnabled(false);
                });
            }
        }

        if (packet instanceof PlayerRespawnS2CPacket) {
            MinecraftClient.getInstance().execute(() -> {
                FreecamModule mod = (FreecamModule) MainClient.MODULE_MANAGER.getModuleByName("Freecam");
                if (mod != null && mod.isEnabled()) mod.setEnabled(false);
            });
        }

        if (packet instanceof CloseScreenS2CPacket || packet instanceof OpenScreenS2CPacket && savedScreen != null) {
            MinecraftClient.getInstance().execute(() -> {
                if (!deadGui) {
                    deadGui = true;
                    CommandCat.sendMessage("§cYour saved GUI was closed by the server.", true);
                }
            });
        }

        if (MainClient.MODULE_MANAGER.getModuleByName("packetLogger").isEnabled()) {
            String name = RECEIVE_PACKETS.get(packet.getClass());
            if (name == null) return;
            MinecraftClient.getInstance().execute(() ->
                    CommandCat.sendMessage("§b[S2C] " + name, true)
            );
        }
    }
}
