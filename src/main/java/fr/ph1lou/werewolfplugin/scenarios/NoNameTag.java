package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

@Scenario(key = ScenarioBase.NO_NAME_TAG)
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
