package fr.ph1lou.werewolfplugin.commands.roles.neutral.charmer;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.charmer.CharmerEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.lovers.FakeLoverCharmer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;

@RoleCommand(key = "werewolf.roles.charmer.command",
        roleKeys = RoleBase.CHARMER,
        requiredPower = true,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true)))
public class CommandCharmer implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole charmer = playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);

        ((IPower) charmer).setPower(false);

        CharmerEvent charmerEvent = new CharmerEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(charmerEvent);

        if (charmerEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) charmer).clearAffectedPlayer();
        ((IAffectedPlayers) charmer).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.charmer.perform",
                Formatter.player(playerWW1.getName()));

        game.getLoversManager().addLover(new FakeLoverCharmer(game, new ArrayList<>(Arrays.asList(playerWW, playerWW1)), playerWW));
    }
}
