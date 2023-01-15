package fr.ph1lou.werewolfplugin.game;

import fr.mrmicky.fastboard.FastBoard;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.LoadEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalJoinEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.ILanguageManager;
import fr.ph1lou.werewolfapi.game.IListenersManager;
import fr.ph1lou.werewolfapi.game.IMapManager;
import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.game.IStuffManager;
import fr.ph1lou.werewolfapi.game.IWerewolfChatHandler;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.lovers.ILoverManager;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.vote.IVoteManager;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.save.ConfigurationLoader;
import fr.ph1lou.werewolfplugin.save.LanguageLoader;
import fr.ph1lou.werewolfplugin.save.StuffLoader;
import fr.ph1lou.werewolfplugin.scoreboards.ScoreBoard;
import fr.ph1lou.werewolfplugin.tasks.LobbyTask;
import fr.ph1lou.werewolfplugin.utils.UpdateChecker;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

public class GameManager implements WereWolfAPI {

    private final Main main;
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private final Map<UUID, IPlayerWW> playersWW = new HashMap<>();
    private final ScoreBoard score = new ScoreBoard(this);
    private final LoversManagement loversManage = new LoversManagement(this);
    private final ModerationManager moderationManager = new ModerationManager(this);
    private final WerewolfChatHandler werewolfChatHandler = new WerewolfChatHandler();
    private final MapManager mapManager = new MapManager(this);
    private final End end = new End(this);
    private final StuffManager stuff = new StuffManager();
    private final LanguageManager languageManager = new LanguageManager(this);
    private final ListenersManager listenersManager = new ListenersManager(this);
    private final Random r = new Random(System.currentTimeMillis());
    private final UUID gameUUID = UUID.randomUUID();
    private StateGame state;
    private Day day;
    private boolean debug;
    private IVoteManager voteManager = new VoteManager(this);
    private Configuration configuration;
    private String gameName;
    private int groupSize = 5;
    private int playerSize = 0;
    private int timer = 0;
    private boolean crack = false;
    private int roleInitialSize = 0;

    private GameManager(Main main) {
        this.main = main;
    }

    public static void createGame(Main main, Consumer<WereWolfAPI> game) {
        GameManager gameManager = new GameManager(main);
        game.accept(gameManager);
        gameManager.init();
    }

    private void init() {

        this.debug = main.getConfig().getBoolean("debug");
        this.setDay(Day.DAY);
        this.setState(StateGame.LOBBY);
        LanguageLoader.loadLanguage(this, this.getLanguage());
        StuffLoader.loadStuff(this, "saveCurrent");
        this.gameName = this.translate("werewolf.score_board.default_game_name");
        ConfigurationLoader.loadConfig(this, "saveCurrent");
        Bukkit.getPluginManager().callEvent(new LoadEvent(this));
        LobbyTask start = new LobbyTask(this);
        start.runTaskTimer(main, 0, 20);
    }

    public void join(Player player) {

        UUID uuid = player.getUniqueId();

        if (this.moderationManager.getWhiteListedPlayers().contains(uuid)) {
            finalJoin(player);
        } else if (this.getPlayersCount() >= this.configuration.getPlayerMax()) {
            player.sendMessage(translate(Prefix.RED, "werewolf.check.full"));
            this.moderationManager.addQueue(player);
        } else if (this.configuration.isWhiteList()) {
            player.sendMessage(translate(Prefix.RED, "werewolf.commands.admin.whitelist.player_not_whitelisted"));
            this.moderationManager.addQueue(player);
        } else {
            finalJoin(player);
        }
    }

