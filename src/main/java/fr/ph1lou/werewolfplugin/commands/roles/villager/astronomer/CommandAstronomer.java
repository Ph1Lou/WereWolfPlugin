package fr.ph1lou.werewolfplugin.commands.roles.villager.astronomer;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.events.roles.astronomer.TrackEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RoleCommand(key = "werewolf.roles.astronomer.command",
        roleKeys = RoleBase.ASTRONOMER,
        requiredPower = true,
        argNumbers = 0)
public class CommandAstronomer implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        IRole astronomer = playerWW.getRole();

        if (game.isDay(Day.DAY)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.night");
            return;
        }

        List<IPlayerWW> playerWWS = game.getAlivePlayersWW()
                .stream()
                .filter(iPlayerWW -> !iPlayerWW.equals(playerWW))
                .filter(iPlayerWW -> iPlayerWW.getRole().isNeutral() || iPlayerWW.getRole().isWereWolf())
                .filter(iPlayerWW -> iPlayerWW.distance(playerWW) > 20)
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) {
            return;
        }

        Collections.shuffle(playerWWS, game.getRandom());

        IPlayerWW playerWW1 = playerWWS.get(0);

        TrackEvent trackEvent = new TrackEvent(playerWW, playerWW1);
        ((IPower) astronomer).setPower(false);
        Bukkit.getPluginManager().callEvent(trackEvent);

        if (trackEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) astronomer).clearAffectedPlayer();
        ((IAffectedPlayers) astronomer).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.astronomer.tracking_perform");

        BukkitUtils.scheduleSyncDelayedTask(game, () -> ((IPower) astronomer).setPower(true), 2 * 60 * 20);
    }
}
