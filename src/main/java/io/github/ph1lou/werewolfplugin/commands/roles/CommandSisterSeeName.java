package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.roles.sister.SisterSeeNameEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandSisterSeeName implements ICommands {


    private final Main main;

    public CommandSisterSeeName(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);
        IPlayerWW killerWW = args[0].equals("pve") ? null : game.getPlayerWW(UUID.fromString(args[0]));
        if (playerWW == null) return;

        IAffectedPlayers affectedPlayers = (IAffectedPlayers) playerWW.getRole();

        if (!affectedPlayers.getAffectedPlayers().contains(killerWW)) {
            playerWW.sendMessageWithKey("werewolf.role.sister.already");
            return;
        }

        affectedPlayers.removeAffectedPlayer(killerWW);

        SisterSeeNameEvent sisterSeeNameEvent = new SisterSeeNameEvent(playerWW, killerWW);

        if (sisterSeeNameEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }


        playerWW.sendMessageWithKey("werewolf.role.sister.reveal_killer_name",
                killerWW != null ?
                        killerWW.getName() :
                        game.translate("werewolf.utils.pve"));
    }
}
