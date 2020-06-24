package io.github.ph1lou.werewolfplugin.savelg;


import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.commandlg.AdminLG;
import io.github.ph1lou.werewolfplugin.commandlg.CommandLG;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.game.OptionLG;
import io.github.ph1lou.werewolfplugin.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LangLG {

    final Main main;

    public LangLG(Main main) {
        this.main = main;
    }


    public Map<String, String> loadTranslations(final String file) {
        final JsonObject jsonObject = Json.parse(file).asObject();

        return this.loadTranslationsRec("", jsonObject, new HashMap<>());
    }

    private Map<String, String> loadTranslationsRec(final String currentPath, final JsonValue jsonValue, final Map<String, String> keys) {
        // This value is an object - it means she contains sub-section that should be analyzed
        if (jsonValue.isObject()) {

            // For each child
            for (JsonObject.Member member : jsonValue.asObject()) {

                final String newPath = String.format("%s%s%s", currentPath, currentPath.equals("") ? "" : ".", member.getName());

                this.loadTranslationsRec(newPath, member.getValue(), keys);
            }
        }

        // This value is a single string (not object), add-it to the list of translations (only if not null)
        else if (!jsonValue.isNull()) {
            keys.put(currentPath, jsonValue.asString());
        }

        return keys;
    }



    public void init(){

        FileLG.copy(main.getResource("fr.json"),main.getDataFolder()+File.separator+"languages"+File.separator+"fr.json");
        FileLG.copy(main.getResource("en.json"),main.getDataFolder()+File.separator+"languages"+File.separator+"en.json");

    }

    public void updateLanguage(GameManager game){

        String lang_select = main.getConfig().getString("lang");
        File file = new File(main.getDataFolder() + File.separator + "languages" + File.separator, lang_select+".json");
        File fileFR = new File(main.getDataFolder() + File.separator + "languages" + File.separator, "fr.json");

        if (!file.exists()){
            String content = FileLG.loadContent(fileFR);
            FileLG.save(file,content);
            game.getLanguage().clear();
            game.getLanguage().putAll(loadTranslations(content));
        }
        else {
            Map<String,String> fr =loadTranslations(FileLG.loadContent(fileFR));
            Map<String,String> custom =loadTranslations(FileLG.loadContent(file));
            final JsonObject jsonObject = Json.parse(FileLG.loadContent(file)).asObject();

            for(String string:fr.keySet()){
                if(!custom.containsKey(string)){
                    JsonObject temp = jsonObject;
                    String tempString = string;
                    while(temp.get(tempString.split("\\.")[0])!=null){
                        String temp2=tempString.split("\\.")[0];
                        tempString=tempString.replaceFirst(temp2+"\\.","");
                        temp=temp.get(temp2).asObject();
                    }
                    String[] strings =tempString.split("\\.");
                    for (int i=0;i<strings.length-1;i++){
                        temp.set(strings[i],new JsonObject());
                        temp=temp.get(strings[i]).asObject();
                    }
                    temp.set(strings[strings.length-1],fr.get(string));
                }
            }
            FileLG.saveJson(file,jsonObject);
            game.getLanguage().clear();
            game.getLanguage().putAll(loadTranslations(FileLG.loadContent(file)));
        }


        for(Player player:Bukkit.getOnlinePlayers()){
            Title.sendTabTitle(player, game.translate("werewolf.tab.top"), game.translate("werewolf.tab.bot"));
            if(game.boards.containsKey(player.getUniqueId())){
                game.boards.get(player.getUniqueId()).updateTitle(game.translate("werewolf.score_board.title"));
            }

            player.closeInventory();
        }
        game.optionlg=new OptionLG(main,game);
        main.getCommand("a").setExecutor(new AdminLG(main));
        main.getCommand("ww").setExecutor(new CommandLG(main,game));
    }
}
