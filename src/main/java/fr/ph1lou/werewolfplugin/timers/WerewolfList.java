package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

@Timer(key = TimerBase.WEREWOLF_LIST,
        defaultValue = 60 * 10,
        meetUpValue = 60 * 5,
        decrementAfterRole = true,
        onZero = WereWolfListEvent.class)
public class WerewolfList extends ListenerWerewolf {

    public WerewolfList(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onWerewolfList(WereWolfListEvent event){
        this.getGame().getPlayersWW().stream()
                .filter(playerWW -> !playerWW.isState(StatePlayer.DEATH))
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .forEach(playerWW -> {
                    playerWW.sendMessageWithKey(Prefix.YELLOW , "werewolf.roles.werewolf.see_others");
                    Sound.WOLF_HOWL.play(playerWW);
                    Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW));
                });
    }
}
