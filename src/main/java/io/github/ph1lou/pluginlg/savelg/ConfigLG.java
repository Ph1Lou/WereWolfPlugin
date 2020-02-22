package io.github.ph1lou.pluginlg.savelg;

import io.github.ph1lou.pluginlg.enumlg.BordureLG;
import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;

import java.io.File;

import java.util.HashMap;

import java.util.Map;



public class ConfigLG {
	
	
	public Map<TimerLG, Integer> value = new HashMap<>();
	public Map<BordureLG, Integer> bordurevalue = new HashMap<>();
	public Map<ToolLG, Boolean> tool_switch = new HashMap<>();
	public Map<RoleLG,Integer> rolecount = new HashMap<>();
	
	private float strenghrate=(float) 1.5;
	private int diamondlimit=17;

	public void getconfig(MainLG main, int numero) {
		
		
		ConfigLG configload=this;
		
		
		File file = new File(main.getDataFolder(), "savelg"+numero+".json");
		
		if(file.exists()) {
			configload=main.serialize.deserialize(main.filelg.loadContent(file));
			this.setDiamondlimit(configload.getDiamondlimit());
			this.setStrenghrate(configload.getStrenghrate());
			for(PlayerLG plg:main.playerlg.values()) {
				plg.setDiamondLimit(this.getDiamondlimit());
			}
		}
		for(RoleLG role:RoleLG.values()) {
			
			if(!configload.rolecount.containsKey(role)) {
				this.rolecount.put(role,0);
			}
			else this.rolecount.put(role,configload.rolecount.get(role));
		}
		
		for(ToolLG tool:ToolLG.values()) {
			if(!configload.tool_switch.containsKey(tool)) {
				this.tool_switch.put(tool,tool.getValue());
			}
			else this.tool_switch.put(tool,configload.tool_switch.get(tool));
		}
		
		for(TimerLG timer:TimerLG.values()) {
			if(!configload.value.containsKey(timer)) {
				this.value.put(timer,timer.getValue());
			}
			else this.value.put(timer,configload.value.get(timer));
		}	
		
		for(BordureLG bordure:BordureLG.values()) {
			if(!configload.bordurevalue.containsKey(bordure)) {
				this.bordurevalue.put(bordure,bordure.getValue());
			}
			else this.bordurevalue.put(bordure,configload.bordurevalue.get(bordure));
		}	
		
		main.filelg.save(file, main.serialize.serialize(this));
		
		main.score.setRole(0);
		
		for (RoleLG role:RoleLG.values()) {
			if(!role.equals(RoleLG.COUPLE)) {
				main.score.setRole(main.score.getRole()+this.rolecount.get(role));
			}
		}
	}

	public int getDiamondlimit() {
		return diamondlimit;
	}

	public void setDiamondlimit(int diamondlimit) {
		this.diamondlimit = diamondlimit;
	}

	public float getStrenghrate() {
		return strenghrate;
	}

	public void setStrenghrate(float strenghrate) {
		this.strenghrate = strenghrate;
	}
	
	
	
	
	
}
