package fr.ph1lou.werewolfplugin.game;

import fr.ph1lou.werewolfapi.game.IWerewolfChatHandler;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WerewolfChatHandler implements IWerewolfChatHandler {

    private final Map<UUID, List<String>> messageSend = new HashMap<>();
    private boolean enable = false;

    @Override
    public void enableWereWolfChat() {
        this.enable = true;
        this.messageSend.clear();
    }

    @Override
    public void disableWereWolfChat() {
        this.enable = false;
    }

    @Override
    public boolean isWereWolfChatEnable() {
        return this.enable;
    }

    @Override
    public int getMessagesCount(IPlayerWW playerWW) {
        return this.messageSend.getOrDefault(playerWW.getUUID(),
                new ArrayList<>()).size();
    }

    @Override
    public List<String> getMessages(IPlayerWW playerWW) {
        return this.messageSend.getOrDefault(playerWW.getUUID(),
                new ArrayList<>());
    }

    @Override
    public void addMessage(IPlayerWW playerWW, String message) {
        List<String> messages = this.messageSend.getOrDefault(playerWW.getUUID(), new ArrayList<>());
        messages.add(message);
        this.messageSend.put(playerWW.getUUID(), messages);
    }
}
