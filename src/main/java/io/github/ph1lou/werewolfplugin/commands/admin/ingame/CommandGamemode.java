package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandGamemode implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        try {
            int i = Integer.parseInt(args[0]);
            int j = 2;
            if (i == 0) {
                j = 1;
            } else if (i == 1) {
                j = 0;
            } else if (i == 3) {
                j = 3;
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
