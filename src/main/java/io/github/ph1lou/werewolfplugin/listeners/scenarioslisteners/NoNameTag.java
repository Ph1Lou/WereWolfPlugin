package io.github.ph1lou.werewolfplugin.listeners.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTag;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class NoNameTag extends Scenarios {
    public NoNameTag(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game, key);
    }


    @EventHandler
    public void onUpdateNameTag(UpdatePlayerNameTag event) {
        event.setVisibility(false);
    }

    @Override
    public void register() {

        if (game.getConfig().getScenarioValues().get(scenarioID)) {
            if (!register) {
                Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(
                        Bukkit.getOnlinePlayers()));

                Bukkit.getPluginManager().registerEvents(this, (Plugin) main);
                register = true;
            }
        } else {
            if (register) {
                register = false;
                HandlerList.unregisterAll(this);
                Bukkit.getPluginManager().callEvent(
                        new UpdateNameTagEvent(Bukkit.getOnlinePlayers()));
            }
        }
    }
}
