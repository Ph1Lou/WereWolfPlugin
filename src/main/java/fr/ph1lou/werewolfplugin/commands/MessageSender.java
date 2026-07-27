package fr.ph1lou.werewolfplugin.commands;

import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import org.bukkit.entity.Player;

/**
 * Canal d'envoi de message abstrait pour {@link ArgumentResolver}.
 *
 * <p>Permet d'envoyer un message traduit (clé i18n + préfixe) indifféremment à un
 * exécutant {@link Player} (commandes admin/joueur) ou {@link IPlayerWW} (commandes
 * de rôle), sans coupler le résolveur à l'un ou l'autre.</p>
 */
@FunctionalInterface
public interface MessageSender {

    void send(String prefixKey, String key, Formatter... formatters);

    /** Adaptateur pour un exécutant Bukkit {@link Player}. */
    static MessageSender forPlayer(WereWolfAPI game, Player player) {
        return (prefixKey, key, formatters) ->
                player.sendMessage(game.translate(prefixKey, key, formatters));
    }

    /** Adaptateur pour un exécutant {@link IPlayerWW}. */
    static MessageSender forPlayerWW(IPlayerWW playerWW) {
        return (prefixKey, key, formatters) ->
                playerWW.sendMessageWithKey(prefixKey, key, formatters);
    }
}
