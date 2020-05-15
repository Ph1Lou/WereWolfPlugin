package io.github.ph1lou.pluginlg.tasks;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.events.DayEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.utils.Title;
import io.github.ph1lou.pluginlgapi.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;


public class TransportationTask extends BukkitRunnable {

    private final MainLG main;
    private final GameManager game;
    private int i = 0;
    private int j = 0;

    public TransportationTask(MainLG main, GameManager game) {
        this.main = main;
        this.game=game;
    }


    private void createStructure(Material m, Location location) {

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        World world = game.getWorld();
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

    @Override
    public void run() {


        if (game.isState(StateLG.END)) {
            cancel();
            return;
        }
        game.score.updateBoard();
        World world = game.getWorld();
        WorldBorder wb = world.getWorldBorder();

        if (i < game.playerLG.size()) {

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sound.DIG_GRASS, 1, 20);
                Title.sendActionBar(p, game.translate("werewolf.action_bar.create_tp_point", i + 1, game.playerLG.size()));
            }

            UUID uuid = (UUID) game.playerLG.keySet().toArray()[i];

            double a = i * 2 * Math.PI / Bukkit.getOnlinePlayers().size();
            int x = (int) (Math.round(wb.getSize() / 3 * Math.cos(a) + world.getSpawnLocation().getX()));
            int z = (int) (Math.round(wb.getSize() / 3 * Math.sin(a) + world.getSpawnLocation().getZ()));
            Location spawn = new Location(world, x, world.getHighestBlockYAt(x, z) + 100, z);
            world.getChunkAt(x, z).load(true);
            createStructure(Material.BARRIER, spawn);
            game.playerLG.get(uuid).setSpawn(spawn.clone());
        } else if (i < 2 * game.playerLG.size()) {

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 20);
                Title.sendActionBar(p, game.translate("werewolf.action_bar.tp", i - game.playerLG.size() + 1, game.playerLG.size()));
            }

            UUID uuid = (UUID) game.playerLG.keySet().toArray()[i - game.playerLG.size()];

            if (Bukkit.getPlayer(uuid) != null) {
                Player player = Bukkit.getPlayer(uuid);
                player.setGameMode(GameMode.ADVENTURE);
                game.clearPlayer(player);
                Inventory inventory = player.getInventory();
                inventory.clear();

                for (int j = 0; j < 40; j++) {
                    inventory.setItem(j, game.stufflg.getStartLoot().getItem(j));
                }
                if (game.config.getScenarioValues().get(ScenarioLG.CAT_EYES)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
                }
                player.teleport(game.playerLG.get(uuid).getSpawn());
            }
        } else if (i % 5 == 0 && j == 10) {
            for (PlayerLG plg : game.playerLG.values()) {
                createStructure(Material.AIR, plg.getSpawn());
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (game.playerLG.containsKey(p.getUniqueId())) {
                    p.setGameMode(GameMode.SURVIVAL);
                    p.sendMessage(game.translate("werewolf.announcement.start.message",game.score.conversion(game.config.getTimerValues().get(TimerLG.INVULNERABILITY))));
                } else {
                    p.teleport(game.getWorld().getSpawnLocation());
                    p.setGameMode(GameMode.SPECTATOR);
                }

                Title.sendTitle(p, 20, 20, 20, game.translate("werewolf.announcement.start.top_title"), game.translate("werewolf.announcement.start.bot_title"));
                p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 20);
            }
            world.setTime(0);

            game.optionlg.updateCompass();
            game.setState(StateLG.START);
            GameTask start = new GameTask(game);
            start.runTaskTimer(main, 0, 5);
            Bukkit.getPluginManager().callEvent(new DayEvent(game.getGameUUID(),1));
            cancel();
        } else if (i % 5 == 0) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Title.sendTitle(p, 25, 20, 25, "Start", "§b" + (10 - j));
                p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1, 20);
            }
            j++;
        }
        i++;
    }
}
