package net.universestudio.commands;

import net.universestudio.CustomGen;
import net.universestudio.models.GenGeneration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class GenTabCompleter implements TabCompleter {
    private CustomGen plugin;

    public GenTabCompleter(CustomGen plugin) {
        this.plugin =  plugin;
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

        List<String> completion = new ArrayList<>();
        if(args.length == 1) {
            completion.add("list");
            completion.add("tasks");
            completion.add("version");
            completion.add("generation");
            completion.add("help");
            completion.add("give");

            return completion;
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("generation") || args[0].equalsIgnoreCase("give"))
                for(GenGeneration generation : this.plugin.generationManager.getGenerations())
                    completion.add(generation.getName());

            return completion;
        }

        return null;
    }
}
