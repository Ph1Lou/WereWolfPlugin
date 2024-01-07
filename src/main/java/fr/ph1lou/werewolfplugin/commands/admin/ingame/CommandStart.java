package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.save.ConfigurationLoader;
import fr.ph1lou.werewolfplugin.save.StuffLoader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

@AdminCommand(key = "werewolf.commands.admin.start.command",
        descriptionKey = "werewolf.commands.admin.start.description",
        statesGame = StateGame.LOBBY,
        argNumbers = 0)
public class CommandStart implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        if (game.getTotalRoles() - game.getPlayersCount() > 0) {
            player.sendMessage(
                    game.translate(Prefix.RED, "werewolf.commands.admin.start.too_much_role"));
            return;
        }

        if (game.getMapManager().getPercentageGenerated() < 100) {
            player.sendMessage(
                    game.translate(Prefix.RED, "werewolf.commands.admin.start.generation_not_finished",
                            Formatter.format("&progress&", new DecimalFormat("0.0")
                                    .format(game.getMapManager().getPercentageGenerated()))));
            return;
        }

        World world = game.getMapManager().getWorld();
        WorldBorder wb = world.getWorldBorder();
        wb.setCenter(world.getSpawnLocation().getX(), world.getSpawnLocation().getZ());
        wb.setSize(game.getConfig().getBorderMax());
        wb.setWarningDistance((int) (wb.getSize() / 7));
        ((GameManager) game).setState(StateGame.TRANSPORTATION);
        ConfigurationLoader.saveConfig(game, "saveCurrent");
        StuffLoader.saveStuff(game, "saveCurrent");
        Bukkit.getPluginManager().callEvent(new StartEvent(game));
    }
}
