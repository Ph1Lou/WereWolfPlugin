package fr.ph1lou.werewolfplugin.random_events;


import fr.ph1lou.werewolfapi.annotations.RandomEvent;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import org.bukkit.event.EventHandler;

@RandomEvent(key = EventBase.CUPID_INDECISIVE,
        loreKey = "werewolf.random_events.cupid_indecisive.description"
)
public class CupidIndecisive extends ListenerWerewolf {

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {

        getGame().getConfig().switchConfigValue(ConfigBase.RANDOM_CUPID);
    }

    public CupidIndecisive(WereWolfAPI game) {
        super(game);
    }
}
