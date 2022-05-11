package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.LoverType;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.lovers.RevealLoversEvent;
import fr.ph1lou.werewolfapi.events.random_events.TroupleEvent;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.roles.lovers.Lover;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Event(key = EventBase.TRIPLE, loreKey = "werewolf.random_events.triple.description")
public class Triple extends ListenerManager {

    public Triple(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onRevealLover(RevealLoversEvent event) {
        WereWolfAPI game = this.getGame();

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

                    List<IPlayerWW> playerWWS = game.getPlayersWW().stream()
                            .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                            .filter(playerWW -> playerWW.getLovers().isEmpty())
                            .collect(Collectors.toList());

                    if (playerWWS.isEmpty()) return;

                    IPlayerWW playerWW = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

                    TroupleEvent troupleEvent = new TroupleEvent(playerWW, new HashSet<>(lover.getLovers()));
                    Bukkit.getPluginManager().callEvent(troupleEvent);

                    if (troupleEvent.isCancelled()) return;

                    lover.addLover(playerWW);

                    register(false);
                }
            }
        }, (long) (game.getRandom().nextDouble() * 10 * 60 * 20));
    }


}
