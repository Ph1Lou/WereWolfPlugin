package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.SisterSeeRoleEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandSisterSeeRole implements Commands {


    private final Main main;

    public CommandSisterSeeRole(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);
        PlayerWW killerWW = args[0].equals("pve") ? null : game.getPlayerWW(UUID.fromString(args[0]));
        if (playerWW == null) return;

        AffectedPlayers affectedPlayers = (AffectedPlayers) playerWW.getRole();

        if (!affectedPlayers.getAffectedPlayers().contains(killerWW)) {
            player.sendMessage(game.translate("werewolf.role.sister.already"));
            return;
        }

        affectedPlayers.removeAffectedPlayer(killerWW);

        SisterSeeRoleEvent sisterSeeRoleEvent = new SisterSeeRoleEvent(playerWW, killerWW);

        if (sisterSeeRoleEvent.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }


        player.sendMessage(game.translate("werewolf.role.sister.reveal_killer_role",
                killerWW != null ?
                        game.translate(killerWW.getRole().getKey()) :
                        game.translate("werewolf.utils.pve")));
    }
}
