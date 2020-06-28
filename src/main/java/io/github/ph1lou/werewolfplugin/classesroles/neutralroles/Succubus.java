package io.github.ph1lou.werewolfplugin.classesroles.neutralroles;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.TimerLG;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Progress;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesNeutral;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Succubus extends RolesNeutral implements Progress, AffectedPlayers, Power {

    private float progress = 0;
    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Succubus(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
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
    public String getDisplay() {
        return "werewolf.role.succubus.display";
    }

    @Override
    public void stolen(UUID uuid) {

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player =Bukkit.getPlayer(getPlayerUUID());

        if (hasPower()) {
            player.sendMessage(game.translate("werewolf.role.succubus.charming_message"));
        } else {
            if (!getAffectedPlayers().isEmpty()) {
                UUID affectedUUID=getAffectedPlayers().get(0);
                player.sendMessage(game.translate("werewolf.role.succubus.charming_perform", game.getPlayersWW().get(affectedUUID).getName()));
                if(Bukkit.getPlayer(affectedUUID)!=null){
                    Bukkit.getPlayer(affectedUUID).sendMessage(game.translate("werewolf.role.succubus.get_charmed",game.getPlayersWW().get(getPlayerUUID()).getName()));
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

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());

        if(!plg.isState(State.ALIVE)){
            return;
        }
        if(getAffectedPlayers().isEmpty()){
            return;
        }

        if(!hasPower()){
            return;
        }

        UUID playerCharmedUUID = getAffectedPlayers().get(0);
        PlayerWW plc = game.getPlayersWW().get(playerCharmedUUID);

        if (!plc.isState(State.ALIVE)) {
            return;
        }

        if(Bukkit.getPlayer(playerCharmedUUID) == null){
            return;
        }

        Player charmed = Bukkit.getPlayer(playerCharmedUUID);
        Player player = Bukkit.getPlayer(getPlayerUUID());

        Location succubusLocation = player.getLocation();
        Location playerLocation = charmed.getLocation();

        if (succubusLocation.distance(playerLocation) > game.getConfig().getDistanceSuccubus()) {
            return;
        }

        float temp = getProgress() + 100f / (game.getConfig().getTimerValues().get(TimerLG.SUCCUBUS_DURATION) + 1);

        setProgress(temp);

        if (temp % 10 > 0 && temp % 10 <= 100f / (game.getConfig().getTimerValues().get(TimerLG.SUCCUBUS_DURATION) + 1)) {
            player.sendMessage(game.translate("werewolf.role.succubus.progress_charm", Math.min(100,Math.floor(temp))));
        }

        if (temp >= 100) {

            charmed.playSound(charmed.getLocation(), Sound.PORTAL_TRAVEL, 1, 20);
            charmed.sendMessage(game.translate("werewolf.role.succubus.get_charmed", plg.getName()));
            player.sendMessage(game.translate("werewolf.role.succubus.charming_perform", charmed.getName()));
            setProgress(0f);
            setPower(false);
            Bukkit.getPluginManager().callEvent(new CharmEvent(getPlayerUUID(),playerCharmedUUID));
            game.checkVictory();
        }
    }
    @EventHandler
    public void onTargetIsStolen(StealEvent event){

        UUID newUUID = event.getNewUUID();
        UUID oldUUID = event.getOldUUID();
        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());

        if(!getAffectedPlayers().contains(oldUUID)) return;

        removeAffectedPlayer(oldUUID);
        addAffectedPlayer(newUUID);

        if(Bukkit.getPlayer(newUUID)!=null){
            Player player = Bukkit.getPlayer(newUUID);
            player.sendMessage(game.translate("werewolf.role.succubus.get_charmed", plg.getName()));
        }
        if(Bukkit.getPlayer(getPlayerUUID())!=null) {
            Player player = Bukkit.getPlayer(getPlayerUUID());
            player.sendMessage(game.translate("werewolf.role.succubus.change",game.getPlayersWW().get(newUUID).getName()));
        }
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        UUID uuid=event.getUuid();

        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());

        if(!getAffectedPlayers().contains(uuid)) return;


        if (!plg.isState(State.ALIVE)) return;

        clearAffectedPlayer();
        setPower(true);
        setProgress(0f);

        if(Bukkit.getPlayer(getPlayerUUID())==null) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());
        player.sendMessage(game.translate("werewolf.role.succubus.charming_message"));
    }

    @EventHandler
    public void onFirstDeathEvent(FirstDeathEvent event) {

        UUID uuid = event.getUuid();

        if(event.isCancelled()) return;

        if (!event.getUuid().equals(getPlayerUUID())) return;

        if(getAffectedPlayers().isEmpty()) return;

        if(hasPower()) return;

        UUID targetUUID = getAffectedPlayers().get(0);

        PlayerWW trg = game.getPlayersWW().get(targetUUID);

        if (!trg.isState(State.ALIVE)) return;

        SuccubusResurrectionEvent succubusResurrectionEvent = new SuccubusResurrectionEvent(uuid,targetUUID);

        Bukkit.getPluginManager().callEvent(succubusResurrectionEvent);

        if(succubusResurrectionEvent.isCancelled()){
            if(Bukkit.getPlayer(getPlayerUUID())!=null){
                Bukkit.getPlayer(getPlayerUUID()).sendMessage(game.translate("werewolf.check.cancel"));
            }
            return;
        }

        clearAffectedPlayer();
        event.setCancelled(true);

        if (Bukkit.getPlayer(targetUUID) == null) {
            game.death(targetUUID);
        } else {
            Player target = Bukkit.getPlayer(targetUUID);
            target.damage(10000);
            target.sendMessage(game.translate("werewolf.role.succubus.free_of_succubus"));
        }

        game.resurrection(uuid);
    }

}
