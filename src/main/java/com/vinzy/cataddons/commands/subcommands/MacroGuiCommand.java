package com.vinzy.cataddons.commands.subcommands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.vinzy.cataddons.commands.CommandCat;
import com.vinzy.cataddons.features.macrogui.GuiMacro;
import com.vinzy.cataddons.features.macrogui.MacroManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public final class MacroGuiCommand {
    private MacroGuiCommand() {}

    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("macrogui")
                .then(literal("record")
                        .then(argument("name", StringArgumentType.word())
                                .then(argument("gui", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            String gui = StringArgumentType.getString(ctx, "gui");
                                            GuiMacro.prepareRecording(name, gui);
                                            CommandCat.sendMessage("Recording macro §b" + name + "§f when GUI §3" + gui + " §fopens.", true);
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "name");
                                    GuiMacro.prepareRecording(name, "Any");
                                    CommandCat.sendMessage("Recording macro §b" + name + "§f on §3any GUI§f.", true);
                                    return 1;
                                })
                        )
                )
                .then(literal("run")
                        .then(argument("name", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    GuiMacro.getList().forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "name");
                                    GuiMacro.runMacro(name);
                                    CommandCat.sendMessage("Macro §b" + name + " §fhas been §aenabled.", true);
                                    return 1;
                                })
                        )
                )
                .then(literal("stop")
                        .executes(ctx -> {
                            MacroManager.stop();
                            CommandCat.sendMessage("All macros §cdisabled.", true);
                            return 1;
                        })
                )
                .then(literal("list")
                        .executes(ctx -> {
                            var list = GuiMacro.getList();
                            if (list.isEmpty()) {
                                CommandCat.sendMessage("No macros saved.", true);
                            } else {
                                CommandCat.sendMessage("Macros: §b" + String.join(", ", list), true);
                            }
                            return 1;
                        })
                )
                .then(literal("delete")
                        .then(argument("name", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    GuiMacro.getList().forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "name");
                                    GuiMacro.deleteMacro(name);
                                    CommandCat.sendMessage("Deleted macro §b" + name + ".", true);
                                    return 1;
                                })
                        )
                );
    }
}