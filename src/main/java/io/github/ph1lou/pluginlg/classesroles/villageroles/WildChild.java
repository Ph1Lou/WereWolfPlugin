package io.github.ph1lou.pluginlg.classesroles.villageroles;


import io.github.ph1lou.pluginlg.classesroles.AffectedPlayers;
import io.github.ph1lou.pluginlg.classesroles.Power;
import io.github.ph1lou.pluginlg.classesroles.Transformed;
import io.github.ph1lou.pluginlg.events.*;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WildChild extends RolesVillage implements AffectedPlayers, Transformed, Power {

    boolean transformed=false;
    private final List<UUID> affectedPlayer = new ArrayList<>();

    public WildChild(GameManager game, UUID uuid) {
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
    public boolean getTransformed(){
        return transformed;
    }

    @Override
    public void setTransformed(boolean transformed){
        this.transformed=transformed;
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
    public void onNight(NightEvent event) {

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        if(!game.playerLG.get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }

        if(!transformed){
            return;
        }

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1, false, false));
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        if(!game.playerLG.get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }

        if(!transformed){
            return;
        }

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }

    @EventHandler
    public void onAutoModel(AutoModelEvent event){

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        if(!hasPower()){
            return;
        }

        UUID modelUUID = game.roleManage.autoSelect(getPlayerUUID());
        PlayerLG model = game.playerLG.get(modelUUID);
        addAffectedPlayer(modelUUID);
        setPower(false);

        if (Bukkit.getPlayer(getPlayerUUID()) == null) {
            return;
        }

        if (!game.playerLG.get(getPlayerUUID()).isState(State.ALIVE)) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());
        player.sendMessage(game.translate("werewolf.role.wild_child.reveal_model", model.getName()));
        player.playSound(player.getLocation(), Sound.BAT_IDLE, 1, 20);
    }

    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.WILD_CHILD;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.wild_child.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.wild_child.display");
    }

    @Override
    public void stolen(UUID uuid) {

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if(!transformed) {
            player.sendMessage(game.translate("werewolf.role.wild_child.design_model", game.score.conversion(game.config.getTimerValues().get(TimerLG.MODEL_DURATION))));
        }
        else {
            UUID modelUUID = getAffectedPlayers().get(0);
            PlayerLG model = game.playerLG.get(modelUUID);

            if (modelUUID.equals(getPlayerUUID()) && !model.getInfected()) {
                Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(uuid));
            } else
                player.sendMessage(game.translate("werewolf.role.wild_child.reveal_model", model.getName()));
        }
    }
    @Override
    public void recoverPotionEffect(Player player) {
        super.recoverPotionEffect(player);
        if(!transformed) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,0,false,false));
    }

    @Override
    public void recoverPower(Player player) {
        player.sendMessage(game.translate("werewolf.role.wild_child.design_model", game.score.conversion(game.config.getTimerValues().get(TimerLG.MODEL_DURATION))));
    }

    @Override
    public boolean isWereWolf() {
        return this.transformed;
    }

    @EventHandler
    public void onTargetDeath(TargetDeathEvent event) {

        UUID uuid = event.getUuid();

        PlayerLG plg = game.playerLG.get(getPlayerUUID());

        if(!getAffectedPlayers().contains(uuid)) return;

        setTransformed(true);

        if (!plg.getRole().isCamp(Camp.WEREWOLF)) {
            Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(uuid));
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

        if(!plg.isState(State.ALIVE)) return;

        if(Bukkit.getPlayer(newUUID)!=null){
            Player player = Bukkit.getPlayer(newUUID);
            player.sendMessage(game.translate("werewolf.role.succubus.get_charmed", plg.getName()));
        }
        if(Bukkit.getPlayer(getPlayerUUID())!=null) {
            Player player = Bukkit.getPlayer(getPlayerUUID());
            player.sendMessage(game.translate("werewolf.role.succubus.change",game.playerLG.get(newUUID).getName()));
        }
    }
}
