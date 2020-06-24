package io.github.ph1lou.werewolfplugin.commandlg.utilities;

import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.RoleRegister;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.command.CommandSender;

public class CommandCompo implements Commands {


    private final Main main;

    public CommandCompo(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!game.getConfig().getConfigValues().get(ToolLG.HIDE_COMPOSITION)) {
            StringBuilder sb = new StringBuilder();
            if(game.getConfig().getLoverSize()>0){
                sb.append("§3").append(game.getConfig().getLoverSize()).append("§r ").append(game.translate("werewolf.role.lover.display")).append("\n");
            }
            if(game.getConfig().getAmnesiacLoverSize()>0){
                sb.append("§3").append(game.getConfig().getAmnesiacLoverSize()).append("§r ").append(game.translate("werewolf.role.amnesiac_lover.display")).append("\n");
            }
            if(game.getConfig().getCursedLoverSize()>0){
                sb.append("§3").append(game.getConfig().getCursedLoverSize()).append("§r ").append(game.translate("werewolf.role.cursed_lover.display")).append("\n");
            }
            for (RoleRegister roleRegister:game.getRolesRegister()) {
                String key = roleRegister.getKey();
                if (game.getConfig().getRoleCount().get(key) > 0) {
                    sb.append("§3").append(game.getConfig().getRoleCount().get(key)).append("§r ").append(roleRegister.getName()).append("\n");
                }
            }
            sender.sendMessage(sb.toString());
        } else sender.sendMessage(game.translate("werewolf.commands.compo.composition_hide"));
    }
}
