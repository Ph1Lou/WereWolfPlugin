package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import org.bukkit.command.CommandSender;

public class CommandCompo extends Commands {


    public CommandCompo(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!game.config.getConfigValues().get(ToolLG.HIDE_COMPOSITION)) {
            StringBuilder sb = new StringBuilder();
            for (RoleLG role : RoleLG.values()) {
                if (game.config.getRoleCount().get(role) > 0) {
                    sb.append("§3").append(game.config.getRoleCount().get(role)).append("§r ").append(game.translate(role.getKey())).append("\n");
                }
            }
            sender.sendMessage(sb.toString());
        } else sender.sendMessage(game.translate("werewolf.commands.compo.composition_hide"));
    }
}
