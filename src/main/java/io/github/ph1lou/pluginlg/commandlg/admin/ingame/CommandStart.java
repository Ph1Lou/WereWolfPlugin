package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.FileLG;
import io.github.ph1lou.pluginlg.savelg.SerializerLG;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
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


        GameManager game = main.currentGame;


        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.start.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }
        
        if (!game.isState(StateLG.LOBBY)) {
            sender.sendMessage(game.translate("werewolf.check.already_begin"));
            return;
        }
        if (game.score.getRole() - game.score.getPlayerSize() > 0) {
            sender.sendMessage(game.translate("werewolf.commands.admin.start.too_much_role"));
            return;
        }

        if(game.getWorld()==null){
            sender.sendMessage(game.translate("werewolf.commands.admin.generation.not_generated"));
            return;
        }

        World world = game.getWorld();
        WorldBorder wb = world.getWorldBorder();
        wb.setCenter(world.getSpawnLocation().getX(), world.getSpawnLocation().getZ());
        wb.setSize(game.config.getBorderMax());
        wb.setWarningDistance((int) (wb.getSize() / 7));
        game.setState(StateLG.TRANSPORTATION);
        File file = new File(game.getDataFolder() + File.separator + "configs" + File.separator, "saveCurrent.json");
        FileLG.save(file, SerializerLG.serialize(game.config));
        game.stufflg.save("saveCurrent");
    }
}
