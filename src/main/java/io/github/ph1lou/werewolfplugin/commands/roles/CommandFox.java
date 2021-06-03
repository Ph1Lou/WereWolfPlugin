package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.fox.BeginSniffEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.ILimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IProgress;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandFox implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole fox = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (argUUID.equals(uuid)) {
            playerWW.sendMessageWithKey("werewolf.check.not_yourself");
            return;
        }

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        Location location = player.getLocation();
        Location locationTarget = playerArg.getLocation();

        if (player.getWorld().equals(playerArg.getWorld())) {
            if (location.distance(locationTarget) > game.getConfig().getDistanceFox()) {
                playerWW.sendMessageWithKey("werewolf.role.fox.not_enough_near");
                return;
            }
        } else {
            return;
        }


        if (((ILimitedUse) fox).getUse() >= game.getConfig().getUseOfFlair()) {
            playerWW.sendMessageWithKey("werewolf.check.power");
            return;
        }

        ((IPower) fox).setPower(false);
        ((ILimitedUse) fox).setUse(((ILimitedUse) fox).getUse() + 1);

        BeginSniffEvent beginSniffEvent = new BeginSniffEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(beginSniffEvent);

        if (beginSniffEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) fox).clearAffectedPlayer();
        ((IAffectedPlayers) fox).addAffectedPlayer(playerWW1);
        ((IProgress) fox).setProgress(0f);

        playerWW.sendMessageWithKey("werewolf.role.fox.smell_beginning", playerArg.getName());
    }
}
