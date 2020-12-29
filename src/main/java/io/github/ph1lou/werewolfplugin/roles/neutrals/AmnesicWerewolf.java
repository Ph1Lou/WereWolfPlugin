package io.github.ph1lou.werewolfplugin.roles.neutrals;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesNeutral;
import io.github.ph1lou.werewolfapi.rolesattributs.Transformed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class AmnesicWerewolf extends RolesNeutral implements Transformed {


    private boolean transformed = false;

    public AmnesicWerewolf(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @EventHandler
    public void onNight(NightEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        getPlayerWW().addPotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }


    @EventHandler
    public void onDay(DayEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        getPlayerWW().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        PlayerWW playerWW = event.getPlayerWW();

        if (playerWW.getLastKiller() == null) return;

        if (!playerWW.getLastKiller().equals(getPlayerWW())) return;

        if (transformed) return;

        AmnesiacTransformationEvent amnesiacTransformationEvent =
                new AmnesiacTransformationEvent(getPlayerWW(), playerWW);

        Bukkit.getPluginManager().callEvent(amnesiacTransformationEvent);

        if (amnesiacTransformationEvent.isCancelled()) {
            getPlayerWW().sendMessage(game.translate("werewolf.check.transformation"));
            return;
        }

        setTransformed(true);

        if (!super.isWereWolf()) {
            Bukkit.getPluginManager().callEvent(
                    new NewWereWolfEvent(getPlayerWW()));
        }

    }

    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();

        getPlayerWW().addPotionEffect(PotionEffectType.NIGHT_VISION);

        if (game.isDay(Day.DAY)) return;

        getPlayerWW().addPotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }

    @Override
    public @NotNull String getDescription() {

        return super.getDescription() +
                game.translate("werewolf.description.description", game.translate("werewolf.role.amnesiac_werewolf.description")) +
                game.translate("werewolf.description.werewolf") +
                game.translate("werewolf.description._");
    }

    @Override
    public void recoverPower() {
    }

    @Override
    public boolean getTransformed() {
        return this.transformed;
    }

    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        StringBuilder sb = event.getEndMessage();
        if(transformed){
            sb.append(game.translate("werewolf.end.transform"));
        }
    }

    @Override
    public void setTransformed(boolean transformed) {
        this.transformed=transformed;
    }

    @Override
    public boolean isWereWolf() {
        return this.transformed || super.isWereWolf();
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        if (!transformed) return;

        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();

        if (!killer.getUniqueId().equals(getPlayerUUID())) return;

        killer.removePotionEffect(PotionEffectType.ABSORPTION);
        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
                1200,
                0,
                false,
                false));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,
                1200,
                0,
                false,
                false));
    }

}
