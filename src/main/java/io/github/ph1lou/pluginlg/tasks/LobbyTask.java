package io.github.ph1lou.pluginlg.tasks;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;


public class LobbyTask extends BukkitRunnable {

    private final MainLG main;
    private final GameManager game;

    public LobbyTask(MainLG main, GameManager game) {
        this.main = main;
        this.game=game;
    }


    @Override
    public void run() {

        if (game.isState(StateLG.FIN)) {
            game.deleteGame();
            cancel();
        }

        game.score.updateBoard();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (game.getWorld().equals(p.getWorld())) {
                if (game.wft == null) {
                    if (p.isOp() || p.hasPermission("adminLG.use") || p.hasPermission("adminLG.generation.use")) {
                        Title.sendActionBar(p, game.text.getText(164));
                    }
                } else {
                    Title.sendActionBar(p, String.format(game.text.getText(222), new DecimalFormat("0.0").format(game.wft.getPercentageCompleted())));
                }
            }
        }

        if (game.isState(StateLG.TRANSPORTATION)) {
            TransportationTask transportationTask = new TransportationTask(main,game);
            transportationTask.runTaskTimer(main, 0, 4);
            cancel();
        }

    }
}
