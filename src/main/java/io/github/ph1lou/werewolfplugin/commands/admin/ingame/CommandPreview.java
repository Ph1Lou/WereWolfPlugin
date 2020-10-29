package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandPreview implements Commands {


    private final Main main;

    public CommandPreview(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();


        if (!game.isState(StateLG.LOBBY)) {
            player.sendMessage(game.translate("werewolf.check.game_in_progress"));
            return;
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
