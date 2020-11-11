package io.github.ph1lou.werewolfplugin;


import fr.minuskube.inv.InventoryManager;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.LangManager;
import io.github.ph1lou.werewolfapi.RegisterManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.commands.Admin;
import io.github.ph1lou.werewolfplugin.commands.Command;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.save.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin implements GetWereWolfAPI, Listener {


    private final Lang lang = new Lang(this);
    private GameManager currentGame;
    private final Register register = new Register(this);
    private final InventoryManager invManager = new InventoryManager(this);

    @Override
    public InventoryManager getInvManager() {
        return invManager;
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();
        this.invManager.init();
        Bukkit.getPluginManager().registerEvents(lang, this);
        Bukkit.getPluginManager().registerEvents(this, this);
        currentGame = new GameManager(this);
        Objects.requireNonNull(getCommand("a")).setExecutor(new Admin(this));
        Objects.requireNonNull(getCommand("ww")).setExecutor(new Command(this));

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            setWorld();
            currentGame.init();
            currentGame.getMapManager().createMap();
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Bukkit.getOnlinePlayers()
                    .forEach(player -> {
                        if (currentGame == null || !currentGame.isState(StateGame.TRANSPORTATION)) {
                            ActionBarEvent actionBarEvent = new ActionBarEvent(player.getUniqueId(), "");
                            Bukkit.getPluginManager().callEvent(actionBarEvent);
                            VersionUtils.getVersionUtils().sendActionBar(player, actionBarEvent.getActionBar());
                        }
                    }), 0, 4);
        }, 5);
    }


    @Override
    public void onLoad() {
        VersionUtils.getVersionUtils().patchBiomes();
    }

    @Override
    public WereWolfAPI getWereWolfAPI() {
        return currentGame;
    }

    @Override
    public RegisterManager getRegisterManager() {
        return register;
    }

    @Override
    public LangManager getLangManager() {
        return lang;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (getWereWolfAPI().isState(null)) {
            player.kickPlayer("Waiting Addons");
        }
    }

    public void createGame() {

        this.currentGame = new GameManager(this);
        this.currentGame.init();
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
        world.getWorldBorder().reset();

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






}

