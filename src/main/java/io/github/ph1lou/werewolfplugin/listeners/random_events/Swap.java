package io.github.ph1lou.werewolfplugin.listeners.random_events;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import io.github.ph1lou.werewolfapi.events.random_events.SwapEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.stream.Collectors;

public class Swap extends ListenerManager {

    public Swap(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = main.getWereWolfAPI();

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (game.isState(StateGame.GAME)) {
                if (isRegister()) {

                    List<IPlayerWW> playerWWS = game.getPlayerWW().stream()
                            .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                            .collect(Collectors.toList());

                    if (playerWWS.isEmpty()) return;

                    IPlayerWW playerWW1 = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

                    playerWWS = game.getPlayerWW().stream()
                            .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                            .filter(playerWW -> !playerWW.equals(playerWW1))
                            .collect(Collectors.toList());

                    if (playerWWS.isEmpty()) return;

                    IPlayerWW playerWW2 = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

                    SwapEvent swapEvent = new SwapEvent(playerWW1, playerWW2);
                    Bukkit.getPluginManager().callEvent(swapEvent);

                    if (swapEvent.isCancelled()) return;

                    IRole roles1 = playerWW1.getRole();
                    IRole roles2 = playerWW2.getRole();
                    playerWW1.setRole(roles2);
                    playerWW2.setRole(roles1);
                    Bukkit.broadcastMessage(game.translate("werewolf.random_events.swap.message"));
                    register(false);
                    playerWW1.addPlayerMaxHealth(20 - playerWW1.getMaxHealth());
                    playerWW2.addPlayerMaxHealth(20 - playerWW2.getMaxHealth());
                    playerWW1.getPotionEffects().forEach(playerWW1::removePotionEffect);
                    playerWW2.getPotionEffects().forEach(playerWW1::removePotionEffect);
                    Player player1 = Bukkit.getPlayer(playerWW1.getUUID());
                    Player player2 = Bukkit.getPlayer(playerWW2.getUUID());
                    if (player1 != null) {
                        player1.getActivePotionEffects().forEach(potionEffect -> player1.removePotionEffect(potionEffect.getType()));
                    }
                    if (player2 != null) {
                        player2.getActivePotionEffects().forEach(potionEffect -> player2.removePotionEffect(potionEffect.getType()));
                    }

                    playerWW1.sendMessageWithKey("werewolf.random_events.swap.concerned");
                    playerWW2.sendMessageWithKey("werewolf.random_events.swap.concerned");
                    roles1.recoverPower();
                    roles2.recoverPower();
                    roles1.recoverPotionEffect();
                    roles2.recoverPotionEffect();
                }
            }
        }, (long) (game.getRandom().nextDouble() * game.getConfig().getTimerValue(TimersBase.WEREWOLF_LIST.getKey()) * 20));
    }

}
