package fr.ph1lou.werewolfplugin.commands.roles.villager.priestess;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.priestess.PriestessEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.villagers.Priestess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.role.priestess.command",
        roleKeys = RoleBase.PRIESTESS,
        requiredPower = true,
        argNumbers = 1)
public class CommandPriestess implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole priestess = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.player_not_found");
            return;
        }

        if (!player.getWorld().equals(playerArg.getWorld()) ||
                player.getLocation().distance(playerArg.getLocation()) >
                        game.getConfig().getValue(RoleBase.PRIESTESS, Priestess.DISTANCE)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.role.priestess.distance",
                    Formatter.number(game.getConfig().getValue(RoleBase.PRIESTESS, Priestess.DISTANCE)));
            return;
        }

        if (player.getHealth() < 5) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.role.seer.not_enough_life");
        } else {
            IRole role1 = playerWW1.getRole();

            PriestessEvent priestessEvent = new PriestessEvent(playerWW, playerWW1, role1.getDisplayCamp());
            ((IPower) priestess).setPower(false);
            Bukkit.getPluginManager().callEvent(priestessEvent);

            if (priestessEvent.isCancelled()) {
                playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
                return;
            }

            ((IAffectedPlayers) priestess).addAffectedPlayer(playerWW1);

            playerWW.removePlayerMaxHealth(4);

            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.role.priestess.message",
                    Formatter.player(playerArg.getName()),
                    Formatter.format("&camp&",game.translate(priestessEvent.getCamp())));

        }
    }
}
