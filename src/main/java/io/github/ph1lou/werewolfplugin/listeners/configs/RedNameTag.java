package io.github.ph1lou.werewolfplugin.listeners.configs;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import org.bukkit.Bukkit;

public class RedNameTag extends ListenerManager {

    public RedNameTag(GetWereWolfAPI main) {
        super(main);
    }

    @Override
    public void register(boolean isActive) {
        super.register(isActive);
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(Bukkit.getOnlinePlayers()));
    }
}
