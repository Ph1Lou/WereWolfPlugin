package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import org.bukkit.Bukkit;

@Configuration(key = ConfigBase.RED_NAME_TAG, defaultValue = true, meetUpValue = true)
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
