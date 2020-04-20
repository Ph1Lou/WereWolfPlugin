package io.github.ph1lou.pluginlg.tasks;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.Title;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;


public class LobbyTask extends BukkitRunnable {

    private final MainLG main;

    public LobbyTask(MainLG main) {
        this.main = main;
    }


    @Override
    public void run() {

        main.score.updateBoard();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (main.wft == null) {
                if (p.isOp() || p.hasPermission("adminLG.use") || p.hasPermission("adminLG.generation.use")) {
                    Title.sendActionBar(p, main.text.getText(164));
                }
            } else {
                Title.sendActionBar(p, String.format(main.text.getText(222), new DecimalFormat("0.0").format(main.wft.getPercentageCompleted())));
            }
        }

        if (main.isState(StateLG.TRANSPORTATION)) {
            TransportationTask transportationTask = new TransportationTask(main);
            transportationTask.runTaskTimer(main, 0, 4);
            cancel();
        }


    }
}
