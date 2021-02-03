package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.BeginSniffEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.LimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Progress;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandFox implements Commands {


    private final Main main;

    public CommandFox(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles fox = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        PlayerWW playerWW1 = game.getPlayerWW(argUUID);

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


        if (((LimitedUse) fox).getUse() >= game.getConfig().getUseOfFlair()) {
            playerWW.sendMessageWithKey("werewolf.check.power");
            return;
        }

        ((Power) fox).setPower(false);
        ((LimitedUse) fox).setUse(((LimitedUse) fox).getUse() + 1);

        BeginSniffEvent beginSniffEvent = new BeginSniffEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(beginSniffEvent);

        if (beginSniffEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        ((AffectedPlayers) fox).clearAffectedPlayer();
        ((AffectedPlayers) fox).addAffectedPlayer(playerWW1);
        ((Progress) fox).setProgress(0f);

        playerWW.sendMessageWithKey("werewolf.role.fox.smell_beginning", playerArg.getName());
    }
}
