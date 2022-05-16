package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.IMapManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AdminCommand(key = "werewolf.commands.admin.preview.command",
        descriptionKey = "werewolf.commands.admin.preview.description",
        statesGame = StateGame.LOBBY,
        argNumbers = 0,
        moderatorAccess = true)
public class CommandPreview implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        IMapManager mapManager = game.getMapManager();

        if (mapManager.getWorld() == null) {
            mapManager.createMap();
        }

        if (player.getWorld().equals(game.getMapManager().getWorld())) {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            player.sendMessage(game.translate(Prefix.YELLOW , "werewolf.commands.admin.preview.lobby"));
        } else {
            player.teleport(game.getMapManager().getWorld().getSpawnLocation());
            player.sendMessage(game.translate(Prefix.YELLOW , "werewolf.commands.admin.preview.map"));
        }


    }
}
