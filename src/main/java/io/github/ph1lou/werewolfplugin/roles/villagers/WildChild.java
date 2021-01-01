package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.Sounds;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import io.github.ph1lou.werewolfapi.rolesattributs.Transformed;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
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
    public void onAutoModel(AutoModelEvent event) {


        PlayerWW model = game.autoSelect(getPlayerWW());

        if (!hasPower()) return;

        addAffectedPlayer(model);
        setPower(false);
        Bukkit.getPluginManager().callEvent(new ModelEvent(getPlayerWW(), model));

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        getPlayerWW().sendMessage(game.translate("werewolf.role.wild_child.reveal_model",
                model.getName()));
        Sounds.BAT_IDLE.play(getPlayerWW());
    }

    @Override
    public @NotNull String getDescription() {
        return super.getDescription() +
                game.translate("werewolf.description.description", game.translate("werewolf.role.wild_child.description")) +
                (affectedPlayer.isEmpty() ? game.translate("werewolf.role.wild_child.model") : game.translate("werewolf.description.power", game.translate(transformed ? "werewolf.role.wild_child.model_death" : "werewolf.role.wild_child.model_alive", affectedPlayer.get(0).getName())));
    }


    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!event.getThiefWW().equals(getPlayerWW())) return;

        if (!transformed) {
            getPlayerWW().sendMessage(game.translate(
                    "werewolf.role.wild_child.design_model",
                    game.getScore().conversion(
                            game.getConfig()
                                    .getTimerValue(TimersBase.MODEL_DURATION
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
                    getPlayerWW().sendMessage(game.translate(
                            "werewolf.check.transformation"));
                    return;
                }

                setTransformed(true);

                if (!super.isWereWolf()) {
                    Bukkit.getPluginManager().callEvent(
                            new NewWereWolfEvent(getPlayerWW()));
                }

            } else
                getPlayerWW().sendMessage(game.translate(
                        "werewolf.role.wild_child.reveal_model",
                        model.getName()));
        }
    }


    @Override
    public void recoverPower() {

        getPlayerWW().sendMessage(game.translate(
                "werewolf.role.wild_child.design_model",
                game.getScore().conversion(
                        game.getConfig()
                                .getTimerValue(TimersBase.MODEL_DURATION
                                        .getKey()))));
    }

    @Override
    public boolean isWereWolf() {
        return this.transformed || super.isWereWolf();
    }


    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        PlayerWW playerWW = event.getPlayerWW();

        if (!getAffectedPlayers().contains(playerWW)) return;

        if (getPlayerWW().isState(StatePlayer.DEATH)) return;

        if (transformed) return;

        WildChildTransformationEvent wildChildTransformationEvent =
                new WildChildTransformationEvent(getPlayerWW(), playerWW);

        Bukkit.getPluginManager().callEvent(wildChildTransformationEvent);

        if (wildChildTransformationEvent.isCancelled()) {
            getPlayerWW().sendMessage(game.translate(
                    "werewolf.check.transformation"));
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

        if (!getAffectedPlayers().contains(playerWW)) return;

        removeAffectedPlayer(playerWW);
        addAffectedPlayer(thiefWW);

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        getPlayerWW().sendMessage(game.translate(
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
