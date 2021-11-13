package io.github.ph1lou.werewolfplugin.commands.admin;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandSize implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        World world = game.getMapManager().getWorld();

        if (world == null) {
            game.getMapManager().createMap();
            world = game.getMapManager().getWorld();
        }

        Location location = world.getSpawnLocation();
        player.sendMessage(game.translate("werewolf.commands.admin.size.begin"));
        int size = VersionUtils.getVersionUtils().biomeSize(location, world);
        player.sendMessage(game.translate("werewolf.commands.admin.size.result",
                Formatter.format("&number&",size)));

        TextComponent msg = new TextComponent(game.translate("werewolf.commands.admin.size.change"));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/a %s", game.translate("werewolf.commands.admin.change.command"))));
        player.spigot().sendMessage(msg);
    }
}
