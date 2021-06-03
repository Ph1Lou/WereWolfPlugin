package io.github.ph1lou.werewolfplugin.commands.admin;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;

import java.io.IOException;

public class CommandChange implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        player.sendMessage(game.translate("werewolf.commands.admin.change.in_progress"));


        try {
            game.getMapManager().loadMap();
        } catch (IOException ignored) {
        }

        player.sendMessage(game.translate("werewolf.commands.admin.change.finished"));

    }
}
