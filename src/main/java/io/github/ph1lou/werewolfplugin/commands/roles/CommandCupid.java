package io.github.ph1lou.werewolfplugin.commands.roles;

import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.CupidLoversEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandCupid implements Commands {


    private final Main main;

    public CommandCupid(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles cupid = playerWW.getRole();

        if (args[0].equalsIgnoreCase(args[1])) {
            playerWW.sendMessageWithKey("werewolf.check.two_distinct_player");
            return;
        }

        for(String p:args) {

            Player playerArg = Bukkit.getPlayer(p);

            if (playerArg == null) {
                playerWW.sendMessageWithKey("werewolf.check.offline_player");
                return;
            }

            UUID uuid1 = playerArg.getUniqueId();
            PlayerWW playerWW1 = game.getPlayerWW(uuid1);

            if (playerWW1 == null || playerWW1.isState(StatePlayer.DEATH)) {
                playerWW.sendMessageWithKey("werewolf.check.player_not_found");
                return;
            }

            if (uuid.equals(uuid1)) {
                playerWW.sendMessageWithKey("werewolf.check.not_yourself");
                return;
            }
        }


        for(String p:args) {
            Player playerArg = Bukkit.getPlayer(p);

            if (playerArg != null) {
                PlayerWW playerWW1 = game.getPlayerWW(playerArg.getUniqueId());
                if (playerWW1 != null) {
                    ((AffectedPlayers) cupid).addAffectedPlayer(playerWW1);
                }
            }
        }
        ((Power) cupid).setPower(false);
        Bukkit.getPluginManager().callEvent(new CupidLoversEvent(playerWW, Sets.newHashSet(((AffectedPlayers) cupid).getAffectedPlayers())));
        playerWW.sendMessageWithKey("werewolf.role.cupid.designation_perform", args[0], args[1]);
    }
}
