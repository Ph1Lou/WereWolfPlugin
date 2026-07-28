package fr.ph1lou.werewolfplugin.commands.roles.villager.info.detective;

import fr.ph1lou.werewolfapi.annotations.ChoiceArg;
import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Detective;

@RoleCommand(key = "werewolf.roles.detective.command_guess",
        roleKeys = RoleBase.DETECTIVE,
        argNumbers = 1,
        args = @Params(choices = @ChoiceArg(index = 0, choices = { "bleu", "jaune" })))
public class CommandDetectiveGuess implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Detective detective = (Detective) playerWW.getRole();

        if (!detective.canGuess()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.detective.guess_unavailable");
            return;
        }

        String color = ctx.string(0);
        detective.clearGuess();

        if (detective.isSameColorCode(color)) {
            playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.detective.guess_correct",
                    Formatter.format("&code&", game.translate("werewolf.roles.detective.color_" + color)));
        } else {
            detective.setPowerLost(true);
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.detective.guess_wrong",
                    Formatter.format("&code&", game.translate("werewolf.roles.detective.color_" + color)));
        }
    }
}
