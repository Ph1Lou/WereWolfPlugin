package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;

public class CommandAdminHelp implements Commands {


    private final Main main;

    public CommandAdminHelp(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();

        player.sendMessage(game.translate("werewolf.commands.admin.help"));
    }
}
