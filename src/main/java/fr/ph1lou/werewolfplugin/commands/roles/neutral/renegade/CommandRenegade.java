package fr.ph1lou.werewolfplugin.commands.roles.neutral.renegade;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.renegade.RenegadeSwapEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.neutrals.Renegade;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.renegade.command",
        roleKeys = RoleBase.RENEGADE,
        requiredPower = true,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE, StatePlayer.JUDGEMENT }, excludeSelf = true)))
public class CommandRenegade implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Renegade renegade = (Renegade) playerWW.getRole();
        IPlayerWW target = ctx.player(0);

        if (renegade.isJoined()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.renegade.already_joined");
            return;
        }

        if (!renegade.hasDiscovered(target.getUUID())) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.renegade.not_discovered",
                    Formatter.player(target.getName()));
            return;
        }

        RenegadeSwapEvent event = new RenegadeSwapEvent(playerWW, target);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        renegade.swap(target);
        renegade.setPower(false);

        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.renegade.swap_perform",
                Formatter.player(target.getName()));
    }
}
