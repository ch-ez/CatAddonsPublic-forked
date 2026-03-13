package com.vinzy.cataddons.features;

import com.vinzy.cataddons.commands.CommandCat;
import com.vinzy.cataddons.mixin.accessor.ClientPlayNetworkHandlerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;

import java.util.*;

public class PluginScanner {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Random RANDOM = new Random();

    private static boolean scanning = false;
    private static int ticksWaiting = 0;

    private static final List<String> treePlugins = new ArrayList<>();
    private static final List<String> foundPlugins = new ArrayList<>();

    private static final Set<String> VERSION_ALIASES = Set.of(
            "version", "ver", "about", "bukkit:version", "bukkit:ver", "bukkit:about"
    );
    private static String versionAlias = null;

    private static final Set<String> ANTICHEAT_LIST = Set.of(
            "nocheatplus", "negativity", "warden", "horizon", "vulcan",
            "spartan", "grimac", "matrix", "kauri", "themis", "intave",
            "anticheat", "witherac", "godseye", "coreprotect", "wraith",
            "antixrayheuristics", "anticheatreloaded", "exploitsx",
            "foxaddition", "guardianac", "ggintegrity", "lightanticheat",
            "anarchyexploitfixes", "abc", "illegalstack"
    );

    public static void onCommandTree(CommandTreeS2CPacket packet) {
        treePlugins.clear();
        versionAlias = null;

        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        if (handler == null) return;

        ClientPlayNetworkHandlerAccessor accessor = (ClientPlayNetworkHandlerAccessor) handler;

        packet.getCommandTree(
                CommandRegistryAccess.of(
                        accessor.cataddons$getCombinedDynamicRegistries(),
                        accessor.cataddons$getEnabledFeatures()
                ),
                ClientPlayNetworkHandlerAccessor.cataddons$getCommandNodeFactory()
        ).getChildren().forEach(node -> {
            String name = node.getName();
            if (name == null) return;

            String[] split = name.split(":");
            if (split.length > 1 && !treePlugins.contains(split[0])) {
                treePlugins.add(split[0]);
            }

            if (versionAlias == null && VERSION_ALIASES.contains(name)) {
                versionAlias = name;
            }
        });
    }

    public static void startScan() {
        if (mc.getNetworkHandler() == null || mc.player == null) return;

        if (scanning) {
            CommandCat.sendMessage("Already scanning plugins, silly!", true);
            return;
        }

        foundPlugins.clear();
        scanning = true;
        ticksWaiting = 0;

        CommandCat.sendMessage("Starting plugin scan...", true);

        String cmd = versionAlias != null ? versionAlias : "ver";
        mc.getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(RANDOM.nextInt(200), cmd + " "));
    }

    public static void onTick() {
        if (!scanning) return;

        ticksWaiting++;
        if (ticksWaiting >= 100) {
            printResults();
        }
    }

    public static void onCommandSuggestions(CommandSuggestionsS2CPacket packet) {
        if (!scanning) return;

        scanning = false;
        var suggestions = packet.getSuggestions().getList();

        if (!suggestions.isEmpty()) {
            for (var s : suggestions) {
                String name = s.getText().trim();
                if (!name.isEmpty() && !foundPlugins.contains(name)) {
                    foundPlugins.add(name);
                }
            }
        }

        printResults();
    }

    private static void printResults() {
        scanning = false;
        ticksWaiting = 0;
        if (mc.player == null) return;

        for (String p : treePlugins) {
            if (!foundPlugins.contains(p)) {
                foundPlugins.add(p);
            }
        }

        if (foundPlugins.isEmpty()) {
            CommandCat.sendMessage("§cServer blocked the request or no plugins found.", true);
            return;
        }

        Collections.sort(foundPlugins);

        StringBuilder sb = new StringBuilder();
        sb.append("§fFound §b").append(foundPlugins.size()).append(" §fplugins: ");

        for (int i = 0; i < foundPlugins.size(); i++) {
            String plugin = foundPlugins.get(i);
            if (isAnticheat(plugin)) {
                sb.append("§9").append(plugin);
            } else {
                sb.append("§a").append(plugin);
            }
            if (i < foundPlugins.size() - 1) sb.append("§7, ");
        }

        CommandCat.sendMessage(sb.toString(), true);
    }

    private static boolean isAnticheat(String name) {
        String n = name.toLowerCase();
        return ANTICHEAT_LIST.contains(n) || n.contains("exploit") || n.contains("anti") || n.contains("shield");
    }
}