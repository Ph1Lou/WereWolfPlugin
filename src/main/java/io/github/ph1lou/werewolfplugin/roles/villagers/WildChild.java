package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.enumlg.TimersBase;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import io.github.ph1lou.werewolfapi.rolesattributs.Transformed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WildChild extends RolesVillage implements AffectedPlayers, Transformed, Power {

    boolean transformed=false;
    private final List<UUID> affectedPlayer = new ArrayList<>();

    public WildChild(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
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


        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!transformed) {
            return;
        }

        if (player == null) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, -1, false, false));
    }

    @EventHandler
    public void onDay(DayEvent event) {


        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!transformed) {
            return;
        }

        if (player == null) return;

        player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }

    @EventHandler
    public void onAutoModel(AutoModelEvent event) {



        UUID modelUUID = game.autoSelect(getPlayerUUID());
        PlayerWW model = game.getPlayersWW().get(modelUUID);
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!hasPower()) return;

        addAffectedPlayer(modelUUID);
        setPower(false);
        Bukkit.getPluginManager().callEvent(new ModelEvent(getPlayerUUID(), modelUUID));

        if (player == null) return;

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }

        player.sendMessage(game.translate("werewolf.role.wild_child.reveal_model", model.getName()));
        Sounds.BAT_IDLE.play(player);
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.wild_child.description");
    }

    @Override
    public void recoverPowerAfterStolen() {


        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        if (!transformed) {
            player.sendMessage(game.translate(
                    "werewolf.role.wild_child.design_model",
                    game.getScore().conversion(
                            game.getConfig()
                                    .getTimerValues()
                                    .get(TimersBase.MODEL_DURATION
                                            .getKey()))));
        } else {
            UUID modelUUID = getAffectedPlayers().get(0);
            PlayerWW model = game.getPlayersWW().get(modelUUID);

            if (modelUUID.equals(getPlayerUUID())) {

                WildChildTransformationEvent wildChildTransformationEvent =
                        new WildChildTransformationEvent(
                                getPlayerUUID(),
                                getPlayerUUID());

                Bukkit.getPluginManager().callEvent(wildChildTransformationEvent);

                if (wildChildTransformationEvent.isCancelled()) {
                    player.sendMessage(game.translate(
                            "werewolf.check.transformation"));
                    return;
                }

                setTransformed(true);

                if (!super.isWereWolf()) {
                    Bukkit.getPluginManager().callEvent(
                            new NewWereWolfEvent(getPlayerUUID()));
                }

            } else
                player.sendMessage(game.translate(
                        "werewolf.role.wild_child.reveal_model",
                        model.getName()));
        }
    }

    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();
        if (!transformed) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.NIGHT_VISION,
                Integer.MAX_VALUE,
                0,
                false,
                false));
    }

    @Override
    public void recoverPower() {

        super.recoverPower();

        Player player = Bukkit.getPlayer(getPlayerUUID());
        if (player == null) return;
        player.sendMessage(game.translate(
                "werewolf.role.wild_child.design_model",
                game.getScore().conversion(
                        game.getConfig()
                                .getTimerValues()
                                .get(TimersBase.MODEL_DURATION
                                        .getKey()))));
    }

    @Override
    public boolean isWereWolf() {
        return this.transformed || super.isWereWolf();
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        if (!transformed) return;

        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();

        if (!killer.getUniqueId().equals(getPlayerUUID())) return;

        killer.removePotionEffect(PotionEffectType.ABSORPTION);
        killer.addPotionEffect(
                new PotionEffect(PotionEffectType.SPEED,
                        1200,
                        0,
                        false,
                        false));
        killer.addPotionEffect(new PotionEffect(
                PotionEffectType.ABSORPTION,
                1200,
                0,
                false,
                false));
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {


        UUID uuid = event.getUuid();
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!getAffectedPlayers().contains(uuid)) return;

        if (game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.DEATH)) return;

        if (transformed) return;

        WildChildTransformationEvent wildChildTransformationEvent =
                new WildChildTransformationEvent(getPlayerUUID(), uuid);

        Bukkit.getPluginManager().callEvent(wildChildTransformationEvent);

        if (wildChildTransformationEvent.isCancelled()) {
            if (player != null) {
                player.sendMessage(game.translate(
                        "werewolf.check.transformation"));
            }
            return;
        }

        setTransformed(true);

        if (!super.isWereWolf()) { //au cas ou il est infecté
            Bukkit.getPluginManager().callEvent(
                    new NewWereWolfEvent(getPlayerUUID()));
        }
    }

    @EventHandler
    public void onTargetIsStolen(StealEvent event) {


        UUID newUUID = event.getKiller();
        UUID oldUUID = event.getPlayer();
        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!getAffectedPlayers().contains(oldUUID)) return;

        removeAffectedPlayer(oldUUID);
        addAffectedPlayer(newUUID);

        if (!plg.isState(StatePlayer.ALIVE)) return;

        if (player != null) {
            player.sendMessage(game.translate(
                    "werewolf.role.wild_child.change",
                    game.getPlayersWW().get(newUUID).getName()));
        }
    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event){

        if(!event.getPlayerUUID().equals(getPlayerUUID())) return;

        StringBuilder sb = event.getEndMessage();

        if(!getAffectedPlayers().isEmpty()) {
            sb.append(game.translate("werewolf.end.model",
                    game.getPlayersWW()
                            .get(getAffectedPlayers()
                                    .get(0))
                            .getName()));
        }
        if(transformed){
            sb.append(game.translate("werewolf.end.transform"));
        }

    }
}
