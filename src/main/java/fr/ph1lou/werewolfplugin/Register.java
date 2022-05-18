package fr.ph1lou.werewolfplugin;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.DisableAutoLoad;
import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.annotations.Lover;
import fr.ph1lou.werewolfapi.annotations.ModuleWerewolf;
import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.registers.IRegisterManager;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings({"unchecked"})
public class Register implements IRegisterManager {

    private final Set<Wrapper<JavaPlugin, ModuleWerewolf>> modules = new HashSet<>();

    private final Set<Wrapper<IRole, Role>> roles = new HashSet<>();
    private final Set<Wrapper<ListenerManager, Scenario>> scenarios = new HashSet<>();
    private final Set<Wrapper<ListenerManager, Event>> events = new HashSet<>();
    private final Set<Wrapper<ICommand, PlayerCommand>> commands = new HashSet<>();
    private final Set<Wrapper<ICommandRole, RoleCommand>> roleCommands = new HashSet<>();
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
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            ModuleWerewolf moduleWerewolf = plugin.getClass().getAnnotation(ModuleWerewolf.class);
            if(moduleWerewolf != null){

                if(moduleWerewolf.key().startsWith("werewolf.") && !plugin.equals(main)){
                    Bukkit.getLogger().warning(String.format("Addon keys %s can't start with 'werewolf.'", moduleWerewolf.key()));
                    continue;
                }

                String prefix = moduleWerewolf.key().split("\\.")[0];


                if(this.modules.stream().anyMatch(javaPluginModuleWerewolfWrapper -> javaPluginModuleWerewolfWrapper.getAddonKey().split("\\.")[0]
                        .equals(prefix))){
                    Bukkit.getLogger().warning(String.format("An addon key already starts with %s", prefix));
                    continue;
                }

                this.modules.add(new Wrapper<>(JavaPlugin.class,
                        moduleWerewolf,
                        moduleWerewolf.key(),
                        (JavaPlugin)plugin));
                this.register(plugin.getClass().getPackage().getName(),
                        moduleWerewolf,
                        plugin,
                        prefix);
            }
        }
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

    public void register(String packageName, ModuleWerewolf addon, Plugin plugin, String prefix){

        try {
            ReflectionUtils.findAllClasses(plugin, packageName)
                    .forEach(clazz -> {

                        if(clazz.getAnnotation(DisableAutoLoad.class) != null){
                            if(clazz.getAnnotation(DisableAutoLoad.class).isDisable()){
                                return;
                            }
                        }

                        if(clazz.getAnnotation(Role.class) != null){

                            if(IRole.class.isAssignableFrom(clazz)){

                                Role role = clazz.getAnnotation(Role.class);

                                if(role.key().startsWith(prefix)){
                                    this.roles.add(new Wrapper<>((Class<IRole>)clazz,
                                            role,
                                            addon.key(),
                                            null));
                                }
                                else{
                                    Bukkit.getLogger().warning(String.format(
                                            "The role key %s does not have the same prefix as the addon key %s",
                                            role.key(), prefix));
                                }

                            }
                        }
                        else if(clazz.getAnnotation(Scenario.class) != null){
                            if(ListenerManager.class.isAssignableFrom(clazz)){

                                Scenario scenario = clazz.getAnnotation(Scenario.class);

                                if(scenario.key().startsWith(prefix)){
                                    this.scenarios.add(new Wrapper<>((Class<ListenerManager>)clazz,
                                            scenario,
                                            addon.key(),
                                            this.instantiate((Class<ListenerManager>)clazz)));
                                }
                                else{
                                    Bukkit.getLogger().warning(String.format(
                                            "The scenario key %s does not have the same prefix as the addon key %s",
                                            scenario.key(), prefix));
                                }

                            }
                        }
                        else if(clazz.getAnnotation(Event.class) != null){
                            if(ListenerManager.class.isAssignableFrom(clazz)){

                                Event event = clazz.getAnnotation(Event.class);

                                if(event.key().startsWith(prefix)){
                                    this.events.add(new Wrapper<>((Class<ListenerManager>)clazz,
                                            event,
                                            addon.key(),
                                            this.instantiate((Class<ListenerManager>)clazz)));
                                }
                                else{
                                    Bukkit.getLogger().warning(String.format(
                                            "The event key %s does not have the same prefix as the addon key %s",
                                            event.key(), prefix));
                                }
                            }
                        }
                        else if(clazz.getAnnotation(Configuration.class) != null){

                            Configuration configuration = clazz.getAnnotation(Configuration.class);

                            if(configuration.key().startsWith(prefix)){
                                this.configurations.add(new Wrapper<>((Class<ICommand>)clazz,
                                        configuration,
                                        addon.key(),
                                        this.instantiate((Class<ICommand>)clazz)));
                            }
                            else{
                                Bukkit.getLogger().warning(String.format(
                                        "The configuration key %s does not have the same prefix as the addon key %s",
                                        configuration.key(), prefix));
                            }
                        }
                        else if(clazz.getAnnotation(Timer.class) != null){

                            Timer timer = clazz.getAnnotation(Timer.class);

                            if(timer.key().startsWith(prefix)){
                                this.timers.add(new Wrapper<>((Class<ICommand>)clazz,
                                        timer,
                                        addon.key(),
                                        this.instantiate((Class<ICommand>)clazz)));
                            }
                            else{
                                Bukkit.getLogger().warning(String.format(
                                        "The timer key %s does not have the same prefix as the addon key %s",
                                        timer.key(), prefix));
                            }
                        }
                        else if(clazz.getAnnotation(PlayerCommand.class) != null){

                            if(ICommand.class.isAssignableFrom(clazz)){

                                PlayerCommand playerCommand = clazz.getAnnotation(PlayerCommand.class);

                                if(playerCommand.key().startsWith(prefix)){
                                    this.commands.add(new Wrapper<>((Class<ICommand>)clazz,
                                            playerCommand,
                                            addon.key(),
                                            this.instantiate((Class<ICommand>)clazz)));
                                }
                                else {
                                    Bukkit.getLogger().warning(String.format(
                                            "The playercommand key %s does not have the same prefix as the addon key %s",
                                            playerCommand.key(), prefix));
                                }
                            }
                        }
                        else if(clazz.getAnnotation(RoleCommand.class) != null){
                            if(ICommandRole.class.isAssignableFrom(clazz)){

                                RoleCommand roleCommand = clazz.getAnnotation(RoleCommand.class);

                                if(roleCommand.key().startsWith(prefix)){
                                    this.roleCommands.add(new Wrapper<>((Class<ICommandRole>)clazz,
                                            roleCommand,
                                            addon.key(),
                                            this.instantiate((Class<ICommandRole>)clazz)));
                                }
                                else {
                                    Bukkit.getLogger().warning(String.format(
                                            "The rolecommand key %s does not have the same prefix as the addon key %s",
                                            roleCommand.key(), prefix));
                                }
                            }
                        }
                        else if(clazz.getAnnotation(AdminCommand.class) != null){
                            if(ICommand.class.isAssignableFrom(clazz)){

                                AdminCommand adminCommand = clazz.getAnnotation(AdminCommand.class);

                                if(adminCommand.key().startsWith(prefix)){
                                    this.adminCommands.add(new Wrapper<>((Class<ICommand>)clazz,
                                            adminCommand,
                                            addon.key(),
                                            this.instantiate((Class<ICommand>)clazz)));
                                }
                                else {
                                    Bukkit.getLogger().warning(String.format(
                                            "The admincommand key %s does not have the same prefix as the addon key %s",
                                            adminCommand.key(), prefix));
                                }
                            }
                        }
                        else if(clazz.getAnnotation(Lover.class) != null){
                            if(ILover.class.isAssignableFrom(clazz)){

                                Lover lover = clazz.getAnnotation(Lover.class);

                                if(lover.key().startsWith(prefix)){
                                    this.lovers.add(new Wrapper<>((Class<ILover>)clazz,
                                            lover,
                                            addon.key(),
                                            null));
                                }
                                else {
                                    Bukkit.getLogger().warning(String.format(
                                            "The lover key %s does not have the same prefix as the addon key %s",
                                            lover.key(), prefix));
                                }

                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<String> getModuleKey(String key) {
        String addonKey = this.checkRoles(key)
                .orElseGet(() -> this.checkConfigurations(key)
                        .orElseGet(() -> this.checkTimers(key)
                                .orElseGet(() -> this.checkScenarios(key)
                                        .orElseGet(() -> this.checkEvents(key)
                                                .orElseGet(() -> this.checkRoleCommands(key)
                                                        .orElseGet(() -> this.checkCommands(key)
                                                                .orElseGet(() -> this.checkAdminCommands(key)
                                                                        .orElseGet(() -> this.checkLovers(key)
                                                                                .orElse("")))))))));

        if(addonKey.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(addonKey);
    }

    private Optional<String> checkLovers(String key) {
        return this.lovers.stream()
                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key))
                .map(Wrapper::getAddonKey)
                .findFirst();
    }

    private Optional<String> checkAdminCommands(String key) {
        return this.adminCommands.stream()
                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key))
                .map(Wrapper::getAddonKey)
                .findFirst();
    }

    private Optional<String> checkCommands(String key) {
        return this.commands.stream()
                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key))
                .map(Wrapper::getAddonKey)
                .findFirst();
    }

    private Optional<String> checkRoleCommands(String key) {
        return this.roleCommands.stream()
                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key))
                .map(Wrapper::getAddonKey)
                .findFirst();
    }

    private Optional<String> checkEvents(String key) {
        return this.events.stream()
                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().configValues()).anyMatch(intValue -> intValue.key().equals(key)) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().configurations()).anyMatch(configuration -> configuration.key().equals(key)) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().timers()).anyMatch(timer -> timer.key().equals(key)))
                .map(Wrapper::getAddonKey)
                .findFirst();
    }

    private Optional<String> checkScenarios(String key) {
        return this.scenarios.stream()
                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().configValues()).anyMatch(intValue -> intValue.key().equals(key)))
                .map(Wrapper::getAddonKey)
                .findFirst();
    }

    private Optional<String> checkTimers(String key) {
        return this.timers.stream()
                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key))
                .map(Wrapper::getAddonKey)
                .findFirst();
    }

    private Optional<String> checkConfigurations(String key) {
        return this.configurations.stream()
                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().configValues()).anyMatch(intValue -> intValue.key().equals(key)))
                .map(Wrapper::getAddonKey)
                .findFirst();
    }

    private Optional<String> checkRoles(String key) {
        return this.roles.stream()
                .filter(iRoleRoleWrapper -> iRoleRoleWrapper.getMetaDatas().key().equals(key) ||
                        Arrays.stream(iRoleRoleWrapper.getMetaDatas().timers()).anyMatch(timer -> timer.key().equals(key)) ||
                        Arrays.stream(iRoleRoleWrapper.getMetaDatas().configurations()).anyMatch(configuration -> configuration.key().equals(key)) ||
                        Arrays.stream(iRoleRoleWrapper.getMetaDatas().configValues()).anyMatch(intValue -> intValue.key().equals(key)))
                .map(Wrapper::getAddonKey)
                .findFirst();
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
    public Set<Wrapper<ICommandRole, RoleCommand>> getRoleCommandsRegister() {
        return this.roleCommands;
    }

    @Override
    public Set<Wrapper<ICommand, AdminCommand>> getAdminCommandsRegister() {
        return this.adminCommands;
    }

    @Override
    public Set<Wrapper<JavaPlugin, ModuleWerewolf>> getModulesRegister() {
        return this.modules;
    }

    @Override
    public Set<Wrapper<ListenerManager, Event>> getRandomEventsRegister() {
        return this.events;
    }
}
