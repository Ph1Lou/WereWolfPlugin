package fr.ph1lou.werewolfplugin.commands.roles.neutral.thug;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.roles.thug.ThugEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.thug.command",
        roleKeys = RoleBase.THUG,
        argNumbers = 1,
        requiredPower = true,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE },
                rejectIfAlreadyAffected = true)))
public class CommandThug implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole thug = playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);


        ((IPower) thug).setPower(false);

        ThugEvent thugEvent = new ThugEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(thugEvent);

        if (thugEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) thug).clearAffectedPlayer();
        ((IAffectedPlayers) thug).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.thug.perform", Formatter.player(playerWW1.getName()));

        playerWW1.sendMessageWithKey(Prefix.RED, "werewolf.roles.thug.alert");

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW1));
    }
}
