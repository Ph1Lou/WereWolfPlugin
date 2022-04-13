package fr.ph1lou.werewolfplugin.commands.admin;

import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandStop implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        Bukkit.broadcastMessage(game.translate(Prefix.RED.getKey() , "werewolf.commands.admin.stop.send",
                Formatter.player(player.getName())));
        ((GameManager) game).setState(StateGame.END);
        game.stopGame();

    }
}
