package io.github.ph1lou.werewolfplugin.commands.roles.werewolf;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.ConfigBase;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.events.werewolf.WereWolfCanSpeakInChatEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandWereWolfChat implements ICommand {

    private static final Map<UUID, Integer> messageSend = new HashMap<>();
    private static boolean enable = false;

    public static void disable() {
        enable = false;
    }

    public static void enable() {
        enable = true;
        messageSend.clear();
    }

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (!game.getConfig().isConfigActive(ConfigBase.WEREWOLF_CHAT.getKey())) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.commands.admin.ww_chat.disable");
            return;
        }

        WereWolfCanSpeakInChatEvent wereWolfCanSpeakInChatEvent =
                new WereWolfCanSpeakInChatEvent(playerWW);

        Bukkit.getPluginManager().callEvent(wereWolfCanSpeakInChatEvent);

        if (wereWolfCanSpeakInChatEvent.canSpeak()) {

            if (messageSend.getOrDefault(player.getUniqueId(), 0) < game.getConfig().getWereWolfChatMaxMessage()) {
                if (enable) {
                    messageSend.merge(player.getUniqueId(), 1, Integer::sum);
                    StringBuilder sb = new StringBuilder();

                    for (String w : args) {
                        sb.append(w).append(" ");
                    }

                    WereWolfChatEvent wereWolfChatEvent = new WereWolfChatEvent(playerWW, sb.toString());
                    Bukkit.getPluginManager().callEvent(wereWolfChatEvent);
                } else {
                    playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.commands.admin.ww_chat.timer");
                }
            } else {
                playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.commands.admin.ww_chat.speak_number",
                        Formatter.number(game.getConfig().getWereWolfChatMaxMessage()));
            }


        } else playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.commands.admin.ww_chat.not_access");
    }
}
