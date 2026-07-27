package fr.ph1lou.werewolfplugin.commands.roles.villager.trapper;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.trapper.TrackEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.trapper.command",
        roleKeys = RoleBase.TRAPPER,
        requiredPower = true,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true,
                rejectIfAlreadyAffected = true)))
public class CommandTrapper implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole trapper = playerWW.getRole();
        IPlayerWW playerWW1 = ctx.player(0);

        TrackEvent trackEvent = new TrackEvent(playerWW, playerWW1);
        ((IPower) trapper).setPower(false);
        Bukkit.getPluginManager().callEvent(trackEvent);

        if (trackEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) trapper).clearAffectedPlayer();
        ((IAffectedPlayers) trapper).addAffectedPlayer(playerWW1);

        playerWW1.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.trapper.get_track");
        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.trapper.tracking_perform",
                Formatter.player(playerWW1.getName()));
    }
}
