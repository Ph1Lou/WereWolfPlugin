package fr.ph1lou.werewolfplugin.save;

import fr.ph1lou.werewolfapi.enums.RolesBase;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.registers.impl.ConfigRegister;
import fr.ph1lou.werewolfapi.registers.impl.RandomEventRegister;
import fr.ph1lou.werewolfapi.registers.impl.ScenarioRegister;
import fr.ph1lou.werewolfapi.registers.impl.TimerRegister;
import fr.ph1lou.werewolfapi.registers.interfaces.IRegisterManager;
import fr.ph1lou.werewolfplugin.RegisterManager;

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
    private int distanceFlutePlayer;
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
    private int useOfFlair;
    private int goldenAppleParticles;
    private int distanceBearTrainer;
    private int distanceSuccubus;
    private int distanceWiseElder;
    private int distanceServitor;
    private int distanceScammer;
    private int distanceAmnesiacLovers;
    private int distancePriestess;
    private int distanceSister;
    private int distanceTwin;
    private int distanceHowlingWerewolf;
    private int distanceWillOTheWisp;
    private int distanceHermit;
    private int distanceFox;
    private int distanceFearfulWerewolf;
    private int distanceAvengerWerewolf;
    private int distanceDruid;
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
    private boolean autoRez;
    private boolean seerEveryOtherDay;
    private boolean oracleEveryOtherDay;
    private boolean detectiveEveryOtherDay;
    private boolean sweetAngel;
    private int distanceFruitMerchant;
    private int scamDelay;
    private int tenebrousDuration;
    private int distanceTenebrousWerewolf;
    private int distanceGravedigger;

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
        this.distanceFlutePlayer = 20;
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
        this.useOfFlair = 3;
        this.goldenAppleParticles = 1;
        this.distanceBearTrainer = 50;
        this.distanceSuccubus = 20;
        this.distanceAmnesiacLovers = 15;
        this.distancePriestess = 10;
        this.distanceSister = 20;
        this.distanceTwin = 50;
        this.distanceHowlingWerewolf = 80;
        this.distanceWillOTheWisp = 50;
        this.distanceHermit = 20;
        this.distanceFox = 20;
        this.distanceFearfulWerewolf = 20;
        this.distanceAvengerWerewolf = 10;
        this.distanceDruid=50;
        this.trollSV = false;
        this.borderMax = 2000;
        this.borderMin = 300;
        this.limitDepthStrider = 0;
        this.knockBackMode = 0;
        this.trollKey = RolesBase.VILLAGER.getKey();
        this.spectatorMode = 2;  // 0 no Spectators, 1 allowed for death players, 2 for all players;
        this.whiteList = false;
        this.playerMax = 30;
        this.borderSpeed = 0.3;
        this.werewolfChatMaxMessage = 1;
        this.trollLover = false;
        this.autoRez = false;
        this.seerEveryOtherDay = true;
        this.oracleEveryOtherDay = true;
        this.detectiveEveryOtherDay = true;
        this.sweetAngel = false;
        this.distanceFruitMerchant=50;
        this.scamDelay = 9;
        this.distanceWiseElder = 15;
        this.distanceServitor = 25;
        this.distanceScammer = 20;
        this.tenebrousDuration = 600;
        this.distanceTenebrousWerewolf = 50;
        this.distanceGravedigger = 70;
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
    public int getDistanceDruid() {
        return this.distanceDruid;
    }

    @Override
    public void setDistanceDruid(int distanceDruid) {
        this.distanceDruid=distanceDruid;
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
        this.distanceHowlingWerewolf = distanceHowlingWerewolf;
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
    public int getDistanceFruitMerchant() {
        return this.distanceFruitMerchant;
    }

    @Override
    public void setDistanceFruitMerchant(int distanceFruitMerchant) {
        this.distanceFruitMerchant=distanceFruitMerchant;
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
    public int getDistanceWiseElder() {
        return distanceWiseElder;
    }

    @Override
    public void setDistanceWiseElder(int i) {
        distanceWiseElder = i;
    }

    @Override
    public int getDistanceServitor() {
        return distanceServitor;
    }

    @Override
    public void setDistanceServitor(int i) {
        distanceServitor = i;
    }

    @Override
    public void setSweetAngel(boolean sweetAngel) {
        this.sweetAngel = sweetAngel;
    }

    public void addRegister(RegisterManager registerManager) {
        this.registerManager = registerManager;
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

    @Override
    public int getScamDelay() {
        return scamDelay;
    }

    @Override
    public void setScamDelay(int scamDelay) {
        if (scamDelay < 0) return;
        this.scamDelay = scamDelay;
    }

    @Override
    public int getDistanceScammer() {
        return distanceScammer;
    }

    @Override
    public void setDistanceScammer(int distanceScammer) {
        this.distanceScammer = distanceScammer;
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
    public void setGravediggerDistance(int i) {
        this.distanceGravedigger = i;
    }

    @Override
    public int getGravediggerDistance() {
        return this.distanceGravedigger;
    }
}
