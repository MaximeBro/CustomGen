package net.universestudio.generators;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GenInstance {
    private UUID id;
    private String name;
    private UUID worldId;
    private Location location;

    public GenInstance(String name, Location location) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.worldId = location.getWorld().getUID();
        this.location = location;
    }

    public GenInstance() { }

    public UUID getId() { return this.id; }
    public String getName() { return this.name; }
    public Location getLocation() { return this.location; }
    public World getWorld() { return this.location.getWorld(); }
    public UUID getWorldId() { return this.getWorld().getUID(); }

    public String toString() {
        return this.id + " | " + this.location + " | " + this.worldId;
    }

    public void setId(String id) { this.id = UUID.fromString(id); }

    public void setName(String name) { this.name = name; }

    public void setWorldId(String id) {
        this.worldId = UUID.fromString(id);
    }

    public void setLocation(String location) {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] args = location.split(",");
        for(String value : args) {
            String[] values = value.split("=");
            map.put(values[0].trim(), values[1].trim());
        }

        this.location = Location.deserialize(map);
    }
}
