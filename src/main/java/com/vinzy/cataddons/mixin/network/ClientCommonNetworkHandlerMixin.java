package com.vinzy.cataddons.mixin.network;

import com.vinzy.cataddons.features.PacketPauseManager;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class ClientCommonNetworkHandlerMixin {

    @Inject(
            method = "sendPacket",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cataddons$pausePackets(Packet<?> packet, CallbackInfo ci) {
        if (PacketPauseManager.isPaused()) {
            PacketPauseManager.queue(packet);
            ci.cancel(); // prevent it from actually sending (shocked face emoji tone 2)
        }
    }
}
