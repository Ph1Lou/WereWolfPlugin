package fr.ph1lou.werewolfplugin.commands.roles.werewolf;

import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.RandomEvent;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.TimerBase;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.events.werewolf.RequestSeeWereWolfListEvent;
import fr.ph1lou.werewolfplugin.RegisterManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWereWolf implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST.getKey()) > 0) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.werewolf.list_not_revealed");
            return;
        }

        RequestSeeWereWolfListEvent requestSeeWereWolfListEvent = new RequestSeeWereWolfListEvent(uuid);
        Bukkit.getPluginManager().callEvent(requestSeeWereWolfListEvent);

        if (!requestSeeWereWolfListEvent.isAccept()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.werewolf.not_werewolf");
            return;
        }

        StringBuilder list = new StringBuilder();

        for (IPlayerWW playerWW1 : game.getPlayersWW()) {

            AppearInWereWolfListEvent appearInWereWolfListEvent =
                    new AppearInWereWolfListEvent(playerWW1.getUUID(), uuid);
            Bukkit.getPluginManager().callEvent(appearInWereWolfListEvent);

            if (playerWW1.isState(StatePlayer.ALIVE) && appearInWereWolfListEvent.isAppear()) {
                list.append(playerWW1.getName()).append(" ");
            }
        }
        playerWW.sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.werewolf.werewolf_list", Formatter.format("&list&",list.toString()));
        if (RegisterManager.get().getRandomEventsRegister().stream()
                .filter(randomEventRegister -> randomEventRegister.getKey().equals(RandomEvent.DRUNKEN_WEREWOLF.getKey()))
                .anyMatch(randomEventRegister -> randomEventRegister.getRandomEvent().isRegister())) {
            playerWW.sendMessageWithKey("werewolf.commands.admin.ww_chat.drunken");
        }

    }
}
