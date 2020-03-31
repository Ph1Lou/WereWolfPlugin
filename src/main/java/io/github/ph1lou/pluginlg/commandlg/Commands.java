package io.github.ph1lou.pluginlg.commandlg;

import org.bukkit.command.CommandSender;

public abstract class Commands {
    final String name;

    public Commands(String name){
        this.name=name;
    }

    public abstract void execute(CommandSender sender, String[] args);

    public String getName() {
        return this.name;
    }

}
