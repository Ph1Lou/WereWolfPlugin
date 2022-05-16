package fr.ph1lou.werewolfplugin.commands.roles.villager.cupid;

import com.google.common.collect.Sets;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.lovers.CupidLoversEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.role.cupid.command",
        roleKeys = RoleBase.CUPID,
        requiredPower = true,
        argNumbers = 2)
public class CommandCupid implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        UUID uuid = playerWW.getUUID();

        IRole cupid = playerWW.getRole();

        if (args[0].equalsIgnoreCase(args[1])) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.two_distinct_player");
            return;
        }

        if(game.getConfig().isConfigActive(ConfigBase.RANDOM_CUPID)){
            playerWW.sendMessageWithKey(Prefix.GREEN,"werewolf.role.cupid.random");
            return;
        }

        for(String p:args) {

            Player playerArg = Bukkit.getPlayer(p);

            if (playerArg == null) {
                playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.offline_player");
                return;
            }

            UUID uuid1 = playerArg.getUniqueId();
            IPlayerWW playerWW1 = game.getPlayerWW(uuid1).orElse(null);

            if (playerWW1 == null || playerWW1.isState(StatePlayer.DEATH)) {
                playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.player_not_found");
                return;
            }

            if (uuid.equals(uuid1)) {
                playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.not_yourself");
                return;
            }
        }


        for (String p : args) {
            Player playerArg = Bukkit.getPlayer(p);

            if (playerArg != null) {
                game.getPlayerWW(playerArg.getUniqueId()).ifPresent(((IAffectedPlayers) cupid)::addAffectedPlayer);
            }
        }
        ((IPower) cupid).setPower(false);
        Bukkit.getPluginManager().callEvent(new CupidLoversEvent(playerWW, Sets.newHashSet(((IAffectedPlayers) cupid).getAffectedPlayers())));
        playerWW.sendMessageWithKey(Prefix.YELLOW , "werewolf.role.cupid.designation_perform",
                Formatter.format("&player1&",args[0]),
                        Formatter.format("&player2&",args[1]));
    }
}
