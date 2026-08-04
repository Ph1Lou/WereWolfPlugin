package fr.ph1lou.werewolfplugin.statistiks;

import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.statistics.interfaces.IGameReview;
import fr.ph1lou.werewolfapi.statistics.interfaces.IRegisteredAction;
import fr.ph1lou.werewolfapi.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Construit le texte de survol (hover) listant les actions d'un joueur pendant la partie,
 * à partir du {@link IGameReview}. Partagé entre le récapitulatif de fin de partie
 * ({@code End}) et la commande admin role.
 */
public final class StatsHoverText {

    private static final int MAX_HOVER_ACTIONS = 200;
    private static final int MAX_HOVER_LENGTH = 24000;

    private StatsHoverText() {
    }

    /**
     * Mappe les review-UUID des joueurs vers leur pseudo, pour résoudre les cibles d'actions.
     */
    public static Map<UUID, String> buildReviewNames(WereWolfAPI game) {
        Map<UUID, String> reviewNames = new HashMap<>();
        for (IPlayerWW p : game.getPlayersWW()) {
            reviewNames.put(p.getReviewUUID(), p.getName());
        }
        return reviewNames;
    }

    /**
     * Construit le texte multiligne affiché au survol du pseudo d'un joueur.
     *
     * @return le texte de survol, ou une chaîne vide si aucune donnée n'est disponible.
     */
    public static String build(WereWolfAPI game, IPlayerWW playerWW,
                               IGameReview review, Map<UUID, String> reviewNames) {

        if (review == null) {
            return "";
        }

        UUID reviewUUID = playerWW.getReviewUUID();
        List<String> lines = new ArrayList<>();
        lines.add(game.translate("werewolf.end.stats_hover_title",
                Formatter.player(playerWW.getName())));

        List<String> actionLines = new ArrayList<>();

        for (IRegisteredAction action : review.getRegisteredActions()) {
            if (action.getUuid() == null || !action.getUuid().equals(reviewUUID)) {
                continue;
            }

            String label = translateAction(game, action.getEvent());
            String time = Utils.conversion(action.getTimer());
            StringBuilder line = new StringBuilder("§7").append(time).append(" §f").append(label);

            if (action.getUuidS() != null && !action.getUuidS().isEmpty()) {
                List<String> targets = new ArrayList<>();
                for (UUID targetUUID : action.getUuidS()) {
                    String targetName = reviewNames.get(targetUUID);
                    if (targetName != null) {
                        targets.add(targetName);
                    }
                }
                if (!targets.isEmpty()) {
                    line.append(" §b-> §e").append(String.join("§f, §e", targets));
                }
            }

            actionLines.add(line.toString());
        }

        if (actionLines.size() > MAX_HOVER_ACTIONS) {
            int skipped = actionLines.size() - MAX_HOVER_ACTIONS;
            lines.add("§7... (" + skipped + " more)");
            lines.addAll(actionLines.subList(actionLines.size() - MAX_HOVER_ACTIONS, actionLines.size()));
        } else {
            lines.addAll(actionLines);
        }

        String result = String.join("\n", lines);
        if (result.length() > MAX_HOVER_LENGTH) {
            result = result.substring(0, MAX_HOVER_LENGTH) + "\n§7...";
        }
        return result;
    }

    private static String translateAction(WereWolfAPI game, String eventKey) {

        String suffix = eventKey.startsWith("werewolf.")
                ? eventKey.substring("werewolf.".length()) : eventKey;
        String label = game.translate("werewolf.statistics_action." + suffix);

        if (label == null || (label.startsWith("Message ") && (label.endsWith(" not found") || label.endsWith(" is not a string")))) {
            label = suffix.replace("_", " ");
            if (!label.isEmpty()) {
                label = Character.toUpperCase(label.charAt(0)) + label.substring(1);
            }
        }
        return label;
    }
}
