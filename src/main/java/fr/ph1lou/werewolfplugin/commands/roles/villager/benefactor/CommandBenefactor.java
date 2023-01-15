package fr.ph1lou.werewolfplugin.commands.roles.villager.benefactor;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.benefactor.BenefactorGiveHeartEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RoleCommand(key = "werewolf.roles.benefactor.command",
        roleKeys = RoleBase.BENEFACTOR,
        argNumbers = 1)
public class CommandBenefactor implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        IRole benefactor = playerWW.getRole();

        if (!(benefactor instanceof IAffectedPlayers)) return;

        if (((IAffectedPlayers) benefactor).getAffectedPlayers().size() >= 3) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.benefactor.too_many_players");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.offline_player");
            return;
        }

        IPlayerWW targetWW = game.getPlayerWW(target.getUniqueId()).orElse(null);

        if (targetWW == null || !targetWW.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.player_not_found");
            return;
        }

        if (((IAffectedPlayers) benefactor).getAffectedPlayers().contains(targetWW)) {
            playerWW.sendMessageWithKey(Prefix.RED,
                    "werewolf.roles.benefactor.already_use_on_player");
            return;
        }

        if (playerWW.getUUID().equals(targetWW.getUUID())) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.not_yourself");
            return;
        }

        BenefactorGiveHeartEvent event = new BenefactorGiveHeartEvent(playerWW, targetWW);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) benefactor).addAffectedPlayer(targetWW);
        targetWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.benefactor.target_message");
        targetWW.addPlayerMaxHealth(2);
        playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.benefactor.perform", Formatter.player(targetWW.getName()));
    }
}