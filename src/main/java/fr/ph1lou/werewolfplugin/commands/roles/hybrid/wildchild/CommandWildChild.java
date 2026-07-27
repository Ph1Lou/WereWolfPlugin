package fr.ph1lou.werewolfplugin.commands.roles.hybrid.wildchild;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.wild_child.ModelEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.wild_child.command",
        roleKeys = RoleBase.WILD_CHILD,
        argNumbers = 1,
        requiredPower = true,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true)))
public class CommandWildChild implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole wildChild = playerWW.getRole();
        IPlayerWW playerWW1 = ctx.player(0);

        ((IAffectedPlayers) wildChild).addAffectedPlayer(playerWW1);
        ((IPower) wildChild).setPower(false);
        Bukkit.getPluginManager().callEvent(new ModelEvent(playerWW, playerWW1));
        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.wild_child.reveal_model",
                Formatter.player(playerWW1.getName()));
    }
}
