package fr.ph1lou.werewolfplugin.commands.roles.villager.citizen;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.buildings.IVoteOffice;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.roles.villagers.Citizen;

@RoleCommand(key = "werewolf.roles.citizen.annuler_command",
        roleKeys = RoleBase.CITIZEN,
        argNumbers = 0)
public class CommandCitizenAnnuler implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Citizen citizen = (Citizen) playerWW.getRole();
        int session = citizen.getCurrentUrnSession();

        if (session <= 0) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.citizen.urn_not_active");
            return;
        }

        if (!citizen.canCancelSession(session)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.citizen.urn_already_canceled");
            return;
        }

        IVoteOffice office = citizen.findNearestOffice();
        if (office == null) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.citizen.urn_not_near");
            return;
        }

        citizen.cancelVotes(office, session);
    }
}
