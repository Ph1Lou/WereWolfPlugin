package io.github.ph1lou.pluginlg.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.Day;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.listener.gamelisteners.ScenariosLG;
import io.github.ph1lou.pluginlg.savelg.ConfigLG;
import io.github.ph1lou.pluginlg.savelg.StuffLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import io.github.ph1lou.pluginlg.tasks.LobbyTask;
import io.github.ph1lou.pluginlg.utils.Title;
import io.github.ph1lou.pluginlg.utils.WorldUtils;
import io.github.ph1lou.pluginlg.worldloader.WorldFillTask;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
    public WorldFillTask wft=null;
    public TextLG text;
    private String lang;
    private final World world;
    private final List<UUID> queue = new ArrayList<>();
    private final List<String> whiteListedPlayers;
    private final List<UUID> hosts;
    private final List<UUID> moderators;
    private int spectatorMode = 2;  // 0 no Spectators, 1 allowed for death players, 2 for all players;
    private boolean whiteList=true;
    private int playerMax;
    private final UUID gameUUID;
    private String gameName;


    public GameManager(MainLG main, String name, List<UUID> hostsUUIDs, List<UUID> moderatorsUUIDs, List<String> whiteListedPlayers, int playerMax){

        this.main=main;
        this.gameName= name;
        this.hosts=hostsUUIDs;
        this.moderators=moderatorsUUIDs;
        this.whiteListedPlayers=whiteListedPlayers;
        this.playerMax=playerMax;
        gameUUID= UUID.randomUUID();
        main.listGames.put(gameUUID,this);

        WorldCreator wc = new WorldCreator(gameUUID.toString());
        wc.environment(World.Environment.NORMAL);
        wc.type(WorldType.NORMAL);
        this.world=wc.createWorld();
        lang=getConfig().getString("lang");
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
        endlg = new EndLG(this);
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


    public GameManager(MainLG main, String name, UUID hostUUID){
        this(main,"Partie de "+name,new ArrayList<>(Collections.singleton(hostUUID)),new ArrayList<>(),new ArrayList<>(), 30);
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
            world.setPVP(false);
            world.setWeatherDuration(0);
            world.setThundering(false);
            world.setTime(0);
            world.setGameRuleValue("reducedDebugInfo", "true");
            world.setGameRuleValue("keepInventory", "true");
            world.setGameRuleValue("naturalRegeneration", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.getWorldBorder().reset();
            world.save();
            int x=(int) world.getSpawnLocation().getX();
            int z=(int)  world.getSpawnLocation().getZ();

            if(main.getConfig().getBoolean("autoRoofedMiddle")){
                Location biome = WorldUtils.findBiome(this,Biome.ROOFED_FOREST, world, 2000);
                x=(int) biome.getX();
                z=(int) biome.getZ();
            }
            world.setSpawnLocation(x, 151,z);

            for(int i=-16;i<=16;i++) {

                for(int j=-16;j<=16;j++) {

                    new Location(world, i+x, 150,j+z).getBlock().setType(Material.BARRIER);
                    new Location(world, i+x, 154,j+z).getBlock().setType(Material.BARRIER);
                }
                for(int j=151;j<154;j++){
                    new Location(world, i+x, j,z-16).getBlock().setType(Material.BARRIER);
                    new Location(world, i+x, j,z+16).getBlock().setType(Material.BARRIER);
                    new Location(world, x-16, j,i+z).getBlock().setType(Material.BARRIER);
                    new Location(world, x+16, j,i+z).getBlock().setType(Material.BARRIER);
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

        for(Player p:Bukkit.getOnlinePlayers()){
            if(p.getWorld().equals(this.getWorld())){
                p.performCommand("lg leave");
            }
        }
        main.listGames.remove(gameUUID);
        Bukkit.unloadWorld(world,false);
        try {
            FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer() + File.separator + world.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        FastBoard fastboard = main.boards.remove(player.getUniqueId());
        if (fastboard != null) {
            fastboard.delete();
        }
        fastboard = new FastBoard(player);
        fastboard.updateTitle(text.getText(125));
        boards.put(player.getUniqueId(), fastboard);
        Title.sendTabTitle(player, text.getText(125), text.getText(184));

        clearPlayer(player);

        if(moderators.contains(player.getUniqueId())){
            player.sendMessage(text.getText(294));
            player.setGameMode(GameMode.SPECTATOR);
            player.setScoreboard(board);
        }
        else if(isState(StateLG.LOBBY)) {

            String playerName = player.getName();
            score.addPlayerSize();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (playerLG.containsKey(p.getName())) {
                    p.sendMessage(String.format(text.getText(194), score.getPlayerSize(), score.getRole(), playerName));
                }
            }
            board.registerNewTeam(playerName);
            board.getTeam(playerName).addEntry(playerName);
            player.setGameMode(GameMode.ADVENTURE);
            player.sendMessage(text.getText(1));
            playerLG.put(playerName, new PlayerLG(player));
            player.setScoreboard(playerLG.get(playerName).getScoreBoard());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false));
        } else {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(text.getText(38));
        }
        if(isState(StateLG.FIN)){
            score.updateBoard();
        }
        optionlg.updateNameTag();
        optionlg.updateCompass();
        player.teleport(getWorld().getSpawnLocation());
    }

    public void checkQueue(){

        while(!queue.isEmpty() && getPlayerMax()>playerLG.size()){
            if (Bukkit.getPlayer(queue.get(0)) != null) {
                Player player = Bukkit.getPlayer(queue.get(0));
                if (player.getWorld().equals(Bukkit.getWorlds().get(0))) {
                    join(player);
                }
            }
            queue.remove(0);
        }
    }

    public void sendMessage(Player player) {

        TextComponent msg = new TextComponent(String.format(text.getText(293),world.getName()));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lg join " + world.getName()));
        player.spigot().sendMessage(msg);
    }

    public void clearPlayer(Player player){
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setExp(0);
        player.setLevel(0);
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        for(PotionEffect po:player.getActivePotionEffects()) {
            player.removePotionEffect(po.getType());
        }
    }

    public int getSpectatorMode() {
        return spectatorMode;
    }

    public void setSpectatorMode(int spectatorMode) {
        this.spectatorMode = spectatorMode;
    }

    public UUID getGameUUID() {
        return gameUUID;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public List<String> getWhiteListedPlayers() {
        return whiteListedPlayers;
    }

    public void addWhiteListedPlayer(String whiteListedPlayer) {
        this.whiteListedPlayers.add(whiteListedPlayer);
    }

    public void removeWhiteListedPlayer(String whiteListedPlayer) {
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

    public List<UUID> getQueue(){
        return this.queue;
    }

    public void addPlayerInQueue(UUID uuid){
        this.queue.add(uuid);
    }
}
