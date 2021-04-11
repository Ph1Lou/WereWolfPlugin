package io.github.ph1lou.werewolfplugin.listeners.random_events;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.lovers.RevealLoversEvent;
import io.github.ph1lou.werewolfapi.events.random_events.TroupleEvent;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfplugin.roles.lovers.Lover;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

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

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (game.isState(StateGame.GAME)) {
                if (isRegister()) {
                    List<Lover> lovers = game.getLoversManager().getLovers().stream()
                            .filter(ILover -> ILover.isKey(LoverType.LOVER.getKey()))
                            .map(ILover -> (Lover) ILover)
                            .collect(Collectors.toList());

                    if (lovers.isEmpty()) return;

                    Lover lover = lovers.get((int) Math.floor(
                            game.getRandom().nextFloat() * lovers.size()));

                    List<IPlayerWW> playerWWS = game.getPlayerWW().stream()
                            .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                            .filter(playerWW -> playerWW.getLovers().isEmpty())
                            .collect(Collectors.toList());

                    if (playerWWS.isEmpty()) return;

                    IPlayerWW playerWW = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

                    TroupleEvent troupleEvent = new TroupleEvent(playerWW, new HashSet<>(lover.getLovers()));
                    Bukkit.getPluginManager().callEvent(troupleEvent);

                    if (troupleEvent.isCancelled()) return;

                    lover.addLover(playerWW);
                    playerWW.addLover(lover);
                    register(false);

                    Bukkit.broadcastMessage(game.translate("werewolf.random_events.triple.message"));
                }
            }
        }, (long) (game.getRandom().nextDouble() * 10 * 60 * 20));
    }


}
