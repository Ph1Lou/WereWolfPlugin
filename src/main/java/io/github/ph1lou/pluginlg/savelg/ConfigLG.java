package io.github.ph1lou.pluginlg.savelg;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigLG {
	public final Map<TimerLG, Integer> value = new HashMap<>();
	public final Map<BorderLG, Integer> border_value = new HashMap<>();
	public final Map<ToolLG, Boolean> tool_switch = new HashMap<>();
	public final Map<RoleLG,Integer> role_count = new HashMap<>();
	public final Map<ScenarioLG,Boolean> scenario = new HashMap<>();

	private int strength_rate =130;
	private int apple_rate =10;
	private int flint_rate =10;
	private int pearl_rate =30;
	private int xp_boost=500;
	private int player_required_before_voting_ends=10;
	private int diamond_limit=17;
	private int limitProtectionIron=3;
	private int limitProtectionDiamond=2;
	private int limitSharpnessDiamond=3;
	private int limitSharpnessIron=4;
	private int limitPowerBow=3;
	private int limitPunch=1;
	private int limitKnockBack=1;
	private int useOfFlair=4;
	private int goldenAppleParticles=1;

	public void getConfig(MainLG main,String configName) {

		ConfigLG config_load=this;
		
		File file = new File(main.getDataFolder()+"/configs/", configName+".json");

		if(file.exists()) {
			config_load=main.serialize.deserialize(main.filelg.loadContent(file));
			this.setDiamondLimit(config_load.getDiamondLimit());
			this.setStrengthRate(config_load.getStrengthRate());
			this.setPlayerRequiredVoteEnd(config_load.getPlayerRequiredVoteEnd());
			this.setApple_rate(config_load.getApple_rate());
			this.setFlint_rate(config_load.getFlint_rate());
			this.setPearl_rate(config_load.getPearl_rate());
			this.setXp_boost(config_load.getXp_boost());
			this.setLimitPowerBow(config_load.getLimitPowerBow());
			this.setLimitSharpnessIron(config_load.getLimitSharpnessIron());
			this.setLimitSharpnessDiamond(config_load.getLimitSharpnessDiamond());
			this.setLimitProtectionDiamond(config_load.getLimitProtectionDiamond());
			this.setLimitProtectionIron(config_load.getLimitProtectionIron());
			this.setLimitKnockBack(config_load.getLimitKnockBack());
			this.setLimitPunch(config_load.getLimitPunch());
			this.setUseOfFlair(config_load.getUseOfFlair());
			this.setGoldenAppleParticles(config_load.getGoldenAppleParticles());
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
		
		for(BorderLG border: BorderLG.values()) {
			this.border_value.put(border, config_load.border_value.getOrDefault(border, border.getValue()));
		}

		for(ScenarioLG scenarios:ScenarioLG.values()) {
			this.scenario.put(scenarios, config_load.scenario.getOrDefault(scenarios, scenarios.getValue()));
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

	public int getStrengthRate() {
		return this.strength_rate;
	}

	public void setStrengthRate(int strength_rate) {
		this.strength_rate = strength_rate;
	}

	public int getPlayerRequiredVoteEnd() {
		return this.player_required_before_voting_ends;
	}

	public void setPlayerRequiredVoteEnd(int player_required_before_voting_ends) {
		this.player_required_before_voting_ends = player_required_before_voting_ends;
	}

	public int getPearl_rate() {
		return pearl_rate;
	}

	public void setPearl_rate(int pearl_rate) {
		this.pearl_rate = pearl_rate;
	}

	public int getFlint_rate() {
		return flint_rate;
	}

	public void setFlint_rate(int flint_rate) {
		this.flint_rate = flint_rate;
	}

	public int getApple_rate() {
		return apple_rate;
	}

	public void setApple_rate(int apple_rate) {
		this.apple_rate = apple_rate;
	}

	public int getXp_boost() {
		return xp_boost;
	}

	public void setXp_boost(int xp_boost) {
		this.xp_boost = xp_boost;
	}

	public int getLimitProtectionIron() {
		return limitProtectionIron;
	}

	public void setLimitProtectionIron(int limitProtectionIron) {
		this.limitProtectionIron = limitProtectionIron;
	}

	public int getLimitProtectionDiamond() {
		return limitProtectionDiamond;
	}

	public void setLimitProtectionDiamond(int limitProtectionDiamond) {
		this.limitProtectionDiamond = limitProtectionDiamond;
	}

	public int getLimitSharpnessDiamond() {
		return limitSharpnessDiamond;
	}

	public void setLimitSharpnessDiamond(int limitSharpnessDiamond) {
		this.limitSharpnessDiamond = limitSharpnessDiamond;
	}

	public int getLimitSharpnessIron() {
		return limitSharpnessIron;
	}

	public void setLimitSharpnessIron(int limitSharpnessIron) {
		this.limitSharpnessIron = limitSharpnessIron;
	}

	public int getLimitPowerBow() {
		return limitPowerBow;
	}

	public void setLimitPowerBow(int limitPowerBow) {
		this.limitPowerBow = limitPowerBow;
	}

	public int getLimitKnockBack() {
		return limitKnockBack;
	}

	public void setLimitKnockBack(int limitKnockBack) {
		this.limitKnockBack = limitKnockBack;
	}

	public int getLimitPunch() {
		return limitPunch;
	}

	public void setLimitPunch(int limitPunch) {
		this.limitPunch = limitPunch;
	}

	public int getUseOfFlair() {
		return useOfFlair;
	}

	public void setUseOfFlair(int useOfFlair) {
		this.useOfFlair = useOfFlair;
	}

	public int getGoldenAppleParticles() {
		return goldenAppleParticles;
	}

	public void setGoldenAppleParticles(int goldenAppleParticles) {
		this.goldenAppleParticles = goldenAppleParticles;
	}
}
