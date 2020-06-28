package io.github.ph1lou.werewolfplugin.tasks;

import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;


public class LobbyTask extends BukkitRunnable {

    private final Main main;
    private final GameManager game;

    public LobbyTask(Main main, GameManager game) {
        this.main = main;
        this.game=game;
    }

    @Override
    public void run() {

        if (game.isState(StateLG.END)) {
            cancel();
            return;
        }

        game.score.updateBoard();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (game.wft == null) {
                if (p.isOp() || p.hasPermission("a.use") || p.hasPermission("a.generation.use") || game.getHosts().contains(p.getUniqueId())) {
                    Title.sendActionBar(p, game.translate("werewolf.action_bar.generation"));
                }
            } else if(game.wft.getPercentageCompleted()<100){
                Title.sendActionBar(p, game.translate("werewolf.action_bar.progress", new DecimalFormat("0.0").format(game.wft.getPercentageCompleted())));
            }
            else{
                Title.sendActionBar(p, game.translate("werewolf.action_bar.complete"));
            }
        }

        if (game.isState(StateLG.TRANSPORTATION)) {
            TransportationTask transportationTask = new TransportationTask(main,game);
            transportationTask.runTaskTimer(main, 0, 4);
            cancel();
        }
    }
}
