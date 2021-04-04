package io.github.ph1lou.werewolfplugin.commands.admin;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandStop implements ICommands {


    private final Main main;

    public CommandStop(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = (GameManager) main.getWereWolfAPI();
        Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.stop.send",
                player.getName()));
        game.setState(StateGame.END);
        game.stopGame();

    }
}
