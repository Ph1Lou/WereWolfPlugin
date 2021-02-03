package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.ElderResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.events.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Elder extends RolesVillage implements Power {

    private boolean power = true;

    public Elder(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
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
    public @NotNull String getDescription() {
        return super.getDescription() +
                game.translate("werewolf.description.description",
                        game.translate("werewolf.role.elder.description")) +
                game.translate("werewolf.description.power",
                        game.translate(power ? "werewolf.role.elder.available" : "werewolf.role.elder.not_available")) +
                game.translate("werewolf.description.effect",
                        game.translate("werewolf.role.elder.effect"));
    }


    @Override
    public void recoverPower() {

    }


    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();

        if (!hasPower()) return;

        getPlayerWW().addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDay(DayEvent event) {
        restoreResistance();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onNight(NightEvent event){
        restoreResistance();
    }


    public void restoreResistance() {

        if (!hasPower()) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        getPlayerWW().addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) return;

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        if (!hasPower()) return;

        Optional<PlayerWW> killerWW = getPlayerWW().getLastKiller();

        ElderResurrectionEvent elderResurrectionEvent =
                new ElderResurrectionEvent(getPlayerWW(),
                        killerWW.isPresent()
                                && killerWW.get()
                                .getRole().isCamp(Camp.VILLAGER));

        Bukkit.getPluginManager().callEvent(elderResurrectionEvent);
        setPower(false);

        if (elderResurrectionEvent.isCancelled()) {
            getPlayerWW().sendMessageWithKey("werewolf.check.cancel");
        } else {
            if (elderResurrectionEvent.isKillerAVillager()) {
                getPlayerWW().removePlayerMaxHealth(6);

            }
            event.setCancelled(true);
            game.resurrection(getPlayerWW());
        }

    }
}
