package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.xml.stream.events.StartElement;
import java.util.Optional;

public class CatEyes extends ListenerManager {


    public CatEyes(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onStartEvent(StartEvent event) {

        this.getGame().getPlayersWW().forEach(playerWW -> {
            playerWW.addPotionModifier(PotionModifier.add(PotionEffectType.NIGHT_VISION,"cat_eyes"));
        });
    }

    @Override
    public void register(boolean isActive) {


        if (isActive) {
            if (!isRegister()) {
                this.getGame().getPlayersWW().forEach(playerWW -> {
                    playerWW.addPotionModifier(PotionModifier.add(PotionEffectType.NIGHT_VISION,"cat_eyes"));
                });
                BukkitUtils.registerEvents(this);
                register = true;
            }
        } else if (isRegister()) {
            register = false;
            HandlerList.unregisterAll(this);

            this.getGame().getPlayersWW().forEach(playerWW -> {
                playerWW.addPotionModifier(PotionModifier.remove(PotionEffectType.NIGHT_VISION,"cat_eyes"));
            });
        }
    }
}
