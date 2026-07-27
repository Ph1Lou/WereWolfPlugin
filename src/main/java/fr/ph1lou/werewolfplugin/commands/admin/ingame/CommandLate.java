package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerBukkitArg;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.late.command",
        descriptionKey = "werewolf.commands.admin.late.description",
        statesGame = {StateGame.TRANSPORTATION, StateGame.START},
        argNumbers = 1,
        moderatorAccess = true,
        args = @Params(bukkitPlayers = @PlayerBukkitArg(index = 0)))
public class CommandLate implements IParameterizedCommand {

    @Override
    public void execute(WereWolfAPI game, Player player, CommandContext ctx) {

        Player player1 = ctx.bukkitPlayer(0);

        UUID uuid = player1.getUniqueId();

        if (game.getPlayerWW(uuid).isPresent()) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.late.in_game"));
            return;
        }

        if (game.getModerationManager().getModerators().contains(uuid)) {
            return;
        }

        if(game.isState(StateGame.TRANSPORTATION)){
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.late.teleportation"));
            return;
        }

        Bukkit.broadcastMessage(game.translate(Prefix.GREEN, "werewolf.commands.admin.late.launch",
                Formatter.player(player1.getName())));

        ((GameManager) game).addLatePlayer(player1);
    }
}
