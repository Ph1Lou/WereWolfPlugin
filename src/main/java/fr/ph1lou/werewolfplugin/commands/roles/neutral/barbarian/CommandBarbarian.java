package fr.ph1lou.werewolfplugin.commands.roles.neutral.barbarian;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.barbarian.BarbarianEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.barbarian.command",
        roleKeys = RoleBase.BARBARIAN,
        autoCompletion = false,
        requiredPower = true,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, byUuid = true,
                states = { StatePlayer.JUDGEMENT },
                stateMessageKey = "werewolf.check.player_not_found")))
public class CommandBarbarian implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole barbarian = playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);

        ((IPower) barbarian).setPower(false);

        BarbarianEvent barbarianEvent = new BarbarianEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(barbarianEvent);

        if (barbarianEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) barbarian).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.YELLOW,
                "werewolf.roles.barbarian.perform",
                Formatter.player(playerWW1.getName()));
    }
}
