package fr.ph1lou.werewolfplugin;

import fr.minuskube.inv.InventoryManager;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Author;
import fr.ph1lou.werewolfapi.annotations.ModuleWerewolf;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.registers.IRegisterManager;
import fr.ph1lou.werewolfapi.statistics.impl.GameReview;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.commands.Admin;
import fr.ph1lou.werewolfplugin.commands.Command;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.game.MapManager;
import fr.ph1lou.werewolfplugin.statistiks.StatisticsEvents;
import fr.ph1lou.werewolfplugin.statistiks.StatistiksUtils;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.List;
import java.util.Objects;

@ModuleWerewolf(key = Main.KEY,
        loreKeys = "werewolf.description_plugin",
        item = UniversalMaterial.ANVIL,
        defaultLanguage = "fr_FR",
        authors = @Author(uuid = "056be797-2a0b-4807-9af5-37faf5384396",
                name = "Ph1Lou"))
public class Main extends JavaPlugin implements GetWereWolfAPI {

    public static final String KEY = "werewolf.name";
    private WereWolfAPI currentGame;
    private Register registerManager;
    private final InventoryManager invManager = new InventoryManager(this);

    public Main()
    {
        super();
    }

    protected Main(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
    {
        super(loader, description, dataFolder, file);
    }

    @Override
    public InventoryManager getInvManager() {
        return invManager;
    }

    @Override
    public List<GameReview> loadPreviousGames() {
        throw new NotImplementedException();
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

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            this.registerManager = new Register(this); //to do before all things who need register
            BukkitUtils.registerListener(new StatisticsEvents(this));
            Objects.requireNonNull(getCommand("a")).setExecutor(new Admin(this));
            Objects.requireNonNull(getCommand("ww")).setExecutor(new Command(this));
            GameManager.createGame(this, wereWolfAPI -> this.currentGame = wereWolfAPI);
            MapManager mapManager = (MapManager) currentGame.getMapManager();
            mapManager.init(); //first game only
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Bukkit.getOnlinePlayers()
                    .forEach(player -> {
                        ActionBarEvent actionBarEvent = new ActionBarEvent(player.getUniqueId());
                        Bukkit.getPluginManager().callEvent(actionBarEvent);
                        VersionUtils.getVersionUtils().sendActionBar(player, actionBarEvent.getActionBar());
                    }), 0, 4);
        });

        StatistiksUtils.loadContributors();
    }

    @Override
    public void onDisable() {
        Bukkit.getPluginManager().callEvent(new StopEvent(this.currentGame));
    }

    @Override
    public void onLoad() {
        Replacer.replaceBiomes();
    }

    @Override
    public WereWolfAPI getWereWolfAPI() {
        return currentGame;
    }

    @Override
    public IRegisterManager getRegisterManager() {
        return this.registerManager;
    }

    public void createGame() {
        GameManager.createGame(this, wereWolfAPI -> this.currentGame = wereWolfAPI);
    }
}

