package fr.ph1lou.werewolfplugin.utils;

import net.md_5.bungee.api.chat.TextComponent;

public class MessageAction {

    private TextComponent messageComponent;
    private String messageString = "";

    public MessageAction(TextComponent message) {
        this.messageComponent = message;
    }

    public MessageAction(String message) {
        this.messageString = message;
    }

    public boolean isMessageComponent() {
        return this.messageComponent != null;
    }

    public String getMessageString() {
        return messageString;
    }

    public TextComponent getMessageComponent() {
        return messageComponent;
    }

}
