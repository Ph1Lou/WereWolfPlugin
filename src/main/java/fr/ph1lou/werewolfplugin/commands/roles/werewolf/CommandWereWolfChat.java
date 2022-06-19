package fr.ph1lou.werewolfplugin.commands.roles.werewolf;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfCanSpeakInChatEvent;
import fr.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfplugin.configs.WerewolfChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@PlayerCommand(key = "werewolf.commands.player.ww_chat.command",
        descriptionKey = "werewolf.commands.player.ww_chat.description",
        statesPlayer = StatePlayer.ALIVE,
        statesGame = StateGame.GAME)
public class CommandWereWolfChat implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        if (!game.getConfig().isConfigActive(ConfigBase.WEREWOLF_CHAT)) {
            playerWW.sendMessageWithKey(Prefix.RED , "werewolf.commands.player.ww_chat.disable");
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

                    WereWolfChatEvent wereWolfChatEvent = new WereWolfChatEvent(playerWW, sb.toString());
                    Bukkit.getPluginManager().callEvent(wereWolfChatEvent);
                } else {
                    playerWW.sendMessageWithKey(Prefix.RED , "werewolf.commands.player.ww_chat.timer");
                }
            } else {
                playerWW.sendMessageWithKey(Prefix.RED , "werewolf.commands.player.ww_chat.speak_number",
                        Formatter.number(game.getConfig().getValue(WerewolfChat.CONFIG)));
            }


        } else playerWW.sendMessageWithKey(Prefix.RED , "werewolf.commands.player.ww_chat.not_access");
    }
}
