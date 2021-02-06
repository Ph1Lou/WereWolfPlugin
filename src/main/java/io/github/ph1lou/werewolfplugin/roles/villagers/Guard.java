package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.GuardResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.ThirdDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Guard extends RolesWithLimitedSelectionDuration implements AffectedPlayers {

    private final List<PlayerWW> affectedPlayer = new ArrayList<>();
    private PlayerWW last;
    private boolean power = true;


    public Guard(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
        setPower(false);
    }

    @Override
    public void addAffectedPlayer(PlayerWW playerWW) {
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

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

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
                game.getScore().conversion(
                        game.getConfig().getTimerValue(TimersBase.POWER_DURATION.getKey())));
    }


    @Override
    public @NotNull String getDescription() {
        return super.getDescription() +
                game.translate("werewolf.description.description", game.translate("werewolf.role.guard.description")) +
                game.translate("werewolf.description.item", game.translate("werewolf.role.guard.items"));
    }


    @Override
    public void recoverPower() {

    }
}