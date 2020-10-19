package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.AppearInWereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.RequestSeeWereWolfListEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWereWolf implements Commands {


    private final Main main;

    public CommandWereWolf(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }


        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if(!game.getPlayersWW().containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerWW plg = game.getPlayersWW().get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!plg.isState(State.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        RequestSeeWereWolfListEvent requestSeeWereWolfListEvent = new RequestSeeWereWolfListEvent(uuid);
        Bukkit.getPluginManager().callEvent(requestSeeWereWolfListEvent);


        if (!requestSeeWereWolfListEvent.isAccept()) {
            sender.sendMessage(game.translate("werewolf.role.werewolf.not_werewolf"));
            return;
        }

        if (game.getConfig().getTimerValues().get("werewolf.menu.timers.werewolf_list") > 0) {
            sender.sendMessage(game.translate("werewolf.role.werewolf.list_not_revealed"));
            return;
        }

        StringBuilder list = new StringBuilder();

        for (UUID playerUUID : game.getPlayersWW().keySet()) {

            PlayerWW lg = game.getPlayersWW().get(playerUUID);
            AppearInWereWolfListEvent appearInWereWolfListEvent = new AppearInWereWolfListEvent(playerUUID);
            Bukkit.getPluginManager().callEvent(appearInWereWolfListEvent);

            if (lg.isState(State.ALIVE) && appearInWereWolfListEvent.isAppear()) {
                list.append(lg.getName()).append(" ");
            }
        }
        player.sendMessage(game.translate("werewolf.role.werewolf.werewolf_list", list.toString()));
    }
}
