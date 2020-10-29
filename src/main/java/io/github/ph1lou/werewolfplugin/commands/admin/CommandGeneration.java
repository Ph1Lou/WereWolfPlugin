package io.github.ph1lou.werewolfplugin.commands.admin;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;

public class CommandGeneration implements Commands {

    private final Main main;

    public CommandGeneration(Main main) {
        this.main = main;
    }


    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();

        game.getMapManager().generateMap(player, game.getConfig().getBorderMax() / 2);
    }
}
