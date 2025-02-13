package fr.ph1lou.werewolfplugin.commands.roles.werewolf;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfCanHowlingEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfHowlingEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@RoleCommand(key = "werewolf.configurations.werewolf_howling.command",
        roleKeys = {},
        statesPlayer = StatePlayer.ALIVE,
        statesGame = StateGame.GAME,
        argNumbers = {})
public class CommandWereWolfHowling implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        WereWolfCanHowlingEvent wereWolfCanHowlingEvent = new WereWolfCanHowlingEvent(playerWW);

        Bukkit.getPluginManager().callEvent(wereWolfCanHowlingEvent);

        if (!wereWolfCanHowlingEvent.isCanHowling()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.werewolf_howling.not_allowed");
            return;
        }


        Location location = playerWW.getLocation();

        game.getAlivePlayersWW()
                .stream().filter(playerWW1 -> !playerWW1.equals(playerWW))
                .forEach(playerWW1 -> Bukkit.getPluginManager().callEvent(new WereWolfHowlingEvent(playerWW, playerWW1, location)));

        int numberOfNearWolves = (int) game.getAlivePlayersWW()
                .stream()
                .filter(playerWW1 -> !playerWW1.equals(playerWW))
                .filter(playerWW1 -> playerWW1.getRole().isWereWolf())
                .filter(playerWW1 -> playerWW1.distance(playerWW) < 50)
                .count();

        playerWW.sendMessageWithKey(Prefix.GREEN,
                "werewolf.configurations.werewolf_howling.message",
                Formatter.number(numberOfNearWolves));

    }
}
