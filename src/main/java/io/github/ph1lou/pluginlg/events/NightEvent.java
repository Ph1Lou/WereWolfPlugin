package io.github.ph1lou.pluginlg.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class NightEvent extends Event {

    private final UUID uuid;
    private final int number;

    private static final HandlerList HANDLERS_LIST = new HandlerList();


    public NightEvent(UUID uuid, int number){
        this.uuid = uuid;
        this.number = number;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public int getNumber() {
        return number;
    }

    public UUID getUuid() {
        return uuid;
    }
}
