package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.BorderLG;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;

import java.io.File;

public class CommandStart extends Commands {

    final MainLG main;

    public CommandStart(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.start.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }
        if (!main.isState(StateLG.LOBBY)) {
            sender.sendMessage(main.text.getText(119));
            return;
        }
        if (main.score.getRole() - Bukkit.getOnlinePlayers().size() > 0) {
            sender.sendMessage(main.text.getText(120));
            return;
        }
        try {
            World world = Bukkit.getWorld("world");
            WorldBorder wb = world.getWorldBorder();
            wb.setCenter(world.getSpawnLocation().getX(), world.getSpawnLocation().getZ());
            wb.setSize(main.config.borderValues.get(BorderLG.BORDER_MAX));
            wb.setWarningDistance((int) (wb.getSize() / 7));
            main.setState(StateLG.TRANSPORTATION);
            main.spark.updateDiscord();
        } catch (Exception e) {
            sender.sendMessage(main.text.getText(21));
        }
        File file = new File(main.getDataFolder() + File.separator + "configs" + File.separator, "saveCurrent.json");
        main.filelg.save(file, main.serialize.serialize(main.config));
        main.stufflg.save("saveCurrent");
    }
}
