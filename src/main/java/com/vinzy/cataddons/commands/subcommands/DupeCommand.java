package com.vinzy.cataddons.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.vinzy.cataddons.commands.CommandCat;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class DupeCommand {
    private DupeCommand() {}

    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("dupe")
                .executes(context -> {
                    CommandCat.sendMessage("§cAn unknown error has occurred.\n§7(UNKNOWN_PACKET_SENT)", false);
                    return 1;
                });
    }
}
