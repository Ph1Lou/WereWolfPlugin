package fr.ph1lou.werewolfplugin.commands.roles.villager.info.fox;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.fox.BeginSniffEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IProgress;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.fox.command",
        roleKeys = RoleBase.FOX,
        requiredPower = true,
        argNumbers = 1)
public class CommandFox implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        UUID uuid = playerWW.getUUID();

        IRole fox = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (argUUID.equals(uuid)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.not_yourself");
            return;
        }

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.player_not_found");
            return;
        }

        if (((ILimitedUse) fox).getUse() >= game.getConfig().getValue(IntValueBase.FOX_SMELL_NUMBER)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
            return;
        }

        Location location = playerWW.getLocation();
        Location locationTarget = playerArg.getLocation();

        if (location.getWorld() == playerArg.getWorld()) {
            if (location.distance(locationTarget) > game.getConfig().getValue(IntValueBase.FOX_DISTANCE)) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.fox.not_enough_near");
                return;
            }
        } else {
            return;
        }

        ((IPower) fox).setPower(false);
        ((ILimitedUse) fox).setUse(((ILimitedUse) fox).getUse() + 1);

        BeginSniffEvent beginSniffEvent = new BeginSniffEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(beginSniffEvent);

        if (beginSniffEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) fox).clearAffectedPlayer();
        ((IAffectedPlayers) fox).addAffectedPlayer(playerWW1);
        ((IProgress) fox).setProgress(0f);

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.fox.smell_beginning",
                Formatter.player(playerArg.getName()));
    }
}
