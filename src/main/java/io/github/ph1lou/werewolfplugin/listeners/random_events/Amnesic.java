package io.github.ph1lou.werewolfplugin.listeners.random_events;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.AppearInWereWolfListEvent;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
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

public class Amnesic extends ListenerManager {

    List<UUID> list = new ArrayList<>();
    private IPlayerWW temp;

    public Amnesic(GetWereWolfAPI main) {
        super(main);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onWereWolfList(WereWolfListEvent event) {

        if (this.temp != null) {
            return;
        }

        WereWolfAPI game = main.getWereWolfAPI();

        List<IPlayerWW> playerWWS = game.getPlayerWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().isWereWolf())
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return;

        this.temp = playerWWS.get((int) Math.floor(game.getRandom().nextDouble() * playerWWS.size()));

        this.list.add(this.temp.getUUID());
    }


    @EventHandler
    public void onDamageByWereWolf(EntityDamageByEntityEvent event) {

        if (temp == null) return;

        if (temp.getRole().isWereWolf()) {
            return;
        }

        WereWolfAPI game = main.getWereWolfAPI();

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!temp.getUUID().equals(event.getEntity().getUniqueId())) {
            return;
        }

        IPlayerWW playerWW = game.getPlayerWW(event.getDamager().getUniqueId());

        if (playerWW == null) {
            return;
        }

        if (!playerWW.getRole().isWereWolf()) {
            return;
        }

        this.temp.getRole().setInfected();

        this.temp.sendMessageWithKey("werewolf.random_events.amnesic.message");

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent((Player) event.getEntity()));

        BukkitUtils.scheduleSyncDelayedTask(this::revealWereWolf, 20 * 60 * 5L);
    }


    private void revealWereWolf() {

        if (this.temp == null) return;

        WereWolfAPI game = main.getWereWolfAPI();

        List<UUID> playerWWS = game.getPlayerWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .map(IPlayerWW::getUUID)
                .filter(uuid -> !this.list.contains(uuid))
                .collect(Collectors.toList());

        if (playerWWS.isEmpty()) return;

        Collections.shuffle(playerWWS, game.getRandom());

        UUID uuid = playerWWS.get(0);
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        this.list.add(uuid);

        Player player = Bukkit.getPlayer(this.temp.getUUID());

        if (player == null) return;

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));

        playerWW.sendMessageWithKey("werewolf.role.werewolf.new_werewolf");
        Sound.WOLF_HOWL.play(playerWW);

        this.temp.sendMessageWithKey("werewolf.random_events.amnesic.new", playerWW.getName());

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
