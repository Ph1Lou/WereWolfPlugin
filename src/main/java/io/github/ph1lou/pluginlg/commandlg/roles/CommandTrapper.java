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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTrapper extends Commands {


    public CommandTrapper(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        GameManager game = main.currentGame;
        Player player = (Player) sender;
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

        if (!plg.isRole(RoleLG.TRAPPEUR)) {
            player.sendMessage(String.format(text.getText(189), text.translateRole.get(RoleLG.TRAPPEUR)));
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

        if (!plg.hasPower()) {
            player.sendMessage(text.getText(103));
            return;
        }

        if (Bukkit.getPlayer(args[0]) == null || !game.playerLG.containsKey(args[0]) || game.playerLG.get(args[0]).isState(State.MORT)) {
            player.sendMessage(text.getText(106));
            return;
        }

        if (plg.getAffectedPlayer().contains(args[0])) {
            player.sendMessage(text.getText(107));
            return;
        }

        plg.clearAffectedPlayer();
        plg.addAffectedPlayer(args[0]);
        plg.setPower(false);

        Bukkit.getPlayer(args[0]).sendMessage(text.getText(272));
        player.sendMessage(String.format(text.powerHasBeenUse.get(RoleLG.TRAPPEUR), args[0]));
    }
}
