package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.Camp;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.ElderResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.events.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import io.github.ph1lou.werewolfplugin.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Elder extends RolesVillage implements Power {

    public Elder(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }

    private boolean power=true;
    @Override
    public void setPower(Boolean power) {
        this.power=power;
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
    public String getDisplay() {
        return "werewolf.role.elder.display";
    }

    @Override
    public void recoverPotionEffect(Player player) {
        if(!hasPower()) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,0,false,false));
        super.recoverPotionEffect(player);
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

        if (getPlayerUUID() == null) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)) return;

        if (player == null) return;

        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSecondDeathEvent(SecondDeathEvent event){

        if(event.isCancelled()) return;

        if(!event.getUuid().equals(getPlayerUUID())) return;

        if (!hasPower()) return;

        ElderResurrectionEvent elderResurrectionEvent = new ElderResurrectionEvent(getPlayerUUID());
        Bukkit.getPluginManager().callEvent(elderResurrectionEvent);
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if(elderResurrectionEvent.isCancelled()) {
            if (player != null) {
                player.sendMessage(game.translate("werewolf.check.cancel"));
            }
            return;
        }

        setPower(false);

        UUID killerUUID = game.getPlayersWW().get(getPlayerUUID()).getLastKiller();

        if (player != null) {

            if (game.getPlayersWW().containsKey(killerUUID) && game.getPlayersWW().get(killerUUID).getRole().isCamp(Camp.VILLAGER)) {
                VersionUtils.getVersionUtils().setPlayerMaxHealth(player, Math.max(1, VersionUtils.getVersionUtils().getPlayerMaxHealth(player) - 6));
            }
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        }
        event.setCancelled(true);
        game.resurrection(getPlayerUUID());
    }
}
