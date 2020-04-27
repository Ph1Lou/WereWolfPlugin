package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandCreate extends Commands {


    public CommandCreate(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.create.use")) {
            sender.sendMessage(main.defaultLanguage.getText(116));
            return;
        }
        Player player = (Player) sender;
        String playerName = sender.getName();
        UUID uuid = player.getUniqueId();

        if(main.listGames.containsKey(uuid)){
            sender.sendMessage(main.defaultLanguage.getText(288));
            return;
        }

        if(main.listGames.size()==1){
            for(GameManager game:main.listGames.values()){
                sender.sendMessage(String.format(main.defaultLanguage.getText(289),game.getGameName()));
            }
            return;
        }
        sender.sendMessage(main.defaultLanguage.getText(290));
        ((Player) sender).performCommand("lg join "+new GameManager(main, playerName,uuid).getGameUUID());

    }
}
