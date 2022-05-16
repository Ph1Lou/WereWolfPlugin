package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.enums.LoverType;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

@PlayerCommand(key = "werewolf.menu.roles.command_2",
        descriptionKey = "werewolf.menu.roles.description2",
        argNumbers = 0)
public class CommandCompo implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {


        if (game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION)) {

            player.sendMessage(game.translate(Prefix.RED , "werewolf.commands.compo.composition_hide"));

            return;
        }

        StringBuilder sb = new StringBuilder(game.translate("werewolf.commands.compo._"));
        sb.append(ChatColor.WHITE);
        if (game.getConfig().getLoverCount(LoverBase.LOVER) > 0) {
            sb.append(LoverType.LOVER.getChatColor()).append(game.translate(LoverBase.LOVER)).append(ChatColor.WHITE);
            if (game.getConfig().getLoverCount(LoverBase.LOVER) == 1) {
                sb.append(", ");
            } else {
                sb.append(" (§b").append(game.getConfig().getLoverCount(LoverBase.LOVER)).append("§f), ");
            }
        }
        if (game.getConfig().getLoverCount(LoverBase.AMNESIAC_LOVER) > 0) {
            sb.append(LoverType.AMNESIAC_LOVER.getChatColor()).append(game.translate(LoverBase.AMNESIAC_LOVER)).append(ChatColor.WHITE);
            if (game.getConfig().getLoverCount(LoverBase.AMNESIAC_LOVER) == 1) {
                sb.append(", ");
            } else {
                sb.append(" (§b").append(game.getConfig().getLoverCount(LoverBase.AMNESIAC_LOVER)).append("§f), ");
            }
        }

        if (game.getConfig().getLoverCount(LoverBase.CURSED_LOVER) > 0) {
            sb.append(LoverType.CURSED_LOVER.getChatColor()).append(game.translate(LoverBase.CURSED_LOVER)).append(ChatColor.WHITE);
            if (game.getConfig().getLoverCount(LoverBase.CURSED_LOVER) != 1) {
                sb.append(" (§b").append(game.getConfig().getLoverCount(LoverBase.CURSED_LOVER)).append("§f)");
            }
        } else {
            sb.replace(sb.length() - 2, sb.length(), "");
        }
        sb.append("\n");

        sb.append(getCompo(game, Category.WEREWOLF));
        sb.append(getCompo(game, Category.VILLAGER));
        sb.append(getCompo(game, Category.NEUTRAL));

        sb.append(game.translate("werewolf.commands.compo._"));
        player.sendMessage(sb.toString());
    }

    public String getCompo(WereWolfAPI game, Category category) {

        StringBuilder sb = new StringBuilder(category.getChatColor() + game.translate(category.getKey()));
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        sb.append("§f : ");

        Register.get().getRolesRegister().stream()
                .filter(roleRegister -> roleRegister.getMetaDatas()
                        .category() == category)
                .forEach(roleRegister -> {
                    String key = roleRegister.getMetaDatas().key();
                    int number = game.getConfig().getRoleCount(key);
                    if (number > 0) {
                        if (number == 1) {
                            sb.append(game.translate(roleRegister.getMetaDatas().key()))
                                    .append(", ");
                        } else {
                            sb.append(game.translate(roleRegister.getMetaDatas().key()))
                                    .append(" (§b").append(game.getConfig().getRoleCount(key))
                                    .append("§f), ");
                        }
                        atomicBoolean.set(true);
                    }
                });
        sb.replace(sb.length() - 2, sb.length(), "");
        sb.append("\n");
        if (!atomicBoolean.get()) {
            return "";
        }
        return sb.toString();
    }
}
