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
	public Map<BordureLG, Integer> border_value = new HashMap<>();
	public Map<ToolLG, Boolean> tool_switch = new HashMap<>();
	public Map<RoleLG,Integer> role_count = new HashMap<>();
	
	private float strength_rate =1.5f;
	private float apple_rate =1.5f;
	private float flint_rate =0.3f;
	private float pearl_rate =0.3f;
	private int diamond_limit=17;
	private int player_required_before_voting_ends=10;
	private int xp_boost=5;

	public void getConfig(MainLG main, int number) {

		ConfigLG config_load=this;
		
		File file = new File(main.getDataFolder(), "save"+number+".json");
		
		if(file.exists()) {

			config_load=main.serialize.deserialize(main.filelg.loadContent(file));

			this.setDiamondLimit(config_load.getDiamondLimit());
			this.setStrengthRate(config_load.getStrengthRate());
			this.setPlayerRequiredVoteEnd(config_load.getPlayerRequiredVoteEnd());
			this.setApple_rate(config_load.getApple_rate());
			this.setFlint_rate(config_load.getFlint_rate());
			this.setPearl_rate(config_load.getPearl_rate());
			this.setXp_boost(config_load.getXp_boost());

			for(PlayerLG plg:main.playerlg.values()) {
				plg.setDiamondLimit(this.getDiamondLimit());
			}
		}
		for(RoleLG role:RoleLG.values()) {
			this.role_count.put(role, config_load.role_count.getOrDefault(role, 0));
		}
		
		for(ToolLG tool:ToolLG.values()) {
			this.tool_switch.put(tool, config_load.tool_switch.getOrDefault(tool, tool.getValue()));
		}
		
		for(TimerLG timer:TimerLG.values()) {
			this.value.put(timer, config_load.value.getOrDefault(timer, timer.getValue()));
		}	
		
		for(BordureLG border:BordureLG.values()) {
			this.border_value.put(border, config_load.border_value.getOrDefault(border, border.getValue()));
		}	
		
		main.filelg.save(file, main.serialize.serialize(this));
		
		main.score.setRole(0);
		
		for (RoleLG role:RoleLG.values()) {
			if(!role.equals(RoleLG.COUPLE)) {
				main.score.setRole(main.score.getRole()+this.role_count.get(role));
			}
		}
	}

	public int getDiamondLimit() {
		return this.diamond_limit;
	}

	public void setDiamondLimit(int diamond_limit) {
		this.diamond_limit = diamond_limit;
	}

	public float getStrengthRate() {
		return this.strength_rate;
	}

	public void setStrengthRate(float strength_rate) {
		this.strength_rate = strength_rate;
	}

	public int getPlayerRequiredVoteEnd() {
		return this.player_required_before_voting_ends;
	}

	public void setPlayerRequiredVoteEnd(int player_required_before_voting_ends) {
		this.player_required_before_voting_ends = player_required_before_voting_ends;
	}

	public float getPearl_rate() {
		return pearl_rate;
	}

	public void setPearl_rate(float pearl_rate) {
		this.pearl_rate = pearl_rate;
	}

	public float getFlint_rate() {
		return flint_rate;
	}

	public void setFlint_rate(float flint_rate) {
		this.flint_rate = flint_rate;
	}

	public float getApple_rate() {
		return apple_rate;
	}

	public void setApple_rate(float apple_rate) {
		this.apple_rate = apple_rate;
	}

	public int getXp_boost() {
		return xp_boost;
	}

	public void setXp_boost(int xp_boost) {
		this.xp_boost = xp_boost;
	}
}
