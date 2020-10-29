package io.github.ph1lou.werewolfplugin.commands.admin;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandStop implements Commands {


    private final Main main;

    public CommandStop(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();

        if (game.isState(StateLG.LOBBY)) return;

        Bukkit.broadcastMessage(game.translate("werewolf.announcement.stop", player.getName()));

        game.stopGame();

    }
}
