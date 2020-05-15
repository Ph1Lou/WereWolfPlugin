package io.github.ph1lou.pluginlg.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.RolesImpl;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.*;
import io.github.ph1lou.pluginlg.classesroles.villageroles.*;
import io.github.ph1lou.pluginlg.classesroles.werewolfroles.*;
import io.github.ph1lou.pluginlg.listener.ScenariosLG;
import io.github.ph1lou.pluginlg.savelg.ConfigLG;
import io.github.ph1lou.pluginlg.savelg.StuffLG;
import io.github.ph1lou.pluginlg.tasks.LobbyTask;
import io.github.ph1lou.pluginlg.utils.UpdateChecker;
import io.github.ph1lou.pluginlg.utils.WorldUtils;
import io.github.ph1lou.pluginlg.worldloader.WorldFillTask;
import io.github.ph1lou.pluginlgapi.ConfigWereWolfAPI;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.enumlg.Day;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.*;

public class GameManager implements WereWolfAPI {

    final MainLG main;
    public final Scoreboard board;
    public final Map<UUID, FastBoard> boards = new HashMap<>();
    public final Map<UUID, PlayerLG> playerLG = new HashMap<>();
    private StateLG state;
    private Day dayState;
    public final DeathManagementLG death_manage;
    public final VoteLG vote ;
    public final ScoreBoardLG score;
    public final EventsLG eventslg;
    public OptionLG optionlg;
    public final RoleManagementLG roleManage;
    public final LoversManagement loversManage;
    public final ConfigLG config = new ConfigLG();
    public final EndLG endlg;
    public final StuffLG stufflg;
    public final ScenariosLG scenarios;
    private final Random r = new Random(System.currentTimeMillis());
    public WorldFillTask wft = null;
    public Map<String,String> language;
    private World world;
    private final List<UUID> queue = new ArrayList<>();
    private List<UUID> whiteListedPlayers = new ArrayList<>();
    private List<UUID> hosts = new ArrayList<>();
    private List<UUID> moderators = new ArrayList<>();
    private int spectatorMode = 2;  // 0 no Spectators, 1 allowed for death players, 2 for all players;
    private boolean whiteList = false;
    private int playerMax = 30;
    private String gameName = "/a name";
    private final UUID uuid =UUID.randomUUID();
    public final Map<RoleLG, Constructor<? extends RolesImpl>> rolesRegister = new HashMap<>();

    public GameManager(MainLG main) {

        this.main = main;
        death_manage = new DeathManagementLG(main,this);
        vote = new VoteLG(this);
        score = new ScoreBoardLG(this);
        eventslg = new EventsLG(this);
        main.lang.updateLanguage(this);
        roleManage = new RoleManagementLG(main,this);
        loversManage = new LoversManagement(this);
        endlg = new EndLG(main, this);
        stufflg = new StuffLG(this);
        scenarios = new ScenariosLG(main,this);
        config.getConfig(this, "saveCurrent");
        stufflg.load("saveCurrent");
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        scenarios.init();
        setState(StateLG.LOBBY);
        setDay(Day.DAY);
        registerRole();
        LobbyTask start = new LobbyTask(main,this);
        start.runTaskTimer(main, 0, 20);
    }

