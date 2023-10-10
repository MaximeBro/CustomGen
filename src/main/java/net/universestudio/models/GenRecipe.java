package net.universestudio.models;

import net.universestudio.CustomGen;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class GenRecipe {

    private String name;
    private String[] lines;
    private Map<Material, Character> mapping;

    public GenRecipe() {
        this.mapping = new HashMap<>();
    }

    public String getName() { return this.name; }
    public String[] getLines() { return this.lines; }
    public Map<Material, Character> getMapping() { return this.mapping; }

    public void setName(String name) { this.name = name; }
    public void deserialize(String[] lines) {
        int ingredient = 'A';
        this.lines = lines;
        for(String line : this.lines) {
            for(String material : line.split(" ")) {
                Material mat = Enum.valueOf(Material.class, material);
                if(!this.mapping.containsKey(mat)) {
                    this.mapping.put(mat, (char)ingredient);
                    ingredient++;
                }
            }
        }
    }

    public ShapedRecipe toRecipe(JavaPlugin plugin) {
        if(this.name == null || this.lines == null || this.lines.length < 3) {
            return null;
        }

        ItemStack generator = new ItemStack(Material.DISPENSER);
        ItemMeta meta = generator.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name);
        generator.setItemMeta(meta);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, this.name), generator);

        String[] shape = this.getShape();
        recipe.shape(shape[0], shape[1], shape[2]);
        for(Material key : mapping.keySet())
            recipe.setIngredient(mapping.get(key), key);

        return recipe;
    }

    public String[] getShape() {
        String[] shape = new String[3];
        shape[0] = "";
        shape[1] = "";
        shape[2] = "";
        for(int i=0; i < 3; i++) {
            for(String val : this.lines[i].split(" ")) {
                shape[i] += "" + this.mapping.get(Enum.valueOf(Material.class, val.trim()));
            }
        }

        return shape;
    }
}
