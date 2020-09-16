package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.StealEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Protector extends RolesWithLimitedSelectionDuration implements AffectedPlayers {

    private final List<UUID> affectedPlayer = new ArrayList<>();
    private UUID last;


    public Protector(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main, game, uuid);
        setPower(false);
    }

    @Override
    public void addAffectedPlayer(UUID uuid) {
        this.affectedPlayer.add(uuid);
        this.last = uuid;
    }

    @Override
    public void removeAffectedPlayer(UUID uuid) {
        this.affectedPlayer.remove(uuid);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<UUID> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {


        if (this.last != null) {
            Player player = Bukkit.getPlayer(this.last);

            if (player != null) {
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                player.sendMessage(game.translate("werewolf.role.protector.no_longer_protected"));
            }
            this.last = null;
        }


        if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());
        setPower(true);

        if (player == null) {
            return;
        }

        player.sendMessage(game.translate("werewolf.role.protector.protection_message", game.getScore().conversion(game.getConfig().getTimerValues().get("werewolf.menu.timers.power_duration"))));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onStealProtection(StealEvent event) {

        if (this.last == null) return;

        if (!event.getKiller().equals(this.last)) return;

        Player player = Bukkit.getPlayer(this.last);


        if (player == null) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));

    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.protector.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.protector.display";
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {


        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();

        if (!uuid.equals(last)) return;

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        event.setCancelled(true);

    }
}