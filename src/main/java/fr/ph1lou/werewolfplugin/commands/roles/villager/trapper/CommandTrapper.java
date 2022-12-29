package fr.ph1lou.werewolfplugin.commands.roles.villager.trapper;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.trapper.TrackEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.trapper.command",
        roleKeys = RoleBase.TRAPPER,
        requiredPower = true,
        argNumbers = 1)
public class CommandTrapper implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        UUID uuid = playerWW.getUUID();

        IRole trapper = playerWW.getRole();
        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.offline_player");
            return;
        }

        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (uuid.equals(argUUID)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.not_yourself");
            return;
        }

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.player_not_found");
            return;
        }

        if (((IAffectedPlayers) trapper).getAffectedPlayers().contains(playerWW1)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.already_get_power");
            return;
        }

        TrackEvent trackEvent = new TrackEvent(playerWW, playerWW1);
        ((IPower) trapper).setPower(false);
        Bukkit.getPluginManager().callEvent(trackEvent);

        if (trackEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) trapper).clearAffectedPlayer();
        ((IAffectedPlayers) trapper).addAffectedPlayer(playerWW1);

        playerArg.sendMessage(game.translate(Prefix.YELLOW , "werewolf.roles.trapper.get_track"));
        playerWW.sendMessageWithKey(Prefix.YELLOW , "werewolf.roles.trapper.tracking_perform",
                Formatter.player(playerArg.getName()));
    }
}
