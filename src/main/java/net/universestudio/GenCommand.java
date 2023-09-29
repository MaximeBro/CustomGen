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
        if(args.length == 0) {
            return false;
        }

        // Give command
        if(args[0].equalsIgnoreCase("give") && sender instanceof Player player) {

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
            meta.setDisplayName("§eGénérateur Custom");
            gen.setItemMeta(meta);

            player.getInventory().addItem(gen);
        }

        // Other ...
        return false;
    }
}
