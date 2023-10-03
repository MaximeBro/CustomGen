package net.universestudio;

import net.universestudio.generators.GenInstance;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitTask;

public class GenListener implements Listener {

    private final CustomGen plugin;

    public GenListener(CustomGen javaPlugin) {
        this.plugin = javaPlugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.getBlock().getType() == Material.DISPENSER) {
            Dispenser genBlock = (Dispenser) e.getBlock().getState();
            genBlock.getPersistentDataContainer();
            if(genBlock.getCustomName() != null && this.genExists(genBlock.getCustomName().replace("§e", ""))) {
                GenInstance instance = new GenInstance(genBlock.getCustomName().replace("§e", ""), genBlock.getLocation());
                this.plugin.dataSaver.addLocation(instance);
                this.plugin.registerGenerator(genBlock, instance.getId());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.getBlock().getType() == Material.DISPENSER) {
            Dispenser genBlock = (Dispenser) e.getBlock().getState();
            if(genBlock.getCustomName() != null && this.genExists(genBlock.getCustomName().replace("§e", ""))) {
                GenInstance instance = this.plugin.dataSaver.getInstance(genBlock.getLocation());
                this.plugin.dataSaver.removeLocation(instance);
                BukkitTask task = this.plugin.pluginTasks.remove(instance.getId());
                if(task != null) task.cancel();
            }
        }
    }

    private boolean genExists(String name) { return this.plugin.dataLoader.contains(name); }
}
