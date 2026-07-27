package fr.ph1lou.werewolfplugin.commands.roles.werewolf.amnesic;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.werewolfs.AmnesicWerewolf;

@RoleCommand(key = "werewolf.roles.amnesiac_werewolf.howl_command",
        roleKeys = RoleBase.AMNESIAC_WEREWOLF,
        argNumbers = 0,
        statesPlayer = StatePlayer.ALIVE,
        requiredPower = true)
public class CommandAmnesicHowl implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {
        IRole role = playerWW.getRole();
        if (!(role instanceof AmnesicWerewolf)) {
            return;
        }
        ((AmnesicWerewolf) role).howl();
    }
}
