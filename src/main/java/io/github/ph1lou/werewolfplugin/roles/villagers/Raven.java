package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.enumlg.TimersBase;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.VoteEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Raven extends RolesWithLimitedSelectionDuration implements AffectedPlayers {

    private final List<UUID> affectedPlayer = new ArrayList<>();
    private UUID last;

    public Raven(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {

        super(main,game,uuid, key);
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

        if (last != null) {
            Player player = Bukkit.getPlayer(last);

            if (player != null) {
                player.removePotionEffect(PotionEffectType.JUMP);
                player.sendMessage(game.translate("werewolf.role.raven.no_longer_curse"));
            }
            last = null;
        }

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());
        setPower(true);

        if (player == null) {
            return;
        }

        player.sendMessage(game.translate("werewolf.role.raven.curse_message",
                game.getScore().conversion(
                        game.getConfig()
                                .getTimerValues()
                                .get(TimersBase.POWER_DURATION.getKey()))));
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.raven.description");
    }


    @EventHandler
    public void onVoteEvent(VoteEvent event) {

        if (!event.getPlayerUUID().equals(getPlayerUUID())) return;
        game.getVote().getVotes().put(event.getTargetUUID(),
                game.getVote().getVotes()
                        .getOrDefault(event.getTargetUUID(),
                                0
                        ) + 1);
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();

        if (!getPlayerUUID().equals(uuid)) return;


        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            event.setCancelled(true);
        }
    }
}
