package io.github.ph1lou.pluginlg.classesroles.neutralroles;


import io.github.ph1lou.pluginlg.classesroles.AffectedPlayers;
import io.github.ph1lou.pluginlg.classesroles.Power;
import io.github.ph1lou.pluginlg.classesroles.Progress;
import io.github.ph1lou.pluginlg.events.TargetDeathEvent;
import io.github.ph1lou.pluginlg.events.TargetStealEvent;
import io.github.ph1lou.pluginlg.events.UpdateEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Succubus extends RolesNeutral implements Progress, AffectedPlayers, Power{

    private float progress = 0;
    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Succubus(GameManager game, UUID uuid) {
        super(game,uuid);
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
    public RoleLG getRoleEnum() {
        return RoleLG.SUCCUBUS;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.succubus.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.succubus.display");
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
                player.sendMessage(game.translate("werewolf.role.succubus.charming_perform", game.playerLG.get(affectedUUID).getName()));
                if(Bukkit.getPlayer(affectedUUID)!=null){
                    Bukkit.getPlayer(affectedUUID).sendMessage(game.translate("werewolf.role.succubus.get_charmed",game.playerLG.get(getPlayerUUID()).getName()));
                }
            }
        }
    }

    @Override
    public void recoverPower(Player player) {
        player.sendMessage(game.translate("werewolf.role.succubus.charming_message"));
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }
        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        PlayerLG plg = game.playerLG.get(getPlayerUUID());

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
        PlayerLG plc = game.playerLG.get(playerCharmedUUID);

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

        if (succubusLocation.distance(playerLocation) > game.config.getDistanceSuccubus()) {
            return;
        }

        float temp = getProgress() + 100f / (game.config.getTimerValues().get(TimerLG.SUCCUBUS_DURATION) + 1);

        setProgress(temp);

        if (temp % 10 > 0 && temp % 10 <= 100f / (game.config.getTimerValues().get(TimerLG.SUCCUBUS_DURATION) + 1)) {
            player.sendMessage(game.translate("werewolf.role.succubus.progress_charm", Math.min(100,Math.floor(temp))));
        }

        if (temp >= 100) {

            charmed.playSound(charmed.getLocation(), Sound.PORTAL_TRAVEL, 1, 20);
            charmed.sendMessage(game.translate("werewolf.role.succubus.get_charmed", plg.getName()));
            player.sendMessage(game.translate("werewolf.role.succubus.charming_perform", charmed.getName()));
            setProgress(0f);
            setPower(false);
            game.endlg.check_victory();
        }
    }
    @EventHandler
    public void onTargetIsStolen(TargetStealEvent event){

        UUID newUUID = event.getNewUUID();
        UUID oldUUID = event.getOldUUID();
        PlayerLG plg = game.playerLG.get(getPlayerUUID());

        if(!getAffectedPlayers().contains(oldUUID)) return;

        removeAffectedPlayer(oldUUID);
        addAffectedPlayer(newUUID);

        if(Bukkit.getPlayer(newUUID)!=null){
            Player player = Bukkit.getPlayer(newUUID);
            player.sendMessage(game.translate("werewolf.role.succubus.get_charmed", plg.getName()));
        }
        if(Bukkit.getPlayer(getPlayerUUID())!=null) {
            Player player = Bukkit.getPlayer(getPlayerUUID());
            player.sendMessage(game.translate("werewolf.role.succubus.change",game.playerLG.get(newUUID).getName()));
        }
    }

    @EventHandler
    public void onTargetDeath(TargetDeathEvent event) {

        UUID uuid=event.getUuid();

        PlayerLG plg = game.playerLG.get(getPlayerUUID());

        if(!getAffectedPlayers().contains(uuid)) return;


        if (!plg.isState(State.ALIVE)) return;

        clearAffectedPlayer();
        setPower(true);
        setProgress(0f);

        if(Bukkit.getPlayer(getPlayerUUID())==null) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());
        player.sendMessage(game.translate("werewolf.role.succubus.charming_message"));
    }
}
