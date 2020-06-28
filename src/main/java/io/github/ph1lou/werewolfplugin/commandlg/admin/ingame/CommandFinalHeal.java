package io.github.ph1lou.werewolfplugin.commandlg.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFinalHeal implements Commands {


    private final Main main;

    public CommandFinalHeal(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.fh.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setHealth(p.getMaxHealth());
            p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1, 20);
            p.sendMessage(game.translate("werewolf.commands.admin.final_heal"));
        }
    }
}
