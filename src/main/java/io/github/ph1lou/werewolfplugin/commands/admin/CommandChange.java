package io.github.ph1lou.werewolfplugin.commands.admin;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;

public class CommandChange implements Commands {


    private final Main main;

    public CommandChange(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();


        if (!game.isState(StateLG.LOBBY)) {
            game.translate("werewolf.check.game_in_progress");
            return;
        }

        player.sendMessage(game.translate("werewolf.commands.admin.change.in_progress"));
        if (game.getMapManager().getWft() != null) {
            game.getMapManager().getWft().stop();
            game.getMapManager().setWft(null);
        }
        game.getMapManager().deleteMap();
        game.getMapManager().createMap();
        player.sendMessage(game.translate("werewolf.commands.admin.change.finished"));
    }
}
