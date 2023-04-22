package fr.ph1lou.werewolfplugin.commands.roles.villager.sister;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.events.roles.sister.SisterSeeNameEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import org.bukkit.Bukkit;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.sister.command_name",
        roleKeys = RoleBase.SISTER,
        argNumbers = 1,
        autoCompletion = false)
public class CommandSisterSeeName implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        IPlayerWW killerWW = args[0].equals("pve") ? null : game.getPlayerWW(UUID.fromString(args[0])).orElse(null);

        IAffectedPlayers affectedPlayers = (IAffectedPlayers) playerWW.getRole();

        if (!affectedPlayers.getAffectedPlayers().contains(killerWW)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.sister.already");
            return;
        }

        affectedPlayers.removeAffectedPlayer(killerWW);

        SisterSeeNameEvent sisterSeeNameEvent = new SisterSeeNameEvent(playerWW, killerWW);

        Bukkit.getPluginManager().callEvent(sisterSeeNameEvent);

        if (sisterSeeNameEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }


        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.sister.reveal_killer_name",
                Formatter.player(
                        killerWW != null ?
                                killerWW.getName() :
                                game.translate("werewolf.utils.pve")));
    }
}
