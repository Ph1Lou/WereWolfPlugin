package fr.ph1lou.werewolfplugin.commands.roles.villager.devoted_servant;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;

import java.util.UUID;

@RoleCommand(key = "werewolf.role.devoted_servant.command",
        roleKeys = RoleBase.DEVOTED_SERVANT,
        requiredPower = true,
        autoCompletion = false,
        argNumbers = 1)
public class CommandDevotedServant implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        UUID uuid = playerWW.getUUID();

        IRole devotedServant = playerWW.getRole();

        if(Bukkit.getPlayer(UUID.fromString(args[0]))==null){
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = UUID.fromString(args[0]);
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (argUUID.equals(uuid)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.not_yourself");
            return;
        }

        if (playerWW1 == null) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.player_not_found");
            return;
        }

        if (!playerWW1.isState(StatePlayer.JUDGEMENT)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.not_in_judgement");
            return;
        }

        if(!((IAffectedPlayers) devotedServant).getAffectedPlayers().isEmpty()){
            return;
        }
        ((IAffectedPlayers) devotedServant).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.LIGHT_BLUE,"werewolf.role.devoted_servant.perform",
                Formatter.player(playerWW1.getName()));

    }
}
