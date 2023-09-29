package net.universestudio;

import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class GenListener implements Listener {

    private final Main plugin;

    public GenListener(Main javaPlugin) {
        this.plugin = javaPlugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.getBlock().getType() == Material.DISPENSER) {
            Dispenser genBlock = (Dispenser) e.getBlock().getState();
            if(genBlock.getCustomName() != null && genBlock.getCustomName().equals("§eGénérateur Custom")) {
                this.plugin.registerGenerator(genBlock, true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.getBlock().getType() == Material.DISPENSER) {
            Dispenser genBlock = (Dispenser) e.getBlock().getState();
            if(genBlock.getCustomName() != null && genBlock.getCustomName().equals("§eGénérateur Custom")) {
                this.plugin.pluginConfig.removeLocation(genBlock.getLocation());
            }
        }
    }
}
