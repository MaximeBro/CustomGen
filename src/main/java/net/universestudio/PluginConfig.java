package net.universestudio;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PluginConfig {

    private final String path = System.getProperty("user.dir") + "\\plugins\\CustomGen";
    private List<GenLocation> genLocations;
    private final Gson gson;

    private final JavaPlugin plugin;
    public PluginConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
        this.genLocations = new ArrayList<GenLocation>();

        File configDir = new File(this.path);
        if(!configDir.exists()) {
            try { configDir.mkdir(); } catch(Exception e) { e.printStackTrace(); }
        }

        File configFile = new File(this.path+ "\\saved_gen.data");
        if(!configFile.exists()) {
            try { configFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public void init() {
        this.loadConfig();
    }

    public List<GenLocation> getLocations() {
        return this.genLocations;
    }

    public void addLocation(GenLocation location) {
        this.genLocations.add(location);
        this.saveConfig();
    }

    public void removeLocation(GenLocation location) {
        this.genLocations.remove(location);
        this.saveConfig();
    }

    public void removeLocation(Location location) {
        GenLocation locationToDelete = null;
        for(GenLocation _location : this.genLocations) {
            if(location.getWorld().getUID().equals(_location.worldId)) {
                locationToDelete = _location;
                break;
            }
        }

        if(locationToDelete != null) {
            this.genLocations.remove(locationToDelete);
        }
        this.saveConfig();
    }



    private void loadConfig() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.path + "\\saved_gen.json"));
            String dataContent = "";
            String line = "";
            while((line = reader.readLine()) != null) {
                dataContent += line;
            }

            TypeToken listType = new TypeToken<List<GenLocation>>(){};
            gson.fromJson(dataContent, listType);
        } catch (Exception e) { e.printStackTrace(); }

    }

    private void saveConfig() {
        String dataContent = gson.toJson(this.genLocations);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.path + "\\saved_gen.json"));
            writer.write(dataContent);
            writer.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private World getWorld(String uid) {
        return this.plugin.getServer().getWorld(UUID.fromString(uid));
    }

    @SuppressWarnings({"Will be replaced by Json Serialization"})
    private void _saveConfig() {
        try {
            FileWriter writer = new FileWriter(this.path + "\\saved_gen.data");
            writer.write("");
            for(GenLocation location : this.genLocations) {
                writer.write(location.toString());
            }
            writer.close();
        } catch(Exception e) { e.printStackTrace(); }
    }

    @SuppressWarnings({"Will be replaced by Json Deserialization"})
    private void _loadConfig() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.path +"\\saved_gen.data"));
            String line;
            while((line = reader.readLine()) != null) {
                String[] values = line.split("</>");
                try {
                    UUID id = UUID.fromString(values[0]);

                    String[] coordinates = values[2].split(",");
                    Location location = new Location(this.getWorld(values[1]),
                            Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]),Double.parseDouble(coordinates[2]));

                    this.genLocations.add(new GenLocation(id, location));
                } catch (Exception e) {
                    System.out.println("§c[CUSTOM GEN] - Data mismatch or corruption !");
                    System.out.println("§cline : " + line);
                    System.out.println("§cGenId : " + values[0]);
                    e.printStackTrace();
                }
            }
        } catch(Exception e) { e.printStackTrace(); }
    }


    // Inner-class used for data-saving
    public static class GenLocation {
        private UUID id;
        private UUID worldId;
        private Location location;

        public GenLocation(Location location) {
            this.id = UUID.randomUUID();
            this.worldId = location.getWorld().getUID();
            this.location = location;
        }

        public GenLocation(UUID id, Location location) {
            this.id = id;
            this.location = location;
        }

        public String toString() {
            return this.id.toString() + "</>" + this.worldId + "</>" +
                    this.location.getX() + "," + this.location.getY() + "," +this.location.getZ() + "\n";
        }

        public Location getLocation() { return this.location; }
        public World getWorld() { return this.location.getWorld(); }
    }
}