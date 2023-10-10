package net.universestudio.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.universestudio.CustomGen;
import net.universestudio.models.GenGeneration;
import net.universestudio.models.GenRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RecipeManager {

    private final JavaPlugin plugin;
    private final String path;
    private final Gson gson;
    private final List<GenRecipe> recipes;

    public RecipeManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.path = this.plugin.getDataFolder().getPath() + "\\Recipes";
        this.gson = new GsonBuilder().registerTypeAdapter(GenRecipe.class, new GenRecipeAdapter()).create();
        this.recipes = new ArrayList<>();

        File recipes = new File(this.path);
        if(!recipes.exists())
            recipes.mkdir();
    }

    public void init() {
        this.loadRecipes();
        this.registerRecipes();
    }

    private void loadRecipes() {
        try {
            for(File recipe : Path.of(this.path).toFile().listFiles()) {
                if(recipe.exists() && recipe.isFile()) {
                    Reader reader = new FileReader(recipe);
                    GenRecipe deserializedRecipe = gson.fromJson(reader, GenRecipe.class);
                    if(!this.recipes.contains(deserializedRecipe.getName())) {
                        this.recipes.add(deserializedRecipe);
                    }
                    reader.close();
                }
            }
        } catch (JsonSyntaxException | IOException e) { e.printStackTrace(); }
    }

    private void registerRecipes() {
        for(GenRecipe recipe : this.recipes)
            ((CustomGen)this.plugin).registerRecipe(recipe.toRecipe(this.plugin));
    }

    private final class GenRecipeAdapter extends TypeAdapter<GenRecipe> {

        @Override
        public void write(JsonWriter out, GenRecipe value) throws IOException { }

        @Override
        public GenRecipe read(JsonReader reader) throws IOException {
            GenRecipe recipe = new GenRecipe();
            reader.beginObject();
            String fieldName = "";

            while(reader.hasNext()) {
                JsonToken token = reader.peek();

                if(token.equals(JsonToken.NAME)) {
                    fieldName = reader.nextName();
                }

                if(fieldName.equals("name")) {
                    token = reader.peek();
                    recipe.setName(reader.nextString());
                }

                if(fieldName.equals("recipe")) {
                    token = reader.peek();

                    reader.beginArray();
                    List<String> list = new ArrayList<>();
                    while(reader.hasNext()) {
                        reader.beginObject();
                        reader.nextName();
                        list.add(reader.nextString().trim());
                        reader.endObject();
                    }
                    reader.endArray();
                    recipe.deserialize(list.toArray(String[]::new));
                }
            }

            reader.endObject();
            return recipe;
        }
    }
}
