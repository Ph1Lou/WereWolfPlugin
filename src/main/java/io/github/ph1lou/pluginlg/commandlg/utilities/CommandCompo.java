package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import org.bukkit.command.CommandSender;

public class CommandCompo extends Commands {

    final MainLG main;

    public CommandCompo(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (main.config.configValues.get(ToolLG.COMPO_VISIBLE)) {
            StringBuilder sb = new StringBuilder();
            for (RoleLG role : RoleLG.values()) {
                if (main.config.roleCount.get(role) > 0) {
                    sb.append("§3").append(main.config.roleCount.get(role)).append("§r ").append(main.text.translateRole.get(role)).append("\n");
                }
            }
            sender.sendMessage(sb.toString());
        } else sender.sendMessage(main.text.getText(53));
    }
}
