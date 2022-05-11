package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

@Timer(key = TimerBase.WEREWOLF_LIST,
        defaultValue = 600,
        meetUpValue = 600,
        decrementAfterRole = true,
        onZero = WereWolfListEvent.class)
public class WerewolfList extends ListenerManager {

    public WerewolfList(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onWerewolfList(WereWolfListEvent event){
        this.getGame().getPlayersWW().stream()
                .filter(playerWW -> !playerWW.isState(StatePlayer.DEATH))
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .forEach(playerWW -> {
                    playerWW.sendMessageWithKey(Prefix.YELLOW , "werewolf.role.werewolf.see_others");
                    Sound.WOLF_HOWL.play(playerWW);
                    Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW));
                });
    }
}
