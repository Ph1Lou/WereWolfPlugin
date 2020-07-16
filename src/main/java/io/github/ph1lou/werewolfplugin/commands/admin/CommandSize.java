package io.github.ph1lou.werewolfplugin.commands.admin;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.utils.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSize implements Commands {


    private final Main main;

    public CommandSize(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();
        World world = game.getWorld();

        if(world==null){
            game.createMap();
            world=game.getWorld();
        }

        Location location = world.getSpawnLocation();

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.size.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        sender.sendMessage(game.translate("werewolf.commands.admin.size.begin"));
        int size = VersionUtils.getVersionUtils().biomeSize(location, world);
        sender.sendMessage(game.translate("werewolf.commands.admin.size.result",size));

        if(!(sender instanceof Player)) return;

        TextComponent msg = new TextComponent(game.translate("werewolf.commands.admin.size.change"));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/a change"));
        ((Player)sender).spigot().sendMessage(msg);
    }
}
