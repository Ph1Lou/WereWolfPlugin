package fr.ph1lou.werewolfplugin.commands.roles.villager.troublemaker;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.trouble_maker.TroubleMakerEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.troublemaker.command",
        roleKeys = RoleBase.TROUBLEMAKER,
        requiredPower = true,
        argNumbers = 1)
public class CommandTroubleMaker implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        IRole troublemaker = playerWW.getRole();
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

        TroubleMakerEvent troubleMakerEvent = new TroubleMakerEvent(playerWW, playerWW1);
        ((IPower) troublemaker).setPower(false);
        Bukkit.getPluginManager().callEvent(troubleMakerEvent);

        if (troubleMakerEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) troublemaker).addAffectedPlayer(playerWW1);

        playerWW1.sendMessageWithKey(Prefix.YELLOW , "werewolf.roles.troublemaker.get_switch");
        game.getMapManager().transportation(playerWW1, Math.random() * 2 * Math.PI);
        playerWW.sendMessageWithKey(Prefix.YELLOW , "werewolf.roles.troublemaker.troublemaker_perform",
                Formatter.player(playerArg.getName()));
    }
}
