package io.github.ph1lou.werewolfplugin.tasks;

import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.events.UpdateEvent;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;


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

        if (game.isState(StateGame.TRANSPORTATION)) {
            cancel();
            Bukkit.getPluginManager().registerEvents(new TransportationTask(game), game.getMain());
        }
    }
}
