package fr.ph1lou.werewolfplugin;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.DisableAutoLoad;
import fr.ph1lou.werewolfapi.annotations.Lover;
import fr.ph1lou.werewolfapi.annotations.ModuleWerewolf;
import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.annotations.RandomEvent;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.annotations.statistics.StatisticsEvent;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.registers.IRegisterManager;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings({"unchecked"})
public class Register implements IRegisterManager {

    private static Register INSTANCE;
    private final Set<Wrapper<JavaPlugin, ModuleWerewolf>> modules = new HashSet<>();
    private final Set<Wrapper<IRole, Role>> roles = new HashSet<>();
    private final Set<Wrapper<ListenerWerewolf, Scenario>> scenarios = new HashSet<>();
    private final Set<Wrapper<ListenerWerewolf, RandomEvent>> randomEvents = new HashSet<>();
    private final Set<Wrapper<ICommand, PlayerCommand>> commands = new HashSet<>();
    private final Set<Wrapper<ICommandRole, RoleCommand>> roleCommands = new HashSet<>();
    private final Set<Wrapper<ICommand, AdminCommand>> adminCommands = new HashSet<>();
    private final Set<Wrapper<?, Configuration>> configurations = new HashSet<>();
    private final Set<Wrapper<?, Timer>> timers = new HashSet<>();
    private final Set<Wrapper<ILover, Lover>> lovers = new HashSet<>();
    private final Set<Wrapper<Event, StatisticsEvent>> statisticsEvents = new HashSet<>();
    private final Map<String, JavaPlugin> addons = new HashMap<>();

    public Register(Main main) {
        INSTANCE = this;
        this.scanPlugins(main);
    }

    private void scanPlugins(Main main) {

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {

            ModuleWerewolf moduleWerewolf = plugin.getClass().getAnnotation(ModuleWerewolf.class);

            if (moduleWerewolf != null) {

                if (moduleWerewolf.key().startsWith("werewolf.") && !plugin.equals(main)) {
                    Bukkit.getLogger().severe(String.format("Addon keys %s can't start with 'werewolf.'", moduleWerewolf.key()));
                    continue;
                }

                String prefix = moduleWerewolf.key().split("\\.")[0];

                if (this.modules.stream().anyMatch(javaPluginModuleWerewolfWrapper -> javaPluginModuleWerewolfWrapper.getAddonKey().split("\\.")[0]
                        .equals(prefix))) {
                    Bukkit.getLogger().warning(String.format("An addon key already starts with %s", prefix));
                    continue;
                }

                this.addons.put(moduleWerewolf.key(), (JavaPlugin) plugin);
                this.modules.add(new Wrapper<>(JavaPlugin.class,
                        moduleWerewolf,
                        moduleWerewolf.key()));
                this.register(plugin.getClass().getPackage().getName(),
                        moduleWerewolf,
                        plugin,
                        prefix);

                if (plugin.equals(main)) { //register api too
                    this.register("fr.ph1lou.werewolfapi",
                            moduleWerewolf,
                            plugin,
                            prefix);
                }
            }
        }
    }

    public static Register get() {
        return INSTANCE;
    }

