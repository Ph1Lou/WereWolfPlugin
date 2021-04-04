package io.github.ph1lou.werewolfplugin.commands.admin;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;

import java.io.IOException;

public class CommandChange implements ICommands {


    private final Main main;

    public CommandChange(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = (GameManager) main.getWereWolfAPI();

        player.sendMessage(game.translate("werewolf.commands.admin.change.in_progress"));


        try {
            game.getMapManager().loadMap();
        } catch (IOException ignored) {
        }

        player.sendMessage(game.translate("werewolf.commands.admin.change.finished"));

    }
}
