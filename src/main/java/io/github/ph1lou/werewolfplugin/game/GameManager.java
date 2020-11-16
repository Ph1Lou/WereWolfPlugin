package io.github.ph1lou.werewolfplugin.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.werewolfapi.*;
import io.github.ph1lou.werewolfapi.enumlg.Day;
import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.save.Configuration;
import io.github.ph1lou.werewolfplugin.save.FileUtils_;
import io.github.ph1lou.werewolfplugin.save.Stuff;
import io.github.ph1lou.werewolfplugin.scoreboards.ScoreBoard;
import io.github.ph1lou.werewolfplugin.tasks.LobbyTask;
import io.github.ph1lou.werewolfplugin.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class GameManager implements WereWolfAPI {

    private final Main main;
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private final Map<UUID, PlayerWW> playerLG = new HashMap<>();
    private StateGame state;
    private Day day;
    private boolean debug = false;
    private final ScoreBoard score = new ScoreBoard(this);
    private final Vote vote = new Vote(this);
    private final LoversManagement loversManage = new LoversManagement(this);
    private final ModerationManager moderationManager = new ModerationManager(this);
    private final MapManager mapManager;
    private Configuration configuration = new Configuration();
    private final End end = new End(this);
    private final Stuff stuff;
    private final ScenariosLoader scenarios;
    private final Random r = new Random(System.currentTimeMillis());
    private final Map<String, String> language = new HashMap<>();
    private final UUID uuid = UUID.randomUUID();
    private String gameName = "@Ph1Lou_";


    public GameManager(Main main) {
        this.main = main;
        mapManager = new MapManager(main);
        stuff = new Stuff(main);
        scenarios = new ScenariosLoader(main);
        File mapFolder = new File(main.getDataFolder() +
                File.separator + "maps");
        if (!mapFolder.exists()) {
            mapFolder.mkdirs();
        }

        setDay(Day.DAY);
    }

    public void init() {
        Bukkit.getPluginManager().callEvent(new UpdateLanguageEvent());
        FileUtils_.loadConfig(main, "saveCurrent");
        stuff.load("saveCurrent");
        setState(StateGame.LOBBY);
        scenarios.init();
        Bukkit.getPluginManager().callEvent(new LoadEvent(this));
        LobbyTask start = new LobbyTask(this);
        start.runTaskTimer(main, 0, 20);
    }

    public void join(Player player) {

        UUID uuid = player.getUniqueId();

        if (moderationManager.getWhiteListedPlayers().contains(uuid)) {
            finalJoin(player);
        } else if (getPlayersWW().size() >= configuration.getPlayerMax()) {
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
        Bukkit.getPluginManager().callEvent(new FinalJoinEvent(uuid));
        moderationManager.getQueue().remove(uuid);
        score.addPlayerSize();
        Bukkit.broadcastMessage(translate("werewolf.announcement.join", score.getPlayerSize(), score.getRole(), player.getName()));
        clearPlayer(player);
        player.setGameMode(GameMode.ADVENTURE);
        PlayerWW plg = new PlayerLG(main, this, player);
        getPlayersWW().put(uuid, plg);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false));
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

        new UpdateChecker(main, 73113).getVersion(version -> {

            if (main.getDescription().getVersion().equalsIgnoreCase(version)) {
                player.sendMessage(translate("werewolf.update.up_to_date"));
            } else {
                player.sendMessage(translate("werewolf.update.out_of_date"));
            }
        });

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
    public Map<String, String> getLanguage() {
        return this.language;
    }

    @Override
    public void checkVictory() {
        end.check_victory();
    }


    @Override
    public void stopGame() {

        if (!main.getWereWolfAPI().equals(this)) return;

        scenarios.delete();

        main.createGame();

        GameManager newGame = (GameManager) main.getWereWolfAPI();

        for (Player player : Bukkit.getOnlinePlayers()) {
            FastBoard fastboard = new FastBoard(player);
            fastboard.updateTitle(newGame.translate("werewolf.score_board.title"));
            newGame.boards.put(player.getUniqueId(), fastboard);
            player.setGameMode(GameMode.ADVENTURE);
            newGame.join(player);
        }

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> player.getWorld().equals(mapManager.getWorld()))
                .forEach(player -> player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()));

        if (score.getTimer() > 60) { //Si la game a commencé depuis moins d'une minute on ne delete pas la map
            mapManager.deleteMap();
        } else {
            newGame.getMapManager().generateMap(newGame.getConfig().getBorderMax());
        }


        Bukkit.getPluginManager().callEvent(new StopEvent(this));
    }

    @Override
    public Map<UUID, PlayerWW> getPlayersWW() {
        return this.playerLG;
    }


    @Override
    @Nullable
    public PlayerWW getPlayerWW(UUID uuid) {

        if (!this.playerLG.containsKey(uuid)) {
            return null;
        }

        return this.playerLG.get(uuid);
    }


    @Override
    public ConfigWereWolfAPI getConfig() {
        return this.configuration;
    }

    public void setConfig(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public String translate(String key, Object... args) {
        final String translation;
        if (!main.getLangManager().getExtraTexts().containsKey(key.toLowerCase())) {
            if (!this.language.containsKey(key.toLowerCase())) {
                return String.format("Message error (%s) ", key.toLowerCase());
            }
            translation = this.language.get(key.toLowerCase());
        }
        else translation = main.getLangManager().getExtraTexts().get(key.toLowerCase());
        try {
            return String.format(translation, args);
        } catch (IllegalFormatException e) {
            Bukkit.getConsoleSender().sendMessage(String.format("Error while formatting translation (%s)", key.toLowerCase()));
            return translation + " (Format error)";
        }
    }


    @Override
    public UUID getGameUUID() {
        return uuid;
    }

    @Override
    public Random getRandom() {
        return r;
    }

    @Override
    public UUID autoSelect(UUID playerUUID) {

        List<UUID> players = new ArrayList<>();
        for (UUID uuid : getPlayersWW().keySet()) {
            if (getPlayersWW().get(uuid).isState(StatePlayer.ALIVE) && !uuid.equals(playerUUID)) {
                players.add(uuid);
            }
        }
        if (players.isEmpty()) {
            return playerUUID;
        }
        return players.get((int) Math.floor(getRandom().nextFloat() * players.size()));
    }

    @Override
    public VoteAPI getVote(){
        return this.vote;
    }

    @Override
    public void resurrection(UUID uuid) {
        if (getPlayersWW().containsKey(uuid)) {
            Bukkit.getPluginManager().callEvent(new ResurrectionEvent(uuid));
        }
    }

    @Override
    public void death(UUID uuid) {
        if (getPlayersWW().containsKey(uuid)) {
            Bukkit.getPluginManager().callEvent(new FinalDeathEvent(uuid));
        }
    }
    @Override
    public List<List<UUID>> getLoversRange(){
        return loversManage.getLoversRange();
    }
    @Override
    public List<List<UUID>> getAmnesiacLoversRange(){
        return loversManage.getAmnesiacLoversRange();
    }
    @Override
    public List<List<UUID>> getCursedLoversRange(){
        return loversManage.getCursedLoversRange();
    }


    @Override
    public StuffManager getStuffs() {
        return stuff;
    }


    public LoversManagement getLoversManage() {
        return loversManage;
    }

    @Override
    public ScoreAPI getScore() {
        return score;
    }

    public Map<UUID, FastBoard> getBoards() {
        return boards;
    }

    public Main getMain() {
        return main;
    }

    @Override
    public MapManager getMapManager() {
        return mapManager;
    }

    @Override
    public ModerationManager getModerationManager() {
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
}
