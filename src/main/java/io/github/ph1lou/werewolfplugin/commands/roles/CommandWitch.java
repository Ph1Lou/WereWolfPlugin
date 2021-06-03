package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.witch.WitchResurrectionEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWitch implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole witch = playerWW.getRole();


        if (Bukkit.getPlayer(UUID.fromString(args[0])) == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }
        UUID argUUID = UUID.fromString(args[0]);
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (!game.getConfig().isWitchAutoResurrection() && argUUID.equals(uuid)) {
            playerWW.sendMessageWithKey("werewolf.check.not_yourself");
            return;
        }

        if (!playerWW1.isState(StatePlayer.JUDGEMENT)) {
            playerWW.sendMessageWithKey("werewolf.check.not_in_judgement");
            return;
        }

        if (game.getTimer() - playerWW1.getDeathTime() < 7) {
            return;
        }

        ((IPower) witch).setPower(false);
        WitchResurrectionEvent witchResurrectionEvent = new WitchResurrectionEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(witchResurrectionEvent);

        if (witchResurrectionEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) witch).addAffectedPlayer(playerWW1);
        game.resurrection(playerWW1);
        playerWW.sendMessageWithKey("werewolf.role.witch.resuscitation_perform",
                playerWW1.getName());
    }
}
