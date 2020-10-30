package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.enumlg.Timers;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Progress;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesNeutral;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Succubus extends RolesNeutral implements Progress, AffectedPlayers, Power {

    private float progress = 0;
    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Succubus(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
    }

    private boolean power=true;
    @Override
    public void setPower(Boolean power) {
        this.power=power;
    }

    @Override
    public Boolean hasPower() {
        return(this.power);
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


    @Override
    public float getProgress() {
        return (this.progress);
    }

    @Override
    public void setProgress(Float progress) {
        this.progress = progress;
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.succubus.description");
    }


    @Override
    public void stolen(@NotNull UUID uuid) {


        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        if (hasPower()) {
            player.sendMessage(game.translate("werewolf.role.succubus.charming_message"));
        } else {
            if (!getAffectedPlayers().isEmpty()) {
                UUID affectedUUID = getAffectedPlayers().get(0);
                Player affected = Bukkit.getPlayer(affectedUUID);
                player.sendMessage(game.translate("werewolf.role.succubus.charming_perform", game.getPlayersWW().get(affectedUUID).getName()));
                if (affected != null) {
                    affected.sendMessage(game.translate("werewolf.role.succubus.get_charmed", game.getPlayersWW().get(getPlayerUUID()).getName()));
                }
            }
        }
    }

    @Override
    public Player recoverPower() {
        Player player = super.recoverPower();
        if(player==null) return null;
        player.sendMessage(game.translate("werewolf.role.succubus.charming_message"));
        return player;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {

        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        if (!plg.isState(StatePlayer.ALIVE)) {
            return;
        }
        if (getAffectedPlayers().isEmpty()) {
            return;
        }

        if (!hasPower()) {
            return;
        }

        UUID playerCharmedUUID = getAffectedPlayers().get(0);
        Player charmed = Bukkit.getPlayer(playerCharmedUUID);
        PlayerWW plc = game.getPlayersWW().get(playerCharmedUUID);

        if (!plc.isState(StatePlayer.ALIVE)) {
            return;
        }

        if (charmed == null) {
            return;
        }



        Location succubusLocation = player.getLocation();
        Location playerLocation = charmed.getLocation();

        if (succubusLocation.distance(playerLocation) > game.getConfig().getDistanceSuccubus()) {
            return;
        }

        float temp = getProgress() + 100f / (game.getConfig().getTimerValues().get(Timers.SUCCUBUS_DURATION.getKey()) + 1);

        setProgress(temp);

        if (temp % 10 > 0 && temp % 10 <= 100f / (game.getConfig().getTimerValues().get(Timers.SUCCUBUS_DURATION.getKey()) + 1)) {
            player.sendMessage(game.translate("werewolf.role.succubus.progress_charm", Math.min(100,Math.floor(temp))));
        }

        if (temp >= 100) {

            CharmEvent charmEvent = new CharmEvent(getPlayerUUID(), playerCharmedUUID);
            Bukkit.getPluginManager().callEvent(charmEvent);

            if (!charmEvent.isCancelled()) {
                Sounds.PORTAL_TRAVEL.play(charmed);
                charmed.sendMessage(game.translate("werewolf.role.succubus.get_charmed", plg.getName()));
                player.sendMessage(game.translate("werewolf.role.succubus.charming_perform", charmed.getName()));
                game.checkVictory();
            } else player.sendMessage(game.translate("werewolf.check.cancel"));

            setProgress(0f);
            setPower(false);

        }
    }
    @EventHandler
    public void onTargetIsStolen(StealEvent event) {


        UUID newUUID = event.getKiller();
        UUID oldUUID = event.getPlayer();
        Player player = Bukkit.getPlayer(getPlayerUUID());
        Player charmed = Bukkit.getPlayer(newUUID);
        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());

        if (!getAffectedPlayers().contains(oldUUID)) return;

        removeAffectedPlayer(oldUUID);
        addAffectedPlayer(newUUID);

        if (charmed != null) {
            charmed.sendMessage(game.translate("werewolf.role.succubus.get_charmed", plg.getName()));
        }
        if (player != null) {
            player.sendMessage(game.translate("werewolf.role.succubus.change", game.getPlayersWW().get(newUUID).getName()));
        }
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {


        UUID uuid = event.getUuid();
        Player player = Bukkit.getPlayer(getPlayerUUID());
        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());

        if (!getAffectedPlayers().contains(uuid)) return;


        if (!plg.isState(StatePlayer.ALIVE)) return;

        clearAffectedPlayer();
        setPower(true);
        setProgress(0f);

        if (player == null) return;

        player.sendMessage(game.translate("werewolf.role.succubus.charming_message"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onThirdDeathEvent(ThirdDeathEvent event) {


        UUID uuid = event.getUuid();
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (event.isCancelled()) return;

        if (!event.getUuid().equals(getPlayerUUID())) return;

        if (getAffectedPlayers().isEmpty()) return;

        if(hasPower()) return;

        UUID targetUUID = getAffectedPlayers().get(0);
        Player target = Bukkit.getPlayer(targetUUID);

        PlayerWW trg = game.getPlayersWW().get(targetUUID);

        if (!trg.isState(StatePlayer.ALIVE)) return;

        SuccubusResurrectionEvent succubusResurrectionEvent = new SuccubusResurrectionEvent(uuid,targetUUID);

        Bukkit.getPluginManager().callEvent(succubusResurrectionEvent);

        if(succubusResurrectionEvent.isCancelled()) {
            if (player != null) {
                player.sendMessage(game.translate("werewolf.check.cancel"));
            }
            return;
        }

        clearAffectedPlayer();
        event.setCancelled(true);

        if (target == null) {
            game.death(targetUUID);
        } else {

            target.damage(10000);
            target.sendMessage(game.translate("werewolf.role.succubus.free_of_succubus"));
        }

        game.resurrection(uuid);
    }

}
