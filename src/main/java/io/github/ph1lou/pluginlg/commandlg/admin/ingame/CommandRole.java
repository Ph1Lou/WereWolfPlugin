package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRole extends Commands {


    public CommandRole(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

     GameManager game = main.currentGame;

        TextLG text = game.text;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.role.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }
        
        if (!game.isState(StateLG.LG)) {
            sender.sendMessage(text.getText(144));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage(text.getText(54));
            return;
        }
        if (!game.playerLG.containsKey(args[0])) {
            sender.sendMessage(text.getText(132));
            return;
        }
        PlayerLG plg = game.playerLG.get(args[0]);

        if (game.playerLG.containsKey(sender.getName()) && game.playerLG.get(sender.getName()).isState(State.LIVING)) {
            sender.sendMessage(text.getText(145));
            return;
        }
        sender.sendMessage(String.format(text.getText(92), args[0], text.translateRole.get(plg.getRole())) + String.format(text.getText(91), plg.hasPower()));
        for (String p : plg.getLovers()) {
            sender.sendMessage(String.format(text.getText(146), p));
        }

        if (!plg.getCursedLovers().equals("")) {
            sender.sendMessage(String.format(text.getText(135), plg.getCursedLovers()));
        }

        for (String p : plg.getAffectedPlayer()) {
            sender.sendMessage(String.format(text.getText(147), p));
        }
        if (!plg.getKiller().equals("")) {
            sender.sendMessage(String.format(text.getText(148), plg.getKiller()));
        }
    }
}
