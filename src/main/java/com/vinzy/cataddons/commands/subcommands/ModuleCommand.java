package com.vinzy.cataddons.commands.subcommands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.vinzy.cataddons.commands.CommandCat;
import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.modules.Module;
import com.vinzy.cataddons.modules.settings.BooleanSetting;
import com.vinzy.cataddons.modules.settings.FloatSetting;
import com.vinzy.cataddons.modules.settings.ModeSetting;
import com.vinzy.cataddons.modules.settings.StringSetting;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public final class ModuleCommand {
    private ModuleCommand() {}

    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("module")
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

                            var settings = module.getSettings();
                            if (settings.isEmpty()) {
                                CommandCat.sendMessage("§b" + moduleName + " §fhas no settings.", true);
                                return 1;
                            }

                            CommandCat.sendMessage("§7Settings for §b" + moduleName + "§7:", true);
                            for (var setting : settings) {
                                CommandCat.sendMessage(" §8- §f" + setting.getName() + "§7: §b" + setting.getValue(), true);
                            }
                            return 1;
                        })
                        .then(argument("setting", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    String moduleName = StringArgumentType.getString(context, "module");
                                    Module module = MainClient.MODULE_MANAGER.getModuleByName(moduleName);
                                    if (module != null) {
                                        module.getSettings().forEach(s -> builder.suggest(s.getName()));
                                    }
                                    return builder.buildFuture();
                                })
                                .then(argument("value", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            String moduleName = StringArgumentType.getString(context, "module");
                                            String settingName = StringArgumentType.getString(context, "setting");
                                            String value = StringArgumentType.getString(context, "value");

                                            Module module = MainClient.MODULE_MANAGER.getModuleByName(moduleName);
                                            if (module == null) {
                                                CommandCat.sendMessage("§cModule not found.", true);
                                                return 0;
                                            }

                                            var setting = module.getSettingByName(settingName);
                                            if (setting == null) {
                                                CommandCat.sendMessage("§cSetting §f" + settingName + " §cnot found on §f" + moduleName + ".", true);
                                                return 0;
                                            }

                                            try {
                                                switch (setting) {
                                                    case FloatSetting fs -> fs.setValue(Float.parseFloat(value));
                                                    case BooleanSetting bs -> bs.setValue(Boolean.parseBoolean(value));
                                                    case ModeSetting ms -> ms.setValue(value);
                                                    case StringSetting ss -> ss.setValue(value);
                                                    default -> {
                                                        CommandCat.sendMessage("§cUnsupported setting type.", true);
                                                        return 0;
                                                    }
                                                }
                                                CommandCat.sendMessage("§b" + moduleName + " §fsetting§b " + settingName + " §fset to §b" + setting.getValue(), true);
                                            } catch (NumberFormatException e) {
                                                CommandCat.sendMessage("§cInvalid value §f" + value + " §cfor setting §f" + settingName + ".", true);
                                                return 0;
                                            }

                                            return 1;
                                        })
                                )
                        )
                );
    }
}