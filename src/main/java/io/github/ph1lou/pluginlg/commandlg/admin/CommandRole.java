package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.PlayerLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import org.bukkit.command.CommandSender;

public class CommandRole extends Commands {

    final MainLG main;

    public CommandRole(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.role.use")) {
            sender.sendMessage(main.text.getText(116));
            return;
        }
        if (!main.isState(StateLG.LG)) {
            sender.sendMessage(main.text.getText(144));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage(main.text.getText(54));
            return;
        }
        if (!main.playerLG.containsKey(args[0])) {
            sender.sendMessage(main.text.getText(132));
            return;
        }
        PlayerLG plg = main.playerLG.get(args[0]);

        if (main.playerLG.containsKey(sender.getName()) && main.playerLG.get(sender.getName()).isState(State.LIVING)) {
            sender.sendMessage(main.text.getText(145));
            return;
        }
        sender.sendMessage(String.format(main.text.getText(92), args[0], main.text.translateRole.get(plg.getRole())) + String.format(main.text.getText(91), plg.hasPower()));
        for (String p : plg.getLovers()) {
            sender.sendMessage(String.format(main.text.getText(146), p));
        }

        if (!plg.getCursedLovers().equals("")) {
            sender.sendMessage(String.format(main.text.getText(135), plg.getCursedLovers()));
        }

        for (String p : plg.getAffectedPlayer()) {
            sender.sendMessage(String.format(main.text.getText(147), p));
        }
        if (!plg.getKiller().equals("")) {
            sender.sendMessage(String.format(main.text.getText(148), plg.getKiller()));
        }
    }
}
