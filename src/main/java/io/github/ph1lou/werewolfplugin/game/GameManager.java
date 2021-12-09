package io.github.ph1lou.werewolfplugin.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.werewolfapi.Formatter;
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
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.UpdateLanguageEvent;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.LoadEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalJoinEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.commands.roles.werewolf.CommandWereWolfChat;
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
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final ListenersLoader listenersLoader;
    private final Random r = new Random(System.currentTimeMillis());
    private final UUID gameUUID = UUID.randomUUID();
    private String gameName = "@Ph1Lou_";
    private int groupSize = 5;
    private int playerSize = 0;
    private int timer = 0;

    private int roleInitialSize = 0;


    public GameManager(Main main) {
        this.main = main;
        this.randomConfig = new RandomConfig(main);
        this.configuration = new Configuration(main.getRegisterManager());
        this.mapManager = new MapManager(main);
        this.stuff = new Stuff(main);
        this.listenersLoader = new ListenersLoader(this);
        File mapFolder = new File(main.getDataFolder() +
                File.separator + "maps");
        if (!mapFolder.exists()) {
            if (!mapFolder.mkdirs()) {
                Bukkit.getLogger().warning("[WereWolfPlugin] Folder Map Creation Failed");
            }
        }
        setDay(Day.DAY);

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            Bukkit.getPluginManager().callEvent(new UpdateLanguageEvent());
            FileUtils_.loadConfig(main, "saveCurrent");
            main.getWereWolfAPI().getStuffs().load("saveCurrent");
            listenersLoader.init();
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
        } else if (this.getPlayerSize() >= configuration.getPlayerMax()) {
            player.sendMessage(translate(Prefix.RED.getKey() , "werewolf.check.full"));
            moderationManager.addQueue(player);
        } else if (configuration.isWhiteList()) {
            player.sendMessage(translate(Prefix.RED.getKey() , "werewolf.commands.admin.whitelist.player_not_whitelisted"));
            moderationManager.addQueue(player);
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
                Formatter.number(this.getPlayerSize()),
                Formatter.format("&sum&",this.getRoleInitialSize()),
                Formatter.player(player.getName())));
        clearPlayer(player);
        player.setGameMode(GameMode.ADVENTURE);
        IPlayerWW playerWW = new PlayerWW(this, player);
        this.playerLG.put(uuid, playerWW);
        Bukkit.getPluginManager().callEvent(new FinalJoinEvent(playerWW));
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false));
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

        new UpdateChecker(main, 73113).getVersion(version -> {
            DefaultArtifactVersion siteVersion = new DefaultArtifactVersion(version);
            DefaultArtifactVersion loadVersion = new DefaultArtifactVersion(main.getDescription().getVersion());

            if (loadVersion.compareTo(siteVersion) == 0) {
                player.sendMessage(this.translate(Prefix.GREEN.getKey() , "werewolf.update.up_to_date"));
            } else if (loadVersion.compareTo(siteVersion) < 0) {
                player.sendMessage(this.translate(Prefix.ORANGE.getKey() , "werewolf.update.out_of_date"));
            }
            else {
                player.sendMessage(this.translate(Prefix.GREEN.getKey() , "werewolf.update.snapshot"));
            }
        });

    }


    public void addLatePlayer(Player player) {

        clearPlayer(player);

        Inventory inventory = player.getInventory();

        player.setGameMode(GameMode.SURVIVAL);
        IPlayerWW playerWW = new PlayerWW(this, player);
        this.playerLG.put(player.getUniqueId(), playerWW);
        Location spawn = this.mapManager.getWorld().getSpawnLocation();
        spawn.setY(spawn.getBlockY() - 4);
        playerWW.setSpawn(spawn);
        this.playerSize++;

        for (int j = 0; j < 40; j++) {
            inventory.setItem(j, this.stuff.getStartLoot().getItem(j));
        }

        this.mapManager.transportation(playerWW, 0);
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

        if (!this.main.getWereWolfAPI().equals(this)) return;

        Bukkit.getPluginManager().callEvent(new StopEvent(this));

        this.listenersLoader.delete();

        this.main.createGame();

        GameManager newGame = (GameManager) this.main.getWereWolfAPI();

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                FastBoard fastboard = new FastBoard(player);
                fastboard.updateTitle(newGame.translate("werewolf.score_board.title"));
                newGame.boards.put(player.getUniqueId(), fastboard);
                player.setGameMode(GameMode.ADVENTURE);
                newGame.join(player);
            }

            if (this.getTimer() <= 60) {
                newGame.getMapManager().generateMap(newGame.getConfig().getBorderMax());
            }
        }, 10);

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> player.getWorld().equals(this.mapManager.getWorld()))
                .forEach(player -> player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()));

        if (this.getTimer() > 60) { //Si la game a commencé depuis moins d'une minute on ne delete pas la map
            this.mapManager.deleteMap();
        }
    }

    @Override
    public Collection<? extends IPlayerWW> getPlayersWW() {
        return this.playerLG.values();
    }


    @Override
    public Optional<IPlayerWW> getPlayerWW(UUID uuid) {

        if (!this.playerLG.containsKey(uuid)) {
            return Optional.empty();
        }

        return Optional.of(this.playerLG.get(uuid));
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
        return translate("",key,formatters);
    }


    @Override
    public String translate(String prefixKey, String key, Formatter... formatters) {
        LanguageManager languageManager = (LanguageManager) main.getLangManager();
        String message = languageManager.getTranslation(key);
        String prefix = prefixKey.isEmpty() ? "": languageManager.getTranslation(prefixKey);
        for(Formatter formatter:formatters){
            message = formatter.handle(message);
        }
        return prefix+message;
    }

    @Override
    public List<String> translateArray(String key, Formatter... formatters) {
        LanguageManager languageManager = (LanguageManager) main.getLangManager();
        return languageManager.getTranslationList(key, formatters);
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
        return this.stuff;
    }

    @Override
    public ILoverManager getLoversManager() {
        return this.loversManage;
    }

    @Override
    public int getPlayerSize() {
        return this.playerSize;
    }

    @Override
    public int getGroup() {
        return this.groupSize;
    }

    @Override
    public void setGroup(int groupSize) {
        this.groupSize=groupSize;
    }

    @Override
    public int getTimer() {
        return this.timer;
    }

    @Override
    public int getRoleInitialSize() {
        return this.roleInitialSize;
    }

    @Override
    public IScoreBoard getScore() {
        return this.score;
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


    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }


    public void remove(UUID uuid) {
        if(this.playerLG.remove(uuid) != null){
            this.playerSize--;
        }
    }

    public RandomConfig getRandomConfig() {
        return this.randomConfig;
    }

    public void setRoleInitialSize(int roleInitialSize) {
        this.roleInitialSize = roleInitialSize;
    }

    public void setPlayerSize(int playerSize) {
        this.playerSize = playerSize;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public ListenersLoader getListenersLoader() {
        return this.listenersLoader;
    }

    public void enableWereWolfChat(){
        CommandWereWolfChat.enable();
    }

    public void disableWereWolfChat(){
        CommandWereWolfChat.disable();
    }
}
