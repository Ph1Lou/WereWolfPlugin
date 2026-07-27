package fr.ph1lou.werewolfplugin.commands.roles.villager.devoted_servant;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;

@RoleCommand(key = "werewolf.roles.devoted_servant.command",
        roleKeys = RoleBase.DEVOTED_SERVANT,
        requiredPower = true,
        autoCompletion = false,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, byUuid = true, excludeSelf = true,
                states = { StatePlayer.JUDGEMENT },
                stateMessageKey = "werewolf.check.not_in_judgement")))
public class CommandDevotedServant implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole devotedServant = playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);

        if (!((IAffectedPlayers) devotedServant).getAffectedPlayers().isEmpty()) {
            return;
        }
        ((IAffectedPlayers) devotedServant).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.LIGHT_BLUE, "werewolf.roles.devoted_servant.perform",
                Formatter.player(playerWW1.getName()));

    }
}
