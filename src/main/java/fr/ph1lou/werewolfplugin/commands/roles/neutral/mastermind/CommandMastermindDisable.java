package fr.ph1lou.werewolfplugin.commands.roles.neutral.mastermind;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.mastermind.MastermindEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.neutrals.Mastermind;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.mastermind.command_disable",
        roleKeys = RoleBase.MASTERMIND,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE, StatePlayer.JUDGEMENT })))
public class CommandMastermindDisable implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Mastermind mastermind = (Mastermind) playerWW.getRole();

        IPlayerWW targetWW = ctx.player(0);

        if (!mastermind.getAffectedPlayers().contains(targetWW)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.mastermind.not_guessed", Formatter.format("&player&", targetWW.getName()));
            return;
        }

        MastermindEvent mastermindEvent = new MastermindEvent(playerWW, targetWW);

        Bukkit.getPluginManager().callEvent(mastermindEvent);

        if(mastermindEvent.isCancelled()){
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        playerWW.removePlayerMaxHealth(2);
        targetWW.getRole().disableAbilities();
        playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.mastermind.disable_perform",
                Formatter.format("&player&", targetWW.getName()));
        targetWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.mastermind.disable_target");
    }
}
