package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IMapManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandPreview implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

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
