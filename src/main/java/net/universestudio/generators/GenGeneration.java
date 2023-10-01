package net.universestudio.generators;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GenGeneration {
    private String name;
    private Map<Material, Double> generation;

    public GenGeneration(String name, UUID instanceId) {
        this.name = name;
        this.generation = new HashMap<Material, Double>();
    }

    public GenGeneration() {}

    public String deserialize() {
        String val = "";
        for(Material key : this.generation.keySet()) {
            val += key.name() + "=" + this.generation.get(key).doubleValue() + ";";
        }

        return val;
    }

    public Map<Material, Double> serialize(String serialized) {
        Map<Material, Double> map = new HashMap<Material, Double>();
        String[] entrySet = serialized.split(";");
        for(String entry : entrySet) {
            String[] values = entry.split("=");
            Material material = Enum.valueOf(Material.class, values[0].trim());
            Double percentage = Double.parseDouble(values[1].trim());
            map.put(material, percentage);
        }

        return map;
    }

    // Getters & Setters
    public String getName() { return this.name; }
    public Map<Material, Double> getGeneration() { return this.generation; }

    public void setName(String name) { this.name = name; }
    public void setGeneration(Map<Material, Double> generation) { this.generation = generation; }
    public void setGeneration(Material key, Double val) { this.generation.put(key, val); }
}
