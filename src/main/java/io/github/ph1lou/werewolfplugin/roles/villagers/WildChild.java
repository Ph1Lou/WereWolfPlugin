package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WildChild extends RolesVillage implements AffectedPlayers, Transformed, Power {

    boolean transformed = false;
    private final List<PlayerWW> affectedPlayer = new ArrayList<>();

    public WildChild(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    private boolean power = true;

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }


    @Override
    public boolean hasPower() {
        return (this.power);
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
    public void addAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
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

    @EventHandler
    public void onNight(NightEvent event) {


        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
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

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
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


        PlayerWW model = game.autoSelect(getPlayerWW());
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (model == null) return;

        if (!hasPower()) return;

        addAffectedPlayer(model);
        setPower(false);
        Bukkit.getPluginManager().callEvent(new ModelEvent(getPlayerWW(), model));

        if (player == null) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        player.sendMessage(game.translate("werewolf.role.wild_child.reveal_model", model.getName()));
        Sounds.BAT_IDLE.play(player);
    }

    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.wild_child.description");
    }


    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!event.getThiefWW().equals(getPlayerWW())) return;

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
            PlayerWW model = getAffectedPlayers().get(0);

            if (model.equals(getPlayerWW())) {

                WildChildTransformationEvent wildChildTransformationEvent =
                        new WildChildTransformationEvent(
                                getPlayerWW(),
                                getPlayerWW());

                Bukkit.getPluginManager().callEvent(wildChildTransformationEvent);

                if (wildChildTransformationEvent.isCancelled()) {
                    player.sendMessage(game.translate(
                            "werewolf.check.transformation"));
                    return;
                }

                setTransformed(true);

                if (!super.isWereWolf()) {
                    Bukkit.getPluginManager().callEvent(
                            new NewWereWolfEvent(getPlayerWW()));
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


        PlayerWW playerWW = event.getPlayerWW();
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!getAffectedPlayers().contains(playerWW)) return;

        if (getPlayerWW().isState(StatePlayer.DEATH)) return;

        if (transformed) return;

        WildChildTransformationEvent wildChildTransformationEvent =
                new WildChildTransformationEvent(getPlayerWW(), playerWW);

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
                    new NewWereWolfEvent(getPlayerWW()));
        }
    }

    @EventHandler
    public void onTargetIsStolen(StealEvent event) {


        PlayerWW playerWW = event.getPlayerWW();
        PlayerWW thiefWW = event.getThiefWW();
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!getAffectedPlayers().contains(playerWW)) return;

        removeAffectedPlayer(playerWW);
        addAffectedPlayer(thiefWW);

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (player == null) return;

        player.sendMessage(game.translate(
                "werewolf.role.wild_child.change",
                thiefWW.getName()));
    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event){

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        StringBuilder sb = event.getEndMessage();

        if(!getAffectedPlayers().isEmpty()) {

            PlayerWW modelWW = getAffectedPlayers().get(0);

            if (modelWW != null) {
                sb.append(game.translate("werewolf.end.model",
                        modelWW.getName()));
            }

        }
        if(transformed){
            sb.append(game.translate("werewolf.end.transform"));
        }

    }
}
