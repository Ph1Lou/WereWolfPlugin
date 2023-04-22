package fr.ph1lou.werewolfplugin.game;


import com.eclipsesource.json.JsonValue;
import fr.ph1lou.werewolfapi.game.ILanguageManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LanguageManager implements ILanguageManager {

    private final Map<String, Map<String, JsonValue>> texts = new HashMap<>();
    private final WereWolfAPI game;

    public LanguageManager(WereWolfAPI game) {
        this.game = game;
    }

    @Override
    public void loadTranslations(String key, Map<String, JsonValue> map) {
        this.texts.put(key, map);
        if (Main.KEY.startsWith(key)) { //Update ScoreBoard Title
            Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Entity::getUniqueId)
                    .filter(uuid -> ((GameManager) game).getBoards().containsKey(uuid))
                    .map(uuid -> ((GameManager) game).getBoards().get(uuid))
                    .forEach(fastBoard -> fastBoard.updateTitle(
                            game.translate("werewolf.score_board.title")));
        }
    }

    public List<String> getTranslationList(String key, Formatter... formatters) {

        String keyDomain = key.split("\\.")[0];

        if (this.texts.containsKey(keyDomain)) {

            Map<String, JsonValue> translations = this.texts.get(keyDomain);

            if (translations.containsKey(key)) {

                JsonValue jsonValue = translations.get(key);

                if (jsonValue.isArray()) {
                    return jsonValue
                            .asArray()
                            .values()
                            .stream().filter(JsonValue::isString)
                            .map(JsonValue::asString)
                            .map(s -> {
                                String message = s;
                                for (Formatter formatter : formatters) {
                                    message = formatter.handle(message);
                                }
                                return message;
                            })
                            .collect(Collectors.toList());
                } else {
                    return Collections.singletonList(String.format("Message %s is not an array", key));
                }
            }
        }
        return Collections.singletonList(String.format("Array %s Message not found", key));
    }


    public String getTranslation(String key, Formatter... formatters) {

        String keyDomain = key.split("\\.")[0];

        if (this.texts.containsKey(keyDomain)) {

            Map<String, JsonValue> translations = this.texts.get(keyDomain);

            if (translations.containsKey(key)) {

                JsonValue jsonValue = translations.get(key);

                if (jsonValue.isString()) {
                    String message = jsonValue.asString();
                    for (Formatter formatter : formatters) {
                        message = formatter.handle(message);
                    }
                    return message;
                } else {
                    return String.format("Message %s is not a string", key);
                }
            }
        }

        return String.format("Message %s not found", key);
    }
}
