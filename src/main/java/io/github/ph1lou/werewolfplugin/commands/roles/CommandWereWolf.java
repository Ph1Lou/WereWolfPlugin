package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.enumlg.TimersBase;
import io.github.ph1lou.werewolfapi.events.AppearInWereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.RequestSeeWereWolfListEvent;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWereWolf implements Commands {


    private final Main main;

    public CommandWereWolf(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        if (game.getConfig().getTimerValues().get(TimersBase.WEREWOLF_LIST.getKey()) > 0) {
            player.sendMessage(game.translate("werewolf.role.werewolf.list_not_revealed"));
            return;
        }

        RequestSeeWereWolfListEvent requestSeeWereWolfListEvent = new RequestSeeWereWolfListEvent(uuid);
        Bukkit.getPluginManager().callEvent(requestSeeWereWolfListEvent);

        if (!requestSeeWereWolfListEvent.isAccept()) {
            player.sendMessage(game.translate("werewolf.role.werewolf.not_werewolf"));
            return;
        }

        StringBuilder list = new StringBuilder();

        for (PlayerWW playerWW1 : game.getPlayerWW()) {

            AppearInWereWolfListEvent appearInWereWolfListEvent =
                    new AppearInWereWolfListEvent(playerWW1.getUUID());
            Bukkit.getPluginManager().callEvent(appearInWereWolfListEvent);

            if (playerWW1.isState(StatePlayer.ALIVE) && appearInWereWolfListEvent.isAppear()) {
                list.append(playerWW1.getName()).append(" ");
            }
        }
        player.sendMessage(game.translate("werewolf.role.werewolf.werewolf_list", list.toString()));
    }
}
