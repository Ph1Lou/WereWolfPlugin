package io.github.ph1lou.pluginlg.savelg;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;

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

    public void init(MainLG main){
        for(String lang:languages){
            FileLG.copy(main.getResource(lang+".json"),main.getDataFolder()+File.separator+"languages"+File.separator+lang+".json");
        }
        main.textEN=SerializerLG.deserializeText(FileLG.loadContent(new File(main.getDataFolder() + File.separator + "languages" + File.separator, "en.json")));
        main.textFR=SerializerLG.deserializeText(FileLG.loadContent(new File(main.getDataFolder() + File.separator + "languages" + File.separator, "fr.json")));
        main.textEN.getTextTranslate(main);

        String defaultLang = main.getConfig().getString("lang");

        if(defaultLang.equals("fr")){
            main.defaultLanguage=main.textFR;
        }
        else main.defaultLanguage=main.textEN;
    }




    public void changeLanguage(GameManager game){

        String langName = game.getLang();
        TextLG text;
        if(langName.equals("fr")){
            game.setText(main.textFR);
        }
        else if(langName.equals("en")){
            game.setText(main.textEN);
        }
        else{
            File default_text = new File(main.getDataFolder() + File.separator + "languages" + File.separator, "custom.json");
            if (!default_text.exists()) {
                FileLG.copy(main.getResource(  "fr.json"), main.getDataFolder() +  File.separator +"languages"+ File.separator +"custom.json");
            }
            text=SerializerLG.deserializeText(FileLG.loadContent(default_text));
            text.getTextTranslate(main);
            game.setText(text);
        }
        game.optionlg.initInv();
    }
}
