package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.ThirdDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.guard.GuardResurrectionEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWithLimitedSelectionDuration;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Guard extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private IPlayerWW last;
    private boolean power = true;


    public Guard(GetWereWolfAPI main, IPlayerWW playerWW, String key) {
        super(main, playerWW, key);
        setPower(false);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
        this.last = playerWW;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onThirdDeathEvent(ThirdDeathEvent event) {

        if (event.isCancelled()) return;

        if (!this.power) {
            return;
        }

        if (!event.getPlayerWW().equals(this.last)) {
            return;
        }

        if (!this.last.getLastKiller().isPresent()) return;

        if (!this.last.getLastKiller().get().getRole().isWereWolf()) return;

        if (!this.last.equals(getPlayerWW()) && !getPlayerWW().isState(StatePlayer.ALIVE)) return;

        game.resurrection(this.last);

        getPlayerWW().sendMessageWithKey("werewolf.role.guard.resurrection");

        this.last.sendMessageWithKey("werewolf.role.guard.protect");

        GuardResurrectionEvent guardResurrectionEvent = new GuardResurrectionEvent(getPlayerWW(), this.last);

        if (guardResurrectionEvent.isCancelled()) {
            getPlayerWW().sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        Bukkit.getPluginManager().callEvent(guardResurrectionEvent);

        this.power = false;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDay(DayEvent event) {

        this.last = null;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.power) return;

        setPower(true);

        getPlayerWW().sendMessageWithKey(
                "werewolf.role.guard.message",
                Utils.conversion(
                        game.getConfig().getTimerValue(TimersBase.POWER_DURATION.getKey())));
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.guard.description"))
                .setItems(() -> game.translate("werewolf.role.guard.items"))
                .build();
    }

    @Override
    public void recoverPower() {

    }
}