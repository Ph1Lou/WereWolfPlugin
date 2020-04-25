package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.BorderLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import io.github.ph1lou.pluginlg.worldloader.WorldFillTask;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGeneration extends Commands {

    public CommandGeneration(MainLG main) {
        super(main);
    }


    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        GameManager game=null;
        Player player =(Player) sender;

        for(GameManager gameManager:main.listGames.values()){

            if(gameManager.getWorld().equals(player.getWorld())){
                game=gameManager;
                break;
            }
        }

        if(game==null){
            return;
        }

        TextLG text = game.text;

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.generation.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }

        int chunksPerRun = 20;
        if (game.wft == null || game.wft.getPercentageCompleted()==100) {
            game.wft = new WorldFillTask(game, chunksPerRun, game.config.borderValues.get(BorderLG.BORDER_MAX) / 2);
            game.wft.setTaskID(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main, game.wft, 1, 1));
            sender.sendMessage(text.getText(269));
        } else sender.sendMessage(text.getText(11));
    }
}
