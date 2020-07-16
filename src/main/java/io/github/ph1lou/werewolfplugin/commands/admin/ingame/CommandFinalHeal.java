package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFinalHeal implements Commands {


    private final Main main;

    public CommandFinalHeal(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.fh.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setHealth(VersionUtils.getVersionUtils().getPlayerMaxHealth(p));
            Sounds.NOTE_STICKS.play(p);
            p.sendMessage(game.translate("werewolf.commands.admin.final_heal"));
        }
    }
}
