package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.VersionUtils;
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


        GameManager game = main.getCurrentGame();


        if (!sender.hasPermission("a.setGroup.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }
        

        if (args.length != 1) {
            sender.sendMessage(game.translate("werewolf.check.number_required"));
            return;
        }
        try {
            game.getScore().setGroup(Integer.parseInt(args[0]));
            for (Player p : Bukkit.getOnlinePlayers()) {
                VersionUtils.getVersionUtils().sendTitle(p, game.translate("werewolf.commands.admin.group.top_title"), game.translate("werewolf.commands.admin.group.bot_title", game.getScore().getGroup()), 20, 60, 20);
                p.sendMessage(game.translate("werewolf.commands.admin.group.respect_limit", game.getScore().getGroup()));
            }

        } catch (NumberFormatException ignored) {
        }
    }
}
