package fr.ph1lou.werewolfplugin.utils;

import java.util.UUID;

public class Contributor {

    private final UUID uuid;

    private final int level;

    public Contributor(UUID uuid, int level){
        this.uuid = uuid;
        this.level = level;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "uuid : "+this.uuid+" level : "+this.level;
    }
}
