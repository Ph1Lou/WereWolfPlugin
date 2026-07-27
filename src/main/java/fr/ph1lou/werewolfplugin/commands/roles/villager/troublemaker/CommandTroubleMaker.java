package fr.ph1lou.werewolfplugin.commands.roles.villager.troublemaker;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.trouble_maker.TroubleMakerEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.troublemaker.command",
        roleKeys = RoleBase.TROUBLEMAKER,
        requiredPower = true,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE })))
public class CommandTroubleMaker implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole troublemaker = playerWW.getRole();
        IPlayerWW playerWW1 = ctx.player(0);

        TroubleMakerEvent troubleMakerEvent = new TroubleMakerEvent(playerWW, playerWW1);
        ((IPower) troublemaker).setPower(false);
        Bukkit.getPluginManager().callEvent(troubleMakerEvent);

        if (troubleMakerEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) troublemaker).addAffectedPlayer(playerWW1);

        playerWW1.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.troublemaker.get_switch");
        game.getMapManager().transportation(playerWW1, Math.random() * 2 * Math.PI);
        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.troublemaker.troublemaker_perform",
                Formatter.player(playerWW1.getName()));
    }
}
