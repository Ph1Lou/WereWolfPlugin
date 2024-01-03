package fr.ph1lou.werewolfplugin.commands.roles.werewolf;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.RequestSeeWereWolfListEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@PlayerCommand(key = "werewolf.roles.werewolf.command",
        descriptionKey = "",
        argNumbers = 0,
        statesGame = StateGame.GAME,
        statesPlayer = StatePlayer.ALIVE)
public class CommandWereWolf implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST) > 0) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.werewolf.list_not_revealed");
            return;
        }

        RequestSeeWereWolfListEvent requestSeeWereWolfListEvent = new RequestSeeWereWolfListEvent(playerWW);
        Bukkit.getPluginManager().callEvent(requestSeeWereWolfListEvent);

        if (!requestSeeWereWolfListEvent.isAccept()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.roles.werewolf.not_werewolf");
            return;
        }

        StringBuilder list = new StringBuilder();

        for (IPlayerWW playerWW1 : game.getPlayersWW()) {

            AppearInWereWolfListEvent appearInWereWolfListEvent = new AppearInWereWolfListEvent(playerWW, playerWW1);
            Bukkit.getPluginManager().callEvent(appearInWereWolfListEvent);

            if (playerWW1.isState(StatePlayer.ALIVE) && appearInWereWolfListEvent.isAppear()) {
                list.append(playerWW1.getName()).append(" ");
            }
        }
        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.werewolf.werewolf_list", Formatter.format("&list&", list.toString()));

        game.getListenersManager().getRandomEvent(EventBase.DRUNKEN_WEREWOLF)
                .ifPresent(listenerWerewolf -> {
                    if (listenerWerewolf.isRegister()) {
                        playerWW.sendMessageWithKey("werewolf.commands.player.ww_chat.drunken");
                    }
                });

    }
}
