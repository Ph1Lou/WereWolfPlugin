package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.TrollLoverEvent;
import fr.ph1lou.werewolfapi.events.lovers.LoversRepartitionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.game.LoversManagement;
import fr.ph1lou.werewolfplugin.roles.lovers.FakeLover;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Timer(key =  TimerBase.LOVER_DURATION,
        defaultValue = 240,
        meetUpValue = 240,
        decrementAfterRole = true)
public class LoverDuration extends ListenerManager {


    public LoverDuration(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLover(LoversRepartitionEvent event){
        if (this.getGame().getConfig().isTrollLover()) {
            Bukkit.getPluginManager().callEvent(new TrollLoverEvent());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoverRepartition(LoversRepartitionEvent event){
        ((LoversManagement)this.getGame().getLoversManager()).repartition();
    }

    @EventHandler
    public void onTrollLover(TrollLoverEvent event) {

        WereWolfAPI game = this.getGame();

        List<ILover> loverAPIS = new ArrayList<>();

        List<IPlayerWW> playerWWS = game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return;

        while (playerWWS.size() > 3) {
            loverAPIS.add(new FakeLover(game,
                    Arrays.asList(playerWWS.remove(0),
                            playerWWS.remove(0))));
        }
        if (playerWWS.size() == 3) {
            loverAPIS.add(new FakeLover(game,
                    Arrays.asList(playerWWS.remove(0),
                            playerWWS.remove(0),
                            playerWWS.remove(0))));
        } else if (playerWWS.size() == 2) {
            loverAPIS.add(new FakeLover(game,
                    Arrays.asList(playerWWS.remove(0),
                            playerWWS.remove(0))));
        }

        loverAPIS.forEach(BukkitUtils::registerListener);
        loverAPIS.forEach(iLover -> ((FakeLover)iLover).announceLovers());

        BukkitUtils.scheduleSyncDelayedTask(() -> {

            if (!game.isState(StateGame.END)) {
                loverAPIS.forEach(HandlerList::unregisterAll);
                game.getPlayersWW()
                        .forEach(playerWW -> {
                            playerWW
                                    .sendMessageWithKey(Prefix.GREEN , "werewolf.announcement.lover_troll");
                            loverAPIS.forEach(((IPlayerWW)playerWW)::removeLover);
                        });
                game.getConfig().setTrollLover(false);
                Bukkit.getPluginManager().callEvent(new LoversRepartitionEvent());
            }

        }, 1200L);
    }
}
