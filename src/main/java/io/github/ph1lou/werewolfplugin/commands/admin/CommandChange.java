package io.github.ph1lou.werewolfplugin.commands.admin;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChange implements Commands {


    private final Main main;

    public CommandChange(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();

        if (!sender.hasPermission("a.change.use") && !game.getModerationManager().getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if (!game.isState(StateLG.LOBBY)) {
            game.translate("werewolf.check.game_in_progress");
            return;
        }

        sender.sendMessage(game.translate("werewolf.commands.admin.change.in_progress"));
        if (game.getMapManager().getWft() != null) {
            game.getMapManager().getWft().stop();
            game.getMapManager().setWft(null);
        }
        game.getMapManager().deleteMap();
        game.getMapManager().createMap();
        sender.sendMessage(game.translate("werewolf.commands.admin.change.finished"));
    }
}
