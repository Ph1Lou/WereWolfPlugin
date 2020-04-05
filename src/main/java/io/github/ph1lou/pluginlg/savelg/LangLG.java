package io.github.ph1lou.pluginlg.savelg;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.Title;
import io.github.ph1lou.pluginlg.commandlg.CommandLG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LangLG {

    final MainLG main;
    private final List<String> languages = new ArrayList<>(Arrays.asList("fr", "en"));

    public LangLG(MainLG main) {
        this.main = main;
    }

    public void initLanguage(){
        for(String lang:languages){
            main.filelg.copy(main.getResource(lang+".json"),main.getDataFolder()+File.separator+"languages"+File.separator+lang+".json");
        }
        getLanguage();
    }

    public void getLanguage(){

        String langName = main.getConfig().getString("lang");

        if(languages.contains(langName)){
            File default_text = new File(main.getDataFolder()+ File.separator +"languages"+ File.separator, langName+".json");
            main.text=main.serialize.deserializeText(main.filelg.loadContent(default_text));
        }
        else {
            File default_text = new File(main.getDataFolder() + File.separator +"languages"+ File.separator, "custom.json");
            if (!default_text.exists()) {
                main.filelg.copy(main.getResource(  "en.json"), main.getDataFolder() +  File.separator +"languages"+ File.separator +"custom.json");
                default_text = new File(main.getDataFolder() +  File.separator +"languages" + File.separator, "en.json");
                main.text = main.serialize.deserializeText(main.filelg.loadContent(default_text));
            }
            else {
                main.text = main.serialize.deserializeText(main.filelg.loadContent(default_text));
                main.text.getTextTranslate(main);
            }
        }
        for(Player p: Bukkit.getOnlinePlayers()){
            if(main.playerLG.containsKey(p.getName())){
                main.boards.get(p.getUniqueId()).updateTitle(main.text.getText(125));
                Title.sendTabTitle(p, main.text.getText(125), main.text.getText(184));
            }
        }
        main.getCommand("lg").setExecutor(new CommandLG(main));
        main.optionlg.initInv();
    }
}
