package fr.ph1lou.werewolfplugin.commands.roles.villager.inquisitor;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.inquisitor.InquisitorEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;

import fr.ph1lou.werewolfplugin.roles.villagers.Inquisitor;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.inquisitor.command", roleKeys = {"werewolf.roles.inquisitor.display"},
        argNumbers = 1,
        requiredPower = true,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE, StatePlayer.JUDGEMENT })))
public class CommandInquisitor implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Inquisitor inquisitor = (Inquisitor) playerWW.getRole();

        IPlayerWW targetWW = ctx.player(0);

        inquisitor.addAffectedPlayer(targetWW);
        inquisitor.setPower(false);

        InquisitorEvent inquisitorEvent = new InquisitorEvent(playerWW, targetWW);

        Bukkit.getPluginManager().callEvent(inquisitorEvent);

        if(inquisitorEvent.isCancelled()){
            playerWW.sendMessageWithKey(Prefix.ORANGE, "werewolf.check.cancel");
            return;
        }

        if (targetWW.getRole().isWereWolf()) {
            targetWW.getRole().disableAbilities();

            targetWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.inquisitor.smite_disable");
            playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.inquisitor.smite_success");
        } else {
            inquisitor.disableAbilities();
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.inquisitor.smite_fail");
        }
    }
}
