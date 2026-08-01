package fr.ph1lou.werewolfplugin.commands.utilities;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.TellableStoryEvent;
import fr.ph1lou.werewolfapi.annotations.statistics.StatisticsEvent;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Register;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@PlayerCommand(key = "werewolf.commands.player.tellable_story.command",
        descriptionKey = "werewolf.commands.player.tellable_story.description",
        argNumbers = 0)
public class CommandTellableStory implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        boolean hidden = game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION);

        Map<String, List<String>> actionsByRole = new LinkedHashMap<>();
        List<String> genericActions = new ArrayList<>();

        for (Wrapper<Event, StatisticsEvent> wrapper : Register.get().getEventsClass()) {
            TellableStoryEvent tellable = wrapper.getClazz().getAnnotation(TellableStoryEvent.class);
            if (tellable == null) {
                continue;
            }
            String labelKey = tellable.key().isEmpty() ? wrapper.getMetaDatas().key() : tellable.key();
            String label = game.translate(labelKey);
            String roleKey = tellable.roleKey();
            if (roleKey.isEmpty()) {
                genericActions.add(label);
            } else {
                actionsByRole.computeIfAbsent(roleKey, k -> new ArrayList<>()).add(label);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(game.translate("werewolf.commands.player.tellable_story._")).append('\n');
        sb.append(game.translate(Prefix.YELLOW, "werewolf.commands.player.tellable_story.header")).append('\n');
        if (hidden) {
            sb.append(game.translate("werewolf.commands.player.tellable_story.full_list")).append('\n');
        }

        boolean any = false;

        for (Category category : new Category[]{Category.WEREWOLF, Category.VILLAGER, Category.NEUTRAL}) {

            StringBuilder categorySb = new StringBuilder();
            boolean categoryHasAny = false;

            for (Wrapper<IRole, Role> roleWrapper : Register.get().getRolesRegister()) {
                Role role = roleWrapper.getMetaDatas();
                if (role.category() != category) {
                    continue;
                }
                if (!hidden && game.getConfig().getRoleCount(role.key()) <= 0) {
                    continue;
                }
                List<String> actions = actionsByRole.get(role.key());
                if (actions == null || actions.isEmpty()) {
                    continue;
                }
                categoryHasAny = true;
                any = true;
                categorySb.append(ChatColor.AQUA)
                        .append(game.translate(role.key()))
                        .append(ChatColor.WHITE).append(" :\n");
                for (String action : actions) {
                    categorySb.append("  ").append(ChatColor.GRAY).append("-")
                            .append(ChatColor.WHITE).append(" ").append(action).append('\n');
                }
            }

            if (categoryHasAny) {
                sb.append(game.translate(category.getChatColor()))
                        .append(game.translate(category.getKey()))
                        .append(ChatColor.WHITE).append(" :\n");
                sb.append(categorySb);
            }
        }

        if (!genericActions.isEmpty()) {
            any = true;
            sb.append(game.translate(Prefix.YELLOW, "werewolf.commands.player.tellable_story.generic")).append('\n');
            for (String action : genericActions) {
                sb.append("  ").append(ChatColor.GRAY).append("-")
                        .append(ChatColor.WHITE).append(" ").append(action).append('\n');
            }
        }

        if (!any) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.player.tellable_story.none"));
            return;
        }

        sb.append(game.translate("werewolf.commands.player.tellable_story._"));
        player.sendMessage(sb.toString());
    }
}
