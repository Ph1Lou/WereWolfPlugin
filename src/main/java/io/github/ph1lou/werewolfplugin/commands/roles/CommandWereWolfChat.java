package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.events.werewolf.WereWolfCanSpeakInChatEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandWereWolfChat implements ICommands {

    private static final Map<UUID, Integer> messageSend = new HashMap<>();
    private static boolean enable = false;
    private final Main main;

    public CommandWereWolfChat(Main main) {
        this.main = main;
    }

    public static void disable() {
        enable = false;
    }

    public static void enable() {
        enable = true;
        messageSend.clear();
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        if (!game.getConfig().isConfigActive(ConfigsBase.WEREWOLF_CHAT.getKey())) {
            playerWW.sendMessageWithKey("werewolf.commands.admin.ww_chat.disable");
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
                    playerWW.sendMessageWithKey("werewolf.commands.admin.ww_chat.timer");
                }
            } else {
                playerWW.sendMessageWithKey("werewolf.commands.admin.ww_chat.speak_number", game.getConfig().getWereWolfChatMaxMessage());
            }


        } else playerWW.sendMessageWithKey("werewolf.commands.admin.ww_chat.not_access");
    }
}
