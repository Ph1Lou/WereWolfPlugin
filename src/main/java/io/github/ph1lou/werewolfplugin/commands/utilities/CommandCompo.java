package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;

public class CommandCompo implements Commands {


    private final Main main;

    public CommandCompo(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();


        if (game.getConfig().isConfigActive(ConfigsBase.HIDE_COMPOSITION.getKey())) {

            player.sendMessage(game.translate("werewolf.commands.compo.composition_hide"));
        } else {

            StringBuilder sb = new StringBuilder(game.translate("werewolf.commands.compo.message"));
            if (game.getConfig().getLoverSize() > 0) {
                sb.append("§3").append(game.getConfig().getLoverSize()).append("§r ").append(game.translate(RolesBase.LOVER.getKey())).append(", ");
            }
            if (game.getConfig().getAmnesiacLoverSize() > 0) {
                sb.append("§3").append(game.getConfig().getAmnesiacLoverSize()).append("§r ").append(game.translate(RolesBase.AMNESIAC_LOVER.getKey())).append(", ");
            }
            if (game.getConfig().getCursedLoverSize() > 0) {
                sb.append("§3").append(game.getConfig().getCursedLoverSize()).append("§r ").append(game.translate(RolesBase.CURSED_LOVER.getKey())).append(", ");
            }
            for (RoleRegister roleRegister : main.getRegisterManager().getRolesRegister()) {
                String key = roleRegister.getKey();
                if (game.getConfig().getRoleCount(key) > 0) {
                    sb.append("§3").append(game.getConfig().getRoleCount(key)).append("§r ").append(game.translate(roleRegister.getKey())).append(", ");
                }
            }
            player.sendMessage(sb.toString());
        }
    }
}
