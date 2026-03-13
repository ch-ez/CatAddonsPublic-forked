package com.vinzy.cataddons.mixin.network;

import com.vinzy.cataddons.features.PluginScanner;
import com.vinzy.cataddons.features.TPSDisplay;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"), cancellable = true)
    private void cataddons$onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        TPSDisplay.onWorldTimeUpdate();
    }

    @Inject(method = "onCommandSuggestions", at = @At("HEAD"))
    private void cataddons$onCommandSuggestions(CommandSuggestionsS2CPacket packet, CallbackInfo ci) {
        net.minecraft.client.MinecraftClient.getInstance().execute(() -> PluginScanner.onCommandSuggestions(packet));
    }

    @Inject(method = "onCommandTree", at = @At("HEAD"))
    private void cataddons$onCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
        net.minecraft.client.MinecraftClient.getInstance().execute(() -> PluginScanner.onCommandTree(packet));
    }
}