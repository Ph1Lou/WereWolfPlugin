package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.Camp;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.ElderResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.events.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Elder extends RolesVillage implements Power {

    private boolean power = true;

    public Elder(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main, game, uuid, key);
    }

    @Override
    public void setPower(Boolean power) {
        this.power = power;
    }

    @Override
    public Boolean hasPower() {
        return(this.power);
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.elder.description");
    }


    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        if (!hasPower()) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
                Integer.MAX_VALUE,
                0,
                false,
                false));

    }

    @EventHandler
    public void onDay(DayEvent event) {
        restoreResistance();
    }

    @EventHandler
    public void onNight(NightEvent event){
        restoreResistance();
    }


    public void restoreResistance() {

        if (!hasPower()) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) return;

        if (player == null) return;

        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.DAMAGE_RESISTANCE,
                Integer.MAX_VALUE,
                0,
                false,
                false));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) return;

        if (!event.getUuid().equals(getPlayerUUID())) return;

        if (!hasPower()) return;

        UUID killerUUID = game.getPlayersWW().get(getPlayerUUID()).getLastKiller();

        Player player = Bukkit.getPlayer(getPlayerUUID());

        ElderResurrectionEvent elderResurrectionEvent =
                new ElderResurrectionEvent(getPlayerUUID(),
                        game.getPlayersWW().containsKey(killerUUID)
                                && game.getPlayersWW().get(killerUUID)
                                .getRole().isCamp(Camp.VILLAGER));

        Bukkit.getPluginManager().callEvent(elderResurrectionEvent);
        setPower(false);

        if (elderResurrectionEvent.isCancelled()) {
            if (player == null) return;
            player.sendMessage(game.translate("werewolf.check.cancel"));
        } else {
            if (elderResurrectionEvent.isKillerAVillager()) {
                VersionUtils.getVersionUtils().setPlayerMaxHealth(player,
                        Math.max(1,
                                VersionUtils.getVersionUtils()
                                        .getPlayerMaxHealth(player) - 6));
            }
            event.setCancelled(true);
            game.resurrection(getPlayerUUID());
        }

    }
}
