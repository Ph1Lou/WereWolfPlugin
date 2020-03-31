package io.github.ph1lou.pluginlg.savelg;


import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TextLG {
	
	public final Map<RoleLG, String> translateRole = new HashMap<>();
	public final Map<RoleLG, String> description = new HashMap<>();
	public final Map<RoleLG, String> powerUse = new HashMap<>();
	public final Map<RoleLG, String> powerHasBeenUse = new HashMap<>();
	public final Map<ToolLG, String> translateTool = new HashMap<>();
	public final Map<TimerLG, String> translateTimer = new HashMap<>();
	public final Map<BorderLG, String> translateBorder = new HashMap<>();
	public final Map<ScenarioLG, String> translateScenario = new HashMap<>();

	private final List<List<String>> DEFAULT = new ArrayList<>();

	
	public void getTextTranslate(MainLG main) {

		File file_text = new File(main.getDataFolder(),"languages/fr.json");
		TextLG text_load =main.serialize.deserializeText(main.filelg.loadContent(file_text));

		for(int k=0;k<text_load.DEFAULT.size();k++){
			for(int i=0;i<text_load.DEFAULT.get(k).size();i++) {
				if(this.DEFAULT.size()<=k){
					this.DEFAULT.add(new ArrayList<>());
				}
				if(i<this.DEFAULT.get(k).size()) {
					if(this.DEFAULT.get(k).get(i).length()==0){
						this.DEFAULT.get(k).set(i,text_load.DEFAULT.get(k).get(i));
					}
				}
				else this.DEFAULT.get(k).add(text_load.DEFAULT.get(k).get(i));
			}
		}
		for(RoleLG role:RoleLG.values()) {
			this.translateRole.put(role,this.translateRole.getOrDefault(role,text_load.translateRole.get(role)));
			this.description.put(role,this.description.getOrDefault(role,text_load.description.get(role)));
			this.powerUse.put(role,this.powerUse.getOrDefault(role,text_load.powerUse.get(role)));
			this.powerHasBeenUse.put(role,this.powerHasBeenUse.getOrDefault(role,text_load.powerHasBeenUse.get(role)));
		}
		for(ToolLG tool:ToolLG.values()) {
			this.translateTool.put(tool,this.translateTool.getOrDefault(tool,text_load.translateTool.get(tool)));
		}
		for(TimerLG timer:TimerLG.values()) {
			this.translateTimer.put(timer,this.translateTimer.getOrDefault(timer,text_load.translateTimer.get(timer)));
		}
		for(BorderLG border: BorderLG.values()) {
			this.translateBorder.put(border,this.translateBorder.getOrDefault(border,text_load.translateBorder.get(border)));
		}
		for(ScenarioLG scenario:ScenarioLG.values()) {
			this.translateScenario.put(scenario,this.translateScenario.getOrDefault(scenario,text_load.translateScenario.get(scenario)));
		}
		main.filelg.save(new File(main.getDataFolder(),"/languages/custom.json"), main.serialize.serializeText(this));
	}
	
	public String getText(int i) {
		if(DEFAULT.size()<3){
			return "Error";
		}
	return (DEFAULT.get(2).get(i));
	}

	public List<String> getScoreBoard1() {
		if (DEFAULT.isEmpty()){
			return new ArrayList<>(10);
		}
		return DEFAULT.get(0);
	}

	public List<String> getScoreBoard2() {
		if (DEFAULT.size()<2){
			return new ArrayList<>(10);
		}
		return DEFAULT.get(1);
	}

}
