package fr.ph1lou.werewolfplugin.commands.roles.werewolf;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.random_events.NightAssembly;

@RoleCommand(key = "werewolf.random_events.night_assembly.command",
        roleKeys = {},
        statesPlayer = StatePlayer.ALIVE,
        statesGame = StateGame.GAME,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true)))
public class CommandAssembly implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        if (!playerWW.getRole().isWereWolf()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.random_events.night_assembly.not_active");
            return;
        }

        ListenerWerewolf listener = game.getListenersManager()
                .getRandomEvent(EventBase.NIGHT_ASSEMBLY).orElse(null);

        if (!(listener instanceof NightAssembly)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.random_events.night_assembly.not_active");
            return;
        }

        NightAssembly assembly = (NightAssembly) listener;

        if (!assembly.isAssemblyActive()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.random_events.night_assembly.not_active");
            return;
        }

        IPlayerWW target = ctx.player(0);
        assembly.vote(playerWW, target);
        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.random_events.night_assembly.vote_perform",
                Formatter.player(target.getName()));
    }
}
