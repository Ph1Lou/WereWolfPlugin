package fr.ph1lou.werewolfplugin.commands.roles.villager.scape_goat;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.events.roles.scape_goat.ScapeGoatEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.scape_goat.command_name",
        roleKeys = RoleBase.SCAPE_GOAT,
        requiredPower = true,
        argNumbers = 1,
        autoCompletion = false,
        args = @Params(players = @PlayerArg(index = 0, byUuid = true)))
public class CommandScapeGoat implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IPlayerWW voteWW = ctx.player(0);

        ScapeGoatEvent scapeGoatEvent = new ScapeGoatEvent(playerWW, voteWW);
        Bukkit.getPluginManager().callEvent(scapeGoatEvent);

        if (scapeGoatEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
        }

        ((IAffectedPlayers) playerWW.getRole()).clearAffectedPlayer();
        ((IAffectedPlayers) playerWW.getRole()).addAffectedPlayer(voteWW);
        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.scape_goat.perform", Formatter.player(voteWW.getName()));
    }
}