    public void register(String packageName, ModuleWerewolf addon, Plugin plugin, String prefix) {

        try {
            ReflectionUtils.findAllClasses(plugin, packageName)
                    .forEach(clazz -> {

                        if (clazz.getAnnotation(DisableAutoLoad.class) != null) {
                            if (clazz.getAnnotation(DisableAutoLoad.class).isDisable()) {
                                return;
                            }
                        }

                        if (clazz.getAnnotation(Role.class) != null) {

                            Role role = clazz.getAnnotation(Role.class);

                            if (IRole.class.isAssignableFrom(clazz)) {

                                if (role.key().startsWith(prefix)) {
                                    this.roles.add(new Wrapper<>((Class<IRole>) clazz,
                                            role,
                                            addon.key()));
                                } else {
                                    Bukkit.getLogger().warning(String.format(
                                            "The role key %s does not have the same prefix as the addon key %s",
                                            role.key(), prefix));
                                }

                            } else {
                                Bukkit.getLogger().warning(String.format("Role %s doesn't implement IRole", role.key()));
                            }
                        } else if (clazz.getAnnotation(Scenario.class) != null) {

                            Scenario scenario = clazz.getAnnotation(Scenario.class);

                            if (ListenerWerewolf.class.isAssignableFrom(clazz)) {

                                if (scenario.key().startsWith(prefix)) {
                                    this.scenarios.add(new Wrapper<>((Class<ListenerWerewolf>) clazz,
                                            scenario,
                                            addon.key()));
                                } else {
                                    Bukkit.getLogger().warning(String.format(
                                            "The scenario key %s does not have the same prefix as the addon key %s",
                                            scenario.key(), prefix));
                                }

                            } else {
                                Bukkit.getLogger().warning(String.format("Scenario %s doesn't extend ListenerWerewolf", scenario.key()));
                            }
                        } else if (clazz.getAnnotation(RandomEvent.class) != null) {

                            RandomEvent event = clazz.getAnnotation(RandomEvent.class);

                            if (ListenerWerewolf.class.isAssignableFrom(clazz)) {

                                if (event.key().startsWith(prefix)) {
                                    this.randomEvents.add(new Wrapper<>((Class<ListenerWerewolf>) clazz,
                                            event,
                                            addon.key()));
                                } else {
                                    Bukkit.getLogger().warning(String.format(
                                            "The event key %s does not have the same prefix as the addon key %s",
                                            event.key(), prefix));
                                }
                            } else {
                                Bukkit.getLogger().warning(String.format("Event %s doesn't extend ListenerWerewolf", event.key()));
                            }
                        } else if (clazz.getAnnotation(Configuration.class) != null) {

                            Configuration configuration = clazz.getAnnotation(Configuration.class);

                            if (configuration.config().key().startsWith(prefix)) {
                                this.configurations.add(new Wrapper<>((Class<ICommand>) clazz,
                                        configuration,
                                        addon.key()));
                            } else {
                                Bukkit.getLogger().warning(String.format(
                                        "The configuration key %s does not have the same prefix as the addon key %s",
                                        configuration.config().key(), prefix));
                            }
                        } else if (clazz.getAnnotation(Timer.class) != null) {

                            Timer timer = clazz.getAnnotation(Timer.class);

                            if (timer.key().startsWith(prefix)) {
                                this.timers.add(new Wrapper<>((Class<ICommand>) clazz,
                                        timer,
                                        addon.key()));
                            } else {
                                Bukkit.getLogger().warning(String.format(
                                        "The timer key %s does not have the same prefix as the addon key %s",
                                        timer.key(), prefix));
                            }
                        } else if (clazz.getAnnotation(PlayerCommand.class) != null) {

                            PlayerCommand playerCommand = clazz.getAnnotation(PlayerCommand.class);

                            if (ICommand.class.isAssignableFrom(clazz)) {

                                if (playerCommand.key().startsWith(prefix)) {
                                    this.commands.add(new Wrapper<>((Class<ICommand>) clazz,
                                            playerCommand,
                                            addon.key()));
                                } else {
                                    Bukkit.getLogger().warning(String.format(
                                            "The playercommand key %s does not have the same prefix as the addon key %s",
                                            playerCommand.key(), prefix));
                                }
                            } else {
                                Bukkit.getLogger().warning(String.format("PlayerCommand %s doesn't implement ICommand", playerCommand.key()));
                            }
                        } else if (clazz.getAnnotation(RoleCommand.class) != null) {

                            RoleCommand roleCommand = clazz.getAnnotation(RoleCommand.class);

                            if (ICommandRole.class.isAssignableFrom(clazz)) {

                                if (roleCommand.key().startsWith(prefix)) {
                                    this.roleCommands.add(new Wrapper<>((Class<ICommandRole>) clazz,
                                            roleCommand,
                                            addon.key()));
                                } else {
                                    Bukkit.getLogger().warning(String.format(
                                            "The rolecommand key %s does not have the same prefix as the addon key %s",
                                            roleCommand.key(), prefix));
                                }
                            } else {
                                Bukkit.getLogger().warning(String.format("RoleCommand %s doesn't implement ICommandRole", roleCommand.key()));
                            }
                        } else if (clazz.getAnnotation(AdminCommand.class) != null) {

                            AdminCommand adminCommand = clazz.getAnnotation(AdminCommand.class);

                            if (ICommand.class.isAssignableFrom(clazz)) {

                                if (adminCommand.key().startsWith(prefix)) {
                                    this.adminCommands.add(new Wrapper<>((Class<ICommand>) clazz,
                                            adminCommand,
                                            addon.key()));
                                } else {
                                    Bukkit.getLogger().warning(String.format(
                                            "The admincommand key %s does not have the same prefix as the addon key %s",
                                            adminCommand.key(), prefix));
                                }
                            } else {
                                Bukkit.getLogger().warning(String.format("AdminCommand %s doesn't implement ICommand", adminCommand.key()));
                            }
                        } else if (clazz.getAnnotation(Lover.class) != null) {

                            Lover lover = clazz.getAnnotation(Lover.class);

                            if (ILover.class.isAssignableFrom(clazz)) {

                                if (lover.key().startsWith(prefix)) {
                                    this.lovers.add(new Wrapper<>((Class<ILover>) clazz,
                                            lover,
                                            addon.key()));
                                } else {
                                    Bukkit.getLogger().warning(String.format(
                                            "The lover key %s does not have the same prefix as the addon key %s",
                                            lover.key(), prefix));
                                }
                            } else {
                                Bukkit.getLogger().warning(String.format("Lover %s doesn't implement ILover", lover.key()));
                            }
                        } else if (clazz.getAnnotation(StatisticsEvent.class) != null) {

                            StatisticsEvent statisticsEvent = clazz.getAnnotation(StatisticsEvent.class);

                            if (Event.class.isAssignableFrom(clazz)) {

                                if (statisticsEvent.key().startsWith(prefix)) {
                                    this.statisticsEvents.add(new Wrapper<>((Class<Event>) clazz,
                                            statisticsEvent,
                                            addon.key()));
                                } else {
                                    Bukkit.getLogger().warning(String.format(
                                            "The event key %s does not have the same prefix as the addon key %s",
                                            statisticsEvent.key(), prefix));
                                }
                            } else {
                                Bukkit.getLogger().warning(String.format(
                                        "The event class %s doesn't extend Event bukkit class",
                                        clazz.getName()));
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

        if (addonKey.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(addonKey);
    }

    private Optional<String> checkLovers(String key) {
        return this.lovers.stream()
                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().configurations()).anyMatch(configuration -> configuration.config().key().equals(key)) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().timers()).anyMatch(timer -> timer.key().equals(key)) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().configValues()).anyMatch(intValue -> intValue.key().equals(key)))
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
        return this.randomEvents.stream()
                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().configValues()).anyMatch(intValue -> intValue.key().equals(key)) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().configurations()).anyMatch(configuration -> configuration.config().key().equals(key)) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().timers()).anyMatch(timer -> timer.key().equals(key)))
                .map(Wrapper::getAddonKey)
                .findFirst();
    }

    private Optional<String> checkScenarios(String key) {
        return this.scenarios.stream()
                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().key().equals(key) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().timers()).anyMatch(timer -> timer.key().equals(key)) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().configurations()).anyMatch(configuration -> configuration.config().key().equals(key)) ||
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
                .filter(configurationWrapper -> configurationWrapper.getMetaDatas().config().key().equals(key) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().configValues()).anyMatch(intValue -> intValue.key().equals(key)) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().timers()).anyMatch(timer -> timer.key().equals(key)) ||
                        Arrays.stream(configurationWrapper.getMetaDatas().configurations()).anyMatch(configurationBasic -> configurationBasic.key().equals(key)))
                .map(Wrapper::getAddonKey)
                .findFirst();
    }

    private Optional<String> checkRoles(String key) {
        return this.roles.stream()
                .filter(iRoleRoleWrapper -> iRoleRoleWrapper.getMetaDatas().key().equals(key) ||
                        Arrays.stream(iRoleRoleWrapper.getMetaDatas().timers()).anyMatch(timer -> timer.key().equals(key)) ||
                        Arrays.stream(iRoleRoleWrapper.getMetaDatas().configurations()).anyMatch(configuration -> configuration.config().key().equals(key)) ||
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
    public Set<Wrapper<ListenerWerewolf, Scenario>> getScenariosRegister() {
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
    public Set<Wrapper<ListenerWerewolf, RandomEvent>> getRandomEventsRegister() {
        return this.randomEvents;
    }

    @Override
    public Optional<JavaPlugin> getAddon(String key) {
        return Optional.ofNullable(this.addons.get(key));
    }

    @Override
    public Optional<Category> getCategory(String key) {

        Optional<Category> category = Category.fromKey(key);

        if (category.isPresent()) {
            return category;
        }

        return this.roles.stream()
                .map(Wrapper::getMetaDatas)
                .filter(role -> role.key().equals(key))
                .findFirst()
                .map(Role::category);
    }

    @Override
    public Set<Wrapper<Event, StatisticsEvent>> getEventsClass() {
        return this.statisticsEvents;
    }
}
