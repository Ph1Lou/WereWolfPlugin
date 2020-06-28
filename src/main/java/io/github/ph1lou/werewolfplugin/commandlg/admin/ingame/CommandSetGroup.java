package io.github.ph1lou.werewolfplugin.commandlg.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetGroup implements Commands {


    private final Main main;

    public CommandSetGroup(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        GameManager game = main.currentGame;


        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.setGroup.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }
        

        if (args.length != 1) {
            sender.sendMessage(game.translate("werewolf.check.number_required"));
            return;
        }
        try {
            game.score.setGroup(Integer.parseInt(args[0]));
            for (Player p : Bukkit.getOnlinePlayers()) {
                Title.sendTitle(p, 20, 60, 20, game.translate("werewolf.commands.admin.group.top_title"), game.translate("werewolf.commands.admin.group.bot_title", game.score.getGroup()));
                p.sendMessage(game.translate("werewolf.commands.admin.group.respect_limit", game.score.getGroup()));
            }

        } catch (NumberFormatException ignored) {
        }
    }
}