    private void registerRole() {
        try {
            rolesRegister.put(RoleLG.WILD_CHILD, WildChild.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.WEREWOLF, WereWolf.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.SUCCUBUS, Succubus.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.ELDER, Elder.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.ANGEL, Angel.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.FALLEN_ANGEL, FallenAngel.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.GUARDIAN_ANGEL, GuardianAngel.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.ASSASSIN, Assassin.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.CITIZEN, Citizen.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.COMEDIAN, Comedian.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.RAVEN, Raven.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.CUPID, Cupid.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.DETECTIVE, Detective.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.SIAMESE_TWIN, SiameseTwin.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.INFECT, InfectFatherOfTheWolves.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.AMNESIAC_WEREWOLF, AmnesicWerewolf.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.FALSIFIER_WEREWOLF, FalsifierWereWolf.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.WHITE_WEREWOLF, WhiteWereWolf.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.MISCHIEVOUS_WEREWOLF, MischievousWereWolf.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.MINER, Miner.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.BEAR_TRAINER, BearTrainer.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.LITTLE_GIRL, LittleGirl.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.FOX, Fox.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.PROTECTOR, Protector.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.SISTER, Sister.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.WITCH, Witch.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.TRAPPER, Trapper.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.TROUBLEMAKER, Troublemaker.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.SERIAL_KILLER, SerialKiller.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.NAUGHTY_LITTLE_WOLF, NaughtyLittleWolf.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.THIEF, Thief.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.SEER, Seer.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.CHATTY_SEER, ChattySeer.class.getConstructor(GameManager.class,UUID.class));
            rolesRegister.put(RoleLG.VILLAGER, Villager.class.getConstructor(GameManager.class,UUID.class));

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public void setState(StateLG state) {
        this.state=state;
    }

    @Override
    public boolean isState(StateLG state) {
        return this.state==state;
    }

    public void setDay(Day day) {
        this.dayState =day;
    }

    @Override
    public boolean isDay(Day day) {
        return this.dayState ==day;
    }


    public void setWorld(World world) {

        try{
            this.world=world;
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
        }catch(Exception ignored){
        }
    }


    public File getDataFolder(){
        return main.getDataFolder();
    }

    public InputStream getResource(String filename){
        return main.getResource(filename);
    }


    public World getWorld() {
        return this.world;
    }

    @Override
    public boolean isWhiteList() {
        return whiteList;
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

        if (playerLG.size() >= getPlayerMax()) {
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
                PlayerLG plg =new PlayerLG(player,this);
                playerLG.put(uuid,plg );
                Bukkit.getPluginManager().registerEvents(plg,main);
                player.setScoreboard(playerLG.get(uuid).getScoreBoard());
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false));
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                new UpdateChecker(main, 73113).getVersion(version -> {

                    if (main.getDescription().getVersion().equalsIgnoreCase(version)) {
                        player.sendMessage(translate("werewolf.update.up_to_date"));
                    } else {
                        player.sendMessage(translate("werewolf.update.out_of_date"));
                    }
                });
                optionlg.updateNameTag();
            }
        }
    }

    public void checkQueue() {

        if (!isState(StateLG.LOBBY)) return;

        List<UUID> temp = new ArrayList<>(queue);
        int i = 0;
        while (!temp.isEmpty() && getPlayerMax() > playerLG.size()) {
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
            Bukkit.broadcastMessage(translate("werewolf.announcement.queue" ,player.getName()));
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

        if(getWorld()==null){
            WorldCreator wc = new WorldCreator("werewolf");
            wc.environment(World.Environment.NORMAL);
            wc.type(WorldType.NORMAL);
            setWorld(wc.createWorld());
        }
        int chunksPerRun = 20;
        if (wft == null || wft.getPercentageCompleted()==100) {
            wft = new WorldFillTask(this, chunksPerRun, mapRadius);
            wft.setTaskID(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main, wft, 1, 1));
            sender.sendMessage(translate("werewolf.commands.admin.generation.perform"));
        } else sender.sendMessage(translate("werewolf.commands.admin.generation.already_start"));
    }

    @Override
    public void generateMap(int mapRadius) {
        generateMap(Bukkit.getConsoleSender(),mapRadius);
    }

    @Override
    public void stopGame() {

        if (world == null) return;

        scenarios.delete();
        main.currentGame = new GameManager(main);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            FastBoard fastboard = new FastBoard(player);
            fastboard.updateTitle(translate("werewolf.score_board.title"));
            main.currentGame.boards.put(player.getUniqueId(), fastboard);
            player.setGameMode(GameMode.ADVENTURE);
            main.currentGame.join(player);
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
    public ConfigWereWolfAPI getConfig() {
        return this.config;
    }

    @Override
    public List<UUID> getQueue() {
        return this.queue;
    }

    public String translate(String key, Object... args) {
        if(!this.language.containsKey(key.toLowerCase())){
            return "(Message error)";
        }
        final String translation = this.language.get(key.toLowerCase());
        try {
            return String.format(translation, args);
        } catch (IllegalFormatException e) {
            Bukkit.getConsoleSender().sendMessage(String.format("Error while formatting translation (%s)", key.toLowerCase()));
            return translation + " (Format error)";
        }
    }

    @Override
    public void setModerators(List<UUID> moderatorsUUIDs) {
        this.moderators = moderatorsUUIDs;
    }

    @Override
    public void setWhiteListedPlayers(List<UUID> whiteListedPlayers) {
        this.whiteListedPlayers = whiteListedPlayers;
    }

    @Override
    public void setHosts(List<UUID> hostsUUIDs) {
        this.hosts = hostsUUIDs;
    }

    @Override
    public UUID getGameUUID() {
        return uuid;
    }

    public Random getRandom() {
        return r;
    }
}
