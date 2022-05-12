package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.group.command_2",
        descriptionKey = "werewolf.commands.admin.group.description2",
        moderatorAccess = true,
        argNumbers = 1)
public class CommandSetGroup implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        try {
            game.setGroup(Integer.parseInt(args[0]));
            for (Player p : Bukkit.getOnlinePlayers()) {
                VersionUtils.getVersionUtils().sendTitle(p, game.translate("werewolf.commands.admin.group.top_title"), game.translate("werewolf.commands.admin.group.bot_title",
                        Formatter.number(game.getGroup())), 20, 60, 20);
                p.sendMessage(game.translate(Prefix.YELLOW , "werewolf.commands.admin.group.respect_limit",
                        Formatter.number(game.getGroup())));
            }

        } catch (NumberFormatException ignored) {
        }
    }
}
