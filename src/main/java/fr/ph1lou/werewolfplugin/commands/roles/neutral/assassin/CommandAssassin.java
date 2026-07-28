package fr.ph1lou.werewolfplugin.commands.roles.neutral.assassin;

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
import fr.ph1lou.werewolfplugin.roles.neutrals.Assassin;

import java.util.Optional;

@RoleCommand(key = "werewolf.roles.assassin.command",
        roleKeys = RoleBase.ASSASSIN,
        requiredLimitedUse = 2,
        autoCompletion = false,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, byUuid = true, excludeSelf = true,
                states = { StatePlayer.JUDGEMENT },
                stateMessageKey = "werewolf.check.not_in_judgement")))
public class CommandAssassin implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Assassin assassin = (Assassin) playerWW.getRole();
        IPlayerWW victim = ctx.player(0);

        Optional<IPlayerWW> killerWW = victim.getLastKiller();

        if (!killerWW.isPresent() ||
                !killerWW.get().equals(playerWW) ||
                game.getTimer() - victim.getDeathTime() > 7) {

            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.assassin.player_cannot_be_assassinated");
            return;
        }

        assassin.assassinate(victim);
    }
}
