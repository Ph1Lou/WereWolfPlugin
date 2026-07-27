package fr.ph1lou.werewolfplugin.commands;

import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;

/**
 * Helpers de validation facteurs des patterns récurrents des commandes de rôle,
 * à appeler depuis {@code execute(...)} juste après {@code ctx.player(i)}.
 *
 * <p>Ils complètent {@link ArgumentResolver} (qui gère ce qui est déclarable via
 * {@code @PlayerArg} / {@code requiredLimitedUse}) pour les checks trop métier ou
 * irréguliers pour entrer dans une annotation.</p>
 */
public final class CommandCheck {

    private CommandCheck() {
    }

    /**
     * Rejette si la cible a déjà été affectée par le pouvoir du rôle ({@code IAffectedPlayers}).
     *
     * @param messageKey clé i18n de surcharge (vide → {@code werewolf.check.already_get_power}).
     * @return {@code true} si la cible est déjà affectée (message envoyé, la commande doit s'arrêter).
     */
    public static boolean rejectIfAlreadyAffected(IPlayerWW sender, IRole role, IPlayerWW target, String messageKey) {
        if (role instanceof IAffectedPlayers && ((IAffectedPlayers) role).getAffectedPlayers().contains(target)) {
            sender.sendMessageWithKey(Prefix.RED, messageKey.isEmpty() ? "werewolf.check.already_get_power" : messageKey);
            return true;
        }
        return false;
    }

    /**
     * Rejette si l'exécuteur est trop éloigné de la cible (distance &gt; IntValue {@code distanceKey}).
     *
     * @param messageKey clé i18n de surcharge (vide → {@code werewolf.check.distance}).
     * @return {@code true} si trop éloigné (message envoyé, la commande doit s'arrêter).
     */
    public static boolean rejectIfTooFar(WereWolfAPI game, IPlayerWW sender, IPlayerWW target,
                                         String distanceKey, String messageKey) {
        int max = game.getConfig().getValue(distanceKey);
        if (sender.distance(target) > max) {
            sender.sendMessageWithKey(Prefix.RED,
                    messageKey.isEmpty() ? "werewolf.check.distance" : messageKey,
                    Formatter.number(max));
            return true;
        }
        return false;
    }

    /**
     * Rejette si le rôle ({@code ILimitedUse}) a atteint son nombre d'utilisations.
     * Pour une limite fixe déclarative, préférer {@code @RoleCommand(requiredLimitedUse=...)}.
     *
     * @return {@code true} si le nombre d'utilisations est épuisé (message envoyé).
     */
    public static boolean rejectIfNoUseLeft(IPlayerWW sender, IRole role, int limit) {
        if (role instanceof ILimitedUse && ((ILimitedUse) role).getUse() >= limit) {
            sender.sendMessageWithKey(Prefix.RED, "werewolf.check.power");
            return true;
        }
        return false;
    }
}
