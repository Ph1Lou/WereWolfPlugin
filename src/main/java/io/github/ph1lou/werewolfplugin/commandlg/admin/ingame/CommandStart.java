package io.github.ph1lou.werewolfplugin.commandlg.admin.ingame;

import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.events.StartEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.savelg.FileLG;
import io.github.ph1lou.werewolfplugin.savelg.SerializerLG;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.DecimalFormat;

public class CommandStart implements Commands {


    private final Main main;

    public CommandStart(Main main) {
        this.main = main;
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

        if(game.wft == null){
            sender.sendMessage(game.translate("werewolf.commands.admin.generation.not_generated"));
            return;
        }

        if(game.wft.getPercentageCompleted()<100){
            sender.sendMessage(game.translate("werewolf.commands.admin.generation.not_finished",new DecimalFormat("0.0").format(game.wft.getPercentageCompleted())));
            return;
        }

        World world = game.getWorld();
        WorldBorder wb = world.getWorldBorder();
        wb.setCenter(world.getSpawnLocation().getX(), world.getSpawnLocation().getZ());
        wb.setSize(game.getConfig().getBorderMax());
        wb.setWarningDistance((int) (wb.getSize() / 7));
        game.setState(StateLG.TRANSPORTATION);
        File file = new File(main.getDataFolder() + File.separator + "configs" + File.separator, "saveCurrent.json");
        FileLG.save(file, SerializerLG.serialize(game.getConfig()));
        game.getStuffs().save("saveCurrent");
        Bukkit.getPluginManager().callEvent(new StartEvent(game));
    }
}
