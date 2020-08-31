package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.VoteEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Raven extends RolesWithLimitedSelectionDuration implements AffectedPlayers {

    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Raven(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {

        super(main,game,uuid);
        setPower(false);
    }

    @Override
    public void addAffectedPlayer(UUID uuid) {
        this.affectedPlayer.add(uuid);
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

    @EventHandler
    public void onDay(DayEvent event) {

        getPlayerUUID();

        if(!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());
        setPower(true);

        if (player == null) {
            return;
        }


        player.sendMessage(game.translate("werewolf.role.raven.curse_message", game.getScore().conversion(game.getConfig().getTimerValues().get("werewolf.menu.timers.power_duration"))));
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.raven.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.raven.display";
    }


    @EventHandler
    public void onVoteEvent(VoteEvent event){

        if(!event.getVoterUUID().equals(getPlayerUUID())) return;
        game.getVote().getVotes().put(event.getVoteUUID(),game.getVote().getVotes().get(event.getVoteUUID())+1);
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {

        getPlayerUUID();

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();


        if (!getPlayerUUID().equals(uuid)) return;


        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            event.setCancelled(true);
        }
    }
}
