package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.events.game.timers.BorderStartEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

@Timer(key = TimerBase.BORDER_BEGIN,
        defaultValue = 60 * 60,
        meetUpValue = 30 * 60,
        decrement = true,
        onZero = BorderStartEvent.class)
public class BorderBegin extends ListenerWerewolf {

    public BorderBegin(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onBorderStart(BorderStartEvent event) {
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    player.sendMessage(this.getGame().translate(Prefix.ORANGE, "werewolf.announcement.border"));
                    Sound.FIREWORK_LAUNCH.play(player);
                });
    }
}
