package fr.ph1lou.werewolfplugin.commands.roles.villager.croupier;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.roles.croupier.CroupierChooseEventEvent;
import fr.ph1lou.werewolfapi.events.roles.croupier.CroupierEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.roles.villagers.Croupier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@RoleCommand(key = "werewolf.roles.croupier.command",
        roleKeys = RoleBase.CROUPIER,
        argNumbers = 1,
        requiredPower = true)
public class CommandCroupier implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        Croupier croupier = (Croupier) playerWW.getRole();
        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
            return;
        }

        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW targetWW = game.getPlayerWW(argUUID).orElse(null);

        if (targetWW == null || !targetWW.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (targetWW.equals(playerWW)) {
            playerWW.sendMessageWithKey("werewolf.roles.croupier.yourself");
            return;
        }

        List<IPlayerWW> playerWWS = game.getPlayersWW()
                .stream()
                .filter(p -> p.isState(StatePlayer.ALIVE))
                .filter(iPlayerWW -> !iPlayerWW.equals(playerWW))
                .filter(iPlayerWW -> !iPlayerWW.equals(targetWW))
                .collect(Collectors.toList());

        if (playerWWS.size() < 4) {
            playerWW.sendMessageWithKey("werewolf.roles.croupier.not_enough_players");
            return;
        }

        if (croupier.getAffectedPlayers().contains(targetWW)) {
            playerWW.sendMessageWithKey("werewolf.roles.croupier.repeated_target");
            return;
        }

        ((IAffectedPlayers) croupier).addAffectedPlayer(targetWW);

        ((IPower) croupier).setPower(false);

        IPlayerWW playerRevealed = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

        CroupierChooseEventEvent croupierChooseEventEvent = new CroupierChooseEventEvent(playerWW, playerRevealed);

        Bukkit.getPluginManager().callEvent(croupierChooseEventEvent);

        if(croupierChooseEventEvent.isCancelled()){
            playerWW.sendMessageWithKey(Prefix.ORANGE, "werewolf.check.cancel");
            return;
        }

        List<IRole> role1List = playerWWS
                .stream()
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .filter(playerWW1 -> !playerRevealed.equals(playerWW1))
                .map(IPlayerWW::getRole)
                .filter(iRole -> !iRole.isKey(playerRevealed.getRole().getKey()))
                .filter(roles -> !roles.isCamp(playerRevealed.getRole().getCamp()))
                .collect(Collectors.toList());

        if (role1List.isEmpty()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.croupier.problem");
            return;
        }

        IRole role1 = role1List.get((int) Math.floor(game.getRandom().nextDouble() * role1List.size()));

        List<IRole> role2List = playerWWS
                .stream()
                .filter(playerWW1 -> !playerRevealed.equals(playerWW1))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.isKey(role1.getKey()))
                .filter(iRole -> !iRole.isKey(playerRevealed.getRole().getKey()))
                .collect(Collectors.toList());

        if (role2List.isEmpty()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.croupier.problem");
            return;
        }

        IRole role2 = role2List.get((int) Math.floor(game.getRandom().nextDouble() * role2List.size()));

        List<String> roles = new ArrayList<>(Arrays.asList(role1.getKey(),
                role2.getKey(),
                playerRevealed.getRole().getDisplayRole()));

        Collections.shuffle(roles, game.getRandom());

        CroupierEvent croupierEvent = new CroupierEvent(playerRevealed, new HashSet<>(Arrays.asList(playerRevealed, role1.getPlayerWW(), role2.getPlayerWW())));

        Bukkit.getPluginManager().callEvent(croupierEvent);

        targetWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.croupier.card", Formatter.format("&player&", playerRevealed.getName()),
                Formatter.format("&role1&", game.translate(roles.get(0))),
                Formatter.format("&role2&", game.translate(roles.get(1))),
                Formatter.format("&role3&", game.translate(roles.get(2))));

        playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.croupier.confirm");
    }
}
