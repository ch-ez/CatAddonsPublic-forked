package com.vinzy.cataddons.commands.subcommands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.vinzy.cataddons.commands.CommandCat;
import com.vinzy.cataddons.features.ConfigManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public final class ReloadConfigCommand {
    private ReloadConfigCommand() {}

    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("reloadConfig")
                .executes(c -> {
                    ConfigManager.load();
                    CommandCat.sendMessage("Reloaded config.", true);
                    return 1;
                });
    }
}