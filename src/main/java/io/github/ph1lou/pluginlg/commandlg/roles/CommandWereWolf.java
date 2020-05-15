package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWereWolf extends Commands {


    public CommandWereWolf(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }


        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if(!game.playerLG.containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerLG plg = game.playerLG.get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!plg.isState(State.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if (!game.roleManage.isWereWolf(plg)) {
            sender.sendMessage(game.translate("werewolf.role.werewolf.not_werewolf"));
            return;
        }

        if (game.config.getTimerValues().get(TimerLG.WEREWOLF_LIST) > 0) {
            sender.sendMessage(game.translate("werewolf.role.werewolf.list_not_revealed"));
            return;
        }

        StringBuilder list = new StringBuilder();

        for (UUID playerUUID : game.playerLG.keySet()) {

            PlayerLG lg = game.playerLG.get(playerUUID);

            if (lg.isState(State.ALIVE) && game.roleManage.isWereWolf(lg)) {
                list.append(lg.getName()).append(" ");
            }
        }
        player.sendMessage(game.translate("werewolf.role.werewolf.werewolf_list", list.toString()));
    }
}
