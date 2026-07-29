package fr.ph1lou.werewolfplugin.commands.roles.neutral.pyromane;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.buildings.IBuilding;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.roles.neutrals.Pyromane;

@RoleCommand(key = "werewolf.roles.pyromane.burn_command",
        roleKeys = RoleBase.PYROMANE,
        argNumbers = 0)
public class CommandPyromaneBurn implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {
        Pyromane pyromane = (Pyromane) playerWW.getRole();

        if (!pyromane.canBurn()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.pyromane.burn_used");
            return;
        }

        IBuilding building = pyromane.findNearestBuilding();
        if (building == null) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.pyromane.not_in_building");
            return;
        }

        pyromane.burnBuilding(building);
    }
}
