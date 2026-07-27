package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommand;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.revive.command",
        descriptionKey = "werewolf.commands.admin.revive.description",
        statesGame = StateGame.GAME,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.DEATH },
                stateMessageKey = "werewolf.commands.admin.revive.not_death")))
public class CommandRevive implements IParameterizedCommand {

    @Override
    public void execute(WereWolfAPI game, Player player, CommandContext ctx) {

        IPlayerWW playerWW1 = ctx.player(0);
        UUID uuid = playerWW1.getUUID();

        if (game.getModerationManager().getModerators().contains(uuid)) {
            Bukkit.dispatchCommand(player, "a moderator " + playerWW1.getName());
        }

        IRole role = playerWW1.getRole();
        game.getConfig().addOneRole(role.getKey());
        ((GameManager) game).setPlayerSize(game.getPlayersCount() + 1);
        game.resurrection(playerWW1);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(game.translate(Prefix.YELLOW, "werewolf.commands.admin.revive.perform",
                    Formatter.player(playerWW1.getName()),
                    Formatter.format("&admin&", player.getName())));
            Sound.AMBIENCE_THUNDER.play(p);
        }
    }
}
