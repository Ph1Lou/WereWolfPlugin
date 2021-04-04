package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.RequestSeeWereWolfListEvent;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWereWolf implements ICommands {


    private final Main main;

    public CommandWereWolf(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        if (game.getConfig().getTimerValue(TimersBase.WEREWOLF_LIST.getKey()) > 0) {
            playerWW.sendMessageWithKey("werewolf.role.werewolf.list_not_revealed");
            return;
        }

        RequestSeeWereWolfListEvent requestSeeWereWolfListEvent = new RequestSeeWereWolfListEvent(uuid);
        Bukkit.getPluginManager().callEvent(requestSeeWereWolfListEvent);

        if (!requestSeeWereWolfListEvent.isAccept()) {
            playerWW.sendMessageWithKey("werewolf.role.werewolf.not_werewolf");
            return;
        }

        StringBuilder list = new StringBuilder();

        for (IPlayerWW playerWW1 : game.getPlayerWW()) {

            AppearInWereWolfListEvent appearInWereWolfListEvent =
                    new AppearInWereWolfListEvent(playerWW1.getUUID(), uuid);
            Bukkit.getPluginManager().callEvent(appearInWereWolfListEvent);

            if (playerWW1.isState(StatePlayer.ALIVE) && appearInWereWolfListEvent.isAppear()) {
                list.append(playerWW1.getName()).append(" ");
            }
        }
        playerWW.sendMessageWithKey("werewolf.role.werewolf.werewolf_list", list.toString());
    }
}
