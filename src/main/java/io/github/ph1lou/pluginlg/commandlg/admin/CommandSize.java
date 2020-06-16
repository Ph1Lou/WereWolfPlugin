package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.utils.WorldUtils;
import io.github.ph1lou.pluginlgapi.Commands;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSize implements Commands {


    private final MainLG main;

    public CommandSize(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;
        World world = game.getWorld();

        Location location = world.getSpawnLocation();

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.size.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        sender.sendMessage(game.translate("werewolf.commands.admin.size.begin"));
        int size= WorldUtils.biomeSize(location,world);
        sender.sendMessage(game.translate("werewolf.commands.admin.size.result",size));

        if(!(sender instanceof Player)) return;

        TextComponent msg = new TextComponent(game.translate("werewolf.commands.admin.size.change"));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/a change"));
        ((Player)sender).spigot().sendMessage(msg);
    }
}
