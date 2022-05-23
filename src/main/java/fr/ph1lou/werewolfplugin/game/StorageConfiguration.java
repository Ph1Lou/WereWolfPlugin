package fr.ph1lou.werewolfplugin.game;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.game.IStorageConfiguration;
import fr.ph1lou.werewolfplugin.Register;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class StorageConfiguration implements IStorageConfiguration {

    private transient String addonKey;
    private final Map<String, Integer> timerValues;
    private final Map<String, Boolean> configValues;
    private final Map<String, Integer> loverCount;
    private final Map<String, Integer> roleCount;
    private final Map<String, Integer> values;
    private final Map<String, Boolean> scenarioValues;
    private final Map<String, Integer> randomEventsValues;

    public StorageConfiguration(){
        this.timerValues = new HashMap<>();
        this.configValues = new HashMap<>();
        this.loverCount = new HashMap<>();
        this.roleCount = new HashMap<>();
        this.scenarioValues = new HashMap<>();
        this.randomEventsValues = new HashMap<>();
        this.values = new HashMap<>();
    }

    public StorageConfiguration setAddonKey(String addonKey) {
        this.addonKey = addonKey;
        return this;
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
    public void setValue(String key, int value) {
        this.values.put(key, value);
    }

    @Override
    public void setProbability(String key, int probability) {
        randomEventsValues.put(key, probability);
    }

    @Override
    public void setTimerValue(String key, int value) {
        timerValues.put(key, value);
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
                .getOrDefault(key, Register.get().getScenariosRegister()
                        .stream()
                        .filter(timerWrapper -> timerWrapper.getAddonKey().equals(this.addonKey))
                        .filter(scenarioRegister -> scenarioRegister.getMetaDatas().key().equals(key))
                        .findFirst()
                        .map(listenerManagerScenarioWrapper -> listenerManagerScenarioWrapper.getMetaDatas().defaultValue())
                        .orElse(false));
    }

    @Override
    public int getTimerValue(String key) {

        return timerValues.getOrDefault(key, Register.get().getTimersRegister()
                .stream()
                .filter(timerWrapper -> timerWrapper.getAddonKey().equals(this.addonKey))
                .filter(timerRegister -> timerRegister.getMetaDatas().key().equals(key))
                .map(timerWrapper -> timerWrapper.getMetaDatas().defaultValue())
                .findFirst()
                .orElseGet(() -> Register.get().getRolesRegister()
                        .stream()
                        .filter(roleRoleWrapper -> roleRoleWrapper.getAddonKey().equals(this.addonKey))
                        .flatMap(iRoleRoleWrapper -> Stream.of(iRoleRoleWrapper.getMetaDatas().timers()))
                                .filter(timer -> timer.key().equals(key))
                                .map(Timer::defaultValue)
                                .findFirst()
                                .orElseGet(() -> Register.get().getRandomEventsRegister()
                                        .stream()
                                        .filter(roleRoleWrapper -> roleRoleWrapper.getAddonKey().equals(this.addonKey))
                                        .flatMap(eventWrapper -> Stream.of(eventWrapper.getMetaDatas().timers()))
                                        .map(Timer::defaultValue)
                                        .findFirst()
                                        .orElseGet(() -> Register.get().getLoversRegister()
                                                .stream()
                                                .filter(roleRoleWrapper -> roleRoleWrapper.getAddonKey().equals(this.addonKey))
                                                .flatMap(eventWrapper -> Stream.of(eventWrapper.getMetaDatas().timers()))
                                                .map(Timer::defaultValue)
                                                .findFirst()
                                                .orElse(0)))));
    }

    @Override
    public boolean isConfigActive(String key) {
        return configValues.getOrDefault(key, Register.get().getConfigsRegister()
                .stream()
                .filter(timerWrapper -> timerWrapper.getAddonKey().equals(this.addonKey))
                .filter(configRegister -> configRegister.getMetaDatas().key().equals(key))
                .findFirst().map(configurationWrapper -> configurationWrapper.getMetaDatas().defaultValue())
                .orElseGet(() -> Register.get().getRolesRegister()
                        .stream()
                        .filter(roleRoleWrapper -> roleRoleWrapper.getAddonKey().equals(this.addonKey))
                        .flatMap(iRoleRoleWrapper -> Stream.of(iRoleRoleWrapper.getMetaDatas().configurations()))
                        .filter(timer -> timer.key().equals(key))
                        .map(fr.ph1lou.werewolfapi.annotations.Configuration::defaultValue)
                        .findFirst()
                        .orElseGet(() -> Register.get().getRandomEventsRegister()
                                .stream()
                                .filter(roleRoleWrapper -> roleRoleWrapper.getAddonKey().equals(this.addonKey))
                                .flatMap(eventWrapper -> Stream.of(eventWrapper.getMetaDatas().configurations()))
                                .map(fr.ph1lou.werewolfapi.annotations.Configuration::defaultValue)
                                .findFirst()
                                .orElseGet(() -> Register.get().getLoversRegister()
                                        .stream()
                                        .filter(roleRoleWrapper -> roleRoleWrapper.getAddonKey().equals(this.addonKey))
                                        .flatMap(eventWrapper -> Stream.of(eventWrapper.getMetaDatas().configurations()))
                                        .map(fr.ph1lou.werewolfapi.annotations.Configuration::defaultValue)
                                        .findFirst()
                                        .orElse(false)))));
    }

    @Override
    public int getProbability(String key) {
        return randomEventsValues.getOrDefault(key, Register.get().getRandomEventsRegister().stream()
                .filter(timerWrapper -> timerWrapper.getAddonKey().equals(this.addonKey))
                .filter(randomEventRegister -> randomEventRegister.getMetaDatas().key().equals(key))
                .findFirst()
                .map(listenerManagerEventWrapper -> listenerManagerEventWrapper.getMetaDatas().defaultValue())
                .orElse(0));
    }

    @Override
    public int getValue(String key) {
        return this.values.getOrDefault(key, Register.get().getRolesRegister()
                .stream()
                .filter(timerWrapper -> timerWrapper.getAddonKey().equals(this.addonKey))
                .flatMap(timerWrapper -> Stream.of(timerWrapper.getMetaDatas().configValues()))
                .filter(intValue -> intValue.key().equals(key))
                .map(IntValue::defaultValue)
                .findFirst()
                .orElseGet(() -> Register.get().getConfigsRegister()
                        .stream()
                        .filter(roleRoleWrapper -> roleRoleWrapper.getAddonKey().equals(this.addonKey))
                        .flatMap(iRoleRoleWrapper -> Stream.of(iRoleRoleWrapper.getMetaDatas().configValues()))
                        .filter(timer -> timer.key().equals(key))
                        .map(IntValue::defaultValue)
                        .findFirst()
                        .orElseGet(() -> Register.get().getRandomEventsRegister()
                                .stream()
                                .filter(roleRoleWrapper -> roleRoleWrapper.getAddonKey().equals(this.addonKey))
                                .flatMap(eventWrapper -> Stream.of(eventWrapper.getMetaDatas().configValues()))
                                .map(IntValue::defaultValue)
                                .findFirst()
                                .orElseGet(() -> Register.get().getLoversRegister()
                                        .stream()
                                        .filter(roleRoleWrapper -> roleRoleWrapper.getAddonKey().equals(this.addonKey))
                                        .flatMap(eventWrapper -> Stream.of(eventWrapper.getMetaDatas().configValues()))
                                        .map(IntValue::defaultValue)
                                        .findFirst()
                                        .orElseGet(() -> Register.get().getScenariosRegister()
                                                .stream()
                                                .filter(roleRoleWrapper -> roleRoleWrapper.getAddonKey().equals(this.addonKey))
                                                .flatMap(eventWrapper -> Stream.of(eventWrapper.getMetaDatas().configValues()))
                                                .map(IntValue::defaultValue)
                                                .findFirst()
                                                .orElseGet(() -> Register.get().getModulesRegister()
                                                        .stream()
                                                        .filter(roleRoleWrapper -> roleRoleWrapper.getMetaDatas().key().equals(this.addonKey))
                                                        .flatMap(eventWrapper -> Stream.of(eventWrapper.getMetaDatas().configValues()))
                                                        .map(IntValue::defaultValue)
                                                        .findFirst()
                                                        .orElse(0))))))
        );
    }

    public String getAddonKey() {
        return addonKey;
    }
}
