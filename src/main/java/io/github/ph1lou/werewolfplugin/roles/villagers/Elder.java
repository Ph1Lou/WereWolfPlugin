package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.elder.ElderResurrectionEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Elder extends RoleVillage implements IPower {

    private boolean power = true;

    public Elder(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
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
        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.elder.description"))
                .setPower(() -> game.translate(power ? "werewolf.role.elder.available" : "werewolf.role.elder.not_available"))
                .setEffects(() -> game.translate("werewolf.role.elder.effect"))
                .build();
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

        Optional<IPlayerWW> killerWW = getPlayerWW().getLastKiller();

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
