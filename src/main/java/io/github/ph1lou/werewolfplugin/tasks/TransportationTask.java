package io.github.ph1lou.werewolfplugin.tasks;

import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.enumlg.Timers;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class TransportationTask {

    private final GameManager game;
    private final Main main;
    private final List<Location> spawns = new ArrayList<>();
    private final Map<Integer, Integer> taskStep = new HashMap<>();

    public TransportationTask(GameManager game) {
        this.game = game;
        this.main = game.getMain();
        step0();
    }


    private void step0() {

        World world = game.getMapManager().getWorld();
        AtomicInteger i = new AtomicInteger();
        taskIdManager(0, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            if (i.get() == game.getScore().getPlayerSize()) {
                kill(0);
                step1();

                return;
            }
            initStructure(world, i.getAndIncrement());

        }, 0, 20));

    }


    private void step1() {

        AtomicInteger i = new AtomicInteger();

        taskIdManager(1, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            if (i.get() == game.getScore().getPlayerSize()) {
                kill(1);
                step2();
                return;
            }
            teleportPlayer(i.getAndIncrement());

        }, 0, 20));

    }

    private void step2() {

        AtomicInteger i = new AtomicInteger(10);

        taskIdManager(2, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            if (i.get() == 0) {
                kill(2);
                step3();
                return;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                VersionUtils.getVersionUtils().sendTitle(p, "Start", "§b" + i.get(), 25, 20, 25);
                Sounds.NOTE_PIANO.play(p);
            }
            i.getAndDecrement();

        }, 0, 20));


    }

    private void step3() {

        taskIdManager(3, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            if (spawns.isEmpty()) {
                kill(3);
                step4();
                return;
            }
            createStructure(Material.AIR, spawns.get(0));
            spawns.remove(0);

        }, 0, 1));

    }

    private void step4() {

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (game.getPlayersWW().containsKey(player.getUniqueId())) {
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(game.translate("werewolf.announcement.start.message", game.getScore().conversion(game.getConfig().getTimerValues().get(Timers.INVULNERABILITY.getKey()))));
            } else {
                player.teleport(game.getMapManager().getWorld().getSpawnLocation());
                player.setGameMode(GameMode.SPECTATOR);
            }

            VersionUtils.getVersionUtils().sendTitle(player, game.translate("werewolf.announcement.start.top_title"), game.translate("werewolf.announcement.start.bot_title"), 20, 20, 20);
            Sounds.NOTE_BASS.play(player);
        }

        game.getScenarios().updateCompass();
        game.setState(StateGame.START);
        GameTask start = new GameTask(game);
        start.runTaskTimer(main, 0, 5);
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> Bukkit.getPluginManager().callEvent(new DayEvent(1)), 40);
    }

    private void teleportPlayer(int i) {

        for (Player p : Bukkit.getOnlinePlayers()) {
            Sounds.ORB_PICKUP.play(p);
            VersionUtils.getVersionUtils().sendActionBar(p, game.translate("werewolf.action_bar.tp", i + 1, game.getPlayersWW().size()));
        }

        UUID uuid = (UUID) game.getPlayersWW().keySet().toArray()[i];
        game.getPlayersWW().get(uuid).setSpawn(spawns.get(i));
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {

            player.setGameMode(GameMode.ADVENTURE);
            game.clearPlayer(player);
            Inventory inventory = player.getInventory();
            inventory.clear();

            for (int j = 0; j < 40; j++) {
                inventory.setItem(j, game.getStuffs().getStartLoot().getItem(j));
            }

            player.teleport(spawns.get(i));
        }
    }

    private void taskIdManager(int step, int taskId) {
        taskStep.put(step, taskId);
    }

    private void kill(int step) {
        Bukkit.getScheduler().cancelTask(taskStep.get(step));
    }

    private void initStructure(World world, int i) {

        WorldBorder wb = world.getWorldBorder();

        for (Player p : Bukkit.getOnlinePlayers()) {
            Sounds.DIG_GRASS.play(p);
            VersionUtils.getVersionUtils().sendActionBar(p, game.translate("werewolf.action_bar.create_tp_point", i + 1, game.getPlayersWW().size()));
        }

        double a = i * 2 * Math.PI / game.getScore().getPlayerSize();
        int x = (int) (Math.round(wb.getSize() / 3 * Math.cos(a) + world.getSpawnLocation().getX()));
        int z = (int) (Math.round(wb.getSize() / 3 * Math.sin(a) + world.getSpawnLocation().getZ()));
        Location spawn = new Location(world, x, world.getHighestBlockYAt(x, z) + 100, z);
        world.getChunkAt(x, z).load(true);
        spawns.add(spawn);
        createStructure(Material.BARRIER, spawn);
    }


    private void createStructure(Material m, Location location) {

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        World world = game.getMapManager().getWorld();
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                if (Math.abs(j) == 2 || Math.abs(i) == 2) {
                    for (int k = 0; k < 2; k++) {
                        new Location(world, x + i, y - 1 + k, z + j).getBlock().setType(m);
                    }
                }
                new Location(world, x + i, y - 2, z + j).getBlock().setType(m);
                new Location(world, x + i, y + 2, z + j).getBlock().setType(m);
            }
        }
    }


}
