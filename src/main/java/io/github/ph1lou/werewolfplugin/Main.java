package io.github.ph1lou.werewolfplugin;

import fr.minuskube.inv.InventoryManager;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.LangManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.registers.RegisterManager;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.commands.Admin;
import io.github.ph1lou.werewolfplugin.commands.Command;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.game.MapManager;
import io.github.ph1lou.werewolfplugin.save.Lang;
import io.github.ph1lou.werewolfplugin.statistiks.Events;
import io.github.ph1lou.werewolfplugin.statistiks.GameReview;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;

public class Main extends JavaPlugin implements GetWereWolfAPI {

    private final Lang lang = new Lang(this);
    private GameManager currentGame;
    private final Register register = new Register(this);
    private final InventoryManager invManager = new InventoryManager(this);
    private GameReview currentGameReview;
    private UUID serverUUID;

    @Override
    public InventoryManager getInvManager() {
        return invManager;
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();
        if (getConfig().getString("server_uuid").isEmpty()) {
            getConfig().set("server_uuid", UUID.randomUUID().toString());
            saveConfig();
        }
        this.serverUUID = UUID.fromString(Objects.requireNonNull(getConfig().getString("server_uuid")));

        this.invManager.init();
        Bukkit.getPluginManager().registerEvents(new Events(this), this);
        Bukkit.getPluginManager().registerEvents(lang, this);
        currentGame = new GameManager(this);
        Objects.requireNonNull(getCommand("a")).setExecutor(new Admin(this));
        Objects.requireNonNull(getCommand("ww")).setExecutor(new Command(this));

        MapManager mapManager = currentGame.getMapManager();
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, mapManager::init);
        Bukkit.getServicesManager()
                .register(GetWereWolfAPI.class,
                        this,
                        this,
                        ServicePriority.Normal);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    ActionBarEvent actionBarEvent = new ActionBarEvent(player.getUniqueId());
                    Bukkit.getPluginManager().callEvent(actionBarEvent);
                    VersionUtils.getVersionUtils().sendActionBar(player, actionBarEvent.getActionBar());
                }), 0, 4);
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


    public void createGame() {
        this.currentGame = new GameManager(this);
    }

    public GameReview getCurrentGameReview() {
        return currentGameReview;
    }

    public void setCurrentGameReview(GameReview currentGameReview) {
        this.currentGameReview = currentGameReview;
    }

    public UUID getServerUUID() {
        return serverUUID;
    }
}

