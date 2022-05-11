package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import fr.ph1lou.werewolfapi.events.random_events.RumorsEvent;
import fr.ph1lou.werewolfapi.events.random_events.RumorsWriteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
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

@Event(key = EventBase.RUMORS, loreKey = "werewolf.random_events.rumors.description")
public class Rumors extends ListenerManager {

    private boolean active = false;

    private final Map<IPlayerWW, String> rumors = new HashMap<>();

    public Rumors(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = this.getGame();

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (game.isState(StateGame.GAME)) {
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

                    BukkitUtils.scheduleSyncDelayedTask(() -> {
                        if (game.isState(StateGame.GAME)) {
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
                        }
                    }, 20L * 60);
                }
            }
        }, (long) (20 * 60 * 80 + game.getRandom().nextDouble() * 40 * 60 * 40));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerWriteMessage(RumorsWriteEvent event) {

        if (!active) return;

        if(event.isCancelled()){
            return;
        }

        this.rumors.put(event.getPlayerWW(), event.getMessage());

    }

    @EventHandler
    public void onGameStop(StopEvent event) {
        active = false;
        rumors.clear();
    }

    @EventHandler
    public void onGameStart(StartEvent event) {
        active = false;
        rumors.clear();
    }

    public boolean isActive() {
        return active;
    }


}
