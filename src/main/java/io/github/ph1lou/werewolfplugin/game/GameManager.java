package io.github.ph1lou.werewolfplugin.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlgapi.*;
import io.github.ph1lou.pluginlgapi.enumlg.*;
import io.github.ph1lou.pluginlgapi.events.StopEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.InvisibleState;
import io.github.ph1lou.pluginlgapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.listener.ScenariosLG;
import io.github.ph1lou.werewolfplugin.savelg.ConfigLG;
import io.github.ph1lou.werewolfplugin.savelg.StuffLG;
import io.github.ph1lou.werewolfplugin.tasks.LobbyTask;
import io.github.ph1lou.werewolfplugin.utils.UpdateChecker;
import io.github.ph1lou.werewolfplugin.utils.WorldUtils;
import io.github.ph1lou.werewolfplugin.worldloader.WorldFillTask;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GameManager implements WereWolfAPI {

    private final Main main;
    public final Map<UUID, FastBoard> boards = new HashMap<>();
    private final Map<UUID, PlayerWW> playerLG = new HashMap<>();
    private StateLG state;
    private Day dayState;
    public final DeathManagementLG death_manage;
    private final VoteLG vote = new VoteLG(this);
    public final ScoreBoardLG score = new ScoreBoardLG(this);
    public final EventsLG eventslg = new EventsLG(this);
    public OptionLG optionlg;
    public final RoleManagementLG roleManage;
    public final LoversManagement loversManage = new LoversManagement(this);
    private final ConfigLG config = new ConfigLG();
    private final EndLG endlg;
    private final StuffLG stufflg;
    private final ScenariosLG scenarios;
    private final Random r = new Random(System.currentTimeMillis());
    public WorldFillTask wft = null;
    private final Map<String, String> language = new HashMap<>();
    private World world;
    private final List<UUID> queue = new ArrayList<>();
    private final List<UUID> whiteListedPlayers = new ArrayList<>();
    private final List<UUID> hosts = new ArrayList<>();
    private final List<UUID> moderators = new ArrayList<>();
    private int spectatorMode = 2;  // 0 no Spectators, 1 allowed for death players, 2 for all players;
    private boolean whiteList = false;
    private int playerMax = 30;
    private String gameName = "/a name";
    private final UUID uuid = UUID.randomUUID();


    public GameManager(Main main) {

        this.main = main;

        endlg = new EndLG(main, this);
        death_manage = new DeathManagementLG(main, this);
        main.lang.updateLanguage(this);
        roleManage = new RoleManagementLG(main, this);
        stufflg = new StuffLG(main, this);
        scenarios = new ScenariosLG(main, this);
        config.getConfig(this, "saveCurrent");
        stufflg.load("saveCurrent");
        Bukkit.getPluginManager().registerEvents(vote,main);
        scenarios.init();
        setState(StateLG.LOBBY);
        setDay(Day.DAY);
        LobbyTask start = new LobbyTask(main, this);
        start.runTaskTimer(main, 0, 20);
    }


    public void setState(StateLG state) {
        this.state = state;
    }


    @Override
    public boolean isState(StateLG state) {
        return this.state == state;
    }

    public void setDay(Day day) {
        this.dayState = day;
    }

    @Override
    public boolean isDay(Day day) {
        return this.dayState == day;
    }


    public void setWorld() {

        try {
            world.setWeatherDuration(0);
            world.setThundering(false);
            world.setTime(0);
            world.setPVP(false);
            world.setGameRuleValue("reducedDebugInfo", "true");
            world.setGameRuleValue("keepInventory", "true");
            world.setGameRuleValue("naturalRegeneration", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.save();
            int x = world.getSpawnLocation().getBlockX();
            int z = world.getSpawnLocation().getBlockZ();
            if(world.getWorldBorder()!=null){
                world.getWorldBorder().reset();
            }
            if (main.getConfig().getBoolean("autoRoofedMiddle")) {
                Location biome = WorldUtils.findBiome(Biome.ROOFED_FOREST, world, 2000);
                x = biome.getBlockX();
                z = biome.getBlockZ();
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
        } catch (Exception ignored) {
        }
    }


    public World getWorld() {
        return this.world;
    }

    @Override
    public boolean isWhiteList() {
        return whiteList;
    }

    @Override
    public Map<String,String> getLanguage(){
        return this.language;
    }

    @Override
    public void setWhiteList(boolean whiteList) {
        this.whiteList = whiteList;
    }

    @Override
    public int getPlayerMax() {
        return playerMax;
    }

    @Override
    public void setPlayerMax(int playerMax) {
        this.playerMax = playerMax;
    }


    public void join(Player player) {

        String playerName = player.getName();
        UUID uuid = player.getUniqueId();

        if (getPlayersWW().size() >= getPlayerMax()) {
            player.sendMessage(translate("werewolf.check.full"));
            addQueue(player);
        } else {
            if (isWhiteList() && !getWhiteListedPlayers().contains(uuid)) {
                player.sendMessage(translate("werewolf.commands.admin.whitelist.player_not_whitelisted"));
                addQueue(player);
            } else {
                queue.remove(uuid);
                score.addPlayerSize();
                Bukkit.broadcastMessage(translate("werewolf.announcement.join", score.getPlayerSize(), score.getRole(), playerName));
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
                updateNameTag();
            }
        }
    }

    public void checkQueue() {

        if (!isState(StateLG.LOBBY)) return;

        List<UUID> temp = new ArrayList<>(queue);
        int i = 0;
        while (!temp.isEmpty() && getPlayerMax() > getPlayersWW().size()) {
            if (Bukkit.getPlayer(temp.get(0)) != null && (!isWhiteList() || getWhiteListedPlayers().contains(temp.get(0)))) {
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
            Bukkit.broadcastMessage(translate("werewolf.announcement.queue", player.getName()));
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

    @Override
    public int getSpectatorMode() {
        return spectatorMode;
    }

    @Override
    public void setSpectatorMode(int spectatorMode) {
        this.spectatorMode = spectatorMode;
    }

    @Override
    public String getGameName() {
        return gameName;
    }

    @Override
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    @Override
    public List<UUID> getWhiteListedPlayers() {
        return whiteListedPlayers;
    }

    @Override
    public void addPlayerOnWhiteList(UUID whiteListedPlayer) {
        this.whiteListedPlayers.add(whiteListedPlayer);
    }

    @Override
    public void removePlayerOnWhiteList(UUID whiteListedPlayer) {
        this.whiteListedPlayers.remove(whiteListedPlayer);
    }

    @Override
    public List<UUID> getHosts() {
        return hosts;
    }

    @Override
    public void addHost(UUID host) {
        this.hosts.add(host);
    }

    @Override
    public void removeHost(UUID host) {
        this.hosts.remove(host);
    }

    @Override
    public List<UUID> getModerators() {
        return moderators;
    }

    @Override
    public void addModerator(UUID moderator) {
        this.moderators.add(moderator);
    }

    @Override
    public void removeModerator(UUID moderator) {
        this.moderators.remove(moderator);
    }

    public void generateMap(CommandSender sender, int mapRadius) {

        if (getWorld() == null) {
            createMap();
        }
        int chunksPerRun = 20;
        if (wft == null || wft.getPercentageCompleted() == 100) {
            wft = new WorldFillTask(this, chunksPerRun, mapRadius);
            wft.setTaskID(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main, wft, 1, 1));
            sender.sendMessage(translate("werewolf.commands.admin.generation.perform"));
        } else sender.sendMessage(translate("werewolf.commands.admin.generation.already_start"));
    }

    public void createMap() {
        WorldCreator wc = new WorldCreator("werewolf");
        wc.environment(World.Environment.NORMAL);
        wc.type(WorldType.NORMAL);
        this.world = wc.createWorld();
        setWorld();
    }

    @Override
    public void checkVictory() {
        endlg.check_victory();
    }

    @Override
    public void resurrection(UUID uuid) {
        death_manage.resurrection(uuid);
    }

    @Override
    public void transportation(UUID playerUUID, double d, String message) {

        if (Bukkit.getPlayer(playerUUID) != null) {

            Player player = Bukkit.getPlayer(playerUUID);
            World world = player.getWorld();
            WorldBorder wb = world.getWorldBorder();
            double a = d * 2 * Math.PI / Bukkit.getOnlinePlayers().size();
            int x = (int) (Math.round(wb.getSize() / 3 * Math.cos(a) + world.getSpawnLocation().getX()));
            int z = (int) (Math.round(wb.getSize() / 3 * Math.sin(a) + world.getSpawnLocation().getZ()));
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(message);
            player.removePotionEffect(PotionEffectType.WITHER);
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, -1, false, false));
            player.teleport(new Location(world, x, world.getHighestBlockYAt(x, z) + 100, z));
        }
    }

    @Override
    public void generateMap(int mapRadius) {
        generateMap(Bukkit.getConsoleSender(), mapRadius);
    }

    @Override
    public void stopGame() {

        setState(StateLG.END);

        if (world == null) return;

        Bukkit.getPluginManager().callEvent(new StopEvent(this));
        scenarios.delete();
        main.currentGame = new GameManager(main);

        for (Player player : Bukkit.getOnlinePlayers()) {
            FastBoard fastboard = new FastBoard(player);
            fastboard.updateTitle(main.currentGame.translate("werewolf.score_board.title"));
            main.currentGame.boards.put(player.getUniqueId(), fastboard);
            player.setGameMode(GameMode.ADVENTURE);
            main.currentGame.join(player);
        }
        deleteMap();


    }

    @Override
    public Map<UUID, PlayerWW> getPlayersWW() {
        return this.playerLG;
    }

    @Override
    public void deleteMap() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
        try {
            Bukkit.unloadWorld(world, false);
            FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer() + File.separator + world.getName()));
            this.world = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String conversion(int timer){
        return score.conversion(timer);
    }

    @Override
    public String updateArrow(Player player, Location target){
        return score.updateArrow(player,target);
    }

    @Override
    public ConfigWereWolfAPI getConfig() {
        return this.config;
    }

    @Override
    public List<UUID> getQueue() {
        return this.queue;
    }

    @Override
    public String translate(String key, Object... args) {
        final String translation;
        if(!main.getExtraTexts().containsKey(key.toLowerCase())){
            if(!this.language.containsKey(key.toLowerCase())){
                return "(Message error)";
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
    public void setModerators(List<UUID> moderatorsUUIDs) {
        this.moderators.clear();
        this.moderators.addAll(moderatorsUUIDs);
    }

    @Override
    public void setWhiteListedPlayers(List<UUID> whiteListedPlayers) {
        this.whiteListedPlayers.clear();
        this.whiteListedPlayers.addAll(whiteListedPlayers);
    }

    @Override
    public void setHosts(List<UUID> hostsUUIDs) {
        this.hosts.clear();
        this.hosts.addAll(hostsUUIDs);
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
        return roleManage.autoSelect(playerUUID);
    }

    public List<RoleRegister> getRolesRegister() {
        return main.rolesRegister;
    }

    @Override
    public Vote getVote(){
        return this.vote;
    }

    @Override
    public void death(UUID uuid) {

        if (Bukkit.getPlayer(uuid) != null) {

            PlayerWW playerWW = getPlayersWW().get(uuid);

            if (playerWW.isState(State.ALIVE)) {
                Player player = Bukkit.getPlayer(uuid);
                playerWW.setSpawn(player.getLocation());
                playerWW.clearItemDeath();

                Inventory inv = Bukkit.createInventory(null, 45);

                for (int j = 0; j < 40; j++) {
                    inv.setItem(j, player.getInventory().getItem(j));
                }
                playerWW.setItemDeath(inv.getContents());
            }
        }

        death_manage.death(uuid);
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
    public void updateNameTag() {

        for (PlayerWW playerWW : getPlayersWW().values()) {

            Scoreboard scoreBoard = playerWW.getScoreBoard();

            for (PlayerWW playerWW2: getPlayersWW().values()) {

                String name =playerWW2.getName();

                if (scoreBoard.getTeam(name) == null) {
                    scoreBoard.registerNewTeam(name);
                    scoreBoard.getTeam(name).addEntry(name);
                }

                Team team = scoreBoard.getTeam(name);

                if (config.getScenarioValues().get(ScenarioLG.NO_NAME_TAG)) {
                    team.setNameTagVisibility(NameTagVisibility.NEVER);
                } else {
                    if ((playerWW2.getRole() instanceof InvisibleState) && ((InvisibleState)playerWW2.getRole()).isInvisible()) {
                        team.setNameTagVisibility(NameTagVisibility.NEVER);
                    } else team.setNameTagVisibility(NameTagVisibility.ALWAYS);
                }
            }

            for (UUID uuid: this.getModerators()) {
                if(Bukkit.getPlayer(uuid)!=null){
                    String name3 = Bukkit.getPlayer(uuid).getName();
                    if(scoreBoard.getTeam(name3)==null){
                        scoreBoard.registerNewTeam(name3);
                        scoreBoard.getTeam(name3).addEntry(name3);
                    }
                }
            }

            for (UUID uuid: this.getHosts()) {
                if(Bukkit.getPlayer(uuid)!=null){
                    String name3 = Bukkit.getPlayer(uuid).getName();
                    if(scoreBoard.getTeam(name3)==null){
                        scoreBoard.registerNewTeam(name3);
                        scoreBoard.getTeam(name3).addEntry(name3);
                    }
                }
            }

            for(Team t:scoreBoard.getTeams()){

                for(String e:t.getEntries()){
                    if(Bukkit.getPlayer(e)!=null){
                        UUID uuid=Bukkit.getPlayer(e).getUniqueId();
                        StringBuilder sb = new StringBuilder();
                        if(this.getHosts().contains(uuid)){
                            sb.append(this.translate("werewolf.commands.admin.host.tag"));
                        }
                        else if (this.getModerators().contains(uuid)){
                            sb.append(this.translate("werewolf.commands.admin.moderator.tag"));
                        }
                        if(getPlayersWW().containsKey(uuid)){
                            Roles roles = getPlayersWW().get(uuid).getRole();
                            if(roles.isWereWolf() && playerWW.getRole().isWereWolf()){
                                if (getConfig().getConfigValues().get(ToolLG.RED_NAME_TAG)) {
                                    if(getConfig().getTimerValues().get(TimerLG.WEREWOLF_LIST) <= 0){
                                        sb.append(ChatColor.DARK_RED);
                                    }
                                }
                            }
                        }
                        t.setPrefix(sb.toString());
                    }
                }
            }
        }
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Team hosts = scoreboard.registerNewTeam("hosts");
        hosts.setPrefix(this.translate("werewolf.commands.admin.host.tag"));
        Team moderators = scoreboard.registerNewTeam("moderators");
        moderators.setPrefix(this.translate("werewolf.commands.admin.moderator.tag"));
        for(Player player:Bukkit.getOnlinePlayers()){
            if(getHosts().contains(player.getUniqueId())){
                hosts.addEntry(player.getName());
            }
            else if(getModerators().contains(player.getUniqueId())){
                moderators.addEntry(player.getName());
            }
        }
        for(Player player:Bukkit.getOnlinePlayers()){
            if(!getPlayersWW().containsKey(player.getUniqueId()) && player.getScoreboard()==null){
                player.setScoreboard(scoreboard);
            }
        }
    }


    @Override
    public int getRoleSize(){
        return score.getRole();
    }

    @Override
    public void setRoleSize(int roleSize){
        score.setRole(roleSize);
    }

    public void updateScenarios() {
        scenarios.update();
    }

    @Override
    public StuffManager getStuffs() {
        return stufflg;
    }

    public void updateCompass(){

        for(Player player:Bukkit.getOnlinePlayers()) {
            if(getPlayersWW().containsKey(player.getUniqueId())){
                if (getConfig().getConfigValues().get(ToolLG.COMPASS_MIDDLE)) {
                    player.setCompassTarget(player.getWorld().getSpawnLocation());
                } else {
                    player.setCompassTarget(getPlayersWW().get(player.getUniqueId()).getSpawn());
                }
            }
        }
    }
}
