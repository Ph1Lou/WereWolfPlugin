package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTag;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class NoNameTag extends ListenerManager {
    public NoNameTag(GetWereWolfAPI main) {
        super(main);
    }


    @EventHandler
    public void onUpdateNameTag(UpdatePlayerNameTag event) {
        event.setVisibility(false);
    }

    @Override
    public void register(boolean isActive) {

        if (isActive) {
            if (!isRegister()) {
                Bukkit.getPluginManager().registerEvents(this, (Plugin) main);

                Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(
                        Bukkit.getOnlinePlayers()));
                setRegister(true);
            }
        } else if (isRegister()) {
            setRegister(false);

            HandlerList.unregisterAll(this);
            Bukkit.getPluginManager().callEvent(
                    new UpdateNameTagEvent(Bukkit.getOnlinePlayers()));
        }
    }
}