    public void finalJoin(Player player) {

        UUID uuid = player.getUniqueId();

        if (this.getPlayerWW(uuid).isPresent()) return;

        this.moderationManager.getQueue().remove(uuid);
        this.playerSize++;
        Bukkit.broadcastMessage(translate("werewolf.announcement.join",
                Formatter.number(this.getPlayersCount()),
                Formatter.format("&sum&", this.getRoleInitialSize()),
                Formatter.player(player.getName())));
        player.setGameMode(GameMode.ADVENTURE);
        IPlayerWW playerWW = new PlayerWW(this, player);
        this.playersWW.put(uuid, playerWW);
        Bukkit.getPluginManager().callEvent(new FinalJoinEvent(playerWW));
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false));
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

        new UpdateChecker(main, 73113).getVersion(version -> {
            DefaultArtifactVersion siteVersion = new DefaultArtifactVersion(version);
            DefaultArtifactVersion loadVersion = new DefaultArtifactVersion(main.getDescription().getVersion());

            if (loadVersion.compareTo(siteVersion) == 0) {
                player.sendMessage(this.translate(Prefix.GREEN, "werewolf.update.up_to_date"));
            } else if (loadVersion.compareTo(siteVersion) < 0) {
                player.sendMessage(this.translate(Prefix.ORANGE, "werewolf.update.out_of_date"));
            } else {
                player.sendMessage(this.translate(Prefix.GREEN, "werewolf.update.snapshot"));
            }
        });

    }


    public void addLatePlayer(Player player) {

        Inventory inventory = player.getInventory();

        player.setGameMode(GameMode.SURVIVAL);
        IPlayerWW playerWW = new PlayerWW(this, player);
        this.playersWW.put(player.getUniqueId(), playerWW);
        Location spawn = this.mapManager.getWorld().getSpawnLocation();
        spawn.setY(spawn.getBlockY() - 4);
        playerWW.setSpawn(spawn);
        this.playerSize++;

        this.stuff.getStartLoot().forEach(inventory::addItem);
        Bukkit.getPluginManager().callEvent(new FinalJoinEvent(playerWW));
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));
        this.mapManager.transportation(playerWW, 0);
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
        end.checkVictory();
    }

    @Override
    public void stopGame() {

        if (!this.main.getWereWolfAPI().equals(this)) return;

        Bukkit.getPluginManager().callEvent(new StopEvent(this));

        this.listenersManager.delete();

        this.main.createGame();

        GameManager newGame = (GameManager) this.main.getWereWolfAPI();

        BukkitUtils.scheduleSyncDelayedTask(newGame, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                FastBoard fastboard = new FastBoard(player);
                fastboard.updateTitle(newGame.translate("werewolf.score_board.title"));
                newGame.boards.put(player.getUniqueId(), fastboard);
                player.setGameMode(GameMode.ADVENTURE);
                newGame.join(player);
            }
        }, 10);

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> player.getWorld().equals(this.mapManager.getWorld()))
                .forEach(player -> player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()));

        if (this.getTimer() > 60) { //Si la game a commencé depuis moins d'une minute on ne delete pas la map
            this.mapManager.deleteMap();
        }
        newGame.mapManager.createMap();
    }

    @Override
    public Collection<? extends IPlayerWW> getPlayersWW() {
        return this.playersWW.values();
    }

    @Override
    public Optional<IPlayerWW> getPlayerWW(UUID uuid) {

        if (!this.playersWW.containsKey(uuid)) {
            return Optional.empty();
        }

        return Optional.of(this.playersWW.get(uuid));
    }

    @Override
    public IConfiguration getConfig() {
        return this.configuration;
    }

    public void setConfig(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String translate(String key, Formatter... formatters) {
        return translate("", key, formatters);
    }

    @Override
    public String translate(String prefixKey, String key, Formatter... formatters) {
        String message = this.languageManager.getTranslation(key, formatters);
        String prefix = prefixKey.isEmpty() ? "" : this.languageManager.getTranslation(prefixKey);
        return prefix + message;
    }

    @Override
    public List<String> translateArray(String key, Formatter... formatters) {
        return this.languageManager.getTranslationList(key, formatters);
    }

    @Override
    public UUID getGameUUID() {
        return this.gameUUID;
    }

    @Override
    public Random getRandom() {
        return r;
    }

    @Override
    public IVoteManager getVoteManager() {
        return this.voteManager;
    }

    @Override
    public void setVoteManager(IVoteManager iVoteManager) {
        this.voteManager = iVoteManager;
    }

    @Override
    public void resurrection(IPlayerWW playerWW) {
        Bukkit.getPluginManager().callEvent(new ResurrectionEvent(playerWW));
    }

    @Override
    public void death(IPlayerWW playerWW) {
        Bukkit.getPluginManager().callEvent(new FinalDeathEvent(playerWW,
                new HashSet<>(playerWW.getLastMinutesDamagedPlayer())));
    }


    @Override
    public IStuffManager getStuffs() {
        return this.stuff;
    }

    @Override
    public ILoverManager getLoversManager() {
        return this.loversManage;
    }

    @Override
    public int getPlayersCount() {
        return this.playerSize;
    }

    @Override
    public int getGroup() {
        return this.groupSize;
    }

    @Override
    public void setGroup(int groupSize) {
        this.groupSize = groupSize;
    }

    @Override
    public int getTimer() {
        return this.timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    @Override
    public int getRoleInitialSize() {
        return this.roleInitialSize;
    }

    public void setRoleInitialSize(int roleInitialSize) {
        this.roleInitialSize = roleInitialSize;
    }

    @Override
    public IWerewolfChatHandler getWerewolfChatHandler() {
        return this.werewolfChatHandler;
    }

    public Map<UUID, FastBoard> getBoards() {
        return this.boards;
    }

    @Override
    public IMapManager getMapManager() {
        return this.mapManager;
    }

    @Override
    public IModerationManager getModerationManager() {
        return this.moderationManager;
    }

    @Override
    public StateGame getState() {
        return this.state;
    }

    public void setState(StateGame state) {
        this.state = state;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void remove(UUID uuid) {
        if (this.playersWW.remove(uuid) != null) {
            this.playerSize--;
        }
    }

    public void setPlayerSize(int playerSize) {
        this.playerSize = playerSize;
    }

    @Override
    public String getPluginVersion() {
        return main.getDescription().getVersion();
    }

    @Override
    public String getLanguage() {
        return main.getConfig().getString("lang");
    }

    @Override
    public void setLangage(String langage) {
        main.getConfig().set("lang", langage);
        LanguageLoader.loadLanguage(this, langage);
    }

    @Override
    public ILanguageManager getLanguageManager() {
        return this.languageManager;
    }

    @Override
    public IListenersManager getListenersManager() {
        return this.listenersManager;
    }

    public boolean isCrack() {
        return crack;
    }

    public void setCrack() {
        this.crack = true;
    }

    public ScoreBoard getScore() {
        return this.score;
    }

}
