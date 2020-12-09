package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandChat implements Commands {


    private final Main main;

    public CommandChat(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        game.getConfig().getConfigValues().put(ConfigsBase.CHAT.getKey(), !game.getConfig().getConfigValues().get(ConfigsBase.CHAT.getKey()));

        Bukkit.broadcastMessage(game.getConfig().getConfigValues().get(ConfigsBase.CHAT.getKey()) ? game.translate("werewolf.commands.admin.chat.on") : game.translate("werewolf.commands.admin.chat.off"));
    }
}
