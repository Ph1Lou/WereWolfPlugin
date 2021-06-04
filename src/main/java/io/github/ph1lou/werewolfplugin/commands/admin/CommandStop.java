package io.github.ph1lou.werewolfplugin.commands.admin;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandStop implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.stop.send",
                player.getName()));
        ((GameManager) game).setState(StateGame.END);
        game.stopGame();

    }
}
