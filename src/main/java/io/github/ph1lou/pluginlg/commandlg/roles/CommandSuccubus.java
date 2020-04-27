package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSuccubus extends Commands {


    public CommandSuccubus(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        if (!(sender instanceof Player)) {
            return;
        }

        GameManager game = null;
        Player player = (Player) sender;

        for (GameManager gameManager : main.listGames.values()) {
            if (gameManager.getWorld().equals(player.getWorld())) {
                game = gameManager;
                break;
            }
        }

        if (game == null) {
            return;
        }

        TextLG text = game.text;
        String playername = player.getName();

        if (!game.playerLG.containsKey(playername)) {
            player.sendMessage(text.getText(67));
            return;
        }

        PlayerLG plg = game.playerLG.get(playername);


        if (!game.isState(StateLG.LG)) {
            player.sendMessage(text.getText(68));
            return;
        }

        if (!plg.isRole(RoleLG.SUCCUBUS)) {
            player.sendMessage(String.format(text.getText(189), text.translateRole.get(RoleLG.SUCCUBUS)));
            return;
        }

        if (args.length != 1) {
            player.sendMessage(text.getText(54));
            return;
        }

        if (!plg.isState(State.LIVING)) {
            player.sendMessage(text.getText(97));
            return;
        }

        if (!plg.getAffectedPlayer().isEmpty()) {
            player.sendMessage(text.getText(103));
            return;
        }

        if (args[0].equals(playername)) {
            player.sendMessage(text.getText(105));
            return;
        }

        if (Bukkit.getPlayer(args[0]) == null || !game.playerLG.containsKey(args[0]) || game.playerLG.get(args[0]).isState(State.MORT)) {
            player.sendMessage(text.getText(106));
            return;
        }

        Location location = player.getLocation();
        Location locationTarget = Bukkit.getPlayer(args[0]).getLocation();

        if (location.distance(locationTarget) > game.config.getDistanceSuccubus()) {
            player.sendMessage(text.getText(111));
            return;
        }
        if (plg.getUse() >= game.config.getUseOfCharmed()) {
            player.sendMessage(text.getText(103));
            return;
        }

        plg.addAffectedPlayer(args[0]);
        game.playerLG.get(args[0]).addTargetOf(playername);
        player.sendMessage(String.format(text.powerHasBeenUse.get(RoleLG.SUCCUBUS), args[0]));
    }
}
