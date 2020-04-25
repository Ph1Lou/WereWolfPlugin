package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.BorderLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.FileLG;
import io.github.ph1lou.pluginlg.savelg.SerializerLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class CommandStart extends Commands {


    public CommandStart(MainLG main) {
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

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.start.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }
        
        if (!game.isState(StateLG.LOBBY)) {
            sender.sendMessage(text.getText(119));
            return;
        }
        if (game.score.getRole() - Bukkit.getOnlinePlayers().size() > 0) {
            sender.sendMessage(text.getText(120));
            return;
        }
        try {
            World world = game.getWorld();
            WorldBorder wb = world.getWorldBorder();
            wb.setCenter(world.getSpawnLocation().getX(), world.getSpawnLocation().getZ());
            wb.setSize(game.config.borderValues.get(BorderLG.BORDER_MAX));
            wb.setWarningDistance((int) (wb.getSize() / 7));
            game.setState(StateLG.TRANSPORTATION);
        } catch (Exception e) {
            sender.sendMessage(text.getText(21));
        }
        File file = new File(game.getDataFolder() + File.separator + "configs" + File.separator, "saveCurrent.json");
       FileLG.save(file, SerializerLG.serialize(game.config));
        game.stufflg.save("saveCurrent");
    }
}
