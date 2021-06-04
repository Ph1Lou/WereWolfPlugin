package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffectType;

public class CatEyes extends ListenerManager {


    public CatEyes(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onStartEvent(StartEvent event) {

        this.getGame().getPlayersWW().forEach(playerWW -> playerWW.addPotionModifier(PotionModifier.add(PotionEffectType.NIGHT_VISION,"cat_eyes")));
    }

    @Override
    public void register(boolean isActive) {


        if (isActive) {
            if (!isRegister()) {
                this.getGame().getPlayersWW().forEach(playerWW -> playerWW.addPotionModifier(PotionModifier.add(PotionEffectType.NIGHT_VISION,"cat_eyes")));
                BukkitUtils.registerEvents(this);
                register = true;
            }
        } else if (isRegister()) {
            register = false;
            HandlerList.unregisterAll(this);

            this.getGame().getPlayersWW().forEach(playerWW -> playerWW.addPotionModifier(PotionModifier.remove(PotionEffectType.NIGHT_VISION,"cat_eyes")));
        }
    }
}
