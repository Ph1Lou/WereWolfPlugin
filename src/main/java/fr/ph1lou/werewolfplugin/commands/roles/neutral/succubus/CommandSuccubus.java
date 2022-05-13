package fr.ph1lou.werewolfplugin.commands.roles.neutral.succubus;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.succubus.BeginCharmEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.neutrals.Succubus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.role.succubus.command",
        roleKeys = RoleBase.SUCCUBUS,
        requiredPower = true,
        argNumbers = 1)
public class CommandSuccubus implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole succubus = playerWW.getRole();

        if (!((IAffectedPlayers) succubus).getAffectedPlayers().isEmpty()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.power");
            return;
        }

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (argUUID.equals(uuid)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.not_yourself");
            return;
        }

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.player_not_found");
            return;
        }

        Location location = player.getLocation();
        Location locationTarget = playerArg.getLocation();

        if (player.getWorld().equals(playerArg.getWorld())) {
            if (location.distance(locationTarget) > game.getConfig().getValue(Succubus.DISTANCE)) {
                playerWW.sendMessageWithKey(Prefix.RED , "werewolf.role.succubus.not_enough_near");
                return;
            }
        } else {
            return;
        }

        BeginCharmEvent beginCharmEvent = new BeginCharmEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(beginCharmEvent);

        if (beginCharmEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) succubus).addAffectedPlayer(playerWW1);
        playerWW.sendMessageWithKey(Prefix.YELLOW , "werewolf.role.succubus.charming_beginning",
                Formatter.player(playerArg.getName()));
    }
}
