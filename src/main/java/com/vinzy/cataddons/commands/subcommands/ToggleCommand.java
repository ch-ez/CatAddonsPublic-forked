package com.vinzy.cataddons.commands.subcommands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.vinzy.cataddons.commands.CommandCat;
import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.modules.Module;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public final class ToggleCommand {
    private ToggleCommand() {}

    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("toggle")
                .then(argument("module", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            MainClient.MODULE_MANAGER.getModules().forEach(m -> builder.suggest(m.getName()));
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String moduleName = StringArgumentType.getString(context, "module");
                            Module module = MainClient.MODULE_MANAGER.getModuleByName(moduleName);

                            if (module == null) {
                                CommandCat.sendMessage("§cModule not found.", true);
                                return 0;
                            }

                            module.toggle();
                            boolean moduleEnabled = module.isEnabled();
                            CommandCat.sendMessage("§b" + moduleName + "§f is now " + (moduleEnabled ? "§aenabled" : "§cdisabled") + ".", true);
                            return 1;
                        })
                );
    }
}