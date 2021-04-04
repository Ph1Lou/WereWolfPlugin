package io.github.ph1lou.werewolfplugin.commands.roles;

import com.google.common.collect.Sets;
import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.lovers.CupidLoversEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandCupid implements ICommands {


    private final Main main;

    public CommandCupid(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        IRole cupid = playerWW.getRole();

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
            IPlayerWW playerWW1 = game.getPlayerWW(uuid1);

            if (playerWW1 == null || playerWW1.isState(StatePlayer.DEATH)) {
                playerWW.sendMessageWithKey("werewolf.check.player_not_found");
                return;
            }

            if (uuid.equals(uuid1)) {
                playerWW.sendMessageWithKey("werewolf.check.not_yourself");
                return;
            }
        }


        for (String p : args) {
            Player playerArg = Bukkit.getPlayer(p);

            if (playerArg != null) {
                IPlayerWW playerWW1 = game.getPlayerWW(playerArg.getUniqueId());
                if (playerWW1 != null) {
                    ((IAffectedPlayers) cupid).addAffectedPlayer(playerWW1);
                }
            }
        }
        ((IPower) cupid).setPower(false);
        Bukkit.getPluginManager().callEvent(new CupidLoversEvent(playerWW, Sets.newHashSet(((IAffectedPlayers) cupid).getAffectedPlayers())));
        playerWW.sendMessageWithKey("werewolf.role.cupid.designation_perform", args[0], args[1]);
    }
}
