package io.github.ph1lou.werewolfplugin.roles.werewolfs;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWereWolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class NaughtyLittleWolf extends RolesWereWolf {

    public NaughtyLittleWolf(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.naughty_little_wolf.description");
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onNight(NightEvent event) {
        recoverSpeed();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDay(DayEvent event) {
        recoverSpeed();
    }

    void recoverSpeed() {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        getPlayerWW().addPotionEffect(PotionEffectType.SPEED);
    }


    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();

        getPlayerWW().addPotionEffect(PotionEffectType.SPEED);
    }
}
