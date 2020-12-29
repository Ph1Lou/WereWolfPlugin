package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.PriestessEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Display;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandPriestess implements Commands {

    private final Main main;

    public CommandPriestess(Main main) {
        this.main = main;
    }


    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles priestess = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        PlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (!player.getWorld().equals(playerArg.getWorld()) || player.getLocation().distance(playerArg.getLocation()) > game.getConfig().getDistancePriestess()) {
            player.sendMessage(game.translate("werewolf.role.priestess.distance"));
            return;
        }

        if (player.getHealth() < 5) {
            player.sendMessage(game.translate("werewolf.role.seer.not_enough_life"));
        } else {
            Roles role1 = playerWW1.getRole();

            String message = "werewolf.role.priestess.is_not_werewolf";

            if ((role1 instanceof Display && ((Display) role1).isDisplayCamp(Camp.WEREWOLF)) || (!(role1 instanceof Display) && role1.isWereWolf())) {
                message = "werewolf.role.priestess.is_werewolf";
            }

            PriestessEvent priestessEvent = new PriestessEvent(playerWW, playerWW1, message);
            ((Power) priestess).setPower(false);
            Bukkit.getPluginManager().callEvent(priestessEvent);

            if (priestessEvent.isCancelled()) {
                player.sendMessage(game.translate("werewolf.check.cancel"));
                return;
            }

            ((AffectedPlayers) priestess).addAffectedPlayer(playerWW1);

            playerWW.removePlayerMaxHealth(4);

            player.sendMessage(game.translate(priestessEvent.getCamp(), playerArg.getName()));

        }
    }
}
