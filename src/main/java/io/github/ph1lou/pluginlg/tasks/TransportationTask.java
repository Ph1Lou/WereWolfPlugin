package io.github.ph1lou.pluginlg.tasks;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.Title;
import io.github.ph1lou.pluginlg.enumlg.ScenarioLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.enumlg.TimerLG;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


public class TransportationTask extends BukkitRunnable {

    private final MainLG main;
    private int i = 0;
    private int j = 0;

    public TransportationTask(MainLG main) {
        this.main = main;
    }


    private void createStructure(Material m, Location location) {

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        try {
            World world = Bukkit.getWorld("world");
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
            Bukkit.broadcastMessage(main.text.getText(21));
        }
    }

    @Override
    public void run() {

        try {
            main.score.updateBoard();
            World world = Bukkit.getWorld("world");
            WorldBorder wb = world.getWorldBorder();

            if (i < main.playerLG.size()) {

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.DIG_GRASS, 1, 20);
                    Title.sendActionBar(p, String.format(main.text.getText(33), i + 1, main.playerLG.size()));
                }

                String playername = (String) main.playerLG.keySet().toArray()[i];

                double a = i * 2 * Math.PI / Bukkit.getOnlinePlayers().size();
                int x = (int) (Math.round(wb.getSize() / 3 * Math.cos(a) + world.getSpawnLocation().getX()));
                int z = (int) (Math.round(wb.getSize() / 3 * Math.sin(a) + world.getSpawnLocation().getZ()));
                Location spawn = new Location(world, x, world.getHighestBlockYAt(x, z) + 100, z);
                world.getChunkAt(x, z).load(true);
                createStructure(Material.BARRIER, spawn);
                main.playerLG.get(playername).setSpawn(spawn.clone());
            } else if (i < 2 * main.playerLG.size()) {

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 20);
                    Title.sendActionBar(p, String.format(main.text.getText(34), i - main.playerLG.size() + 1, main.playerLG.size()));
                }

                String playername = (String) main.playerLG.keySet().toArray()[i - main.playerLG.size()];

                if (Bukkit.getPlayer(playername) != null) {
                    Player player = Bukkit.getPlayer(playername);
                    player.setGameMode(GameMode.ADVENTURE);
                    main.playerLG.get(playername).clearPlayer(player);
                    for (ItemStack it : main.stufflg.getStartLoot()) {
                        player.getInventory().addItem(it);
                    }
                    if (main.config.scenarioValues.get(ScenarioLG.CAT_EYES)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
                    }
                    player.teleport(main.playerLG.get(playername).getSpawn());
                }
            } else if (i % 5 == 0 && j == 10) {
                for (PlayerLG plg : main.playerLG.values()) {
                    createStructure(Material.AIR, plg.getSpawn());
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (main.playerLG.containsKey(p.getName())) {
                        p.setGameMode(GameMode.SURVIVAL);
                    }
                    Title.sendTitle(p, 20, 20, 20, main.text.getText(89), main.text.getText(90));
                    p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 20);
                }
                Bukkit.broadcastMessage(String.format(main.text.getText(121), main.config.timerValues.get(TimerLG.INVULNERABILITY)));
                world.setTime(0);
                main.optionlg.updateCompass();
                main.setState(StateLG.DEBUT);
                GameTask start = new GameTask(main);
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
        } catch (Exception e) {
            Bukkit.broadcastMessage(main.text.getText(21));
        }
    }
}
