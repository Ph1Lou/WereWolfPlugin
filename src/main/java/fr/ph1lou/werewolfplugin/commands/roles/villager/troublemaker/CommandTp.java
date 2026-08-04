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
import fr.ph1lou.werewolfplugin.roles.villagers.Troublemaker;
import org.bukkit.Bukkit;

/**
 * {@code /lg tp [Pseudo]} : téléporte un joueur ciblé à une position aléatoire
 * de la carte. Disponible uniquement en mode solitaire, une fois par partie.
 */
@RoleCommand(key = "werewolf.roles.troublemaker.command_tp",
        roleKeys = RoleBase.TROUBLEMAKER,
        requiredPowerKey = Troublemaker.POWER_TP,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE }, excludeSelf = true)))
public class CommandTp implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        Troublemaker troublemaker = (Troublemaker) playerWW.getRole();

        if (!troublemaker.isSolitary()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.troublemaker.not_solo");
            return;
        }

        IPlayerWW playerWW1 = ctx.player(0);

        TroubleMakerEvent troubleMakerEvent = new TroubleMakerEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(troubleMakerEvent);

        if (troubleMakerEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        troublemaker.usePower(Troublemaker.POWER_TP);
        troublemaker.addAffectedPlayer(playerWW1);

        playerWW1.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.troublemaker.get_switch");
        game.getMapManager().transportation(playerWW1, Math.random() * 2 * Math.PI);
        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.troublemaker.troublemaker_perform",
                Formatter.player(playerWW1.getName()));
    }
}
