package io.github.ph1lou.werewolfplugin.save;

import io.github.ph1lou.werewolfapi.ConfigWereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.registers.*;

import java.util.HashMap;
import java.util.Map;

public class Configuration implements ConfigWereWolfAPI {

    private final Map<String, Integer> timerValues = new HashMap<>();
    private final Map<String, Boolean> configValues = new HashMap<>();
    private final Map<String, Integer> roleCount = new HashMap<>();
    private final Map<String, Boolean> scenarioValues = new HashMap<>();
    private final Map<String, Integer> randomEventsValues = new HashMap<>();
    private transient RegisterManager registerManager;
    private int strengthRate = 30;
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
    private int useOfFlair = 3;
    private int goldenAppleParticles = 1;
    private int distanceBearTrainer = 50;
    private int distanceSuccubus = 20;
    private int distanceAmnesiacLovers = 15;
    private int distancePriestess = 10;
    private int distanceSister = 20;
    private int distanceFox = 20;
    private boolean trollSV = false;
    private int borderMax = 2000;
    private int borderMin = 300;
    private int loverSize = 0;
    private int amnesiacLoverSize = 0;
    private int cursedLoverSize = 0;
    private int limitDepthStrider = 0;
    private int knockBackMode = 0;
    private String trollKey = RolesBase.VILLAGER.getKey();
    private int spectatorMode = 2;  // 0 no Spectators, 1 allowed for death players, 2 for all players;
    private boolean whiteList = false;
    private int playerMax = 30;
    private double borderSpeed = 0.3;
    private int werewolfChatMaxMessage = 1;

    public Configuration(RegisterManager registerManager) {
        this.registerManager = registerManager;
    }

    @Override
    public int getLimitDepthStrider() {
        return this.limitDepthStrider;
    }

    @Override
    public void setTimerValue(String key, int value) {
        timerValues.put(key, value);
    }

    @Override
    public void setLimitDepthStrider(int i) {
        this.limitDepthStrider = i;
    }

    @Override
    public int getDiamondLimit() {
        return this.diamondLimit;
    }

    @Override
    public void setDiamondLimit(int diamondLimit) {
        this.diamondLimit = diamondLimit;
    }

    @Override
	public int getStrengthRate() {
        return this.strengthRate;
    }

    @Override
    public void setStrengthRate(int strengthRate) {
        this.strengthRate = strengthRate;
    }

    @Override
    public int getPlayerRequiredVoteEnd() {
        return this.playerRequiredBeforeVotingEnds;
    }

    @Override
    public void setPlayerRequiredVoteEnd(int playerRequiredBeforeVotingEnds) {
        this.playerRequiredBeforeVotingEnds = playerRequiredBeforeVotingEnds;
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
        return borderMax;
    }

    @Override
    public void setBorderMax(int borderMax) {
        this.borderMax = borderMax;
    }

    @Override
    public int getBorderMin() {
        return borderMin;
    }

    @Override
    public void setBorderMin(int borderMin) {
        this.borderMin = borderMin;
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
    public int getTimerValue(String key) {
        return timerValues.getOrDefault(key, registerManager.getTimersRegister().stream().filter(timerRegister -> timerRegister.getKey().equals(key)).findFirst().map(TimerRegister::getDefaultValue).orElse(0));
    }

    @Override
    public boolean isConfigActive(String key) {
        return configValues.getOrDefault(key, registerManager.getConfigsRegister().stream().filter(configRegister -> configRegister.getKey().equals(key)).findFirst().map(ConfigRegister::getDefaultValue).orElse(false));
    }

    @Override
    public int getRoleCount(String key) {
        return roleCount.getOrDefault(key, 0);
    }

    @Override
    public boolean isScenarioActive(String key) {
        return scenarioValues.getOrDefault(key, registerManager.getScenariosRegister().stream().filter(scenarioRegister -> scenarioRegister.getKey().equals(key)).findFirst().map(ScenarioRegister::getDefaultValue).orElse(false));
    }

    @Override
    public double getBorderSpeed() {
        return borderSpeed;
    }

    @Override
    public void setBorderSpeed(double borderSpeed) {
        this.borderSpeed = borderSpeed;
    }

    @Override
    public void switchConfigValue(String key) {
        configValues.put(key, !isConfigActive(key));
    }

    @Override
    public void switchScenarioValue(String key) {
        scenarioValues.put(key, !isScenarioActive(key));
    }

    @Override
    public void removeOneRole(String key) {
        if (getRoleCount(key) > 0) {
            roleCount.put(key, getRoleCount(key) - 1);
        }
    }

    @Override
    public void addOneRole(String key) {
        roleCount.put(key, getRoleCount(key) + 1);
    }

    @Override
    public void setRole(String key, int i) {
        roleCount.put(key, i);
    }

    public void decreaseTimer(String key) {
        timerValues.put(key, getTimerValue(key) - 1);
    }

    @Override
    public void moveTimer(String key, int i) {
        if (getTimerValue(key) + i >= 0) {
            timerValues.put(key, getTimerValue(key) + i);
        }
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
    public void setConfig(String key, boolean value) {
        configValues.put(key, value);
    }

    @Override
    public void setScenario(String key, boolean value) {
        scenarioValues.put(key, value);
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

    @Override
    public int getKnockBackMode() {
        return knockBackMode;
    }

    @Override
    public int getProbability(String key) {
        return randomEventsValues.getOrDefault(key, registerManager.getRandomEventsRegister().stream().filter(randomEventRegister -> randomEventRegister.getKey().equals(key)).findFirst().map(RandomEventRegister::getDefaultValue).orElse(0));
    }

    @Override
    public void setProbability(String key, int probability) {
        randomEventsValues.put(key, probability);
    }

    @Override
    public void setKnockBackMode(int knockBackMode) {
        this.knockBackMode = knockBackMode;
    }

    @Override
    public int getWereWolfChatMaxMessage() {
        return werewolfChatMaxMessage;
    }

    @Override
    public void setWereWolfChatMaxMessage(int nbMessage) {
        this.werewolfChatMaxMessage = nbMessage;
    }

    @Override
    public int getDistanceSister() {
        return distanceSister;
    }

    @Override
    public void setDistanceSister(int i) {
        this.distanceSister = i;
    }

    @Override
    public int getDistancePriestess() {
        return distancePriestess;
    }

    @Override
    public void setDistancePriestess(int i) {
        this.distancePriestess = i;
    }


    public void addRegister(RegisterManager registerManager) {
        this.registerManager = registerManager;
    }
}
