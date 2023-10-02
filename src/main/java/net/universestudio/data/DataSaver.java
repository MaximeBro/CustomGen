package net.universestudio.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.universestudio.generators.GenInstance;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataSaver {
    private final String path;
    private List<GenInstance> genInstances;
    private final Gson gson;
    private final JavaPlugin plugin;
    public DataSaver(JavaPlugin plugin) {
        this.plugin = plugin;
        this.path = this.plugin.getDataFolder().getPath() + "\\Data";
        this.gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(GenInstance.class, new GenInstanceAdapter()).create();
        this.genInstances = new ArrayList<GenInstance>();

        File dataDir = new File(this.path);
        if(!dataDir.exists()) {
            try { dataDir.mkdir(); } catch(Exception e) { e.printStackTrace(); }
        }
    }

    public List<GenInstance> getInstances() { return this.genInstances; }

    public void init() {
        this.loadData();
    }

    public void addLocation(GenInstance location) {
        this.genInstances.add(location);
        this.saveData();
    }

    public void removeLocation(GenInstance location) {
        this.genInstances.remove(location);
        this.saveData();
    }

    public GenInstance getInstance(Location location) {
        for(GenInstance instance : this.genInstances) {
            if(instance.getLocation().equals(location)) {
                return instance;
            }
        }

        return null;
    }

    private void loadData() {
        try {
            File dataFile = new File(this.path + "\\generators_location.json");
            if(dataFile.exists()) {
                Reader reader = new FileReader(dataFile);
                this.genInstances = new ArrayList<GenInstance>(Arrays.asList(gson.fromJson(reader, GenInstance[].class)));
                reader.close();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void saveData() {
        try {
            TypeToken<ArrayList<GenInstance>> typeToken = new TypeToken<>(){};
            File dataFile = new File(this.path + "\\generators_location.json");
            dataFile.createNewFile();
            Writer writer = new FileWriter(dataFile, false);
            String data = gson.toJson(this.genInstances.toArray());
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (Exception e) { e.printStackTrace(); }
    }


    private final class GenInstanceAdapter extends TypeAdapter<GenInstance> {

        @Override
        public void write(JsonWriter writer, GenInstance instance) throws IOException {
            writer.beginObject();
            writer.name("id");
            writer.value(instance.getId().toString());
            writer.name("name");
            writer.value(instance.getName());
            writer.name("location");
            writer.value(instance.getLocation().serialize().toString());
            writer.name("worldId");
            writer.value(instance.getWorldId().toString());
            writer.endObject();
        }

        @Override
        public GenInstance read(JsonReader reader) throws IOException {
            GenInstance location = new GenInstance();
            reader.beginObject();
            String fieldName = "";

            while(reader.hasNext()) {
                JsonToken token = reader.peek();
                if(token.equals(JsonToken.NAME)) {
                    fieldName = reader.nextName();
                }

                if(fieldName.equals("id")) {
                    token = reader.peek();
                    location.setId(reader.nextString());
                }

                if(fieldName.equals("name")) {
                    token = reader.peek();
                    location.setName(reader.nextString());
                }

                if(fieldName.equals("location")) {
                    token = reader.peek();
                    location.setLocation(reader.nextString().replaceAll("[{}]", ""));
                }

                if(fieldName.equals("worldId")) {
                    token = reader.peek();
                    location.setWorldId(reader.nextString());
                }
            }

            reader.endObject();
            return location;
        }
    }
}