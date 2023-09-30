package net.universestudio;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GenCommand implements CommandExecutor {
    public final Main plugin;

    public GenCommand(Main javaPlugin) {
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
            if(args[0].equalsIgnoreCase("give")) {

                int nb = 1;
                if(args.length > 1) {
                    try {
                        nb = Integer.parseInt(args[1]);
                        if(nb > 64) {
                            nb = 64;
                        }
                    } catch (Exception ignore) { nb = 1; }
                }

                ItemStack gen = new ItemStack(Material.DISPENSER, nb);
                ItemMeta meta = gen.getItemMeta();
                meta.setDisplayName("§eCustom Gen");
                gen.setItemMeta(meta);

                player.getInventory().addItem(gen);
                player.sendMessage("§b§lCustomGen §8- §ayou were given §7" + nb + " §acustom generators !");
                return true;
            }

            // Tasks command : gets the number of running tasks (i.e. loaded generators).
            if(args[0].equalsIgnoreCase("tasks")) {
                player.sendMessage("§b§lCustomGen §8- §aRunning tasks " + "§7§n" + this.plugin.pluginTasks.size() + "§r\n");
                return true;
            }


            if(args[0].equalsIgnoreCase("version")) {
                player.sendMessage("§b§lCustomGen §8- §aPlugin version §7§6v1.0.0 §r\n");
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
        message += "\n" + "§3/customgen give §7<§7§oamount§7>";
        message += "\n" + "§3/customgen tasks";
        message += "\n" + "§3/customgen version" + "§r\n";
        message += "\n" + "§b✔ §7Plugin exclusively reserved to staff members";
        message += "\n" + "§8§n                                                            " + "\n§r ";

        return message;
    }
}
