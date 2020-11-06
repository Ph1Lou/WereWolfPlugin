package io.github.ph1lou.werewolfplugin.commands.admin;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandSize implements Commands {


    private final Main main;

    public CommandSize(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        World world = game.getMapManager().getWorld();

        if (world == null) {
            game.getMapManager().createMap();
            world = game.getMapManager().getWorld();
        }

        Location location = world.getSpawnLocation();
        player.sendMessage(game.translate("werewolf.commands.admin.size.begin"));
        int size = VersionUtils.getVersionUtils().biomeSize(location, world);
        player.sendMessage(game.translate("werewolf.commands.admin.size.result", size));

        TextComponent msg = new TextComponent(game.translate("werewolf.commands.admin.size.change"));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/a %s", game.translate("werewolf.commands.admin.change.command"))));
        player.spigot().sendMessage(msg);
    }
}
