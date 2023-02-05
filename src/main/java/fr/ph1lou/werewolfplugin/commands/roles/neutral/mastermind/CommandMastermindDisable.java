package fr.ph1lou.werewolfplugin.commands.roles.neutral.mastermind;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.mastermind.MastermindEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.neutrals.Mastermind;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.mastermind.command_disable",
        roleKeys = RoleBase.MASTERMIND,
        argNumbers = 1)
public class CommandMastermindDisable implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {


        Mastermind mastermind = (Mastermind) playerWW.getRole();

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

        if (!mastermind.getAffectedPlayers().contains(targetWW)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.mastermind.not_guessed", Formatter.format("&player&", targetWW.getName()));
            return;
        }

        MastermindEvent mastermindEvent = new MastermindEvent(playerWW, targetWW);

        Bukkit.getPluginManager().callEvent(mastermindEvent);

        if(mastermindEvent.isCancelled()){
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        playerWW.removePlayerMaxHealth(2);
        targetWW.getRole().disableAbilities();
        playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.mastermind.disable_perform",
                Formatter.format("&player&", targetWW.getName()));
        targetWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.mastermind.disable_target");
    }
}
