package fr.ph1lou.werewolfplugin.commands.roles.neutral.pyromane;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.pyromane.PyromaneCoverEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.neutrals.Pyromane;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.pyromane.cover_command",
        roleKeys = RoleBase.PYROMANE,
        argNumbers = 2,
        args = @Params(players = {
                @PlayerArg(index = 0, states = { StatePlayer.ALIVE, StatePlayer.JUDGEMENT }, excludeSelf = true),
                @PlayerArg(index = 1, states = { StatePlayer.ALIVE, StatePlayer.JUDGEMENT }, excludeSelf = true)
        }))
public class CommandPyromaneCover implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Pyromane pyromane = (Pyromane) playerWW.getRole();
        IPlayerWW target1 = ctx.player(0);
        IPlayerWW target2 = ctx.player(1);

        if (!pyromane.canCover()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.pyromane.cooldown",
                    Formatter.number(pyromane.getCoverRemaining() / 60 + 1));
            return;
        }

        if (target1.getUUID().equals(target2.getUUID())) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.pyromane.same_target");
            return;
        }

        if (!pyromane.hasMet(target1) || !pyromane.hasMet(target2)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.pyromane.not_met");
            return;
        }

        for (IPlayerWW target : new IPlayerWW[]{target1, target2}) {
            PyromaneCoverEvent event = new PyromaneCoverEvent(playerWW, target);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                pyromane.cover(target);
            }
        }

        pyromane.onCovered();
        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.pyromane.cover_perform",
                Formatter.player(target1.getName()), Formatter.format("&player1&", target2.getName()));
    }
}
