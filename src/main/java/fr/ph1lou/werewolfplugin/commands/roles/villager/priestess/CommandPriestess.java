package fr.ph1lou.werewolfplugin.commands.roles.villager.priestess;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.priestess.PriestessEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.priestess.command",
        roleKeys = RoleBase.PRIESTESS,
        requiredPower = true,
        argNumbers = 1)
public class CommandPriestess implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        IRole priestess = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.player_not_found");
            return;
        }

        if (playerWW.getLocation().getWorld() != playerArg.getWorld() ||
                playerWW.getLocation().distance(playerArg.getLocation()) >
                        game.getConfig().getValue(IntValueBase.PRIESTESS_DISTANCE)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.priestess.distance",
                    Formatter.number(game.getConfig().getValue(IntValueBase.PRIESTESS_DISTANCE)));
            return;
        }

        if (playerWW.getHealth() < 5) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.seer.not_enough_life");
        } else {
            IRole role1 = playerWW1.getRole();

            PriestessEvent priestessEvent = new PriestessEvent(playerWW, playerWW1, role1.getDisplayCamp());
            ((IPower) priestess).setPower(false);
            Bukkit.getPluginManager().callEvent(priestessEvent);

            if (priestessEvent.isCancelled()) {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                return;
            }

            ((IAffectedPlayers) priestess).addAffectedPlayer(playerWW1);

            playerWW.removePlayerMaxHealth(4);

            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.priestess.message",
                    Formatter.player(playerArg.getName()),
                    Formatter.format("&camp&", game.translate(priestessEvent.getCamp())));

        }
    }
}
