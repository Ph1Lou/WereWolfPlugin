package fr.ph1lou.werewolfplugin.random_events;

import fr.ph1lou.werewolfapi.annotations.Event;
import fr.ph1lou.werewolfapi.basekeys.EventBase;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.events.random_events.AmnesicEvent;
import fr.ph1lou.werewolfapi.events.random_events.AmnesicTransformEvent;
import fr.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Event(key = EventBase.AMNESIC, loreKey = "werewolf.random_events.amnesic.description")
public class Amnesic extends ListenerManager {

    final List<UUID> list = new ArrayList<>();
    private IPlayerWW temp;

    public Amnesic(GetWereWolfAPI main) {
        super(main);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onWereWolfList(WereWolfListEvent event) {

        if (this.temp != null) {
            return;
        }

        WereWolfAPI game = this.getGame();

        List<IPlayerWW> playerWWS = game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
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

        this.list.add(this.temp.getUUID());
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

        if (!playerWW.getRole().isWereWolf()) {
            return;
        }

        AmnesicTransformEvent event1 = new AmnesicTransformEvent(this.temp, playerWW);

        Bukkit.getPluginManager().callEvent(event1);

        if (event1.isCancelled()) {
            return;
        }

        this.temp.getRole().setInfected();
        this.temp.sendMessageWithKey("werewolf.random_events.amnesic.message");

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent((Player) event.getEntity()));

        BukkitUtils.scheduleSyncDelayedTask(() -> {
            if (!game.isState(StateGame.END)) {
                this.revealWereWolf();
            }
        }, 20 * 60 * 5L);
    }


    private void revealWereWolf() {

        if (this.temp == null) return;

        WereWolfAPI game = this.getGame();

        List<UUID> playerWWS = game.getPlayersWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .map(IPlayerWW::getUUID)
                .filter(uuid -> !this.list.contains(uuid))
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return;

        Collections.shuffle(playerWWS, game.getRandom());

        UUID uuid = playerWWS.get(0);
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        this.list.add(uuid);

        Player player = Bukkit.getPlayer(this.temp.getUUID());

        if (player == null) return;

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));

        playerWW.sendMessageWithKey(Prefix.ORANGE , "werewolf.role.werewolf.new_werewolf");
        Sound.WOLF_HOWL.play(playerWW);

        this.temp.sendMessageWithKey(Prefix.GREEN , "werewolf.random_events.amnesic.new",
                Formatter.player(playerWW.getName()));

        BukkitUtils.scheduleSyncDelayedTask(this::revealWereWolf, 20 * 60 * 5L);

    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAppearInWereWolfList(AppearInWereWolfListEvent event) {

        if (this.temp == null) return;

        if (event.getPlayerUUID().equals(this.temp.getUUID())) {
            event.setAppear(this.list.contains(event.getRequesterUUID()));
            return;
        }

        if (!event.getRequesterUUID().equals(this.temp.getUUID())) {
            return;
        }

        event.setAppear(this.list.contains(event.getPlayerUUID()));
    }


    @EventHandler
    public void onGameStop(StopEvent event) {
        temp = null;
        this.list.clear();
    }

    @EventHandler
    public void onGameStart(StartEvent event) {
        temp = null;
        this.list.clear();
    }
}
