package io.github.ph1lou.werewolfplugin.save;


import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.registers.ConfigRegister;
import io.github.ph1lou.werewolfapi.registers.IRegisterManager;
import io.github.ph1lou.werewolfapi.registers.RandomEventRegister;
import io.github.ph1lou.werewolfapi.registers.ScenarioRegister;
import io.github.ph1lou.werewolfapi.registers.TimerRegister;
import io.github.ph1lou.werewolfplugin.RegisterManager;

import java.util.HashMap;
import java.util.Map;

public class Configuration implements IConfiguration {

    private final Map<String, Integer> timerValues = new HashMap<>();
    private final Map<String, Boolean> configValues = new HashMap<>();
    private final Map<String, Integer> loverCount = new HashMap<>();
    private Map<String, Integer> roleCount = new HashMap<>();
    private final Map<String, Boolean> scenarioValues = new HashMap<>();
    private final Map<String, Integer> randomEventsValues = new HashMap<>();
    private transient IRegisterManager registerManager;
    private int strengthRate = 30;
    private int resistanceRate = 20;
    private int appleRate = 2;
    private int flintRate = 10;
    private int distanceFlutePlayer = 20;
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
    private int distanceTwin = 50;
    private int distanceHowlingWerewolf = 80;
    private int distanceWillOTheWisp = 50;
    private int distanceHermit = 20;
    private int distanceFox = 20;
    private int distanceFearfulWerewolf = 20;
    private int distanceAvengerWerewolf = 10;
    private boolean trollSV = false;
    private int borderMax = 2000;
    private int borderMin = 300;
    private int limitDepthStrider = 0;
    private int knockBackMode = 0;
    private String trollKey = RolesBase.VILLAGER.getKey();
    private int spectatorMode = 2;  // 0 no Spectators, 1 allowed for death players, 2 for all players;
    private boolean whiteList = false;
    private int playerMax = 30;
    private double borderSpeed = 0.3;
    private int werewolfChatMaxMessage = 1;
    private boolean trollLover = false;
    private boolean autoRez = false;
    private boolean seerEveryOtherDay = true;
    private boolean oracleEveryOtherDay = true;
    private boolean detectiveEveryOtherDay = true;
    private boolean sweetAngel = false;
    private int distanceTenebrousWerewolf = 50;
    private int tenebrousDuration = 600;

    public Configuration(IRegisterManager registerManager) {
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
    public int getDistanceHowlingWerewolf() {
        return this.distanceHowlingWerewolf;
    }

    @Override
    public void setDistanceHowlingWerewolf(int distanceHowlingWerewolf) {
        this.distanceHowlingWerewolf=distanceHowlingWerewolf;
    }

    @Override
    public int getDistanceTwin() {
        return this.distanceTwin;
    }

    @Override
    public void setDistanceTwin(int distanceTwin) {
        this.distanceTwin = distanceTwin;
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
    public int getDistanceFlutePlayer() {
        return this.distanceFlutePlayer;
    }

    @Override
    public void setDistanceFlutePlayer(int distanceFlutePlayer) {
        this.distanceFlutePlayer = distanceFlutePlayer;
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
    public int getLoverCount(String key) {
        return this.loverCount.getOrDefault(key, 0);
    }

    @Override
    public void setLoverCount(String key, int i) {
        this.loverCount.put(key, i);
    }

    @Override
    public void addOneLover(String key) {
        this.loverCount.put(key, this.loverCount.getOrDefault(key, 0) + 1);
    }

    @Override
    public void removeOneLover(String key) {
        this.loverCount.put(key, this.loverCount.getOrDefault(key, 0) - 1);
    }

    @Override
    public boolean isScenarioActive(String key) {
        return scenarioValues
                .getOrDefault(key, registerManager.getScenariosRegister()
                        .stream()
                        .filter(scenarioRegister -> scenarioRegister.getKey().equals(key))
                        .findFirst()
                        .map(ScenarioRegister::getDefaultValue)
                        .orElse(false));
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
    public void setConfig(String key, boolean value) {
        configValues.put(key, value);
    }

    @Override
    public void setScenario(String key, boolean value) {
        scenarioValues.put(key, value);
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

    @Override
    public int getDistanceAvengerWerewolf() {
        return this.distanceAvengerWerewolf;
    }

    @Override
    public void setDistanceAvengerWerewolf(int i) {
        this.distanceAvengerWerewolf = i;
    }

    @Override
    public boolean isTrollLover() {
        return this.trollLover;
    }

    @Override
    public void setTrollLover(boolean trollLover) {
        this.trollLover = trollLover;
    }

    @Override
    public boolean isWitchAutoResurrection() {
        return this.autoRez;
    }

    @Override
    public void setWitchAutoResurrection(boolean autoReZ) {
        this.autoRez = autoReZ;
    }

    @Override
    public boolean isSeerEveryOtherDay() {
        return this.seerEveryOtherDay;
    }

    @Override
    public void setSeerEveryOtherDay(boolean seerEveryOtherDay) {
        this.seerEveryOtherDay = seerEveryOtherDay;
    }

    @Override
    public boolean isDetectiveEveryOtherDay() {
        return this.detectiveEveryOtherDay;
    }

    @Override
    public void setDetectiveEveryOtherDay(boolean detectiveEveryOtherDay) {
        this.detectiveEveryOtherDay = detectiveEveryOtherDay;
    }

    @Override
    public boolean isOracleEveryOtherDay() {
        return this.oracleEveryOtherDay;
    }

    @Override
    public void setOracleEveryOtherDay(boolean oracleEveryOtherDay) {
        this.oracleEveryOtherDay = oracleEveryOtherDay;
    }

    @Override
    public boolean isSweetAngel() {
        return this.sweetAngel;
    }

    @Override
    public int getTenebrousDistance() {
        return this.distanceTenebrousWerewolf;
    }

    @Override
    public void setTenebrousDistance(int distance) {
        this.distanceTenebrousWerewolf = distance;
    }

    @Override
    public int getTenebrousDuration() {
        return this.tenebrousDuration;
    }

    @Override
    public void setTenebrousDuration(int t) {
        this.tenebrousDuration = t;
    }

    @Override
    public void setSweetAngel(boolean sweetAngel) {
        this.sweetAngel = sweetAngel;
    }


    public void addRegister(RegisterManager registerManager) {
        this.registerManager = registerManager;
    }

    public void setComposition(Map<String, Integer> composition) {
        this.roleCount = composition;
    }

    @Override
    public int getDistanceFearfulWerewolf() {
        return distanceFearfulWerewolf;
    }

    @Override
    public void setDistanceFearfulWerewolf(int distanceFearfulWerewolf) {
        this.distanceFearfulWerewolf = distanceFearfulWerewolf;
    }

    @Override
    public int getDistanceHermit() {
        return this.distanceHermit;
    }

    @Override
    public void setDistanceHermit(int distanceHermit) {
        this.distanceHermit = distanceHermit;
    }

    @Override
    public int getDistanceWillOTheWisp() {
        return this.distanceWillOTheWisp;
    }

    @Override
    public void setDistanceWillOTheWisp(int distanceWillOTheWisp) {
        this.distanceWillOTheWisp = distanceWillOTheWisp;
    }
}
