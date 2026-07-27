package fr.ph1lou.werewolfplugin.commands.roles.villager.vaudouiste;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.villagers.Vaudouiste;

@RoleCommand(key = "werewolf.roles.vaudouiste.sacrifice_command",
        roleKeys = RoleBase.VAUDOUISTE,
        argNumbers = 0,
        statesPlayer = StatePlayer.ALIVE)
public class CommandSacrifice implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole role = playerWW.getRole();
        if (!(role instanceof Vaudouiste)) {
            return;
        }

        ((Vaudouiste) role).sacrifice();
    }
}
