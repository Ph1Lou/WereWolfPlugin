package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandGroup implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        for (Player p : Bukkit.getOnlinePlayers()) {
            VersionUtils.getVersionUtils().sendTitle(p, game.translate("werewolf.commands.admin.group.top_title"), game.translate("werewolf.commands.admin.group.bot_title", game.getGroup()), 20, 60, 20);
            p.sendMessage(game.translate("werewolf.commands.admin.group.respect_limit", game.getGroup()));
        }
    }
}
