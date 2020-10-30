package io.github.ph1lou.werewolfplugin.tasks;

import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.events.UpdateEvent;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;


public class LobbyTask extends BukkitRunnable {


    private final GameManager game;

    public LobbyTask(GameManager game) {
        this.game = game;
    }

    @Override
    public void run() {

        if (game.isState(StateGame.END)) {
            cancel();
            return;
        }

        Bukkit.getPluginManager().callEvent(new UpdateEvent());

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (game.getMapManager().getWft() == null) {
                if (p.isOp() || p.hasPermission("a.use") || p.hasPermission("a.generation.use") || game.getModerationManager().getHosts().contains(p.getUniqueId())) {
                    VersionUtils.getVersionUtils().sendActionBar(p, game.translate("werewolf.action_bar.generation"));
                }
            } else if (game.getMapManager().getWft().getPercentageCompleted() < 100) {
                VersionUtils.getVersionUtils().sendActionBar(p, game.translate("werewolf.action_bar.progress", new DecimalFormat("0.0").format(game.getMapManager().getWft().getPercentageCompleted())));
            } else {
                VersionUtils.getVersionUtils().sendActionBar(p, game.translate("werewolf.action_bar.complete"));
            }
        }

        if (game.isState(StateGame.TRANSPORTATION)) {
            new TransportationTask(game);
            cancel();
        }
    }
}
