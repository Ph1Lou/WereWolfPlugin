package fr.ph1lou.werewolfplugin.commands.roles.villager.sister;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.events.roles.sister.SisterSeeRoleEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.role.sister.command_role",
        roleKeys = RoleBase.SISTER,
        autoCompletion = false,
        argNumbers = 1)
public class CommandSisterSeeRole implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);
        IPlayerWW killerWW = args[0].equals("pve") ? null : game.getPlayerWW(UUID.fromString(args[0])).orElse(null);
        if (playerWW == null) return;

        IAffectedPlayers affectedPlayers = (IAffectedPlayers) playerWW.getRole();

        if (!affectedPlayers.getAffectedPlayers().contains(killerWW)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.role.sister.already");
            return;
        }

        affectedPlayers.removeAffectedPlayer(killerWW);

        SisterSeeRoleEvent sisterSeeRoleEvent = new SisterSeeRoleEvent(playerWW, killerWW);

        Bukkit.getPluginManager().callEvent(sisterSeeRoleEvent);

        if (sisterSeeRoleEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }


        playerWW.sendMessageWithKey(Prefix.YELLOW , "werewolf.role.sister.reveal_killer_role",
                Formatter.role(
                killerWW != null ?
                        game.translate(killerWW.getRole().getKey()) :
                        game.translate("werewolf.utils.pve")));
    }
}
