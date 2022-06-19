package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.gamemode.command",
        descriptionKey = "werewolf.commands.admin.gamemode.description",
        moderatorAccess = true,
        argNumbers = 1)
public class CommandGamemode implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        try {
            int gamemode = Integer.parseInt(args[0]);
            int j = 2;
            if (gamemode == 0) {
                j = 1;
            } else if (gamemode == 1) {
                j = 0;
            } else if (gamemode == 3) {
                j = 3;
            }

            player.setGameMode(GameMode.values()[j]);
            String message = game.translate(Prefix.YELLOW , "werewolf.commands.admin.gamemode.send",
                    Formatter.player(player.getName()),
                    Formatter.number(gamemode));

            game.getModerationManager().alertHostsAndModerators(message);
            if (!game.getModerationManager().isStaff(uuid)) {
                player.sendMessage(message);
            }
        }
        catch (NumberFormatException ignored){
        }
    }
}
