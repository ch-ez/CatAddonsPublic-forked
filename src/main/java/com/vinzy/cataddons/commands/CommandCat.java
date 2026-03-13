package com.vinzy.cataddons.commands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.commands.subcommands.*;
import com.vinzy.cataddons.utils.SoundUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public final class CommandCat {
    private CommandCat() {}

    public static void sendMessage(String message, Boolean prefix) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (!MainClient.MODULE_MANAGER.getModuleByName("packetLogger").isEnabled()) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(
                    SoundEvents.ENTITY_CAT_AMBIENT, 1f, 1f
            ));
        }
        if (player != null) {
            if (prefix) {
                player.sendMessage(Text.literal("§d§lcat §8>> §r§f" + message), false);
            } else {
                player.sendMessage(Text.literal(message), false);
            }
        }
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    literal("cataddons")
                            .executes(context -> {
                                sendMessage("meow", true);
                                return 1;
                            })
                            .then(RestoreGhostsCommand.register())
                            .then(DupeCommand.register())
                            .then(SetHandCommand.register(registryAccess))
                            .then(MacroGuiCommand.register())
                            .then(ToggleCommand.register())
                            .then(NbtCommand.register())
                            .then(ModuleCommand.register())
                            .then(ReloadConfigCommand.register())
                            .then(PluginsCommand.register())
                            .then(QuoteCommand.register())
                            .then(ReplaceBlockCommand.register(registryAccess))
            );
        });
    }
}