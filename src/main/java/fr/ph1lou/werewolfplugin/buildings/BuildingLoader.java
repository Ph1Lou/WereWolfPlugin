package fr.ph1lou.werewolfplugin.buildings;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import fr.ph1lou.werewolfapi.buildings.BuildingDefinition;
import fr.ph1lou.werewolfapi.buildings.BuildingType;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.save.FileUtils_;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BuildingLoader {

    private static final String FOLDER = "buildings";
    private static final String[] BUNDLED = {"vote_office", "council_chamber", "decorative_house", "sanctuary"};

    private final Map<BuildingType, List<BuildingDefinition>> definitions = new HashMap<>();

    public BuildingLoader() {
        for (BuildingType type : BuildingType.values()) {
            definitions.put(type, new ArrayList<>());
        }
    }

    public void load() {
        Main main = JavaPlugin.getPlugin(Main.class);
        extractDefaults(main);
        loadFromFolder(main);
    }

    private File folder(Main main) {
        return new File(main.getDataFolder(), FOLDER);
    }

    private void extractDefaults(Main main) {
        for (String name : BUNDLED) {
            File file = new File(folder(main), name + ".json");
            if (!file.exists()) {
                FileUtils_.copy(main.getResource(FOLDER + "/" + name + ".json"), file.getAbsolutePath());
            }
        }
    }

    private void loadFromFolder(Main main) {
        File dir = folder(main);
        File[] files = dir.listFiles((d, n) -> n.endsWith(".json"));
        if (files == null) {
            return;
        }
        for (File file : files) {
            String name = file.getName().substring(0, file.getName().length() - ".json".length());
            BuildingDefinition definition = parseFile(main, file, name);
            if (definition != null) {
                add(definition);
            }
        }
    }

    private BuildingDefinition parseFile(Main main, File file, String name) {
        try (Reader reader = new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)) {
            JsonObject json = Json.parse(reader).asObject();
            return BuildingDefinition.parse(json);
        } catch (Exception e) {
            main.getLogger().warning("[WereWolfPlugin] Failed to load building " + file.getName()
                    + ": " + e.getMessage() + ". Falling back to bundled default.");
            return parseBundled(main, name);
        }
    }

    private BuildingDefinition parseBundled(Main main, String name) {
        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(main.getResource(FOLDER + "/" + name + ".json")),
                StandardCharsets.UTF_8)) {
            JsonObject json = Json.parse(reader).asObject();
            return BuildingDefinition.parse(json);
        } catch (Exception e) {
            main.getLogger().warning("[WereWolfPlugin] No bundled default for building " + name + ": " + e.getMessage());
            return null;
        }
    }

    private void add(BuildingDefinition definition) {
        definitions.get(definition.getType()).add(definition);
    }

    public List<BuildingDefinition> getDefinitions(BuildingType type) {
        return definitions.get(type);
    }
}
