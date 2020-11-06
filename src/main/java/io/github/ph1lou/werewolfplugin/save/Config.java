package io.github.ph1lou.werewolfplugin.save;

import io.github.ph1lou.werewolfapi.ConfigWereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.RolesBase;

import java.util.HashMap;
import java.util.Map;

public class Config implements ConfigWereWolfAPI {

    private final Map<String, Integer> timerValues = new HashMap<>();
    private final Map<String, Boolean> configValues = new HashMap<>();
    private final Map<String, Integer> roleCount = new HashMap<>();
    private final Map<String, Boolean> scenarioValues = new HashMap<>();

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
    private int BorderMax = 2000;
    private int BorderMin = 300;
    private int loverSize = 0;
    private int amnesiacLoverSize = 0;
    private int cursedLoverSize = 0;
    private int limitDepthStrider = 0;
    private String trollKey = RolesBase.VILLAGER.getKey();
    private int spectatorMode = 2;  // 0 no Spectators, 1 allowed for death players, 2 for all players;
    private boolean whiteList = false;
    private int playerMax = 30;


    @Override
    public int getLimitDepthStrider() {
        return this.limitDepthStrider;
    }

    @Override
    public void setLimitDepthStrider(int i) {
        this.limitDepthStrider=i;
    }

    @Override
	public int getDiamondLimit() {
        return this.diamondLimit;
    }

    @Override
	public void setDiamondLimit(int diamond_limit) {
        this.diamondLimit = diamond_limit;
    }

    @Override
	public int getStrengthRate() {
        return this.strengthRate;
    }

    @Override
	public void setStrengthRate(int strength_rate) {
        this.strengthRate = strength_rate;
    }

    @Override
    public int getPlayerRequiredVoteEnd() {
        return this.playerRequiredBeforeVotingEnds;
    }

    @Override
    public void setPlayerRequiredVoteEnd(int player_required_before_voting_ends) {
        this.playerRequiredBeforeVotingEnds = player_required_before_voting_ends;
    }

    @Override
    public int getPearlRate() {
        return pearlRate;
    }

    @Override
    public void setPearlRate(int pearlRate) {
        this.pearlRate = pearlRate;
    }

    @Override
    public int getFlintRate() {
        return flintRate;
    }

    @Override
    public void setFlintRate(int flintRate) {
        this.flintRate = flintRate;
    }

    @Override
    public int getAppleRate() {
        return appleRate;
    }

    @Override
    public void setAppleRate(int appleRate) {
        this.appleRate = appleRate;
    }

    @Override
    public int getXpBoost() {
        return xpBoost;
    }

    @Override
    public void setXpBoost(int xpBoost) {
        this.xpBoost = xpBoost;
    }

    @Override
    public int getLimitProtectionIron() {
        return limitProtectionIron;
    }

    @Override
    public void setLimitProtectionIron(int limitProtectionIron) {
        this.limitProtectionIron = limitProtectionIron;
    }

    @Override
	public int getLimitProtectionDiamond() {
		return limitProtectionDiamond;
	}

    @Override
	public void setLimitProtectionDiamond(int limitProtectionDiamond) {
		this.limitProtectionDiamond = limitProtectionDiamond;
	}

    @Override
	public int getLimitSharpnessDiamond() {
		return limitSharpnessDiamond;
	}

    @Override
	public void setLimitSharpnessDiamond(int limitSharpnessDiamond) {
		this.limitSharpnessDiamond = limitSharpnessDiamond;
	}

    @Override
	public int getLimitSharpnessIron() {
		return limitSharpnessIron;
	}

    @Override
	public void setLimitSharpnessIron(int limitSharpnessIron) {
		this.limitSharpnessIron = limitSharpnessIron;
	}

    @Override
	public int getLimitPowerBow() {
		return limitPowerBow;
	}

    @Override
	public void setLimitPowerBow(int limitPowerBow) {
		this.limitPowerBow = limitPowerBow;
	}

    @Override
	public int getLimitKnockBack() {
		return limitKnockBack;
	}

    @Override
	public void setLimitKnockBack(int limitKnockBack) {
		this.limitKnockBack = limitKnockBack;
	}

