package fr.ph1lou.werewolfplugin;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.annotations.Lover;
import fr.ph1lou.werewolfapi.annotations.ModuleWerewolf;
import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.registers.IRegisterManager;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.utils.ReflectionUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings({"unchecked"})
public class Register implements IRegisterManager {

    private final Set<Wrapper<JavaPlugin, ModuleWerewolf>> addons = new HashSet<>();

    private final Set<Wrapper<IRole, Role>> roles = new HashSet<>();
    private final Set<Wrapper<ListenerManager, Scenario>> scenarios = new HashSet<>();
    private final Set<Wrapper<ListenerManager, Event>> events = new HashSet<>();
    private final Set<Wrapper<ICommand, PlayerCommand>> commands = new HashSet<>();
    private final Set<Wrapper<ICommand, RoleCommand>> roleCommands = new HashSet<>();
    private final Set<Wrapper<ICommand, AdminCommand>> adminCommands = new HashSet<>();

    private final Set<Wrapper<?, Configuration>> configurations = new HashSet<>();
    private final Set<Wrapper<?, Timer>> timers = new HashSet<>();

    private final Set<Wrapper<ILover, Lover>> lovers = new HashSet<>();
    private final GetWereWolfAPI main;

    public static Register get() {
        return INSTANCE;
    }

    private static Register INSTANCE;

    //check keys prefix
    public Register(Main main){
        this.main = main;
        INSTANCE = this;
        this.addons.add(new Wrapper<>(JavaPlugin.class,
                main.getClass().getAnnotation(ModuleWerewolf.class),
                Main.KEY,
                main));
        this.register("fr.ph1lou.werewolfplugin", main.getClass().getAnnotation(ModuleWerewolf.class));
    }



