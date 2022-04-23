package fr.ph1lou.werewolfplugin.commands.roles.villager.sister;

import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.events.roles.sister.SisterSeeNameEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandSisterSeeName implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);
        IPlayerWW killerWW = args[0].equals("pve") ? null : game.getPlayerWW(UUID.fromString(args[0])).orElse(null);
        if (playerWW == null) return;

        IAffectedPlayers affectedPlayers = (IAffectedPlayers) playerWW.getRole();

        if (!affectedPlayers.getAffectedPlayers().contains(killerWW)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.sister.already");
            return;
        }

        affectedPlayers.removeAffectedPlayer(killerWW);

        SisterSeeNameEvent sisterSeeNameEvent = new SisterSeeNameEvent(playerWW, killerWW);

        Bukkit.getPluginManager().callEvent(sisterSeeNameEvent);

        if (sisterSeeNameEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }


        playerWW.sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.sister.reveal_killer_name",
                Formatter.player(
                killerWW != null ?
                        killerWW.getName() :
                        game.translate("werewolf.utils.pve")));
    }
}
