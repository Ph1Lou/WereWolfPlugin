package fr.ph1lou.werewolfplugin.commands.roles.villager.inquisitor;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.inquisitor.InquisitorEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;

import fr.ph1lou.werewolfplugin.roles.villagers.Inquisitor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.inquisitor.command", roleKeys = {"werewolf.roles.inquisitor.display"},
        argNumbers = 1,
        requiredPower = true)
public class CommandInquisitor implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        Inquisitor inquisitor = (Inquisitor) playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }

        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW targetWW = game.getPlayerWW(argUUID).orElse(null);

        if (targetWW == null || targetWW.isState(StatePlayer.DEATH)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        inquisitor.addAffectedPlayer(targetWW);
        inquisitor.setPower(false);

        InquisitorEvent inquisitorEvent = new InquisitorEvent(playerWW, targetWW);

        Bukkit.getPluginManager().callEvent(inquisitorEvent);

        if(inquisitorEvent.isCancelled()){
            playerWW.sendMessageWithKey(Prefix.ORANGE, "werewolf.check.cancel");
            return;
        }

        if (targetWW.getRole().isWereWolf()) {
            targetWW.getRole().disableAbilities();

            targetWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.inquisitor.smite_disable");
            playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.inquisitor.smite_success");
        } else {
            inquisitor.disableAbilities();
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.inquisitor.smite_fail");
        }
    }
}
