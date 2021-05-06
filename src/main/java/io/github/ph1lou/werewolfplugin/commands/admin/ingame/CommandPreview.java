package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IMapManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandPreview implements ICommands {


    private final Main main;

    public CommandPreview(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        IMapManager mapManager = game.getMapManager();

        if (mapManager.getWorld() == null) {
            mapManager.createMap();
        }

        if (player.getWorld().equals(game.getMapManager().getWorld())) {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            player.sendMessage(game.translate("werewolf.commands.admin.preview.lobby"));
        } else {
            player.teleport(game.getMapManager().getWorld().getSpawnLocation());
            player.sendMessage(game.translate("werewolf.commands.admin.preview.map"));
        }


    }
}