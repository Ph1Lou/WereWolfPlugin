package fr.ph1lou.werewolfplugin.commands.roles.neutral.pyromane;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.roles.neutrals.Pyromane;

@RoleCommand(key = "werewolf.roles.pyromane.ignite_command",
        roleKeys = RoleBase.PYROMANE,
        requiredPowerKey = Pyromane.POWER_IGNITE,
        argNumbers = 0)
public class CommandPyromaneIgnite implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {
        Pyromane pyromane = (Pyromane) playerWW.getRole();

        if (pyromane.getCoveredPlayers().isEmpty()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.pyromane.no_essence");
            return;
        }

        pyromane.ignite();
    }
}
