package fr.ph1lou.werewolfplugin;

import fr.ph1lou.werewolfapi.registers.impl.AddonRegister;
import fr.ph1lou.werewolfapi.registers.impl.CommandRegister;
import fr.ph1lou.werewolfapi.registers.impl.ConfigRegister;
import fr.ph1lou.werewolfapi.registers.interfaces.IRegister;
import fr.ph1lou.werewolfapi.registers.interfaces.IRegisterManager;
import fr.ph1lou.werewolfapi.registers.impl.RandomEventRegister;
import fr.ph1lou.werewolfapi.registers.impl.RoleRegister;
import fr.ph1lou.werewolfapi.registers.impl.ScenarioRegister;
import fr.ph1lou.werewolfapi.registers.impl.TimerRegister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Register implements IRegisterManager {

    private final String key;
    private final Map<AddonRegister,Register> addonsRegister = new HashMap<>();
    private List<RoleRegister> rolesRegister;
    private List<ScenarioRegister> scenariosRegister;
    private List<ConfigRegister> configsRegister;
    private List<TimerRegister> timersRegister;
    private List<CommandRegister> commandsRegister;
    private List<CommandRegister> adminCommandsRegister;
    private List<RandomEventRegister> eventRandomsRegister;

    public Register(Main main){
        this.key = "werewolf.name";

    }

    public Register(String key){
        this.key =key;
        this.rolesRegister = new ArrayList<>();
        this.scenariosRegister = new ArrayList<>();
        this.configsRegister = new ArrayList<>();
        this.timersRegister = new ArrayList<>();
        this.commandsRegister = new ArrayList<>();
        this.adminCommandsRegister = new ArrayList<>();
        this.eventRandomsRegister = new ArrayList<>();
    }

    @Override
    public Optional<IRegisterManager> getRegister(String key){
        if(this.key.equals(key)){
            return Optional.of(this);
        }
        if(this.addonsRegister.isEmpty()){
            return Optional.empty();
        }
        return this.addonsRegister
                .values()
                .stream()
                .map(addonRegister -> addonRegister.getRegister(key))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    @Override
    public List<? extends RoleRegister> getRolesRegister() {
        List<RoleRegister> rolesRegister1 = this.addonsRegister
                .values()
                .stream().map(Register::getRolesRegister)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        rolesRegister1.addAll(this.rolesRegister);

        return rolesRegister1;
    }

    @Override
    public List<? extends ScenarioRegister> getScenariosRegister() {
        List<ScenarioRegister> scenariosRegister1 = this.addonsRegister
                .values()
                .stream()
                .map(Register::getScenariosRegister)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        scenariosRegister1.addAll(this.scenariosRegister);

        return scenariosRegister1;
    }

    @Override
    public List<? extends ConfigRegister> getConfigsRegister() {
        List<ConfigRegister> scenariosRegister1 = this.addonsRegister
                .values()
                .stream()
                .map(Register::getConfigsRegister)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        scenariosRegister1.addAll(this.configsRegister);

        return scenariosRegister1;
    }

    @Override
    public List<? extends TimerRegister> getTimersRegister() {
        List<TimerRegister> timerRegisters = this.addonsRegister
                .values()
                .stream()
                .map(Register::getTimersRegister)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        timerRegisters.addAll(this.timersRegister);

        return timerRegisters;
    }

    @Override
    public List<? extends CommandRegister> getCommandsRegister() {
        List<CommandRegister> commandRegisters = this.addonsRegister
                .values()
                .stream()
                .map(Register::getCommandsRegister)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        commandRegisters.addAll(this.commandsRegister);

        return commandRegisters;
    }

    @Override
    public List<? extends CommandRegister> getAdminCommandsRegister() {
        List<CommandRegister> commandRegisters = this.addonsRegister
                .values()
                .stream()
                .map(Register::getAdminCommandsRegister)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        commandRegisters.addAll(this.adminCommandsRegister);

        return commandRegisters;
    }

    @Override
    public List<? extends AddonRegister> getAddonsRegister() {
        List<AddonRegister> addonRegisters = this.addonsRegister
                .values()
                .stream()
                .map(Register::getAddonsRegister)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        addonRegisters.addAll(this.addonsRegister.keySet());

        return addonRegisters;
    }

    @Override
    public List<? extends RandomEventRegister> getRandomEventsRegister() {
        List<RandomEventRegister> randomEventRegisters = this.addonsRegister
                .values()
                .stream()
                .map(Register::getRandomEventsRegister)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        randomEventRegisters.addAll(this.eventRandomsRegister);

        return randomEventRegisters;
    }

    @Override
    public void registerAddon(AddonRegister addonRegister) {
        List<AddonRegister> addonRegisters = new ArrayList<>();
        register(addonRegister,
                addonRegisters,
                iRegisterManager -> iRegisterManager.registerAddon(addonRegister));
        if(!addonRegisters.isEmpty()){
            this.addonsRegister.put(addonRegister,new Register(addonRegister.getKey()));
        }
    }

    @Override
    public void registerRole(RoleRegister roleRegister) {
        register(roleRegister,
                this.rolesRegister,
                iRegisterManager -> iRegisterManager.registerRole(roleRegister));
    }

    @Override
    public void registerScenario(ScenarioRegister scenarioRegister) {
        register(scenarioRegister,
                this.scenariosRegister,
                iRegisterManager -> iRegisterManager.registerScenario(scenarioRegister));
    }

    @Override
    public void registerConfig(ConfigRegister configRegister) {
        register(configRegister,
                this.configsRegister,
                iRegisterManager -> iRegisterManager.registerConfig(configRegister));
    }

    @Override
    public void registerTimer(TimerRegister timerRegister) {
        register(timerRegister,
                this.timersRegister,
                iRegisterManager -> iRegisterManager.registerTimer(timerRegister));
    }

    @Override
    public void registerCommands(CommandRegister commandRegister) {
        register(commandRegister,
                this.commandsRegister,
                iRegisterManager -> iRegisterManager.registerCommands(commandRegister));
    }

    @Override
    public void registerRandomEvents(RandomEventRegister randomEventRegister) {
        register(randomEventRegister,
                this.eventRandomsRegister,
                iRegisterManager -> iRegisterManager.registerRandomEvents(randomEventRegister));
    }

    @Override
    public void registerAdminCommands(CommandRegister commandRegister) {
        register(commandRegister,
                this.adminCommandsRegister,
                iRegisterManager -> iRegisterManager.registerAdminCommands(commandRegister));
    }

    private <A extends IRegister> void register(A register, List<A> registerList, Consumer<IRegisterManager> registers) {
        Optional<AddonRegister> addonRegister = this.addonsRegister
                .keySet()
                .stream()
                .filter(addonRegister1 -> addonRegister1.getKey().equals(register.getKey()))
                .findFirst();

        if(addonRegister.isPresent()){
            registers.accept(this.addonsRegister.get(addonRegister.get()));
        }
        else{
            registerList.add(register);
        }
    }

}
