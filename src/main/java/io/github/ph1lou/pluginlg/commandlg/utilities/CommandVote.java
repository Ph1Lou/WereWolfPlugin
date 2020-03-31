package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandVote extends Commands {

    final MainLG main;

    public CommandVote(MainLG main, String name) {
        super(name);
        this.main=main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(main.text.getText(54));
            return;
        }
        main.vote.setUnVote((Player) sender, args[0]);
    }
}
