package io.github.ph1lou.werewolfplugin.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.ILoverManager;
import io.github.ph1lou.werewolfapi.IMapManager;
import io.github.ph1lou.werewolfapi.IModerationManager;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.IScoreBoard;
import io.github.ph1lou.werewolfapi.IStuffManager;
import io.github.ph1lou.werewolfapi.IVoteManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.UpdateLanguageEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.LoadEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalJoinEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.save.Configuration;
import io.github.ph1lou.werewolfplugin.save.FileUtils_;
import io.github.ph1lou.werewolfplugin.save.LanguageManager;
import io.github.ph1lou.werewolfplugin.save.Stuff;
import io.github.ph1lou.werewolfplugin.scoreboards.ScoreBoard;
import io.github.ph1lou.werewolfplugin.tasks.LobbyTask;
import io.github.ph1lou.werewolfplugin.utils.UpdateChecker;
import io.github.ph1lou.werewolfplugin.utils.random_config.RandomConfig;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameManager implements WereWolfAPI {

    private final Main main;
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private final Map<UUID, IPlayerWW> playerLG = new HashMap<>();
    private StateGame state;
    private Day day;
    private boolean debug = false;
    private final ScoreBoard score = new ScoreBoard(this);
    private final Vote vote = new Vote(this);
    private final LoversManagement loversManage = new LoversManagement(this);
    private final ModerationManager moderationManager = new ModerationManager(this);
    private final MapManager mapManager;
    private Configuration configuration;
    private final End end = new End(this);
    private final Stuff stuff;
    private final RandomConfig randomConfig;
    private final ListenersLoader scenarios;
    private final Random r = new Random(System.currentTimeMillis());
    private final UUID gameUUID = UUID.randomUUID();
    private String gameName = "@Ph1Lou_";


    public GameManager(Main main) {
        this.main = main;
        this.randomConfig = new RandomConfig(main);
        this.configuration = new Configuration(main.getRegisterManager());
        mapManager = new MapManager(main);
        stuff = new Stuff(main);
        scenarios = new ListenersLoader(main);
        File mapFolder = new File(main.getDataFolder() +
                File.separator + "maps");
        if (!mapFolder.exists()) {
            if (!mapFolder.mkdirs()) {
                Bukkit.getLogger().warning("[WereWolfPlugin] Folder Map Creation Failed");
            }
        }
        setDay(Day.DAY);

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            Bukkit.getPluginManager().callEvent(new UpdateLanguageEvent());
            FileUtils_.loadConfig(main, "saveCurrent");
            main.getWereWolfAPI().getStuffs().load("saveCurrent");
            scenarios.init();
        });
        setState(StateGame.LOBBY);
        Bukkit.getPluginManager().callEvent(new LoadEvent(this));
        LobbyTask start = new LobbyTask(this);
        start.runTaskTimer(main, 0, 20);
    }


    public void join(Player player) {

        UUID uuid = player.getUniqueId();

        if (moderationManager.getWhiteListedPlayers().contains(uuid)) {
            finalJoin(player);
        } else if (score.getPlayerSize() >= configuration.getPlayerMax()) {
            player.sendMessage(translate("werewolf.check.full"));
            moderationManager.addQueue(player);
        } else if (configuration.isWhiteList()) {
            player.sendMessage(translate("werewolf.commands.admin.whitelist.player_not_whitelisted"));
            moderationManager.addQueue(player);
        } else {
            finalJoin(player);
        }
    }

    public void finalJoin(Player player) {

        UUID uuid = player.getUniqueId();

        if (this.getPlayerWW(uuid) != null) return;

        moderationManager.getQueue().remove(uuid);
        score.addPlayerSize();
        Bukkit.broadcastMessage(translate("werewolf.announcement.join", score.getPlayerSize(), score.getRole(), player.getName()));
        clearPlayer(player);
        player.setGameMode(GameMode.ADVENTURE);
        IPlayerWW plg = new PlayerWW(main, player);
        playerLG.put(uuid, plg);
        Bukkit.getPluginManager().callEvent(new FinalJoinEvent(plg));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false));
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

        new UpdateChecker(main, 73113).getVersion(version -> {
            DefaultArtifactVersion siteVersion = new DefaultArtifactVersion(version);
            DefaultArtifactVersion loadVersion = new DefaultArtifactVersion(main.getDescription().getVersion());
            if (siteVersion.compareTo(loadVersion) >= 0) {
                player.sendMessage(translate("werewolf.update.up_to_date"));
            } else {
                player.sendMessage(translate("werewolf.update.out_of_date"));
            }
        });

    }


    public void addLatePlayer(Player player) {

        clearPlayer(player);

        Inventory inventory = player.getInventory();

        player.setGameMode(GameMode.SURVIVAL);
        IPlayerWW playerWW = new PlayerWW(main, player);
        playerLG.put(player.getUniqueId(), playerWW);
        Location spawn = mapManager.getWorld().getSpawnLocation();
        spawn.setY(spawn.getBlockY() - 4);
        playerWW.setSpawn(spawn);
        score.addPlayerSize();

        for (int j = 0; j < 40; j++) {
            inventory.setItem(j, stuff.getStartLoot().getItem(j));
        }

        mapManager.transportation(playerWW, 0);
    }

    public void clearPlayer(Player player) {

        PlayerInventory inventory = player.getInventory();
        VersionUtils.getVersionUtils().setPlayerMaxHealth(player, 20);
        player.setHealth(20);
        player.setExp(0);
        player.setLevel(0);
        inventory.clear();
        inventory.setHelmet(null);
        inventory.setChestplate(null);
        inventory.setLeggings(null);
        inventory.setBoots(null);

        for (PotionEffect po : player.getActivePotionEffects()) {
            player.removePotionEffect(po.getType());
        }
    }

    public void setState(StateGame state) {
        this.state = state;
    }

    @Override
    public boolean isState(StateGame state) {
        return this.state == state;
    }

    @Override
    public String getGameName() {
        return gameName;
    }

    @Override
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    @Override
    public boolean isDay(Day day) {
        return this.day == day;
    }


    @Override
    public void checkVictory() {
        end.check_victory();
    }


    @Override
    public void stopGame() {

        if (!main.getWereWolfAPI().equals(this)) return;

        Bukkit.getPluginManager().callEvent(new StopEvent(this));

        scenarios.delete();

        main.createGame();

        GameManager newGame = (GameManager) main.getWereWolfAPI();

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                FastBoard fastboard = new FastBoard(player);
                fastboard.updateTitle(newGame.translate("werewolf.score_board.title"));
                newGame.boards.put(player.getUniqueId(), fastboard);
                player.setGameMode(GameMode.ADVENTURE);
                newGame.join(player);
            }

            if (score.getTimer() <= 60) {
                newGame.getMapManager().generateMap(newGame.getConfig().getBorderMax());
            }
        }, 10);

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> player.getWorld().equals(mapManager.getWorld()))
                .forEach(player -> player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()));

        if (score.getTimer() > 60) { //Si la game a commencé depuis moins d'une minute on ne delete pas la map
            mapManager.deleteMap();
        }
    }

    @Override
    public Collection<? extends IPlayerWW> getPlayerWW() {
        return this.playerLG.values();
    }


    @Override
    @Nullable
    public IPlayerWW getPlayerWW(UUID uuid) {

        if (!this.playerLG.containsKey(uuid)) {
            return null;
        }

        return this.playerLG.get(uuid);
    }


    @Override
    public IConfiguration getConfig() {
        return this.configuration;
    }

    public void setConfig(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public String translate(String key, Object... args) {
        LanguageManager languageManager = (LanguageManager) main.getLangManager();
        String translation = languageManager.getTranslation(key);
        try {
            return String.format(translation, args);
        } catch (IllegalFormatException e) {
            Bukkit.getConsoleSender().sendMessage(String.format("Error while formatting translation (%s)", key.toLowerCase()));
            return translation + " (Format error)";
        }
    }

    @Override
    public List<String> translateArray(String key) {
        LanguageManager languageManager = (LanguageManager) main.getLangManager();
        return languageManager.getTranslationList(key);
    }


    @Override
    public UUID getGameUUID() {
        return gameUUID;
    }

    @Override
    public Random getRandom() {
        return r;
    }

    @Override
    public IPlayerWW autoSelect(IPlayerWW playerWW) {

        List<IPlayerWW> players = playerLG.values()
                .stream()
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .filter(playerWW1 -> !playerWW1.equals(playerWW))
                .collect(Collectors.toList());

        if (players.isEmpty()) {
            return playerWW;
        }

        return players.get((int) Math.floor(getRandom().nextFloat() * players.size()));
    }

    @Override
    public IVoteManager getVote() {
        return this.vote;
    }

    @Override
    public void resurrection(IPlayerWW playerWW) {
        Bukkit.getPluginManager().callEvent(new ResurrectionEvent(playerWW));
    }

    @Override
    public void death(IPlayerWW playerWW) {
        Bukkit.getPluginManager().callEvent(new FinalDeathEvent(playerWW));
    }


    @Override
    public IStuffManager getStuffs() {
        return stuff;
    }

    @Override
    public ILoverManager getLoversManager() {
        return loversManage;
    }

    @Override
    public IScoreBoard getScore() {
        return score;
    }

    public Map<UUID, FastBoard> getBoards() {
        return boards;
    }

    public Main getMain() {
        return main;
    }

    @Override
    public IMapManager getMapManager() {
        return mapManager;
    }

    @Override
    public IModerationManager getModerationManager() {
        return moderationManager;
    }

    @Override
    public StateGame getState() {
        return this.state;
    }


    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void remove(UUID uuid) {
        playerLG.remove(uuid);
    }

    public RandomConfig getRandomConfig() {
        return randomConfig;
    }
}
