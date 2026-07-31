package fr.ph1lou.werewolfplugin.commands.roles.villager.spy;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.spy.SpyChoseEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.villagers.Spy;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.spy.command",
        roleKeys = RoleBase.SPY,
        argNumbers = 1,
        requiredPower = true,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE },
                rejectIfAlreadyAffected = true)))
public class CommandSpy implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole spy = playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);

        Spy spyRole = (Spy) spy;
        if (playerWW1.equals(spyRole.getLastSpied())) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.spy.not_consecutive");
            return;
        }

        ((IPower) spy).setPower(false);

        SpyChoseEvent spyChoseEvent = new SpyChoseEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(spyChoseEvent);

        if (spyChoseEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) spy).clearAffectedPlayer();
        ((IAffectedPlayers) spy).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.spy.perform",
                Formatter.player(playerWW1.getName()));
    }
}
