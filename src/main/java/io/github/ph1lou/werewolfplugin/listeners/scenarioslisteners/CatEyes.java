package io.github.ph1lou.werewolfplugin.listeners.scenarioslisteners;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.Scenarios;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.ResurrectionEvent;
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

public class CatEyes extends Scenarios {


    public CatEyes(GetWereWolfAPI main, WereWolfAPI game, String key) {
        super(main, game, key);
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
    private void onResurrection(ResurrectionEvent event) {

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

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
    public void register() {

        if (game.getConfig().getScenarioValues().get(scenarioID)) {
            if (!register) {

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
        } else {
            if (register) {
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
}
