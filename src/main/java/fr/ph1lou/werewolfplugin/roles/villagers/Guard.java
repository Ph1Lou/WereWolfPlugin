package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.TimerBase;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ThirdDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.guard.GuardResurrectionEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.impl.RoleWithLimitedSelectionDuration;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Guard extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private IPlayerWW last;



    private boolean powerFinal = true;


    public Guard(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
        this.last = playerWW;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onThirdDeathEvent(ThirdDeathEvent event) {

        if (event.isCancelled()) return;

        if (!this.powerFinal) {
            return;
        }

        if (!event.getPlayerWW().equals(this.last)) {
            return;
        }

        if (!this.last.getLastKiller().isPresent()) return;

        if (!this.last.getLastKiller().get().getRole().isWereWolf()) return;

        if (!this.last.equals(this.getPlayerWW()) && !this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        GuardResurrectionEvent guardResurrectionEvent = new GuardResurrectionEvent(this.getPlayerWW(), this.last);

        Bukkit.getPluginManager().callEvent(guardResurrectionEvent);

        if (guardResurrectionEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        this.game.resurrection(this.last);

        this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.guard.resurrection");

        this.last.sendMessageWithKey(Prefix.GREEN.getKey() , "werewolf.role.guard.protect");

        event.setCancelled(true);

        this.powerFinal = false;
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

        if (this.last != null) {
            this.last.getRole().removeAuraModifier("guarded");
            this.last = null;
        }


        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.powerFinal) return;

        this.setPower(true);

        this.getPlayerWW().sendMessageWithKey(
                Prefix.YELLOW.getKey() , "werewolf.role.guard.message",
                Formatter.timer(Utils.conversion(
                        this.game.getConfig().getTimerValue(TimerBase.POWER_DURATION.getKey()))));
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(this.game, this)
                .setDescription(this.game.translate("werewolf.role.guard.description"))
                .setItems(this.game.translate("werewolf.role.guard.items"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    public boolean isPowerFinal() {
        return powerFinal;
    }
}