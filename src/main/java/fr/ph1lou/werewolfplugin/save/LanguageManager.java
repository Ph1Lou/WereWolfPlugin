package fr.ph1lou.werewolfplugin.save;


import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.ILanguageManager;
import fr.ph1lou.werewolfapi.events.UpdateLanguageEvent;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LanguageManager implements ILanguageManager, Listener {

    private final Main main;
    private final Map<String, JsonValue> extraTexts = new HashMap<>();
    private final Map<String, JsonValue> language = new HashMap<>();


    public LanguageManager(Main main) {
        this.main = main;
    }

    private Map<String, JsonValue> loadTranslations(String file) {

        try {
            JsonObject jsonObject = Json.parse(file).asObject();
            return this.loadTranslationsRec("", jsonObject, new HashMap<>());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return new HashMap<>();


    }

    private Map<String, JsonValue> loadTranslationsRec(String currentPath, JsonValue jsonValue, Map<String, JsonValue> keys) {
        // This value is an object - it means she contains sub-section that should be analyzed
        if (jsonValue.isObject()) {

            // For each child
            for (JsonObject.Member member : jsonValue.asObject()) {

                String newPath = String.format("%s%s%s", currentPath, currentPath.equals("") ? "" : ".", member.getName());

                this.loadTranslationsRec(newPath, member.getValue(), keys);
            }
        }

        else if (!jsonValue.isNull()) {
            keys.put(currentPath.toLowerCase(), jsonValue);
        }

        return keys;
    }


    @EventHandler(priority = EventPriority.LOW)
    private void updateLanguage(UpdateLanguageEvent event) {
        this.language.clear();
        this.language.putAll(loadTranslations(FileUtils_.loadContent(buildLanguageFile(main, "fr"))));
        this.extraTexts.clear();

        this.main.getRegisterManager().getAddonsRegister().forEach(addon -> {
            String defaultLanguages = addon.getDefaultLanguage();
            this.extraTexts.putAll(loadTranslations(FileUtils_.loadContent(buildLanguageFile(addon.getPlugin(), defaultLanguages))));
        });
    }

    private File buildLanguageFile(Plugin plugin, String defaultLang) {

        String lang = main.getConfig().getString("lang");
        File file = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator, lang + ".json");


        if (!file.exists()) {
            FileUtils_.copy(plugin.getResource(lang + ".json"), plugin.getDataFolder() + File.separator + "languages" + File.separator + lang + ".json");
        }
        if (!file.exists()) {
            FileUtils_.copy(plugin.getResource(defaultLang + ".json"), plugin.getDataFolder() + File.separator + "languages" + File.separator + defaultLang + ".json");
            return new File(plugin.getDataFolder() + File.separator + "languages" + File.separator, defaultLang + ".json");
        } else {
            String defaultText = FileUtils_.convert(plugin.getResource(defaultLang + ".json"));
            Map<String, JsonValue> fr = loadTranslations(defaultText);
            Map<String, JsonValue> custom = loadTranslations(FileUtils_.loadContent(file));
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

    public List<String> getTranslationList(String key, Formatter... formatters) {

        JsonArray array;
        if (!language.containsKey(key) || !language.get(key).isArray()) {
            if (this.extraTexts.containsKey(key) && this.extraTexts.get(key).isArray()) {
                array = this.extraTexts.get(key).asArray();
            }
            else{
                return Collections.singletonList("Array Message error");
            }
        }
        else{
            array = this.language.get(key).asArray();
        }
        return array.values()
                .stream().filter(JsonValue::isString)
                .map(JsonValue::asString)
                .map(s -> {
                    String message = s;
                    for(Formatter formatter:formatters){
                        message = formatter.handle(message);
                    }
                    return message;
                })
                .collect(Collectors.toList());
    }


    public String getTranslation(String key) {

        if (!this.language.containsKey(key) || !this.language.get(key).isString()) {

            if (this.extraTexts.containsKey(key) && this.extraTexts.get(key).isString()) {
                return this.extraTexts.get(key).asString();
            }
            return String.format("Message error (%s) ", key.toLowerCase());
        }
        return this.language.get(key).asString();
    }


}
