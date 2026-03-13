package com.vinzy.cataddons.commands.subcommands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.vinzy.cataddons.features.GhostBlock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public final class RestoreGhostsCommand {
    private RestoreGhostsCommand() {}

    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("restoreGhosts")
                .executes(context -> {
                    GhostBlock.restoreGhosts();
                    return 1;
                });
    }
}

