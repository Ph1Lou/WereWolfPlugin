package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.ConfigBase;
import fr.ph1lou.werewolfapi.enums.LoverType;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfplugin.RegisterManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class CommandCompo implements ICommand {


    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {


        if (game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION.getKey())) {

            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.commands.compo.composition_hide"));

            return;
        }

        StringBuilder sb = new StringBuilder(game.translate("werewolf.commands.compo._"));
        sb.append(ChatColor.WHITE);
        if (game.getConfig().getLoverCount(LoverType.LOVER.getKey()) > 0) {
            sb.append(LoverType.LOVER.getChatColor()).append(game.translate(LoverType.LOVER.getKey())).append(ChatColor.WHITE);
            if (game.getConfig().getLoverCount(LoverType.LOVER.getKey()) == 1) {
                sb.append(", ");
            } else {
                sb.append(" (§b").append(game.getConfig().getLoverCount(LoverType.LOVER.getKey())).append("§f), ");
            }
        }
        if (game.getConfig().getLoverCount(LoverType.AMNESIAC_LOVER.getKey()) > 0) {
            sb.append(LoverType.AMNESIAC_LOVER.getChatColor()).append(game.translate(LoverType.AMNESIAC_LOVER.getKey())).append(ChatColor.WHITE);
            if (game.getConfig().getLoverCount(LoverType.AMNESIAC_LOVER.getKey()) == 1) {
                sb.append(", ");
            } else {
                sb.append(" (§b").append(game.getConfig().getLoverCount(LoverType.AMNESIAC_LOVER.getKey())).append("§f), ");
            }
        }

        if (game.getConfig().getLoverCount(LoverType.CURSED_LOVER.getKey()) > 0) {
            sb.append(LoverType.CURSED_LOVER.getChatColor()).append(game.translate(LoverType.CURSED_LOVER.getKey())).append(ChatColor.WHITE);
            if (game.getConfig().getLoverCount(LoverType.CURSED_LOVER.getKey()) != 1) {
                sb.append(" (§b").append(game.getConfig().getLoverCount(LoverType.CURSED_LOVER.getKey())).append("§f)");
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

        RegisterManager.get().getRolesRegister().stream()
                .filter(roleRegister -> roleRegister.getCategories().contains(category))
                .forEach(roleRegister -> {
                    String key = roleRegister.getKey();
                    int number = game.getConfig().getRoleCount(key);
                    if (number > 0) {
                        if (number == 1) {
                            sb.append(game.translate(roleRegister.getKey()))
                                    .append(", ");
                        } else {
                            sb.append(game.translate(roleRegister.getKey()))
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
