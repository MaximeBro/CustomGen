package net.universestudio.generators;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GenGeneration {
    private String name;
    private Map<Material, Double> generation;

    public GenGeneration(String name) {
        this.name = name;
        this.generation = new HashMap<Material, Double>();
    }

    public GenGeneration(String name, Map<Material, Double> generation) {
        this.name = name;
        this.generation = generation;
    }

    public GenGeneration() {}

    // Getters & Setters
    public String getName() { return this.name; }
    public Map<Material, Double> getGeneration() { return this.generation; }

    public void setName(String name) { this.name = name; }
    public void setGeneration(Map<Material, Double> generation) { this.generation = generation; }
    public void setGeneration(Material key, Double val) { this.generation.put(key, val); }
}
