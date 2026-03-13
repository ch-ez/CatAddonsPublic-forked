package com.vinzy.cataddons.commands.subcommands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.vinzy.cataddons.features.GhostBlock;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.BlockStateArgument;

public final class ReplaceBlockCommand {
    private ReplaceBlockCommand() {}

    public static LiteralArgumentBuilder<FabricClientCommandSource> register(CommandRegistryAccess registryAccess) {
        return literal("replaceBlock")
                .then(argument("block", BlockStateArgumentType.blockState(registryAccess))
                        .executes(context -> {
                            BlockStateArgument blockArg = context.getArgument("block", BlockStateArgument.class);
                            Block block = blockArg.getBlockState().getBlock();
                            GhostBlock.replaceBlock(block);
                            return 1;
                        })
                );
    }
}