package io.github.ph1lou.werewolfplugin.listeners.random_events;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.RepartitionEvent;
import io.github.ph1lou.werewolfapi.events.SwapEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class Swap extends ListenerManager {

    public Swap(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = main.getWereWolfAPI();

        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
            if (game.isState(StateGame.GAME)) {
                if (isRegister()) {

                    List<PlayerWW> playerWWList = game.getPlayerWW().stream()
                            .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                            .collect(Collectors.toList());

                    if (playerWWList.isEmpty()) return;

                    PlayerWW playerWW1 = playerWWList.get((int) Math.floor(game.getRandom().nextDouble() * playerWWList.size()));

                    playerWWList = game.getPlayerWW().stream()
                            .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                            .filter(playerWW -> !playerWW.equals(playerWW1))
                            .collect(Collectors.toList());

                    if (playerWWList.isEmpty()) return;

                    PlayerWW playerWW2 = playerWWList.get((int) Math.floor(game.getRandom().nextDouble() * playerWWList.size()));

                    SwapEvent swapEvent = new SwapEvent(playerWW1, playerWW2);
                    Bukkit.getPluginManager().callEvent(swapEvent);

                    if (swapEvent.isCancelled()) return;

                    Roles roles1 = playerWW1.getRole();
                    Roles roles2 = playerWW2.getRole();
                    playerWW1.setRole(roles2);
                    playerWW2.setRole(roles1);
                    Bukkit.broadcastMessage(game.translate("werewolf.random_events.swap.message"));
                    register(false);
                    playerWW1.addPlayerMaxHealth(20 - playerWW1.getMaxHealth());
                    playerWW2.addPlayerMaxHealth(20 - playerWW2.getMaxHealth());
                    playerWW1.getPotionEffects().forEach(playerWW1::removePotionEffect);
                    playerWW2.getPotionEffects().forEach(playerWW2::removePotionEffect);
                    playerWW1.sendMessageWithKey("werewolf.random_events.swap.concerned");
                    playerWW2.sendMessageWithKey("werewolf.random_events.swap.concerned");
                    roles1.recoverPower();
                    roles2.recoverPower();
                }
            }
        }, (long) (game.getRandom().nextDouble() * game.getConfig().getTimerValue(TimersBase.WEREWOLF_LIST.getKey()) * 20));
    }

}
