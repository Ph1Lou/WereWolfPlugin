package fr.ph1lou.werewolfplugin.commands.roles.villager.raven;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.events.roles.scape_goat.ScapeGoatEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import org.bukkit.Bukkit;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.scape_goat.command_name",
        roleKeys = RoleBase.SCAPE_GOAT,
        requiredPower = true,
        argNumbers = 1,
        autoCompletion = false)
public class CommandScapeGoat implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        IPlayerWW voteWW = game.getPlayerWW(UUID.fromString(args[0])).orElse(null);

        if (voteWW == null) {
            return;
        }

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
