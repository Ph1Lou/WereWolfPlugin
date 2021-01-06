package io.github.ph1lou.werewolfplugin.listeners.random_events;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.RevealLoversEvent;
import io.github.ph1lou.werewolfapi.events.TroupleEvent;
import io.github.ph1lou.werewolfplugin.roles.lovers.Lover;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Triple extends ListenerManager {


    public Triple(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onRevealLover(RevealLoversEvent event) {
        WereWolfAPI game = main.getWereWolfAPI();

        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
            if (game.isState(StateGame.GAME)) {
                if (isRegister()) {
                    List<Lover> lovers = game.getLoversManager().getLovers().stream()
                            .filter(loverAPI -> loverAPI.isKey(LoverType.LOVER.getKey()))
                            .map(loverAPI -> (Lover) loverAPI)
                            .collect(Collectors.toList());

                    Lover lover = lovers.get((int) Math.floor(
                            game.getRandom().nextFloat() * lovers.size()));

                    List<PlayerWW> playerWWList = game.getPlayerWW().stream()
                            .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                            .filter(playerWW -> playerWW.getLovers().isEmpty())
                            .collect(Collectors.toList());

                    if (playerWWList.isEmpty()) return;

                    PlayerWW playerWW = playerWWList.get((int) Math.floor(game.getRandom().nextDouble() * playerWWList.size()));

                    TroupleEvent troupleEvent = new TroupleEvent(playerWW, new HashSet<>(lover.getLovers()));
                    Bukkit.getPluginManager().callEvent(troupleEvent);

                    if (troupleEvent.isCancelled()) return;

                    lover.addLover(playerWW);
                    playerWW.addLover(lover);

                    Bukkit.broadcastMessage(game.translate("werewolf.random_events.triple.message"));
                }
            }
        }, (long) (game.getRandom().nextDouble() * 10 * 60 * 20));
    }


}
