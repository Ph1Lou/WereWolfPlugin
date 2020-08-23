package io.github.ph1lou.werewolfplugin;


import io.github.ph1lou.werewolfapi.*;
import io.github.ph1lou.werewolfapi.enumlg.Category;
import io.github.ph1lou.werewolfapi.events.UpdateLanguageEvent;
import io.github.ph1lou.werewolfplugin.commands.Admin;
import io.github.ph1lou.werewolfplugin.commands.Command;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.listener.scenarioslisteners.*;
import io.github.ph1lou.werewolfplugin.roles.neutrals.*;
import io.github.ph1lou.werewolfplugin.roles.villagers.*;
import io.github.ph1lou.werewolfplugin.roles.werewolfs.*;
import io.github.ph1lou.werewolfplugin.save.FileUtils_;
import io.github.ph1lou.werewolfplugin.save.Lang;
import io.github.ph1lou.werewolfplugin.utils.VersionUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin implements GetWereWolfAPI, Listener {


    private final Lang lang = new Lang(this);
    private GameManager currentGame;
    private final List<RoleRegister> rolesRegister = new ArrayList<>();
    private final Map<String, String> extraTexts = new HashMap<>();
    private final Map<Plugin, String> defaultLanguages = new HashMap<>();
    private final List<Plugin> listAddons = new ArrayList<>();
    private final Map<String, Commands> listCommands = new HashMap<>();
    private final Map<String, Commands> listAdminCommands = new HashMap<>();
    private final List<ScenarioRegister> scenariosRegister = new ArrayList<>();
    private final List<ConfigRegister> configsRegister = new ArrayList<>();
    private final List<TimerRegister> timersRegister = new ArrayList<>();

    public GameManager getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(GameManager currentGame) {
        this.currentGame = currentGame;
    }

    @Override
    public Map<String, Commands> getListCommands() {
        return listCommands;
    }

    @Override
    public Map<String, Commands> getListAdminCommands() {
        return listAdminCommands;
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
        registerRole();
        registerScenario();
        registerTimers();
        registerConfig();
        listAddons.add(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        currentGame = new GameManager(this);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            setWorld();
            getCurrentGame().init();
            getCurrentGame().createMap();
            getCommand("a").setExecutor(new Admin(this));
        }, 60);
    }


    @Override
	public void onLoad() {
        VersionUtils.getVersionUtils().patchBiomes();
    }


    @Override
    public WereWolfAPI getWereWolfAPI() {
        return getCurrentGame();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (getCurrentGame().isState(null)) {
            player.kickPlayer("Waiting Addons");
        }
    }

    public Map<Plugin, String> getDefaultLanguages() {
        return defaultLanguages;
    }


    @EventHandler
    public void onLanguageChange(UpdateLanguageEvent event) {
        getCommand("ww").setExecutor(new Command(this, getCurrentGame()));
    }

    @Override
    public List<RoleRegister> getRegisterRoles() {
        return this.rolesRegister;
    }

    @Override
    public Map<String, String> getExtraTexts() {
        return this.extraTexts;
    }


    public void setWorld() {

        World world = Bukkit.getWorlds().get(0);
        world.setWeatherDuration(0);
        world.setThundering(false);
        VersionUtils.getVersionUtils().setGameRuleValue(world, "doFireTick", false);
        VersionUtils.getVersionUtils().setGameRuleValue(world, "reducedDebugInfo", true);
        VersionUtils.getVersionUtils().setGameRuleValue(world, "naturalRegeneration", false);
        VersionUtils.getVersionUtils().setGameRuleValue(world, "keepInventory", true);

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

    private void registerRole() {
        try {
            new RoleRegister(this, this,"werewolf.role.cupid.display").registerRole(Cupid.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.werewolf.display").registerRole(WereWolf.class).addCategory(Category.WEREWOLF).create();
            new RoleRegister(this, this,"werewolf.role.falsifier_werewolf.display").registerRole(FalsifierWereWolf.class).addCategory(Category.WEREWOLF).create();

            new RoleRegister(this, this,"werewolf.role.infect_father_of_the_wolves.display").registerRole( InfectFatherOfTheWolves.class).addCategory(Category.WEREWOLF).create();
            new RoleRegister(this, this,"werewolf.role.witch.display").registerRole(Witch.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.elder.display").registerRole(Elder.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.naughty_little_wolf.display").registerRole(NaughtyLittleWolf.class).addCategory(Category.WEREWOLF).create();
            new RoleRegister(this, this,"werewolf.role.fox.display").registerRole(Fox.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.mischievous_werewolf.display").registerRole(MischievousWereWolf.class).addCategory(Category.WEREWOLF).create();
            new RoleRegister(this, this,"werewolf.role.little_girl.display").registerRole(LittleGirl.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.wild_child.display").registerRole(WildChild.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.citizen.display").registerRole(Citizen.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.comedian.display").registerRole(Comedian.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.miner.display").registerRole(Miner.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.sister.display").registerRole(Sister.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.siamese_twin.display").registerRole(SiameseTwin.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.raven.display").registerRole(Raven.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.protector.display").registerRole(Protector.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.trapper.display").registerRole(Trapper.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.troublemaker.display").registerRole(Troublemaker.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.bear_trainer.display").registerRole(BearTrainer.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.seer.display").registerRole(Seer.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.chatty_seer.display").registerRole(ChattySeer.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.detective.display").registerRole(Detective.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.succubus.display").registerRole(Succubus.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.angel.display").registerRole(Angel.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.fallen_angel.display").registerRole(FallenAngel.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.guardian_angel.display").registerRole(GuardianAngel.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.assassin.display").registerRole(Assassin.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.serial_killer.display").registerRole(SerialKiller.class).addCategory(Category.NEUTRAL).create();

            new RoleRegister(this, this,"werewolf.role.amnesiac_werewolf.display").registerRole(AmnesicWerewolf.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.white_werewolf.display").registerRole(WhiteWereWolf.class).addCategory(Category.NEUTRAL).create();

            new RoleRegister(this, this,"werewolf.role.thief.display").registerRole(Thief.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.flute_player.display").registerRole(FlutePlayer.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.librarian.display").registerRole(Librarian.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.villager.display").registerRole(Villager.class).addCategory(Category.VILLAGER).create();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    private void registerScenario() {
        try {
            new ScenarioRegister(this, this, "werewolf.menu.scenarios.cat_eyes").registerScenario(CatEyes.class).create();
            new ScenarioRegister(this, this, "werewolf.menu.scenarios.compass_target_last_death").registerScenario(CompassTargetLastDeath.class).create();
            new ScenarioRegister(this, this, "werewolf.menu.scenarios.cut_clean").registerScenario(CutClean.class).setDefaultValue(true).create();
            new ScenarioRegister(this,this, "werewolf.menu.scenarios.diamond_limit").registerScenario(DiamondLimit.class).setDefaultValue(true).create();
            new ScenarioRegister(this,this,"werewolf.menu.scenarios.double_jump").registerScenario(DoubleJump.class).create();
            new ScenarioRegister(this,this,"werewolf.menu.scenarios.fast_smelting").registerScenario(FastSmelting.class).setDefaultValue(true).create();
            new ScenarioRegister(this,this,"werewolf.menu.scenarios.fire_less").registerScenario(FireLess.class).setDefaultValue(true).create();
            new ScenarioRegister(this,this,"werewolf.menu.scenarios.hastey_boys").registerScenario(HasteyBoys.class).setDefaultValue(true).create();
            new ScenarioRegister(this,this,"werewolf.menu.scenarios.horse_less").registerScenario(HorseLess.class).setDefaultValue(true).create();
            new ScenarioRegister(this,this,"werewolf.menu.scenarios.no_clean_up").registerScenario(NoCleanUp.class).create();
            new ScenarioRegister(this,this,"werewolf.menu.scenarios.no_egg_snowball").registerScenario(NoEggSnowBall.class).setDefaultValue(true).create();
            new ScenarioRegister(this,this,"werewolf.menu.scenarios.no_fall").registerScenario(NoFall.class).create();
            new ScenarioRegister(this,this,"werewolf.menu.scenarios.no_fire_weapons").registerScenario(NoFireWeapon.class).setDefaultValue(true).create();
            new ScenarioRegister(this,this,"werewolf.menu.scenarios.no_name_tag").registerScenario(NoNameTag.class).create();
            new ScenarioRegister(this,this,"werewolf.menu.scenarios.no_poison").registerScenario(NoPoison.class).setDefaultValue(true).create();
            new ScenarioRegister(this, this, "werewolf.menu.scenarios.rod_less").registerScenario(RodLess.class).setDefaultValue(true).create();
            new ScenarioRegister(this, this, "werewolf.menu.scenarios.slow_bow").registerScenario(SlowBow.class).create();
            new ScenarioRegister(this, this, "werewolf.menu.scenarios.timber").registerScenario(Timber.class).create();
            new ScenarioRegister(this, this, "werewolf.menu.scenarios.xp_boost").registerScenario(XpBoost.class).setDefaultValue(true).create();
            new ScenarioRegister(this, this, "werewolf.menu.scenarios.vanilla_plus").registerScenario(VanillaPlus.class).setDefaultValue(true).create();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    private void registerConfig() {
        new ConfigRegister(this, this, "werewolf.menu.global.victory_couple").setDefaultValue(false).create();
        new ConfigRegister(this, this, "werewolf.menu.global.event_seer_death").setDefaultValue(true).create();
        new ConfigRegister(this, this, "werewolf.menu.global.chat").setDefaultValue(true).create();
        new ConfigRegister(this,this, "werewolf.menu.global.compass_middle").setDefaultValue(true).create();
        new ConfigRegister(this,this,"werewolf.menu.global.show_role_to_death").setDefaultValue(true).create();
        new ConfigRegister(this,this,"werewolf.menu.global.auto_rez_infect").setDefaultValue(false).create();
        new ConfigRegister(this,this,"werewolf.menu.global.auto_rez_witch").setDefaultValue(false).create();
        new ConfigRegister(this,this,"werewolf.menu.global.polygamy").setDefaultValue(false).create();
        new ConfigRegister(this,this,"werewolf.menu.global.vote").setDefaultValue(true).create();
        new ConfigRegister(this,this,"werewolf.menu.global.hide_composition").setDefaultValue(false).create();
        new ConfigRegister(this,this,"werewolf.menu.global.red_name_tag").setDefaultValue(true).create();
        new ConfigRegister(this,this,"werewolf.menu.global.seer_every_other_day").setDefaultValue(true).create();
        new ConfigRegister(this,this,"werewolf.menu.global.proximity_chat").setDefaultValue(false).create();
    }

    private void registerTimers() {

        new TimerRegister(this,this,"werewolf.menu.timers.invulnerability").setDefaultValue(30).create();
        new TimerRegister(this,this,"werewolf.menu.timers.role_duration").setDefaultValue(1200).create();
        new TimerRegister(this, this, "werewolf.menu.timers.pvp").setDefaultValue(1500).create();
        new TimerRegister(this,this,"werewolf.menu.timers.werewolf_list").setDefaultValue(600).create();

        new TimerRegister(this,this,"werewolf.menu.timers.vote_begin").setDefaultValue(2400).create();
        new TimerRegister(this,this,"werewolf.menu.timers.border_begin").setDefaultValue(3600).create();

        new TimerRegister(this,this,"werewolf.menu.timers.digging_end").setDefaultValue(4200).create();

        new TimerRegister(this,this, "werewolf.menu.timers.border_duration").setDefaultValue(280).create();

        new TimerRegister(this, this, "werewolf.menu.timers.vote_duration").setDefaultValue(180).create();
        new TimerRegister(this, this, "werewolf.menu.timers.citizen_duration").setDefaultValue(60).create();
        new TimerRegister(this,this,"werewolf.menu.timers.model_duration").setDefaultValue(240).create();
        new TimerRegister(this,this,"werewolf.menu.timers.lover_duration").setDefaultValue(240).create();
        new TimerRegister(this,this,"werewolf.menu.timers.angel_duration").setDefaultValue(240).create();
        new TimerRegister(this,this,"werewolf.menu.timers.power_duration").setDefaultValue(240).create();
        new TimerRegister(this,this,"werewolf.menu.timers.fox_smell_duration").setDefaultValue(120).create();

        new TimerRegister(this,this,"werewolf.menu.timers.succubus_duration").setDefaultValue(180).create();

        new TimerRegister(this,this,"werewolf.menu.timers.day_duration").setDefaultValue(300).create();

    }


    public Lang getLang() {
        return lang;
    }

    @Override
    public List<ScenarioRegister> getRegisterScenarios() {
        return this.scenariosRegister;
    }

    @Override
    public List<ConfigRegister> getRegisterConfigs() {
        return this.configsRegister;
    }

    @Override
    public List<TimerRegister> getRegisterTimers() {
        return this.timersRegister;
    }
}
		



