package net.universestudio;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class Main extends JavaPlugin {

    public final PluginConfig pluginConfig;

    public Main() { this.pluginConfig = new PluginConfig(this); }

    @Override
    public void onEnable() {
        super.onEnable();
        this.pluginConfig.init();
        this.retrieveGenerators();

        getServer().getPluginManager().registerEvents(new GenListener(this), this);
        getCommand("customgen").setExecutor(new GenCommand(this));
        System.out.println("§aCustomGen enabled !");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        System.out.println("§bCustomGen disabled !");
    }

    // Will transform back every registered dispenser registered as a new gen
    private void retrieveGenerators() {
        for(PluginConfig.GenLocation location : this.pluginConfig.getLocations()) {
            World world = location.getWorld();
            if(this.getServer().getWorlds().contains(world)) {
                Block block = world.getBlockAt(location.getLocation());
                if(block.getType() == Material.DISPENSER && block.getState() instanceof Dispenser genBlock)
                    if(genBlock.getCustomName() != null && genBlock.getCustomName().equals("§eGénérateur Custom"))
                        this.registerGenerator(genBlock, false);
            } else {
                System.out.println("[CustomGen] - RETRIEVE : world " + location.getWorld().getName() + " unknown");
                this.pluginConfig.removeLocation(location);
            }
        }
    }

    public void registerGenerator(Dispenser genBlock, boolean restore) {
        if(restore) {
            this.pluginConfig.addLocation(new PluginConfig.GenLocation(genBlock.getLocation()));
        }
        Bukkit.getScheduler().runTaskTimer(this, () -> genBlock.getInventory().addItem(this.getRandomMaterial()), 0L,40L);
    }

    private ItemStack getRandomMaterial() {
        Random random = new Random();
        double percentage = random.nextDouble(100) + 1;

        if(percentage <= 0.1) {
            return new ItemStack(Material.ANCIENT_DEBRIS);
        } else if(percentage <= 0.5) {
            return new ItemStack(Material.DIAMOND_ORE);
        } else if(percentage <= 2) {
            return new ItemStack(Material.GOLD_ORE);
        } else if(percentage <= 3.5) {
            return new ItemStack(Material.LAPIS_ORE);
        } else if(percentage <= 5) {
            return new ItemStack(Material.REDSTONE_ORE);
        } else if(percentage <= 10) {
            return new ItemStack(Material.IRON_ORE);
        } else if(percentage <= 20) {
            return new ItemStack(Material.COAL_ORE);
        } else if(percentage <= 58.9) {
            return new ItemStack(Material.STONE);
        }

        return new ItemStack(Material.COBBLESTONE);
    }
}
