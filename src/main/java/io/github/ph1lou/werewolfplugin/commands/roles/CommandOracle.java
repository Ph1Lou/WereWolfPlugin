package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.oracle.OracleEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandOracle implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole oracle = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        Aura aura = playerWW1.getRole().getAura();

        OracleEvent oracleEvent = new OracleEvent(playerWW, playerWW1, aura);
        ((IPower) oracle).setPower(false);
        Bukkit.getPluginManager().callEvent(oracleEvent);

        if (oracleEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) oracle).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey("werewolf.role.oracle.message", playerWW1.getName(),
                aura.getChatColor() + game.translate(aura.getKey()));
    }
}
