package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandChat implements Commands {


    private final Main main;

    public CommandChat(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();

        game.getConfig().getConfigValues().put("werewolf.menu.global.chat", !game.getConfig().getConfigValues().get("werewolf.menu.global.chat"));

        Bukkit.broadcastMessage(game.getConfig().getConfigValues().get("werewolf.menu.global.chat") ? game.translate("werewolf.commands.admin.chat.on") : game.translate("werewolf.commands.admin.chat.off"));
    }
}
