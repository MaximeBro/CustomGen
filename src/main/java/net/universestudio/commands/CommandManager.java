package net.universestudio.commands;

import net.universestudio.CustomGen;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager {

    public static void registerCommands(JavaPlugin plugin) {
        plugin.getCommand("customgen").setExecutor(new GenCommand((CustomGen) plugin));
        plugin.getCommand("customgen").setTabCompleter(new GenTabCompleter((CustomGen) plugin));
    }
}
