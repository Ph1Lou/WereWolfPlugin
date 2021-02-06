package io.github.ph1lou.werewolfplugin.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.werewolfapi.ConfigWereWolfAPI;
import io.github.ph1lou.werewolfapi.LoverManagerAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.ScoreAPI;
import io.github.ph1lou.werewolfapi.StuffManager;
import io.github.ph1lou.werewolfapi.VoteAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.FinalJoinEvent;
import io.github.ph1lou.werewolfapi.events.LoadEvent;
import io.github.ph1lou.werewolfapi.events.ResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.StopEvent;
import io.github.ph1lou.werewolfapi.events.UpdateLanguageEvent;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.save.Configuration;
import io.github.ph1lou.werewolfplugin.save.FileUtils_;
import io.github.ph1lou.werewolfplugin.save.Lang;
import io.github.ph1lou.werewolfplugin.save.Stuff;
import io.github.ph1lou.werewolfplugin.scoreboards.ScoreBoard;
import io.github.ph1lou.werewolfplugin.tasks.LobbyTask;
import io.github.ph1lou.werewolfplugin.utils.UpdateChecker;
import io.github.ph1lou.werewolfplugin.utils.random_config.RandomConfig;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
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
    private final Map<UUID, PlayerWW> playerLG = new HashMap<>();
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
    private final ScenariosLoader scenarios;
    private final Random r = new Random(System.currentTimeMillis());
    private final UUID gameUUID = UUID.randomUUID();
    private String gameName = "@Ph1Lou_";


    public GameManager(Main main) {
        this.main = main;
        this.randomConfig = new RandomConfig(main);
        this.configuration = new Configuration(main.getRegisterManager());
        mapManager = new MapManager(main);
        stuff = new Stuff(main);
        scenarios = new ScenariosLoader(main);
        File mapFolder = new File(main.getDataFolder() +
                File.separator + "maps");
        if (!mapFolder.exists()) {
            mapFolder.mkdirs();
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
        PlayerWW plg = new PlayerLG(main, player);
        playerLG.put(uuid, plg);
        Bukkit.getPluginManager().callEvent(new FinalJoinEvent(plg));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false));
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

        new UpdateChecker(main, 73113).getVersion(version -> {

            if (main.getDescription().getVersion().equalsIgnoreCase(version)) {
                player.sendMessage(translate("werewolf.update.up_to_date"));
            } else {
                player.sendMessage(translate("werewolf.update.out_of_date"));
            }
            Plugin plugin = Bukkit.getPluginManager().getPlugin("Statistiks");

            if (plugin == null) {
                TextComponent msg = new TextComponent(translate("werewolf.utils.stat"));
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        "https://www.spigotmc.org/resources/statistiks-for-loup-garou-uhc-werewolf-uhc.81472/"));
                player.spigot().sendMessage(msg);
            } else {
                new UpdateChecker(plugin, 81472).getVersion(version2 -> {

                    if (!plugin.getDescription().getVersion().equalsIgnoreCase(version2)) {
                        TextComponent msg = new TextComponent(translate("werewolf.utils.stat_out_of_date"));
                        msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                "https://www.spigotmc.org/resources/statistiks-for-loup-garou-uhc-werewolf-uhc.81472/"));
                        player.spigot().sendMessage(msg);
                    }
                });
            }

        });

    }


    public void addLatePlayer(Player player) {

        clearPlayer(player);

        Inventory inventory = player.getInventory();

        player.setGameMode(GameMode.SURVIVAL);
        PlayerWW playerWW = new PlayerLG(main, player);
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
    public Collection<? extends PlayerWW> getPlayerWW() {
        return this.playerLG.values();
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
        Lang lang = (Lang) main.getLangManager();
        String translation = lang.getTranslation(key);
        try {
            return String.format(translation, args);
        } catch (IllegalFormatException e) {
            Bukkit.getConsoleSender().sendMessage(String.format("Error while formatting translation (%s)", key.toLowerCase()));
            return translation + " (Format error)";
        }
    }

    @Override
    public List<String> translateArray(String key) {
        Lang lang = (Lang) main.getLangManager();
        return lang.getTranslationList(key);
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
    public PlayerWW autoSelect(PlayerWW playerWW) {

        List<PlayerWW> players = playerLG.values()
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
    public VoteAPI getVote(){
        return this.vote;
    }

    @Override
    public void resurrection(PlayerWW playerWW) {
        Bukkit.getPluginManager().callEvent(new ResurrectionEvent(playerWW));
    }

    @Override
    public void death(PlayerWW playerWW) {
        Bukkit.getPluginManager().callEvent(new FinalDeathEvent(playerWW));
    }


    @Override
    public StuffManager getStuffs() {
        return stuff;
    }

    @Override
    public LoverManagerAPI getLoversManager() {
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

    public void remove(UUID uuid) {
        playerLG.remove(uuid);
    }

    public RandomConfig getRandomConfig() {
        return randomConfig;
    }
}
