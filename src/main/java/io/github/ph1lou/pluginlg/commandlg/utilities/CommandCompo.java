package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCompo extends Commands {


    public CommandCompo(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) return;

        GameManager game = main.currentGame;

        TextLG text = game.text;

        if (game.config.configValues.get(ToolLG.COMPO_VISIBLE)) {
            StringBuilder sb = new StringBuilder();
            for (RoleLG role : RoleLG.values()) {
                if (game.config.roleCount.get(role) > 0) {
                    sb.append("§3").append(game.config.roleCount.get(role)).append("§r ").append(text.translateRole.get(role)).append("\n");
                }
            }
            sender.sendMessage(sb.toString());
        } else sender.sendMessage(text.getText(53));
    }
}
