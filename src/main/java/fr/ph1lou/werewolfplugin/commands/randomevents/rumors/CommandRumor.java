package fr.ph1lou.werewolfplugin.commands.randomevents.rumors;

import fr.ph1lou.werewolfapi.annotations.Command;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.random_events.RumorsWriteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.RegisterManager;
import fr.ph1lou.werewolfplugin.random_events.Rumors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command(key = "werewolf.random_events.rumors.command", descriptionKey = "",
        stateGame = StateGame.GAME,
        statePlayer = StatePlayer.ALIVE,
        autoCompletion = false)
public class CommandRumor implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) {
            return;
        }

        if (args.length == 0) {
            player.sendMessage(game.translate(Prefix.RED , "werewolf.check.parameters", Formatter.number(1)));
            return;
        }

        if(!RegisterManager.get().getRandomEventsRegister()
                        .stream()
                .filter(randomEventRegister -> randomEventRegister.getKey().equals(EventBase.RUMORS))
                .findFirst()
                .map(randomEventRegister -> ((Rumors)randomEventRegister.getRandomEvent()).isActive())
                .orElse(false)){
            playerWW.sendMessageWithKey(Prefix.RED,"werewolf.random_events.rumors.no_rumor");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (String w : args) {
            sb.append(w).append(" ");
        }

        RumorsWriteEvent event = new RumorsWriteEvent(playerWW, sb.toString());
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()){
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.random_events.rumors.perform");
    }
}
