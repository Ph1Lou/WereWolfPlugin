package fr.ph1lou.werewolfplugin.listeners.configs;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.ListenerManager;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import org.bukkit.Bukkit;

public class RedNameTag extends ListenerManager {

    public RedNameTag(GetWereWolfAPI main) {
        super(main);
    }

    @Override
    public void register(boolean isActive) {
        super.register(isActive);
        Bukkit.getOnlinePlayers().forEach(player -> Bukkit.getPluginManager().callEvent(
                new UpdateNameTagEvent(player)));
    }
}
