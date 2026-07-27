package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.annotations.IntArg;
import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.set_group.command",
        descriptionKey = "werewolf.commands.admin.set_group.description",
        moderatorAccess = true,
        argNumbers = 1,
        statesGame = StateGame.GAME,
        args = @Params(integers = @IntArg(index = 0)))
public class CommandSetGroup implements IParameterizedCommand {

    @Override
    public void execute(WereWolfAPI game, Player player, CommandContext ctx) {

        game.setGroup(ctx.intValue(0));
        player.performCommand(String.format("a %s", game.translate("werewolf.commands.admin.group.command")));
    }
}
