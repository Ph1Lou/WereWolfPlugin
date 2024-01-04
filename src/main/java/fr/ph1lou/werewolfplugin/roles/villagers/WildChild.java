package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.utils.EndPlayerMessageEvent;
import fr.ph1lou.werewolfapi.events.roles.wild_child.AutoModelEvent;
import fr.ph1lou.werewolfapi.events.roles.wild_child.ModelEvent;
import fr.ph1lou.werewolfapi.events.roles.wild_child.WildChildTransformationEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.ITransformed;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


@Role(key = RoleBase.WILD_CHILD,
        defaultAura = Aura.NEUTRAL,
        category = Category.VILLAGER,
        timers = {@Timer(key = TimerBase.MODEL_DURATION,
                defaultValue = 240, meetUpValue = 240,
                decrementAfterRole = true,
                onZero = AutoModelEvent.class)},
        attributes = RoleAttribute.HYBRID)
public class WildChild extends RoleImpl implements IAffectedPlayers, ITransformed, IPower {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    boolean transformed = false;
    private boolean power = true;

    public WildChild(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }


    @Override
    public boolean hasPower() {
        return (this.power);
    }

    @Override
    public boolean isTransformed() {
        return transformed;
    }

    @Override
    public void setTransformed(boolean transformed) {
        this.transformed = transformed;
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


        IPlayerWW model = Utils.autoSelect(game, getPlayerWW());

        if (!hasPower()) return;

        addAffectedPlayer(model);
        setPower(false);
        Bukkit.getPluginManager().callEvent(new ModelEvent(this.getPlayerWW(), model));

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.wild_child.reveal_model",
                Formatter.player(model.getName()));
        this.getPlayerWW().sendSound(Sound.BAT_IDLE);
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.wild_child.description"))
                .addExtraLines(game.translate("werewolf.roles.wild_child.model",
                        Formatter.player(
                                affectedPlayer.isEmpty() ?
                                        !transformed ?
                                                game.translate("werewolf.roles.wild_child.design_model",
                                                        Formatter.timer(game, TimerBase.MODEL_DURATION))
                                                :
                                                game.translate("werewolf.roles.wild_child.model_none")
                                        :
                                        transformed ?
                                                game.translate("werewolf.roles.wild_child.model_death")
                                                :
                                                affectedPlayer.get(0).getName())))
                .build();


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

        if (this.getPlayerWW().isState(StatePlayer.DEATH)) return;

        if (transformed) return;

        WildChildTransformationEvent wildChildTransformationEvent =
                new WildChildTransformationEvent(this.getPlayerWW(), playerWW);

        Bukkit.getPluginManager().callEvent(wildChildTransformationEvent);

        if (wildChildTransformationEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(
                    Prefix.RED, "werewolf.check.transformation");
            return;
        }

        setTransformed(true);

        if (!super.isWereWolf()) { //au cas ou il est infecté
            Bukkit.getPluginManager().callEvent(
                    new NewWereWolfEvent(getPlayerWW()));
        }
    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        StringBuilder sb = event.getEndMessage();

        if (!getAffectedPlayers().isEmpty()) {

            IPlayerWW modelWW = getAffectedPlayers().get(0);

            if (modelWW != null) {
                sb.append(game.translate("werewolf.roles.wild_child.model_end",
                        Formatter.player(modelWW.getName())));
            }
        }
        if (transformed) {
            sb.append(game.translate("werewolf.end.transform"));
        }
    }
}
