package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.RandomEvent;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.events.random_events.RumorsEvent;
import fr.ph1lou.werewolfapi.events.random_events.RumorsWriteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RandomEvent(key = EventBase.RUMORS, loreKey = "werewolf.random_events.rumors.description",
        timers = {@Timer(key = Rumors.TIMER_START, defaultValue = 80*60, meetUpValue = 30*60, step = 30),
                @Timer(key = Rumors.PERIOD, defaultValue = 40*60, meetUpValue = 20*60, step = 30)})
public class Rumors extends ListenerWerewolf {

    public static final String TIMER_START = "werewolf.random_events.rumors.timer_start";
    public static final String PERIOD = "werewolf.random_events.rumors.period";
    private boolean active = false;

    private final Map<IPlayerWW, String> rumors = new HashMap<>();

    public Rumors(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            if (isRegister()) {
                RumorsEvent rumorEvent = new RumorsEvent();
                Bukkit.getPluginManager().callEvent(rumorEvent);

                if (rumorEvent.isCancelled()) return;

                active = true;

                TextComponent textComponent = new TextComponent(
                        game.translate(
                                "werewolf.random_events.rumors.message"));
                textComponent.setClickEvent(
                        new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                String.format("/ww %s",
                                        game.translate("werewolf.random_events.rumors.command"))));
                Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(textComponent));

                BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                    if (isRegister()) {
                        active = false;
                        register(false);
                        List<String> rumors = new ArrayList<>(this.rumors.values());

                        if(rumors.size() == 0){
                            return;
                        }

                        Collections.shuffle(rumors, game.getRandom());

                        Bukkit.broadcastMessage(game.translate("werewolf.random_events.rumors.rumors_announcement",
                                Formatter.format("&rumors&", String.join("\n", rumors))));
                    }
                }, 20L * 60);
            }
        }, (long) (20L * game.getConfig().getTimerValue(TIMER_START) +
                game.getRandom().nextDouble() * 20 * game.getConfig().getTimerValue(PERIOD)));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerWriteMessage(RumorsWriteEvent event) {

        if (!active) return;

        if(event.isCancelled()){
            return;
        }

        this.rumors.put(event.getPlayerWW(), event.getMessage());

    }
}
