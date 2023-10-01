package net.universestudio.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.universestudio.generators.GenGeneration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    private final JavaPlugin plugin;
    private final String path;
    private final Gson gson;
    private final List<GenGeneration> genGenerations;

    public DataLoader(JavaPlugin plugin) {
        this.plugin = plugin;
        this.path = this.plugin.getDataFolder().getPath() + "\\Generators\\Custom";
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
        } catch(Exception e) { e.printStackTrace(); }
    }

    public GenGeneration getGeneration(String name) {
        for(GenGeneration generation : this.genGenerations) {
            if(generation.getName().equals(name)) {
                return generation;
            }
        }

        return null;
    }

    private boolean contains(String name) {
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
            writer.value(value.deserialize());
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
                    generation.setGeneration(generation.serialize(reader.nextString()));
                }
            }

            return generation;
        }
    }
}
