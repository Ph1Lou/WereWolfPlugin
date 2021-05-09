package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.priestess.PriestessEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandPriestess implements ICommands {

    private final Main main;

    public CommandPriestess(Main main) {
        this.main = main;
    }


    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        IRole priestess = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (!player.getWorld().equals(playerArg.getWorld()) || player.getLocation().distance(playerArg.getLocation()) > game.getConfig().getDistancePriestess()) {
            playerWW.sendMessageWithKey("werewolf.role.priestess.distance");
            return;
        }

        if (player.getHealth() < 5) {
            playerWW.sendMessageWithKey("werewolf.role.seer.not_enough_life");
        } else {
            IRole role1 = playerWW1.getRole();

            PriestessEvent priestessEvent = new PriestessEvent(playerWW, playerWW1, role1.getDisplayCamp());
            ((IPower) priestess).setPower(false);
            Bukkit.getPluginManager().callEvent(priestessEvent);

            if (priestessEvent.isCancelled()) {
                playerWW.sendMessageWithKey("werewolf.check.cancel");
                return;
            }

            ((IAffectedPlayers) priestess).addAffectedPlayer(playerWW1);

            playerWW.removePlayerMaxHealth(4);

            playerWW.sendMessageWithKey("werewolf.role.priestess.message", playerArg.getName(), game.translate(priestessEvent.getCamp()));

        }
    }
}
