package io.github.ph1lou.werewolfplugin;


import io.github.ph1lou.werewolfapi.registers.AddonRegister;
import io.github.ph1lou.werewolfapi.registers.CommandRegister;
import io.github.ph1lou.werewolfapi.registers.ConfigRegister;
import io.github.ph1lou.werewolfapi.registers.IRegister;
import io.github.ph1lou.werewolfapi.registers.IRegisterManager;
import io.github.ph1lou.werewolfapi.registers.RandomEventRegister;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfapi.registers.ScenarioRegister;
import io.github.ph1lou.werewolfapi.registers.TimerRegister;
import io.github.ph1lou.werewolfplugin.registers.AdminCommandsRegister;
import io.github.ph1lou.werewolfplugin.registers.CommandsRegister;
import io.github.ph1lou.werewolfplugin.registers.ConfigsRegister;
import io.github.ph1lou.werewolfplugin.registers.EventRandomsRegister;
import io.github.ph1lou.werewolfplugin.registers.RolesRegister;
import io.github.ph1lou.werewolfplugin.registers.ScenariosRegister;
import io.github.ph1lou.werewolfplugin.registers.TimersRegister;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RegisterManager implements IRegisterManager {

    private final Main main;
    private final List<RoleRegister> rolesRegister;
    private final List<ScenarioRegister> scenariosRegister;
    private final List<ConfigRegister> configsRegister;
    private final List<TimerRegister> timersRegister;
    private final List<CommandRegister> commandsRegister;
    private final List<CommandRegister> adminCommandsRegister;
    private final List<AddonRegister> addonsRegister = new ArrayList<>();
    private final List<RandomEventRegister> eventRandomsRegister;
    private static RegisterManager instance;

    public RegisterManager(Main main) {
        this.main = main;
        instance = this;
        this.rolesRegister = RolesRegister.registerRoles();
        this.scenariosRegister = ScenariosRegister.registerScenarios(main);
        this.configsRegister = ConfigsRegister.registerConfigs(main);
        this.timersRegister = TimersRegister.registerTimers();
        this.commandsRegister = CommandsRegister.registerCommands();
        this.adminCommandsRegister = AdminCommandsRegister.registerAdminCommands();
        this.eventRandomsRegister = EventRandomsRegister.registerRandomEvents(main);
    }

    public static IRegisterManager get() {
        return instance;
    }



    @Override
    public List<? extends RoleRegister> getRolesRegister() {
        return rolesRegister;
    }

    @Override
    public List<? extends ScenarioRegister> getScenariosRegister() {
        return scenariosRegister;
    }

    @Override
    public List<? extends ConfigRegister> getConfigsRegister() {
        return configsRegister;
    }

    @Override
    public List<? extends TimerRegister> getTimersRegister() {
        return timersRegister;
    }

    @Override
    public List<? extends CommandRegister> getCommandsRegister() {
        return commandsRegister;
    }

    @Override
    public List<? extends CommandRegister> getAdminCommandsRegister() {
        return adminCommandsRegister;
    }

    @Override
    public List<? extends AddonRegister> getAddonsRegister() {
        return addonsRegister;
    }

    @Override
    public List<? extends RandomEventRegister> getRandomEventsRegister() {
        return eventRandomsRegister;
    }

    @Override
    public void registerAddon(AddonRegister addonRegister) {
        register(addonRegister, addonsRegister);
    }

    @Override
    public void registerRole(RoleRegister roleRegister) {
        register(roleRegister, rolesRegister);
    }

    @Override
    public void registerScenario(ScenarioRegister scenarioRegister) {
        register(scenarioRegister,scenariosRegister);
    }

    @Override
    public void registerConfig(ConfigRegister configRegister) {
        register(configRegister,configsRegister);
    }

    @Override
    public void registerTimer(TimerRegister timerRegister) {
        register(timerRegister, timersRegister);
    }

    @Override
    public void registerCommands(CommandRegister commandRegister) {
        register(commandRegister, commandsRegister);
    }

    @Override
    public void registerRandomEvents(RandomEventRegister randomEventRegister) {
        register(randomEventRegister, eventRandomsRegister);
    }

    @Override
    public void registerAdminCommands(CommandRegister commandRegister) {
        register(commandRegister, adminCommandsRegister);
    }

    private <A extends IRegister> void register(A register, List<A> registers) {
        if (registers.removeAll(registers.stream()
                .filter(register1 -> register1.getKey().equalsIgnoreCase(register.getKey()))
                .collect(Collectors.toList()))) {
            Bukkit.getLogger().warning(String.format("[WereWolfPlugin] L'élément %s a été écrasé par l'addon %s", register.getKey(), register.getAddonKey()));
        }
        registers.add(register);
    }


}
