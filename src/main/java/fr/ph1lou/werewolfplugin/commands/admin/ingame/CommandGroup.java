package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandGroup implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        for (Player p : Bukkit.getOnlinePlayers()) {
            VersionUtils.getVersionUtils().sendTitle(p, game.translate("werewolf.commands.admin.group.top_title"), game.translate("werewolf.commands.admin.group.bot_title",
                    Formatter.number(game.getGroup())), 20, 60, 20);
            p.sendMessage(game.translate(Prefix.YELLOW.getKey() , "werewolf.commands.admin.group.respect_limit",
                    Formatter.number(game.getGroup())));
        }
    }
}
