package io.github.ph1lou.pluginlg.tasks;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.utils.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


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
        try {
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
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(game.text.getText(21));
        }
    }

    @Override
    public void run() {


        if (game.isState(StateLG.FIN)) {
            cancel();
            return;
        }
        game.score.updateBoard();
        World world = game.getWorld();
        WorldBorder wb = world.getWorldBorder();

        if (i < game.playerLG.size()) {

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sound.DIG_GRASS, 1, 20);
                Title.sendActionBar(p, String.format(game.text.getText(33), i + 1, game.playerLG.size()));
            }

            String playername = (String) game.playerLG.keySet().toArray()[i];

            double a = i * 2 * Math.PI / Bukkit.getOnlinePlayers().size();
            int x = (int) (Math.round(wb.getSize() / 3 * Math.cos(a) + world.getSpawnLocation().getX()));
            int z = (int) (Math.round(wb.getSize() / 3 * Math.sin(a) + world.getSpawnLocation().getZ()));
            Location spawn = new Location(world, x, world.getHighestBlockYAt(x, z) + 100, z);
            world.getChunkAt(x, z).load(true);
            createStructure(Material.BARRIER, spawn);
            game.playerLG.get(playername).setSpawn(spawn.clone());
        } else if (i < 2 * game.playerLG.size()) {

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 20);
                Title.sendActionBar(p, String.format(game.text.getText(34), i - game.playerLG.size() + 1, game.playerLG.size()));
            }

            String playername = (String) game.playerLG.keySet().toArray()[i - game.playerLG.size()];

            if (Bukkit.getPlayer(playername) != null) {
                Player player = Bukkit.getPlayer(playername);
                player.setGameMode(GameMode.ADVENTURE);
                game.clearPlayer(player);
                Inventory inventory = player.getInventory();
                inventory.clear();

                for (int j = 0; j < 40; j++) {
                    inventory.setItem(j, game.stufflg.getStartLoot().getItem(j));
                }
                if (game.config.scenarioValues.get(ScenarioLG.CAT_EYES)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
                }
                player.teleport(game.playerLG.get(playername).getSpawn());
            }
        } else if (i % 5 == 0 && j == 10) {
            for (PlayerLG plg : game.playerLG.values()) {
                createStructure(Material.AIR, plg.getSpawn());
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (game.playerLG.containsKey(p.getName())) {
                    p.setGameMode(GameMode.SURVIVAL);
                    p.sendMessage(String.format(game.text.getText(121), game.config.timerValues.get(TimerLG.INVULNERABILITY)));
                } else p.teleport(game.getWorld().getSpawnLocation());

                Title.sendTitle(p, 20, 20, 20, game.text.getText(89), game.text.getText(90));
                p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 20);
            }
            world.setTime(0);
            game.optionlg.updateCompass();
            game.setState(StateLG.DEBUT);
            GameTask start = new GameTask(game);
            start.runTaskTimer(main, 0, 5);
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
