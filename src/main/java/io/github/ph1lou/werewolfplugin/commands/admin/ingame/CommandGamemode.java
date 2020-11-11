package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandGamemode implements Commands {


    private final Main main;

    public CommandGamemode(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        try {
            int i = Integer.parseInt(args[0]);
            int j = 2;
            if (i == 0) {
                j = 1;
            } else if (i == 1) {
                j = 0;
            }

            player.setGameMode(GameMode.values()[j]);
            String message = game.translate("werewolf.commands.admin.gamemode.send", player.getName(), i);

            game.getModerationManager().alertHostsAndModerators(message);
            if (!game.getModerationManager().isStaff(uuid)) {
                player.sendMessage(message);
            }
        }
        catch (NumberFormatException ignored){
        }
    }
}
