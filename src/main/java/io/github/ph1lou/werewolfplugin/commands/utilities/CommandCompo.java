package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.RoleRegister;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.entity.Player;

public class CommandCompo implements Commands {


    private final Main main;

    public CommandCompo(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();

        if (game.getConfig().getConfigValues().get("werewolf.menu.global.hide_composition")) {

            player.sendMessage(game.translate("werewolf.commands.compo.composition_hide"));
        } else {

            StringBuilder sb = new StringBuilder();
            if (game.getConfig().getLoverSize() > 0) {
                sb.append("§3").append(game.getConfig().getLoverSize()).append("§r ").append(game.translate("werewolf.role.lover.display")).append("\n");
            }
            if (game.getConfig().getAmnesiacLoverSize() > 0) {
                sb.append("§3").append(game.getConfig().getAmnesiacLoverSize()).append("§r ").append(game.translate("werewolf.role.amnesiac_lover.display")).append("\n");
            }
            if (game.getConfig().getCursedLoverSize() > 0) {
                sb.append("§3").append(game.getConfig().getCursedLoverSize()).append("§r ").append(game.translate("werewolf.role.cursed_lover.display")).append("\n");
            }
            for (RoleRegister roleRegister : game.getRolesRegister()) {
                String key = roleRegister.getKey();
                if (game.getConfig().getRoleCount().get(key) > 0) {
                    sb.append("§3").append(game.getConfig().getRoleCount().get(key)).append("§r ").append(roleRegister.getName()).append("\n");
                }
            }
            player.sendMessage(sb.toString());
        }
    }
}
