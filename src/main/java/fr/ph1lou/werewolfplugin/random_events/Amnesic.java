package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.RandomEvent;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.events.random_events.AmnesicEvent;
import fr.ph1lou.werewolfapi.events.random_events.AmnesicTransformEvent;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RandomEvent(key = EventBase.AMNESIC,
        loreKey = "werewolf.random_events.amnesic.description",
        timers = @Timer(key = Amnesic.TIMER, defaultValue = 300, meetUpValue = 180, step = 30))
public class Amnesic extends ListenerWerewolf {

    public final static String TIMER = "werewolf.random_events.amnesic.timer";
    private final List<IPlayerWW> list = new ArrayList<>();
    private IPlayerWW temp;

    public Amnesic(WereWolfAPI main) {
        super(main);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWereWolfList(WereWolfListEvent event) {

        if (this.temp != null) {
            return;
        }

        WereWolfAPI game = this.getGame();

        List<IPlayerWW> playerWWS = game.getAlivePlayersWW().stream()
                .filter(playerWW -> !playerWW.getRole().isWereWolf())
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return;

        this.temp = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

        AmnesicEvent event1 = new AmnesicEvent(this.temp);

        Bukkit.getPluginManager().callEvent(event1);

        if (event1.isCancelled()) {
            this.temp = null;
            return;
        }

        this.list.add(this.temp);
    }


    @EventHandler
    public void onDamageByWereWolf(EntityDamageByEntityEvent event) {

        if (temp == null) return;

        if (temp.getRole().isWereWolf()) {
            return;
        }

        WereWolfAPI game = this.getGame();

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!temp.getUUID().equals(event.getEntity().getUniqueId())) {
            return;
        }

        IPlayerWW playerWW = game.getPlayerWW(event.getDamager().getUniqueId()).orElse(null);

        if (playerWW == null) {
            return;
        }

        AppearInWereWolfListEvent appearInWereWolfListEvent = new AppearInWereWolfListEvent(temp, playerWW);

        if (!appearInWereWolfListEvent.isAppear()) {
            return;
        }

        AmnesicTransformEvent event1 = new AmnesicTransformEvent(this.temp, playerWW);

        Bukkit.getPluginManager().callEvent(event1);

        if (event1.isCancelled()) {
            return;
        }

        this.temp.getRole().setInfected();
        this.temp.sendMessageWithKey("werewolf.random_events.amnesic.message",
                Formatter.timer(game, TIMER));

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent((Player) event.getEntity()));

        BukkitUtils.scheduleSyncDelayedTask(game, this::revealWereWolf, 20L * game.getConfig().getTimerValue(TIMER));
    }


    private void revealWereWolf() {

        if (this.temp == null) return;

        WereWolfAPI game = this.getGame();

        List<IPlayerWW> playerWWS = game.getAlivePlayersWW().stream()
                .filter(playerWW -> {
                    AppearInWereWolfListEvent appearInWereWolfListEvent = new AppearInWereWolfListEvent(temp, playerWW);
                    return appearInWereWolfListEvent.isAppear();
                })
                .filter(playerWW -> !this.list.contains(playerWW))
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return;

        Collections.shuffle(playerWWS, game.getRandom());

        IPlayerWW playerWW = playerWWS.get(0);

        this.list.add(playerWW);

        Player player = Bukkit.getPlayer(this.temp.getUUID());

        if (player == null) return;

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));

        playerWW.sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.werewolf.new_werewolf");
        Sound.WOLF_HOWL.play(playerWW);

        this.temp.sendMessageWithKey(Prefix.GREEN, "werewolf.random_events.amnesic.new",
                Formatter.player(playerWW.getName()),
                Formatter.timer(game, TIMER));

        BukkitUtils.scheduleSyncDelayedTask(game, this::revealWereWolf, 20L * game.getConfig().getTimerValue(TIMER));

    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAppearInWereWolfList(AppearInWereWolfListEvent event) {

        if (this.temp == null) return;

        if (event.getTargetWW().equals(this.temp)) {
            event.setAppear(this.list.contains(event.getPlayerWW()));
            return;
        }

        if (!event.getPlayerWW().equals(this.temp)) {
            return;
        }

        event.setAppear(this.list.contains(event.getTargetWW()));
    }
}
