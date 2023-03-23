package fr.ph1lou.werewolfplugin.commands.roles.werewolf.bloodthirsty;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.bloodthirsty_werewolf.BloodthirstyWerewolfHuntDownEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.werewolfs.BloodthirstyWereWolf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.bloodthirsty_werewolf.command",
        roleKeys = RoleBase.BLOODTHIRSTY_WEREWOLF,
        argNumbers = 1,
        requiredPower = true
)
public class CommandBloodthirsty implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {
        BloodthirstyWereWolf sanguinaryWolf = (BloodthirstyWereWolf) playerWW.getRole();

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null){
            playerWW.sendMessageWithKey(Prefix.RED,"werewolf.check.offline_player");
            return;
        }

        UUID playerUUID = player.getUniqueId();
        IPlayerWW selectedPlayerWW = game.getPlayerWW(playerUUID).orElse(null);

        if (selectedPlayerWW == null | !(selectedPlayerWW.isState(StatePlayer.ALIVE))){
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.player_not_found");
            return;
        }

        if(!playerWW.isState(StatePlayer.ALIVE)){
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.state_player");
            return;
        }

        if (selectedPlayerWW == sanguinaryWolf.getPlayerWW()){
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.not_yourself");
            return;
        }

        if (!sanguinaryWolf.hasPower()){
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
        }

        BloodthirstyWerewolfHuntDownEvent event = new BloodthirstyWerewolfHuntDownEvent(playerWW, selectedPlayerWW);

        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()){
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.bloodthirsty_werewolf.player_cant_be_traqued", Formatter.player(selectedPlayerWW.getName()));
            return;
        }

        sanguinaryWolf.addAffectedPlayer(selectedPlayerWW);
    }
}
