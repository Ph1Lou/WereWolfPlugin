package fr.ph1lou.werewolfplugin.save;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.statistiks.StatistiksUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LanguageLoader {

    public static void loadLanguage(WereWolfAPI game, String language){

        Main main = JavaPlugin.getPlugin(Main.class);
        main.getRegisterManager().getModulesRegister().forEach(addon -> {
            String defaultLanguages = addon.getMetaDatas().defaultLanguage();
            main.getRegisterManager().getAddon(addon.getAddonKey())
                    .ifPresent(javaPlugin -> game.getLanguageManager().loadTranslations(addon.getMetaDatas().key().split("\\.")[0],
                    loadTranslations(javaPlugin,
                    FileUtils_.loadContent(buildLanguageFile(javaPlugin, defaultLanguages, language)))));
        });
        StatistiksUtils.loadMessages();

    }

    private static Map<String, JsonValue> loadTranslations(Plugin plugin, String file) {

        try {
            JsonObject jsonObject = Json.parse(file).asObject();
            return loadTranslationsRec(plugin, "", jsonObject, new HashMap<>());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return new HashMap<>();


    }

    private static Map<String, JsonValue> loadTranslationsRec(Plugin plugin, String currentPath, JsonValue jsonValue, Map<String, JsonValue> keys) {

        Main main = JavaPlugin.getPlugin(Main.class);

        // This value is an object - it means she contains sub-section that should be analyzed
        if (jsonValue.isObject()) {

            // For each child
            for (JsonObject.Member member : jsonValue.asObject()) {

                if(currentPath.equals("") && !plugin.equals(main) && member.getName().equals("werewolf")){ //Nom de domaine réservé au plugin lg
                    Bukkit.getLogger().warning(String.format("Plugin %s try to load text file with werewolf.* key",plugin.getName()));
                    continue;
                }
                String newPath = String.format("%s%s%s", currentPath, currentPath.equals("") ? "" : ".", member.getName());

                loadTranslationsRec(plugin,newPath, member.getValue(), keys);
            }
        }

        else if (!jsonValue.isNull()) {
            keys.put(currentPath.toLowerCase(), jsonValue);
        }

        return keys;
    }

    private static File buildLanguageFile(Plugin plugin, String defaultLang, String lang) {

        File file = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator, lang + ".json");

        if (!file.exists()) {
            FileUtils_.copy(plugin.getResource(lang + ".json"), plugin.getDataFolder() + File.separator + "languages" + File.separator + lang + ".json");
        }
        if (!file.exists()) {
            FileUtils_.copy(plugin.getResource(defaultLang + ".json"), plugin.getDataFolder() + File.separator + "languages" + File.separator + defaultLang + ".json");
            return new File(plugin.getDataFolder() + File.separator + "languages" + File.separator, defaultLang + ".json");
        } else {
            String defaultText = FileUtils_.convert(plugin.getResource(defaultLang + ".json"));
            Map<String, JsonValue> fr = loadTranslations(plugin, defaultText);
            Map<String, JsonValue> custom = loadTranslations(plugin, FileUtils_.loadContent(file));
            JsonObject jsonObject = Json.parse(FileUtils_.loadContent(file)).asObject();

            for (String string : fr.keySet()) {
                if (!custom.containsKey(string)) {
                    JsonObject temp = jsonObject;
                    String tempString = string;
                    while (temp.get(tempString.split("\\.")[0]) != null) {
                        String temp2 = tempString.split("\\.")[0];
                        tempString = tempString.replaceFirst(temp2 + "\\.", "");
                        temp = temp.get(temp2).asObject();
                    }
                    String[] strings =tempString.split("\\.");

                    for (int i=0;i<strings.length-1;i++){
                        temp.set(strings[i],new JsonObject());
                        temp = temp.get(strings[i]).asObject();
                    }
                    temp.set(strings[strings.length - 1], fr.get(string));
                }
            }
            FileUtils_.saveJson(file, jsonObject);
        }
        return file;
    }


}
