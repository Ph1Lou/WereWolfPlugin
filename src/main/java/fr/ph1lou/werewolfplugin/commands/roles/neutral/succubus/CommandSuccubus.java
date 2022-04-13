package fr.ph1lou.werewolfplugin.commands.roles.neutral.succubus;

import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.succubus.BeginCharmEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandSuccubus implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole succubus = playerWW.getRole();

        if (!((IAffectedPlayers) succubus).getAffectedPlayers().isEmpty()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.power");
            return;
        }

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (argUUID.equals(uuid)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.not_yourself");
            return;
        }

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.player_not_found");
            return;
        }

        Location location = player.getLocation();
        Location locationTarget = playerArg.getLocation();

        if (player.getWorld().equals(playerArg.getWorld())) {
            if (location.distance(locationTarget) > game.getConfig().getDistanceSuccubus()) {
                playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.succubus.not_enough_near");
                return;
            }
        } else {
            return;
        }

        BeginCharmEvent beginCharmEvent = new BeginCharmEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(beginCharmEvent);

        if (beginCharmEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) succubus).addAffectedPlayer(playerWW1);
        playerWW.sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.succubus.charming_beginning",
                Formatter.player(playerArg.getName()));
    }
}
