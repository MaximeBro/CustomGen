package net.universestudio;

import net.universestudio.data.DataLoader;
import net.universestudio.data.DataSaver;
import net.universestudio.generators.GenGeneration;
import net.universestudio.generators.GenInstance;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomGen extends JavaPlugin {

    public final DataSaver dataSaver;
    public final DataLoader dataLoader;
    public final Map<UUID, BukkitTask> pluginTasks;
    public final ConsoleCommandSender console;
    private int registeredGenerators;
    private final Map<GenInstance, Location> corruptedData;

    public CustomGen() {
        this.dataSaver = new DataSaver(this);
        this.dataLoader = new DataLoader(this);
        this.pluginTasks = new HashMap<>();
        this.console = this.getServer().getConsoleSender();
        this.corruptedData = new HashMap<>();
    }

    @Override
    public void onEnable() {
        this.dataSaver.init();
        this.dataLoader.init();
        this.retrieveGenerators();

        getServer().getPluginManager().registerEvents(new GenListener(this), this);
        getCommand("customgen").setExecutor(new GenCommand(this));
        console.sendMessage(ChatColor.GREEN + "[CustomGen] Plugin enabled !");
        console.sendMessage(ChatColor.GREEN + "" + this.dataLoader.getGenerations().size() + " custom generation(s) loaded !");
        console.sendMessage(ChatColor.GREEN + "" + this.registeredGenerators + " custom task(s) started !");
    }

    @Override
    public void onDisable() {
        console.sendMessage(ChatColor.BLUE + "" + this.registeredGenerators + " generators tasks stopped !");
        console.sendMessage(ChatColor.BLUE + "[CustomGen] Plugin disabled !");
    }

    // Will transform back every registered dispenser as new generators
    private void retrieveGenerators() {
        for(GenInstance instance : this.dataSaver.getInstances()) {
            World world = instance.getWorld();
            if(this.getServer().getWorlds().contains(world)) {
                Block block = world.getBlockAt(instance.getLocation());
                if(block.getType() == Material.DISPENSER && block.getState() instanceof Dispenser genBlock) {
                    if(genBlock.getCustomName() != null && genBlock.getCustomName().equals(instance.getName())) {
                        this.registerGenerator(genBlock, instance.getId());
                    }else
                        this.corruptedData.put(instance, block.getLocation());
                } else
                    this.corruptedData.put(instance, block.getLocation());
            } else
                this.corruptedData.put(instance, instance.getLocation());
        }

        for(Map.Entry<GenInstance, Location> entry : this.corruptedData.entrySet()) {
            this.removeGen(entry.getKey(), entry.getValue());
        }
        this.corruptedData.clear();
    }

    public void registerGenerator(Dispenser genBlock, UUID id) {
        this.registeredGenerators++;
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(this, () -> {
            genBlock.getInventory().addItem(this.getRandomMaterial(genBlock.getCustomName()));
            genBlock.getWorld().playSound(genBlock.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);
        }, 0L,40L);

        this.pluginTasks.put(id, task);
    }

    private ItemStack getRandomMaterial(String name) {
        GenGeneration generation = this.dataLoader.getGeneration(name);

        if(generation != null) {
            Map<Material, Double> generations = generation.getGeneration();
            Random random = new Random();
            double percentage = random.nextDouble(100) + 1;

            ItemStack randomItem = null;
            for(Map.Entry<Material, Double> entry : generations.entrySet()) {
                if(percentage <= entry.getValue()) {
                    randomItem = new ItemStack(entry.getKey(), 1);
                }
            }
            if(randomItem != null) { return randomItem; }
        }

        return new ItemStack(Material.STONE, 1);
    }

    public Map<Material, Double> sortMap(Map<Material, Double> map, Comparator comparator) {

        Stream<Map.Entry<Material, Double>> sorted = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(comparator));

        map = sorted.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return map;
    }

    private void removeGen(GenInstance instance, Location location) {
        console.sendMessage( ChatColor.RED + "[CustomGen] block " + location + " corrupted or got the wrong data. Pending deletion...");
        this.dataSaver.removeLocation(instance);
    }
}
