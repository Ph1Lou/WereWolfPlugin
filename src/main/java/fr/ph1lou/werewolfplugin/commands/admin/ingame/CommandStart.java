package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.save.FileUtils_;
import fr.ph1lou.werewolfplugin.save.Serializer;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.DecimalFormat;

@AdminCommand(key = "werewolf.commands.admin.start.command",
        descriptionKey = "werewolf.commands.admin.start.description",
        hostAccess = true,
        stateGame = StateGame.LOBBY,
        argNumbers = 0)
public class CommandStart implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getRoleInitialSize() - game.getPlayersCount() > 0) {
            player.sendMessage(
                    game.translate(Prefix.RED , "werewolf.commands.admin.start.too_much_role"));
            return;
        }

        if (game.getMapManager().getPercentageGenerated() == 0) {
            player.sendMessage(
                    game.translate(Prefix.RED , "werewolf.commands.admin.generation.not_generated"));
            return;
        }

        if (game.getMapManager().getPercentageGenerated() < 100) {
            player.sendMessage(
                    game.translate(Prefix.RED , "werewolf.commands.admin.generation.not_finished",
                            Formatter.format("&progress&",new DecimalFormat("0.0")
                                    .format(game.getMapManager().getPercentageGenerated()))));
            return;
        }

        World world = game.getMapManager().getWorld();
        WorldBorder wb = world.getWorldBorder();
        wb.setCenter(world.getSpawnLocation().getX(), world.getSpawnLocation().getZ());
        wb.setSize(game.getConfig().getBorderMax());
        wb.setWarningDistance((int) (wb.getSize() / 7));
        ((GameManager) game).setState(StateGame.TRANSPORTATION);
        File file = new File(
                JavaPlugin.getPlugin(Main.class).getDataFolder() +
                        File.separator +
                        "configs" +
                        File.separator,
                "saveCurrent.json");

        FileUtils_.save(file, Serializer.serialize(game.getConfig()));
        game.getStuffs().save("saveCurrent");
        Bukkit.getPluginManager().callEvent(new StartEvent(game));
    }
}
