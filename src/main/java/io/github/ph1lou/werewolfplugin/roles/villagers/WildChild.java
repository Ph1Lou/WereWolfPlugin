package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import io.github.ph1lou.werewolfapi.events.roles.StealEvent;
import io.github.ph1lou.werewolfapi.events.roles.wild_child.AutoModelEvent;
import io.github.ph1lou.werewolfapi.events.roles.wild_child.ModelEvent;
import io.github.ph1lou.werewolfapi.events.roles.wild_child.WildChildTransformationEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import io.github.ph1lou.werewolfapi.rolesattributs.Transformed;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WildChild extends RoleVillage implements IAffectedPlayers, Transformed, IPower {

    boolean transformed = false;
    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();

    public WildChild(GetWereWolfAPI main, IPlayerWW playerWW, String key) {
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
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }


    @EventHandler
    public void onAutoModel(AutoModelEvent event) {


        IPlayerWW model = game.autoSelect(getPlayerWW());

        if (!hasPower()) return;

        addAffectedPlayer(model);
        setPower(false);
        Bukkit.getPluginManager().callEvent(new ModelEvent(getPlayerWW(), model));

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        getPlayerWW().sendMessageWithKey("werewolf.role.wild_child.reveal_model", Sound.BAT_IDLE,
                model.getName());
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.wild_child.description"))
                .addExtraLines(() -> game.translate("werewolf.role.wild_child.model",
                        affectedPlayer.isEmpty() ?
                                !transformed ?
                                        game.translate("werewolf.role.wild_child.design_model",
                                                Utils.conversion(game.getConfig()
                                                        .getTimerValue(TimersBase.MODEL_DURATION.getKey())))
                                        :
                                        game.translate("werewolf.role.wild_child.model_none")
                                :
                                transformed ?
                                        game.translate("werewolf.role.wild_child.model_death")
                                        :
                                        affectedPlayer.get(0).getName()))
                .build();


    }


    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!event.getThiefWW().equals(getPlayerWW())) return;

        if (!transformed) {
            return;
        }

        if (this.affectedPlayer.isEmpty()) return;

        IPlayerWW model = getAffectedPlayers().get(0);

        if (model.equals(getPlayerWW())) {

            WildChildTransformationEvent wildChildTransformationEvent =
                    new WildChildTransformationEvent(
                            getPlayerWW(),
                            getPlayerWW());

            Bukkit.getPluginManager().callEvent(wildChildTransformationEvent);

            if (wildChildTransformationEvent.isCancelled()) {
                getPlayerWW().sendMessageWithKey("werewolf.check.transformation");
                return;
            }

            setTransformed(true);

            if (!super.isWereWolf()) {
                Bukkit.getPluginManager().callEvent(
                        new NewWereWolfEvent(getPlayerWW()));
            }

        } else
            getPlayerWW().sendMessageWithKey("werewolf.role.wild_child.reveal_model",
                    model.getName());


    }

    @Override
    public void recoverPower() {
    }

    @Override
    public boolean isWereWolf() {
        return this.transformed || super.isWereWolf();
    }


    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!getAffectedPlayers().contains(playerWW)) return;

        if (getPlayerWW().isState(StatePlayer.DEATH)) return;

        if (transformed) return;

        WildChildTransformationEvent wildChildTransformationEvent =
                new WildChildTransformationEvent(getPlayerWW(), playerWW);

        Bukkit.getPluginManager().callEvent(wildChildTransformationEvent);

        if (wildChildTransformationEvent.isCancelled()) {
            getPlayerWW().sendMessageWithKey(
                    "werewolf.check.transformation");
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

        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW thiefWW = event.getThiefWW();

        if (!getAffectedPlayers().contains(playerWW)) return;

        removeAffectedPlayer(playerWW);
        addAffectedPlayer(thiefWW);

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        getPlayerWW().sendMessageWithKey(
                "werewolf.role.wild_child.change",
                thiefWW.getName());
    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event){

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        StringBuilder sb = event.getEndMessage();

        if(!getAffectedPlayers().isEmpty()) {

            IPlayerWW modelWW = getAffectedPlayers().get(0);

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
