package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AdminCommand(key = "werewolf.commands.late.command",
        descriptionKey = "werewolf.commands.late.description",
        stateGame = {StateGame.TRANSPORTATION, StateGame.START},
        argNumbers = 1,
        moderatorAccess = true)
public class CommandLate implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        Player player1 = Bukkit.getPlayer(args[0]);

        if (player1 == null) {
            player.sendMessage(game.translate(Prefix.RED , "werewolf.check.offline_player"));
            return;
        }

        UUID uuid = player1.getUniqueId();

        if (game.getPlayerWW(uuid).isPresent()) {
            player.sendMessage(game.translate(Prefix.RED , "werewolf.commands.late.in_game"));
            return;
        }

        if (game.getModerationManager().getModerators().contains(uuid)) {
            return;
        }

        Bukkit.broadcastMessage(game.translate(Prefix.GREEN , "werewolf.commands.late.launch",
                Formatter.player(player1.getName())));

        ((GameManager) game).addLatePlayer(player1);
    }
}
