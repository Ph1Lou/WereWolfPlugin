package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.StartEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.save.FileUtils_;
import io.github.ph1lou.werewolfplugin.save.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CommandStart implements Commands {


    private final Main main;

    public CommandStart(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();

        if (!sender.hasPermission("a.start.use") && !game.getModerationManager().getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }
        
        if (!game.isState(StateLG.LOBBY)) {
            sender.sendMessage(game.translate("werewolf.check.already_begin"));
            return;
        }
        if (game.getScore().getRole() - game.getScore().getPlayerSize() > 0) {
            sender.sendMessage(game.translate("werewolf.commands.admin.start.too_much_role"));
            return;
        }

        if (game.getMapManager().getWft() == null) {
            sender.sendMessage(game.translate("werewolf.commands.admin.generation.not_generated"));
            return;
        }

        if (game.getMapManager().getWft().getPercentageCompleted() < 100) {
            sender.sendMessage(game.translate("werewolf.commands.admin.generation.not_finished", new DecimalFormat("0.0").format(game.getMapManager().getWft().getPercentageCompleted())));
            return;
        }


        World world = game.getMapManager().getWorld();
        WorldBorder wb = world.getWorldBorder();
        wb.setCenter(world.getSpawnLocation().getX(), world.getSpawnLocation().getZ());
        wb.setSize(game.getConfig().getBorderMax());
        wb.setWarningDistance((int) (wb.getSize() / 7));
        game.setState(StateLG.TRANSPORTATION);
        java.io.File file = new java.io.File(main.getDataFolder() + java.io.File.separator + "configs" + java.io.File.separator, "saveCurrent.json");
        FileUtils_.save(file, Serializer.serialize(game.getConfig()));
        game.getStuffs().save("saveCurrent");
        Bukkit.getPluginManager().callEvent(new StartEvent(game));
    }
}
