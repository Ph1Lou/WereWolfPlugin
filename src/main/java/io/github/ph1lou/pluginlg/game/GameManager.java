package io.github.ph1lou.pluginlg.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.Day;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.listener.ScenariosLG;
import io.github.ph1lou.pluginlg.savelg.ConfigLG;
import io.github.ph1lou.pluginlg.savelg.StuffLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import io.github.ph1lou.pluginlg.tasks.LobbyTask;
import io.github.ph1lou.pluginlg.utils.Title;
import io.github.ph1lou.pluginlg.utils.UpdateChecker;
import io.github.ph1lou.pluginlg.utils.WorldUtils;
import io.github.ph1lou.pluginlg.worldloader.WorldFillTask;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GameManager {

    final MainLG main;
    public final Scoreboard board;
    public final Map<UUID, FastBoard> boards = new HashMap<>();
    public final Map<String, PlayerLG> playerLG = new HashMap<>();
    private StateLG state;
    private Day dayState;
    public final CycleLG cycle;
    public final DeathManagementLG death_manage;
    public final VoteLG vote ;
    public final ScoreBoardLG score;
    public final EventsLG eventslg;
    public final OptionLG optionlg;
    public final ProximityLG proximity;
    public final RoleManagementLG roleManage;
    public final LoversManagement loversManage;
    public final ConfigLG config = new ConfigLG();
    public final EndLG endlg;
    public final StuffLG stufflg;
    public final ScenariosLG scenarios;
    public WorldFillTask wft = null;
    public TextLG text;
    private String lang;
    private World world;
    private final List<UUID> queue = new ArrayList<>();
    private List<UUID> whiteListedPlayers = new ArrayList<>();
    private List<UUID> hosts = new ArrayList<>();
    private List<UUID> moderators = new ArrayList<>();
    private int spectatorMode = 2;  // 0 no Spectators, 1 allowed for death players, 2 for all players;
    private boolean whiteList = true;
    private int playerMax = 30;
    private String gameName = "/a setGameName";


    public GameManager(MainLG main) {

        this.main = main;
        WorldCreator wc = new WorldCreator(String.valueOf(UUID.randomUUID()));
        wc.environment(World.Environment.NORMAL);
        wc.type(WorldType.NORMAL);
        this.world = wc.createWorld();
        lang = getConfig().getString("lang");
        cycle = new CycleLG(this);
        death_manage = new DeathManagementLG(this);
        vote = new VoteLG(this);
        score = new ScoreBoardLG(this);
        eventslg = new EventsLG(this);
        optionlg = new OptionLG(this);
        main.lang.changeLanguage(this);
        proximity = new ProximityLG(this);
        roleManage = new RoleManagementLG(this);
        loversManage = new LoversManagement(this);
        endlg = new EndLG(main, this);
        stufflg = new StuffLG(this);
        scenarios = new ScenariosLG(main,this);
        config.getConfig(this, "saveCurrent");
        stufflg.load("saveCurrent");
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        board.registerNewTeam("moderators");
        board.getTeam("moderators").setPrefix(text.getText(295));
        scenarios.init();
        setState(StateLG.LOBBY);
        setDay(Day.DEFAULT);
        setWorld();

        LobbyTask start = new LobbyTask(main,this);
        start.runTaskTimer(main, 0, 20);
    }

    public void setState(StateLG state) {
        this.state=state;
    }

    public StateLG getState() {return this.state;}

    public boolean isState(StateLG state) {
        return this.state==state;
    }


    public void setDay(Day day) {
        this.dayState =day;
    }


    public boolean isDay(Day day) {
        return this.dayState ==day;
    }



    public void setWorld() {

        try{
            World world = this.world;
            world.setWeatherDuration(0);
            world.setThundering(false);
            world.setTime(0);
            world.setGameRuleValue("reducedDebugInfo", "true");
            world.setGameRuleValue("keepInventory", "true");
            world.setGameRuleValue("naturalRegeneration", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.getWorldBorder().reset();
            world.save();
            int x = (int) world.getSpawnLocation().getX();
            int z = (int) world.getSpawnLocation().getZ();

            if (main.getConfig().getBoolean("autoRoofedMiddle")) {
                Location biome = WorldUtils.findBiome(this, Biome.ROOFED_FOREST, world, 2000);
                x = (int) biome.getX();
                z = (int) biome.getZ();
            }
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
        }catch(Exception e){
            Bukkit.getConsoleSender().sendMessage(text.getText(21));
        }
    }

    public FileConfiguration getConfig(){
        return main.getConfig();
    }

    public File getDataFolder(){
        return main.getDataFolder();
    }

    public InputStream getResource(String filename){
        return main.getResource(filename);
    }

    public void setLang(String lang){
        this.lang=lang;
    }

    public String getLang() {
        return lang;
    }

    public void setText(TextLG text) {
        this.text = text;
    }

    public void deleteGame() {

        if (world == null) return;

        scenarios.delete();
        main.currentGame = new GameManager(main);

        for (Player player : Bukkit.getOnlinePlayers()) {

            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            FastBoard fastboard = new FastBoard(player);
            fastboard.updateTitle(getText(125));
            main.currentGame.boards.put(player.getUniqueId(), fastboard);
            Title.sendTabTitle(player, main.currentGame.getText(125), main.currentGame.getText(184));
            player.setGameMode(GameMode.ADVENTURE);
            main.currentGame.join(player);
        }

        Bukkit.unloadWorld(world, false);
        try {
            FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer() + File.separator + world.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.world = null;
    }

    public World getWorld() {
        return this.world;
    }

    public boolean isWhiteList() {
        return whiteList;
    }

    public void setWhiteList(boolean whiteList) {
        this.whiteList = whiteList;
    }

    public int getPlayerMax() {
        return playerMax;
    }

    public void setPlayerMax(int playerMax) {
        this.playerMax = playerMax;
    }


    public void join(Player player) {

        String playerName = player.getName();
        UUID uuid = player.getUniqueId();

        if (playerLG.size() >= getPlayerMax()) {
            addQueue(player);
        } else {
            if (isWhiteList() && !getWhiteListedPlayers().contains(uuid)) {
                player.sendMessage(getText(278));
                addQueue(player);
            } else {
                queue.remove(uuid);
                score.addPlayerSize();
                Bukkit.broadcastMessage(String.format(text.getText(194), score.getPlayerSize(), score.getRole(), playerName));
                clearPlayer(player);
                board.registerNewTeam(playerName);
                board.getTeam(playerName).addEntry(playerName);
                player.setGameMode(GameMode.ADVENTURE);
                player.sendMessage(text.getText(1));
                playerLG.put(playerName, new PlayerLG(player));
                player.setScoreboard(playerLG.get(playerName).getScoreBoard());
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false));
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                new UpdateChecker(main, 73113).getVersion(version -> {

                    if (main.getDescription().getVersion().equalsIgnoreCase(version)) {
                        player.sendMessage(main.defaultLanguage.getText(2));
                    } else {
                        player.sendMessage(main.defaultLanguage.getText(185));
                    }
                });
            }
        }
    }

    public void checkQueue() {

        if (!isState(StateLG.LOBBY)) return;

        List<UUID> temp = new ArrayList<>(queue);
        int i = 0;
        while (!temp.isEmpty() && getPlayerMax() > playerLG.size()) {
            if (Bukkit.getPlayer(temp.get(0)) != null) {
                Player player = Bukkit.getPlayer(temp.get(0));
                queue.remove(i);
                join(player);
            } else i++;
            temp.remove(0);
        }
    }

    public void addQueue(Player player) {

        UUID uuid = player.getUniqueId();

        if (!getQueue().contains(uuid)) {
            queue.add(uuid);
            Bukkit.broadcastMessage(String.format(getText(279), player.getName()));
        }
    }


    public void clearPlayer(Player player) {
        player.setMaxHealth(20);
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

    public int getSpectatorMode() {
        return spectatorMode;
    }

    public void setSpectatorMode(int spectatorMode) {
        this.spectatorMode = spectatorMode;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public List<UUID> getWhiteListedPlayers() {
        return whiteListedPlayers;
    }

    public void addWhiteListedPlayer(UUID whiteListedPlayer) {
        this.whiteListedPlayers.add(whiteListedPlayer);
    }

    public void removeWhiteListedPlayer(UUID whiteListedPlayer) {
        this.whiteListedPlayers.remove(whiteListedPlayer);
    }

    public List<UUID> getHosts() {
        return hosts;
    }

    public void addHost(UUID host) {
        this.hosts.add(host);
    }

    public void removeHost(UUID host) {
        this.hosts.remove(host);
    }

    public List<UUID> getModerators() {
        return moderators;
    }

    public void addModerator(UUID moderator) {
        this.moderators.add(moderator);
    }

    public void removeModerator(UUID moderator) {
        this.moderators.remove(moderator);
    }

    public List<UUID> getQueue() {
        return this.queue;
    }

    public String getText(int i) {
        return text.getText(i);
    }

    public void setWhiteListedPlayer(List<UUID> whiteListedPlayers) {
        this.whiteListedPlayers = whiteListedPlayers;
    }

    public void setModerators(List<UUID> moderatorsUUIDs) {
        this.moderators = moderatorsUUIDs;
    }

    public void setHosts(List<UUID> hostsUUIDs) {
        this.hosts = hostsUUIDs;
    }
}
