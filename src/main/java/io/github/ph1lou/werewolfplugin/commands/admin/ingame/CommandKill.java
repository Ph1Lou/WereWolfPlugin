package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandKill implements Commands {


    private final Main main;

    public CommandKill(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = (GameManager) main.getWereWolfAPI();

        boolean find = false;

        UUID argUUID = null;
        PlayerWW playerWW1 = null;

        for (PlayerWW playerWW : game.getPlayerWW()) {
            if (playerWW.getName().equalsIgnoreCase(args[0])) {
                find = true;
                argUUID = playerWW.getUUID();
                playerWW1 = playerWW;
            }
        }
        if (!find) {
            player.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }

        if (!playerWW1.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate("werewolf.commands.kill.not_living"));
            return;
        }
        if (game.isState(StateGame.START)) {
            game.getScore().removePlayerSize();
            game.remove(argUUID);
            player.sendMessage(game.translate("werewolf.commands.kill.remove_role"));
            return;
        }
        if (Bukkit.getPlayer(args[0]) != null) {
            player.sendMessage(game.translate("werewolf.commands.kill.on_line"));
            return;
        }

        game.death(playerWW1);
    }
}