    @Override
	public int getLimitPunch() {
		return limitPunch;
	}

    @Override
	public void setLimitPunch(int limitPunch) {
		this.limitPunch = limitPunch;
	}

    @Override
	public int getUseOfFlair() {
		return useOfFlair;
	}

    @Override
	public void setUseOfFlair(int useOfFlair) {
        this.useOfFlair = useOfFlair;
    }

    @Override
    public int getGoldenAppleParticles() {
        return goldenAppleParticles;
    }

    @Override
    public void setGoldenAppleParticles(int goldenAppleParticles) {
        this.goldenAppleParticles = goldenAppleParticles;
    }

    @Override
    public int getDistanceBearTrainer() {
        return distanceBearTrainer;
    }

    @Override
    public void setDistanceBearTrainer(int distanceBearTrainer) {
        this.distanceBearTrainer = distanceBearTrainer;
    }

    @Override
    public int getDistanceFox() {
        return distanceFox;
    }

    @Override
    public void setDistanceFox(int distanceFox) {
        this.distanceFox = distanceFox;
    }

    @Override
    public int getResistanceRate() {
        return resistanceRate;
    }

    @Override
    public void setResistanceRate(int resistanceRate) {
        this.resistanceRate = resistanceRate;
    }

    @Override
    public boolean isTrollSV() {
        return trollSV;
    }

    @Override
    public void setTrollSV(boolean trollSV) {
        this.trollSV = trollSV;
    }

    @Override
    public int getDistanceSuccubus() {
        return distanceSuccubus;
    }

    @Override
    public void setDistanceSuccubus(int distanceSuccubus) {
        this.distanceSuccubus = distanceSuccubus;
    }

    @Override
    public int getBorderMax() {
        return BorderMax;
    }

    @Override
    public void setBorderMax(int borderMax) {
        this.BorderMax = borderMax;
    }

    @Override
    public int getBorderMin() {
        return BorderMin;
    }

    @Override
    public void setBorderMin(int borderMin) {
        this.BorderMin = borderMin;
    }

    @Override
    public int getDistanceAmnesiacLovers() {
        return distanceAmnesiacLovers;
    }

    @Override
    public void setDistanceAmnesiacLovers(int distanceAmnesiacLovers) {
        this.distanceAmnesiacLovers = distanceAmnesiacLovers;
    }

    @Override
    public Map<String, Integer> getTimerValues() {
        return timerValues;
    }

    @Override
    public Map<String, Boolean> getConfigValues() {
        return configValues;
    }

    @Override
    public Map<String, Integer> getRoleCount() {
        return roleCount;
    }

    @Override
    public Map<String, Boolean> getScenarioValues() {
        return scenarioValues;
    }

    @Override
    public int getLoverSize() {
        return loverSize;
    }

    @Override
    public void setLoverSize(int loverSize) {
        this.loverSize = loverSize;
    }

    @Override
    public int getAmnesiacLoverSize() {
        return amnesiacLoverSize;
    }

    @Override
    public void setAmnesiacLoverSize(int amnesiacLoverSize) {
        this.amnesiacLoverSize = amnesiacLoverSize;
    }

    @Override
    public int getCursedLoverSize() {
        return cursedLoverSize;
    }

    @Override
    public void setCursedLoverSize(int cursedLoverSize) {
        this.cursedLoverSize = cursedLoverSize;
    }

    @Override
    public boolean isWhiteList() {
        return whiteList;
    }

    @Override
    public void setWhiteList(boolean whiteList) {
        this.whiteList = whiteList;
    }

    @Override
    public int getPlayerMax() {
        return playerMax;
    }


    @Override
    public void setPlayerMax(int playerMax) {
        this.playerMax = playerMax;
    }

    @Override
    public int getSpectatorMode() {
        return spectatorMode;
    }

    @Override
    public void setSpectatorMode(int spectatorMode) {
        this.spectatorMode = spectatorMode;
    }

    @Override
    public String getTrollKey() {
        return trollKey;
    }

    @Override
    public void setTrollKey(String trollKey) {
        this.trollKey = trollKey;
    }
}
