package fr.ph1lou.werewolfplugin.commands.roles.werewolf;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfCanSpeakInChatEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfSiteChatEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.configs.WerewolfChat;
import org.bukkit.Bukkit;

@RoleCommand(key = "werewolf.commands.player.ww_chat.command",
        roleKeys = {},
        statesPlayer = StatePlayer.ALIVE,
        statesGame = StateGame.GAME,
        argNumbers = {})
public class CommandWereWolfChat implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

        if (!game.getConfig().isConfigActive(ConfigBase.WEREWOLF_CHAT)) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.commands.player.ww_chat.disable");
            return;
        }

        WereWolfCanSpeakInChatEvent wereWolfCanSpeakInChatEvent =
                new WereWolfCanSpeakInChatEvent(playerWW);

        Bukkit.getPluginManager().callEvent(wereWolfCanSpeakInChatEvent);

        if (wereWolfCanSpeakInChatEvent.canSpeak()) {

            if (game.getWerewolfChatHandler().getMessagesCount(playerWW) < game.getConfig().getValue(WerewolfChat.CONFIG)) {
                if (game.getWerewolfChatHandler().isWereWolfChatEnable()) {

                    StringBuilder sb = new StringBuilder();

                    for (String w : args) {
                        sb.append(w).append(" ");
                    }

                    game.getWerewolfChatHandler().addMessage(playerWW, sb.toString());

                    game.getAlivePlayersWW()
                            .forEach(playerWW1 -> {
                                WereWolfChatEvent wereWolfChatEvent = new WereWolfChatEvent(playerWW, playerWW1, sb.toString());
                                Bukkit.getPluginManager().callEvent(wereWolfChatEvent);
                            });
                    Bukkit.getPluginManager().callEvent(new WereWolfSiteChatEvent(playerWW, sb.toString()));
                } else {
                    playerWW.sendMessageWithKey(Prefix.RED, "werewolf.commands.player.ww_chat.timer");
                }
            } else {
                playerWW.sendMessageWithKey(Prefix.RED, "werewolf.commands.player.ww_chat.speak_number",
                        Formatter.number(game.getConfig().getValue(WerewolfChat.CONFIG)));
            }


        } else playerWW.sendMessageWithKey(Prefix.RED, "werewolf.commands.player.ww_chat.not_access");
    }
}
