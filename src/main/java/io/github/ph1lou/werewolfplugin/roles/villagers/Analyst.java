package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.ILimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWithLimitedSelectionDuration;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Analyst extends RoleWithLimitedSelectionDuration implements ILimitedUse, IAffectedPlayers {

    private int use = 0;
    private boolean power2 = true;
    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();

    public Analyst(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game,this)
                .setDescription(game.translate("werewolf.role.analyst.description",
                        Formatter.timer(
                                Utils.conversion(
                                        Math.max(0,
                                                Math.max(0,game.getConfig().getTimerValue(TimerBase.ROLE_DURATION.getKey()))+
                                                        game.getConfig().getTimerValue(TimerBase.ANALYSE_DURATION.getKey()))))))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        if(this.use >= 5){
            return;
        }

        if(game.getConfig().getTimerValue(TimerBase.ANALYSE_DURATION.getKey()) > 0){
            return;
        }

        setPower(true);

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(
                Prefix.YELLOW.getKey() , "werewolf.role.analyst.message_see",
                Formatter.number(5 - this.use),
                Formatter.timer(Utils.conversion(
                        game.getConfig().getTimerValue(TimerBase.POWER_DURATION.getKey()))));
    }

    @Override
    public int getUse() {
        return this.use;
    }

    @Override
    public void setUse(int use) {
        this.use = use;
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return this.affectedPlayers;
    }

    public boolean isPower2() {
        return power2;
    }

    public void setPower2(boolean power2) {
        this.power2 = power2;
    }
}
