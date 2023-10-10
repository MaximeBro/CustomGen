package net.universestudio;

import net.universestudio.commands.CommandManager;
import net.universestudio.data.GenerationManager;
import net.universestudio.data.InstanceManager;
import net.universestudio.data.RecipeManager;
import net.universestudio.models.GenGeneration;
import net.universestudio.models.GenInstance;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class CustomGen extends JavaPlugin {
    public final InstanceManager instanceManager;
    public final GenerationManager generationManager;
    public final RecipeManager recipeManager;
    public final Map<UUID, BukkitTask> pluginTasks;
    public final ConsoleCommandSender console;
    private int registeredGenerators;
    private int registeredRecipes;
    private final Map<GenInstance, Location> corruptedData;

    public CustomGen() {
        this.instanceManager = new InstanceManager(this);
        this.generationManager = new GenerationManager(this);
        this.recipeManager = new RecipeManager(this);
        this.pluginTasks = new HashMap<>();
        this.console = this.getServer().getConsoleSender();
        this.corruptedData = new HashMap<>();
    }

    @Override
    public void onEnable() {
        this.instanceManager.init(); // Recover all the saved placed generations in every world
        this.generationManager.init(); // Recover all defined generations in the "Generators" folder
        this.recipeManager.init(); // Recover all defined recipes in the "Recipes" folder
        this.retrieveGenerators();
        CommandManager.registerCommands(this);

        getServer().getPluginManager().registerEvents(new GenListener(this), this);

        console.sendMessage(ChatColor.GREEN + "[CustomGen] Plugin enabled !");
        console.sendMessage(ChatColor.GREEN + "" + this.generationManager.getGenerations().size() + " custom generation(s) loaded !");
        console.sendMessage(ChatColor.GREEN + "" + this.registeredGenerators + " task(s) started !");
        console.sendMessage(ChatColor.GREEN + "" + this.registeredRecipes + " custom recipe(s) registered !");
    }

    @Override
    public void onDisable() {
        console.sendMessage(ChatColor.BLUE + "" + this.pluginTasks.size() + " generators tasks stopped !");
        console.sendMessage(ChatColor.BLUE + "[CustomGen] Plugin disabled !");
    }

    // Will transform back every registered dispenser as new generators
    private void retrieveGenerators() {
        for(GenInstance instance : this.instanceManager.getInstances()) {
            World world = instance.getWorld();
            if(this.getServer().getWorlds().contains(world)) {
                Block block = world.getBlockAt(instance.getLocation());
                if(block.getType() == Material.DISPENSER && block.getState() instanceof Dispenser genBlock) {
                    if(genBlock.getCustomName() != null && genBlock.getCustomName().replace("§e", "").equals(instance.getName())) {
                        this.registerGenerator(genBlock, instance.getId());
                        continue;
                    }
                }
            }
            this.corruptedData.put(instance, instance.getLocation());
        }

        for(Map.Entry<GenInstance, Location> entry : this.corruptedData.entrySet()) {
            this.removeGen(entry.getKey(), entry.getValue());
        }
        this.corruptedData.clear();
    }

    public void registerRecipe(ShapedRecipe recipe) {
        if(recipe == null) {
            this.console.sendMessage("§c[ERROR][REGISTRY] unknown recipe !");
            return;
        }

        this.registeredRecipes++;
        this.getServer().addRecipe(recipe);
    }

    public void registerGenerator(Dispenser genBlock, UUID id) {
        this.registeredGenerators++;
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(this, () -> {
            genBlock.getInventory().addItem(this.getRandomMaterial(genBlock.getCustomName().replace("§e", "")));
            genBlock.getWorld().playSound(genBlock.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);
        }, 0L,40L);

        this.pluginTasks.put(id, task);
    }

    private ItemStack getRandomMaterial(String name) {
        GenGeneration generation = this.generationManager.getGeneration(name);
        if(generation != null) {
            Map<Material, Double> generations = generation.getGeneration();
            Random random = new Random();
            double percentage = random.nextDouble() * 100;

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

    public <K, V extends Comparable<? super V>> Map<K, V> sortMap(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.<K, V>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private void removeGen(GenInstance instance, Location location) {
        console.sendMessage( ChatColor.RED + "[CustomGen] block " + location + " corrupted or got the wrong data. Pending deletion...");
        this.instanceManager.removeLocation(instance);
    }
}
