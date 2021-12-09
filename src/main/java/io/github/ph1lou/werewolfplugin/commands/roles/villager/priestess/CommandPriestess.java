package io.github.ph1lou.werewolfplugin.commands.roles.villager.priestess;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.priestess.PriestessEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandPriestess implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole priestess = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.player_not_found");
            return;
        }

        if (!player.getWorld().equals(playerArg.getWorld()) || player.getLocation().distance(playerArg.getLocation()) > game.getConfig().getDistancePriestess()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.priestess.distance",
                    Formatter.number(game.getConfig().getDistancePriestess()));
            return;
        }

        if (player.getHealth() < 5) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.seer.not_enough_life");
        } else {
            IRole role1 = playerWW1.getRole();

            PriestessEvent priestessEvent = new PriestessEvent(playerWW, playerWW1, role1.getDisplayCamp());
            ((IPower) priestess).setPower(false);
            Bukkit.getPluginManager().callEvent(priestessEvent);

            if (priestessEvent.isCancelled()) {
                playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
                return;
            }

            ((IAffectedPlayers) priestess).addAffectedPlayer(playerWW1);

            playerWW.removePlayerMaxHealth(4);

            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.priestess.message",
                    Formatter.player(playerArg.getName()),
                    Formatter.format("&camp&",game.translate(priestessEvent.getCamp())));

        }
    }
}
