package io.github.ph1lou.pluginlg.savelg;

import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.ConfigWereWolfAPI;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigLG implements ConfigWereWolfAPI {

    private final Map<TimerLG, Integer> timerValues = new HashMap<>();
    private final Map<ToolLG, Boolean> configValues = new HashMap<>();
    private final Map<RoleLG, Integer> roleCount = new HashMap<>();
    private final Map<ScenarioLG, Boolean> scenarioValues = new HashMap<>();

    private int strengthRate = 130;
    private int resistanceRate = 20;
    private int appleRate = 10;
    private int flintRate = 10;
    private int pearlRate = 30;
    private int xpBoost = 500;
    private int playerRequiredBeforeVotingEnds = 10;
    private int diamondLimit = 17;
    private int limitProtectionIron = 3;
    private int limitProtectionDiamond = 2;
    private int limitSharpnessDiamond = 3;
    private int limitSharpnessIron = 4;
    private int limitPowerBow = 3;
    private int limitPunch = 1;
    private int limitKnockBack = 1;
    private int useOfFlair = 4;
    private int goldenAppleParticles = 1;
    private int distanceBearTrainer = 50;
    private int distanceSuccubus = 20;
    private int distanceAmnesiacLovers = 15;
    private int distanceFox = 20;
    private boolean trollSV = false;
    private int BorderMax =2000;
    private int BorderMin =300;

    public void getConfig(GameManager game, String configName) {

        ConfigLG this_load = this;

        File file = new File(game.getDataFolder() + File.separator + "configs" + File.separator, configName + ".json");

        if (file.exists()) {
            this_load = SerializerLG.deserialize(FileLG.loadContent(file));
            this.setDiamondLimit(this_load.getDiamondLimit());
            this.setStrengthRate(this_load.getStrengthRate());
            this.setPlayerRequiredVoteEnd(this_load.getPlayerRequiredVoteEnd());
            this.setAppleRate(this_load.getAppleRate());
            this.setFlintRate(this_load.getFlintRate());
            this.setPearlRate(this_load.getPearlRate());
            this.setXpBoost(this_load.getXpBoost());
            this.setLimitPowerBow(this_load.getLimitPowerBow());
            this.setLimitSharpnessIron(this_load.getLimitSharpnessIron());
            this.setLimitSharpnessDiamond(this_load.getLimitSharpnessDiamond());
            this.setLimitProtectionDiamond(this_load.getLimitProtectionDiamond());
            this.setLimitProtectionIron(this_load.getLimitProtectionIron());
            this.setLimitKnockBack(this_load.getLimitKnockBack());
            this.setLimitPunch(this_load.getLimitPunch());
            this.setUseOfFlair(this_load.getUseOfFlair());
            this.setGoldenAppleParticles(this_load.getGoldenAppleParticles());
            this.setDistanceBearTrainer(this_load.getDistanceBearTrainer());
            this.setDistanceFox(this_load.getDistanceFox());
            this.setResistanceRate(this_load.getResistanceRate());
            this.setTrollSV(this_load.isTrollSV());
            this.setDistanceSuccubus(this_load.getDistanceSuccubus());
            this.setBorderMax(this_load.getBorderMax());
            this.setBorderMin(this_load.getBorderMin());
            this.setDistanceAmnesiacLovers(this_load.getDistanceAmnesiacLovers());
        }


        for(RoleLG role:RoleLG.values()) {
            this.roleCount.put(role, this_load.roleCount.getOrDefault(role, 0));
        }

        for(ToolLG tool:ToolLG.values()) {
            this.configValues.put(tool, this_load.configValues.getOrDefault(tool, tool.getValue()));
        }

        for(TimerLG timer:TimerLG.values()) {
            this.timerValues.put(timer, this_load.timerValues.getOrDefault(timer, timer.getValue()));
        }


        for(ScenarioLG scenarios:ScenarioLG.values()) {
            this.scenarioValues.put(scenarios, this_load.scenarioValues.getOrDefault(scenarios, scenarios.getValue()));
        }

        FileLG.save(file, SerializerLG.serialize(this));

        game.score.setRole(0);

        for (RoleLG role:RoleLG.values()) {
            if (!role.equals(RoleLG.CURSED_LOVER) && !role.equals(RoleLG.LOVER) && !role.equals(RoleLG.AMNESIAC_LOVER)) {
                game.score.setRole(game.score.getRole() + this.roleCount.get(role));
            }
        }
    }

	public int getDiamondLimit() {
        return this.diamondLimit;
    }

	public void setDiamondLimit(int diamond_limit) {
        this.diamondLimit = diamond_limit;
    }

	public int getStrengthRate() {
        return this.strengthRate;
    }

	public void setStrengthRate(int strength_rate) {
        this.strengthRate = strength_rate;
    }

    public int getPlayerRequiredVoteEnd() {
        return this.playerRequiredBeforeVotingEnds;
    }

    public void setPlayerRequiredVoteEnd(int player_required_before_voting_ends) {
        this.playerRequiredBeforeVotingEnds = player_required_before_voting_ends;
    }

    public int getPearlRate() {
        return pearlRate;
    }

    public void setPearlRate(int pearlRate) {
        this.pearlRate = pearlRate;
    }

    public int getFlintRate() {
        return flintRate;
    }

    public void setFlintRate(int flintRate) {
        this.flintRate = flintRate;
    }

    public int getAppleRate() {
        return appleRate;
    }

    public void setAppleRate(int appleRate) {
        this.appleRate = appleRate;
    }

    public int getXpBoost() {
        return xpBoost;
    }

    public void setXpBoost(int xpBoost) {
        this.xpBoost = xpBoost;
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

    public int getDistanceBearTrainer() {
        return distanceBearTrainer;
    }

    public void setDistanceBearTrainer(int distanceBearTrainer) {
        this.distanceBearTrainer = distanceBearTrainer;
    }

    public int getDistanceFox() {
        return distanceFox;
    }

    public void setDistanceFox(int distanceFox) {
        this.distanceFox = distanceFox;
    }

    public int getResistanceRate() {
        return resistanceRate;
    }

    public void setResistanceRate(int resistanceRate) {
        this.resistanceRate = resistanceRate;
    }

    public boolean isTrollSV() {
        return trollSV;
    }

    public void setTrollSV(boolean trollSV) {
        this.trollSV = trollSV;
    }

    public int getDistanceSuccubus() {
        return distanceSuccubus;
    }

    public void setDistanceSuccubus(int distanceSuccubus) {
        this.distanceSuccubus = distanceSuccubus;
    }

    public int getBorderMax() {
        return BorderMax;
    }

    public void setBorderMax(int borderMax) {
        this.BorderMax = borderMax;
    }

    public int getBorderMin() {
        return BorderMin;
    }

    public void setBorderMin(int borderMin) {
        this.BorderMin = borderMin;
    }

    public int getDistanceAmnesiacLovers() {
        return distanceAmnesiacLovers;
    }

    public void setDistanceAmnesiacLovers(int distanceAmnesiacLovers) {
        this.distanceAmnesiacLovers = distanceAmnesiacLovers;
    }

    public Map<TimerLG, Integer> getTimerValues() {
        return timerValues;
    }

    public Map<ToolLG, Boolean> getConfigValues() {
        return configValues;
    }

    public Map<RoleLG, Integer> getRoleCount() {
        return roleCount;
    }

    public Map<ScenarioLG, Boolean> getScenarioValues() {
        return scenarioValues;
    }
}
