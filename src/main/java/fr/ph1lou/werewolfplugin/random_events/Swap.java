package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.RandomEvent;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.events.random_events.SwapEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.stream.Collectors;

@RandomEvent(key = EventBase.SWAP, loreKey = "werewolf.random_events.swap.description")
public class Swap extends ListenerWerewolf {

    public Swap(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        if (game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST) <= 1) {
            return;
        }

        int timer = Math.min(
                game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST),
                game.getConfig().getTimerValue(TimerBase.LOVER_DURATION)
        );

        if (timer <= 0) {
            return;
        }

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (isRegister()) {

                List<IPlayerWW> playerWWS = game.getPlayersWW().stream()
                        .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                        .collect(Collectors.toList());

                if (playerWWS.isEmpty()) return;

                IPlayerWW playerWW1 = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

                playerWWS = game.getPlayersWW().stream()
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
                playerWW1.clearPotionEffects(roles1.getKey());
                playerWW2.clearPotionEffects(roles2.getKey());
                playerWW1.clearPotionEffects(RoleBase.WEREWOLF);
                playerWW2.clearPotionEffects(RoleBase.WEREWOLF);
                playerWW1.sendMessageWithKey(Prefix.RED, "werewolf.random_events.swap.concerned");
                playerWW2.sendMessageWithKey(Prefix.RED, "werewolf.random_events.swap.concerned");
                roles1.recoverPower();
                roles2.recoverPower();
                roles1.recoverPotionEffects();
                roles2.recoverPotionEffects();
                playerWW1.getLovers().forEach(iLover -> {
                    if (iLover.swap(playerWW1, playerWW2)) {
                        playerWW2.addLover(iLover);
                        playerWW1.removeLover(iLover);
                    }
                });
                playerWW2.getLovers().forEach(iLover -> {
                    if (iLover.swap(playerWW2, playerWW1)) {
                        playerWW1.addLover(iLover);
                        playerWW2.removeLover(iLover);
                    }
                });

            }
        }, Math.max(0, (long) (game.getRandom().nextDouble() * timer * 20) - 5));
    }

}
