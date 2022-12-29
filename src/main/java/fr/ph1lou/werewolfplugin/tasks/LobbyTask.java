package fr.ph1lou.werewolfplugin.tasks;

import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
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

        game.getScore().updateBoard();

        if (game.isState(StateGame.TRANSPORTATION)) {
            cancel();
            BukkitUtils.registerListener(new TransportationTask(game));
        }
    }
}
