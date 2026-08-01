package fr.ph1lou.werewolfplugin.commands.roles.villager.priestess;

import fr.ph1lou.werewolfapi.annotations.Params;
import fr.ph1lou.werewolfapi.annotations.PlayerArg;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.CommandContext;
import fr.ph1lou.werewolfapi.commands.IParameterizedCommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.priestess.PriestessEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.roles.priestess.command",
        roleKeys = RoleBase.PRIESTESS,
        requiredPower = true,
        argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = { StatePlayer.ALIVE })))
public class CommandPriestess implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {

        IRole priestess = playerWW.getRole();

        IPlayerWW playerWW1 = ctx.player(0);

        if (playerWW.distance(playerWW1) >
            game.getConfig().getValue(IntValueBase.PRIESTESS_DISTANCE)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.priestess.distance",
                    Formatter.number(game.getConfig().getValue(IntValueBase.PRIESTESS_DISTANCE)));
            return;
        }

        if (playerWW.getHealth() < 5) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.priestess.not_enough_life");
        } else {
            IRole role1 = playerWW1.getRole();

            PriestessEvent priestessEvent = new PriestessEvent(playerWW, playerWW1, role1.getDisplayCamp());
            Bukkit.getPluginManager().callEvent(priestessEvent);

            if (priestessEvent.isCancelled()) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                return;
            }

            ((IPower) priestess).setPower(false);
            ((IAffectedPlayers) priestess).addAffectedPlayer(playerWW1);

            playerWW.removePlayerMaxHealth(4);

            if (role1.isWereWolf()) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.priestess.message_werewolf",
                        Formatter.player(playerWW1.getName()),
                        Formatter.role(game.translate(role1.getKey())));
            } else {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.priestess.message_no_info",
                        Formatter.player(playerWW1.getName()));
            }

        }
    }
}
