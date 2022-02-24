package fr.ph1lou.werewolfplugin.commands.roles.villager.devoted_servant;

import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandDevotedServant implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole devotedServant = playerWW.getRole();

        if(Bukkit.getPlayer(UUID.fromString(args[0]))==null){
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = UUID.fromString(args[0]);
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (argUUID.equals(uuid)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.not_yourself");
            return;
        }

        if (playerWW1 == null) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.player_not_found");
            return;
        }

        if (!playerWW1.isState(StatePlayer.JUDGEMENT)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.not_in_judgement");
            return;
        }

        if(!((IAffectedPlayers) devotedServant).getAffectedPlayers().isEmpty()){
            return;
        }
        ((IAffectedPlayers) devotedServant).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.LIGHT_BLUE.getKey(),"werewolf.role.devoted_servant.perform",
                Formatter.player(playerWW1.getName()));

    }
}
