package net.universestudio;

import net.universestudio.generators.GenInstance;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitTask;

public class GenListener implements Listener {

    private final Main plugin;
    private final NamespacedKey dataKey;

    public GenListener(Main javaPlugin) {
        this.plugin = javaPlugin;
        this.dataKey = new NamespacedKey(this.plugin, "genId");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.getBlock().getType() == Material.DISPENSER) {
            Dispenser genBlock = (Dispenser) e.getBlock().getState();
            genBlock.getPersistentDataContainer();
            if(genBlock.getCustomName() != null && genBlock.getCustomName().equals("§eCustom Gen")) {
                GenInstance instance = new GenInstance("overworld_ores", genBlock.getLocation());
                this.plugin.dataSaver.addLocation(instance);
                this.plugin.registerGenerator(genBlock, instance.getId());
                this.plugin.console.sendMessage(ChatColor.LIGHT_PURPLE + "" + instance.getLocation().getX() + ";" + instance.getLocation().getY() + ";" + instance.getLocation().getZ());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.getBlock().getType() == Material.DISPENSER) {
            Dispenser genBlock = (Dispenser) e.getBlock().getState();
            if(genBlock.getCustomName() != null && genBlock.getCustomName().equals("§eCustom Gen")) {
                GenInstance instance = this.plugin.dataSaver.getInstance(genBlock.getLocation());
                this.plugin.dataSaver.removeLocation(instance);
                BukkitTask task = this.plugin.pluginTasks.remove(instance.getId());
                if(task != null) task.cancel();
            }
        }
    }
}
