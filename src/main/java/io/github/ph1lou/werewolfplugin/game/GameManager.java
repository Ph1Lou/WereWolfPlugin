package io.github.ph1lou.werewolfplugin.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.werewolfapi.*;
import io.github.ph1lou.werewolfapi.enumlg.Day;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.save.Config;
import io.github.ph1lou.werewolfplugin.save.Stuff;
import io.github.ph1lou.werewolfplugin.tasks.LobbyTask;
import io.github.ph1lou.werewolfplugin.utils.UpdateChecker;
import io.github.ph1lou.werewolfplugin.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class GameManager implements WereWolfAPI {

    private final Main main;
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private final Map<UUID, PlayerWW> playerLG = new HashMap<>();
    private StateLG state;
    private Day dayState;
    private final ScoreBoard score = new ScoreBoard(this);
    private final Vote vote = new Vote(this);
    private final Events events = new Events(this);
    private final LoversManagement loversManage = new LoversManagement(this);
    private final ModerationManager moderationManager = new ModerationManager(this);
    private final MapManager mapManager = new MapManager(this);
    private final Config config = new Config();
    private final End end = new End(this);
    private final Stuff stuff = new Stuff(this);
    private final ScenariosLoader scenarios = new ScenariosLoader(this);
    private final Random r = new Random(System.currentTimeMillis());
    private final Map<String, String> language = new HashMap<>();
    private final UUID uuid = UUID.randomUUID();
    private String gameName = "@Ph1Lou_";


    public GameManager(Main main) {
        this.main = main;
        setDay(Day.DAY);
    }

    public void init() {
        main.getLang().updateLanguage(this);
        config.getConfig(this, "saveCurrent");
        stuff.load("saveCurrent");
        setState(StateLG.LOBBY);
        scenarios.init();
        Bukkit.getPluginManager().callEvent(new LoadEvent(this));
        LobbyTask start = new LobbyTask(main, this);
        start.runTaskTimer(main, 0, 20);
    }

    public void join(Player player) {

        UUID uuid = player.getUniqueId();

        if (moderationManager.getWhiteListedPlayers().contains(uuid)) {
            finalJoin(player);
        } else if (getPlayersWW().size() >= config.getPlayerMax()) {
            player.sendMessage(translate("werewolf.check.full"));
            moderationManager.addQueue(player);
        } else if (config.isWhiteList()) {
            player.sendMessage(translate("werewolf.commands.admin.whitelist.player_not_whitelisted"));
            moderationManager.addQueue(player);
        } else {
            finalJoin(player);
        }
    }

    public void finalJoin(Player player) {

        UUID uuid = player.getUniqueId();

        moderationManager.getQueue().remove(uuid);
        score.addPlayerSize();
        Bukkit.broadcastMessage(translate("werewolf.announcement.join", score.getPlayerSize(), score.getRole(), player.getName()));
        clearPlayer(player);
        player.setGameMode(GameMode.ADVENTURE);
        PlayerWW plg = new PlayerLG(main, this, player);
        getPlayersWW().put(uuid, plg);
        Bukkit.getPluginManager().registerEvents((Listener) plg, main);
        player.setScoreboard(getPlayersWW().get(uuid).getScoreBoard());
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false));
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

        new UpdateChecker(main, 73113).getVersion(version -> {

            if (main.getDescription().getVersion().equalsIgnoreCase(version)) {
                player.sendMessage(translate("werewolf.update.up_to_date"));
            } else {
                player.sendMessage(translate("werewolf.update.out_of_date"));
            }
        });
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent());
    }

    public void clearPlayer(Player player) {
        VersionUtils.getVersionUtils().setPlayerMaxHealth(player, 20);
        player.setHealth(20);
        player.setExp(0);
        player.setLevel(0);
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        for (PotionEffect po : player.getActivePotionEffects()) {
            player.removePotionEffect(po.getType());
        }
    }

    public void setState(StateLG state) {
        this.state = state;
    }

    @Override
    public boolean isState(StateLG state) {
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
        this.dayState = day;
    }

    @Override
    public boolean isDay(Day day) {
        return this.dayState == day;
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

        setState(StateLG.END);

        if (mapManager.getWorld() == null) return;

        Bukkit.getPluginManager().callEvent(new StopEvent(this));
        scenarios.delete();

        main.setCurrentGame(new GameManager(main));
        main.getCurrentGame().init();

        for (Player player : Bukkit.getOnlinePlayers()) {
            FastBoard fastboard = new FastBoard(player);
            fastboard.updateTitle(main.getCurrentGame().translate("werewolf.score_board.title"));
            main.getCurrentGame().boards.put(player.getUniqueId(), fastboard);
            player.setGameMode(GameMode.ADVENTURE);
            main.getCurrentGame().join(player);
        }
        mapManager.deleteMap();
    }

    @Override
    public Map<UUID, PlayerWW> getPlayersWW() {
        return this.playerLG;
    }


    @Override
    public ConfigWereWolfAPI getConfig() {
        return this.config;
    }


    @Override
    public String translate(String key, Object... args) {
        final String translation;
        if(!main.getExtraTexts().containsKey(key.toLowerCase())){
            if(!this.language.containsKey(key.toLowerCase())){
                return String.format("Message error (%s) ", key.toLowerCase());
            }
            translation = this.language.get(key.toLowerCase());
        }
        else translation = main.getExtraTexts().get(key.toLowerCase());
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
            if (getPlayersWW().get(uuid).isState(State.ALIVE) && !uuid.equals(playerUUID)) {
                players.add(uuid);
            }
        }
        if (players.isEmpty()) {
            return playerUUID;
        }
        return players.get((int) Math.floor(getRandom().nextFloat() * players.size()));
    }

    public List<RoleRegister> getRolesRegister() {
        return main.getRegisterRoles();
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

    public Events getEvents() {
        return events;
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

    public ScenariosLoader getScenarios() {
        return scenarios;
    }

    public ModerationManager getModerationManager() {
        return moderationManager;
    }


}
