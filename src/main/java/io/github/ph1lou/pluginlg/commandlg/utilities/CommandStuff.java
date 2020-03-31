package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import org.bukkit.command.CommandSender;

public class CommandStuff extends Commands {

    final MainLG main;

    public CommandStuff(MainLG main,String name) {
        super(name);
        this.main=main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(main.text.getText(211 + main.config.getLimitKnockBack()));
        sender.sendMessage(main.text.getText(214 + main.config.getLimitPunch()));
        sender.sendMessage(main.text.getText(217));
        sender.sendMessage(String.format(main.text.getText(206), main.config.getLimitProtectionIron()));
        sender.sendMessage(String.format(main.text.getText(207), main.config.getLimitProtectionDiamond()));
        sender.sendMessage(String.format(main.text.getText(208), main.config.getLimitPowerBow()));
        sender.sendMessage(String.format(main.text.getText(209), main.config.getLimitSharpnessIron()));
        sender.sendMessage(String.format(main.text.getText(210), main.config.getLimitSharpnessDiamond()));
        sender.sendMessage(main.text.getText(218));
        sender.sendMessage(main.text.getText(219));
        sender.sendMessage(String.format(main.text.getText(220), String.format(main.text.getText(208), main.config.getLimitPowerBow() + 1), String.format(main.text.getText(210), main.config.getLimitSharpnessDiamond() + 1)));
        sender.sendMessage(String.format(main.text.getText(221), String.format(main.text.getText(208), main.config.getLimitPowerBow() + 1)));
    }
}
