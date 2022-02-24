package fr.ph1lou.werewolfplugin.commands.roles.neutral.barbarian;

import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.barbarian.BarbarianEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandBarbarian implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole barbarian = playerWW.getRole();

        UUID uuid1;

        try{
             uuid1 = UUID.fromString(args[0]);
        }
        catch (Exception ignored){
            return;
        }

        IPlayerWW playerWW1 = game.getPlayerWW(uuid1).orElse(null);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.JUDGEMENT)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.player_not_found");
            return;
        }

        ((IPower) barbarian).setPower(false);

        BarbarianEvent barbarianEvent = new BarbarianEvent(playerWW, playerWW1);

        Bukkit.getPluginManager().callEvent(barbarianEvent);

        if (barbarianEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) barbarian).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.YELLOW.getKey() ,
                "werewolf.role.barbarian.perform",
                Formatter.player(playerWW1.getName()));
    }
}
