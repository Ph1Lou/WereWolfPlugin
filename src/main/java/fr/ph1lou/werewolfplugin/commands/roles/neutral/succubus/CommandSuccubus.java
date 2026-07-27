package fr.ph1lou.werewolfplugin.commands.roles.neutral.succubus;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.succubus.BeginCharmEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@RoleCommand(key = "werewolf.roles.succubus.command",
        roleKeys = RoleBase.SUCCUBUS,
        requiredPower = true,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true)))
public class CommandSuccubus implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole succubus = playerWW.getRole();

        if (!((IAffectedPlayers) succubus).getAffectedPlayers().isEmpty()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
            return;
        }

        IPlayerWW playerWW1 = ctx.player(0);

        Location location = playerWW.getLocation();
        Location locationTarget = playerWW1.getLocation();

        if (location.getWorld() == locationTarget.getWorld()) {
            if (location.distance(locationTarget) > game.getConfig().getValue(IntValueBase.SUCCUBUS_DISTANCE)) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.succubus.not_enough_near");
                return;
            }
        } else {
            return;
        }

        BeginCharmEvent beginCharmEvent = new BeginCharmEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(beginCharmEvent);

        if (beginCharmEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) succubus).addAffectedPlayer(playerWW1);
        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.succubus.charming_beginning",
                Formatter.player(playerWW1.getName()));
    }
}
