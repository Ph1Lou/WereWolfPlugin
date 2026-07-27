package fr.ph1lou.werewolfplugin.commands.roles.villager.info.fox;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.fox.BeginSniffEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IProgress;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.commands.CommandCheck;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.fox.command",
        roleKeys = RoleBase.FOX,
        requiredPower = true,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true,
                maxDistanceKey = IntValueBase.FOX_DISTANCE,
                maxDistanceMessageKey = "werewolf.roles.fox.not_enough_near")))
public class CommandFox implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole fox = playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);

        if (CommandCheck.rejectIfNoUseLeft(playerWW, fox, game.getConfig().getValue(IntValueBase.FOX_SMELL_NUMBER))) {
            return;
        }

        ((IPower) fox).setPower(false);
        ((ILimitedUse) fox).setUse(((ILimitedUse) fox).getUse() + 1);

        BeginSniffEvent beginSniffEvent = new BeginSniffEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(beginSniffEvent);

        if (beginSniffEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) fox).clearAffectedPlayer();
        ((IAffectedPlayers) fox).addAffectedPlayer(playerWW1);
        ((IProgress) fox).setProgress(0f);

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.fox.smell_beginning",
                Formatter.player(playerWW1.getName()));
    }
}
