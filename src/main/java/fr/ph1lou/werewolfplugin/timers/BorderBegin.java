package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.game.timers.BorderStartEvent;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

@Timer(key = TimerBase.BORDER_BEGIN,
        defaultValue = 3600,
        meetUpValue = 30 * 60,
        decrement = true,
        onZero = BorderStartEvent.class)
public class BorderBegin extends ListenerManager {

    public BorderBegin(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onBorderStart(BorderStartEvent event){
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    player.sendMessage(this.getGame().translate(Prefix.ORANGE , "werewolf.announcement.border"));
                    Sound.FIREWORK_LAUNCH.play(player);
                });
    }
}
