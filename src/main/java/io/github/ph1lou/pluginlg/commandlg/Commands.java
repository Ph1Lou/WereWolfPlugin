package io.github.ph1lou.pluginlg.commandlg;

import io.github.ph1lou.pluginlg.MainLG;
import org.bukkit.command.CommandSender;

public abstract class Commands {

    public final MainLG main;

    public Commands(MainLG main) {
        this.main=main;
    }

    public abstract void execute(CommandSender sender, String[] args);

}
