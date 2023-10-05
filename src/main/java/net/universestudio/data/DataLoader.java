package net.universestudio.data;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.universestudio.CustomGen;
import net.universestudio.generators.GenGeneration;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class DataLoader {

    private final JavaPlugin plugin;
    private final String path;
    private final Gson gson;
    private final List<GenGeneration> genGenerations;

    public DataLoader(JavaPlugin plugin) {
        this.plugin = plugin;
        this.path = this.plugin.getDataFolder().getPath() + "\\Generators";
        this.gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(GenGeneration.class, new GenGenerationAdapter()).create();
        this.genGenerations = new ArrayList<GenGeneration>();

        File dataDir = new File(this.path);
        if(!dataDir.exists()) {
            try { dataDir.mkdir(); } catch(Exception e) { e.printStackTrace(); }
        }
    }

    public List<GenGeneration> getGenerations() { return this.genGenerations; }

    public void init() {
        this.loadGenerations();
        this.sortGenerations();
    }

    private void loadGenerations() {
        try {
            for(File generation : Path.of(this.path).toFile().listFiles()) {
                if(generation.exists() && generation.isFile()) {
                    Reader reader = new FileReader(generation);
                    GenGeneration deserializedGen = gson.fromJson(reader, GenGeneration.class);
                    if(!this.contains(deserializedGen.getName())) {
                        this.genGenerations.add(deserializedGen);
                    }
                    reader.close();
                }
            }
        } catch(JsonSyntaxException | IOException e) { e.printStackTrace(); }
    }

    private void sortGenerations() {
        for(GenGeneration generation : this.genGenerations) {
            generation.setGeneration(((CustomGen)this.plugin).sortMap(generation.getGeneration()));
        }
    }

    public GenGeneration getGeneration(String name) {
        for(GenGeneration generation : this.genGenerations) {
            if(generation.getName().equals(name)) {
                return generation;
            }
        }

        return null;
    }

    public boolean contains(String name) {
        for(GenGeneration generation : this.genGenerations)
            if(generation.getName().equals(name))
                return true;

        return false;
    }

    private final class GenGenerationAdapter extends TypeAdapter<GenGeneration> {

        @Override
        public void write(JsonWriter writer, GenGeneration value) throws IOException {
            writer.beginObject();
            writer.name("name");
            writer.value(value.getName());
            writer.name("generation");
            writer.beginArray();
            for(Map.Entry<Material, Double> entry : value.getGeneration().entrySet()) {
                writer.beginObject();
                writer.name(entry.getKey().name());
                writer.value(entry.getValue().doubleValue());
                writer.endObject();
            }
            writer.endArray();
            writer.endObject();
        }

        @Override
        public GenGeneration read(JsonReader reader) throws IOException {
            GenGeneration generation = new GenGeneration();
            reader.beginObject();
            String fieldName = "";

            while(reader.hasNext()) {
                JsonToken token = reader.peek();
                if(token.equals(JsonToken.NAME)) {
                    fieldName = reader.nextName();
                }

                if(fieldName.equals("name")) {
                    token = reader.peek();
                    generation.setName(reader.nextString());
                }

                if(fieldName.equals("generation")) {
                    token = reader.peek();

                    reader.beginArray();
                    Map<Material, Double> map = new HashMap<>();
                    while(reader.hasNext()) {
                        reader.beginObject();
                        map.put(Enum.valueOf(Material.class, reader.nextName().trim()), Double.parseDouble(reader.nextString().trim()));
                        reader.endObject();
                    }
                    reader.endArray();

                    generation.setGeneration(map);
                }
            }

            reader.endObject();
            return generation;
        }
    }
}
