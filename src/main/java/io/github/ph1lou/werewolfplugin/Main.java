package io.github.ph1lou.werewolfplugin;


import fr.minuskube.inv.InventoryManager;
import io.github.ph1lou.werewolfapi.*;
import io.github.ph1lou.werewolfapi.events.UpdateLanguageEvent;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.commands.Admin;
import io.github.ph1lou.werewolfplugin.commands.Command;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.save.FileUtils_;
import io.github.ph1lou.werewolfplugin.save.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin implements GetWereWolfAPI, Listener {


    private final Lang lang = new Lang(this);
    private GameManager currentGame;
    private final Map<String, String> extraTexts = new HashMap<>();
    private final Map<Plugin, String> defaultLanguages = new HashMap<>();
    private final List<Plugin> listAddons = new ArrayList<>();
    private final List<CommandRegister> listCommands = new ArrayList<>();
    private final List<CommandRegister> listAdminCommands = new ArrayList<>();
    private final Register register = new Register(this);

    private InventoryManager invManager;

    public InventoryManager getInvManager() {
        return invManager;
    }

    public GameManager getCurrentGame() {
        return currentGame;
    }

    @Override
    public void registerCommands(CommandRegister commandRegister) {
        listCommands.add(commandRegister);
    }

    @Override
    public void registerAdminCommands(CommandRegister commandRegister) {
        listAdminCommands.add(commandRegister);
    }

    @Override
    public List<Plugin> getAddonsList() {
        return this.listAddons;
    }

    @Override
    public void onEnable() {
        if (getConfig().getBoolean("multichat")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "multichatbypass");
        }
        saveDefaultConfig();
        this.register.init();
        this.invManager = new InventoryManager(this);
        this.invManager.init();
        listAddons.add(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        currentGame = new GameManager(this);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            setWorld();
            getCurrentGame().init();
            getCurrentGame().getMapManager().createMap();
            getCommand("a").setExecutor(new Admin(this));
        }, 60);
    }


    @Override
    public void onLoad() {
        VersionUtils.getVersionUtils().patchBiomes();
    }


    @Override
    public WereWolfAPI getWereWolfAPI() {
        return currentGame;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (getCurrentGame().isState(null)) {
            player.kickPlayer("Waiting Addons");
        }
    }

    @EventHandler
    public void onLanguageChange(UpdateLanguageEvent event) {
        getCommand("ww").setExecutor(new Command(this, getCurrentGame()));
    }

    @Override
    public List<RoleRegister> getRegisterRoles() {
        return this.register.getRolesRegister();
    }

    @Override
    public Map<String, String> getExtraTexts() {
        return this.extraTexts;
    }

    public Lang getLang() {
        return lang;
    }

    public void createGame() {

        this.currentGame = new GameManager(this);
        this.currentGame.init();
    }

    @Nullable
    public String getDefaultLanguages(Plugin plugin) {
        if (!defaultLanguages.containsKey(plugin)) {
            return null;
        }

        return defaultLanguages.get(plugin);
    }

    public void setWorld() {

        World world = Bukkit.getWorlds().get(0);
        world.setWeatherDuration(0);
        world.setThundering(false);
        VersionUtils.getVersionUtils().setGameRuleValue(world, "doFireTick", false);
        VersionUtils.getVersionUtils().setGameRuleValue(world, "reducedDebugInfo", true);
        VersionUtils.getVersionUtils().setGameRuleValue(world, "naturalRegeneration", false);
        VersionUtils.getVersionUtils().setGameRuleValue(world, "keepInventory", true);
        VersionUtils.getVersionUtils().setGameRuleValue(world, "announceAdvancements", false);
        int x = world.getSpawnLocation().getBlockX();
        int z = world.getSpawnLocation().getBlockZ();
        try {
            world.getWorldBorder().reset();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getConfig().getBoolean("default_lobby")) {

            world.setSpawnLocation(x, 151, z);

            for (int i = -16; i <= 16; i++) {

                for (int j = -16; j <= 16; j++) {

                    new Location(world, i + x, 150, j + z).getBlock().setType(Material.BARRIER);
                    new Location(world, i + x, 154, j + z).getBlock().setType(Material.BARRIER);
                }
                for (int j = 151; j < 154; j++) {
                    new Location(world, i + x, j, z - 16).getBlock().setType(Material.BARRIER);
                    new Location(world, i + x, j, z + 16).getBlock().setType(Material.BARRIER);
                    new Location(world, x - 16, j, i + z).getBlock().setType(Material.BARRIER);
                    new Location(world, x + 16, j, i + z).getBlock().setType(Material.BARRIER);
                }
            }
        }
    }

    @Override
    public void loadTranslation(Plugin plugin, String defaultLang) {
        extraTexts.putAll(this.lang.loadTranslations(FileUtils_.loadContent(lang.buildLanguageFile(plugin, defaultLang))));
        defaultLanguages.put(plugin, defaultLang);
    }


    @Override
    public List<ScenarioRegister> getRegisterScenarios() {
        return this.register.getScenariosRegister();
    }

    @Override
    public List<ConfigRegister> getRegisterConfigs() {
        return this.register.getConfigsRegister();
    }

    @Override
    public List<TimerRegister> getRegisterTimers() {
        return this.register.getTimersRegister();
    }


    public List<CommandRegister> getListCommands() {
        return listCommands;
    }

    public List<CommandRegister> getListAdminCommands() {
        return listAdminCommands;
    }


}

