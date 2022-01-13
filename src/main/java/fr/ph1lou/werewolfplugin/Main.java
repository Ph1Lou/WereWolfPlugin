package fr.ph1lou.werewolfplugin;

import fr.minuskube.inv.InventoryManager;
import fr.ph1lou.werewolfplugin.commands.Admin;
import fr.ph1lou.werewolfplugin.save.LanguageManager;
import fr.ph1lou.werewolfplugin.statistiks.Events;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.ILanguageManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.registers.interfaces.IRegisterManager;
import fr.ph1lou.werewolfapi.statistics.impl.GameReview;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.commands.Command;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.game.MapManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Main extends JavaPlugin implements GetWereWolfAPI {

    private final LanguageManager languageManager = new LanguageManager(this);
    private GameManager currentGame;
    private final RegisterManager registerManager = new RegisterManager(this);
    private final InventoryManager invManager = new InventoryManager(this);
    private GameReview currentGameReview;

    @Override
    public InventoryManager getInvManager() {
        return invManager;
    }

    @Override
    public List<GameReview> loadPreviousGames() {
        return null; //todo
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getServicesManager()
                .register(GetWereWolfAPI.class,
                        this,
                        this,
                        ServicePriority.Normal);
        this.invManager.init();

        Bukkit.getPluginManager().registerEvents(new Events(this), this);
        Bukkit.getPluginManager().registerEvents(languageManager, this);
        currentGame = new GameManager(this);
        Objects.requireNonNull(getCommand("a")).setExecutor(new Admin(this));
        Objects.requireNonNull(getCommand("ww")).setExecutor(new Command(this));
        MapManager mapManager = (MapManager) currentGame.getMapManager();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, mapManager::init);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    ActionBarEvent actionBarEvent = new ActionBarEvent(player.getUniqueId());
                    Bukkit.getPluginManager().callEvent(actionBarEvent);
                    VersionUtils.getVersionUtils().sendActionBar(player, actionBarEvent.getActionBar());
                }), 0, 4);
    }

    @Override
    public void onLoad() {
        new Replacer();
    }

    @Override
    public WereWolfAPI getWereWolfAPI() {
        return currentGame;
    }

    @Override
    public IRegisterManager getRegisterManager() {
        return this.registerManager;
    }

    @Override
    public Optional<IRegisterManager> getAddonRegisterManager(String addonKey) {
        return this.registerManager.getRegister(addonKey);
    }

    @Override
    public ILanguageManager getLangManager() {
        return languageManager;
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

}

