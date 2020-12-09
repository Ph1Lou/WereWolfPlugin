package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Raven extends RolesWithLimitedSelectionDuration implements AffectedPlayers {

    private final List<PlayerWW> affectedPlayer = new ArrayList<>();
    private PlayerWW last;

    public Raven(GetWereWolfAPI main, PlayerWW playerWW, String key) {

        super(main, playerWW, key);
        setPower(false);
    }

    @Override
    public void addAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
        this.last = playerWW;
    }

    @Override
    public void removeAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<PlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        if (last != null) {
            Player player = Bukkit.getPlayer(last.getUUID());

            if (player != null) {
                player.removePotionEffect(PotionEffectType.JUMP);
                player.sendMessage(game.translate("werewolf.role.raven.no_longer_curse"));
            }
            last = null;
        }

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
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
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.raven.description");
    }


    @Override
    public void recoverPower() {

    }


    @EventHandler
    public void onVoteEvent(VoteEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;
        game.getVote().getVotes().put(event.getTargetWW(),
                game.getVote().getVotes()
                        .getOrDefault(event.getTargetWW(),
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
