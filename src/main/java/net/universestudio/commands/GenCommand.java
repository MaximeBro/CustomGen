package net.universestudio.commands;

import net.universestudio.CustomGen;
import net.universestudio.generators.GenGeneration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class GenCommand implements CommandExecutor {
    public final CustomGen plugin;

    public GenCommand(CustomGen javaPlugin) {
        this.plugin = javaPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        // Checks command length
        if(sender instanceof Player player) {
            if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
                player.sendMessage(this.getHelpMessage());
                return true;
            }

            // Give command : give the specified number of generators (cap at 64), if not specified gives only one.
            if(args[0].equalsIgnoreCase("give") && args.length > 1) {

                String name = args[1];
                if(this.plugin.dataLoader.contains(name)) {
                    int nb = 1;
                    if(args.length > 2) {
                        try {
                            nb = Integer.parseInt(args[2]);
                            if(nb > 64) {
                                nb = 64;
                            }
                        } catch (Exception ignore) { nb = 1; }
                    }

                    ItemStack gen = new ItemStack(Material.DISPENSER, nb);
                    ItemMeta meta = gen.getItemMeta();
                    meta.setDisplayName("§e" + name);
                    gen.setItemMeta(meta);

                    player.getInventory().addItem(gen);
                    player.sendMessage("§b§lCustomGen §8- §ayou were given §7" + nb + " §b" + name + "§r §agenerator(s) !");
                    return true;
                } else {
                    player.sendMessage("§cUnknown generator !");
                    return false;
                }
            }

            // List command : gets available generation names
            if( args[0].equalsIgnoreCase("list")) {
                String genList = "§b§lCustomGen §8- §3Available generator(s) §b✔" + "§r";
                for(GenGeneration generation : this.plugin.dataLoader.getGenerations()) {
                    genList += "\n §8- §3" + generation.getName() + "§r";
                }

                player.sendMessage(genList);
                return true;
            }

            // Generation command : gets the generation of the given generation name
            if(args[0].equalsIgnoreCase("generation") && args.length > 1) {
                String name = args[1];
                if(this.plugin.dataLoader.contains(name)) {
                    String generation = "§b§lCustomGen §8- §3Generation of §b" + name + "§r\n";
                    Map<Material, Double> map = this.plugin.dataLoader.getGeneration(name).getGeneration();
                    for(Material key : map.keySet()) {
                        generation += "§8" + key.name() + "→ §7" + map.get(key).doubleValue() + "§6%" + "§r\n";
                    }

                    player.sendMessage(generation);
                    return true;
                }
                else {
                    player.sendMessage("§cUnknown generator !");
                    return false;
                }
            }

            // Tasks command : gets the number of running tasks (i.e. loaded generators).
            if(args[0].equalsIgnoreCase("tasks")) {
                player.sendMessage("§b§lCustomGen §8- §aRunning tasks " + "§7" + this.plugin.pluginTasks.size() + "§r");
                return true;
            }

            // Version command : gets the version of the plugin build
            if(args[0].equalsIgnoreCase("version")) {
                player.sendMessage("§b§lCustomGen §8- §aPlugin version §7§6v1.0.0 §r");
                return true;
            }
        }

        // Other ...
        return false;
    }

    private String getHelpMessage() {
        String message = "\n";
        message += "\n" + "§8§n                                                            " + "§r\n";
        message += "\n" + "\n" + "§b§lCustomGen §8- §3Plugin usage" + "§r\n";
        message += "\n" + "§3/customgen";
        message += "\n" + "§3/customgen help";
        message += "\n" + "§3/customgen list";
        message += "\n" + "§3/customgen generation §7<§7§ogenName§7>";
        message += "\n" + "§3/customgen give §7<§7§ogenName§7> §7<§7§oamount§7>";
        message += "\n" + "§3/customgen tasks";
        message += "\n" + "§3/customgen version" + "§r\n";
        message += "\n" + "§b✔ §7Plugin exclusively reserved to staff members";
        message += "\n" + "§8§n                                                            " + "\n§r ";

        return message;
    }
}
