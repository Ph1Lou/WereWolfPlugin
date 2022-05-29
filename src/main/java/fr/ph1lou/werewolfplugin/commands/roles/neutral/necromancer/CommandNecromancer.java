package fr.ph1lou.werewolfplugin.commands.roles.neutral.necromancer;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;

@RoleCommand(key = "werewolf.roles.necromancer.command",
        roleKeys = RoleBase.NECROMANCER,
        requiredPower = true,
        argNumbers = 0)
public class CommandNecromancer implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        playerWW.sendMessageWithKey(Prefix.YELLOW,"werewolf.roles.necromancer.use");

        ((IPower)playerWW.getRole()).setPower(false);
    }
}
