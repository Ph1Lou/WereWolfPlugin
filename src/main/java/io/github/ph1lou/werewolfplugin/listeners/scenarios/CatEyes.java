package io.github.ph1lou.werewolfplugin.listeners.scenarios;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class CatEyes extends ListenerManager {


    public CatEyes(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    private void onJoinEvent(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        player.removePotionEffect(PotionEffectType.NIGHT_VISION);

        player.addPotionEffect(
                new PotionEffect(PotionEffectType.NIGHT_VISION,
                        Integer.MAX_VALUE,
                        0,
                        false,
                        false));
    }

    @EventHandler
    public void onNight(NightEvent event) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.NIGHT_VISION,
                            Integer.MAX_VALUE,
                            0,
                            false,
                            false));
        });
    }

    @EventHandler
    public void onDay(DayEvent event) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.NIGHT_VISION,
                            Integer.MAX_VALUE,
                            0,
                            false,
                            false));
        });
    }

    @EventHandler
    public void onRepartition(RepartitionEvent event) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.NIGHT_VISION,
                            Integer.MAX_VALUE,
                            0,
                            false,
                            false));
        });
    }

    @EventHandler
    private void onResurrection(ResurrectionEvent event) {

        Player player = Bukkit.getPlayer(event.getPlayerWW().getUUID());

        if (player == null) return;

        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.addPotionEffect(
                new PotionEffect(PotionEffectType.NIGHT_VISION,
                        Integer.MAX_VALUE,
                        0,
                        false,
                        false));
    }

    @Override
    public void register(boolean isActive) {

        WereWolfAPI game = main.getWereWolfAPI();

        if (isActive) {
            if (!isRegister()) {

                Bukkit.getOnlinePlayers()
                        .forEach(player -> {
                            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                            player.addPotionEffect(
                                    new PotionEffect(PotionEffectType.NIGHT_VISION,
                                            Integer.MAX_VALUE,
                                            0,
                                            false,
                                            false));
                        });
                Bukkit.getPluginManager().registerEvents(this, (Plugin) main);
                register = true;
            }
        } else if (isRegister()) {
            register = false;
            HandlerList.unregisterAll(this);

            Bukkit.getOnlinePlayers()
                    .forEach(player -> player.removePotionEffect(PotionEffectType.NIGHT_VISION));

            Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Entity::getUniqueId)
                    .map(game::getPlayerWW)
                    .filter(Objects::nonNull)
                    .forEach(playerWW -> playerWW.getRole().recoverPotionEffect());
        }
    }
}