    public <T> T instantiate(Class<T> clazz){
        if(ListenerManager.class.isAssignableFrom(clazz)){
            try {
                return clazz.getConstructor(GetWereWolfAPI.class).newInstance(main);
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if(ICommand.class.isAssignableFrom(clazz)){
            try {
                return clazz.getConstructor().newInstance();
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void register(String packageName, ModuleWerewolf addon){

        try {
            ReflectionUtils.findAllClasses(main, packageName)
                    .forEach(clazz -> {
                        if(clazz.getAnnotation(Role.class) != null){

                            if(IRole.class.isAssignableFrom(clazz)){

                                this.roles.add(new Wrapper<>((Class<IRole>)clazz,
                                        clazz.getAnnotation(Role.class),
                                        addon.key(),
                                        null));
                            }
                        }
                        else if(clazz.getAnnotation(Scenario.class) != null){
                            if(ListenerManager.class.isAssignableFrom(clazz)){
                                this.scenarios.add(new Wrapper<>((Class<ListenerManager>)clazz,
                                        clazz.getAnnotation(Scenario.class),
                                        addon.key(),
                                        this.instantiate((Class<ListenerManager>)clazz)));
                            }
                        }
                        else if(clazz.getAnnotation(Event.class) != null){
                            if(ListenerManager.class.isAssignableFrom(clazz)){
                                this.events.add(new Wrapper<>((Class<ListenerManager>)clazz,
                                        clazz.getAnnotation(Event.class),
                                        addon.key(),
                                        this.instantiate((Class<ListenerManager>)clazz)));
                            }
                        }
                        else if(clazz.getAnnotation(Configuration.class) != null){
                            this.configurations.add(new Wrapper<>((Class<ICommand>)clazz,
                                    clazz.getAnnotation(Configuration.class),
                                    addon.key(),
                                    this.instantiate((Class<ICommand>)clazz)));
                        }
                        else if(clazz.getAnnotation(Timer.class) != null){
                            this.timers.add(new Wrapper<>((Class<ICommand>)clazz,
                                    clazz.getAnnotation(Timer.class),
                                    addon.key(),
                                    this.instantiate((Class<ICommand>)clazz)));
                        }
                        else if(clazz.getAnnotation(PlayerCommand.class) != null){
                            if(ICommand.class.isAssignableFrom(clazz)){
                                this.commands.add(new Wrapper<>((Class<ICommand>)clazz,
                                        clazz.getAnnotation(PlayerCommand.class),
                                        addon.key(),
                                        this.instantiate((Class<ICommand>)clazz)));
                            }
                        }
                        else if(clazz.getAnnotation(RoleCommand.class) != null){
                            if(ICommand.class.isAssignableFrom(clazz)){
                                this.roleCommands.add(new Wrapper<>((Class<ICommand>)clazz,
                                        clazz.getAnnotation(RoleCommand.class),
                                        addon.key(),
                                        this.instantiate((Class<ICommand>)clazz)));
                            }
                        }
                        else if(clazz.getAnnotation(AdminCommand.class) != null){
                            if(ICommand.class.isAssignableFrom(clazz)){
                                this.adminCommands.add(new Wrapper<>((Class<ICommand>)clazz,
                                        clazz.getAnnotation(AdminCommand.class),
                                        addon.key(),
                                        this.instantiate((Class<ICommand>)clazz)));
                            }
                        }
                        else if(clazz.getAnnotation(Lover.class) != null){
                            if(ILover.class.isAssignableFrom(clazz)){
                                this.lovers.add(new Wrapper<>((Class<ILover>)clazz,
                                        clazz.getAnnotation(Lover.class),
                                        addon.key(),
                                        null));
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<String> getModuleKey(String key) {
        String addonKey = this.roles.stream()
                .filter(iRoleRoleWrapper -> iRoleRoleWrapper.getMetaDatas().key().equals(key))
                .map(Wrapper::getAddonKey)
                .findFirst()
                .orElseGet(() -> this.configurations.stream()
                        .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key))
                        .map(Wrapper::getAddonKey)
                        .findFirst()
                        .orElseGet(() -> this.timers.stream()
                                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key))
                                .map(Wrapper::getAddonKey)
                                .findFirst()
                                .orElseGet(() -> this.scenarios.stream()
                                        .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key))
                                        .map(Wrapper::getAddonKey)
                                        .findFirst()
                                        .orElseGet(() -> this.events.stream()
                                                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key))
                                                .map(Wrapper::getAddonKey)
                                                .findFirst()
                                                .orElseGet(() -> this.roleCommands.stream()
                                                        .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key))
                                                        .map(Wrapper::getAddonKey)
                                                        .findFirst()
                                                        .orElseGet(() -> this.commands.stream()
                                                                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key))
                                                                .map(Wrapper::getAddonKey)
                                                                .findFirst()
                                                                .orElseGet(() -> this.adminCommands.stream()
                                                                        .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key))
                                                                        .map(Wrapper::getAddonKey)
                                                                        .findFirst()
                                                                        .orElseGet(() -> this.lovers.stream()
                                                                                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key))
                                                                                .map(Wrapper::getAddonKey)
                                                                                .findFirst()
                                                                                .orElse("")))))))));

        if(addonKey.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(addonKey);
    }

    @Override
    public Set<Wrapper<IRole, Role>> getRolesRegister() {
        return this.roles;
    }

    @Override
    public Set<Wrapper<ILover, Lover>> getLoversRegister() {
        return this.lovers;
    }

    @Override
    public Set<Wrapper<ListenerManager, Scenario>> getScenariosRegister() {
        return this.scenarios;
    }

    @Override
    public Set<Wrapper<?, Configuration>> getConfigsRegister() {
        return this.configurations;
    }

    @Override
    public Set<Wrapper<?, Timer>> getTimersRegister() {
        return this.timers;
    }

    @Override
    public Set<Wrapper<ICommand, PlayerCommand>> getPlayerCommandsRegister() {
        return this.commands;
    }

    @Override
    public Set<Wrapper<ICommand, RoleCommand>> getRoleCommandsRegister() {
        return this.roleCommands;
    }

    @Override
    public Set<Wrapper<ICommand, AdminCommand>> getAdminCommandsRegister() {
        return this.adminCommands;
    }

    @Override
    public Set<Wrapper<JavaPlugin, ModuleWerewolf>> getModulesRegister() {
        return this.addons;
    }

    @Override
    public Set<Wrapper<ListenerManager, Event>> getRandomEventsRegister() {
        return this.events;
    }
}
