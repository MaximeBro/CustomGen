package net.universestudio;

import net.universestudio.data.DataLoader;
import net.universestudio.data.DataSaver;
import net.universestudio.generators.GenGeneration;
import net.universestudio.generators.GenInstance;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Panda;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {

    public final DataSaver dataSaver;
    public final DataLoader dataLoader;
    public final Map<UUID, BukkitTask> pluginTasks;
    public final ConsoleCommandSender console;

    private int registeredGenerators;

    public Main() {
        this.dataSaver = new DataSaver(this);
        this.dataLoader = new DataLoader(this);
        this.pluginTasks = new HashMap<>();
        this.console = this.getServer().getConsoleSender();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.dataSaver.init();
        this.dataLoader.init();
        this.retrieveGenerators();

        getServer().getPluginManager().registerEvents(new GenListener(this), this);
        getCommand("customgen").setExecutor(new GenCommand(this));
        console.sendMessage(ChatColor.GREEN + "[CustomGen] Plugin enabled !");
        console.sendMessage(ChatColor.GREEN + "" + this.registeredGenerators + " custom generators loaded !");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        console.sendMessage(ChatColor.BLUE + "" + this.registeredGenerators + " generators tasks disabled !");
        console.sendMessage(ChatColor.BLUE + "[CustomGen] Plugin disabled !");
    }

    // Will transform back every registered dispenser as new generators
    private void retrieveGenerators() {
        for(GenInstance instance : this.dataSaver.getInstances()) {
            World world = instance.getWorld();
            if(this.getServer().getWorlds().contains(world)) {
                Block block = world.getBlockAt(instance.getLocation());
                if(block.getType() == Material.DISPENSER && block.getState() instanceof Dispenser genBlock) {
                    if(genBlock.getCustomName() != null && genBlock.getCustomName().equals("§eCustom Gen")) {
                        this.registerGenerator(genBlock, instance.getId());
                    }
                } else {
                    console.sendMessage( ChatColor.RED + "[CustomGen] - FAIL : block " + block);
                    console.sendMessage( ChatColor.RED + "[CustomGen] - FAIL : instance " + block.getLocation());
                    this.dataSaver.removeLocation(instance);
                }
            }
        }
    }

    public void registerGenerator(Dispenser genBlock, UUID id) {
        this.registeredGenerators++;
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(this, () -> {
            genBlock.getInventory().addItem(this.getRandomMaterial());
            genBlock.getWorld().playSound(genBlock.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);
        }, 0L,40L);

        this.pluginTasks.put(id, task);
    }

    private ItemStack getMaterialFromGeneration(String name) {
        GenGeneration generation = this.dataLoader.getGeneration(name);

        if(generation != null) {
            Map<Material, Double> generations = generation.getGeneration(). // Sorts map in natural order of values (percentages...)
                    entrySet().stream().sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            Random random = new Random();
            double percentage = random.nextDouble(100) + 1;

            ItemStack randomItem = null;
            for(Material key : generations.keySet()) {
                if(generations.get(key) <= percentage) {
                    randomItem = new ItemStack(key, 1);
                }
            }

            if(randomItem != null) { return randomItem; }
        }
        return new ItemStack(Material.COBBLESTONE, 1);
    }

    private ItemStack getRandomMaterial() {
        Random random = new Random();
        double percentage = random.nextDouble(100) + 1;

        if(percentage <= 0.1) {
            return new ItemStack(Material.ANCIENT_DEBRIS, 1);
        } else if(percentage <= 0.5) {
            return new ItemStack(Material.DIAMOND_ORE, 1);
        } else if(percentage <= 2) {
            return new ItemStack(Material.GOLD_ORE, 1);
        } else if(percentage <= 3.5) {
            return new ItemStack(Material.LAPIS_ORE, 1);
        } else if(percentage <= 5) {
            return new ItemStack(Material.REDSTONE_ORE, 1);
        } else if(percentage <= 10) {
            return new ItemStack(Material.IRON_ORE, 1);
        } else if(percentage <= 20) {
            return new ItemStack(Material.COAL_ORE, 1);
        } else if(percentage <= 58.9) {
            return new ItemStack(Material.STONE, 1);
        }

        return new ItemStack(Material.COBBLESTONE, 1);
    }
}
