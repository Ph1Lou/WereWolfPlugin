package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class NoNameTag extends ListenerManager {
    public NoNameTag(GetWereWolfAPI main) {
        super(main);
    }


    @EventHandler
    public void onUpdateNameTag(UpdatePlayerNameTagEvent event) {
        event.setVisibility(false);
    }

    @Override
    public void register(boolean isActive) {

        super.register(isActive);
        Bukkit.getOnlinePlayers().forEach(player -> Bukkit.getPluginManager().callEvent(
                new UpdateNameTagEvent(player)));
    }
}
