package io.github.ph1lou.werewolfplugin.save;


import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import io.github.ph1lou.werewolfapi.LangManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.UpdateLanguageEvent;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Lang implements LangManager, Listener {

    private final Main main;
    private final Map<String, String> extraTexts = new HashMap<>();

    public Lang(Main main) {
        this.main = main;
    }


    private Map<String, String> loadTranslations(String file) {
        final JsonObject jsonObject = Json.parse(file).asObject();

        return this.loadTranslationsRec("", jsonObject, new HashMap<>());
    }

    private Map<String, String> loadTranslationsRec(String currentPath, JsonValue jsonValue, Map<String, String> keys) {
        // This value is an object - it means she contains sub-section that should be analyzed
        if (jsonValue.isObject()) {

            // For each child
            for (JsonObject.Member member : jsonValue.asObject()) {

                String newPath = String.format("%s%s%s", currentPath, currentPath.equals("") ? "" : ".", member.getName());

                this.loadTranslationsRec(newPath, member.getValue(), keys);
            }
        }

        else if (!jsonValue.isNull()) {
            keys.put(currentPath.toLowerCase(), jsonValue.asString());
        }

        return keys;
    }


    @EventHandler(priority = EventPriority.LOW)
    private void updateLanguage(UpdateLanguageEvent event) {

        WereWolfAPI game = main.getWereWolfAPI();
        game.getLanguage().clear();
        game.getLanguage().putAll(loadTranslations(FileUtils_.loadContent(buildLanguageFile(main, "fr"))));
        extraTexts.clear();

        main.getRegisterManager().getAddonsRegister().forEach(addon -> {
            String defaultLanguages = addon.getDefaultLanguage();
            extraTexts.putAll(loadTranslations(FileUtils_.loadContent(buildLanguageFile(addon.getPlugin(), defaultLanguages))));
        });
    }

    private File buildLanguageFile(Plugin plugin, String defaultLang) {

        String lang = main.getConfig().getString("lang");
        File file = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator, lang + ".json");
        String defaultText = FileUtils_.convert(plugin.getResource(defaultLang + ".json"));

        if (!file.exists()) {
            FileUtils_.copy(plugin.getResource(lang + ".json"), plugin.getDataFolder() + File.separator + "languages" + File.separator + lang + ".json");
        }
        if (!file.exists()) {
            assert defaultText != null;
            FileUtils_.saveJson(file, Json.parse(defaultText).asObject());
        } else {
            Map<String, String> fr = loadTranslations(defaultText);
            Map<String, String> custom = loadTranslations(FileUtils_.loadContent(file));
            final JsonObject jsonObject = Json.parse(FileUtils_.loadContent(file)).asObject();

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
                        temp=temp.get(strings[i]).asObject();
                    }
                    temp.set(strings[strings.length-1],fr.get(string));
                }
            }
            FileUtils_.saveJson(file, jsonObject);
        }
        return file;
    }

    @Override
    public Map<String, String> getExtraTexts() {
        return extraTexts;
    }
}
