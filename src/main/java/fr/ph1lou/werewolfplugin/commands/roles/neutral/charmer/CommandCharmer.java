package fr.ph1lou.werewolfplugin.commands.roles.neutral.charmer;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.charmer.CharmerEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfplugin.roles.lovers.FakeLoverCharmer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@RoleCommand(key = "werewolf.roles.charmer.command",
        roleKeys = RoleBase.CHARMER,
        requiredPower = true,
        argNumbers = 1)
public class CommandCharmer implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        UUID uuid = playerWW.getUUID();

        IRole charmer = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (argUUID.equals(uuid)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.not_yourself");
            return;
        }

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.player_not_found");
            return;
        }

        ((IPower) charmer).setPower(false);

        CharmerEvent charmerEvent = new CharmerEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(charmerEvent);

        if (charmerEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) charmer).clearAffectedPlayer();
        ((IAffectedPlayers) charmer).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.YELLOW , "werewolf.roles.charmer.perform",
                Formatter.player(playerArg.getName()));

        game.getLoversManager().addLover(new FakeLoverCharmer(game,new ArrayList<>(Arrays.asList(playerWW,playerWW1)),playerWW));
    }
}
