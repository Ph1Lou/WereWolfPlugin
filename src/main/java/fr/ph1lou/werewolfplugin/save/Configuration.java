package fr.ph1lou.werewolfplugin.save;

import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.registers.IRegisterManager;

import java.util.HashMap;
import java.util.Map;

public class Configuration implements IConfiguration {

    private final Map<String, Integer> timerValues;
    private final Map<String, Boolean> configValues;
    private final Map<String, Integer> loverCount;
    private final Map<String, Integer> roleCount;
    private final Map<String, Boolean> scenarioValues;
    private final Map<String, Integer> randomEventsValues;
    private transient IRegisterManager registerManager;
    private int strengthRate;
    private int resistanceRate;
    private int appleRate;
    private int flintRate;
    private int pearlRate;
    private int xpBoost;
    private int playerRequiredBeforeVotingEnds;
    private int diamondLimit;
    private int limitProtectionIron;
    private int limitProtectionDiamond;
    private int limitSharpnessDiamond;
    private int limitSharpnessIron;
    private int limitPowerBow;
    private int limitPunch;
    private int limitKnockBack;
    private int goldenAppleParticles;
    private boolean trollSV;
    private int borderMax;
    private int borderMin;
    private int limitDepthStrider;
    private int knockBackMode;
    private String trollKey;
    private int spectatorMode;  // 0 no Spectators, 1 allowed for death players, 2 for all players;
    private boolean whiteList;
    private int playerMax;
    private double borderSpeed ;
    private int werewolfChatMaxMessage;
    private boolean trollLover;

    public Configuration(){
        this.timerValues = new HashMap<>();
        this.configValues = new HashMap<>();
        this.loverCount = new HashMap<>();
        this.roleCount = new HashMap<>();
        this.scenarioValues = new HashMap<>();
        this.randomEventsValues = new HashMap<>();
        this.strengthRate = 30;
        this.resistanceRate = 20;
        this.appleRate = 2;
        this.flintRate = 10;
        this.pearlRate = 30;
        this.xpBoost = 500;
        this.playerRequiredBeforeVotingEnds = 10;
        this.diamondLimit = 17;
        this.limitProtectionIron = 3;
        this.limitProtectionDiamond = 2;
        this.limitSharpnessDiamond = 3;
        this.limitSharpnessIron = 4;
        this.limitPowerBow = 3;
        this.limitPunch = 1;
        this.limitKnockBack = 1;
        this.goldenAppleParticles = 1;
        //this.distanceAmnesiacLovers = 15;
        this.trollSV = false;
        this.borderMax = 2000;
        this.borderMin = 300;
        this.limitDepthStrider = 0;
        this.knockBackMode = 0;
        this.trollKey = RoleBase.VILLAGER;
        this.spectatorMode = 2;  // 0 no Spectators, 1 allowed for death players, 2 for all players;
        this.whiteList = false;
        this.playerMax = 30;
        this.borderSpeed = 0.3;
        this.werewolfChatMaxMessage = 1;
        this.trollLover = false;
    }

    public Configuration(IRegisterManager registerManager) {
        this();
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
    public int getGoldenAppleParticles() {
        return goldenAppleParticles;
    }

    @Override
    public void setGoldenAppleParticles(int goldenAppleParticles) {
        this.goldenAppleParticles = goldenAppleParticles;
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
    public int getTimerValue(String key) {
        return timerValues.getOrDefault(key, registerManager.getTimersRegister()
                .stream().filter(timerRegister -> timerRegister.getMetaDatas().key().equals(key))
                .findFirst().map(timerWrapper -> timerWrapper.getMetaDatas().defaultValue()).orElse(0));
    }

    @Override
    public boolean isConfigActive(String key) {
        return configValues.getOrDefault(key, registerManager.getConfigsRegister()
                .stream().filter(configRegister -> configRegister.getMetaDatas().key().equals(key))
                .findFirst().map(configurationWrapper -> configurationWrapper.getMetaDatas().defaultValue()).orElse(false));
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
                        .filter(scenarioRegister -> scenarioRegister.getMetaDatas().key().equals(key))
                        .findFirst()
                        .map(listenerManagerScenarioWrapper -> listenerManagerScenarioWrapper.getMetaDatas().defaultValue())
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
        return randomEventsValues.getOrDefault(key, registerManager.getRandomEventsRegister().stream()
                .filter(randomEventRegister -> randomEventRegister.getMetaDatas().key().equals(key)).findFirst()
                .map(listenerManagerEventWrapper -> listenerManagerEventWrapper.getMetaDatas().defaultValue())
                .orElse(0));
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
    public boolean isTrollLover() {
        return this.trollLover;
    }

    @Override
    public void setTrollLover(boolean trollLover) {
        this.trollLover = trollLover;
    }

    @Override
    public int getValue(String baseKey, String key) {
        return 0;//todo
    }

    @Override
    public void setValue(String baseKey, String key, int value) {
//todo
    }

    @Override
    public boolean isMeetUp() {
        return false;//todo
    }

    @Override
    public void setMeetUp(boolean b) {
//todo
    }

    public void addRegister(IRegisterManager registerManager) {
        this.registerManager = registerManager;
    }
}
