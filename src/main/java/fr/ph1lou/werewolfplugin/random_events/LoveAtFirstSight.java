package fr.ph1lou.werewolfplugin.random_events;


import fr.ph1lou.werewolfapi.annotations.RandomEvent;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import org.bukkit.event.EventHandler;

@RandomEvent(key = EventBase.LOVE_AT_FIRST_SIGHT,
        loreKey = "werewolf.random_events.love_at_first_sight.description"
)
public class LoveAtFirstSight extends ListenerWerewolf {

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {

        getGame().getConfig().setConfig(ConfigBase.AMNESIAC_LOVERS, true);
    }

    public LoveAtFirstSight(WereWolfAPI game) {
        super(game);
    }
}
