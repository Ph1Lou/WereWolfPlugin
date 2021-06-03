package io.github.ph1lou.werewolfplugin.listeners.random_events;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.random_events.PoorlyGroomedBearEvent;
import io.github.ph1lou.werewolfapi.events.roles.bear_trainer.GrowlEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Optional;

public class PoorlyGroomedBear extends ListenerManager {
    public PoorlyGroomedBear(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGrowl(GrowlEvent event) {

        WereWolfAPI game = this.getGame();

        int modification = game.getRandom().nextFloat() < 0.5 ? 1 : -1;

        PoorlyGroomedBearEvent event1 = new PoorlyGroomedBearEvent(modification);

        Bukkit.getPluginManager().callEvent(event1);

        if (event1.isCancelled()) {
            return;
        }


        if (modification == -1) {
            Optional<IPlayerWW> removedPlayer = event.getPlayerWWS().stream().findFirst();
            removedPlayer.ifPresent(playerWW -> event.getPlayerWWS().remove(playerWW));
        } else {
            Optional<? extends IPlayerWW> addedPlayer = game.getPlayersWW().stream()
                    .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                    .filter(playerWW -> !event.getPlayerWWS().contains(playerWW))
                    .findFirst();
            addedPlayer.ifPresent(playerWW -> event.getPlayerWWS().add(playerWW));
        }
    }
}
