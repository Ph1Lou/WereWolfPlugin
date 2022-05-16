package fr.ph1lou.werewolfplugin.commands.roles.neutral.thug;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.roles.thug.ThugEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.role.thug.command",
        roleKeys = RoleBase.THUG,
        argNumbers = 1,
        requiredPower = true)
public class CommandThug implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        IRole thug = playerWW.getRole();

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

        if (((IAffectedPlayers) thug).getAffectedPlayers().contains(playerWW1)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.already_get_power");
            return;
        }


        ((IPower) thug).setPower(false);

        ThugEvent thugEvent = new ThugEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(thugEvent);

        if (thugEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) thug).clearAffectedPlayer();
        ((IAffectedPlayers) thug).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.YELLOW,"werewolf.role.thug.perform",Formatter.player(playerWW1.getName()));

        playerWW1.sendMessageWithKey(Prefix.RED,"werewolf.role.thug.alert");

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW1));
    }
}
