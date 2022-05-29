package fr.ph1lou.werewolfplugin.commands.roles.villager.info.detective;

import com.google.common.collect.Sets;
import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.detective.InvestigateEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "werewolf.roles.detective.command",
        roleKeys = RoleBase.DETECTIVE,
        requiredPower = true,
        argNumbers = 2
)
public class CommandDetective implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        UUID uuid = playerWW.getUUID();

        IRole detective = playerWW.getRole();

        if (args.length != 2) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.parameters",
                    Formatter.number(2));
            return;
        }

        if (args[0].equalsIgnoreCase(args[1])) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.two_distinct_player");
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

            if (((IAffectedPlayers) detective).getAffectedPlayers().contains(playerWW1)) {
                playerWW.sendMessageWithKey(Prefix.RED , "werewolf.roles.detective.already_inspect");
                return;
            }
        }

        Player player1 = Bukkit.getPlayer(args[0]);
        Player player2 = Bukkit.getPlayer(args[1]);

        if (player1 == null || player2 == null) return;

        UUID uuid1 = player1.getUniqueId();
        UUID uuid2 = player2.getUniqueId();

        IPlayerWW playerWW1 = game.getPlayerWW(uuid1).orElse(null);
        IPlayerWW playerWW2 = game.getPlayerWW(uuid2).orElse(null);

        if (playerWW1 == null || playerWW2 == null) return;

        String isLG1 = playerWW1.getRole().getDisplayCamp();
        String isLG2 = playerWW2.getRole().getDisplayCamp();

        ((IPower) detective).setPower(false);

        InvestigateEvent event = new InvestigateEvent(playerWW, Sets.newHashSet(playerWW1, playerWW2), isLG1.equals(isLG2));
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) detective).addAffectedPlayer(playerWW1);
        ((IAffectedPlayers) detective).addAffectedPlayer(playerWW2);

        if (event.isSameCamp()) {
            playerWW.sendMessageWithKey(Prefix.YELLOW , "werewolf.roles.detective.same_camp",
                    Formatter.format("&player1&",player1.getName()),
                    Formatter.format("&player2&",player2.getName()));
        } else
            playerWW.sendMessageWithKey(Prefix.YELLOW , "werewolf.roles.detective.opposing_camp",
                    Formatter.format("&player1&",player1.getName()),
                    Formatter.format("&player2&",player2.getName()));
    }
}
